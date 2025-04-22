package com.pet_science.service;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.controller.FileController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FileService {
    /**
     * 上传产品图片
     *
     * @param file 上传的文件
     * @param type 图片类型（如产品、用户等）
     * @return 包含图片URL的JSON对象
     */
    JSONObject uploadImage(MultipartFile file, String type);

    /**
     * 获取图片
     * @param request HTTP请求对象
     */
    FileController.FileResponse getImage(HttpServletRequest request);

    /**
     * 清理临时图片
     * @param urls 需要清理的图片URL列表
     * @return 清理结果信息
     */
    boolean cleanupImages(String[] urls);
}