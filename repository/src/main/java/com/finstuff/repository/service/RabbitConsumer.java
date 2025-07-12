package com.finstuff.repository.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.NewAccountDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.sec-rep}", durable = "false"),
                    exchange = @Exchange(value = "${rabbitmq.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.account.new}"
            )
    )
    public String consume(@Payload String message, Message msg){
        try {
            log.info("Message properties -> {}", msg.getMessageProperties());
            NewAccountDTO dto = objectMapper.readValue(message, NewAccountDTO.class);
            AccountEnlargedDTO res = accountService.addAccount(dto.title(), dto.ownedByUserId());
            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            log.error("JSON parsing exception: {}",e.getMessage());
            return "null";
        }
    }
}
