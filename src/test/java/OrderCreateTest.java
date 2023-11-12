import java.util.List;
import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Создать заказ")
public class OrderCreateTest {
    private static final String MESSAGE_BAD_REQUEST = "Ingredient ids must be provided";
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
    }

    @Test
    @DisplayName("Создать заказ с авторизацией")
    public void orderCreateByAuthorization() {
        fillListIngredients();
        response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Заказ создан неправильно", isCreate, equalTo(true));
        assertThat("Номер заказа равен нулю", orderNumber, notNullValue());
        assertThat("Идентификатор заказа равен нулю", orderId, notNullValue());
    }

    @Test
    @DisplayName("Создать заказ без авторизации")
    public void orderCreateWithoutAuthorization() {
        fillListIngredients();
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_OK));
        assertThat("Заказ создан неправильно", isCreate, equalTo(true));
        assertThat("Номер заказа равен нулю", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("Создать заказ без авторизации и ингредиентов")
    public void orderCreateWithoutAuthorizationAndIngredients() {
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Неверный код ответа", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Неверное сообщение", message, equalTo(MESSAGE_BAD_REQUEST));
        assertThat("Заказ создан правильно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Создать заказ без авторизации и изменить хэш-ингредиент")
    public void orderCreateWithoutAuthorizationAndChangeHashIngredient() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5).replaceAll("a", "l"));
        ingredients.add(list.get(0));
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();

        assertThat("Неверный код ответа", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));

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