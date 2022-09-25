package com.daoutech.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DaoutechApiMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaoutechApiMasterApplication.class, args);
	}

}
