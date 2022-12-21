import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestGETOrders {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Description("В ответ на запрос должен приходить списко заказов")
    public void checkOrdersList() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .get("/api/v1/orders");
        response.then().assertThat().statusCode(200).and().body("orders", notNullValue());

    }
}
