package com.pet_science.mapper;

import com.pet_science.pojo.Admin;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface AdminMapper {
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);

    /**
     * 根据邮箱查询管理员
     * @param email 邮箱
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin WHERE email = #{email}")
    Admin findByEmail(String email);
    
    /**
     * 更新管理员最后登录时间
     * @param adminId 管理员ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    @Update("UPDATE admin SET last_login_time = #{lastLoginTime} WHERE admin_id = #{adminId}")
    int updateLastLoginTime(@Param("adminId") Integer adminId, @Param("lastLoginTime") Date lastLoginTime);
}