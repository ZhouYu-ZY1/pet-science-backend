package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Product;
import com.pet_science.pojo.Result;
import com.pet_science.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/product")
@Api(tags = "产品接口")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    @ApiOperation(value = "获取产品列表", notes = "支持按产品名称、类别、状态等筛选，支持分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer"),
        @ApiImplicitParam(name = "name", value = "产品名称", dataType = "String"),
        @ApiImplicitParam(name = "category", value = "产品类别", dataType = "String"),
        @ApiImplicitParam(name = "status", value = "状态", dataType = "Integer")
    })
    public Result<PageResult<Product>> getProductList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            // 获取分页参数
            Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            // 调用服务层方法获取分页数据
            PageResult<Product> pageResult = productService.getProductListPage(pageNum, pageSize, params);
            return Result.successResultData(pageResult);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取产品列表失败");
        }
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取产品详情", notes = "根据产品ID获取产品详细信息")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Product> getProductDetail(@PathVariable("id") Integer id) {
        try {
            Product product = productService.getProductDetail(id);
            return Result.successResultData(product);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取产品详情失败");
        }
    }
    
    @PostMapping
    @ApiOperation(value = "创建产品", notes = "创建新产品")
    public Result<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return Result.successResultData(createdProduct);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "创建产品失败");
        }
    }
    
    @PutMapping("/{productId}")
    @ApiOperation(value = "更新产品", notes = "更新指定产品的信息")
    @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataType = "Integer", paramType = "path")
    public Result<Product> updateProduct(@PathVariable Integer productId, @RequestBody Product product) {
        try {
            product.setProductId(productId);
            Product updatedProduct = productService.updateProduct(product);
            return Result.successResultData(updatedProduct);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "更新产品失败");
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除产品", notes = "删除指定产品")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Integer", paramType = "path")
    public Result<String> deleteProduct(@PathVariable("id") Integer id) {
        try {
            boolean result = productService.deleteProduct(id);
            if (result) {
                return Result.successResultData("删除产品成功");
            } else {
                return Result.error(400, "删除产品失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "删除产品失败");
        }
    }
}