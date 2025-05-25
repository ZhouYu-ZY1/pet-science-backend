package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 数据可视化服务接口
 */
public interface DataVisualService {

    /**
     * 获取平台概览数据
     * @return 包含平台概览数据的JSON对象
     */
    JSONObject getPlatformOverview();

    /**
     * 获取宠物类型数量和占比
     * @return 包含宠物类型数量和占比的JSON列表
     */
    JSONObject getPetTypeProportion();

    /**
     * 获取各类产品销售额和占比
     * @return 包含产品类别销售额和占比的JSON列表
     */
    List<JSONObject> getProductCategorySales();

    /**
     * 获取订单收货地区分布
     */
    List<JSONObject> getOrderRegionDistribution();

    /**
     * 获取销量最高的商品TOP10
     */
    List<JSONObject> getTopSellingProducts();
}