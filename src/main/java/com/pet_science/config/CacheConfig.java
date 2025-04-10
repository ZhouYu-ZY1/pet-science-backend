package com.pet_science.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

import static com.pet_science.service.impl.OrderServiceImpl.ORDER_EXPIRATION_MINUTES;

/**
 * 缓存配置
 */
@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, String> verificationCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 5分钟过期
                .build(); // 构建缓存对象
    }

     // 新增订单过期时间缓存配置
     @Bean
     public Cache<String, Long> orderExpirationCache() {
         return Caffeine.newBuilder()
                 .expireAfterWrite(ORDER_EXPIRATION_MINUTES, TimeUnit.MINUTES) // 缓存过期时间
                 .build();
     }
}