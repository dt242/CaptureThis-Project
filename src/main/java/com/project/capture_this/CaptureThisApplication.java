package com.project.capture_this;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaptureThisApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaptureThisApplication.class, args);
	}

}
