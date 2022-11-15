package com.tnc.namingserveras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class NamingServerAsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamingServerAsApplication.class, args);
	}

}
