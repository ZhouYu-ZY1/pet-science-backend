package com.pet_science.mapper;

import com.pet_science.pojo.content.Content;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ContentMapper {
    @Insert("INSERT INTO content_like (user_id, video_id, create_time) VALUES (#{userId}, #{videoId}, NOW())")
    int insertLike(@Param("userId") String userId, @Param("videoId") String videoId);
    @Select("SELECT count(1) FROM content WHERE video_id = #{videoId}")
    int findTypeById(String videoId);
    @Insert("INSERT INTO content (video_id,author_id,video_src,cover_src,description,share_url,create_time,update_time,type,nickname,avatar_url,comment_count,digg_count,share_count,status)" +
            " VALUES (#{videoId}, #{uid},#{videoSrc},#{coverSrc},#{desc},#{shareUrl},NOW(),NOW(),#{type},#{nickname},#{authorAvatar},#{commentCount},#{diggCount},#{shareCount},#{status})")
    int insertVideo(Content content);
    @Delete("DELETE FROM content_like WHERE user_id = #{userId} AND video_id = #{videoId}")
    int deleteLike(String userId, String videoId);

    @Select("SELECT * FROM content WHERE video_id = #{videoId}")
    Content findVideoById(String videoId);


//    @Select("SELECT l.id as like_id, l.user_id, l.create_time, " +
//            "v.video_id, v.author_id as uid, v.video_src, v.cover_src, v.description as `desc`, " +
//            "v.share_url " +
//            "FROM content_like l " +
//            "LEFT JOIN content v ON l.video_id = v.video_id " +
//            "WHERE l.user_id = #{uid}")
//   @Results({
//            @Result(property = "like_id", column = "like_id"),
//            @Result(property = "user_id", column = "user_id"),
//            @Result(property = "create_time", column = "create_time"),
//            @Result(property = "video.videoId", column = "video_id"),
//            @Result(property = "video.uid", column = "uid"),
//            @Result(property = "video.videoSrc", column = "video_src"),
//            @Result(property = "video.coverSrc", column = "cover_src"),
//            @Result(property = "video.desc", column = "desc"),
//            @Result(property = "video.shareUrl", column = "share_url")
//  })
    @Select("SELECT v.* " +
            "FROM content_like l " +
            "LEFT JOIN content v ON l.video_id = v.video_id " +
            "WHERE l.user_id = #{uid} " +
            "ORDER BY l.create_time DESC") //时间降序排序
    @Results({
            @Result(property = "videoSrc", column = "video_src"),
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "desc", column = "description"),
            @Result(property = "shareUrl", column = "share_url"),
            @Result(property = "coverSrc", column = "cover_src"),
            @Result(property = "authorAvatar", column = "avatar_url"),
            @Result(property = "uid", column = "author_id"),
    })
    List<Content> selectLikeList(String uid);



    //是否收藏视频
    @Select("SELECT COUNT(*) " +
            "FROM content_like " +
            "WHERE user_id = #{user_id} AND video_id = #{video_id}")
    boolean isLikeVideo(@Param("user_id") String user_id,@Param("video_id") String video_id);

    //获取作品列表
    @Select("SELECT v.*, ul.video_id IS NOT NULL AS is_liked " +
            "FROM content v " +
            "LEFT JOIN content_like ul ON v.video_id = ul.video_id AND ul.user_id = #{uid} " +
            "WHERE v.author_id = #{uid} " +
            "ORDER BY v.create_time DESC")
    @Results({
            @Result(property = "videoSrc", column = "video_src"),
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "desc", column = "description"),
            @Result(property = "shareUrl", column = "share_url"),
            @Result(property = "coverSrc", column = "cover_src"),
            @Result(property = "authorAvatar", column = "avatar_url"),
            @Result(property = "uid", column = "author_id"),
            @Result(property = "like", column = "is_liked", javaType = boolean.class)
    })
    List<Content> selectUserVideoList(@Param("uid") String uid);

    /**
     * 查询内容列表
     * @param params 查询参数
     * @return 内容列表
     */
    @Select("<script>" +
            "SELECT * FROM content " +
            "<where>" +
            "<if test='description != null and description !=  \"\"'>" +
            "AND description LIKE CONCAT('%', #{description}, '%')" +
            "</if>" +
            "<if test='uid != null and uid != \"\"'>" +
            "AND author_id = #{uid}" +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status}" +
            "</if>" +
            "</where>" +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    @Results({
            @Result(property = "videoSrc", column = "video_src"),
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "desc", column = "description"),
            @Result(property = "shareUrl", column = "share_url"),
            @Result(property = "coverSrc", column = "cover_src"),
            @Result(property = "authorAvatar", column = "avatar_url"),
            @Result(property = "uid", column = "author_id"),
    })
    List<Content> selectContentList(Map<String, Object> params);

    /**
     * 统计内容列表总数
     * @param params 查询参数
     * @return 总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM content " +
            "<where>" +
            "<if test='description != null and description != \"\"'>" +
            "AND description LIKE CONCAT('%', #{description}, '%')" +
            "</if>" +
            "<if test='uid != null and uid != \"\"'>" +
            "AND author_id = #{uid}" +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status}" +
            "</if>" +
            "</where>" +
            "</script>")
    int countContentList(Map<String, Object> params);

    /**
     * 更新内容状态
     * @param videoId 视频ID
     * @param status 状态：-1(下架)、0(未审核)、1(已审核)
     * @return 影响行数
     */
    @Update("UPDATE content SET status = #{status}, update_time = NOW() WHERE video_id = #{videoId}")
    int updateContentStatus(@Param("videoId") String videoId, @Param("status") Integer status);
}
