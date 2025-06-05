package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.mapper.ContentMapper;
import com.pet_science.mapper.UserMapper;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.content.Content;
import com.pet_science.pojo.content.ContentLike;
import com.pet_science.pojo.user.User;
import com.pet_science.service.ContentService;
import com.pet_science.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private UserMapper userMapper;
    private List<Content> contentList = new ArrayList<>();
    @Override
    public List<Content> getRecommendVideoList() {
        if(contentList.size() < 50){
            if(contentList.size() < 5){
                List<Content> contents = HttpUtils.recommendVideo(null);
                contentList.addAll(contents);
            }
            new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) { //批量获取10次
                        List<Content> recommendContent = HttpUtils.recommendVideo(null);
                        contentList.addAll(recommendContent);
                        Thread.sleep(3000);  //延迟300毫秒再获取
                    }
                }catch (Exception e){}
            }).start();
        }


        // 取出前10条
        List<Content> result = new ArrayList<>(contentList.subList(0, Math.min(10, contentList.size())));
        // 移除这10条数据
        contentList.removeAll(result);
//        System.err.println("视频列表剩余"+ contentList.size());

        // 调用HttpUtils类的recommendVideo方法获取视频列表
        return result;
    }

    @Transactional
    @Override
    public boolean likeVideo(ContentLike contentLike) {
        try {
            String userId = contentLike.getUser_id();
            String videoId = contentLike.getContent().getVideoId();
            if (contentLike.getContent().getType() == 0) {  //0为抖音视频
                //验证视频在不在数据库中
                if (contentMapper.findTypeById(videoId) <= 0) {
                    //如果不存在则插入数据
                    contentLike.getContent().setStatus(1); //设置状态为已审核
                    if(contentMapper.insertVideo(contentLike.getContent()) <= 0){
                        throw new RuntimeException("抖音视频插入失败");
                    }
                }
            } else if (contentLike.getContent().getType() == 1) { //1为用户上传视频
                if (contentMapper.findTypeById(videoId) <= 0) {
                    //如果不存在则说明数据异常
                    throw new RuntimeException("视频数据异常");
                }
            }else {
                throw new RuntimeException("视频类型错误");
            }

            int result = contentMapper.insertLike(userId, videoId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dislikeVideo(ContentLike contentLike) {
        try {
            String userId = contentLike.getUser_id();
            String videoId = contentLike.getContent().getVideoId();
            int result = contentMapper.deleteLike(userId, videoId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Content> getLikeList(String userId) {
        try {
            List<Content> likeList = contentMapper.selectLikeList(userId);
            for (Content content : likeList) {
                content.setLike(true); //喜欢列表都是点赞的视频
            }
            return likeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer updateUserContent(Content content, Integer userId) {
        User userInfo = userMapper.findUserById(userId);
        content.setVideoId(UUID.randomUUID().toString()); //生成视频唯一ID
        content.setUid(String.valueOf(userId));
        content.setType(1);
        content.setShareUrl(content.getVideoId());
        content.setNickname(userInfo.getNickname());
        content.setAuthorAvatar(userInfo.getAvatarUrl());
        content.setCommentCount(0);
        content.setDiggCount(0);
        content.setShareCount(0);
        content.setStatus(0); // 设置状态为未审核
//        video.setDesc("视频介绍介绍");
        return contentMapper.insertVideo(content);
    }

    @Override
    public List<Content> getUserVideoList(String uid) {
        try {
            List<Content> contents = contentMapper.selectUserVideoList(uid);
            for (Content content : contents) {
                boolean likeVideo = contentMapper.isLikeVideo(uid, content.getVideoId());
                content.setLike(likeVideo);
            }
            return contents;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> getContentList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取分页参数
            int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            params.put("offset", offset);
            params.put("limit", pageSize);

            // 查询内容列表
            List<Content> list = contentMapper.selectContentList(params);
            int total = contentMapper.countContentList(params);

            result.put("list", list);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public boolean updateContentStatus(String videoId, Integer status) {
        try {
            // 状态值校验：-1(下架)、0(未审核)、1(已审核)
            if (status != -1 && status != 0 && status != 1) {
                return false;
            }
            
            int result = contentMapper.updateContentStatus(videoId, status);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
