package com.finstuff.repository.controller;

import com.finstuff.repository.dto.*;
import com.finstuff.repository.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repo/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService service;

    @GetMapping("/all")
    public ResponseEntity<AccountTransactionsDTO> getAll() {
        AccountTransactionsDTO res = service.getAll();
        return new ResponseEntity<>(res,
                res.transactionList().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<TransactionEnlargedDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/get-by-account-id/{accountId}")
    public ResponseEntity<AccountTransactionsDTO> getByAccountId(@PathVariable String accountId) {
        AccountTransactionsDTO res = service.getByAccountId(accountId);
        return new ResponseEntity<>(res,
                res.transactionList().isEmpty()?
                        HttpStatus.NO_CONTENT : HttpStatus.OK);
    }
}
