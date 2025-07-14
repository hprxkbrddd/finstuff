package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.dto.AccountDTO;
import com.finstuff.security2.dto.AccountEnlargedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitAccountListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitAccountListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = {"${rabbitmq.acc.queue.rep-sec.new}"})
    public void addResponseHandler(AccountEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service ADD response\n!{}!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.rep-sec.title-upd}"})
    public void updateTitleResponseHandler(AccountEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service TITLE UPDATE response\n!\n{}\n!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

    @RabbitListener(queues = {"${rabbitmq.acc.queue.rep-sec.del}"})
    public void delResponseHandler(AccountDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service DELETE response\n!\n{}\n!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }
}
