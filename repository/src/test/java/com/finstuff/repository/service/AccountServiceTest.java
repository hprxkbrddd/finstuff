package com.finstuff.repository.service;

import com.finstuff.repository.argsProvider.accountArgsProvider.*;
import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.UserAccountsDTO;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.AccountsRepository;
import com.finstuff.repository.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private TransactionsRepository transactionsRepository;

    @InjectMocks
    private AccountService accountService;

    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        accounts = List.of(
                createAccount("id1", "testAcc1", "Uid1", 200L),
                createAccount("id2", "testAcc2", "Uid2", 300L),
                createAccount("id3", "testAcc3", "Uid3", 400L),
                createAccount("id4", "testAcc4", "Uid3", 500L));
    }

    private Account createAccount(String id, String title, String ownedByUserId, Long balance) {
        Account account = new Account(); // Используем конструктор без параметров
        account.setId(id);
        account.setTitle(title);
        account.setOwnedByUserId(ownedByUserId);
        account.setTransactions(List.of(createTransaction("Tid1-" + id.charAt(2), id, LocalDateTime.of(2020, Integer.parseInt(id.substring(2)), Integer.parseInt(id.substring(2)), 0, 0), balance)));
        return account;
    }

    private Transaction createTransaction(String id, String accountId, LocalDateTime timestamp, Long amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTitle("transaction1");
        transaction.setAccountId(accountId);
        transaction.setTimestamp(timestamp);
        transaction.setAmount(BigDecimal.valueOf(amount));
        return transaction;
    }

    private AccountEnlargedDTO createExpectedResult(String id, String title, String userId, Long balance) {
        return new AccountEnlargedDTO(id, title, userId, BigDecimal.valueOf(balance));
    }

    @Test
    void getAll() {
        // ARRANGE
        UserAccountsDTO expected = new UserAccountsDTO(
                accounts.stream().map(account ->
                                new AccountDTO(
                                        account.getId(),
                                        account.getTitle(),
                                        account.getOwnedByUserId()))
                        .toList());
        when(accountsRepository.findAll()).thenReturn(accounts);

        // ACT
        UserAccountsDTO res = accountService.getAll();

        // ASSERT
        assertEquals(expected, res);
    }

    @ParameterizedTest
    @ArgumentsSource(AddValidArgsProvider.class)
    void addAccount_validData(String title, String ownedByUserId) {

        // ARRANGE
        AccountEnlargedDTO expectedResponse = createExpectedResult(
                "generated-id", title, ownedByUserId, 0L);

        when(idGenerator.generateId()).thenReturn("generated-id");
        when(accountsRepository.save(any(Account.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // ACT
        AccountEnlargedDTO result = accountService.addAccount(title, ownedByUserId);

        // ASSERT
        assertEquals(expectedResponse, result);
        verify(accountsRepository).save(argThat(account ->
                account.getId().equals("generated-id") &&
                        account.getTitle().equals(title) &&
                        account.getOwnedByUserId().equals(ownedByUserId)));
    }

    @ParameterizedTest
    @ArgumentsSource(AddInvalidArgsProvider.class)
    void addAccount_invalidData(String title, String ownedByUserId) {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.addAccount(title, ownedByUserId));
    }

    @ParameterizedTest
    @ArgumentsSource(GetByIdArgsProvider.class)
    void getById_validId(String id, String accountTitle, String ownedByUserId, Long balance) {
        // ARRANGE
        AccountEnlargedDTO expectedRes = createExpectedResult(id, accountTitle, ownedByUserId, balance);

        when(accountsRepository.findById(id)).thenReturn(
                accounts.stream()
                        .filter(acc ->
                                acc.getId().equals(id))
                        .findFirst());
        when(transactionsRepository.getAccountBalance(id)).thenReturn(Optional.of(BigDecimal.valueOf(balance)));

        // ACT
        AccountEnlargedDTO result = accountService.getById(id);

        // ASSERT
        verify(accountsRepository).findById(id);
        verify(transactionsRepository).getAccountBalance(id);
        assertEquals(expectedRes, result);
    }

    @Test
    void getById_nonExistingId() {
        assertThrows(EntityNotFoundException.class, () ->
                accountService.getById("non-existing-id"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void getById_blankId(String id) {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.getById(id));
    }

    @ParameterizedTest
    @ArgumentsSource(GetByOwnerIdValidArgsProvider.class)
    void getByOwnerId_validData(String ownerId, List<AccountDTO> expectedAccounts) {
        // ARRANGE
        List<Account> accountsOfUser = accounts
                .stream().filter(account ->
                        account.getOwnedByUserId().equals(ownerId))
                .toList();
        when(accountsRepository.findByOwnedByUserId(ownerId)).thenReturn(Optional.of(accountsOfUser));

        // ACT
        List<AccountDTO> result = accountService.getByOwnerId(ownerId);

        // ASSERT
        assertEquals(expectedAccounts, result);
        verify(accountsRepository).findByOwnedByUserId(ownerId);
    }

    @Test
    void getByOwnerId_nonExistingUser() {
        assertThrows(EntityNotFoundException.class, () ->
                accountService.getByOwnerId("non-existing-id"));
    }

    @Test
    void updateTitle_valid() {
        // ARRANGE
        when(accountsRepository.findById("id1")).thenReturn(accounts.stream()
                .filter(acc ->
                        acc.getId().equals("id1"))
                .findFirst());
        when(transactionsRepository.getAccountBalance("id1")).thenReturn(Optional.of(BigDecimal.valueOf(200)));

        // ACT
        AccountEnlargedDTO result = accountService.updateTitle("id1", "testAcc1_renamed");

        // ASSERT
        verify(accountsRepository).updateTitle("id1", "testAcc1_renamed");
        assertEquals("testAcc1_renamed", result.title());
    }

    @ParameterizedTest
    @ArgumentsSource(UpdateTitleBlankArgsProvider.class)
    void updateTitle_blank(String id, String newTitle) {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.updateTitle(id, newTitle));
    }

    @Test
    void updateTitle_invalid() {
        assertThrows(EntityNotFoundException.class,
                () -> accountService.updateTitle("non-existing-id", "new title"));
    }

    @Test
    void deleteAccount() {
        // ARRANGE
        when(accountsRepository.findById("id1")).thenReturn(accounts.stream()
                .filter(acc ->
                        acc.getId().equals("id1"))
                .findFirst());

        // ACT
        AccountEnlargedDTO result = accountService.deleteAccount("id1");

        // ASSERT
        verify(accountsRepository).deleteById("id1");
        assertEquals("id1", result.id());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void deleteAccount_blank(String id){
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deleteAccount(id));
    }

    @Test
    void deleteAccount_invalid(){
        assertThrows(EntityNotFoundException.class,
                () -> accountService.deleteAccount("non-existing-id"));
    }
}