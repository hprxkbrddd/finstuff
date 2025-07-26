package com.finstuff.repository.argsProvider.accountArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AddValidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                // Валидные данные
                Arguments.of("Основной счет", "user123"),
                Arguments.of("Накопительный", "user456"),
                Arguments.of("Кредитная карта *1234", "user789"),
                Arguments.of("A", "u1"),
                Arguments.of("Очень длинное название счета с пробелами и спецсимволами @#$%", "veryLongUserIdWithNumbers1234567890")
        );
    }
}
