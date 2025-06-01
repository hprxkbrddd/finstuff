package com.finstuff.repository.service;

import com.finstuff.repository.entity.Account;
import com.finstuff.repository.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;

    public List<Account> getAll(){
        return accountsRepository.findAll();
    }
}
