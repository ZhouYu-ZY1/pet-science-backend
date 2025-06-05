package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BusinessException;
import com.pet_science.mapper.AdminMapper;
import com.pet_science.pojo.user.Admin;
import com.pet_science.service.AdminService;
import com.pet_science.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 密码加密
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * 管理员登录
     * @param account 账号（用户名/邮箱）
     * @param password 密码
     * @return JSONObject
     */
    @Override
    public JSONObject login(String account, String password) {
        Admin admin;
        String loginType;
        
        // 自动判断登录类型
        if (EMAIL_PATTERN.matcher(account).matches()) {
            // 邮箱登录
            admin = findByEmail(account);
            loginType = "email";
        } else {
            // 用户名登录
            admin = findByUsername(account);
            loginType = "username";
        }
        
        // 验证管理员是否存在
        if (admin == null) {
            throw new BusinessException("账号或密码错误");
        }
        
        // 验证管理员状态
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系超级管理员");
        }
        
        // 验证密码是否正确
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        
        // 更新最后登录时间
        admin.setLastLoginTime(new Date());
        adminMapper.updateLastLoginTime(admin.getAdminId(), admin.getLastLoginTime());
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", JWTUtil.createToken(admin.getAdminId(),true));
        jsonObject.put("loginType", loginType);
        jsonObject.put("adminId", admin.getAdminId());
        jsonObject.put("username", admin.getUsername());
        jsonObject.put("realName", admin.getRealName());
        jsonObject.put("avatarUrl", admin.getAvatarUrl());
        
        return jsonObject;
    }
    
    @Override
    public Admin findByUsername(String username) {
        try {
            return adminMapper.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Admin findByEmail(String email) {
        try {
            return adminMapper.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
}