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

    @PostMapping("/add")
    public ResponseEntity<TransactionEnlargedDTO> add(@RequestBody NewTransactionDTO dto) {
        return new ResponseEntity<>(
                service.add(
                        dto.title(),
                        dto.amount(),
                        dto.accountId()
                ),
                HttpStatus.CREATED);
    }

    @PutMapping("/update-title")
    public ResponseEntity<TitleUpdateDTO> updateTitle(@RequestBody TitleUpdateDTO dto) {
        TransactionEnlargedDTO result = service.updateTitle(dto.id(), dto.title());
        return ResponseEntity.ok(new TitleUpdateDTO(
                result.id(),
                result.title()
        ));
    }

    @PutMapping("/update-amount")
    public ResponseEntity<AmountUpdateDTO> updateTitle(@RequestBody AmountUpdateDTO dto) {
        TransactionEnlargedDTO result = service.updateAmount(dto.id(), dto.amount());
        return ResponseEntity.ok(new AmountUpdateDTO(
                result.id(),
                result.amount()
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<TransactionEnlargedDTO> delete(@PathVariable String id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
