package ru.praktikum.scooter;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderListTest extends BaseTest {

    @Test
    @DisplayName("GET /api/v1/orders возвращает список заказов")
    void responseContainsOrdersList() {
        Response response = orderClient.getOrders(1);

        assertOrdersListReturned(response);
    }
}
