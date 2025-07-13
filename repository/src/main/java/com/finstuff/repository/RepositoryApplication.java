package com.finstuff.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RepositoryApplication {

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private Queue repSecQueue;

//	@PostConstruct
//	public void declareSecRepQueue(){
//		rabbitAdmin.declareQueue(secRepQueue);
//	}
	@PostConstruct
	public void declareRepSecQueue(){
		rabbitAdmin.declareQueue(repSecQueue);
	}
	public static void main(String[] args) {
		SpringApplication.run(RepositoryApplication.class, args);
	}

}
