package com.pet_science.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.SystemException;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.User;
import com.pet_science.service.FileService;
import com.pet_science.service.UserService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;

    @Data
    public static class FileResponse {
        private InputStream stream;
        private MediaType contentType;
    }

    @PostMapping("/upload/product/image")
    @ApiOperation(value = "上传产品图片", notes = "上传产品相关图片")
    @RequireUser
    public Result<JSONObject> uploadProductImage(@RequestParam("file") MultipartFile file) {
        return uploadImageFile(file, "images/product");
    }

    @PostMapping("/upload/userAvatar")
    @ApiOperation(value = "上传用户头像", notes = "上传用户头像")
    @RequireUser
    public Result<JSONObject> uploadUserAvatar(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        Result<JSONObject> result = uploadImageFile(file, "images/userAvatar");
        // 更新用户头像
        String url = result.getData().getString("url");
        Integer userId = JWTUtil.getUserId(token);
        User user = userService.findUserById(userId);
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // 清理旧头像
            fileService.deleteImage(avatarUrl);
        }
        // 设置新头像
        user.setAvatarUrl(url);
        userService.updateUser(user);
        return result;
    }

    @GetMapping("/images/**")
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


    public Result<JSONObject> uploadImageFile(MultipartFile file, String type) {
        JSONObject jsonObject = fileService.uploadImage(file, type);
        return Result.successResultData(jsonObject);
    }
}
