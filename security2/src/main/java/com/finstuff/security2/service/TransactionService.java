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
public class TransactionService {
    private final WebClient webClient = WebClient.create("http://localhost:8082/repo/transactions");

    public Mono<AccountTransactionsDTO> getAll(){
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToMono(AccountTransactionsDTO.class);
    }

    public Mono<TransactionEnlargedDTO> getById(String transactionId){
        return webClient.get()
                .uri(String.format("/get-by-id/%s", transactionId))
                .retrieve()
                .bodyToMono(TransactionEnlargedDTO.class);
    }

    public Mono<AccountTransactionsDTO> getByAccountId(String accountId){
        return webClient.get()
                .uri(String.format("/get-by-account-id/%s", accountId))
                .retrieve()
                .bodyToMono(AccountTransactionsDTO.class);
    }

    public Mono<TransactionDTO> add(NewTransactionDTO dto){
        return webClient.post()
                .uri("/add")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(TransactionDTO.class);
    }

    public Mono<TitleUpdateDTO> updateTitle(TitleUpdateDTO dto){
        return webClient.put()
                .uri("/update-title")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(TitleUpdateDTO.class);
    }

    public Mono<AmountUpdateDTO> updateAmount(AmountUpdateDTO dto){
        return webClient.put()
                .uri("/update-title")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(AmountUpdateDTO.class);
    }

    public Mono<String> deleteTransaction(String id){
        return webClient.delete()
                .uri(String.format("/delete/%s", id))
                .retrieve()
                .bodyToMono(String.class);
    }
}
