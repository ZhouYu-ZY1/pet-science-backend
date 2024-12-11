package edu.ace.infinite.controller;


import com.alibaba.fastjson.JSON;
import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import com.alibaba.fastjson.JSONObject;
import edu.ace.infinite.utils.JWTUtil;
import okhttp3.internal.platform.ConscryptPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/getRecommendList")
    public String getRecommendVideoList() {
        List<Video> videos = videoService.getRecommendVideoList();
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("message", "成功");
        response.put("data", videos);
        return response.toJSONString();
    }

    @PostMapping("/like")
    public String likeVideo(@RequestBody Like like,HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer userId = JWTUtil.getUserId(token);
        like.setUser_id(String.valueOf(userId));

        boolean success = videoService.likeVideo(like);
        JSONObject response = new JSONObject();
        if (success) {
            response.put("code", 200);
            response.put("message", "点赞成功");
        } else {
            response.put("code", 500);
            response.put("message", "点赞失败");
        }
        return response.toJSONString();
    }
    @PostMapping("/dislike")
    public String dislikeVideo(@RequestBody Like like, HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer userId = JWTUtil.getUserId(token);
        like.setUser_id(String.valueOf(userId));

        boolean success = videoService.dislikeVideo(like);
        JSONObject response = new JSONObject();
        if (success) {
            response.put("code", 200);
            response.put("message", "取消成功");
        } else {
            response.put("code", 500);
            response.put("message", "取消失败");
        }
        return response.toJSONString();
    }
    @RequestMapping("/getLikeList")
    public String getLikeVideoList(HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer userId = JWTUtil.getUserId(token);

        JSONObject response = new JSONObject();
        try {
            List<Video> likeList = videoService.getLikeList(String.valueOf(userId));
            response.put("code", 200);
            response.put("message", "获取喜欢列表成功");
            response.put("data", likeList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取喜欢列表失败，发生异常");
        }
        return response.toJSONString();
    }



}
