package com.finstuff.repository.argsProvider.transactionArgsProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class GetByIdArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Tid1", "testTrans1", "id1",
                        LocalDateTime.of(2001, 1, 1, 0, 0),
                        100L),
                Arguments.of("Tid2", "testTrans2", "id2",
                        LocalDateTime.of(2002, 2, 1, 0, 0),
                        200L),
                Arguments.of("Tid3", "testTrans3", "id3",
                        LocalDateTime.of(2003, 3, 1, 0, 0),
                        300L),
                Arguments.of("Tid4", "testTrans4", "id3",
                        LocalDateTime.of(2004, 4, 1, 0, 0),
                        -400L),
                Arguments.of("Tid5", "testTrans5", "id4",
                        LocalDateTime.of(2005, 5, 1, 0, 0),
                        500L),
                Arguments.of("Tid6", "testTrans6", "id4",
                        LocalDateTime.of(2006, 6, 1, 0, 0),
                        600L),
                Arguments.of("Tid7", "testTrans7", "id4",
                        LocalDateTime.of(2007, 7, 1, 0, 0),
                        -700L)
        );
    }
}
