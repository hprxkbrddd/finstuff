package com.finstuff.repository.dto;

import java.math.BigDecimal;

public record NewTransactionDTO(
        String title,
        BigDecimal amount,
        String accountId)
{}