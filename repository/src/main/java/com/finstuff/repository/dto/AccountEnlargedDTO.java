package com.finstuff.repository.dto;

import java.math.BigDecimal;
public record AccountEnlargedDTO(
        String id,
        String title,
        String ownedByUserId,
        BigDecimal balance) {
}
