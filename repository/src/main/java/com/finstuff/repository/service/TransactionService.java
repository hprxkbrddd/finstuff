package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
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

    public Optional<Transaction> getById(String id){
        return transactionsRepository.findById(id);
    }

    public Transaction add(String title,
                           BigDecimal amount,
                           String accountId){
        Transaction transaction = new Transaction();
        transaction.setId(IdGenerator.generateId());
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
    public int updateAmount(String id, BigDecimal amount){
        return transactionsRepository.updateAmount(id, amount);
    }

    @Transactional
    public boolean delete(String id){
        if (transactionsRepository.findById(id).isEmpty()) return false;
        transactionsRepository.deleteById(id);
        return true;
    }
}
