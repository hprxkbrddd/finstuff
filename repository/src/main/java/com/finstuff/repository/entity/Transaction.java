package com.finstuff.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    private String id;
    private String title;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String accountId;
}