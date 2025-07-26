package com.finstuff.repository.argsProvider.accountArgsProvider;

import com.finstuff.repository.dto.AccountDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

public class GetByOwnerIdValidArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Uid1", List.of(
                        new AccountDTO("id1", "testAcc1", "Uid1"))),
                Arguments.of("Uid2", List.of(
                        new AccountDTO("id2", "testAcc2", "Uid2"))),
                Arguments.of("Uid3", List.of(
                        new AccountDTO("id3", "testAcc3", "Uid3"),
                        new AccountDTO("id4", "testAcc4", "Uid3")
                ))
        );
    }
}
