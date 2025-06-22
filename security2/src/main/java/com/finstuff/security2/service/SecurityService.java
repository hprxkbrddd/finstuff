package com.finstuff.security2.service;

import com.finstuff.security2.dto.TokenRequestDTO;
import com.finstuff.security2.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final WebClient webClient = WebClient.create("http://localhost:8080/realms/finstuff/protocol/openid-connect");

    public Mono<TokenResponseDTO> getToken(String username, String password){
        return webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(new TokenRequestDTO(
                        "password",
                        "finstuff_client",
                        username,
                        password,
                        "openid"
                ))
                .retrieve()
                .bodyToMono(TokenResponseDTO.class);
    }
}
