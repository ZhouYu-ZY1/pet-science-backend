package edu.ace.infinite.pojo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class User {
    private Integer id;
    private String uname;
    private String upass;
    private String avatar;
    private String nickname;
    private Date createTime;
    private Date updateTime;
    private Boolean isFollowed; //是否关注
    private Date followTime; //关注时间
    private String intro;//简介
    private Integer followCount;
    private Integer fansCount;
}
