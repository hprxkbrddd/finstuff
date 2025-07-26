package com.finstuff.security2.dto;


import java.util.List;

public record AccountTransactionsDTO(List<TransactionDTO> transactionList) {
}
