package edu.ace.infinite.mapper;

import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoMapper {
    @Insert("INSERT INTO `like` (user_id, video_id, create_time) VALUES (#{userId}, #{videoId}, NOW())")
    int insertLike(@Param("userId") String userId, @Param("videoId") String videoId);
    @Select("SELECT count(1) FROM video WHERE video_id = #{videoId}")
    int findTypeById(String videoId);
    @Insert("INSERT INTO video (video_id,author_id,video_src,cover_src,description,share_url,create_time,update_time,type,nickname,avatar_url)" +
            " VALUES (#{videoId}, #{uid},#{videoSrc},#{coverSrc},#{desc},#{shareUrl},NOW(),NOW(),#{type},#{nickname},#{authorAvatar})")
    int insertVideo(Video video);
    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND video_id = #{videoId}")
    int deleteLike(String userId, String videoId);

    @Select("SELECT * FROM video WHERE video_id = #{videoId}")
    Video findVideoById(String videoId);
    @Select("SELECT l.id as like_id, l.user_id, l.create_time, " +
            "v.video_id, v.author_id as uid, v.video_src, v.cover_src, v.description as `desc`, " +
            "v.share_url " +
            "FROM `like` l " +
            "LEFT JOIN video v ON l.video_id = v.video_id " +
            "WHERE l.user_id = #{uid}")
    @Results({
            @Result(property = "like_id", column = "like_id"),
            @Result(property = "user_id", column = "user_id"),
            @Result(property = "create_time", column = "create_time"),
            @Result(property = "video.videoId", column = "video_id"),
            @Result(property = "video.uid", column = "uid"),
            @Result(property = "video.videoSrc", column = "video_src"),
            @Result(property = "video.coverSrc", column = "cover_src"),
            @Result(property = "video.desc", column = "desc"),
            @Result(property = "video.shareUrl", column = "share_url")
    })
    List<Like> selectLikeList(String uid);


}
