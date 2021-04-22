package com.emr.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TracingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TracingSystemApplication.class, args);
	}

}
