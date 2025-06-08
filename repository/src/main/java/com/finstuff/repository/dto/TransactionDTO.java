package com.finstuff.repository.dto;

import java.math.BigDecimal;

public record TransactionDTO(
        String title,
        Boolean income,
        BigDecimal amount,
        Long accountId)
{}