package com.pet_science.pojo.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("product_category")
@ApiModel(value = "Category对象", description = "产品分类信息")
public class Category {
    
    @TableId(value = "category_id", type = IdType.AUTO)
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    
    @ApiModelProperty(value = "分类编码")
    private String categoryCode;
    
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
}