package edu.ace.infinite.pojo;

import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class Video {
    // 视频源地址
    private String videoSrc;
    // 视频ID
    private String videoId;
    // 描述
    private String desc;
    // 分享链接
    private String shareUrl;
    // 封面图片地址
    private String coverSrc;
    // 作者头像地址
    private String authorAvatar;
    // 用户ID
    private String uid;
    // 用户昵称
    private String nickname;
    // 视频类型，0为抖音视频，1为用户上传
    private Integer type;
    //是否收藏
    private boolean like;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(videoId, video.videoId) && Objects.equals(uid, video.uid);
    }
}
