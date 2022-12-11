import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestCourierLogin {

    Courier courier = new Courier("nico", "7890", "robin");
    Credentials cred = new Credentials(courier.getLogin(), courier.getPassword());

    private void courierSchouldBeCratedByPost(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post("/api/v1/courier");
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    private Response logIn(Credentials cred) {
        return given()
                .header("Content-type", "application/json")
                .body(cred)
                .when()
                .post("/api/v1/courier/login");
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courierSchouldBeCratedByPost(courier);
    }

    @Test
    @Description("Попытка входа с валидной парой логин/пароль")
    public void checkIfValidCourierCanLogin() {
        Response response = logIn(cred);
        response.then().assertThat().statusCode(200).and().body("id", notNullValue());
    }

    @Test
    @Description("Попытка входа без логина")
    public void checkIfNoLoginIsForbidden() {
        Credentials cred = new Credentials(null, this.cred.getPassword());
        Response response = logIn(cred);
        response.then().assertThat().statusCode(400).and().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Попытка входа без пароля")
    public void checkIfNoPasswordIsForbidden() {
        Credentials cred = new Credentials(this.cred.getLogin(), null);
        Response response = logIn(cred);
        response.then().assertThat().statusCode(400).and().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Попытка входа с неправильным паролем")
    public void checkIfWrongPasswordIsForbidden() {
        Credentials cred = new Credentials(this.cred.getLogin(), "3456");
        Response response = logIn(cred);
        response.then().assertThat().statusCode(404).and().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Попытка входа с неправильным логином")
    public void checkIfWrongLoginIsForbidden() {
        Credentials cred = new Credentials("nami", this.cred.getPassword());
        Response response = logIn(cred);
        response.then().assertThat().statusCode(404).and().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Попытка входа с неправильным логином и паролем")
    public void checkIfWrongLoginAndPasswordIsForbidden() {
        Credentials cred = new Credentials("nami", "123");
        Response response = logIn(cred);
        response.then().assertThat().statusCode(404).and().body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void deleteCreatedCourier() {
        int id = given()
                .log().all()
                .header("Content-type", "application/json")
                .body(cred)
                .when()
                .post("/api/v1/courier/login")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("id");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .delete("/api/v1/courier/" + id);
        response.then().assertThat().statusCode(200).and().body("ok", equalTo(true));

    }

}
