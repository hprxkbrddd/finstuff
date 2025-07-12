package com.finstuff.security2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitAccountListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitAccountListener.class);

    @RabbitListener(queues = {"${rabbitmq.queue.rep-sec}"})
    public void consumer(String message){
        log.info("MESSAGE -> {}",message);
    }
}
