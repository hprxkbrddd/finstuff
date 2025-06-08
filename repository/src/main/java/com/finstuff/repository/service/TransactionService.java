package com.finstuff.repository.service;

import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        transaction.setIncome(income);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionsRepository.save(transaction);
    }

    public boolean updateTitle(Long id, String title){
        return transactionsRepository.updateTitle(id, title);
    }

    public boolean updateIncome(Long id, Boolean income){
        return transactionsRepository.updateIncome(id, income);
    }

    public boolean updateAmount(Long id, BigDecimal amount){
        return transactionsRepository.updateAmount(id, amount);
    }

    public boolean delete(Long id){
        if (transactionsRepository.findById(id).isEmpty()) return false;
        transactionsRepository.deleteById(id);
        return true;
    }
}
