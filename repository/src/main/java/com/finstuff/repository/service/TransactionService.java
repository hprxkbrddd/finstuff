package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountTransactionsDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.dto.TransactionEnlargedDTO;
import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public AccountTransactionsDTO getAll() {
        return new AccountTransactionsDTO(transactionsRepository.findAll()
                .stream().map(transaction -> new TransactionDTO(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTitle()
                )).toList()
        );
    }

    public TransactionEnlargedDTO getById(String id) {
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

    public AccountTransactionsDTO getByAccountId(String accountId) {
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

    public TransactionDTO add(String title,
                              BigDecimal amount,
                              String accountId) {
        Transaction transaction = new Transaction();
        transaction.setId(IdGenerator.generateId());
        transaction.setTitle(title);
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction.setTimestamp(LocalDateTime.now());
        Transaction res = transactionsRepository.save(transaction);
        return new TransactionDTO(
                res.getId(),
                res.getAmount(),
                res.getTitle()
        );
    }

    @Transactional
    public int updateTitle(String id, String title) {
        return transactionsRepository.updateTitle(id, title);
    }

    @Transactional
    public int updateAmount(String id, BigDecimal amount) {
        return transactionsRepository.updateAmount(id, amount);
    }

    @Transactional
    public boolean delete(String id) {
        if (transactionsRepository.findById(id).isEmpty()) return false;
        transactionsRepository.deleteById(id);
        return true;
    }
}
