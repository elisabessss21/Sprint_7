package ru.praktikum.scooter.data;

import io.qameta.allure.Step;
import ru.praktikum.scooter.model.Courier;
import ru.praktikum.scooter.model.Order;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TestData {
    public static final String COURIER_CREATION_ERROR = "Недостаточно данных для создания учетной записи";
    public static final String COURIER_DUPLICATE_ERROR = "Этот логин уже используется. Попробуйте другой.";
    public static final String COURIER_LOGIN_ERROR = "Недостаточно данных для входа";
    public static final String COURIER_NOT_FOUND_ERROR = "Учетная запись не найдена";

    private TestData() {
    }

    @Step("Подготовить данные нового курьера")
    public static Courier newCourier() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        return new Courier("qa_" + suffix, "pass_" + suffix, "Тестовый курьер");
    }

    @Step("Подготовить данные заказа")
    public static Order newOrder(List<String> colors) {
        return new Order("Тест", "Заказ", "Тестовая улица, 1", 4,
                "+7 900 000 00 00", 2, LocalDate.now().plusDays(3).toString(),
                "Автоматизированное тестирование API", colors);
    }

    @Step("Подготовить логин и пароль курьера")
    public static Map<String, String> courierCredentials(Courier courier) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("login", courier.getLogin());
        credentials.put("password", courier.getPassword());
        return credentials;
    }

    @Step("Подготовить поля для создания курьера")
    public static Map<String, String> courierFields(Courier courier) {
        Map<String, String> fields = courierCredentials(courier);
        fields.put("firstName", courier.getFirstName());
        return fields;
    }
}
