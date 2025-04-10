package com.pet_science.mapper.order;

import com.pet_science.pojo.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    /**
     * 根据订单ID查询订单项列表
     * @param orderId 订单ID
     * @return 订单项列表
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Integer orderId);
    
    /**
     * 插入订单项
     * @param orderItem 订单项信息
     * @return 影响行数
     */
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_image, quantity, price, subtotal, created_at, updated_at) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productImage}, #{quantity}, #{price}, #{subtotal}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "orderItemId")
    int insert(OrderItem orderItem);
    
    /**
     * 批量插入订单项
     * @param orderItems 订单项列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO order_items (order_id, product_id, product_name, product_image, quantity, price, subtotal, created_at, updated_at) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.productName}, #{item.productImage}, #{item.quantity}, #{item.price}, #{item.subtotal}, #{item.createdAt}, #{item.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<OrderItem> orderItems);
    
    /**
     * 删除订单项
     * @param orderItemId 订单项ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_items WHERE order_item_id = #{orderItemId}")
    int deleteById(Integer orderItemId);
    
    /**
     * 根据订单ID删除订单项
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteByOrderId(Integer orderId);
}