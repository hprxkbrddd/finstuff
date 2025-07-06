package com.finstuff.security2.controller;

import com.finstuff.security2.dto.*;
import com.finstuff.security2.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(
            summary = "Get all transactions",
            description = "Returns contracted data about all transactions in db." +
                    " Requires admin role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "204", description = "no transactions in db")
            }
    )
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('admin')")
    public Mono<ResponseEntity<AccountTransactionsDTO>> getAll() {
        return service.getAll()
                .map(transactions ->
                        transactions.transactionList().isEmpty() ?
                                ResponseEntity.noContent().build() : ResponseEntity.ok(transactions));
    }

    @Operation(
            summary = "Get transaction by id",
            description = "Returns full data about one transaction. " +
                    "Requires transaction id in request path.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @GetMapping("/get-by-id/{transactionId}")
    public Mono<ResponseEntity<TransactionEnlargedDTO>> getById(@PathVariable String transactionId) {
        return service.getById(transactionId)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get transactions of account",
            description = "Returns an object which contains transactionList, which belong to provided account. " +
                    "Requires transaction id in request path.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @GetMapping("/get-by-account-id/{accountId}")
    public Mono<ResponseEntity<AccountTransactionsDTO>> getByAccountId(@PathVariable String accountId){
        return service.getByAccountId(accountId)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Add transaction",
            description = "Creates new transaction. " +
                    "Requires transaction data (title, amount and account which it belongs to) in request body",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success"),
            }
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<TransactionDTO>> add(@RequestBody NewTransactionDTO dto) {
        return service.add(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Update title",
            description = "Updates title of the transaction. " +
                    "Requires new transaction title and id of the transaction in request body",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/update-title")
    public Mono<ResponseEntity<TitleUpdateDTO>> updateTitle(@RequestBody TitleUpdateDTO dto) {
        return service.updateTitle(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Update amount",
            description = "Updates amount of the transaction. " +
                    "Requires new transaction amount and id of the transaction in request body",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/update-amount")
    public Mono<ResponseEntity<AmountUpdateDTO>> updateAmount(@RequestBody AmountUpdateDTO dto) {
        return service.updateAmount(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Delete transaction",
            description = "Deletes the transaction. Transaction id has to be provided in request path",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return service.deleteTransaction(id)
                .map(ResponseEntity::ok);
    }
}
