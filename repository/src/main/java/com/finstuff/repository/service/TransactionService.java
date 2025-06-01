package com.finstuff.repository.service;

import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionsRepository transactionsRepository;

    public List<Transaction> getAll(){
        return transactionsRepository.findAll();
    }
}
