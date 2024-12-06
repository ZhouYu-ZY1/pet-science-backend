package edu.ace.infinite.pojo;

import edu.ace.infinite.service.VideoService;
import lombok.Data;

@Data
public class Like {
    private String like_id;
    private String user_id;
    private Video video;
    private String create_time;
    private boolean isLike;
}
