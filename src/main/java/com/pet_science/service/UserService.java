package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.pojo.FollowVO;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.User;

public interface UserService {
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    String register(User user);

    /**
     * 用户登录
     * @param account 账号
     * @param password 密码
     * @return JWT token
     */
    JSONObject login(String account, String password);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);

    /**
     * 根据手机号查询用户
     * @param mobile 手机号
     * @return 用户信息
     */
    User findByMobile(String mobile);

    /**
     * 验证码登录
     * @param email 邮箱
     * @param code 验证码
     * @return JWT token
     */
    JSONObject loginByCode(String email, String code);
}
