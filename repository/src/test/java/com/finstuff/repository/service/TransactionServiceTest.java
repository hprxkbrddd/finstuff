package com.finstuff.repository.service;

import com.finstuff.repository.argsProvider.transactionArgsProvider.*;
import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountTransactionsDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.dto.TransactionEnlargedDTO;
import com.finstuff.repository.entity.Transaction;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TransactionService transactionService;

    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        transactions = new ArrayList<>();
        transactions.addAll(
                List.of(
                        createTransaction("Tid1", "testTrans1", "id1",
                                LocalDateTime.of(2001, 1, 1, 0, 0),
                                100L),
                        createTransaction("Tid2", "testTrans2", "id2",
                                LocalDateTime.of(2002, 2, 1, 0, 0),
                                200L),
                        createTransaction("Tid3", "testTrans3", "id3",
                                LocalDateTime.of(2003, 3, 1, 0, 0),
                                300L),
                        createTransaction("Tid4", "testTrans4", "id3",
                                LocalDateTime.of(2004, 4, 1, 0, 0),
                                -400L),
                        createTransaction("Tid5", "testTrans5", "id4",
                                LocalDateTime.of(2005, 5, 1, 0, 0),
                                500L),
                        createTransaction("Tid6", "testTrans6", "id4",
                                LocalDateTime.of(2006, 6, 1, 0, 0),
                                600L),
                        createTransaction("Tid7", "testTrans7", "id4",
                                LocalDateTime.of(2007, 7, 1, 0, 0),
                                -700L)
                )
        );
    }

    private Transaction createTransaction(String id, String title, String accountId, LocalDateTime timestamp, Long amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTitle(title);
        transaction.setAccountId(accountId);
        transaction.setTimestamp(timestamp);
        transaction.setAmount(BigDecimal.valueOf(amount));
        return transaction;
    }

    @Test
    void getAll() {
        // ARRANGE
        AccountTransactionsDTO expected = new AccountTransactionsDTO(
                transactions.stream().map(transaction ->
                                new TransactionDTO(
                                        transaction.getId(),
                                        transaction.getAmount(),
                                        transaction.getTitle()
                                ))
                        .toList());
        when(transactionsRepository.findAll()).thenReturn(transactions);

        // ACT
        AccountTransactionsDTO res = transactionService.getAll();

        // ASSERT
        assertEquals(expected, res);
        verify(transactionsRepository).findAll();
    }

    @ParameterizedTest
    @ArgumentsSource(GetByIdArgsProvider.class)
    void getById_valid(String id, String title, String accountId, LocalDateTime timestamp, Long amount) {
        // ARRANGE
        TransactionEnlargedDTO expected = new TransactionEnlargedDTO(id, title, BigDecimal.valueOf(amount), timestamp, accountId);
        when(transactionsRepository.findById(id)).thenReturn(
                transactions.stream()
                        .filter(trans ->
                                trans.getId().equals(id))
                        .findFirst()
        );

        // ACT
        TransactionEnlargedDTO result = transactionService.getById(id);

        // ASSERT
        verify(transactionsRepository).findById(id);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void getById_blank(String id) {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.getById(id));
    }

    @Test
    void getById_invalid() {
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getById("non-existing-id"));
    }

    @ParameterizedTest
    @ArgumentsSource(GetByAccIdArgsProvider.class)
    void getByAccountId_valid(String accountId, List<TransactionDTO> expected) {
        // ARRANGE
        List<Transaction> transactionList = transactions.stream()
                .filter(trans ->
                        trans.getAccountId().equals(accountId)
                ).toList();

        when(transactionsRepository.findByAccountId(accountId)).thenReturn(Optional.of(transactionList));

        // ACT
        AccountTransactionsDTO result = transactionService.getByAccountId(accountId);

        // ASSERT
        verify(transactionsRepository).findByAccountId(accountId);
        assertEquals(expected, result.transactionList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void getByAccountId_blank(String accountId) {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.getByAccountId(accountId));
    }

    @Test
    void getByAccountId_invalid() {
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getByAccountId("non-existing-acc-id"));
    }

    @ParameterizedTest
    @ArgumentsSource(AddValidArgsProvider.class)
    void add(String title, Long amount, String accountId) {
        // ARRANGE
        when(idGenerator.generateId()).thenReturn("generated-id");
        when(transactionsRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));
        // ACT
        TransactionEnlargedDTO result = transactionService.add(title, BigDecimal.valueOf(amount), accountId);

        // ASSERT
        assertEquals("generated-id", result.id());
        assertEquals(title, result.title());
        assertEquals(BigDecimal.valueOf(amount), result.amount());
        assertEquals(accountId, result.accountId());
        verify(idGenerator).generateId();
        verify(transactionsRepository).save(any(Transaction.class));
    }

    @ParameterizedTest
    @ArgumentsSource(AddInvalidArgsProvider.class)
    void add_blank(String title, Long amount, String accountId) {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.add(title, BigDecimal.valueOf(amount), accountId));
    }

    @Test
    void updateTitle_valid() {
        // ARRANGE
        when(transactionsRepository.findById("Tid1")).thenReturn(
                transactions.stream()
                        .filter(trans ->
                                trans.getId().equals("Tid1"))
                        .findFirst()
        );

        // ACT
        TransactionEnlargedDTO result = transactionService.updateTitle("Tid1", "newTitle");

        // ASSERT
        assertEquals("newTitle", result.title());
        verify(transactionsRepository).updateTitle("Tid1", "newTitle");
    }

    @ParameterizedTest
    @ArgumentsSource(UpdateTitleInvalidArgsProvider.class)
    void updateTitle_blank(String id, String newTitle){
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.updateTitle(id, newTitle));
    }

    @Test
    void updateTitle_invalid(){
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.updateTitle("non-existing-id", "new-title"));
    }

    @Test
    void updateAmount_valid() {
        // ARRANGE
        when(transactionsRepository.findById("Tid1")).thenReturn(
                transactions.stream()
                        .filter(trans ->
                                trans.getId().equals("Tid1"))
                        .findFirst()
        );

        // ACT
        TransactionEnlargedDTO result = transactionService.updateAmount("Tid1", BigDecimal.valueOf(10000));

        // ASSERT
        assertEquals(BigDecimal.valueOf(10000), result.amount());
        verify(transactionsRepository).updateAmount("Tid1", BigDecimal.valueOf(10000));
    }

    @ParameterizedTest
    @ArgumentsSource(UpdateAmountBlankArgsProvider.class)
    void updateAmount_blank(String id, BigDecimal amount){
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.updateAmount(id, amount));
    }

    @Test
    void updateAmount_invalid(){
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.updateAmount("non-existing-id", BigDecimal.TEN));
    }

    @Test
    void deleteAccount() {
        // ARRANGE
        when(transactionsRepository.findById("Tid1")).thenReturn(transactions.stream()
                .filter(trans ->
                        trans.getId().equals("Tid1"))
                .findFirst());

        // ACT
        TransactionEnlargedDTO result = transactionService.delete("Tid1");

        // ASSERT
        verify(transactionsRepository).deleteById("Tid1");
        assertEquals("Tid1", result.id());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void deleteAccount_blank(String id){
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.delete(id));
    }

    @Test
    void deleteAccount_invalid(){
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.delete("non-existing-id"));
    }
}