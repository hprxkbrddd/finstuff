package com.finstuff.repository.argsProvider.accountArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AddInvalidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                // Невалидные данные (blank)
                Arguments.of("", "user123"),
                Arguments.of("   ", "user456"),
                Arguments.of("Основной счет", ""),
                Arguments.of("Накопительный", "   "),
                Arguments.of("", ""),
                Arguments.of("   ", "   ")
        );
    }
}
