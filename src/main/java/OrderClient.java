import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient extends Specification {
    @Step("Send GET request to /api/ingredients")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(EndPoints.INGREDIENTS_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders")
    public ValidatableResponse getOrdersByAuthorization(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders/all")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(EndPoints.ORDER_PATH + "all")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/orders")
    public ValidatableResponse createOrderByAuthorization(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .log().all()
                .post(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/orders")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .log().all()
                .post(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }
}