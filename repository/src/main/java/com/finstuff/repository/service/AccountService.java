package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.UserAccountsDTO;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.repository.AccountsRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final TransactionsRepository transactionsRepository;
    private final IdGenerator idGenerator;

    // GET ALL ACCOUNTS
    public UserAccountsDTO getAll() {
        return new UserAccountsDTO(accountsRepository.findAll()
                .stream().map(acc ->
                        new AccountDTO(
                                acc.getId(),
                                acc.getTitle(),
                                acc.getOwnedByUserId()
                        )
                ).toList());
    }

    // CREATE ACCOUNT
    @Caching(
            put = @CachePut(value = "account", key = "#result.id"),
            evict = @CacheEvict(value = "accounts_of_user", key = "#ownedByUserId")
    )
    public AccountEnlargedDTO addAccount(String title, String ownedByUserId) {
        if (title.isBlank() || ownedByUserId.isBlank())
            throw new IllegalArgumentException("Blank fields");
        Account account = new Account();
        account.setId(idGenerator.generateId());
        account.setTitle(title);
        account.setOwnedByUserId(ownedByUserId);
        accountsRepository.save(account);
        return new AccountEnlargedDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId(),
                BigDecimal.ZERO
        );
    }

    // GET ACCOUNT BY ID
    @Cacheable(value = "account", key = "#id")
    public AccountEnlargedDTO getById(String id) {
        if (id.isBlank()) throw new IllegalArgumentException("Blank fields");
        Account account = accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-" + id + " is not found"));
        return new AccountEnlargedDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId(),
                getAccountBalance(account.getId())
        );

    }

    // CALCULATE ACCOUNT BALANCE
    private BigDecimal getAccountBalance(String id) {
        return transactionsRepository.getAccountBalance(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-" + id + " is not found"));
    }

    // GET ACCOUNTS OF USER
    @Cacheable(value = "accounts_of_user", key = "#ownerId")
    public List<AccountDTO> getByOwnerId(String ownerId) {
        if (ownerId.isBlank()) throw new IllegalArgumentException("Blank fields");
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

    // UPDATE ACCOUNT TITLE
    @Transactional
    @Caching(
            put = @CachePut(value = "account", key = "#id"),
            evict = @CacheEvict(value = "accounts_of_user", key = "#result.ownedByUserId")
    )
    public AccountEnlargedDTO updateTitle(String id, String title) {
        if (id.isBlank() || title.isBlank()) throw new IllegalArgumentException("Blank fields");
        Account account = accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-" + id + " is not found. Title is not updated"));
        accountsRepository.updateTitle(id, title);
        account.setTitle(title);
        return new AccountEnlargedDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId(),
                getAccountBalance(account.getId())
        );
    }

    // DELETE ACCOUNT
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "account", key = "#id"),
                    @CacheEvict(value = "accounts_of_user", key = "#result.ownedByUserId")
            }
    )
    public AccountEnlargedDTO deleteAccount(String id) {
        if (id.isBlank()) throw new IllegalArgumentException("Blank fields");
        Account account = accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-" + id + " is not found. Account is not deleted"));
        accountsRepository.deleteById(id);
        return new AccountEnlargedDTO(
                account.getId(),
                account.getTitle(),
                account.getOwnedByUserId(),
                BigDecimal.ZERO
        );
    }
}
