import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Обновить пользователя")
public class UserUpdateTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void clearState() {
        userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
    }

    @Test
    @DisplayName("Обновить пользователя по авторизации")
    public void updateUserByAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = userClient.updateUserByAuthorization(GenerateUser.getRandomUser(), accessToken);
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Пользователь обновляется неправильно", isUpdate, equalTo(true));
    }

    @Test
    @DisplayName("Обновить пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.updateUserWithoutAuthorization(GenerateUser.getRandomUser());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isUpdate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь обновил правильно", isUpdate, equalTo(false));
    }
}
