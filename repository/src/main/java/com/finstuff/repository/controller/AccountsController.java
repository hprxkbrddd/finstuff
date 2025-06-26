package com.finstuff.repository.controller;

import com.finstuff.repository.dto.*;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("finstuff/v1/repo/accounts")
@Tag(name = "Accounts Controller", description = "Operations pertaining to accounts")
public class AccountsController {
    private final AccountService service;

    @Operation(
            summary = "Get all accounts",
            description = "Returns all accounts from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "204", description = "no accounts in db")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAll(){
        return new ResponseEntity<>(service.getAll(),
                service.getAll().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @Operation(
            summary = "Get account by ID",
            description = "Returns a single account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<AccountEnlargedDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Get account by user ID",
            description = "Returns a list of accounts of user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "201", description = "User has no accounts")
            }
    )
    @GetMapping("/get-by-owner/{ownerId}")
    public ResponseEntity<UserAccountsDTO> getByOwnerId(@PathVariable String ownerId){
        UserAccountsDTO accounts = new UserAccountsDTO(service.getByOwnerId(ownerId));
        return new ResponseEntity<>(accounts,
                accounts.accountList().isEmpty() ?
                HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @Operation(
            summary = "Add account",
            description = "Creates an account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @PostMapping("/add")
    public ResponseEntity<Account> addAccount(@RequestBody NewAccountDTO dto){
        return new ResponseEntity<>(service.addAccount(dto.title(), dto.ownedByUserId()), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update title",
            description = "Updates title of account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @PutMapping("/update-title")
    public ResponseEntity<Account> updateTitle(@RequestBody TitleUpdateDTO dto){
        return new ResponseEntity<>(service.updateTitle(dto.id(), dto.title()), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete account",
            description = "Deletes an account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id){
        return new ResponseEntity<>(service.deleteAccount(id), HttpStatus.OK);
    }
}
