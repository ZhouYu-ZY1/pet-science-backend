package edu.ace.infinite.mapper;

import edu.ace.infinite.pojo.Video;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface VideoMapper {
    @Insert("INSERT INTO `like` (user_id, video_id, create_time) VALUES (#{userId}, #{videoId}, NOW())")
    int insertLike(@Param("userId") String userId, @Param("videoId") String videoId);
    @Select("SELECT count(1) FROM video WHERE user_id = #{videoId}")
    int selectTypeById(String videoId);
    @Insert("INSERT INTO video (video_id, author_id, cover_src,video_src,description,share_url,avatar_url,nickname,type,create_time,update_time)" +
            " VALUES (#{videoId}, #{uid}, #{coverSrc}, #{videoSrc},#{desc},#{shareUrl},#{authorAvatar},#{nickname},#{type},NOW(),NOW()})")
    int insertVideo(String uid, Video video);
}
