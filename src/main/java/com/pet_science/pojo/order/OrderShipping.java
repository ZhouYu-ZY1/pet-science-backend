package com.pet_science.pojo.order;

import lombok.Data;
import java.util.Date;

@Data
public class OrderShipping {
    // 物流ID
    private Integer shippingId;
    // 订单ID
    private Integer orderId;
    // 收货地址
    private String address;
    // 收货人姓名
    private String receiverName;
    // 收货人电话
    private String receiverMobile;
    // 物流状态
    private String shippingStatus;
    // 运单号
    private String trackingNumber;
    // 发货时间
    private Date shippingTime;
    // 收货时间
    private Date completionTime;
    // 物流公司
    private String shippingCompany;
    // 创建时间
    private Date createdAt;
    // 更新时间
    private Date updatedAt;
}