package com.pet_science.mapper;

import com.pet_science.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserMapper {
    /**
     * 插入新用户
     * @param user 用户信息
     */
    @Insert("INSERT INTO users (username, password, email, mobile, avatar_url, bio, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{email}, #{mobile}, #{avatarUrl}, #{bio}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    Integer insert(User user);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 根据手机号查询用户
     * @param mobile 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE mobile = #{mobile}")
    User findByMobile(String mobile);
}