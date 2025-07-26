package com.finstuff.repository.argsProvider.transactionArgsProvider;

import com.finstuff.repository.dto.TransactionDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class GetByAccIdArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("id1", List.of(
                        new TransactionDTO("Tid1", BigDecimal.valueOf(100), "testTrans1")
                )),
                Arguments.of("id2", List.of(
                        new TransactionDTO("Tid2", BigDecimal.valueOf(200), "testTrans2")
                )),
                Arguments.of("id3", List.of(
                        new TransactionDTO("Tid3", BigDecimal.valueOf(300), "testTrans3"),
                        new TransactionDTO("Tid4", BigDecimal.valueOf(-400), "testTrans4")
                )),
                Arguments.of("id4", List.of(
                        new TransactionDTO("Tid5", BigDecimal.valueOf(500), "testTrans5"),
                        new TransactionDTO("Tid6", BigDecimal.valueOf(600), "testTrans6"),
                        new TransactionDTO("Tid7", BigDecimal.valueOf(-700), "testTrans7")
                ))
        );
    }
}
