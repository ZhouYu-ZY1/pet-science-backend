package com.pet_science.pojo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "用户收货地址")
public class UserAddress {
    @ApiModelProperty(value = "地址ID")
    private Integer id;
    
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    @ApiModelProperty(value = "收货人姓名")
    private String recipientName;
    
    @ApiModelProperty(value = "收货人手机号")
    private String recipientPhone;
    
    @ApiModelProperty(value = "省份")
    private String province;
    
    @ApiModelProperty(value = "城市")
    private String city;
    
    @ApiModelProperty(value = "区县")
    private String district;
    
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;
    
    @ApiModelProperty(value = "地址标签")
    private String addressTag;
    
    @ApiModelProperty(value = "是否默认地址：0-否，1-是")
    private Integer isDefault;
    
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
}