package com.finstuff.security2.service;

import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.TokenRequestDTO;
import com.finstuff.security2.dto.TokenResponseDTO;
import com.finstuff.security2.dto.UserAccountsDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
    private final WebClient webClient = WebClient.create("http://localhost:8080/realms/finstuff/protocol/openid-connect");
    private final WebClient webClientRepo = WebClient.create("http://localhost:8082/finstuff/v1/repo");
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    public Mono<TokenResponseDTO> getToken(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
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

    public Mono<UserAccountsDTO> getAccounts(String token) {
        JWTDecoder decoder = new JWTDecoder();
        Map<String, Object> claims = decoder.jwtDecoder().decode(token).getClaims();
        String ownerId = claims.get("sub").toString();
        log.info("Issuer id: {}", ownerId);
        return webClientRepo.get()
                .uri("/accounts/get-by-owner/"+ownerId)
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }
}
