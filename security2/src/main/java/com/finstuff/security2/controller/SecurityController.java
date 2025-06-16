package com.finstuff.security2.controller;

import com.finstuff.security2.dto.PersonDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld(Authentication authentication ){
        log.info("User authorities: {}", authentication.getAuthorities());
        return ResponseEntity.ok("Heeeeelo world");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-slavik")
    public ResponseEntity<PersonDTO> getAll(){
        return ResponseEntity.ok(new PersonDTO("Slavik", "slavik@mail.com"));
    }
}
