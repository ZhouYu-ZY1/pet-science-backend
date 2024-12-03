package edu.ace.infinite.service.impl;

import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import edu.ace.infinite.utils.HttpUtils;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VideoServiceImpl implements VideoService {

    @Override
    public List<Video> getVideoList() {
        // 调用HttpUtils类的recommendVideo方法获取视频列表
        List<Video> videos = HttpUtils.recommendVideo(null);
        return videos;
    }

}
