package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    /**
     * 上传产品图片
     * @param file 上传的文件
     * @return 包含图片URL的JSON对象
     */
    JSONObject uploadProductImage(MultipartFile file);
    
    /**
     * 清理临时图片
     * @param urls 需要清理的图片URL列表
     * @return 清理结果信息
     */
    boolean cleanupImages(String[] urls);
}