package com.pet_science.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.Result;
import com.pet_science.service.FileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequireUser
public class FileController {
    
    @Autowired
    private FileService fileService;

    @PostMapping("/upload/image")
    @ApiOperation(value = "上传产品图片", notes = "上传产品相关图片")
    public Result<JSONObject> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            JSONObject result = fileService.uploadProductImage(file);
            return Result.successResultData(result);
        } catch (BaseException e) {
            e.printStackTrace();
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "上传图片失败");
        }
    }

    @PostMapping("/cleanup/image")
    @ApiOperation(value = "清理临时图片", notes = "清理未使用的临时图片")
    public Result<String> cleanupImages(@RequestBody JSONObject json) {
        try {
            JSONArray urlsArray = json.getJSONArray("urls");
            String[] urls = new String[urlsArray.size()];
            for (int i = 0; i < urlsArray.size(); i++) {
                urls[i] = urlsArray.getString(i);
            }
            boolean b = fileService.cleanupImages(urls);
            if (!b) {
                return Result.error(500, "清理临时图片失败");
            }
            return Result.successResultData("清理临时图片成功");
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "清理临时图片失败");
        }
    }
}
