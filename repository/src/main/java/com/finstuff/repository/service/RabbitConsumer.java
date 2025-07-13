package com.finstuff.repository.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.NewAccountDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.rep-sec}")
    private String replyQueue;

    @Value("${rabbitmq.routing-key.account.response.new}")
    private String rkNewRes;
    @Value("${rabbitmq.routing-key.account.title-upd}")
    private String rkUpdTitle;
    @Value("${rabbitmq.routing-key.account.del}")
    private String rkDel;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.sec-rep}", durable = "false"),
                    exchange = @Exchange(value = "${rabbitmq.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.account.new}"
            )
    )
    public void add(@Payload String message, Message msg){
        try {
            log.info("\n!Received account ADD message! \n{}\n!Message properties!\n {}\n", message, msg.getMessageProperties());
            NewAccountDTO dto = objectMapper.readValue(message, NewAccountDTO.class);
            AccountEnlargedDTO res = accountService.addAccount(dto.title(), dto.ownedByUserId());
            rabbitTemplate.convertAndSend(
                    exchange,
                    rkNewRes,
                    objectMapper.writeValueAsString(res)
            );
        } catch (JsonProcessingException e) {
            log.error("Account 'add': JSON parsing exception: {}",e.getMessage());
        }
    }

//    @RabbitListener(
//            bindings = @QueueBinding(
//                    value = @Queue(value = "${rabbitmq.queue.sec-rep}", durable = "false"),
//                    exchange = @Exchange(value = "${rabbitmq.exchange}", type = ExchangeTypes.TOPIC),
//                    key = "${rabbitmq.routing-key.account.title-upd}"
//            )
//    )
//    public String updateTitle(@Payload String message, Message msg){
//        try {
//            log.info("!Received account TITLE UPDATE message! \nMessage properties -> {}", msg.getMessageProperties());
//            TitleUpdateDTO dto = objectMapper.readValue(message, TitleUpdateDTO.class);
//            AccountEnlargedDTO res = accountService.updateTitle(dto.id(), dto.title());
//            return objectMapper.writeValueAsString(res);
//        } catch (JsonProcessingException e) {
//            log.error("Account 'update title': JSON parsing exception: {}",e.getMessage());
//            return "Account's title is not updated";
//        }
//    }
}
