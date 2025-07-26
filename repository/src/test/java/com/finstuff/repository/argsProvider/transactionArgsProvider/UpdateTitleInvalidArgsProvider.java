package com.finstuff.repository.argsProvider.transactionArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UpdateTitleInvalidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("", "   "),
                Arguments.of("", ""),
                Arguments.of("   ", "   "),
                Arguments.of("   ", "")
        );
    }
}
