package com.chemiq;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication(exclude = {
		S3AutoConfiguration.class
})
public class ChemiqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChemiqApplication.class, args);
	}

}
