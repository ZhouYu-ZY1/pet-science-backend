package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.BusinessException;
import com.pet_science.pojo.Category;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Result;
import com.pet_science.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@RequireUser
@Api(tags = "产品分类接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation(value = "获取分类列表", notes = "支持按分类名称筛选，支持分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer"),
        @ApiImplicitParam(name = "categoryName", value = "分类名称", dataType = "String")
    })
    public Result<PageResult<Category>> getCategoryList(@RequestParam(required = false) Map<String, Object> params) {
        // 获取分页参数
        Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        
        // 调用服务层方法获取分页数据
        PageResult<Category> pageResult = categoryService.getCategoryListPage(pageNum, pageSize, params);
        return Result.successResultData(pageResult);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取分类详情", notes = "根据分类ID获取分类详细信息")
    @ApiImplicitParam(name = "id", value = "分类ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Category> getCategoryDetail(@PathVariable("id") Integer id) {
        Category category = categoryService.getCategoryDetail(id);
        return Result.successResultData(category);
    }
    
    @PostMapping
    @ApiOperation(value = "创建分类", notes = "创建新分类")
    public Result<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return Result.successResultData(createdCategory);
    }
    
    @PutMapping("/{categoryId}")
    @ApiOperation(value = "更新分类", notes = "更新指定分类的信息")
    @ApiImplicitParam(name = "categoryId", value = "分类ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Category> updateCategory(@PathVariable Integer categoryId, @RequestBody Category category) {
        category.setCategoryId(categoryId);
        Category updatedCategory = categoryService.updateCategory(category);
        return Result.successResultData(updatedCategory);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除分类", notes = "删除指定分类")
    @ApiImplicitParam(name = "id", value = "分类ID", required = true, dataType = "Integer", paramType = "path")
    public Result<String> deleteCategory(@PathVariable("id") Integer id) {
        boolean result = categoryService.deleteCategory(id);
        if (result) {
            return Result.successResultData("删除分类成功");
        }
        throw new BusinessException("删除分类失败");
    }
    
    @GetMapping("/all")
    @ApiOperation(value = "获取所有分类", notes = "获取所有分类，不分页")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.successResultData(categories);
    }
}