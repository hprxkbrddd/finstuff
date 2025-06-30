package com.finstuff.security2.service;

import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.NewAccountDTO;
import com.finstuff.security2.dto.UpdateTitleDTO;
import com.finstuff.security2.dto.UserAccountsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
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

    public Mono<AccountDTO> create(String token, String title){
        return webClient.post()
                .uri("/add")
                .bodyValue(new NewAccountDTO(title, extractSubject(token)))
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    public Mono<UserAccountsDTO> getAccounts(String token) {
        return webClient.get()
                .uri("/get-by-owner/"+extractSubject(token))
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public Mono<AccountDTO> updateTitle(String accountId, String newTitle){
        return webClient.put()
                .uri("/update-title")
                .bodyValue(new UpdateTitleDTO(accountId, newTitle))
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    public Mono<String> delete(String accountId){
        return webClient.delete()
                .uri("/delete/"+accountId)
                .retrieve()
                .bodyToMono(String.class);
    }
}
