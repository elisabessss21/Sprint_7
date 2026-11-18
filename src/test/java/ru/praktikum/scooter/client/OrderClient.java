package ru.praktikum.scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikum.scooter.model.Order;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static ru.praktikum.scooter.config.ApiConfig.BASE_URL;
import static ru.praktikum.scooter.config.ApiConfig.ORDERS;
import static ru.praktikum.scooter.config.ApiConfig.ORDER_CANCEL;

public class OrderClient {

    @Step("Отправить POST /api/v1/orders")
    public Response createOrder(Order order) {
        return given()
                .baseUri(BASE_URL)
                .contentType(JSON)
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Получить список заказов")
    public Response getOrders(int limit) {
        return given()
                .baseUri(BASE_URL)
                .queryParam("limit", limit)
                .when()
                .get(ORDERS);
    }

    @Step("Отменить заказ с трек-номером {track}")
    public Response cancelOrder(int track) {
        return given()
                .baseUri(BASE_URL)
                .queryParam("track", track)
                .when()
                .put(ORDER_CANCEL);
    }
}
