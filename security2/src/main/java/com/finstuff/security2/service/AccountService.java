package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final WebClient webClient = WebClient.create("http://localhost:8082/repo/accounts");
    private final JWTDecoder decoder = new JWTDecoder();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.acc.exchange}")
    private String accExchange;

    @Value("${rabbitmq.routing-key.sec-rep.new}")
    private String rkNew;
    @Value("${rabbitmq.routing-key.sec-rep.title-upd}")
    private String rkUpdTitle;
    @Value("${rabbitmq.routing-key.sec-rep.del}")
    private String rkDel;

    private String extractSubject(String token) {
        Map<String, Object> claims = decoder.jwtDecoder().decode(token).getClaims();
        return claims.get("sub").toString();
    }

    public Mono<UserAccountsDTO> getAll() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    public void create(String token, String title) throws JsonProcessingException {
        log.info("\n!\nSending account ADD message\n!");
        NewAccountDTO dto = new NewAccountDTO(title, extractSubject(token));
        rabbitTemplate.convertAndSend(
                accExchange,
                rkNew,
                dto
        );
        log.info("\n!\nAccount ADD message sent\n!{}!",objectMapper.writeValueAsString(dto));
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

    public void updateTitle(TitleUpdateDTO dto) throws JsonProcessingException {
        log.info("\n!\nSending account TITLE UPDATE message\n!");
        rabbitTemplate.convertAndSend(
                accExchange,
                rkUpdTitle,
                dto
        );
        log.info("\n!\nAccount TITLE UPDATE message sent\n!{}!",objectMapper.writeValueAsString(dto));
    }

    public void delete (String accountId) {
        log.info("\n!\nSending account DELETE message\n!");
        rabbitTemplate.convertAndSend(
                accExchange,
                rkDel,
                accountId
        );
        log.info("\n!\nAccount DELETE message sent\n!{}!",accountId);
    }
}
