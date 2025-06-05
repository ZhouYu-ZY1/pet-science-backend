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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
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
    public JSONObject uploadFile(MultipartFile file,String type) {
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
            jsonObject.put("url", "/"+path);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public FileController.FileResponse getImage(HttpServletRequest request) {
        try {
            // 获取完整的路径（例如：/images/product/262efb39-0a2d-42e5-ae0f-77a5f7420443）
            String imagePath = request.getRequestURI();

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
    public FileController.FileResponse getVideo(HttpServletRequest request) {
        try {
            String videoPath = request.getRequestURI();
            
            // 获取Range请求头
            String rangeHeader = request.getHeader("Range");
            
            // 从MinIO获取对象信息
            StatObjectResponse objectStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(videoPath)
                            .build());
            
            long fileSize = objectStat.size();
            long start = 0;
            long end = fileSize - 1;
            
            // 解析Range请求头
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                try {
                    if (ranges.length > 0 && !ranges[0].isEmpty()) {
                        start = Long.parseLong(ranges[0]);
                    }
                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
                        end = Long.parseLong(ranges[1]);
                    }
                } catch (NumberFormatException e) {
                    // 忽略解析错误，使用默认范围
                }
            }
            
            // 确保范围有效
            if (start > end || start >= fileSize) {
                start = 0;
                end = fileSize - 1;
            }
            if (end >= fileSize) {
                end = fileSize - 1;
            }
            
            // 从MinIO获取指定范围的文件流
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(videoPath)
                            .offset(start)
                            .length(end - start + 1)
                            .build());
            
            // 确定Content-Type
            MediaType contentType = getVideoContentType(videoPath);
            
            FileController.FileResponse fileResponse = new FileController.FileResponse();
            fileResponse.setStream(stream);
            fileResponse.setContentType(contentType);
            fileResponse.setFileSize(fileSize);
            fileResponse.setStart(start);
            fileResponse.setEnd(end);
            fileResponse.setRangeRequest(rangeHeader != null);
            
            return fileResponse;
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("视频获取失败");
        }
    }

    private MediaType getVideoContentType(String videoPath) {
        if (videoPath.endsWith(".mp4")) {
            return MediaType.valueOf("video/mp4");
        } else if (videoPath.endsWith(".avi")) {
            return MediaType.valueOf("video/x-msvideo");
        } else if (videoPath.endsWith(".mov")) {
            return MediaType.valueOf("video/quicktime");
        } else if (videoPath.endsWith(".wmv")) {
            return MediaType.valueOf("video/x-ms-wmv");
        } else if (videoPath.endsWith(".flv")) {
            return MediaType.valueOf("video/x-flv");
        } else if (videoPath.endsWith(".webm")) {
            return MediaType.valueOf("video/webm");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public boolean deleteImage(String url) {
        try {
            // 删除指定的对象
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(url) // 直接使用传入的URL
                            .build());
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean cleanupImages(String[] urls) {
        try {
            for (String url : urls) {
                deleteImage(url);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
