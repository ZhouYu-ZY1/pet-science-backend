package com.pet_science.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class Admin {
    private Integer adminId;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String realName;
    private String avatarUrl;
    private Integer status; // 0-禁用，1-正常
    private Date lastLoginTime;
    private Date createdAt;
    private Date updatedAt;
}