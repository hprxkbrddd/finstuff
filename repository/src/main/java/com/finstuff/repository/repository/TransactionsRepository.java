package com.finstuff.repository.repository;

import com.finstuff.repository.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionsRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountId = :accountId")
    Optional<BigDecimal> getAccountBalance(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE Transaction t SET t.title = :newValue WHERE t.id = :id")
    void updateTitle(@Param("id") String id, @Param("newValue") String newValue);

    @Modifying
    @Query("UPDATE Transaction t SET t.amount = :newValue WHERE t.id = :id")
    void updateAmount(@Param("id") String id, @Param("newValue") BigDecimal newValue);

    Optional<List<Transaction>> findByAccountId(String accountId);
}
