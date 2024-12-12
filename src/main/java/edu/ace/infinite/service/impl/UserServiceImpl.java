package edu.ace.infinite.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import edu.ace.infinite.pojo.PageResult;
import edu.ace.infinite.pojo.FollowVO;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.mapper.UserMapper;
import edu.ace.infinite.service.UserService;
import edu.ace.infinite.utils.JWTUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

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

    @Override
    public PageResult<User> searchUsers(Integer current, Integer size, String key,Integer userId) {
        PageHelper.startPage(current, size);
        List<User> users = userMapper.searchUsers(key);
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            //移除自己
            if (user.getId().equals(userId)) {
                iterator.remove();
            } else {
                // 遍历用户列表，为每个用户添加是否被当前用户关注的信息
                boolean isFollowed = userMapper.isFollowing(userId, user.getId());
                user.setIsFollowed(isFollowed);
            }
        }
        return PageResult.restPage((Page<User>) users);
    }

    @Override
    public User getUserInfo(Integer userId) {
        User user = userMapper.getUserInfo(userId);
        if (user != null) {
            int followCount = userMapper.getFollowList(userId).size();
            int fansCount = userMapper.getFansList(userId).size();
            user.setFollowCount(followCount);
            user.setFansCount(fansCount);
        }
        return user;
    }

    @Override
    public Boolean followUser(FollowVO follow,boolean isFollow) {
        Integer fromId = follow.getFromUserId();
        Integer toId = follow.getToUserId();
        Integer i = 0;
        if(isFollow){
            i = userMapper.followUser(fromId, toId);
        }else {
            i = userMapper.unFollowUser(fromId, toId);
        }
        return i > 0;
    }
//    @Override
//    public Boolean unFollowUser(FollowVO follow) {
//        Integer fromId = follow.getFromUserId();
//        Integer toId = follow.getToUserId();
//        Integer i = userMapper.unFollowUser(fromId, toId);
//        return i > 0;
//    }

    @Override
    public Integer updateUserAvatar(User user) {
        Integer id = user.getId();
        return userMapper.updateAvatarById(user,id);
    }

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");  //用于时间格式化
    @Override
    public PageResult<User> getFollowList(Integer current, Integer size, Integer userId) {
        PageHelper.startPage(current, size);
        List<User> users = userMapper.getFollowList(userId);
        for (User user : users) {
            user.setIsFollowed(true);
        }
        return PageResult.restPage((Page<User>) users);
    }

}