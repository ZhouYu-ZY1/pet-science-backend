package com.pet_science;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.pet_science") // 确保包含 UserService 所在的包
public class PetScienceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetScienceApplication.class, args);
    }
}
