package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WebClient webClient = WebClient.create("http://localhost:8082/repo/transactions");
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.trans.exchange}")
    private String transExchange;

    @Value("${rabbitmq.routing-key.sec-rep.new}")
    private String rkNew;
    @Value("${rabbitmq.routing-key.sec-rep.title-upd}")
    private String rkUpdTitle;
    @Value("${rabbitmq.routing-key.sec-rep.amnt-upd}")
    private String rkUpdAmnt;
    @Value("${rabbitmq.routing-key.sec-rep.del}")
    private String rkDel;

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

    public void add(NewTransactionDTO dto) throws JsonProcessingException {
        log.info("\n!\nSending transaction ADD message\n!");
        rabbitTemplate.convertAndSend(
                transExchange,
                rkNew,
                dto
        );
        log.info("\n!\nTransaction ADD message sent\n!{}!",objectMapper.writeValueAsString(dto));
    }

    public void updateTitle(TitleUpdateDTO dto) throws JsonProcessingException {
        log.info("\n!\nSending transaction TITLE UPDATE message\n!");
        rabbitTemplate.convertAndSend(
                transExchange,
                rkUpdTitle,
                dto
        );
        log.info("\n!\nTransaction TITLE UPDATE message sent\n!{}!",objectMapper.writeValueAsString(dto));
    }

    public void updateAmnt(AmountUpdateDTO dto) throws JsonProcessingException {
        log.info("\n!\nSending transaction AMOUNT UPDATE message\n!");
        rabbitTemplate.convertAndSend(
                transExchange,
                rkUpdAmnt,
                dto
        );
        log.info("\n!\nTransaction AMOUNT UPDATE message sent\n!{}!",objectMapper.writeValueAsString(dto));
    }

    public void delete (String transactionId) {
        log.info("\n!\nSending transaction DELETE message\n!");
        rabbitTemplate.convertAndSend(
                transExchange,
                rkDel,
                transactionId
        );
        log.info("\n!\nTransaction DELETE message sent\n!{}!",transactionId);
    }
}
