package com.pet_science.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@ApiModel(description = "用户实体类")
public class User {
    @ApiModelProperty(value = "用户ID", example = "1")
    private Integer userId;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须为4-16位字母、数字或下划线")
    @ApiModelProperty(value = "用户名", required = true, example = "john_doe")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间")
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱", required = true, example = "john@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", example = "13800138000")
    private String mobile;

    @ApiModelProperty(value = "头像URL", example = "/statics/images/defaultAvatar.jpg")
    private String avatarUrl;

    @ApiModelProperty(value = "昵称", example = "昵称")
    private String nickname;

    @ApiModelProperty(value = "性别", example = "0", notes = "0: 保密, 1: 男, 2: 女")
    private Integer gender;

    @ApiModelProperty(value = "生日", example = "1990-01-01")
    private Date birthday;

    @ApiModelProperty(value = "地区", example = "北京市")
    private String location;

    @ApiModelProperty(value = "个人简介", example = "这是我的个人简介")
    private String bio;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    @ApiModelProperty(value = "状态", example = "1", notes = "0: 禁用, 1: 正常")
    private Integer status;

    private Boolean isFollowed = false; // 是否已关注
    private Date followTime; // 关注时间
    private Integer followCount = 0; // 关注数
    private Integer fansCount = 0; // 粉丝数
}
