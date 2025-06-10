package com.finstuff.repository.repository;

import com.finstuff.repository.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    @Modifying
    @Query("UPDATE Transaction t SET t.title = :newValue WHERE t.id = :id")
    int updateTitle(@Param("id") Long id, @Param("newValue") String newValue);

    @Modifying
    @Query("UPDATE Transaction t SET t.amount = :newValue WHERE t.id = :id")
    int updateAmount(@Param("id") Long id, @Param("newValue") BigDecimal newValue);
}
