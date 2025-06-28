package com.bannakon.zentasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // start project
public class ZentasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZentasksApplication.class, args);
		System.out.println("Welcome to, My ZentasksApplication");
	}

}
