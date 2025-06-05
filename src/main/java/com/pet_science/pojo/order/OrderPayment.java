package com.pet_science.pojo.order;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderPayment {
    // 支付ID
    private Integer paymentId;
    // 订单ID
    private Integer orderId;
    // 支付方式
    private String paymentMethod;
    // 支付金额
    private BigDecimal paymentAmount;
    // 支付状态
    private String paymentStatus;
    // 交易号
    private String transactionNo;
    // 支付时间
    private Date paymentTime;
    // 创建时间
    private Date createdAt;
    // 更新时间
    private Date updatedAt;
}