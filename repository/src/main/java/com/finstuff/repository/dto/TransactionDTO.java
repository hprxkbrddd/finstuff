package com.finstuff.repository.dto;

import java.math.BigDecimal;

public record TransactionDTO(
        String title,
        BigDecimal amount,
        String accountId)
{}