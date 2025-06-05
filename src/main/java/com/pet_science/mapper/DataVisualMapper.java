package com.pet_science.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据可视化Mapper接口
 */
@Mapper
public interface DataVisualMapper {
    
    /**
     * 获取注册用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    int getRegisteredUsersCount();
    
    /**
     * 获取宠物总数
     */
    @Select("SELECT COUNT(*) FROM pets")
    int getTotalPetsCount();
    
    /**
     * 获取订单总数
     */
    @Select("SELECT COUNT(*) FROM orders")
    int getTotalOrdersCount();
    
    /**
     * 获取年交易额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM orders WHERE YEAR(created_at) = YEAR(CURRENT_DATE)")
    double getAnnualTransactionValue();
    
    /**
     * 获取宠物类型数量
     */
    @Select("SELECT type, COUNT(*) as count FROM pets GROUP BY `type`")
    List<Map<String, Object>> getPetTypeCounts();
    
    /**
     * 获取各类产品销售额和占比
     */
    @Select("SELECT pc.category_name, IFNULL(SUM(oi.subtotal), 0) as sales_amount, " +
            "COUNT(DISTINCT oi.order_id) as order_count " +
            "FROM product_category pc " +
            "LEFT JOIN products p ON pc.category_id = p.category " +
            "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
            "GROUP BY pc.category_id, pc.category_name")
    List<Map<String, Object>> getProductCategorySales();

    /**
     * 获取总销售额
     */
    @Select("SELECT IFNULL(SUM(subtotal), 0) FROM order_items")
    double getTotalSalesAmount();
    
    /**
     * 获取订单收货地区分布
     */
    @Select("SELECT " +
            "SUBSTRING_INDEX(os.address, '省', 1) as address, " +
            "SUM(oi.quantity) as count " +
            "FROM order_shipping os " +
            "JOIN orders o ON os.order_id = o.order_id " +
            "JOIN order_items oi ON o.order_id = oi.order_id " +
            "WHERE os.address REGEXP '^[^省]+省' " +  // 匹配以省结尾的地址
            "GROUP BY SUBSTRING_INDEX(os.address, '省', 1) " +
            "UNION ALL " +
            "SELECT " +
            "SUBSTRING_INDEX(os.address, '市', 1) as address, " +
            "SUM(oi.quantity) as count " +
            "FROM order_shipping os " +
            "JOIN orders o ON os.order_id = o.order_id " +
            "JOIN order_items oi ON o.order_id = oi.order_id " +
            "WHERE os.address REGEXP '^[^市]+市' " +  // 匹配直辖市
            "AND os.address NOT REGEXP '^[^省]+省' " +  // 排除省份地址
            "GROUP BY SUBSTRING_INDEX(os.address, '市', 1)")
    List<Map<String, Object>> getOrderRegionDistribution();
    
    /**
     * 获取销量最高的商品TOP10
     */
    @Select("SELECT p.product_id, p.product_name, p.main_image, " +
            "SUM(oi.quantity) as total_sales " +
            "FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.product_id " +
            "GROUP BY p.product_id, p.product_name, p.main_image " +
            "ORDER BY total_sales DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getTopSellingProducts();
    
    /**
     * 获取视频标题中的关键词及其互动数据
     * 提取description中的#标签并统计点赞数、分享数、评论数和视频数量
     */
    @Select("SELECT " +
            "    REGEXP_SUBSTR(description, '#([^\\\\s#]+)') AS keyword, " +
            "    SUM(digg_count) AS total_digg_count, " +
            "    SUM(share_count) AS total_share_count, " +
            "    SUM(comment_count) AS total_comment_count, " +
            "    COUNT(*) AS video_count " +
            "FROM content " +
            "WHERE description REGEXP '#[^\\\\s#]+' " +
            "GROUP BY keyword " +
            "ORDER BY total_digg_count DESC " +
            "LIMIT 20")
    List<Map<String, Object>> getVideoKeywords();
}