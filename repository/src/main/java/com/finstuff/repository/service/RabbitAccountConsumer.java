package com.finstuff.repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.*;
import com.rabbitmq.client.Channel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    public void add(@Payload NewAccountDTO dto, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived account ADD message\n!{}!\nMessage properties\n!{}!\n",
                    objectMapper.writeValueAsString(dto), msg.getMessageProperties());

            // VALIDATION
            if (dto.title().isBlank() || dto.ownedByUserId().isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            AccountEnlargedDTO res = accountService.addAccount(dto.title(), dto.ownedByUserId());

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    accExchange,
                    rkNewRes,
                    res
            );
            log.info("\n!\nResponded to account ADD message\n!{}!\n", res);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "ADD", rkDelRes);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.sec-rep.title-upd}"})
    public void updateTitle(@Payload TitleUpdateDTO dto, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived account TITLE UPDATE message\n!{}!\nMessage properties\n!{}!\n",
                    objectMapper.writeValueAsString(dto), msg.getMessageProperties());
            // VALIDATION
            if (dto.id().isBlank() || dto.title().isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            AccountEnlargedDTO res = accountService.updateTitle(dto.id(), dto.title());

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    accExchange,
                    rkUpdTitleRes,
                    res
            );
            log.info("\n!\nResponded to account TITLE UPDATE message\n!{}!\n", res);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "TITLE UPDATE", rkUpdTitleRes);
        } catch (EntityNotFoundException e){
            handleNotFoundException(dto.id(), channel, msg, e, "TITLE UPDATE", rkUpdTitleRes);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.sec-rep.del}"})
    public void delete(@Payload String accountId, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived account DELETE message\n!{}!\nMessage properties\n!{}!\n",
                    accountId, msg.getMessageProperties());

            // VALIDATION
            if (accountId.isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            AccountEnlargedDTO res = accountService.deleteAccount(accountId);

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    accExchange,
                    rkDelRes,
                    res
            );
            log.info("\n!\nResponded to account DELETE message\n!{}!\n", res);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "DELETE", rkDelRes);
        } catch (EntityNotFoundException e){
            handleNotFoundException(accountId, channel, msg, e, "DELETE", rkDelRes);
        }
    }

    private void handleNotFoundException(
            String accountId,
            Channel channel,
            Message msg,
            Exception e,
            String methodName,
            String routingKey) throws IOException {
        log.warn("Account-id:{} is not found: {}", accountId, e.getMessage());

        // FORMING 'NULL' RESPONSE
        AccountEnlargedDTO errorResponse = new AccountEnlargedDTO(
                null, null, null, null
        );

        // SENDING RESPONSE
        rabbitTemplate.convertAndSend(accExchange, routingKey, errorResponse);

        // CLOSING CHANNEL
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        log.info("\n!\nResponded to account {} message with 'null' response. Reason: not found\n!{}!\n", methodName, e.getMessage());
    }

    private void handleIllegalArgumentException(
            Channel channel,
            Message msg,
            Exception e,
            String methodName,
            String routingKey) throws IOException {
        log.warn("Bad account {} request: {}", methodName, e.getMessage());

        //FORMING 'NULL' RESPONSE
        AccountEnlargedDTO errorResponse = new AccountEnlargedDTO(
                null, null, null, null
        );

        // SENDING RESPONSE
        rabbitTemplate.convertAndSend(accExchange, routingKey, errorResponse);

        // CLOSING CHANNEL
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        log.info("\n!\nResponded to account {} message with 'null' response. Reason: bad request\n!{}!\n", methodName, e.getMessage());
    }
}
