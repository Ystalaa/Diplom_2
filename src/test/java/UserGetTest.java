import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Получить пользователя")
public class UserGetTest {
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
    @DisplayName("Получить пользователя по валидным данным")
    public void userGetByValidCredentials() {
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.getUser(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        String email = response.extract().path("user.email");
        String name = response.extract().path("user.name");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Пользователь вводит неверные данные", isGet, equalTo(true));
        assertThat("Электронная почта не равна", email, equalTo(user.getEmail()));
        assertThat("Неверное имя", name, equalTo(user.getName()));
    }
}
