package edu.ace.infinite.pojo;

import io.swagger.models.auth.In;
import lombok.Data;

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
}
