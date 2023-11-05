import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Создать пользователя")
public class UserCreateTest {
    private static final String MESSAGE_FORBIDDEN = "User already exists";
    private static final String MESSAGE_FORBIDDEN_EMPTY_FIELD = "Email, password and name are required fields";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Пользователь создан с валидными данными")
    public void userCreateByValidCredentials() {
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        String accessToken = response.extract().path("accessToken");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Пользователь создан неправильно", isCreate, equalTo(true));
    }

    @Test
    @DisplayName("Пользователь создает пустой адрес электронной почты")
    public void userCreateIsEmptyEmail() {
        user.setEmail(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан правильно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Пользователь создает пустой пароль")
    public void userCreateIsEmptyPassword() {
        user.setPassword(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан правильно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Имя пользователя пустое")
    public void userCreateIsEmptyName() {
        user.setName(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан правильно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Повторный запрос создания пользователя")
    public void repeatedRequestByCreateUser() {
        userClient.createUser(user);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_FORBIDDEN));
        assertThat("Пользователь создан правильно", isCreate, equalTo(false));
    }
}
