package edu.ace.infinite.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

}
