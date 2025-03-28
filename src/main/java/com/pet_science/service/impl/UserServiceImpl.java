package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pet_science.mapper.UserMapper;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.FollowVO;
import com.pet_science.pojo.User;
import com.pet_science.service.UserService;
import com.pet_science.utils.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.Console;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Cache<String, String> verificationCodeCache;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");


    /**
     * 用户邮箱验证码登录
     * @param email 邮箱
     * @param code 验证码
     * @return JSONObject
     */
    @Override
    public JSONObject loginByCode(String email, String code) {
        // 从Caffeine缓存获取验证码
        String savedCode = verificationCodeCache.getIfPresent("email:code:" + email);
        if (savedCode == null) {
            throw new BusinessException("验证码已过期");
        }
        if (!savedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        
        // 验证码正确，从缓存中删除
        verificationCodeCache.invalidate("email:code:" + email);
        
        // 查找用户是否存在
        User user = findByEmail(email);
        if (user == null) {
            // 用户不存在，自动注册
            user = new User();
            user.setEmail(email);
            // 生成随机用户名
            String username = "user_" + System.currentTimeMillis();
            user.setUsername(username);
            // 随机生成初始密码
            String password = UUID.randomUUID().toString().substring(0, 8);
            user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
            register(user);
            user = findByEmail(email);
            if(user == null){ // 注册失败，抛出异常
                throw new SystemException("注册失败，请重试！");
            }
        }
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", JWTUtil.createToken(user.getUserId()));
        jsonObject.put("loginType", "email_code");
        return jsonObject;
    }

    /**
     * 用户账号密码登录
     * @param account 账号
     * @param password 密码
     * @return JSONObject
     */
    @Override
    public JSONObject login(String account, String password) {
        User user;
        String loginType;
        // 自动判断登录类型
        if (EMAIL_PATTERN.matcher(account).matches()) {
            //邮箱登录
            user = findByEmail(account);
            loginType = "email";
        } else if (MOBILE_PATTERN.matcher(account).matches()) {
            //手机号登录
            user = findByMobile(account);
            loginType = "mobile";
            if (user == null) {
                // 如果手机号登录失败，则尝试用户名登录
                user = findByUsername(account);
                loginType = "username";
            }
        } else {
            // 用户名登录
            user = findByUsername(account);
            loginType = "username";
        }


        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码是否正确
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new BusinessException("密码错误");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token",JWTUtil.createToken(user.getUserId()));
        jsonObject.put("loginType",loginType);
        // 生成JWT token
        return jsonObject;
    }


    /**
     * 用户注册
     * @param user 用户信息
     */

    @Override
    public String register(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (findByEmail(user.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }
        // 检查手机号是否已存在
        if (user.getMobile() != null && findByMobile(user.getMobile()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 对密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        // 设置创建时间和更新时间
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());


        // 保存用户信息
        Integer i = userMapper.insert(user);
        if(i > 0){
            return "注册成功";
        }else {
            throw new SystemException("注册失败");
        }
    }

    @Override
    public User findByUsername(String username) {
        try {
            return userMapper.findByUsername(username);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public User findByEmail(String email) {
        try {
            return userMapper.findByEmail(email);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public User findByMobile(String mobile) {
        try {
            return userMapper.findByMobile(mobile);
        }catch (Exception e){
            return null;
        }
    }
}