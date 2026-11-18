package ru.praktikum.scooter;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.praktikum.scooter.data.TestData;
import ru.praktikum.scooter.model.Courier;

import java.util.Map;

import static ru.praktikum.scooter.data.TestData.COURIER_CREATION_ERROR;
import static ru.praktikum.scooter.data.TestData.COURIER_DUPLICATE_ERROR;

public class CourierCreateTest extends BaseTest {

    @Test
    @DisplayName("Новый курьер создаётся: 201, ok = true")
    void courierCanBeCreated() {
        Courier courier = TestData.newCourier();
        Response response = courierClient.createCourier(courier);

        assertCourierCreated(response);
        registerCourierForCleanup(courier);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    void cannotCreateTwoIdenticalCouriers() {
        Courier courier = createCourierForTest();

        Response response = courierClient.createCourier(courier);

        assertError(response, 409, COURIER_DUPLICATE_ERROR);
    }

    @Test
    @DisplayName("Нельзя зарегистрировать другого курьера с занятым логином")
    void cannotCreateCourierWithExistingLogin() {
        Courier existing = createCourierForTest();
        Courier another = TestData.newCourier();
        another.setLogin(existing.getLogin());

        Response response = courierClient.createCourier(another);

        assertError(response, 409, COURIER_DUPLICATE_ERROR);
    }

    @ParameterizedTest(name = "Не передано обязательное поле: {0}")
    @ValueSource(strings = {"login", "password"})
    void cannotCreateCourierWithoutRequiredField(String missingField) {
        Map<String, String> body = TestData.courierFields(TestData.newCourier());
        body.remove(missingField);

        Response response = courierClient.createCourier(body);

        assertError(response, 400, COURIER_CREATION_ERROR);
    }
}
