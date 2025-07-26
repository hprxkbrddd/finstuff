package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountTransactionsDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.dto.TransactionEnlargedDTO;
import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionsRepository transactionsRepository;
    private final IdGenerator idGenerator;

    // GET ALL TRANSACTIONS
    public AccountTransactionsDTO getAll() {
        return new AccountTransactionsDTO(transactionsRepository.findAll()
                .stream().map(transaction -> new TransactionDTO(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTitle()
                )).toList()
        );
    }

    // GET TRANSACTION BY ID
    @Cacheable(value = "transaction", key = "#id")
    public TransactionEnlargedDTO getById(String id) {
        if (id.isBlank()) throw new IllegalArgumentException("Blank fields");
        Transaction res = transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction-id:" + id + " is not found."));
        return new TransactionEnlargedDTO(
                res.getId(),
                res.getTitle(),
                res.getAmount(),
                res.getTimestamp(),
                res.getAccountId()
        );
    }

    // GET TRANSACTIONS OF THE ACCOUNT
    @Cacheable(value = "account_transactions", key = "#accountId")
    public AccountTransactionsDTO getByAccountId(String accountId) {
        if (accountId.isBlank()) throw new IllegalArgumentException("Blank fields");
        List<Transaction> res = transactionsRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account-id:" + accountId + " either has no transactions or does not exist."));
        return new AccountTransactionsDTO(res.stream().map(
                transaction -> new TransactionDTO(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTitle()
                )
        ).collect(Collectors.toList()));
    }

    // CREATE TRANSACTION
    @Caching(
            put = @CachePut(value = "transaction", key = "#result.id"),
            evict = @CacheEvict(value = "account_transactions", key = "#accountId")
    )
    public TransactionEnlargedDTO add(String title,
                                      BigDecimal amount,
                                      String accountId) {
        if (title.isBlank() || amount == null || amount.equals(BigDecimal.ZERO) || accountId.isBlank())
            throw new IllegalArgumentException("Blank fields");
        Transaction transaction = new Transaction();
        transaction.setId(idGenerator.generateId());
        transaction.setTitle(title);
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction.setTimestamp(LocalDateTime.now());
        Transaction res = transactionsRepository.save(transaction);
        return new TransactionEnlargedDTO(
                res.getId(),
                res.getTitle(),
                res.getAmount(),
                res.getTimestamp(),
                res.getAccountId()
        );
    }

    // UPDATE TITLE OF THE TRANSACTION
    @Transactional
    @Caching(
            put = @CachePut(value = "transaction", key = "#result.id"),
            evict = @CacheEvict(value = "account_transactions", key = "#accountId")
    )
    public TransactionEnlargedDTO updateTitle(String id, String title) {
        if (id.isBlank() || title.isBlank()) throw new IllegalArgumentException("Blank fields");
        Transaction res = transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction-id:" + id + " is not found."));
        transactionsRepository.updateTitle(id, title);
        res.setTitle(title);
        return new TransactionEnlargedDTO(
                res.getId(),
                res.getTitle(),
                res.getAmount(),
                res.getTimestamp(),
                res.getAccountId()
        );
    }

    // UPDATE AMOUNT OF THE TRANSACTION
    @Transactional
    @Caching(
            put = @CachePut(value = "transaction", key = "#result.id"),
            evict = @CacheEvict(value = "account_transactions", key = "#accountId")
    )
    public TransactionEnlargedDTO updateAmount(String id, BigDecimal amount) {
        if (id.isBlank() || amount == null || amount.equals(BigDecimal.ZERO))
            throw new IllegalArgumentException("Blank fields");
        Transaction res = transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction-id:" + id + " is not found."));
        transactionsRepository.updateAmount(id, amount);
        res.setAmount(amount);
        return new TransactionEnlargedDTO(
                res.getId(),
                res.getTitle(),
                res.getAmount(),
                res.getTimestamp(),
                res.getAccountId()
        );
    }

    // DELETE TRANSACTION
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "account_transactions", key = "#result.accountId"),
                    @CacheEvict(value = "transaction", key = "#id")
            }
    )
    public TransactionEnlargedDTO delete(String id) {
        if (id.isBlank()) throw new IllegalArgumentException("Blank fields");
        Transaction res = transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction-id:" + id + " is not found."));
        transactionsRepository.deleteById(id);
        return new TransactionEnlargedDTO(
                res.getId(),
                res.getTitle(),
                res.getAmount(),
                res.getTimestamp(),
                res.getAccountId()
        );
    }
}
