package com.finstuff.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    private final String title;
    private final Boolean income;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private final Account account;
}