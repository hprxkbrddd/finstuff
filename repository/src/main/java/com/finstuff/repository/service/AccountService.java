package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.dto.UserAccountsDTO;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.repository.AccountsRepository;
import com.finstuff.repository.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final TransactionsRepository transactionsRepository;

    public UserAccountsDTO getAll(){
        return new UserAccountsDTO(accountsRepository.findAll()
                .stream().map(acc ->
                        new AccountDTO(
                                acc.getId(),
                                acc.getTitle(),
                                acc.getOwnedByUserId()
                        )
                ).toList());
    }

    @CacheEvict(value = "accounts_of_user", key = "#ownedByUserId")
    public AccountDTO addAccount(String title, String ownedByUserId){
        Account account = new Account();
        account.setId(IdGenerator.generateId());
        account.setTitle(title);
        account.setOwnedByUserId(ownedByUserId);
        accountsRepository.save(account);
        return new AccountDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId()
        );
    }

    public AccountEnlargedDTO getById(String id){
        Account account = accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-"+id+" is not found"));
        List<TransactionDTO> transactions = account.getTransactions()
                .stream().map(t -> new TransactionDTO(
                        t.getId(),
                        t.getAmount(),
                        t.getTitle()
                )).toList();
        return new AccountEnlargedDTO(
                account.getTitle(),
                account.getOwnedByUserId(),
                transactions,
                getAccountBalance(account.getId())
        );

    }

    private BigDecimal getAccountBalance(String id){
        return transactionsRepository.getAccountBalance(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-"+id+" is not found"));
    }

    @Cacheable(value = "accounts_of_user", key = "#ownerId")
    public List<AccountDTO> getByOwnerId(String ownerId) {
        List<Account> accounts = accountsRepository.findByOwnedByUserId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner:id-" + ownerId + " is not found"));
        return accounts.stream()
                .map(acc -> new AccountDTO(
                        acc.getId(),
                        acc.getTitle(),
                        acc.getOwnedByUserId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDTO updateTitle(String id, String title){
        accountsRepository.updateTitle(id, title);
        Account account = accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-"+id+" is not found. Title is not updated"));
        return new AccountDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId()
        );
    }

    public String deleteAccount(String id){
        String accountTitle = accountsRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Account:id-"+id+" is not found. Account is not deleted"))
                                .getTitle();
        accountsRepository.deleteById(id);
        return accountTitle;
    }
}
