package com.finstuff.repository.repository;

import com.finstuff.repository.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
}
