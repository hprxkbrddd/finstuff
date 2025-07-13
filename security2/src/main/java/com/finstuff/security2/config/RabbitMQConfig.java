package com.finstuff.security2.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.queue.sec-rep}")
    private String secRepQueue;
    @Value("${rabbitmq.routing-key.account.new}")
    private String rkNew;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue secRepQueue() {
        return new Queue(secRepQueue, false);
    }

//    @Bean
//    public Queue replyQueue() {
//        return new AnonymousQueue(); // Автоматически создаваемая временная очередь для ответов
//    }

    @Bean
    public Binding secRepBindingNew(TopicExchange exchange) {
        return BindingBuilder.bind(secRepQueue()).to(exchange).with(rkNew);
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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange replyExchange() {
        return new TopicExchange("rpc.exchange");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setReplyAddress(replyQueue().getName());
        template.setReplyTimeout(60000);
        template.setMessageConverter(messageConverter);
//        template.setUseDirectReplyToContainer(false); // Отключаем встроенный механизм, если используем свою очередь

        // Настройка конвертера сообщений (если используете DTO)
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
//
//    @Bean
//    public SimpleMessageListenerContainer replyListenerContainer(RabbitTemplate rabbitTemplate) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
//        container.setQueues(replyQueue());
//        container.setMessageListener(rabbitTemplate);
//        return container;
//    }
}
