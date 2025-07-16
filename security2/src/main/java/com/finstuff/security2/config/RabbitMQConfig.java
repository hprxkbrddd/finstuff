package com.finstuff.security2.config;

import org.springframework.amqp.core.*;
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
    @Value("${rabbitmq.acc.queue.sec-rep.new}")
    private String secRepAcNewQueue;
    @Value("${rabbitmq.acc.queue.sec-rep.title-upd}")
    private String secRepAcTitleUpdQueue;
    @Value("${rabbitmq.acc.queue.sec-rep.del}")
    private String secRepAcDelQueue;

    // TRANSACTION QUEUES
    @Value("${rabbitmq.trans.queue.sec-rep.new}")
    private String secRepTrNewQueue;
    @Value("${rabbitmq.trans.queue.sec-rep.title-upd}")
    private String secRepTrTitleUpdQueue;
    @Value("${rabbitmq.trans.queue.sec-rep.amnt-upd}")
    private String secRepTrAmntUpdQueue;
    @Value("${rabbitmq.trans.queue.sec-rep.del}")
    private String secRepTrDelQueue;

    // ROUTING KEYS
    @Value("${rabbitmq.routing-key.sec-rep.new}")
    private String rkNew;
    @Value("${rabbitmq.routing-key.sec-rep.title-upd}")
    private String rkTitleUpd;
    @Value("${rabbitmq.routing-key.sec-rep.amnt-upd}")
    private String rkAmntUpd;
    @Value("${rabbitmq.routing-key.sec-rep.del}")
    private String rkDel;

    // EXCHANGES BEANS
    @Bean
    public TopicExchange accExchange() {
        return new TopicExchange(accExchange);
    }

    @Bean
    public TopicExchange transExchange() {
        return new TopicExchange(transExchange);
    }

    // ACCOUNT QUEUES BEANS
    @Bean
    public Queue secRepAcNewQueue() {
        return new Queue(secRepAcNewQueue, false);
    }

    @Bean
    public Queue secRepAcTitleUpdQueue() {
        return new Queue(secRepAcTitleUpdQueue, false);
    }

    @Bean
    public Queue secRepAcDelQueue() {
        return new Queue(secRepAcDelQueue, false);
    }

    //TRANSACTION QUEUES BEANS
    @Bean
    public Queue secRepTrNewQueue() {
        return new Queue(secRepTrNewQueue, false);
    }

    @Bean
    public Queue secRepTrTitleUpdQueue() {
        return new Queue(secRepTrTitleUpdQueue, false);
    }

    @Bean
    public Queue secRepTrAmntUpdQueue() {
        return new Queue(secRepTrAmntUpdQueue, false);
    }

    @Bean
    public Queue secRepTrDelQueue() {
        return new Queue(secRepTrDelQueue, false);
    }

    // ACCOUNT BINDINGS
    @Bean
    public Binding secRepAcNewBinding() {
        return BindingBuilder.bind(secRepAcNewQueue()).to(accExchange()).with(rkNew);
    }

    @Bean
    public Binding secRepAcTitleUpdBinding() {
        return BindingBuilder.bind(secRepAcTitleUpdQueue()).to(accExchange()).with(rkTitleUpd);
    }

    @Bean
    public Binding secRepAcDelBinding() {
        return BindingBuilder.bind(secRepAcDelQueue()).to(accExchange()).with(rkDel);
    }

    // TRANSACTION BINDINGS
    @Bean
    public Binding secRepTrNewBinding() {
        return BindingBuilder.bind(secRepTrNewQueue()).to(transExchange()).with(rkNew);
    }

    @Bean
    public Binding secRepTrTitleUpdBinding() {
        return BindingBuilder.bind(secRepTrTitleUpdQueue()).to(transExchange()).with(rkTitleUpd);
    }

    @Bean
    public Binding secRepTrAmntUpdBinding() {
        return BindingBuilder.bind(secRepTrAmntUpdQueue()).to(transExchange()).with(rkAmntUpd);
    }

    @Bean
    public Binding secRepTrDelBinding() {
        return BindingBuilder.bind(secRepTrDelQueue()).to(transExchange()).with(rkDel);
    }

    // OTHER BEANS
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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyTimeout(60000);
        template.setMessageConverter(messageConverter);

        // Настройка конвертера сообщений (если используете DTO)
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
