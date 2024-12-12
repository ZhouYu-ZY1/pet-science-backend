package edu.ace.infinite.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ace.infinite.pojo.PageResult;
import edu.ace.infinite.pojo.FollowVO;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.service.UserService;
import edu.ace.infinite.utils.JWTUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Random;

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
        return response.toJSONString();
    }

    @PostMapping("/registerUser")
    @ResponseBody
    public String registerUser(@RequestBody User user) {
        JSONObject response = new JSONObject();
        try {
            boolean b = userService.registerUser(user);
            if(b){
                response.put("code", 200);
                response.put("message", "注册成功");
            }else {
                response.put("code", 500);
                response.put("message", "注册失败");
            }
        } catch (RuntimeException e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response.toJSONString();
        }
        return response.toJSONString();
    }

    @GetMapping("/searchUser")
    @ResponseBody
    public String searchUser(@RequestParam String keyword, HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer userId = JWTUtil.getUserId(token);

        JSONObject response = new JSONObject();
        try {
            PageResult<User> users = userService.searchUsers(1,20,keyword,userId);
            List<User> list = users.getList();
            response.put("code", 200);
            response.put("message", "请求成功");
            response.put("data", list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response.toJSONString();
        }
        return response.toJSONString();
    }
    @GetMapping("/getUserInfo")
    @ResponseBody
    public String  getUserInfo(HttpServletRequest request) {
        JSONObject response = new JSONObject();
        String token = request.getHeader("token");
        if (token == null) {
            response.put("code", 400);
            response.put("message", "未登录");
            return response.toJSONString();
        }
        Integer userId = JWTUtil.getUserId(token);
        if (userId == null) {
            response.put("code", 400);
            response.put("message", "无效的用户");
            return response.toJSONString();
        }
        try {
            User userInfo = userService.getUserInfo(userId);
            response.put("data", userInfo);
            response.put("code", 200);
            return response.toJSONString();
        } catch (Exception e) {
            return response.toJSONString();
        }
    }
    @PostMapping("/followUser")
    @ResponseBody
    public String followUser(HttpServletRequest request, @RequestParam Integer toUserId,@RequestParam boolean isFollow) {
        JSONObject response = new JSONObject();
        String token = request.getHeader("token");
        if (token == null) {
            response.put("code", 400);
            response.put("message", "未登录");
            return response.toJSONString();
        }
        Integer fromUserId = JWTUtil.getUserId(token);
        if (fromUserId == null) {
            response.put("code", 400);
            response.put("message", "无效的用户");
            return response.toJSONString();
        }

        String type = isFollow ? "关注":"取消关注";
        response.put("type", type);

        FollowVO follow = new FollowVO();
        follow.setFromUserId(fromUserId);
        follow.setToUserId(toUserId);
        try {
            boolean result = userService.followUser(follow,isFollow);
            if (result) {
                response.put("code", 200);
                response.put("message", "成功");
            } else {
                response.put("code", 500);
                response.put("message", "失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "失败");
            return response.toJSONString();
        }
    }

    @GetMapping("/getFollowList")
    @ResponseBody
    public String getFollowList(HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer userId = JWTUtil.getUserId(token);

        JSONObject response = new JSONObject();
        try {
            PageResult<User> users = userService.getFollowList(1,20,userId);
            List<User> list = users.getList();
            response.put("code", 200);
            response.put("message", "请求成功");
            response.put("data", list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response.toJSONString();
        }
        return response.toJSONString();
    }



    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/updateAvatar")
    public String updateAvatar(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        return uploadImageFile(file, "avatar",request);
    }

    @PostMapping("/uploadBackground")
    public String uploadBackground(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        return uploadImageFile(file, "background",request);
    }
    public static final String USER_AVATAR_PATH = "src/main/resources/statics/images/userAvatar/";
    private String uploadImageFile(MultipartFile file, String type,HttpServletRequest request) {
        JSONObject response = new JSONObject();
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "空文件");
            return response.toJSONString();
        }
        try {
            Integer userId = JWTUtil.getUserId(request.getHeader("token"));;
            String fromFileName = file.getOriginalFilename();
            int index = fromFileName.lastIndexOf(46); //ASCII码46为.
            String fileName = fromFileName.substring(0, index); //截取文件名
            String newName = new StringBuffer(fileName).append(new Random().nextInt(10000)).append(".jpg").toString();
            Path path = Paths.get(USER_AVATAR_PATH+newName);
            byte[] bytes = file.getBytes();

            Files.write(path,bytes); //使用Files.write解析相对路径

            Integer result = 0;
            if(type.equals("avatar")){
                User user = new User();
                user.setId(userId);
                user.setAvatar("/statics/images/userAvatar/" + newName);
                result = userService.updateUserAvatar(user);
            }
            if(result>0){
                response.put("code", 200);
                response.put("message", "上传成功");
            }else {
                response.put("code", 500);
                response.put("message", "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return response.toJSONString();
    }




//    @PostMapping("/updateAvatar")
//    public Integer updateAvatar(MultipartFile avatar, HttpServletRequest request) throws IOException {
//        String pathInServer = request.getServletContext().getRealPath("/statics/images");
//        if (pathInServer == null) {
//            pathInServer = request.getServletContext().getRealPath("/") + File.separator + "statics" + File.separator + "images" + File.separator;
//            new File(pathInServer).mkdirs();
//        }
//        String fromFileName = avatar.getOriginalFilename();
//        int index = fromFileName.lastIndexOf(46);
//        String sufName = fromFileName.substring(index);
//        String fileName = fromFileName.substring(0, index);
//        String newName = new StringBuffer(fileName).append(new Random().nextInt(10000)).append(sufName).toString();
//        avatar.transferTo(new File(pathInServer, newName));
//        User user = new User();
//        String token = request.getHeader("token");
//        Integer userId = JWTUtil.getUserId(token);
//        user.setId(userId);
//        user.setAvatar("/statics/images/" + newName);
//        return userService.updateUserAvatar(user);
//    }
}
