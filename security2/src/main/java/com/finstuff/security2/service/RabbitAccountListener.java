package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.dto.AccountEnlargedDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitAccountListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitAccountListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.rep-sec}", durable = "false"),
                    exchange = @Exchange(value = "${rabbitmq.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.account.response.new}"
            )
    )
    public void addResponseHandler(String message, Message msg) throws JsonProcessingException {
        AccountEnlargedDTO response = objectMapper.readValue(message, AccountEnlargedDTO.class);
        log.info("\n!Repository service ADD response! \n{}\n!Message properties! \n{}\n",message, msg.getMessageProperties());
    }
}
