package edu.ace.infinite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "edu.ace") // 确保包含 UserService 所在的包
public class InfiniteApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfiniteApplication.class, args);
    }
}
