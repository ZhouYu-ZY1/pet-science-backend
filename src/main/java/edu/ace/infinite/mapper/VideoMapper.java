package edu.ace.infinite.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface VideoMapper {
    @Insert("INSERT INTO `like` (user_id, video_id, create_time) VALUES (#{userId}, #{videoId}, NOW())")
    int insertLike(@Param("userId") String userId, @Param("videoId") String videoId);
}
