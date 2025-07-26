package com.finstuff.repository.argsProvider.transactionArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AddInvalidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                // Пустой title
                Arguments.of("", 100L, "valid-acc"),

                // Title из пробелов
                Arguments.of("   ", 100L, "valid-acc"),

                // Нулевой amount
                Arguments.of("Valid title", 0L, "valid-acc"),

                // Пустой accountId
                Arguments.of("Valid title", 100L, ""),

                // AccountId из пробелов
                Arguments.of("Valid title", 100L, "   "),

                // Все поля пустые
                Arguments.of("", 0L, "")
        );
    }
}
