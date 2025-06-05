package com.pet_science.service;

import com.pet_science.pojo.order.Order;
import com.pet_science.pojo.PageResult;

import java.util.Map;

public interface OrderService {
    /**
     * 获取订单列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<Order> getOrderListPage(Integer pageNum, Integer pageSize, Map<String, Object> params);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单信息（包含订单项、支付信息和物流信息）
     */
    Order getOrderDetail(Integer orderId,Integer userId,boolean isAdmin);

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 状态值
     * @return 是否更新成功
     */
    boolean updateOrderStatus(Integer orderId, String status);
    
    /**
     * 创建订单
     * @param order 订单信息（包含订单项）
     * @return 创建的订单
     */
    Order createOrder(Order order);

    // 添加新方法
    /**
     * 设置订单过期定时任务
     * @param orderId 订单ID
     * @param minutes 过期时间（分钟）
     */
    void scheduleOrderExpiration(Integer orderId, int minutes);

    /**
     * 移除订单过期定时任务
     * @param orderId 订单ID
     */
    void removeOrderExpiration(Integer orderId);
    
    /**
     * 获取订单剩余支付时间（秒）
     * @param orderId 订单ID
     * @return 剩余秒数，如果订单已过期或不存在返回0
     */
    Long getOrderRemainingTime(Integer orderId);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 是否支付成功
     */
    boolean payOrder(Integer orderId, String paymentMethod);
    
    /**
     * 订单发货
     *
     * @param orderId          订单ID
     * @param trackingNumber   物流单号
     * @param shippingCompany 物流公司
     * @return 是否发货成功
     */
    boolean shipOrder(Integer orderId, String trackingNumber, String shippingCompany);
    
    /**
     * 完成订单
     * @param orderId 订单ID
     * @return 是否完成成功
     */
    boolean completeOrder(Integer orderId);
}