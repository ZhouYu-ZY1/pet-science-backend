package edu.ace.infinite.controller;


import com.alibaba.fastjson.JSON;
import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import com.alibaba.fastjson.JSONObject;
import okhttp3.internal.platform.ConscryptPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return JSON.toJSONString(response);
    }

    @PostMapping("/like")
    public String likeVideo(@RequestBody Like like) {
        String videoId = like.getVideo().getVideoId();
        boolean success = videoService.likeVideo(like, videoId);
        JSONObject response = new JSONObject();
        if (success) {
            response.put("code", 200);
            response.put("message", "点赞成功");
        } else {
            response.put("code", 500);
            response.put("message", "点赞失败");
        }
        return JSON.toJSONString(response);
    }
    @PostMapping("/dislike")
    public String dislikeVideo(@RequestBody Like like) {
        String videoId = like.getVideo().getVideoId();
        boolean success = videoService.dislikeVideo(like, videoId);

        JSONObject response = new JSONObject();
        if (success) {
            response.put("code", 200);
            response.put("message", "取消成功");
        } else {
            response.put("code", 500);
            response.put("message", "取消失败");
        }
        return JSON.toJSONString(response);
    }
    @PostMapping("/getLikeList")
    public String getLikeVideoList(@RequestBody Map<String, String> requestData) {
        String userId = requestData.get("userId");
        JSONObject response = new JSONObject();
        try {
            List<Like> likeList = videoService.getLikeList(userId);
            if (likeList != null && !likeList.isEmpty()) {
                response.put("code", 200);
                response.put("message", "获取喜欢列表成功");
                response.put("data", likeList);
            } else {
                response.put("code", 404); // 数据为空时，使用更合理的状态码
                response.put("message", "获取喜欢列表失败，数据为空");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取喜欢列表失败，发生异常");
        }
        return JSON.toJSONString(response);
    }



}
