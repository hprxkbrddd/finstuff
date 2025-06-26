package com.finstuff.repository.service;

import com.finstuff.repository.component.IdGenerator;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.repository.AccountsRepository;
import com.finstuff.repository.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final TransactionsRepository transactionsRepository;

    public List<Account> getAll(){
        return accountsRepository.findAll();
    }

    public Account addAccount(String title, String ownedByUserId){
        Account account = new Account();
        account.setId(IdGenerator.generateId());
        account.setTitle(title);
        account.setOwnedByUserId(ownedByUserId);
        return accountsRepository.save(account);
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

    public List<AccountDTO> getByOwnerId(String ownerId){
        List<Account> accounts = accountsRepository.findByOwnedByUserId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner:id-"+ownerId+" is not found"));
        return accounts.stream().map(acc ->
                new AccountDTO(
                        acc.getId(),
                        acc.getTitle()
                )
        ).toList();
    }

    @Transactional
    public Account updateTitle(String id, String title){
        accountsRepository.updateTitle(id, title);
        return accountsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account:id-"+id+" is not found. Title is not updated"));
    }

    public String deleteAccount(String id){
        accountsRepository.deleteById(id);
        return "Account with id: "+id+" has been deleted";
    }
}
