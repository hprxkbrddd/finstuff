package com.finstuff.security2.controller;

import com.finstuff.security2.dto.AuthDTO;
import com.finstuff.security2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@EnableMethodSecurity
@RequestMapping("/finstuff/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    public Mono<ResponseEntity<String>> getToken(@RequestBody AuthDTO dto) {
        return authService.getToken(dto.username(), dto.password())
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse.access_token()));
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Heeeeelo world");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-slavik")
    public ResponseEntity<String> getAll(){
        return ResponseEntity.ok("Wazuup slavik");
    }
}
