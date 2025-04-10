package com.pet_science.interceptor;

import com.pet_science.annotation.RequireAdmin;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BaseException;
import com.pet_science.utils.JWTUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)  {
        // 如果不是映射到方法，直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        // 获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 检查是否需要管理员权限
        if (method.isAnnotationPresent(RequireAdmin.class) || handlerMethod.getBeanType().isAnnotationPresent(RequireAdmin.class)) {
            if (token == null) {
                throw new BaseException(401, "未登录或token已过期");
            }

            // 验证token并检查是否为管理员
            if (!JWTUtil.verifyAdmin(token)) {
                throw new BaseException(403, "无管理员权限");
            }
        }

        // 检查是否需要用户权限
        if (method.isAnnotationPresent(RequireUser.class) || handlerMethod.getBeanType().isAnnotationPresent(RequireUser.class)) {
            if (token == null) {
                throw new BaseException(401, "未登录或token已过期");
            }
            
            // 验证token
            if (!JWTUtil.verifyUser(token)) {
                throw new BaseException(401, "未登录或token已过期");
            }
        }

        return true;
    }
}