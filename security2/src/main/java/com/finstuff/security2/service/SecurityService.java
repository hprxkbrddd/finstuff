package com.finstuff.security2.service;

import com.finstuff.security2.dto.TokenRequestDTO;
import com.finstuff.security2.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final WebClient webClient = WebClient.create("http://localhost:8080/realms/finstuff/protocol/openid-connect");

    public Mono<TokenResponseDTO> getToken(String username, String password){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "finstuff-client");
        formData.add("username", username);
        formData.add("password", password);
        formData.add("scope", "openid");

        return webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class);
    }
}
