package com.pet_science.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.controller.FileController;
import com.pet_science.exception.BaseException;
import com.pet_science.exception.BusinessException;
import com.pet_science.service.FileService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;
    private final String bucketName = "pet"; // MinIO存储桶名

    @Override
    public JSONObject uploadImage(MultipartFile file,String type) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        try {
            // 确保存储桶存在
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 获取文件名后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = null;
            if (originalFilename != null) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // 随机生成文件名
            String fileName = UUID.randomUUID() + suffix;
            // 安全检查，去除文件名中的分号
            fileName = fileName.replace(";","");

            String path = type + "/" + fileName;
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            JSONObject jsonObject = new JSONObject();
            // 返回访问唯一标识
            jsonObject.put("url", "/image/"+path);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public FileController.FileResponse getImage(HttpServletRequest request) {
        try {
            // 获取完整的路径（例如：/image/product/image/262efb39-0a2d-42e5-ae0f-77a5f7420443）
            String fullPath = request.getRequestURI();
            // 提取 /image/ 之后的部分
            String imagePath = fullPath.substring("/image/".length());

            // 确定Content-Type
            MediaType contentType = MediaType.IMAGE_JPEG; // 默认设为JPEG
            if (imagePath.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG;
            } else if (imagePath.endsWith(".gif")) {
                contentType = MediaType.IMAGE_GIF;
            }

            // 从MinIO获取文件流
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imagePath)  // 直接使用提取的路径
                            .build());

            FileController.FileResponse fileResponse = new FileController.FileResponse();
            fileResponse.setStream(stream);
            fileResponse.setContentType(contentType);
            return fileResponse;
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("图片获取失败");
        }
    }

    @Override
    public boolean cleanupImages(String[] urls) {
        try {
            for (String url : urls) {
                url = url.replaceFirst("/image/", ""); // 去除前缀
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(url) // 直接使用传入的URL
                                .build());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}