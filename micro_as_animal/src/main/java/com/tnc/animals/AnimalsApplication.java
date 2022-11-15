package com.tnc.animals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;

@SpringBootApplication
@EnableFeignClients
public class AnimalsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AnimalsApplication.class, args);
    }
}
