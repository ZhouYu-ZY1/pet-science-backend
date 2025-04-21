package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.BusinessException;
import com.pet_science.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.image_upload_path}")
    private String uploadPath;

//    @Value("${upload.image_upload_url}")
//    private String uploadUrl;

    @Override
    public JSONObject uploadProductImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = null;
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成新文件名
        String fileName = UUID.randomUUID() + suffix;
        fileName = fileName.replace(";",""); // 去除文件名中的分号

        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        try {
            File destFile = new File(uploadPath + File.separator + fileName);
            file.transferTo(destFile);

            // 检查文件是否存在
            if (!destFile.exists()) {
                throw new BaseException(500, "文件上传失败");
            }
            // 返回访问URL
            String imageUrl = uploadPath + "/" + fileName;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", imageUrl);
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "文件上传失败");
        }
    }

    @Override
    public boolean cleanupImages(String[] urls) {
        try {
            for (String url : urls) {
                url = url.replace(uploadPath + "/", uploadPath + File.separator);
                File file = new File(url);
                if (file.exists()) {
                    file.delete();
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}