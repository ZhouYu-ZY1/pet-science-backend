package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.content.Content;
import com.pet_science.pojo.content.ContentLike;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ContentService {
    List<Content> getRecommendVideoList();
    boolean likeVideo(ContentLike contentLike);

    boolean dislikeVideo(ContentLike contentLike);

    List<Content> getLikeList(String uid);

    Integer updateUserContent(Content content, Integer userId);

    List<Content> getUserVideoList(String uid);
    
    // 获取内容列表（分页）
    Map<String, Object> getContentList(Map<String, Object> params);
    
    // 更新内容状态
    boolean updateContentStatus(String videoId, Integer status);
}
