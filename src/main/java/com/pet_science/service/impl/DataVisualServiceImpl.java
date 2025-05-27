package com.pet_science.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.mapper.DataVisualMapper;
import com.pet_science.service.DataVisualService;
import com.pet_science.utils.UserActivityTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public JSONObject getPetTypeProportion() {
        List<Map<String, Object>> petTypeCounts = dataVisualMapper.getPetTypeCounts();
        long totalPets = dataVisualMapper.getTotalPetsCount(); // 获取宠物总数
        
        // 创建返回结果对象
        JSONObject result = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        
        // 初始化猫和狗的计数
        long catCount = 0;
        long dogCount = 0;
        long otherCount = 0;

        DecimalFormat df = new DecimalFormat("0.00%"); // 格式化为百分比

        for (Map<String, Object> countData : petTypeCounts) {
            JSONObject jsonObject = new JSONObject();
            String petType = (String) countData.get("type");
            Long count = (Long) countData.get("count");
            
            // 统计猫和狗的数量
            if ("cat".equals(petType)) {
                catCount = count;
            } else if ("dog".equals(petType)) {
                dogCount = count;
            }else {
                otherCount += count;
            }
            
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
            
            jsonObject.put("name", petType);
            jsonObject.put("value", count);
            // 计算占比
            if (totalPets > 0) {
                double proportion = (double) count / totalPets;
                jsonObject.put("proportion", df.format(proportion));
            } else {
                jsonObject.put("proportion", "0.00%");
            }
            list.add(jsonObject);
        }
        
        // 添加总数据到结果中
        result.put("totalPets", totalPets);
        result.put("catCount", catCount);
        result.put("dogCount", dogCount);
        result.put("otherCount", otherCount);
        result.put("list", list);

        return result;
    }


    /**
     * 获取各地区用户数量
     */
    @Override
    public List<JSONObject> getUserRegionNumber() {
//        List<Map<String, Object>> categorySales = dataVisualMapper.getProductCategorySales();
//        double totalSales = dataVisualMapper.getTotalSalesAmount();

        List<JSONObject> resultList = new ArrayList<>();
        JSONArray jsonArray;
        try {
            String filePath = "src/main/resources/data/userRegionNumber.json";
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonArray = JSON.parseArray(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理文件读取异常，可以返回空列表或记录错误
            return resultList;
        }
//        DecimalFormat df = new DecimalFormat("0.00%");
//        DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
//            JSONObject jsonObject = new JSONObject();
//            String category = (String) salesData.get("category_name");
//
//            Double salesAmount = ((Number) salesData.get("sales_amount")).doubleValue();
//            Long orderCount = ((Number) salesData.get("order_count")).longValue();
//
//            jsonObject.put("name", category);
//            jsonObject.put("value", moneyFormat.format(salesAmount));
//            jsonObject.put("orderCount", orderCount);
//
//            // 计算占比
//            if (totalSales > 0) {
//                double proportion = salesAmount / totalSales;
//                jsonObject.put("proportion", df.format(proportion));
//            } else {
//                jsonObject.put("proportion", "0.00%");
//            }
            resultList.add(jsonObject);
        }

        return resultList;
    }


    /**
     * 获取各类产品销售额和占比
     */
    @Override
    public List<JSONObject> getProductCategorySales() {
//        List<Map<String, Object>> categorySales = dataVisualMapper.getProductCategorySales();
//        double totalSales = dataVisualMapper.getTotalSalesAmount();

        List<JSONObject> resultList = new ArrayList<>();
        JSONArray jsonArray;
        try {
            // 从文件中读取地图数据
            String filePath = "src/main/resources/data/productTypeProportion.json";
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonArray = JSON.parseArray(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理文件读取异常，可以返回空列表或记录错误
            return resultList;
        }
//        DecimalFormat df = new DecimalFormat("0.00%");
//        DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
//            JSONObject jsonObject = new JSONObject();
//            String category = (String) salesData.get("category_name");
//
//            Double salesAmount = ((Number) salesData.get("sales_amount")).doubleValue();
//            Long orderCount = ((Number) salesData.get("order_count")).longValue();
//
//            jsonObject.put("name", category);
//            jsonObject.put("value", moneyFormat.format(salesAmount));
//            jsonObject.put("orderCount", orderCount);
//
//            // 计算占比
//            if (totalSales > 0) {
//                double proportion = salesAmount / totalSales;
//                jsonObject.put("proportion", df.format(proportion));
//            } else {
//                jsonObject.put("proportion", "0.00%");
//            }
            resultList.add(jsonObject);
        }

        return resultList;
    }

    /**
     * 获取订单收货地区分布
     */
    @Override
    public List<JSONObject> getOrderRegionDistribution() {
//        List<Map<String, Object>> regionData = dataVisualMapper.getOrderRegionDistribution();
        List<JSONObject> resultList = new ArrayList<>();

        // 从文件中读取地图数据
        JSONArray jsonArray;
        try {
            // 从文件中读取地图数据
            String filePath = "src/main/resources/data/orderRegionDistribution.json";
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonArray = JSON.parseArray(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理文件读取异常，可以返回空列表或记录错误
            return resultList;
        }

        for (Object object: jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
//            String province = (String) data.get("address");
//            Long count = ((Number) data.get("count")).longValue();
//
//            // 处理省份名称，确保与地图数据匹配
//            if (province != null) {
//                // 移除"省"、"自治区"、"市"等后缀
//                if (province.endsWith("省") || province.endsWith("市")) {
//                    province = province.substring(0, province.length() - 1);
//                } else if (province.contains("自治区")) {
//                    province = province.substring(0, province.indexOf("自治区"));
//                }
//            }
//
//            jsonObject.put("name", province);
//            jsonObject.put("value", count);
            resultList.add(jsonObject);
        }
        
        return resultList;
    }

    /**
     * 获取销量最高的商品TOP10
     */
    @Override
    public List<JSONObject> getTopSellingProducts() {
        List<Map<String, Object>> topProducts = dataVisualMapper.getTopSellingProducts();
        List<JSONObject> resultList = new ArrayList<>();
        
        for (Map<String, Object> product : topProducts) {
            JSONObject jsonObject = new JSONObject();
            String productName = (String) product.get("product_name");
            String productImage = (String) product.get("main_image");
            productImage = productImage.split(";")[0]; // 取第一个图片
            Long totalSales = ((Number) product.get("total_sales")).longValue();
            
            jsonObject.put("name", productName);
            jsonObject.put("value", totalSales);
            jsonObject.put("image", productImage);
            
            resultList.add(jsonObject);
        }
        
        return resultList;
    }
}