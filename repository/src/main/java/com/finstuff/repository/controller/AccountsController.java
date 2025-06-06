package com.finstuff.repository.controller;

import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import com.finstuff.repository.entity.Account;
import com.finstuff.repository.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("finstuff/v1/repo/accounts")
public class AccountsController {
    private final AccountService service;

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAll(){
        return new ResponseEntity<>(service.getAll(),
                service.getAll().isEmpty() ?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id){
        return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
    }

    @GetMapping("/get-by-owner/{ownerId}")
    public ResponseEntity<List<Account>> getByOwnerId(@PathVariable Long ownerId){
        List<Account> accounts = service.getByOwnerId(ownerId);
        return new ResponseEntity<>(accounts,
                accounts.isEmpty() ?
                HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @PostMapping("/add")
    public ResponseEntity<Account> addAccount(@RequestBody AccountDTO dto){
        return new ResponseEntity<>(service.addAccount(dto.title(), dto.ownedByUserId()), HttpStatus.CREATED);
    }

    @PutMapping("/update-title")
    public ResponseEntity<Account> updateTitle(@RequestBody TitleUpdateDTO dto){
        return new ResponseEntity<>(service.updateTitle(dto.id(), dto.title()), HttpStatus.FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return new ResponseEntity<>(service.deleteAccount(id), HttpStatus.OK);
    }
}
