package com.pet_science.controller;

import com.pet_science.exception.BaseException;
import com.pet_science.pojo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileController {
    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url}")
    private String uploadUrl;

    @PostMapping("/upload/image")
    @ApiOperation(value = "上传产品图片", notes = "上传产品相关图片")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BaseException(400, "上传文件不能为空");
            }

            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = null;
            if (originalFilename != null) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名
            String fileName = UUID.randomUUID() + suffix;

            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String imageUrl;
            // 保存文件
            try {
                File destFile = new File(uploadPath + File.separator + fileName);
                file.transferTo(destFile);
                // 返回访问URL
                imageUrl = uploadUrl + "/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BaseException(500, "文件上传失败");
            }
            return Result.successResultData(imageUrl);
        } catch (BaseException e) {
            e.printStackTrace();
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "上传图片失败");
        }
    }
}
