package com.finstuff.security2;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Security2Application {

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private Queue secRepAcNewQueue;
	@Autowired
	private Queue secRepAcTitleUpdQueue;
	@Autowired
	private Queue secRepAcDelQueue;

	@PostConstruct
	public void declareQueue(){
		rabbitAdmin.declareQueue(secRepAcNewQueue);
        rabbitAdmin.declareQueue(secRepAcTitleUpdQueue);
        rabbitAdmin.declareQueue(secRepAcDelQueue);
	}

	public static void main(String[] args) {
		SpringApplication.run(Security2Application.class, args);
	}

}
