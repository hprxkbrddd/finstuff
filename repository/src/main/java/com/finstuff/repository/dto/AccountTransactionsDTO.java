package com.finstuff.repository.dto;

import java.util.List;

public record AccountTransactionsDTO(List<TransactionDTO> transactionList) {
}
