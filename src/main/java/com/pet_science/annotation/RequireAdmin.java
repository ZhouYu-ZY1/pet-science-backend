package com.pet_science.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要管理员权限的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // 注解作用在方法或者类上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时存在
public @interface RequireAdmin {
    boolean value() default true;
}