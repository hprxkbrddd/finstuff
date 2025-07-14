package com.finstuff.repository.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.NewAccountDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitAccountConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitAccountConsumer.class);
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.acc.exchange}")
    private String accExchange;

    @Value("${rabbitmq.routing-key.rep-sec.new}")
    private String rkNewRes;
    @Value("${rabbitmq.routing-key.rep-sec.title-upd}")
    private String rkUpdTitleRes;
    @Value("${rabbitmq.routing-key.rep-sec.del}")
    private String rkDelRes;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {"${rabbitmq.acc.queue.sec-rep.new}"})
    public void add(@Payload NewAccountDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nReceived account ADD message\n!{}!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
        AccountEnlargedDTO res = accountService.addAccount(dto.title(), dto.ownedByUserId());
        rabbitTemplate.convertAndSend(
                accExchange,
                rkNewRes,
                res
        );
        log.info("\n!\nResponded to account ADD message\n!{}!\n",res);
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.sec-rep.title-upd}"})
    public void updateTitle(@Payload TitleUpdateDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nReceived account TITLE UPDATE message\n!{}!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
        AccountEnlargedDTO res = accountService.updateTitle(dto.id(), dto.title());
        rabbitTemplate.convertAndSend(
                accExchange,
                rkUpdTitleRes,
                res
        );
        log.info("\n!\nResponded to account TITLE UPDATE message\n!{}!\n",res);
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.sec-rep.del}"})
    public void delete(@Payload String accountId, Message msg){
        log.info("\n!\nReceived account DELETE message\n!{}!\nMessage properties\n!{}!\n",
                accountId, msg.getMessageProperties());
        AccountDTO res = accountService.deleteAccount(accountId);
        rabbitTemplate.convertAndSend(
                accExchange,
                rkDelRes,
                res
        );
        log.info("\n!\nResponded to account DELETE message\n!{}!\n",res);
    }
}
