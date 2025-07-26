package com.finstuff.repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.AmountUpdateDTO;
import com.finstuff.repository.dto.NewTransactionDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import com.finstuff.repository.dto.TransactionEnlargedDTO;
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
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RabbitTransactionConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitTransactionConsumer.class);
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.trans.exchange}")
    private String transExchange;

    @Value("${rabbitmq.routing-key.rep-sec.new}")
    private String rkNewRes;
    @Value("${rabbitmq.routing-key.rep-sec.title-upd}")
    private String rkUpdTitleRes;
    @Value("${rabbitmq.routing-key.rep-sec.amnt-upd}")
    private String rkUpdAmntRes;
    @Value("${rabbitmq.routing-key.rep-sec.del}")
    private String rkDelRes;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {"${rabbitmq.trans.queue.sec-rep.new}"})
    public void add(@Payload NewTransactionDTO dto, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived transaction ADD message\n!{}!\nMessage properties\n!{}!\n",
                    objectMapper.writeValueAsString(dto), msg.getMessageProperties());

            // VALIDATION
            if (
                    dto.amount() == null ||
                            dto.amount().equals(BigDecimal.ZERO) ||
                            dto.title().isBlank() ||
                            dto.accountId().isBlank()
            )
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            TransactionEnlargedDTO res = transactionService.add(
                    dto.title(), dto.amount(), dto.accountId());

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    transExchange,
                    rkNewRes,
                    res
            );
            log.info("\n!\nResponded to transaction ADD message\n!{}!\n", res);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "ADD", rkNewRes);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.sec-rep.title-upd}"})
    public void updateTitle(@Payload TitleUpdateDTO dto, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived transaction TITLE UPDATE message\n!{}!\nMessage properties\n!{}!\n",
                    objectMapper.writeValueAsString(dto), msg.getMessageProperties());

            // VALIDATION
            if (dto.title().isBlank() || dto.id().isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            TransactionEnlargedDTO res = transactionService.updateTitle(dto.id(), dto.title());

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    transExchange,
                    rkUpdTitleRes,
                    res
            );
            log.info("\n!\nResponded to transaction TITLE UPDATE message\n!{}!\n", res);
        } catch (EntityNotFoundException e) {
            handleNotFoundException(dto.id(), channel, msg, e, "TITLE UPDATE", rkUpdTitleRes);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "TITLE UPDATE", rkUpdTitleRes);
        }

    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.sec-rep.amnt-upd}"})
    public void updateAmnt(@Payload AmountUpdateDTO dto, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived transaction AMOUNT UPDATE message\n!{}!\nMessage properties\n!{}!\n",
                    objectMapper.writeValueAsString(dto), msg.getMessageProperties());

            // VALIDATION
            if (dto.amount()==null || dto.amount().equals(BigDecimal.ZERO) || dto.id().isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            TransactionEnlargedDTO res = transactionService.updateAmount(dto.id(), dto.amount());

            // SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    transExchange,
                    rkUpdTitleRes,
                    res
            );
            log.info("\n!\nResponded to transaction AMOUNT UPDATE message\n!{}!\n", res);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (EntityNotFoundException e) {
            handleNotFoundException(dto.id(), channel, msg, e, "AMOUNT UPDATE", rkUpdAmntRes);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "AMOUNT UPDATE", rkUpdAmntRes);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.sec-rep.del}"})
    public void delete(@Payload String id, Message msg, Channel channel) throws IOException {
        try {
            log.info("\n!\nReceived transaction DELETE message\n!{}!\nMessage properties\n!{}!\n",
                    id, msg.getMessageProperties());

            // VALIDATION
            if (id.isBlank())
                throw new BadRequestException("Blank/null argument");

            // REPOSITORY CALL
            TransactionEnlargedDTO res = transactionService.delete(id);

            //SENDING RESPONSE
            rabbitTemplate.convertAndSend(
                    transExchange,
                    rkDelRes,
                    res
            );
            log.info("\n!\nResponded to transaction DELETE message\n!{}!\n", res);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (EntityNotFoundException e) {
            handleNotFoundException(id, channel, msg, e, "DELETE", rkDelRes);
        } catch (BadRequestException e) {
            handleIllegalArgumentException(channel, msg, e, "DELETE", rkDelRes);
        }
    }

    private void handleNotFoundException(
            String transactionId,
            Channel channel,
            Message msg,
            Exception e,
            String methodName,
            String routingKey) throws IOException {
        log.warn("Transaction-id:{} is not found: {}", transactionId, e.getMessage());

        // FORMING 'NULL' RESPONSE
        TransactionEnlargedDTO errorResponse = new TransactionEnlargedDTO(
                null, null, null, null, null
        );

        // SENDING RESPONSE
        rabbitTemplate.convertAndSend(transExchange, routingKey, errorResponse);

        // CLOSING CHANNEL
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        log.info("\n!\nResponded to transaction {} message with 'null' response. Reason: not found\n!{}!\n", methodName, e.getMessage());
    }

    private void handleIllegalArgumentException(
            Channel channel,
            Message msg,
            Exception e,
            String methodName,
            String routingKey) throws IOException {
        log.warn("Bad transaction {} request: {}", methodName, e.getMessage());

        //FORMING 'NULL' RESPONSE
        TransactionEnlargedDTO errorResponse = new TransactionEnlargedDTO(
                null, null, null, null, null
        );

        // SENDING RESPONSE
        rabbitTemplate.convertAndSend(transExchange, routingKey, errorResponse);

        // CLOSING CHANNEL
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        log.info("\n!\nResponded to transaction {} message with 'null' response. Reason: bad request\n!{}!\n", methodName, e.getMessage());
    }
}
