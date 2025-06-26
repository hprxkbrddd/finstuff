package com.finstuff.repository.service;

import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionsRepository transactionsRepository;

    public List<Transaction> getAll(){
        return transactionsRepository.findAll();
    }

    public Optional<Transaction> getById(Long id){
        return transactionsRepository.findById(id);
    }

    public Transaction add(String title,
                           Boolean income,
                           BigDecimal amount,
                           Long accountId){
        Transaction transaction = new Transaction();
        transaction.setTitle(title);
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionsRepository.save(transaction);
    }

    @Transactional
    public int updateTitle(String id, String title){
        return transactionsRepository.updateTitle(id, title);
    }

    @Transactional
    public int updateAmount(Long id, BigDecimal amount){
        return transactionsRepository.updateAmount(id, amount);
    }
    @Transactional
    public boolean delete(Long id){
        if (transactionsRepository.findById(id).isEmpty()) return false;
        transactionsRepository.deleteById(id);
        return true;
    }
}
