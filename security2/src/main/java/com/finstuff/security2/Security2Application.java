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
	@Autowired
	private Queue secRepTrNewQueue;
	@Autowired
	private Queue secRepTrTitleUpdQueue;
	@Autowired
	private Queue secRepTrAmntUpdQueue;
	@Autowired
	private Queue secRepTrDelQueue;

	@PostConstruct
	public void declareQueue(){
		rabbitAdmin.declareQueue(secRepAcNewQueue);
        rabbitAdmin.declareQueue(secRepAcTitleUpdQueue);
        rabbitAdmin.declareQueue(secRepAcDelQueue);
		rabbitAdmin.declareQueue(secRepTrNewQueue);
		rabbitAdmin.declareQueue(secRepTrTitleUpdQueue);
		rabbitAdmin.declareQueue(secRepTrAmntUpdQueue);
		rabbitAdmin.declareQueue(secRepTrDelQueue);
	}

	public static void main(String[] args) {
		SpringApplication.run(Security2Application.class, args);
	}

}
