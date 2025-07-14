package com.finstuff.repository.controller;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // CONNECTION DATA
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    // EXCHANGES
    @Value("${rabbitmq.acc.exchange}")
    private String accExchange;
    @Value("${rabbitmq.trans.exchange}")
    private String transExchange;

    // ACCOUNT QUEUES
    @Value("${rabbitmq.acc.queue.rep-sec.new}")
    private String repSecAcNewQueue;
    @Value("${rabbitmq.acc.queue.rep-sec.title-upd}")
    private String repSecAcTitleUpdQueue;
    @Value("${rabbitmq.acc.queue.rep-sec.del}")
    private String repSecAcDelQueue;

    // TRANSACTION QUEUES
    @Value("${rabbitmq.trans.queue.rep-sec.new}")
    private String repSecTrNewQueue;
    @Value("${rabbitmq.trans.queue.rep-sec.title-upd}")
    private String repSecTrTitleUpdQueue;
    @Value("${rabbitmq.trans.queue.rep-sec.del}")
    private String repSecTrDelQueue;

    // ROUTING KEYS
    @Value("${rabbitmq.routing-key.rep-sec.new}")
    private String rkNew;
    @Value("${rabbitmq.routing-key.rep-sec.title-upd}")
    private String rkTitleUpd;
    @Value("${rabbitmq.routing-key.rep-sec.del}")
    private String rkDel;

    @Bean
    public TopicExchange accExchange() {
        return new TopicExchange(accExchange);
    }

    @Bean
    public Queue repSecAcNewQueue() {
        return new Queue(repSecAcNewQueue, false);
    }

    @Bean
    public Queue repSecAcTitleUpdQueue() {
        return new Queue(repSecAcTitleUpdQueue, false);
    }

    @Bean
    public Queue repSecAcDelQueue() {
        return new Queue(repSecAcDelQueue, false);
    }

    @Bean
    Binding repSecAcNewResBinding() {
        return BindingBuilder.bind(repSecAcNewQueue()).to(accExchange()).with(rkNew);
    }

    @Bean
    Binding repSecAcTitleUpdResBinding() {
        return BindingBuilder.bind(repSecAcTitleUpdQueue()).to(accExchange()).with(rkTitleUpd);
    }

    @Bean
    Binding repSecAcDelResBinding() {
        return BindingBuilder.bind(repSecAcDelQueue()).to(accExchange()).with(rkDel);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReplyTimeout(10000); // 10 секунд
        return template;
    }
}
