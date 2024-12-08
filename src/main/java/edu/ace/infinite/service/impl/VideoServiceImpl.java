package edu.ace.infinite.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import edu.ace.infinite.mapper.VideoMapper;
import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import edu.ace.infinite.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Constraint;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    private List<Video> videoList = new ArrayList<>();
    @Override
    public List<Video> getRecommendVideoList() {
        if(videoList.size() < 50){
            if(videoList.size() < 5){
                List<Video> videos = HttpUtils.recommendVideo(null);
                videoList.addAll(videos);
            }
            new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) { //批量获取10次
                        List<Video> recommendVideo = HttpUtils.recommendVideo(null);
                        videoList.addAll(recommendVideo);
                        Thread.sleep(3000);  //延迟300毫秒再获取
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }


        // 取出前10条
        List<Video> result = new ArrayList<>(videoList.subList(0, Math.min(10,videoList.size())));
        // 移除这10条数据
        videoList.removeAll(result);
        System.err.println("视频列表剩余"+videoList.size());

        // 调用HttpUtils类的recommendVideo方法获取视频列表
        return result;
    }

    @Transactional
    @Override
    public boolean likeVideo(Like like,String videoId) {
        try {
            String userId = like.getUser_id();
            if (like.getVideo().getType() == 0) {  //0为抖音视频
                //验证视频在不在数据库中
                if (videoMapper.findTypeById(videoId) <= 0) {
                    //如果不存在则插入数据
                    if(videoMapper.insertVideo(like.getVideo()) <= 0){
                        throw new RuntimeException("抖音视频插入失败");
                    }
                }
            } else if (like.getVideo().getType() == 1) { //1为用户上传视频
                if (videoMapper.findTypeById(videoId) <= 0) {
                    //如果不存在则说明数据异常
                    throw new RuntimeException("视频数据异常");
                }
            }else {
                throw new RuntimeException("视频类型错误");
            }

            int result = videoMapper.insertLike(userId, videoId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dislikeVideo(Like like, String videoId) {
        try {
            String userId = like.getUser_id();
            int result = videoMapper.deleteLike(userId, videoId);
            if (result > 0) {
                return true;
            } else {
                throw new RuntimeException("取消点赞失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Like> getLikeList(String userId) {
        try {
            List<Like> likeList = videoMapper.selectLikeList(userId);
            return likeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
