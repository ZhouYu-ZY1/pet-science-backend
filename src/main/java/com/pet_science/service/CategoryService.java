package com.pet_science.service;

import com.pet_science.pojo.product.Category;
import com.pet_science.pojo.PageResult;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    
    /**
     * 分页获取分类列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<Category> getCategoryListPage(Integer pageNum, Integer pageSize, Map<String, Object> params);
    
    /**
     * 获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    Category getCategoryDetail(Integer id);
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建后的分类
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新后的分类
     */
    Category updateCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Integer id);
    
    /**
     * 获取所有分类（不分页）
     * @return 所有分类列表
     */
    List<Category> getAllCategories();
}