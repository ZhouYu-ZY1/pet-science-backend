package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.pojo.Result;
import com.pet_science.service.DataVisualService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据可视化控制器
 */
@RestController
@RequestMapping("/dataVisual")
@Api(tags = "数据可视化接口")
public class DataVisualController {

    @Autowired
    private DataVisualService dataVisualService;

    /**
     * 获取数据可视化的简介信息
     */
    @GetMapping("/intro")
    public Result<JSONObject> getDataVisualIntro() {
        JSONObject jsonObject = dataVisualService.getPlatformOverview();
        return Result.successResultData(jsonObject);
    }

    /**
     * 获取宠物类型数量和占比
     */
    @GetMapping("/petTypeProportion")
    @ApiOperation(value = "获取宠物类型数量和占比", notes = "展示各类宠物的数量和占比")
    public Result<JSONObject> getPetTypeProportion() {
        JSONObject petTypeData = dataVisualService.getPetTypeProportion();
        return Result.successResultData(petTypeData);
    }

    /**
     * 获取各类产品销售额和占比
     */
    @GetMapping("/productCategorySales")
    @ApiOperation(value = "获取各类产品销售额和占比", notes = "展示各类产品的销售额和占比")
    public Result<List<JSONObject>> getProductCategorySales() {
        List<JSONObject> salesData = dataVisualService.getProductCategorySales();
        return Result.successResultData(salesData);
    }

    /**
     * 获取订单各地区销量分布
     */
    @GetMapping("/orderRegionDistribution")
    @ApiOperation(value = "获取订单收货地区分布", notes = "展示各地区商品销售数量分布")
    public Result<List<JSONObject>> getOrderRegionDistribution() {
        List<JSONObject> regionData = dataVisualService.getOrderRegionDistribution();
        return Result.successResultData(regionData);
    }

    /**
     * 获取销量最高的商品TOP10
     */
    @GetMapping("/topSellingProducts")
    @ApiOperation(value = "获取销量最高的商品TOP10", notes = "展示销量最高的10个商品")
    public Result<List<JSONObject>> getTopSellingProducts() {
        List<JSONObject> topProducts = dataVisualService.getTopSellingProducts();
        return Result.successResultData(topProducts);
    }
}
