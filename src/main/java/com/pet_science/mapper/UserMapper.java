package com.pet_science.mapper;

import com.pet_science.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findById(Integer userId);
    
    /**
     * 获取用户列表
     * @param params 查询参数
     * @return 用户列表
     */
    @SelectProvider(type = UserSqlProvider.class, method = "getUserList")
    List<User> getUserList(Map<String, Object> params);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态值
     * @return 影响行数
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") Integer userId, @Param("status") Integer status);
    
    /**
     * SQL提供者，用于动态SQL
     */
    class UserSqlProvider {
        public String getUserList(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM users WHERE 1=1 ");
            
            if (params.containsKey("username") && params.get("username") != null) {
                sql.append(" AND username LIKE CONCAT('%', #{username}, '%')");
            }
            
            if (params.containsKey("email") && params.get("email") != null) {
                sql.append(" AND email LIKE CONCAT('%', #{email}, '%')");
            }
            
            if (params.containsKey("mobile") && params.get("mobile") != null) {
                sql.append(" AND mobile LIKE CONCAT('%', #{mobile}, '%')");
            }
            
            if (params.containsKey("status") && params.get("status") != null) {
                sql.append(" AND status = #{status}");
            }
            
            sql.append(" ORDER BY created_at DESC");
            
            return sql.toString();
        }
    }
}