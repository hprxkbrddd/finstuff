package com.finstuff.security2.dto;

import java.math.BigDecimal;

public record AccountEnlargedDTO(
        String id,
        String title,
        String ownedByUserId,
        BigDecimal balance
) {
}
