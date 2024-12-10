package edu.ace.infinite.service.impl;

import edu.ace.infinite.pojo.User;
import edu.ace.infinite.mapper.UserMapper;
import edu.ace.infinite.service.UserService;
import edu.ace.infinite.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(User user) {
        User foundUser = userMapper.login(user.getUname(), user.getUpass());
        if (foundUser != null) {
            return JWTUtil.createToken(foundUser.getId());
        }
        return null;
    }

    @Override
    public Boolean registerUser(User user) {
        //检查用户名是否已存在
        User existingUser = userMapper.getUserByUsername(user.getUname());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        //设置默认头像
        user.setAvatar("/statics/images/defaultAvatar.jpg");
        //使用账号作为默认昵称
        user.setNickname(user.getUname());
        //插入新用户
        Integer i = userMapper.insertUser(user);
        return i > 0;
    }
} 