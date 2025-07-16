package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.component.JWTDecoder;
import com.finstuff.security2.dto.*;
import lombok.NonNull;
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

    @Value("${url.repository}")
    @NonNull
    private String repositoryURL;

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final WebClient webClient = WebClient.create(repositoryURL);
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

    // GET TOKEN OWNER
    private String extractSubject(String token) {
        Map<String, Object> claims = decoder.jwtDecoder().decode(token).getClaims();
        return claims.get("sub").toString();
    }

    // GET ALL ACCOUNTS
    public Mono<UserAccountsDTO> getAll() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    // CREATE ACCOUNT
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

    // GET ACCOUNT BY ID
    public Mono<AccountEnlargedDTO> getById(String id) {
        return webClient.get()
                .uri("/get-by-id/" + id)
                .retrieve()
                .bodyToMono(AccountEnlargedDTO.class);
    }

    // GET ACCOUNTS OF USER
    public Mono<UserAccountsDTO> getAccounts(String token) {
        return webClient.get()
                .uri("/get-by-owner/" + extractSubject(token))
                .retrieve()
                .bodyToMono(UserAccountsDTO.class);
    }

    // UPDATE TITLE OF THE ACCOUNT
    public void updateTitle(TitleUpdateDTO dto) throws JsonProcessingException {
        log.info("\n!\nSending account TITLE UPDATE message\n!");
        rabbitTemplate.convertAndSend(
                accExchange,
                rkUpdTitle,
                dto
        );
        log.info("\n!\nAccount TITLE UPDATE message sent\n!{}!",objectMapper.writeValueAsString(dto));
    }

    // DELETE ACCOUNT
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
