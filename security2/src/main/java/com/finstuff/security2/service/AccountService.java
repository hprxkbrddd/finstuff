package com.finstuff.security2.service;

import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final WebClient webClient = WebClient.create("http://localhost:8082/repo/accounts");
    private final JWTDecoder decoder = new JWTDecoder();

    private String extractSubject(String token){
        Map<String, Object> claims = decoder.jwtDecoder().decode(token).getClaims();
        return claims.get("sub").toString();
    }

    public Mono<UserAccountsDTO> getAll(){
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public Mono<AccountEnlargedDTO> create(String token, String title){
        return webClient.post()
                .uri("/add")
                .bodyValue(new NewAccountDTO(title, extractSubject(token)))
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    // TODO add protection from illegal access
    public Mono<AccountEnlargedDTO> getById(String id){
        return webClient.get()
                .uri("/get-by-id/"+id)
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    public Mono<UserAccountsDTO> getAccounts(String token) {
        return webClient.get()
                .uri("/get-by-owner/"+extractSubject(token))
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public Mono<AccountEnlargedDTO> updateTitle(String accountId, String newTitle){
        return webClient.put()
                .uri("/update-title")
                .bodyValue(new TitleUpdateDTO(accountId, newTitle))
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    public Mono<AccountDTO> delete(String accountId){
        return webClient.delete()
                .uri("/delete/"+accountId)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }
}
