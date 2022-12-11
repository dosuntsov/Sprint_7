import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class TestCourierInvalidCreationParametrized {

    private Courier courier;

    public TestCourierInvalidCreationParametrized(String login, String password) {
        this.courier = new Courier(login, password,  "chopper");
    }

    @Parameterized.Parameters
    public static Object[][] variableNullsInQuery() {
        return new Object[][]{
                {null, "1234"},
                {"doctor", null},
                {null, null},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Description("Создание курьеров без логина или пароля")
    public void checkIfCreatingNullPointsIsForbidden() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post("/api/v1/courier");
        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }


}
