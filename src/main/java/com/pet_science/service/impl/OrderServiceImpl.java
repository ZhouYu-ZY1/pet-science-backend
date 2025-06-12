package com.pet_science.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.mapper.order.OrderItemMapper;
import com.pet_science.mapper.order.OrderMapper;
import com.pet_science.mapper.order.OrderPaymentMapper;
import com.pet_science.mapper.order.OrderShippingMapper;
import com.pet_science.pojo.*;
import com.pet_science.pojo.order.Order;
import com.pet_science.pojo.order.OrderItem;
import com.pet_science.pojo.order.OrderPayment;
import com.pet_science.pojo.order.OrderShipping;
import com.pet_science.pojo.product.Product;
import com.pet_science.service.OrderService;
import com.pet_science.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.benmanes.caffeine.cache.Cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderPaymentMapper orderPaymentMapper;

    @Autowired
    private OrderShippingMapper orderShippingMapper;

    // 订单过期缓存前缀
    private static final String ORDER_EXPIRATION_KEY_PREFIX = "order:expiration:";
    public static final int ORDER_EXPIRATION_MINUTES = 1; // 订单过期时间（分钟）
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private Cache<String, Long> orderExpirationCache; // 订单过期时间缓存

    /**
     * 获取订单列表（分页）
     * 
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @param params   查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<Order> getOrderListPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        try {
            // 设置分页参数
            PageHelper.startPage(pageNum, pageSize);
            // 查询数据
            List<Order> orderList = orderMapper.getOrderList(params);
            // 获取分页信息
            PageInfo<Order> pageInfo = new PageInfo<>(orderList);
            // 返回分页结果
            return PageResult.restPage(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("获取订单列表失败");
        }
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单信息（包含订单项、支付信息和物流信息）
     */
    @Override
    public Order getOrderDetail(Integer orderId,Integer userId,boolean isAdmin) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }

        // 查询订单基本信息
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于当前用户
        if(!isAdmin && !order.getUserId().equals(userId)){
            throw new BusinessException("订单不属于该用户");
        }

        // 查询订单项列表
        List<OrderItem> orderItems = orderItemMapper.findByOrderId(orderId);
        if (orderItems != null && !orderItems.isEmpty()) {
            // 设置订单项列表（支持多商品）
            order.setOrderItems(orderItems);
            // 为了向后兼容，也设置第一个订单项
            order.setOrderItem(orderItems.get(0));
        }

        // 查询支付信息
        OrderPayment payment = orderPaymentMapper.findByOrderId(orderId);
        order.setPayment(payment);

        // 查询物流信息
        OrderShipping shipping = orderShippingMapper.findByOrderId(orderId);
        order.setShipping(shipping);

        return order;
    }

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status  状态值
     * @return 是否更新成功
     */
    @Override
    public boolean updateOrderStatus(Integer orderId, String status) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (status == null || status.isEmpty()) {
            throw new BusinessException("订单状态不能为空");
        }
        // 检查订单是否存在
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 更新状态
        int result = orderMapper.updateStatus(orderId, status);
        return result > 0;
    }

    /**
     * 创建订单
     *
     * @param order 订单信息（包含订单项）
     * @return 创建的订单
     */
    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);

        // 设置初始状态为待支付
        order.setStatus("pending");

        // 设置创建时间和更新时间
        Date now = new Date();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // 获取订单项列表（支持多商品和单商品）
        List<OrderItem> orderItems = getOrderItemsFromOrder(order);
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException("订单项不能为空");
        }

        // 验证所有订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProductId() == null) {
                throw new BusinessException("商品ID不能为空");
            }

            // 获取商品信息
            Product product = productService.getProductDetail(orderItem.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在：" + orderItem.getProductId());
            }

            // 检查商品库存
            Integer quantity = orderItem.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new BusinessException("购买数量必须大于0");
            }
            if (product.getStock() < quantity) {
                throw new BusinessException("商品库存不足：" + product.getProductName());
            }

            // 获取商品单价并设置订单项信息
            BigDecimal price = product.getPrice();
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(price);

            // 计算小计
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
            orderItem.setSubtotal(subtotal);

            // 累加到总金额
            totalAmount = totalAmount.add(subtotal);
        }

        // 设置订单总金额
        order.setTotalAmount(totalAmount);

        // 保存订单
        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new SystemException("创建订单失败");
        }

        // 批量保存订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getOrderId());
            orderItem.setCreatedAt(now);
            orderItem.setUpdatedAt(now);
        }
        orderItemMapper.batchInsert(orderItems);

        // 保存物流信息
        OrderShipping shipping = order.getShipping();
        if (shipping == null) {
            throw new BusinessException("物流信息不能为空");
        }
        shipping.setOrderId(order.getOrderId());
        shipping.setShippingStatus("pending");
        shipping.setCreatedAt(now);
        shipping.setUpdatedAt(now);
        orderShippingMapper.insert(shipping);

        return getOrderDetail(order.getOrderId(),order.getUserId(),false);
    }

    /**
     * 支付订单
     * 
     * @param orderId       订单ID
     * @param paymentMethod 支付方式
     * @return 是否支付成功
     */
    @Override
    @Transactional
    public boolean payOrder(Integer orderId, String paymentMethod) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new BusinessException("支付方式不能为空");
        }

        // 检查订单是否存在
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态是否为待支付
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        // 更新订单状态为已支付
        Date now = new Date();
        int orderResult = orderMapper.updateStatus(orderId, "paid");

        // 创建或更新支付信息
        OrderPayment payment = orderPaymentMapper.findByOrderId(orderId);
        if (payment == null) {
            payment = new OrderPayment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentAmount(order.getTotalAmount());
            payment.setPaymentStatus("success");
            payment.setPaymentTime(now);
            payment.setCreatedAt(now);
            payment.setUpdatedAt(now);
            orderPaymentMapper.insert(payment);
        } else {
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus("success");
            payment.setPaymentTime(now);
            payment.setUpdatedAt(now);
            orderPaymentMapper.update(payment);
        }

        return orderResult > 0;
    }

    /**
     * 订单发货
     *
     * @param orderId          订单ID
     * @param trackingNumber   物流单号
     * @param shippingCompany 物流公司
     * @return 是否发货成功
     */
    @Override
    @Transactional
    public boolean shipOrder(Integer orderId, String trackingNumber, String shippingCompany) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            throw new BusinessException("物流单号不能为空");
        }

        // 检查订单是否存在
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态是否为已支付
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法发货");
        }

        // 更新订单状态为已发货
        Date now = new Date();
        int orderResult = orderMapper.updateStatus(orderId, "shipped");

        // 更新物流信息
        OrderShipping shipping = orderShippingMapper.findByOrderId(orderId);
        if (shipping != null) {
            shipping.setTrackingNumber(trackingNumber);
            shipping.setShippingCompany(shippingCompany);
            shipping.setShippingStatus("shipped");
            shipping.setShippingTime(now);
            shipping.setUpdatedAt(now);
            orderShippingMapper.update(shipping);
        } else {
            throw new BusinessException("订单物流信息不存在");
        }

        return orderResult > 0;
    }

    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @return 是否完成成功
     */
    @Override
    @Transactional
    public boolean completeOrder(Integer orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }

        // 检查订单是否存在
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态是否为已发货
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法完成");
        }

        // 更新订单状态为已完成
        Date now = new Date();
        int orderResult = orderMapper.updateStatus(orderId, "completed");

        // 更新物流信息
        OrderShipping shipping = orderShippingMapper.findByOrderId(orderId);
        if (shipping != null) {
            shipping.setShippingStatus("delivered");
            shipping.setCompletionTime(now);
            shipping.setUpdatedAt(now);
            orderShippingMapper.update(shipping);
        }

        return orderResult > 0;
    }

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成格式为：年月日+随机数的订单号
        String dateStr = String.format("%tY%<tm%<td", new Date());
        String randomStr = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        return "ORD" + dateStr + randomStr;
    }

    /**
     * 从订单对象中获取订单项列表
     * 支持多商品订单（orderItems）和单商品订单（orderItem）的兼容性
     *
     * @param order 订单对象
     * @return 订单项列表
     */
    private List<OrderItem> getOrderItemsFromOrder(Order order) {
        List<OrderItem> orderItems = new ArrayList<>();

        // 优先使用 orderItems 列表（多商品订单）
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            orderItems.addAll(order.getOrderItems());
        }
        // 如果没有 orderItems，则使用单个 orderItem（向后兼容）
        else if (order.getOrderItem() != null) {
            orderItems.add(order.getOrderItem());
        }

        return orderItems;
    }


    /**
     * 设置订单过期时间
     * @param orderId 订单ID
     * @param minutes 过期时间（分钟）
     */
    @Override
    @Async("orderTaskExecutor")  // 添加异步注解
    public void scheduleOrderExpiration(Integer orderId, int minutes) {
        if (orderId == null || minutes <= 0) {
            return;
        }

        // 在缓存中设置过期键，用于记录订单过期时间
        String expirationKey = ORDER_EXPIRATION_KEY_PREFIX + orderId;
        long expirationTime = System.currentTimeMillis() + minutes * 60 * 1000L;
        orderExpirationCache.put(expirationKey, expirationTime);

        try {
            // 异步等待指定时间
            Thread.sleep(minutes * 60 * 1000);

            // 检查订单状态
            Order order = orderMapper.findById(orderId);
            if (order != null && "pending".equals(order.getStatus())) {
                // 如果订单仍然是待支付状态，则自动取消
                updateOrderStatus(orderId, "cancelled");
                System.out.println("订单 " + orderId + " 超时未支付，已自动取消");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除订单过期时间
     * @param orderId 订单ID
     */
    @Override
    public void removeOrderExpiration(Integer orderId) {
        if (orderId == null) {
            return;
        }
        
        // 从缓存中移除订单过期时间记录
        String expirationKey = ORDER_EXPIRATION_KEY_PREFIX + orderId;
        orderExpirationCache.invalidate(expirationKey);
    }

    /**
     * 获取订单剩余支付时间
     * @param orderId 订单ID
     * @return  订单剩余支付时间（秒）
     */
    @Override
    public Long getOrderRemainingTime(Integer orderId) {
        if (orderId == null) {
            return 0L;
        }

        // 1. 从缓存中获取过期时间
        String expirationKey = ORDER_EXPIRATION_KEY_PREFIX + orderId;
        Long expirationTime = orderExpirationCache.getIfPresent(expirationKey);

        if (expirationTime == null) {
            // 检查订单是否存在且状态为pending
            Order order = orderMapper.findById(orderId);
            if (order == null || !"pending".equals(order.getStatus())) {
                return 0L;
            }

            // 如果缓存中没有过期时间记录，但订单状态为pending，设置默认剩余时间为5分钟
            return 5 * 60L;
        }

        // 2. 计算剩余时间（秒）
        long currentTime = System.currentTimeMillis();
        long remainingMillis = expirationTime - currentTime;

        // 如果剩余时间小于等于0，返回0
        if (remainingMillis <= 0) {
            return 0L;
        }

        // 转换为秒并返回
        return remainingMillis / 1000;
    }
}