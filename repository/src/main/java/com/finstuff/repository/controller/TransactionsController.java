package com.finstuff.repository.controller;

import com.finstuff.repository.dto.*;
import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<TransactionDTO> add(@RequestBody NewTransactionDTO dto) {
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
        return service.updateTitle(dto.id(), dto.title()) == 1 ?
                new ResponseEntity<>(dto, HttpStatus.OK) :
                new ResponseEntity<>(new TitleUpdateDTO(dto.id(), ""), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update-amount")
    public ResponseEntity<AmountUpdateDTO> updateTitle(@RequestBody AmountUpdateDTO dto) {
        return service.updateAmount(dto.id(), dto.amount()) == 1 ?
                new ResponseEntity<>(dto, HttpStatus.OK) :
                new ResponseEntity<>(new AmountUpdateDTO(dto.id(), BigDecimal.ZERO), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        return service.delete(id) ?
                new ResponseEntity<>(id, HttpStatus.OK) :
                new ResponseEntity<>("err: no transaction in db", HttpStatus.NOT_FOUND);
    }
}
