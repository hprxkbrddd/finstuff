package com.finstuff.security2.controller;

import com.finstuff.security2.dto.AuthDTO;
import com.finstuff.security2.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@EnableMethodSecurity
@RequestMapping("/finstuff/v1/auth")
@Tag(name = "Authorization service")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Get JWT token",
            description = "Returns a JWT token as plain text. " +
                    "Body must contain a username and a password of the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Wrong credentials"),
                    @ApiResponse(responseCode = "400", description = "Wrong body")
            }
    )
    @PostMapping("/token")
    public Mono<ResponseEntity<String>> getToken(@RequestBody AuthDTO dto) {
        return authService.getToken(dto.username(), dto.password())
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse.access_token()));
    }
}
