package com.pet_science.service.impl;

import com.pet_science.exception.BusinessException;
import com.pet_science.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱服务
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis

    // Redis键前缀
    private static final String EMAIL_CODE_PREFIX = "email:code:"; // 邮箱验证码前缀
    private static final String EMAIL_LIMIT_PREFIX = "email:limit:"; // 邮箱发送限制前缀
    
    // 验证码有效期（分钟）
    private static final long CODE_EXPIRE_MINUTES = 5;
    // 发送限制时间（秒）
    private static final long SEND_LIMIT_SECONDS = 60;

    /**
     * 发送验证码
     */
    public Boolean sendVerificationCode(String to) {
        // 检查是否在限制时间内
        String limitKey = EMAIL_LIMIT_PREFIX + to;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            // 如果在限制时间内，获取剩余时间并抛出异常
            Long ttl = redisTemplate.getExpire(limitKey, TimeUnit.SECONDS);
            throw new BusinessException("操作过于频繁，请" + ttl + "秒后再试");
        }

        // 异步发送邮件
        sendEmailAsync(to);

        // 设置60秒内不能重复发送
        redisTemplate.opsForValue().set(limitKey, 1, SEND_LIMIT_SECONDS, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 异步发送邮件
     * @param to 收件人邮箱
     */
    @Async("emailTaskExecutor")
    public void sendEmailAsync(String to) {
        String verificationCode = EmailUtils.sendLoginCode(to, mailSender);

        System.err.println("验证码已发送：" + verificationCode);

        // 存储验证码到Redis，并设置过期时间
        String codeKey = EMAIL_CODE_PREFIX + to;
        redisTemplate.opsForValue().set(codeKey, verificationCode, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        Object code = redisTemplate.opsForValue().get(key);
        return code != null ? code.toString() : null;
    }

    public void removeVerificationCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }
}