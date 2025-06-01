package com.finstuff.security.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(String msg){
        System.out.println("--------THE MESSAGE-------------> "+msg);
    }
}
