package com.pet_science.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.mapper.CategoryMapper;
import com.pet_science.pojo.Category;
import com.pet_science.pojo.PageResult;
import com.pet_science.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<Category> getCategoryListPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (params.get("categoryName") != null && StringUtils.isNotBlank(params.get("categoryName").toString())) {
            queryWrapper.like(Category::getCategoryName, params.get("categoryName").toString());
        }
        
        // 执行分页查询
        Page<Category> page = new Page<>(pageNum, pageSize);
        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);

        // 返回分页结果
        return PageResult.restPage(categoryPage);
    }

    @Override
    public Category getCategoryDetail(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BaseException(404, "分类不存在");
        }
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, category.getCategoryName());
        if (categoryMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }
        
        // 插入数据
        int rows = categoryMapper.insert(category);
        if (rows <= 0) {
            throw new SystemException("创建分类失败");
        }
        
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        // 检查分类是否存在
        Category existingCategory = categoryMapper.selectById(category.getCategoryId());
        if (existingCategory == null) {
            throw new BaseException(404, "分类不存在");
        }
        
        // 检查分类名称是否已存在（排除自身）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, category.getCategoryName())
                .ne(Category::getCategoryId, category.getCategoryId());
        if (categoryMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }
        
        // 更新数据
        int rows = categoryMapper.updateById(category);
        if (rows <= 0) {
            throw new SystemException("更新分类失败");
        }
        
        return categoryMapper.selectById(category.getCategoryId());
    }

    @Override
    @Transactional
    public boolean deleteCategory(Integer id) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BaseException(404, "分类不存在");
        }
        
        // 删除分类
        int rows = categoryMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    public List<Category> getAllCategories() {
        // 查询所有分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        return categoryMapper.selectList(queryWrapper);
    }
}