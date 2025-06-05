package com.pet_science.mapper;

import com.pet_science.pojo.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 插入新用户
     * @param user 用户信息
     */
    @Insert("INSERT INTO users (username, password, email, mobile, avatar_url, nickname, gender, birthday, location, bio, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{email}, #{mobile}, #{avatarUrl}, #{nickname}, #{gender}, #{birthday}, #{location}, #{bio}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    Integer insert(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     */
    @Update("<script>" +
            "UPDATE users " +
            "<set>" +
            "   <if test='username != null and username != \"\"'>username = #{username},</if>" +
            "   <if test='password != null and password != \"\"'>password = #{password},</if>" +
            "   <if test='email != null and email != \"\"'>email = #{email},</if>" +
            "   <if test='mobile != null and mobile != \"\"'>mobile = #{mobile},</if>" +
            "   <if test='avatarUrl != null and avatarUrl != \"\"'>avatar_url = #{avatarUrl},</if>" +
            "   <if test='nickname != null and nickname != \"\"'>nickname = #{nickname},</if>" +
            "   <if test='gender != null'>gender = #{gender},</if>" +
            "   <if test='birthday != null'>birthday = #{birthday},</if>" +
            "   <if test='location != null and location != \"\"'>location = #{location},</if>" +
            "   <if test='bio != null and bio != \"\"'>bio = #{bio},</if>" +
            "   updated_at = #{updatedAt}" +
            "</set>" +
            "WHERE user_id = #{userId}" +
            "</script>")
    Integer update(User user);

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
     * 是否关注某人
     */
    @Select("SELECT COUNT(*) " +
            "FROM follows " +
            "WHERE from_user_id = #{from} AND to_user_id = #{to}")
    boolean isFollowing(@Param("from") Integer from,@Param("to") Integer to);

    /**
     * 关注
     */
    @Insert("INSERT INTO follows (from_user_id, to_user_id,follow_time) VALUES (#{fromId}, #{toId},NOW())")
    Integer followUser(@Param("fromId") Integer fromId,@Param("toId") Integer toId);

    /**
     * 取关
     */
    @Delete("DELETE FROM follows WHERE from_user_id = #{fromId} AND to_user_id = #{toId}")
    Integer unFollowUser(@Param("fromId") Integer fromId,@Param("toId") Integer toId);

    /**
     * 获取关注列表
     */
    @Select("SELECT u.*,f.follow_time " +
            "FROM users u " +
            "JOIN follows f ON u.user_id = f.to_user_id " +
            "WHERE f.from_user_id = #{userId} " +
            "ORDER BY f.follow_time DESC")
    List<User> getFollowList(@Param("userId") Integer userId);

    /**
     * 获取粉丝列表
     */
    @Select("SELECT u.*,f.follow_time " +
            "FROM users u " +
            "JOIN follows f ON u.user_id = f.from_user_id " +
            "WHERE f.to_user_id = #{userId} " +
            "ORDER BY f.follow_time DESC")
    List<User> getFansList(@Param("userId") Integer userId);

    /**
     * 获取粉丝数
     */
    @Select("SELECT COUNT(*) " +
            "FROM follows " +
            "WHERE to_user_id = #{userId}")
    Integer getFansSize(@Param("userId") Integer userId);

    /**
     * 获取关注数
     */
    @Select("SELECT COUNT(*) " +
            "FROM follows " +
            "WHERE from_user_id = #{userId}")
    Integer getFollowSize(@Param("userId") Integer userId);

    /**
     * 获取互关数量
     */
    @Select("SELECT COUNT(*) " +
            "FROM follows f1 " +
            "WHERE EXISTS ( " +
            "   SELECT 1 FROM follows f2 " +
            "   WHERE f2.from_user_id = f1.to_user_id AND f2.to_user_id = f1.from_user_id " +
            ") AND f1.from_user_id = #{userId}")
    Integer getMutualFollowSize(@Param("userId") Integer userId);


    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findUserById(Integer userId);

    /**
     * SQL提供者，用于动态SQL
     */
    class UserSqlProvider {
        public String getUserList(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT user_id, username, email, mobile, avatar_url, nickname, " +
                    "gender, birthday, location, bio, created_at, updated_at, status " +
                    "FROM users WHERE 1=1 ");

            if(params.containsKey("keyword") && params.get("keyword") != null) {
                sql.append(" AND (username LIKE CONCAT('%', #{keyword}, '%') " +
//                        "OR email LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR nickname LIKE CONCAT('%', #{keyword}, '%') " +
//                        "OR mobile LIKE CONCAT('%', #{keyword}, '%')" +
                        ")");

            }else {
                if (params.containsKey("username") && params.get("username") != null) {
                    sql.append(" AND username LIKE CONCAT('%', #{username}, '%')");
                }

                if (params.containsKey("nickname") && params.get("nickname") != null) {
                    sql.append(" AND nickname LIKE CONCAT('%', #{nickname}, '%')");
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
            }
            sql.append(" ORDER BY created_at DESC");
            
            return sql.toString();
        }
    }
}