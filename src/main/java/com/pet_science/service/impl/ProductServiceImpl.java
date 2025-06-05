package com.pet_science.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.SystemException;
import com.pet_science.mapper.ProductMapper;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.product.Product;
import com.pet_science.service.FileService;
import com.pet_science.service.ProductService;
import com.pet_science.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FileService fileService;


    @Override
    public PageResult<Product> getProductListPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();

        if(params.get("keyword") != null && StringUtils.isNotBlank(params.get("keyword").toString())){
            // 添加模糊查询条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getProductName, params.get("keyword").toString())
                    .or()
                    .like(Product::getCategory, params.get("keyword").toString()));
        }else {
            // 添加查询条件
            if (params.get("productName") != null && StringUtils.isNotBlank(params.get("productName").toString())) {
                queryWrapper.like(Product::getProductName, params.get("productName").toString());
            }

            if (params.get("category") != null && StringUtils.isNotBlank(params.get("category").toString())) {
                queryWrapper.eq(Product::getCategory, params.get("category").toString());
            }

            if (params.get("status") != null && StringUtils.isNotBlank(params.get("status").toString())) {
                queryWrapper.eq(Product::getStatus, Integer.parseInt(params.get("status").toString()));
            }
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Product::getCreatedAt);
        
        // 执行分页查询
        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> productPage = productMapper.selectPage(page, queryWrapper);
        // 返回分页结果
        return PageResult.restPage(productPage);
    }

    @Override
    public Product getProductDetail(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BaseException(404, "产品不存在");
        }
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(0); // 默认下架状态
        }
        
        Date now = new Date();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        // 插入数据
        int rows = productMapper.insert(product);
        if (rows <= 0) {
            throw new SystemException("创建产品失败");
        }
        
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        // 检查产品是否存在
        Product existingProduct = productMapper.selectById(product.getProductId());
        if (existingProduct == null) {
            throw new BaseException(404, "产品不存在");
        }

        String existMainImage = existingProduct.getMainImage();
        String newMainImage = product.getMainImage();
        
        // 如果图片列表发生变化，清理旧的图片
        String[] imagesToDelete = FileUtils.findImagesToDelete(existMainImage, newMainImage);
        boolean b = fileService.cleanupImages(imagesToDelete);
        if(!b){
            throw new SystemException("删除产品图片失败");
        }
        
        // 设置更新时间
        product.setUpdatedAt(new Date());
        
        // 更新数据
        int rows = productMapper.updateById(product);
        if (rows <= 0) {
            throw new SystemException("更新产品失败");
        }
        
        return productMapper.selectById(product.getProductId());
    }

    @Override
    @Transactional
    public boolean deleteProduct(Integer id) {
        // 检查产品是否存在
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BaseException(404, "产品不存在");
        }

        // 删除关联的图片
        String mainImage = product.getMainImage();
        if (mainImage != null && !mainImage.isEmpty()) {
            String[] imageUrls = mainImage.split(";");
            boolean b = fileService.cleanupImages(imageUrls);
            if(!b){
                throw new SystemException("删除产品图片失败");
            }
        }

        // 删除产品
        int rows = productMapper.deleteById(id);
        return rows > 0;
    }
}