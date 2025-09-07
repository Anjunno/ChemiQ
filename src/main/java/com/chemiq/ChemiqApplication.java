package com.chemiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class ChemiqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChemiqApplication.class, args);
	}

}
