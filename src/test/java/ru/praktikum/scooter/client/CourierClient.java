package ru.praktikum.scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static ru.praktikum.scooter.config.ApiConfig.BASE_URL;
import static ru.praktikum.scooter.config.ApiConfig.COURIER;
import static ru.praktikum.scooter.config.ApiConfig.COURIER_LOGIN;

public class CourierClient {

    @Step("Отправить POST /api/v1/courier")
    public Response createCourier(Object body) {
        return given()
                .baseUri(BASE_URL)
                .contentType(JSON)
                .body(body)
                .when()
                .post(COURIER);
    }

    @Step("Отправить POST /api/v1/courier/login")
    public Response loginCourier(Map<String, String> credentials) {
        return given()
                .baseUri(BASE_URL)
                .contentType(JSON)
                .body(credentials)
                .when()
                .post(COURIER_LOGIN);
    }

    @Step("Удалить курьера с id {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .baseUri(BASE_URL)
                .when()
                .delete(COURIER + "/{id}", courierId);
    }
}
