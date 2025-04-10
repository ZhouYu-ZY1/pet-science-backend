package com.pet_science.mapper.order;

import com.pet_science.pojo.OrderShipping;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface OrderShippingMapper {
    /**
     * 根据订单ID查询物流信息
     * @param orderId 订单ID
     * @return 物流信息
     */
    @Select("SELECT * FROM order_shipping WHERE order_id = #{orderId}")
    OrderShipping findByOrderId(Integer orderId);
    
    /**
     * 插入物流信息
     * @param shipping 物流信息
     * @return 影响行数
     */
    @Insert("INSERT INTO order_shipping (order_id, address, receiver_name, receiver_mobile, shipping_status, " +
            "tracking_number, shipping_time, completion_time, shipping_company, created_at, updated_at) " +
            "VALUES (#{orderId}, #{address}, #{receiverName}, #{receiverMobile}, #{shippingStatus}, " +
            "#{trackingNumber}, #{shippingTime}, #{completionTime}, #{shippingCompany}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "shippingId")
    int insert(OrderShipping shipping);
    
    /**
     * 更新物流状态
     * @param shippingId 物流ID
     * @param shippingStatus 物流状态
     * @return 影响行数
     */
    @Update("UPDATE order_shipping SET shipping_status = #{shippingStatus}, updated_at = NOW() WHERE shipping_id = #{shippingId}")
    int updateStatus(@Param("shippingId") Integer shippingId, @Param("shippingStatus") String shippingStatus);
    
    /**
     * 更新物流信息
     * @param shipping 物流信息
     * @return 影响行数
     */
    @Update("UPDATE order_shipping SET address = #{address}, receiver_name = #{receiverName}, " +
            "receiver_mobile = #{receiverMobile}, shipping_status = #{shippingStatus}, " +
            "tracking_number = #{trackingNumber}, shipping_time = #{shippingTime}, " +
            "completion_time = #{completionTime}, shipping_company = #{shippingCompany}, " +
            "updated_at = NOW() WHERE shipping_id = #{shippingId}")
    int update(OrderShipping shipping);
    
    /**
     * 更新物流单号和发货时间
     * @param orderId 订单ID
     * @param trackingNumber 物流单号
     * @param shippingTime 发货时间
     * @param shippingStatus 物流状态
     * @return 影响行数
     */
    @Update("UPDATE order_shipping SET tracking_number = #{trackingNumber}, shipping_time = #{shippingTime}, " +
            "shipping_status = #{shippingStatus}, updated_at = NOW() WHERE order_id = #{orderId}")
    int updateShippingInfo(@Param("orderId") Integer orderId, 
                           @Param("trackingNumber") String trackingNumber,
                           @Param("shippingTime") Date shippingTime,
                           @Param("shippingStatus") String shippingStatus);
    
    /**
     * 删除物流信息
     * @param shippingId 物流ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_shipping WHERE shipping_id = #{shippingId}")
    int deleteById(Integer shippingId);
    
    /**
     * 根据订单ID删除物流信息
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_shipping WHERE order_id = #{orderId}")
    int deleteByOrderId(Integer orderId);
}