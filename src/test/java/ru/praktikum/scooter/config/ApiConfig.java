package ru.praktikum.scooter.config;

public final class ApiConfig {
    public static final String BASE_URL = System.getProperty(
            "base.url", "https://qa-scooter.education-services.ru");

    public static final String COURIER = "/api/v1/courier";
    public static final String COURIER_LOGIN = COURIER + "/login";
    public static final String ORDERS = "/api/v1/orders";
    public static final String ORDER_CANCEL = ORDERS + "/cancel";

    private ApiConfig() {
    }
}
