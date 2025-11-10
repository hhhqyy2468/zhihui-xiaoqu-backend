package com.hyu.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtils {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private long maxSize;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问路径
     * @throws IOException IO异常
     */
    public String upload(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new IOException("文件大小超过限制");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!isAllowedType(extension)) {
            throw new IOException("不支持的文件类型: " + extension);
        }

        // 创建上传目录
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String newFilename = generateUniqueFilename(extension);
        Path filePath = uploadDir.resolve(newFilename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        // 返回文件访问路径
        return "/upload/" + newFilename;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean deleteFile(String filePath) {
        try {
            // 移除访问路径前缀，只保留文件名
            String filename = filePath.replace("/upload/", "");
            Path path = Paths.get(uploadPath, filename);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查文件类型是否允许
     *
     * @param extension 文件扩展名
     * @return 是否允许
     */
    private boolean isAllowedType(String extension) {
        String[] allowedTypeArray = allowedTypes.split(",");
        return Arrays.asList(allowedTypeArray).contains(extension);
    }

    /**
     * 生成唯一文件名
     *
     * @param extension 文件扩展名
     * @return 唯一文件名
     */
    private String generateUniqueFilename(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    public boolean fileExists(String filePath) {
        try {
            String filename = filePath.replace("/upload/", "");
            Path path = Paths.get(uploadPath, filename);
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public long getFileSize(String filePath) {
        try {
            String filename = filePath.replace("/upload/", "");
            Path path = Paths.get(uploadPath, filename);
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }
}