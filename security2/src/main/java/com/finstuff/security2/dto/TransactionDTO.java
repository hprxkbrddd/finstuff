package com.finstuff.security2.dto;

import java.math.BigDecimal;

public record TransactionDTO(String id, BigDecimal amount, String title) {
}
