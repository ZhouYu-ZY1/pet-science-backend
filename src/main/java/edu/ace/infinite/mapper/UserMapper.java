package edu.ace.infinite.mapper;

import edu.ace.infinite.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import org.apache.ibatis.annotations.*;

import java.util.List;
import org.apache.ibatis.annotations.*;

// ... existing code ...

@Mapper
public interface UserMapper {
    //登录
    @Select("SELECT * FROM user WHERE uname = #{username} AND upass = #{password}")
    User login(String username, String password);

    // 新增用户
    @Insert("INSERT INTO user (uname, upass, avatar, nickname,create_time,update_time) " +
            "VALUES (#{uname}, #{upass}, #{avatar}, #{nickname},NOW(),NOW())")
    @Options(useGeneratedKeys = true)
    Integer insertUser(User user);

    //用户名查询用户
    @Select("SELECT * FROM user WHERE uname = #{uname}")
    User getUserByUsername(String uname);

    //获取用户信息
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserInfo(Integer id);

    //搜索用户
    @Select("SELECT * FROM user where nickname " +
            "LIKE CONCAT('%', #{key}, '%') " +
            "OR uname LIKE CONCAT('%', #{key}, '%')")
    List<User> searchUsers(@Param("key") String key);

    //是否关注某人
    @Select("SELECT COUNT(*) " +
            "FROM follow " +
            "WHERE from_user_id = #{from} AND to_user_id = #{to}")
    boolean isFollowing(Integer from, Integer to);

    //关注
    @Insert("INSERT INTO follow (from_user_id, to_user_id,follow_time) VALUES (#{fromId}, #{toId},NOW())")
    Integer followUser(Integer fromId, Integer toId);

    //取关
    @Delete("DELETE FROM follow WHERE from_user_id = #{fromId} AND to_user_id = #{toId}")
    Integer unFollowUser(Integer fromId, Integer toId);

    //获取关注列表
    @Select("SELECT u.*,f.follow_time " +
            "FROM user u " +
            "JOIN follow f ON u.id = f.to_user_id " +
            "WHERE f.from_user_id = #{userId} " +
            "ORDER BY f.follow_time DESC")
    List<User> getFollowList(@Param("userId") Integer userId);

    //获取粉丝列表
    @Select("SELECT u.*,f.follow_time " +
            "FROM user u " +
            "JOIN follow f ON u.id = f.from_user_id " +
            "WHERE f.to_user_id = #{userId} " +
            "ORDER BY f.follow_time DESC")
    List<User> getFansList(@Param("userId") Integer userId);

    @Update("UPDATE user SET avatar=#{user.avatar} WHERE id=#{id}")
    Integer updateAvatarById(User user, Integer id);
}