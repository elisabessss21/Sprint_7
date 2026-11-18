package ru.praktikum.scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import ru.praktikum.scooter.client.CourierClient;
import ru.praktikum.scooter.client.OrderClient;
import ru.praktikum.scooter.data.TestData;
import ru.praktikum.scooter.model.Courier;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseTest {

    private static final int CLEANUP_ATTEMPTS = 3;

    protected final CourierClient courierClient = new CourierClient();
    protected final OrderClient orderClient = new OrderClient();

    private final List<Courier> couriersForCleanup = new ArrayList<>();
    private final List<Integer> orderTracks = new ArrayList<>();

    @Step("Проверить успешное создание курьера")
    protected void assertCourierCreated(Response response) {
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Проверить успешную авторизацию курьера")
    protected void assertCourierLoggedIn(Response response) {
        response.then()
                .statusCode(200)
                .body("id", greaterThan(0));
    }

    @Step("Проверить HTTP-код {statusCode} и сообщение об ошибке")
    protected void assertError(Response response, int statusCode, String message) {
        response.then()
                .statusCode(statusCode)
                .body("message", equalTo(message));
    }

    @Step("Проверить, что создан заказ и получен track")
    protected void assertOrderCreated(Response response) {
        response.then()
                .statusCode(201)
                .body("track", greaterThan(0));
    }

    @Step("Проверить, что ответ содержит список заказов")
    protected void assertOrdersListReturned(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", instanceOf(List.class));
    }

    @Step("Создать курьера перед тестом")
    protected Courier createCourierForTest() {
        Courier courier = TestData.newCourier();

        assertCourierCreated(courierClient.createCourier(courier));
        registerCourierForCleanup(courier);

        return courier;
    }

    @Step("Сохранить данные курьера для удаления после теста")
    protected void registerCourierForCleanup(Courier courier) {
        couriersForCleanup.add(courier);
    }

    @Step("Получить id курьера для удаления")
    protected int findCourierIdForCleanup(Courier courier) {
        String error = "Не удалось получить id курьера";

        for (int attempt = 1; attempt <= CLEANUP_ATTEMPTS; attempt++) {
            try {
                Response response = courierClient.loginCourier(
                        TestData.courierCredentials(courier)
                );

                if (response.statusCode() == 200) {
                    Integer id = response.path("id");

                    if (id != null && id > 0) {
                        return id;
                    }

                    error = "В ответе авторизации отсутствует корректный id";
                    break;
                }

                error = "Ошибка авторизации курьера: HTTP "
                        + response.statusCode();

                if (response.statusCode() < 500) {
                    break;
                }

            } catch (Exception e) {
                error = "Ошибка получения id курьера: "
                        + e.getMessage();
            }
        }

        throw new IllegalStateException(error);
    }

    @Step("Сохранить трек-номер заказа для отмены после теста")
    protected void registerOrderForCleanup(int track) {
        orderTracks.add(track);
    }

    @AfterEach
    @Step("Удалить созданных курьеров и отменить созданные заказы")
    void cleanUp() {
        List<String> cleanupErrors = new ArrayList<>();

        for (Integer track : orderTracks) {
            String error = null;

            for (int attempt = 1; attempt <= CLEANUP_ATTEMPTS; attempt++) {
                try {
                    Response response = orderClient.cancelOrder(track);

                    if (response.statusCode() == 200
                            && Boolean.TRUE.equals(response.path("ok"))) {
                        error = null;
                        break;
                    }

                    error = "Не удалось отменить заказ "
                            + track + ": " + response.asString();

                    if (response.statusCode() < 500) {
                        break;
                    }

                } catch (Exception e) {
                    error = "Ошибка отмены заказа "
                            + track + ": " + e.getMessage();
                }
            }

            if (error != null) {
                cleanupErrors.add(error);
            }
        }

        for (Courier courier : couriersForCleanup) {
            int id;

            try {
                id = findCourierIdForCleanup(courier);
            } catch (Exception e) {
                cleanupErrors.add(e.getMessage());
                continue;
            }

            String error = null;

            for (int attempt = 1; attempt <= CLEANUP_ATTEMPTS; attempt++) {
                try {
                    Response response = courierClient.deleteCourier(id);

                    if (response.statusCode() == 200
                            && Boolean.TRUE.equals(response.path("ok"))) {
                        error = null;
                        break;
                    }

                    error = "Не удалось удалить курьера "
                            + id + ": " + response.asString();

                    if (response.statusCode() < 500) {
                        break;
                    }

                } catch (Exception e) {
                    error = "Ошибка удаления курьера "
                            + id + ": " + e.getMessage();
                }
            }

            if (error != null) {
                cleanupErrors.add(error);
            }
        }

        assertTrue(
                cleanupErrors.isEmpty(),
                String.join("; ", cleanupErrors)
        );
    }
}