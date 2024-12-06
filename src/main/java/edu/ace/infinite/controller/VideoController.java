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
        response.put("message", "Success");
        response.put("data", videos);

        return JSON.toJSONString(response);
    }
    @PostMapping("/like")
    public String likeVideo(@RequestBody Like like) {
        System.err.println(like);

        String userId = like.getUser_id();

        boolean success = videoService.likeVideo(userId, like);
        JSONObject response = new JSONObject();
        if (success) {
            response.put("code", 200);
            response.put("message", "Successfully liked the video");
        } else {
            response.put("code", 500);
            response.put("message", "Failed to like the video");
        }

        return JSON.toJSONString(response);
    }
}
