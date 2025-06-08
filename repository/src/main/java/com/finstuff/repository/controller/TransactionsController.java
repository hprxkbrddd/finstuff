package com.finstuff.repository.controller;

import com.finstuff.repository.dto.AmountUpdateDTO;
import com.finstuff.repository.dto.IncomeUpdateDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import com.finstuff.repository.dto.TransactionDTO;
import com.finstuff.repository.entity.Transaction;
import com.finstuff.repository.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("finstuff/v1/repo/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "Operations pertaining to transactions")
public class TransactionsController {

    private final TransactionService service;

    @Operation(
            summary = "Get all transactions",
            description = "Returns all transactions from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "204", description = "no transactions in db")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAll(){
        return new ResponseEntity<>(service.getAll(),
                service.getAll().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @Operation(
            summary = "Get transaction by id",
            description = "Returns one transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id){
        Optional<Transaction> transaction = service.getById(id);
        return transaction.map(
                value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new Transaction(), HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Add transaction",
            description = "Creates transaction",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success"),
            }
    )
    @PostMapping("/add")
    public ResponseEntity<Transaction> add(@RequestBody TransactionDTO dto){
        return new ResponseEntity<>(
                service.add(
                        dto.title(),
                        dto.income(),
                        dto.amount(),
                        dto.accountId()
                ),
                HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update title",
            description = "Updates title of the transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/update-title")
    public ResponseEntity<String> updateTitle(@RequestBody TitleUpdateDTO dto){
        return service.updateTitle(dto.id(), dto.title())?
                new ResponseEntity<>("Title has been updated to '"+dto.title()+"'", HttpStatus.OK):
                new ResponseEntity<>("Title has not been updated", HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Update income",
            description = "Updates income of the transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/update-income")
    public ResponseEntity<String> updateIncome(@RequestBody IncomeUpdateDTO dto){
        return service.updateIncome(dto.id(), dto.income())?
                new ResponseEntity<>("Income has been updated to '"+dto.income()+"'", HttpStatus.OK):
                new ResponseEntity<>("Income has not been updated", HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Update amount",
            description = "Updates amount of the transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/update-amount")
    public ResponseEntity<String> updateTitle(@RequestBody AmountUpdateDTO dto){
        return service.updateAmount(dto.id(), dto.amount())?
                new ResponseEntity<>("Amount has been updated to '"+dto.amount()+"'", HttpStatus.OK):
                new ResponseEntity<>("Amount has not been updated", HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Delete transaction",
            description = "Deletes the transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "No transaction in db")
            }
    )
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return service.delete(id)?
                new ResponseEntity<>("Transaction-id:"+id+" has been deleted", HttpStatus.OK):
                new ResponseEntity<>("Transaction-id:"+id+" has not been deleted", HttpStatus.NOT_FOUND);
    }
}
