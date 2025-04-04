package com.pet_science.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet_science.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 继承BaseMapper后，基本的CRUD操作已经由MyBatis-Plus提供
}