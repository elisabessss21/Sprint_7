package ru.praktikum.scooter;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.praktikum.scooter.data.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class OrderCreateTest extends BaseTest {

    @ParameterizedTest(name = "Создание заказа: {0}")
    @MethodSource("colorVariants")
    void orderCanBeCreatedWithDifferentColors(String variant, List<String> colors) {
        Response response = orderClient.createOrder(TestData.newOrder(colors));

        assertOrderCreated(response);
        registerOrderForCleanup(response.path("track"));
    }

    private static Stream<Arguments> colorVariants() {
        return Stream.of(
                Arguments.of("BLACK", Collections.singletonList("BLACK")),
                Arguments.of("GREY", Collections.singletonList("GREY")),
                Arguments.of("BLACK и GREY", Arrays.asList("BLACK", "GREY")),
                Arguments.of("цвет не указан", (Object) null),
                Arguments.of("пустой массив цветов", Collections.emptyList())
        );
    }
}
