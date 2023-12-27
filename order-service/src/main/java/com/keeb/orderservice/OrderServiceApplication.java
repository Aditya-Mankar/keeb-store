package com.keeb.orderservice;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Slf4j
@EnableFeignClients
public class OrderServiceApplication {

	@Value("${service.version}")
	private String buildVersion;
	@Value("${service.message}")
	private String buildMessage;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Build version: " + buildVersion);
		log.info("Build message: " + buildMessage);
	}

}
