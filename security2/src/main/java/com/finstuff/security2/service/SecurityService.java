package com.finstuff.security2.service;

import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.TokenRequestDTO;
import com.finstuff.security2.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final WebClient webClient = WebClient.create("http://localhost:8080/realms/finstuff/protocol/openid-connect");
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

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

//    public List<AccountDTO> getAccounts(String token){
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//
//        formData.add("token", token);
//        formData.add("client_id", clientId);
//        formData.add("client_secret", clientSecret);
//        webClient.post()
//                .uri("/token/introspect")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .bodyValue(formData)
//                .retrieve()
//                .bodyToMono(TokenResponseDTO.class);
//    }
}
