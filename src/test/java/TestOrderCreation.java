import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class TestOrderCreation {
    Order order = new Order("Naruto", "Uzumaki", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", null);
    List<String> colors;

    public TestOrderCreation(List<String> colors){
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] variableColor() {
        return new Object[][]{
                {Arrays.asList("BLACK", "GREY")},
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {null},
        };
    }
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        order.setColor(colors);
    }

    @Test
    @Description("Попытка передать разные варианты цветов")
    public void checkIfDifferentColorsAreValid(){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(order)
                        .post("/api/v1/orders");
        response.then().assertThat().statusCode(201).and().body("track", notNullValue());
    }

}
