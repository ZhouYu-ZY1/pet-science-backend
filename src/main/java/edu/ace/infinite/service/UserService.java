package edu.ace.infinite.service;

import edu.ace.infinite.pojo.FollowVO;
import edu.ace.infinite.pojo.User;

public interface UserService {
    String login(User user);

    Boolean registerUser(User user);

    User getUserInfo(User user);

    Boolean  followUser(FollowVO follow);
}
