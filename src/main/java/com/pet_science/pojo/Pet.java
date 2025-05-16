package com.pet_science.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "宠物信息")
public class Pet {
    @ApiModelProperty(value = "宠物ID")
    private Long id;
    
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    @ApiModelProperty(value = "宠物名称")
    private String name;
    
    @ApiModelProperty(value = "宠物类型：cat-猫, dog-狗, other-其他")
    private String type;
    
    @ApiModelProperty(value = "宠物品种")
    private String breed;
    
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    
    @ApiModelProperty(value = "宠物头像")
    private String avatarUrl;
    
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
}