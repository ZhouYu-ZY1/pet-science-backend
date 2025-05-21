package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.mapper.DataVisualMapper;
import com.pet_science.service.DataVisualService;
import com.pet_science.utils.UserActivityTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList; // 导入 ArrayList
import java.util.List;
import java.util.Map; // 导入 Map

/**
 * 数据可视化服务实现类
 */
@Service
public class DataVisualServiceImpl implements DataVisualService {

    @Autowired
    private DataVisualMapper dataVisualMapper;

    @Autowired
    private UserActivityTracker trackUserActivity;

    /**
     * 获取平台概览数据
     */
    @Override
    public JSONObject getPlatformOverview() {
        JSONObject jsonObject = new JSONObject();
        
        // 平台简介
        jsonObject.put("introduction", "萌宠视界平台致力于为宠物爱好者提供全方位的宠物服务，包括宠物商城、宠物AI问答、宠物社区（图文发布、实时通讯）等功能。我们通过线上线为用户提供便捷的宠物用品购买渠道、专业的宠物咨询、以及宠物社区交流平台。平台汇集了众多宠物专家和爱宠人士，共同打造温馨和谐的宠物生态圈，让每一位宠物主人和他们的爱宠都能享受高品质的服务体验。");
        // 获取数据
        int registeredUsers = dataVisualMapper.getRegisteredUsersCount();
        int totalPets = dataVisualMapper.getTotalPetsCount();
        int totalOrders = dataVisualMapper.getTotalOrdersCount();
        double annualTransactionValue = dataVisualMapper.getAnnualTransactionValue();
//        int activeDailyUsers = dataVisualMapper.getActiveDailyUsers();
        // 格式化年交易额（单位：万元）
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedAnnualValue = df.format(annualTransactionValue / 10000) + "万";
        // 设置数据
        jsonObject.put("registeredUsers", registeredUsers);
        jsonObject.put("totalPets", totalPets);
        jsonObject.put("totalOrders", totalOrders);
        jsonObject.put("annualTransactionValue", formattedAnnualValue);
        jsonObject.put("activeDailyUsers", trackUserActivity.getDailyActiveUserCount());
        jsonObject.put("activeWeeklyUsers", trackUserActivity.getWeeklyActiveUserCount());
        jsonObject.put("activeMonthlyUsers", trackUserActivity.getMonthlyActiveUserCount());

        return jsonObject;
    }

    /**
     * 获取宠物类型数量和占比
     */
    @Override
    public List<JSONObject> getPetTypeProportion() {
        List<Map<String, Object>> petTypeCounts = dataVisualMapper.getPetTypeCounts();
        long totalPets = dataVisualMapper.getTotalPetsCount(); // 获取宠物总数

        List<JSONObject> resultList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00%"); // 格式化为百分比

        for (Map<String, Object> countData : petTypeCounts) {
            JSONObject jsonObject = new JSONObject();
            String petType = (String) countData.get("type");
            // 处理宠物类型名称
            switch (petType){
                case "dog" -> petType = "狗狗";
                case "cat" -> petType = "猫咪";
                case "fish" -> petType = "鱼类";
                case "bird" -> petType = "鸟类";
                case "reptile" -> petType = "爬行动物";
                case "rabbit" -> petType = "兔子";
                case "hamster" -> petType = "仓鼠";
                case "other" -> petType = "其他";
            }
            Long count = (Long) countData.get("count");
            jsonObject.put("name", petType);
            jsonObject.put("value", count);
            // 计算占比
            if (totalPets > 0) {
                double proportion = (double) count / totalPets;
                jsonObject.put("proportion", df.format(proportion));
            } else {
                jsonObject.put("proportion", "0.00%");
            }
            resultList.add(jsonObject);
        }

        return resultList;
    }
}