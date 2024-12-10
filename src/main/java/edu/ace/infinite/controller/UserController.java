package edu.ace.infinite.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ace.infinite.pojo.FollowVO;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.service.UserService;
import edu.ace.infinite.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/loginUser")
    @ResponseBody
    public String login(@RequestBody User user) {
        String t = userService.login(user);
        JSONObject response = new JSONObject();
        if (t != null && !t.isEmpty()) {
            response.put("code", "200");
            response.put("message", "ok");
            response.put("token", t);
        } else {
            response.put("code", "500");
            response.put("message", "err");
        }
        return JSON.toJSONString(response);
    }

    @PostMapping("/registerUser")
    @ResponseBody
    public String registerUser(@RequestBody User user) {
        JSONObject response = new JSONObject();
        try {
            boolean b = userService.registerUser(user);
            if(b){
                response.put("code", "200");
                response.put("message", "注册成功");
            }else {
                response.put("code", "500");
                response.put("message", "注册失败");
            }
        } catch (RuntimeException e) {
            response.put("code", "500");
            response.put("message", e.getMessage());
            return JSON.toJSONString(response);
        }
        return JSON.toJSONString(response);
    }
    @GetMapping("/getUserInfo")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = JWTUtil.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = new User();
        user.setId(userId);
        try {
            User userInfo = userService.getUserInfo(user);
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/followUser")
    @ResponseBody
    public String followUser(HttpServletRequest request, @RequestParam Integer toUserId) {
        String token = request.getHeader("token");
        if (token == null) {
            return "未登录";
        }
        Integer fromUserId = JWTUtil.getUserId(token);
        if (fromUserId == null) {
            return "无效的用户";
        }

        FollowVO follow = new FollowVO();
        follow.setFromUserId(fromUserId);
        follow.setToUserId(toUserId);

        try {
            boolean result = userService.followUser(follow);
            if (result) {
                return "关注成功";
            } else {
                return "关注失败";
            }
        } catch (Exception e) {
            e.printStackTrace(); // 实际生产环境中应使用日志记录，如：log.error("关注用户时发生异常", e);
            return "关注失败";
        }
    }

}
