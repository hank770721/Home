package com.hkma.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HomeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HomeApplication.class, args);
	}
}
