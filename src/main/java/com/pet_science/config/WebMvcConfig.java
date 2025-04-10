package com.pet_science.config;

import com.pet_science.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /* 
     * 配置拦截器
    */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/user/login", "/user/register",
//                "/swagger-resources/**", "/webjars/**", "/v2/**", "/doc.html/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        registry.addResourceHandler("/statics/**")
                .addResourceLocations("classpath:/statics/")
                // 开启缓存控制
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)
                        .cachePublic())
                // 开启资源链，用于处理静态资源的版本控制
                .resourceChain(true);

        // 特别配置图片资源
        registry.addResourceHandler("/statics/images/**")
                .addResourceLocations("classpath:/statics/images/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)
                        .cachePublic())
                .resourceChain(true);
    }
}