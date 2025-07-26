package com.finstuff.repository.argsProvider.accountArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class GetByIdArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("id1", "testAcc1", "Uid1", 200L),
                Arguments.of("id2", "testAcc2", "Uid2", 300L),
                Arguments.of("id3", "testAcc3", "Uid3", 400L),
                Arguments.of("id4", "testAcc4", "Uid3", 500L)
        );
    }
}
