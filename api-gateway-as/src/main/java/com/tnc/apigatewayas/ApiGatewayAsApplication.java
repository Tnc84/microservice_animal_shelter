package com.tnc.apigatewayas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tnc.apigatewayas", "com.tnc.security"})
public class ApiGatewayAsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayAsApplication.class, args);
    }

}
