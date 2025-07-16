package com.finstuff.security2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.security2.dto.TransactionEnlargedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RabbitTransactionListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitTransactionListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = {"${rabbitmq.trans.queue.rep-sec.new}"})
    public void addResponseHandler(TransactionEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service transaction ADD response\n!{}!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.rep-sec.title-upd}"})
    public void updateTitleResponseHandler(TransactionEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service transaction TITLE UPDATE response\n!\n{}\n!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.rep-sec.amnt-upd}"})
    public void updateAmntResponseHandler(TransactionEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service transaction AMOUNT UPDATE response\n!\n{}\n!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

    @RabbitListener(queues = {"${rabbitmq.trans.queue.rep-sec.del}"})
    public void delResponseHandler(TransactionEnlargedDTO dto, Message msg) throws JsonProcessingException {
        log.info("\n!\nRepository service transaction DELETE response\n!\n{}\n!\nMessage properties\n!{}!\n",
                objectMapper.writeValueAsString(dto), msg.getMessageProperties());
    }

}
