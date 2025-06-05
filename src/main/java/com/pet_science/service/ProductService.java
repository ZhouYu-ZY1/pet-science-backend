package com.pet_science.service;

import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.product.Product;

import java.util.Map;

public interface ProductService {
    
    /**
     * 分页获取产品列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<Product> getProductListPage(Integer pageNum, Integer pageSize, Map<String, Object> params);
    
    /**
     * 获取产品详情
     * @param id 产品ID
     * @return 产品详情
     */
    Product getProductDetail(Integer id);
    
    /**
     * 创建产品
     * @param product 产品信息
     * @return 创建后的产品
     */
    Product createProduct(Product product);
    
    /**
     * 更新产品
     * @param product 产品信息
     * @return 更新后的产品
     */
    Product updateProduct(Product product);
    
    /**
     * 删除产品
     * @param id 产品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Integer id);
}