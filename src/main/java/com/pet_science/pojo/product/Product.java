package com.pet_science.pojo.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("products")
@ApiModel(value = "Product对象", description = "产品信息")
public class Product {

    @TableId(value = "product_id", type = IdType.AUTO)
    @ApiModelProperty(value = "产品ID")
    private Integer productId;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品类别")
    private String category;

    @ApiModelProperty(value = "产品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "产品库存")
    private Integer stock;

    @ApiModelProperty(value = "产品描述")
    private String description;

    @ApiModelProperty(value = "产品图片URL")
    private String mainImage;

    @ApiModelProperty(value = "产品状态：0-下架，1-上架")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;
}