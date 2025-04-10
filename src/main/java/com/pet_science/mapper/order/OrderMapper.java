package com.pet_science.mapper.order;

import com.pet_science.pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 获取订单列表
     * @param params 查询参数
     * @return 订单列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "getOrderList")
    @Results({
        @Result(property = "shipping.receiverName", column = "receiver_name"),
        @Result(property = "shipping.receiverMobile", column = "receiver_mobile"),
        @Result(property = "shipping.address", column = "shipping_address"),
        @Result(property = "shipping.trackingNumber", column = "tracking_number"),
        @Result(property = "shipping.shippingTime", column = "shipping_time"),
        @Result(property = "shipping.completionTime", column = "completion_time"),
        @Result(property = "payment.paymentTime", column = "payment_time")
    })
    List<Order> getOrderList(Map<String, Object> params);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order findById(Integer orderId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 状态值
     * @return 影响行数
     */
    @Update("UPDATE orders SET status = #{status}, updated_at = NOW() WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);
    
    /**
     * SQL提供者，用于动态SQL
     */
    class OrderSqlProvider {
        public String getOrderList(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT o.*, s.*, p.* FROM orders o ");
            sql.append("LEFT JOIN order_shipping s ON o.order_id = s.order_id ");
            sql.append("LEFT JOIN order_payment p ON o.order_id = p.order_id ");
            sql.append("WHERE 1=1 ");

            if (params.containsKey("orderNo") && params.get("orderNo") != null && !params.get("orderNo").toString().trim().isEmpty()) {
                sql.append(" AND o.order_no LIKE CONCAT('%', #{orderNo}, '%')");
            }

            if (params.containsKey("status") && params.get("status") != null && !params.get("status").toString().trim().isEmpty()) {
                sql.append(" AND o.status = #{status}");
            }

            if (params.containsKey("userId") && params.get("userId") != null && !params.get("userId").toString().trim().isEmpty()) {
                sql.append(" AND o.user_id = #{userId}");
            }

            // 添加收货人查询条件
            if (params.containsKey("consignee") && params.get("consignee") != null && !params.get("consignee").toString().trim().isEmpty()) {
                sql.append(" AND s.receiver_name LIKE CONCAT('%', #{consignee}, '%')");
            }

            // 添加手机号查询条件
            if (params.containsKey("mobile") && params.get("mobile") != null && !params.get("mobile").toString().trim().isEmpty()) {
                sql.append(" AND s.receiver_mobile LIKE CONCAT('%', #{mobile}, '%')");
            }

            // 修改时间范围查询，使用startTime和endTime参数
            if (params.containsKey("startTime") && params.get("startTime") != null && !params.get("startTime").toString().trim().isEmpty()) {
                sql.append(" AND o.created_at >= #{startTime}");
            }

            if (params.containsKey("endTime") && params.get("endTime") != null && !params.get("endTime").toString().trim().isEmpty()) {
                sql.append(" AND o.created_at <= #{endTime}");
            }
            
            sql.append(" ORDER BY o.created_at DESC");
            
            return sql.toString();
        }
    }
    
    /**
     * 插入新订单
     * @param order 订单信息
     * @return 影响行数
     */
    @Insert("INSERT INTO orders (user_id, order_no, total_amount, status, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{orderNo}, #{totalAmount}, #{status}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insert(Order order);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM orders WHERE order_id = #{orderId}")
    int deleteById(Integer orderId);

}