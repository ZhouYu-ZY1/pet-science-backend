package edu.ace.infinite.service;

import edu.ace.infinite.pojo.Video;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface VideoService {
    List<Video> getVideoList();
}
