package com.finstuff.repository.dto;

import com.finstuff.repository.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public record AccountEnlargedDTO(
        String id,
        String title,
        String ownedByUserId,
        List<TransactionDTO> transactions,
        BigDecimal balance) {
}
