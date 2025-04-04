package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.pojo.Admin;

public interface AdminService {
    
    /**
     * 管理员登录
     * @param account 账号（用户名/邮箱）
     * @param password 密码
     * @return JSONObject 包含token和登录类型
     */
    JSONObject login(String account, String password);
    
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin findByUsername(String username);
    
    /**
     * 根据邮箱查询管理员
     * @param email 邮箱
     * @return 管理员信息
     */
    Admin findByEmail(String email);
}