package com.pet_science.controller;


import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BusinessException;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.content.ContentLike;
import com.pet_science.pojo.content.Content;
import com.pet_science.service.ContentService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content")
@Api(tags = "内容接口")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/getRecommendList")
    public Result<List<Content>> getRecommendVideoList() {
        List<Content> contents = contentService.getRecommendVideoList();
        return Result.successResultData(contents);
    }

    @PostMapping("/like")
    public String likeVideo(@RequestBody ContentLike contentLike, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        contentLike.setUser_id(String.valueOf(userId));

        boolean success = contentService.likeVideo(contentLike);
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
    public String dislikeVideo(@RequestBody ContentLike contentLike, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        contentLike.setUser_id(String.valueOf(userId));

        boolean success = contentService.dislikeVideo(contentLike);
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
    @GetMapping("/getLikeList")
    @ApiOperation(value = "获取用户点赞的视频列表", notes = "根据用户ID获取用户点赞的视频列表")
    @RequireUser
    public Result<List<Content>> getLikeVideoList(@RequestParam("userId") Integer userId,@RequestHeader("Authorization") String token) {
        if(userId == null || userId == 0){
            // 从token中获取用户ID
            userId = JWTUtil.getUserId(token);
        }
        List<Content> likeList = contentService.getLikeList(String.valueOf(userId));
        return Result.successResultData(likeList);
    }


    @GetMapping("/getUserVideoList")
    @ApiOperation(value = "获取用户上传的视频列表", notes = "根据用户ID获取用户上传的视频列表")
    @RequireUser
    public Result<List<Content>> getUserVideoList(@RequestParam("userId") Integer userId,@RequestHeader("Authorization") String token) {
        if(userId == null || userId == 0){
            // 从token中获取用户ID
            userId = JWTUtil.getUserId(token);
        }
        List<Content> userVideoList = contentService.getUserVideoList(String.valueOf(userId));
        return Result.successResultData(userVideoList);
    }

    /**
     * 获取内容列表（分页）
     * @param params 查询参数
     * @return 内容列表和总数
     */
    @GetMapping("/getList")
    @ApiOperation(value = "获取内容列表", notes = "分页获取内容列表，支持按描述、用户ID和状态筛选")
    public Result<Map<String, Object>> getContentList(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = contentService.getContentList(params);
        return Result.successResultData(result);
    }
    
    /**
     * 更新内容状态
     * @param params 参数，包含videoId和status
     * @return 更新结果
     */
    @PostMapping("/updateStatus")
    @ApiOperation(value = "更新内容状态", notes = "更新内容状态：-1(下架)、0(未审核)、1(已审核)")
    public Result<Boolean> updateContentStatus(@RequestBody Map<String, Object> params) {
        String videoId = (String) params.get("videoId");
        Integer status = Integer.parseInt(params.get("status").toString());
        
        if (videoId == null || status == null) {
            return Result.successResultData(false);
        }
        
        boolean success = contentService.updateContentStatus(videoId, status);
        if (success) {
            return Result.successResultData(true);
        } else {
            throw new BusinessException("更新内容状态失败");
        }
    }
}
