package com.tnc.apigatewayas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ApiGatewayAsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayAsApplication.class, args);
    }

}
