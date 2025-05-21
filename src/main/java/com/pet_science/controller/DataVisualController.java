package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.pojo.Result;
import com.pet_science.service.DataVisualService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation; // 导入 ApiOperation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List; // 导入 List

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
    @RequestMapping("/intro")
    public Result<JSONObject> getDataVisualIntro() {
        JSONObject jsonObject = dataVisualService.getPlatformOverview();
        return Result.successResultData(jsonObject);
    }

    /**
     * 获取宠物类型数量和占比
     */
    @RequestMapping("/petTypeProportion")
    @ApiOperation(value = "获取宠物类型数量和占比", notes = "展示各类宠物的数量和占比")
    public Result<List<JSONObject>> getPetTypeProportion() {
        List<JSONObject> petTypeData = dataVisualService.getPetTypeProportion();
        return Result.successResultData(petTypeData);
    }
}
