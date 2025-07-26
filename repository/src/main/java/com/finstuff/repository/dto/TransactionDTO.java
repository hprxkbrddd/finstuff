package com.finstuff.repository.dto;

import java.math.BigDecimal;

public record TransactionDTO(String id, BigDecimal amount, String title) {
}
