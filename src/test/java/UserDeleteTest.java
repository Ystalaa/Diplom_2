import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Удаление пользователя")
public class UserDeleteTest {
    private static final String MESSAGE_ACCEPTED = "User successfully removed";
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Удаление пользователя по валидным данным")
    public void userDeleteByValidCredentials() {
        ValidatableResponse response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isDelete = response.extract().path("success");

        assertThat("Токен имеет значение null", accessToken, notNullValue());
        assertThat("Неверный код ответа", statusCode, equalTo(SC_ACCEPTED));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_ACCEPTED));
        assertThat("Пользователь удален неправильно", isDelete, equalTo(true));
    }
}
