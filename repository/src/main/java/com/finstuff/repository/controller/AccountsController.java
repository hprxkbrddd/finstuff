package com.finstuff.repository.controller;

import com.finstuff.repository.dto.*;
import com.finstuff.repository.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repo/accounts")
public class AccountsController {
    private final AccountService service;

    @GetMapping("/all")
    public ResponseEntity<UserAccountsDTO> getAll(){
        return new ResponseEntity<>(service.getAll(),
                service.getAll().accountList().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<AccountEnlargedDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
    }

    @GetMapping("/get-by-owner/{ownerId}")
    public ResponseEntity<UserAccountsDTO> getByOwnerId(@PathVariable String ownerId){
        UserAccountsDTO accounts = new UserAccountsDTO(service.getByOwnerId(ownerId));
        return new ResponseEntity<>(accounts,
                accounts.accountList().isEmpty() ?
                HttpStatus.NO_CONTENT : HttpStatus.OK);
    }
}
