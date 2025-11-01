package com.example.springmodels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.springmodels.models")
@EnableJpaRepositories(basePackages = "com.example.springmodels.repos")
public class SpringModelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringModelsApplication.class, args);
	}

}
