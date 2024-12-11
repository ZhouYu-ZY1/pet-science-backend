package edu.ace.infinite.service;

import edu.ace.infinite.pojo.FollowVO;
import edu.ace.infinite.pojo.PageResult;
import edu.ace.infinite.pojo.User;

public interface UserService {
    String login(User user);

    Boolean registerUser(User user);


    PageResult<User> searchUsers(Integer current, Integer size, String keyword, Integer userId);

    User getUserInfo(Integer userId);

    Boolean  followUser(FollowVO follow,boolean isFollow);

    PageResult<User> getFollowList(Integer current, Integer size, Integer userId);


    Integer updateUserAvatar(User user);
}
