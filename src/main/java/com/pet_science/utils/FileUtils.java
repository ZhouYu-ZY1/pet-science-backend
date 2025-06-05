package com.pet_science.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class FileUtils {

    /**
     *  获取视频封面
     */
    /**
     *  获取视频封面
     */
    public static MultipartFile extractVideoCover(MultipartFile videoFile) throws IOException {
        // 获取原始文件扩展名
        String originalFilename = videoFile.getOriginalFilename();
        String extension = ".mp4";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 创建临时文件，使用原始文件扩展名
        File tempFile = File.createTempFile("video"+UUID.randomUUID(), extension);
        videoFile.transferTo(tempFile);
        
        try {
            // 使用JavaCV打开视频文件
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile);
            // 设置一些参数以提高兼容性
            grabber.setOption("analyzeduration", "2147483647");
            grabber.setOption("probesize", "2147483647");
            grabber.start();
            
            // 获取视频总帧数
            int totalFrames = grabber.getLengthInFrames();
            if (totalFrames <= 0) {
                throw new IOException("无法获取视频帧，视频可能已损坏或格式不支持");
            }
            
            // 尝试获取视频中间的一帧作为封面（通常更有代表性）
            int middleFrame = totalFrames / 2;
            grabber.setFrameNumber(middleFrame);
            Frame frame = grabber.grab();
            
            // 如果中间帧获取失败，尝试获取第10帧
            if (frame == null || frame.image == null) {
                grabber.setFrameNumber(Math.min(10, totalFrames - 1));
                frame = grabber.grab();
            }

            // 如果还是失败，尝试获取第一帧
            if (frame == null || frame.image == null) {
                grabber.setFrameNumber(0);
                frame = grabber.grab();
            }
            
            // 如果所有尝试都失败，尝试顺序读取帧直到找到有效的图像帧
            if (frame == null || frame.image == null) {
                grabber.setFrameNumber(0);
                for (int i = 0; i < Math.min(30, totalFrames); i++) {
                    frame = grabber.grab();
                    if (frame != null && frame.image != null) {
                        break;
                    }
                }
            }
            
            // 转换为Java BufferedImage
            BufferedImage image = null;
            if (frame != null && frame.image != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                image = converter.convert(frame);
            }
            
            // 关闭资源
            grabber.stop();
            grabber.release();
            
            // 如果没有获取到图像，抛出异常
            if (image == null) {
                throw new IOException("视频处理异常，无法提取封面，视频可能不包含有效的视频流");
            }
            
            // 将BufferedImage转换为MultipartFile
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            
            // 将ByteArrayOutputStream转换为ByteArrayInputStream
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            
            // 创建MultipartFile对象
            return new MultipartFile() {
                @NotNull
                @Override
                public String getName() {
                    return videoFile.getName();
                }
                
                @Override
                public String getOriginalFilename() {
                    return UUID.randomUUID() + ".jpg";
                }
                
                @Override
                public String getContentType() {
                    return "image/jpeg";
                }
                
                @Override
                public boolean isEmpty() {
                    return baos.size() == 0;
                }
                
                @Override
                public long getSize() {
                    return baos.size();
                }
                
                @NotNull
                @Override
                public byte[] getBytes() {
                    return baos.toByteArray();
                }
                
                @NotNull
                @Override
                public InputStream getInputStream() {
                    return bais;
                }
                
                @Override
                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(baos.toByteArray());
                    }
                }
            };
        } catch (Exception e) {
            // 删除临时文件
            tempFile.delete();
            throw new IOException("视频处理异常: " + e.getMessage(), e);
        } finally {
            if(tempFile.exists()){
                tempFile.delete();
            }
        }
    }

    public static String[] findImagesToDelete(String oldImages, String newImages) {
        if (oldImages == null || newImages == null) {
            return new String[0];  // 返回空数组而不是空列表
        }

        String[] oldUrls = Arrays.stream(oldImages.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        String[] newUrls = Arrays.stream(newImages.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        Set<String> newUrlSet = new HashSet<>(Arrays.asList(newUrls));
        List<String> toDelete = new ArrayList<>();

        for (String oldUrl : oldUrls) {
            if (!newUrlSet.contains(oldUrl)) {
                toDelete.add(oldUrl);
            }
        }

        // 将List转换为数组返回
        return toDelete.toArray(new String[0]);
    }
}
