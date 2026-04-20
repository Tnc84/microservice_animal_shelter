package com.tnc.animals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.tnc.animals", "com.tnc.security"}) 
public class AnimalsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AnimalsApplication.class, args);
    }
}
