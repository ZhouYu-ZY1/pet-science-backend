package com.pet_science.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.pojo.Pet;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.content.Content;
import com.pet_science.pojo.user.User;
import com.pet_science.service.ContentService;
import com.pet_science.service.FileService;
import com.pet_science.service.PetService;
import com.pet_science.service.UserService;
import com.pet_science.utils.FileUtils;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Api(tags = "文件接口")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    @Autowired
    private ContentService contentService;

    @Data
    public static class FileResponse {
        private InputStream stream;
        private MediaType contentType;
        private long fileSize;
        private long start;
        private long end;
        private boolean rangeRequest;
    }

    @PostMapping("/upload/product/image")
    @ApiOperation(value = "上传产品图片", notes = "上传产品相关图片")
    @RequireUser
    public Result<JSONObject> uploadProductImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "images/product");
    }

    @PostMapping("/upload/userAvatar")
    @ApiOperation(value = "上传用户头像", notes = "上传用户头像")
    @RequireUser
    public Result<JSONObject> uploadUserAvatar(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        Result<JSONObject> result = uploadFile(file, "images/userAvatar");
        // 更新用户头像
        String url = result.getData().getString("url");
        Integer userId = JWTUtil.getUserId(token);
        User user = userService.findUserById(userId);
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.startsWith("/images/default/")) {
            // 清理旧头像
            fileService.deleteImage(avatarUrl);
        }
        // 设置新头像
        user.setAvatarUrl(url);
        userService.updateUser(user);
        return result;
    }

    @PostMapping("/upload/petAvatar")
    @ApiOperation(value = "上传宠物头像", notes = "上传宠物头像")
    @RequireUser
    public Result<JSONObject> uploadPetAvatar(@RequestParam("file") MultipartFile file,@RequestParam("petId") Long petId, @RequestHeader("Authorization") String token) {
        Result<JSONObject> result = uploadFile(file, "images/petAvatar");
        // 更新用户头像
        String url = result.getData().getString("url");
        Integer userId = JWTUtil.getUserId(token);
        Pet pet = petService.getPetDetail(petId,userId);
        String avatarUrl = pet.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.startsWith("/images/default/")) {
            // 清理旧头像
            fileService.deleteImage(avatarUrl);
        }
        // 设置新头像
        pet.setAvatarUrl(url);
        petService.updatePet(pet);
        return result;
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload/video/userWorks")
    @ApiOperation(value = "上传用户视频作品", notes = "上传用户视频作品")
    @RequireUser
    public Result<JSONObject> uploadVideo(@RequestParam("file") MultipartFile file,@RequestParam("desc")String desc, @RequestHeader("Authorization") String token) throws Exception{
        Result<JSONObject> videoResult = uploadFile(file, "video/userWorks");


        Integer userId = JWTUtil.getUserId(token);


        String videoUrl = videoResult.getData().getString("url");

        // 从视频中提取封面
        MultipartFile coverFile = FileUtils.extractVideoCover(file);
        String coverUrl = "";
        if (coverFile != null) {
            Result<JSONObject> coverResult = uploadFile(coverFile, "images/videoCover");
            coverUrl = coverResult.getData().getString("url");
        }


        Content content = new Content();
        content.setVideoSrc(videoUrl);
        content.setCoverSrc(coverUrl);
        content.setDesc(desc);
        if(contentService.updateUserContent(content,userId) == 0) {
            throw new BusinessException("上传视频失败");
        }
        return videoResult;
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

    @GetMapping("/video/**")
    @ApiOperation(value = "获取视频")
    public ResponseEntity<?> getVideo(HttpServletRequest request) {
        // 获取视频流
        FileResponse fileResponse = fileService.getVideo(request);
        
        ResponseEntity.BodyBuilder responseBuilder;
        
        if (fileResponse.isRangeRequest()) {
            // 支持Range请求，返回206状态码
            responseBuilder = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT);
            
            // 设置Content-Range头
            String contentRange = String.format("bytes %d-%d/%d", 
                    fileResponse.getStart(), 
                    fileResponse.getEnd(), 
                    fileResponse.getFileSize());
            responseBuilder.header("Content-Range", contentRange);
            
            // 设置Content-Length为实际传输的字节数
            long contentLength = fileResponse.getEnd() - fileResponse.getStart() + 1;
            responseBuilder.header("Content-Length", String.valueOf(contentLength));
        } else {
            // 普通请求，返回200状态码
            responseBuilder = ResponseEntity.ok();
            responseBuilder.header("Content-Length", String.valueOf(fileResponse.getFileSize()));
        }
        
        // 设置支持Range请求的头部
        responseBuilder.header("Accept-Ranges", "bytes");
        
        // 设置缓存控制
        responseBuilder.header("Cache-Control", "public, max-age=3600");
        
        // 返回视频流
        return responseBuilder
                .contentType(fileResponse.getContentType())
                .body(new InputStreamResource(fileResponse.getStream()));
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


    public Result<JSONObject> uploadFile(MultipartFile file, String type) {
        JSONObject jsonObject = fileService.uploadFile(file, type);
        return Result.successResultData(jsonObject);
    }
}
