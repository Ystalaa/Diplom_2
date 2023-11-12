import java.util.List;
import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Получить заказ и ингредиенты")
public class OrderGetTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
        fillListIngredients();
    }

    @Test
    @DisplayName("Получите все ингредиенты")
    public void getAllIngredients() {
        response = orderClient.getAllIngredients();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Ингредиенты указаны неправильно", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить все заказы")
    public void getAllOrders() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getAllOrders();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Заказы получаются неправильные", isGet, equalTo(true));

    }

    @Test
    @DisplayName("Получить заказ с авторизацией пользователя")
    public void getOrderByAuthorizationUser() {
        response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        response = orderClient.getOrdersByAuthorization(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Заказ получен неправильно", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить заказ без авторизации пользователя")
    public void getOrderWithoutAuthorizationUser() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getOrdersWithoutAuthorization();
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isGet = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Заказ выполнен правильно", isGet, equalTo(false));
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
        ingredients.add(list.get(0));
    }
}
