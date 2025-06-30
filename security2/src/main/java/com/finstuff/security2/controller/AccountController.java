package com.finstuff.security2.controller;

import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.UpdateTitleDTO;
import com.finstuff.security2.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class AccountController {

    @Autowired
    private AccountService accountService;

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("/list")
    public Mono<ResponseEntity<List<AccountDTO>>> getAccounts(@RequestHeader("Authorization") String token){
        return accountService.getAccounts(token.substring(7))
                .map(accounts ->
                        ResponseEntity.ok(accounts.accountList()));
    }

    //TODO some issues with admin access
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-all")
    public Mono<ResponseEntity<List<AccountDTO>>> getAll(Authentication authentication){
        return accountService.getAll()
                .map(accounts ->
                        ResponseEntity.ok(accounts.accountList()));
    }

    @PostMapping("/new")
    public Mono<ResponseEntity<AccountDTO>> add(@RequestHeader("Authorization") String token, @RequestBody String title){
        return accountService.create(token.substring(7), title)
                .map(account ->
                        ResponseEntity.ok(new AccountDTO(
                                account.id(),
                                account.title(),
                                account.ownedByUserId())
                        )
                );
    }

    @PutMapping("/update-title")
    public Mono<ResponseEntity<AccountDTO>> updateTitle(@RequestBody UpdateTitleDTO dto){
        return accountService.updateTitle(dto.id(), dto.title())
                .map(account ->
                        ResponseEntity.ok(new AccountDTO(
                                account.id(),
                                account.title(),
                                account.ownedByUserId())
                        )
                );
    }

    @DeleteMapping("/delete/{accountId}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String accountId) {
        return accountService.delete(accountId)
                .map(ResponseEntity::ok);
    }
}
