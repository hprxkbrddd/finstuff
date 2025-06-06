package com.finstuff.repository.repository;

import com.finstuff.repository.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Account, Long> {

    @Query("SELECT transaction_id FROM accounts WHERE id=${accountId}")
    Optional<List<Long>> getAccountTransactions(Long accountId);

    Optional<List<Account>> findByOwnedByUserId(Long ownedByUserId);
}
