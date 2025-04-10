package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireAdmin;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.Order;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Result;
import com.pet_science.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.pet_science.service.impl.OrderServiceImpl.ORDER_EXPIRATION_MINUTES;

@RestController
@RequestMapping("/order")
@RequireUser // 用户身份验证注解
@Api(tags = "订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    @RequireAdmin
    @ApiOperation(value = "获取订单列表", notes = "支持按订单号、收货人、手机号、状态、用户ID和时间范围筛选，支持分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer"),
        @ApiImplicitParam(name = "orderNo", value = "订单号", dataType = "String"),
        @ApiImplicitParam(name = "consignee", value = "收货人", dataType = "String"),
        @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String"),
        @ApiImplicitParam(name = "status", value = "订单状态", dataType = "String"),
        @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer"),
        @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String")
    })
    public Result<PageResult<Order>> getOrderList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            // 获取分页参数
            Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            // 调用服务层方法获取分页数据
            PageResult<Order> pageResult = orderService.getOrderListPage(pageNum, pageSize, params);
            return Result.successResultData(pageResult);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取订单列表失败");
        }
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取订单详情", notes = "根据订单ID获取订单详细信息")
    @ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Order> getOrderDetail(@PathVariable("id") Integer id) {
        try {
            Order order = orderService.getOrderDetail(id);
            return Result.successResultData(order);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取订单详情失败");
        }
    }
    
    @PutMapping("/status")
    @ApiOperation(value = "更新订单状态", notes = "更新指定订单的状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "status", value = "状态值", required = true, dataType = "String")
    })
    public Result<String> updateOrderStatus(@RequestBody JSONObject jsonObject) {
        try {
            Integer orderId = jsonObject.getInteger("orderId");
            String status = jsonObject.getString("status");
            boolean result = orderService.updateOrderStatus(orderId, status);
            if (result) {
                return Result.successResultData("更新订单状态成功");
            } else {
                return Result.error(400, "更新订单状态失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "更新订单状态失败");
        }
    }
    
    @PostMapping("/create")
    @ApiOperation(value = "创建订单", notes = "创建新订单，只需提供必要的订单信息，订单将在30分钟内未支付自动取消")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "remark", value = "订单备注", dataType = "String"),
        @ApiImplicitParam(name = "orderItem.productId", value = "商品ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "orderItem.quantity", value = "购买数量", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "shipping.address", value = "收货地址", dataType = "String"),
        @ApiImplicitParam(name = "shipping.receiverName", value = "收货人姓名", dataType = "String"),
        @ApiImplicitParam(name = "shipping.receiverMobile", value = "收货人电话", dataType = "String"),
    })
    public Result<Order> createOrder(@RequestBody Order order) {
        try {
            // 创建订单
            Order createdOrder = orderService.createOrder(order);
            // 设置订单过期时间
            orderService.scheduleOrderExpiration(order.getOrderId(), ORDER_EXPIRATION_MINUTES);
            return Result.successResultData(createdOrder);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "创建订单失败");
        }
    }
    
    @GetMapping("/expiration/{id}")
    @ApiOperation(value = "获取订单过期时间", notes = "获取订单剩余支付时间（秒）")
    @ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Long> getOrderExpiration(@PathVariable("id") Integer id) {
        try {
            Long remainingSeconds = orderService.getOrderRemainingTime(id);
            return Result.successResultData(remainingSeconds);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取订单过期时间失败");
        }
    }
    
    @PutMapping("/pay")
    @ApiOperation(value = "支付订单", notes = "更新订单为已支付状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "paymentMethod", value = "支付方式", required = true, dataType = "String")
    })
    public Result<String> payOrder(@RequestBody JSONObject jsonObject) {
        try {
            Integer orderId = jsonObject.getInteger("orderId");
            String paymentMethod = jsonObject.getString("paymentMethod");
            boolean result = orderService.payOrder(orderId, paymentMethod);
            if (result) {
                // 订单支付成功，移除订单过期时间
                orderService.removeOrderExpiration(orderId);
                return Result.successResultData("订单支付成功");
            } else {
                return Result.error(400, "订单支付失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "订单支付失败");
        }
    }
    
    @PutMapping("/ship")
    @ApiOperation(value = "订单发货", notes = "更新订单为已发货状态")
    @RequireAdmin
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "trackingNumber", value = "物流单号", required = true, dataType = "String")
    })
    public Result<String> shipOrder(@RequestBody JSONObject jsonObject) {
        try {
            Integer orderId = jsonObject.getInteger("orderId");
            String trackingNumber = jsonObject.getString("trackingNumber");
            String shippingCompany = jsonObject.getString("shippingCompany");
            boolean result = orderService.shipOrder(orderId, trackingNumber,shippingCompany);
            if (result) {
                return Result.successResultData("订单发货成功");
            } else {
                return Result.error(400, "订单发货失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "订单发货失败");
        }
    }
    
    @PutMapping("/complete")
    @ApiOperation(value = "完成订单", notes = "更新订单为已完成状态")
    @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "Integer")
    public Result<String> completeOrder(@RequestBody JSONObject jsonObject) {
        try {
            Integer orderId = jsonObject.getInteger("orderId");
            boolean result = orderService.completeOrder(orderId);
            if (result) {
                return Result.successResultData("订单完成成功");
            } else {
                return Result.error(400, "订单完成失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "订单完成失败");
        }
    }
}