package com.finstuff.security2.controller;

import com.finstuff.security2.dto.*;
import com.finstuff.security2.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@EnableMethodSecurity
@RequestMapping("/finstuff/v1/transaction")
@Tag(name = "Protected transaction service endpoints")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('admin')")
    public Mono<ResponseEntity<AccountTransactionsDTO>> getAll() {
        return service.getAll()
                .map(transactions ->
                        transactions.transactionsList().isEmpty() ?
                                ResponseEntity.noContent().build() : ResponseEntity.ok(transactions));
    }

    @GetMapping("/get-by-id/{transactionId}")
    public Mono<ResponseEntity<TransactionEnlargedDTO>> getById(@PathVariable String transactionId) {
        return service.getById(transactionId)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<TransactionDTO>> add(NewTransactionDTO dto) {
        return service.add(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/update-title")
    public Mono<ResponseEntity<TitleUpdateDTO>> updateTitle(TitleUpdateDTO dto) {
        return service.updateTitle(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/update-amount")
    public Mono<ResponseEntity<AmountUpdateDTO>> updateAmount(AmountUpdateDTO dto) {
        return service.updateAmount(dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return service.deleteTransaction(id)
                .map(ResponseEntity::ok);
    }
}
