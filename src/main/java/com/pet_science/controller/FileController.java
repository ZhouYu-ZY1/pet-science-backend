package com.pet_science.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.pojo.Result;
import com.pet_science.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;
    @Data
    public static class FileResponse {
        private InputStream stream;
        private MediaType contentType;
    }

    @PostMapping("/upload/product/image")
    @ApiOperation(value = "上传产品图片", notes = "上传产品相关图片")
    @RequireUser
    public Result<JSONObject> uploadProductImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "product/image");
    }

    @GetMapping("/image/**")
    @ApiOperation(value = "获取图片")
    public ResponseEntity<?> getImage(HttpServletRequest request) {
        // 获取图片流
        FileResponse fileResponse = fileService.getImage(request);
        // 返回 MinIO 存储的图片
        return ResponseEntity.ok()
                .contentType(fileResponse.contentType)
                .body(new InputStreamResource(fileResponse.stream));
    }

    @PostMapping("/cleanup/image")
    @RequireUser
    @ApiOperation(value = "清理临时图片", notes = "清理未使用的临时图片")
    public Result<String> cleanupImages(@RequestBody JSONObject json) {
        JSONArray urlsArray = json.getJSONArray("urls");
        String[] urls = new String[urlsArray.size()];
        for (int i = 0; i < urlsArray.size(); i++) {
            urls[i] = urlsArray.getString(i);
        }

        boolean b = fileService.cleanupImages(urls);
        if (b) {
            return Result.successResultData("清理临时图片成功");
        }
        throw new SystemException("清理临时图片失败");
    }


    public Result<JSONObject> uploadFile(MultipartFile file,String type) {
        JSONObject jsonObject = fileService.uploadImage(file, type);
        return Result.successResultData(jsonObject);
    }
}
