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
import static org.hamcrest.Matchers.*;

@Epic("Вход и выход пользователя")
public class UserLoginLogoutTest {
    private static final String MESSAGE_LOGOUT = "Successful logout";
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect";
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
    @DisplayName("Вход пользователя по валидным данным")
    public void userLoginByValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isLogin = response.extract().path("success");

        assertThat("Токен имеет значение null", accessToken, notNullValue());
        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Пользователь вводит неверный логин", isLogin, equalTo(true));
    }

    @Test
    @DisplayName("Выход пользователя из системы с использованием валидных данных")
    public void userLogoutByValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        String refreshToken = response.extract().path("refreshToken");
        refreshToken = "{\"token\":\"" + refreshToken + "\"}";
        response = userClient.logoutUser(refreshToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogout = response.extract().path("success");

        assertThat("Токен имеет значение null", refreshToken, notNullValue());
        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_LOGOUT));
        assertThat("Пользователь вышел из системы некорректно", isLogout, equalTo(true));
    }

    @Test
    @DisplayName("Вход пользователя по пустому адресу электронной почты")
    public void userLoginByEmptyEmail() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Токен имеет значение null", accessToken, notNullValue());
        assertThat("Неверный код ответа", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь вошел в систему правильно", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("Вход пользователя по пустому паролю")
    public void userLoginByEmptyPassword() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Токен имеет значение null", accessToken, notNullValue());
        assertThat("Неверный код ответа", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь вошел в систему правильно", isLogin, equalTo(false));
    }
}