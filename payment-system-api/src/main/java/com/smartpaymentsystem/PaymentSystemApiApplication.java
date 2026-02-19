package com.smartpaymentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class PaymentSystemApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentSystemApiApplication.class, args);
	}

}
