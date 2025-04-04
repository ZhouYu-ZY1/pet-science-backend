package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 用户注册
     *
     * @param user 用户信息
     */
    void register(User user);

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
     * 邮箱验证码登录
     * @param email 邮箱
     * @param code 验证码
     * @return JWT token
     */
    JSONObject loginByCode(String email, String code);

    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email);

    /**
     * 获取用户列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<User> getUserListPage(Integer pageNum, Integer pageSize, Map<String, Object> params);
    
    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserDetail(Integer userId);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态值
     * @return 是否更新成功
     */
    boolean updateUserStatus(Integer userId, Integer status);
}
