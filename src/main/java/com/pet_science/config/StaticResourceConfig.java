package com.pet_science.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /static/** 映射到 classpath:/static/
        registry.addResourceHandler("/statics/**")
                .addResourceLocations("classpath:/statics/");
    }
}

