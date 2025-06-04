package com.finstuff.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @Value("${rabbitmq.routing-key.name}")
    String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMsg(String message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
