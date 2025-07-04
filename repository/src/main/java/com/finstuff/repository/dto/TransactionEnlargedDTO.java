package com.finstuff.repository.dto;

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
