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
}