package com.finstuff.repository.argsProvider.transactionArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class UpdateAmountBlankArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("", BigDecimal.TEN),
                Arguments.of("   ", BigDecimal.TEN),
                Arguments.of("", BigDecimal.ZERO),
                Arguments.of("   ", BigDecimal.ZERO),
                Arguments.of("Tid1", BigDecimal.ZERO)
        );
    }
}
