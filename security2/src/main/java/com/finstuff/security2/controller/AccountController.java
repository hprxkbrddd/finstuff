package com.finstuff.security2.controller;

import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.AccountEnlargedDTO;
import com.finstuff.security2.dto.TitleUpdateDTO;
import com.finstuff.security2.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@EnableMethodSecurity
@RequestMapping("/finstuff/v1/account")
@Tag(name = "Protected account service endpoints")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(
            summary = "Get user's accounts",
            description = "Returns a list of accounts of token owner. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "204", description = "User has no accounts"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials")
            }
    )
    @GetMapping("/list")
    public Mono<ResponseEntity<List<AccountDTO>>> getAccounts(@RequestHeader("Authorization") String token) {
        return accountService.getAccounts(token.substring(7))
                .map(accounts ->
                        accounts.accountList().isEmpty() ?
                                ResponseEntity.noContent().build() : ResponseEntity.ok(accounts.accountList()));
    }

    @Operation(
            summary = "Get all accounts",
            description = "Returns all accounts from db. Requires admin role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "204", description = "No accounts in db"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials"),
                    @ApiResponse(responseCode = "403", description = "No admin role detected")
            }
    )
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-all")
    public Mono<ResponseEntity<List<AccountDTO>>> getAll(Authentication authentication) {
        return accountService.getAll()
                .map(accounts -> accounts.accountList().isEmpty() ?
                        ResponseEntity.noContent().build() : ResponseEntity.ok(accounts.accountList()));
    }

    @GetMapping("/get-by-id/{id}")
    public Mono<ResponseEntity<AccountEnlargedDTO>> getById(@PathVariable String id){
        return accountService.getById(id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Create account",
            description = "Creates account and binds it to owner of the token. " +
                    "Requires title of the new account. " +
                    "Title has to be sent as plain text in request body.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials"),
                    @ApiResponse(responseCode = "400", description = "Wrong body")
            }
    )
    @PostMapping("/new")
    public Mono<ResponseEntity<AccountEnlargedDTO>> add(
            @RequestHeader("Authorization") String token,
            @RequestBody String title) {
        return accountService.create(token.substring(7), title)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Update account's title",
            description = "Updates title of chosen account. " +
                    "Body must contain account id and its new title",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials"),
                    @ApiResponse(responseCode = "400", description = "Wrong body")
            }
    )
    @PutMapping("/update-title")
    public Mono<ResponseEntity<AccountEnlargedDTO>> updateTitle(@RequestBody TitleUpdateDTO dto) {
        return accountService.updateTitle(dto.id(), dto.title())
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Delete account",
            description = "Deletes chosen account. " +
                    "Requires Bearer token within Authorization header. " +
                    "Account id has to be provided in endpoint's path.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials"),
                    @ApiResponse(responseCode = "400", description = "Wrong body")
            }
    )
    @DeleteMapping("/delete/{accountId}")
    public Mono<ResponseEntity<AccountDTO>> delete(@PathVariable String accountId) {
        return accountService.delete(accountId)
                .map(ResponseEntity::ok);
    }
}
