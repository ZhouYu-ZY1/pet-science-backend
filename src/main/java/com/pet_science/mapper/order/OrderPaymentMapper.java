package com.pet_science.mapper.order;

import com.pet_science.pojo.OrderPayment;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface OrderPaymentMapper {
    /**
     * 根据订单ID查询支付信息
     * @param orderId 订单ID
     * @return 支付信息
     */
    @Select("SELECT * FROM order_payment WHERE order_id = #{orderId}")
    OrderPayment findByOrderId(Integer orderId);
    
    /**
     * 插入支付信息
     * @param payment 支付信息
     * @return 影响行数
     */
    @Insert("INSERT INTO order_payment (order_id, payment_method, payment_amount, payment_status, transaction_no, payment_time, created_at, updated_at) " +
            "VALUES (#{orderId}, #{paymentMethod}, #{paymentAmount}, #{paymentStatus}, #{transactionNo}, #{paymentTime}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "paymentId")
    int insert(OrderPayment payment);
    
    /**
     * 更新支付状态
     * @param paymentId 支付ID
     * @param paymentStatus 支付状态
     * @return 影响行数
     */
    @Update("UPDATE order_payment SET payment_status = #{paymentStatus}, updated_at = NOW() WHERE payment_id = #{paymentId}")
    int updateStatus(@Param("paymentId") Integer paymentId, @Param("paymentStatus") String paymentStatus);
    
    /**
     * 更新支付信息
     * @param payment 支付信息
     * @return 影响行数
     */
    @Update("UPDATE order_payment SET payment_method = #{paymentMethod}, payment_amount = #{paymentAmount}, " +
            "payment_status = #{paymentStatus}, transaction_no = #{transactionNo}, payment_time = #{paymentTime}, " +
            "updated_at = NOW() WHERE payment_id = #{paymentId}")
    int update(OrderPayment payment);
    
    /**
     * 删除支付信息
     * @param paymentId 支付ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_payment WHERE payment_id = #{paymentId}")
    int deleteById(Integer paymentId);
    
    /**
     * 根据订单ID删除支付信息
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_payment WHERE order_id = #{orderId}")
    int deleteByOrderId(Integer orderId);
}