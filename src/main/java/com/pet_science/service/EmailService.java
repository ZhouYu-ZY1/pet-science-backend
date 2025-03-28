package com.pet_science.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Cache<String, String> verificationCodeCache;

    public String sendVerificationCode(String to) {
        // 生成6位随机验证码
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            // 设置发件人昵称和邮箱
            helper.setFrom("2179853437@qq.com", "萌宠视界");
            // 收件人
            helper.setTo(to);
            // 邮件主题
            helper.setSubject("Pet Science - 验证码");
            // 邮件内容使用HTML格式
            String htmlContent = String.format("""
                    <div style="background-color: #f7f7f7; padding: 20px;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                            <h2 style="color: #333; text-align: center; margin-bottom: 30px;">萌宠视界验证码</h2>
                            <div style="font-size: 16px; color: #666; margin-bottom: 20px;">
                                亲爱的用户：<br/>
                                您好！您正在进行邮箱验证，验证码为：
                            </div>
                            <div style="background-color: #f5f5f5; padding: 15px; text-align: center; margin: 20px 0;">
                                <span style="color: #e74c3c; font-size: 24px; font-weight: bold; letter-spacing: 5px;">%s</span>
                            </div>
                            <div style="font-size: 14px; color: #999; margin-top: 20px;">
                                <p>验证码有效期为5分钟，请尽快完成验证。</p>
                                <p>如非本人操作，请忽略此邮件。</p>
                            </div>
                            <div style="border-top: 1px solid #eee; margin-top: 30px; padding-top: 20px; text-align: center; color: #999; font-size: 12px;">
                                © 2025 萌宠视界. All rights reserved.
                            </div>
                        </div>
                    </div>
                    """, verificationCode);
            helper.setText(htmlContent, true); // 第二个参数true表示支持HTML格式

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);

        // 存储验证码
        verificationCodeCache.put("email:code:" + to, verificationCode);
        return verificationCode;
    }

    public String getVerificationCode(String email) {
        return verificationCodeCache.getIfPresent("email:code:" + email);
    }

    public void removeVerificationCode(String email) {
        verificationCodeCache.invalidate("email:code:" + email);
    }
}