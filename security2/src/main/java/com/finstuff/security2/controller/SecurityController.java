package com.finstuff.security2.controller;

import com.finstuff.security2.dto.AuthDTO;
import com.finstuff.security2.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@EnableMethodSecurity
@RequestMapping("/finstuff/v1/keycloak")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    @GetMapping("/")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok("HOOOOOOOOOOOOOOOOOOOOOME");
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<String>> getToken(@RequestBody AuthDTO dto) {
        return securityService.getToken(dto.username(), dto.password())
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse.access_token()));
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld(Authentication authentication ){
        log.info("User authorities: {}", authentication.getAuthorities());
        return ResponseEntity.ok("Heeeeelo world");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-slavik")
    public ResponseEntity<String> getAll(){
        return ResponseEntity.ok("Wazuup slavik");
    }
}
