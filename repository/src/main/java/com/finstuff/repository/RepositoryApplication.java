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
	@Autowired
	private Queue repSecTrNewQueue;
	@Autowired
	private Queue repSecTrTitleUpdQueue;
	@Autowired
	private Queue repSecTrAmntUpdQueue;
	@Autowired
	private Queue repSecTrDelQueue;


	@PostConstruct
	public void declareRepSecQueue(){
		rabbitAdmin.declareQueue(repSecAcNewQueue);
		rabbitAdmin.declareQueue(repSecAcTitleUpdQueue);
		rabbitAdmin.declareQueue(repSecAcDelQueue);
		rabbitAdmin.declareQueue(repSecTrNewQueue);
		rabbitAdmin.declareQueue(repSecTrTitleUpdQueue);
		rabbitAdmin.declareQueue(repSecTrAmntUpdQueue);
		rabbitAdmin.declareQueue(repSecTrDelQueue);
	}
	public static void main(String[] args) {
		SpringApplication.run(RepositoryApplication.class, args);
	}

}
