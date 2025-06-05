package com.pet_science.pojo.order;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem {
    // 订单项ID
    private Integer orderItemId;
    // 订单ID
    private Integer orderId;
    // 商品ID
    private Integer productId;
    // 商品名称
    private String productName;
    // 商品图片
    private String productImage;
    // 购买数量
    private Integer quantity;
    // 单价
    private BigDecimal price;
    // 小计金额
    private BigDecimal subtotal;
    // 创建时间
    private Date createdAt;
    // 更新时间
    private Date updatedAt;
}