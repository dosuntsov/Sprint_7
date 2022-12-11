import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestCourierCreation {
    Courier courier = new Courier("roronoa", "1234", "zoro");

    private Response create(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post("/api/v1/courier");
        return response;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Description("Создание уникального курьера")
    public void courierSchouldBeCratedByPost() {
        Response response = create(courier);
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Создание двух одинаковых курьеров")
    public void equalCouriersCannotBeCreated() {
        Response response = create(courier);
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        Response responseTwo = create(courier);
        responseTwo.then().assertThat()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));

    }

    @Test
    @Description("Попытка создать курьеров с одинаковыми логинами, но разными остальными полями")
    public void equalLoginsCannotBeCreated() {
        Response response = create(courier);
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        Courier courierTwo = new Courier(courier.getLogin(), "4321", "garp");

        Response responseTwo = create(courierTwo);
        responseTwo.then().assertThat()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));

    }
    @Test
    @Description("Проверка возможности создать курьера без поля firstname")
    public void checkNoFirstNameCreationIsValid (){
        courier.setFirstName(null);
        Response response = create(courier);
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @After
    public void deleteCreatedCourier() {
        Credentials creds = new Credentials(courier.getLogin(), courier.getPassword());
        int id = given()
                .log().all()
                .header("Content-type", "application/json")
                .body(creds)
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
