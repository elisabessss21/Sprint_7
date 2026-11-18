package ru.praktikum.scooter;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.praktikum.scooter.data.TestData;
import ru.praktikum.scooter.model.Courier;

import java.util.Map;

import static ru.praktikum.scooter.data.TestData.COURIER_LOGIN_ERROR;
import static ru.praktikum.scooter.data.TestData.COURIER_NOT_FOUND_ERROR;

public class CourierLoginTest extends BaseTest {

    @Test
    @DisplayName("Курьер может войти в систему: 200, получен id")
    void courierCanLogIn() {
        Courier courier = createCourierForTest();
        Response response = courierClient.loginCourier(TestData.courierCredentials(courier));

        assertCourierLoggedIn(response);
    }

    @ParameterizedTest(name = "Не передано обязательное поле: {0}")
    @ValueSource(strings = {"login", "password"})
    void cannotLogInWithoutRequiredField(String missingField) {
        Courier courier = createCourierForTest();
        Map<String, String> body = TestData.courierCredentials(courier);
        body.remove(missingField);

        Response response = courierClient.loginCourier(body);

        assertError(response, 400, COURIER_LOGIN_ERROR);
    }

    @Test
    @DisplayName("Неверный пароль: 404, учётная запись не найдена")
    void cannotLogInWithWrongPassword() {
        Courier courier = createCourierForTest();
        Map<String, String> body = TestData.courierCredentials(courier);
        body.put("password", TestData.newCourier().getPassword());

        Response response = courierClient.loginCourier(body);

        assertError(response, 404, COURIER_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("Неверный логин: 404, учётная запись не найдена")
    void cannotLogInWithWrongLogin() {
        Courier courier = createCourierForTest();
        Map<String, String> body = TestData.courierCredentials(courier);
        body.put("login", TestData.newCourier().getLogin());

        Response response = courierClient.loginCourier(body);

        assertError(response, 404, COURIER_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("Несуществующий курьер не может войти")
    void nonexistentCourierCannotLogIn() {
        Courier unknown = TestData.newCourier();

        Response response = courierClient.loginCourier(TestData.courierCredentials(unknown));

        assertError(response, 404, COURIER_NOT_FOUND_ERROR);
    }
}
