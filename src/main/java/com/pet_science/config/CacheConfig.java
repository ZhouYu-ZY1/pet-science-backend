package com.pet_science.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * 用于验证码的缓存配置
 */
@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, String> verificationCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 5分钟过期
                .build(); // 构建缓存对象
    }
}