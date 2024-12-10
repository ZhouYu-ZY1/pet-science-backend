package edu.ace.infinite.service.impl;

import edu.ace.infinite.mapper.UserMapper;
import edu.ace.infinite.pojo.FollowVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    public void testFollowUser() {
        // 模拟请求参数
        FollowVO follow = new FollowVO();
        follow.setFromUserId(1);
        follow.setToUserId(2);

        // 模拟UserMapper的行为
        when(userMapper.followUser(any(Integer.class), any(Integer.class))).thenReturn(1);

        // 调用方法
        Boolean result = userService.followUser(follow);

        // 验证结果
        assertEquals("关注成功", result);
    }
}