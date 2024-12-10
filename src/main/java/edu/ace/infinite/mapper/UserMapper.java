package edu.ace.infinite.mapper;

import edu.ace.infinite.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

// ... existing code ...

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE uname = #{username} AND upass = #{password}")
    User login(String username, String password);

    // 新增用户的插入方法
    @Insert("INSERT INTO user (uname, upass, avatar, nickname,create_time,update_time) " +
            "VALUES (#{uname}, #{upass}, #{avatar}, #{nickname},NOW(),NOW())")
    @Options(useGeneratedKeys = true)
    Integer insertUser(User user);

    @Select("SELECT * FROM user WHERE uname = #{uname}")
    User getUserByUsername(String uname);
}