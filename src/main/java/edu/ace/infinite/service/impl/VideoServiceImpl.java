package edu.ace.infinite.service.impl;

import edu.ace.infinite.mapper.VideoMapper;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import edu.ace.infinite.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
    public List<Video> getVideoList() {
        if(videoList.size() < 200){
            if(videoList.size() < 5){
                List<Video> videos = HttpUtils.recommendVideo(null);
                videoList.addAll(videos);
            }
            new Thread(() -> {
                try {
                    for (int i = 0; i < 20; i++) { //批量获取20次
                        List<Video> recommendVideo = HttpUtils.recommendVideo(null);
                        videoList.addAll(recommendVideo);
                        Thread.sleep(3000);  //延迟300毫秒再获取
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }

//        System.err.println(videoList.size());
        // 取出前10条
        List<Video> result = new ArrayList<>(videoList.subList(0, Math.min(10,videoList.size())));
        // 移除这10条数据
        videoList.removeAll(result);

        // 调用HttpUtils类的recommendVideo方法获取视频列表
        return result;
    }

    @Override
    public boolean likeVideo(String userId, String videoId) {
        try {
            int result = videoMapper.insertLike(userId, videoId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
