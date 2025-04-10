package com.pet_science.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    // 订单ID
    private Integer orderId;
    // 用户ID
    private Integer userId;
    // 订单号
    private String orderNo;
    // 总金额
    private BigDecimal totalAmount;
    // 订单状态
    // pending 待支付,paid 已支付,shipped 已发货,completed 完成,cancelled 取消
    private String status;
    // 备注
    private String remark;
    // 创建时间
    private Date createdAt;
    // 更新时间
    private Date updatedAt;
    
    // 关联的订单项（非数据库字段，用于数据传输）
    private OrderItem orderItem;
    // 关联的支付信息（非数据库字段，用于数据传输）
    private OrderPayment payment;
    // 关联的物流信息（非数据库字段，用于数据传输）
    private OrderShipping shipping;
}