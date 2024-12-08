package edu.ace.infinite.service;

import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface VideoService {
    List<Video> getRecommendVideoList();
    boolean likeVideo(Like like,String  videoId);

    boolean dislikeVideo(Like like,String  videoId);

    List<Like> getLikeList(String uid);
}
