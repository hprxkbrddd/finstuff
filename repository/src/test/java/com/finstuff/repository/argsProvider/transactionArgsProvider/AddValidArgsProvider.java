package com.finstuff.repository.argsProvider.transactionArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AddValidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                // Стандартная транзакция (положительная)
                Arguments.of("Grocery shopping", 5000L, "acc-12345"),

                // Отрицательная сумма (расход)
                Arguments.of("Rent payment", -100000L, "acc-67890"),

                // Минимальная сумма
                Arguments.of("Small purchase", 1L, "acc-min"),

                // Максимальная сумма (Long.MAX_VALUE)
                Arguments.of("Large transfer", Long.MAX_VALUE, "acc-large"),

                // Специальные символы в названии
                Arguments.of("Café & Restaurant ★", 2500L, "acc-special"),

                // Длинное название
                Arguments.of("Monthly subscription for premium service " +
                        "with additional features", 1999L, "acc-long"),

                // Короткий accountId
                Arguments.of("Fuel", 3000L, "a"),

                // Сложный accountId
                Arguments.of("Bonus", 15000L, "acc-2023-09-15-xyz")
        );
    }
}
