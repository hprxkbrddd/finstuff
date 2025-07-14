package com.finstuff.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RepositoryApplication {

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private Queue repSecAcNewQueue;
	@Autowired
	private Queue repSecAcTitleUpdQueue;
	@Autowired
	private Queue repSecAcDelQueue;


	@PostConstruct
	public void declareRepSecQueue(){
		rabbitAdmin.declareQueue(repSecAcDelQueue);
		rabbitAdmin.declareQueue(repSecAcTitleUpdQueue);
		rabbitAdmin.declareQueue(repSecAcDelQueue);
	}
	public static void main(String[] args) {
		SpringApplication.run(RepositoryApplication.class, args);
	}

}
