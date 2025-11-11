package com.hyu.property.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件上传结果传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class UploadResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件MIME类型
     */
    private String contentType;

    /**
     * 文件分类（image:图片, document:文档, other:其他）
     */
    private String fileCategory;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}