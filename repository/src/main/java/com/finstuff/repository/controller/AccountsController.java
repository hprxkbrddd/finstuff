package com.finstuff.repository.controller;

import com.finstuff.repository.entity.Account;
import com.finstuff.repository.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("finstuff/v1/repo/accounts")
public class AccountsController {
    private final AccountService service;


    public ResponseEntity<List<Account>> getAll(){
        return new ResponseEntity<>(service.getAll(),
                service.getAll().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }
}
