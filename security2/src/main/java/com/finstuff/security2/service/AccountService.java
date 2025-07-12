package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final WebClient webClient = WebClient.create("http://localhost:8082/repo/accounts");
    private final JWTDecoder decoder = new JWTDecoder();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.rep-sec}")
    private String queue;

    @Value("${rabbitmq.routing-key.account.new}")
    private String rkNew;
    @Value("${rabbitmq.routing-key.account.title-upd}")
    private String rkUpdTitle;
    @Value("${rabbitmq.routing-key.account.del}")
    private String rkDel;

    private String extractSubject(String token) {
        Map<String, Object> claims = decoder.jwtDecoder().decode(token).getClaims();
        return claims.get("sub").toString();
    }



//    private void sendMsg(Object message, String routingKey) throws JsonProcessingException {
//        rabbitTemplate.convertAndSend(
//                exchange,
//                routingKey,
//                objectMapper.writeValueAsString(message),
//                m -> {
//                    m.getMessageProperties().setReplyTo(queue);
//                    m.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
//                    return m;
//                }
//        );
//    }

    public Mono<UserAccountsDTO> getAll() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public void create(String token, String title) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(
                exchange,
                rkNew,
                objectMapper.writeValueAsString(new NewAccountDTO(title, extractSubject(token))),
                m -> {
                    m.getMessageProperties().setReplyTo(queue);
                    m.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
                    return m;
                }
        );
    }

    public Mono<AccountEnlargedDTO> getById(String id) {
        return webClient.get()
                .uri("/get-by-id/" + id)
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    public Mono<UserAccountsDTO> getAccounts(String token) {
        return webClient.get()
                .uri("/get-by-owner/" + extractSubject(token))
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public Mono<AccountEnlargedDTO> updateTitle(String accountId, String newTitle) {
        return webClient.put()
                .uri("/update-title")
                .bodyValue(new TitleUpdateDTO(accountId, newTitle))
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    public Mono<AccountDTO> delete(String accountId) {
        return webClient.delete()
                .uri("/delete/" + accountId)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }
}
