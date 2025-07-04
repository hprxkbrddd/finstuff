package com.finstuff.security2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEnlargedDTO(
        String id,
        String title,
        BigDecimal amount,
        LocalDateTime timestamp,
        String accountId
) {
}
