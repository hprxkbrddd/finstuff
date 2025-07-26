package com.finstuff.repository.argsProvider.accountArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UpdateTitleBlankArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("", "new title"),
                Arguments.of("    ", "new title"),
                Arguments.of("id1", ""),
                Arguments.of("id1", "   "),
                Arguments.of("", ""),
                Arguments.of("   ", "   "),
                Arguments.of("", "   "),
                Arguments.of("   ","")
        );
    }
}
