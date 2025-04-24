package com.pet_science;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class} // 关闭Spring Security的自动配置
)
@EnableCaching // 启用缓存
public class PetScienceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetScienceApplication.class, args);
    }
}
