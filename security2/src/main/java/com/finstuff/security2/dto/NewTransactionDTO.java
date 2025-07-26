package com.finstuff.security2.dto;

import java.math.BigDecimal;

public record NewTransactionDTO(
        String title,
        BigDecimal amount,
        String accountId
) {
}
