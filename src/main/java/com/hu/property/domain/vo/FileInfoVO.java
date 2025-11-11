package com.hyu.property.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class FileInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储文件名
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
     * 上传者ID
     */
    private Long uploadUserId;

    /**
     * 上传者姓名
     */
    private String uploadUserName;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 文件分类（image:图片, document:文档, other:其他）
     */
    private String fileCategory;

    /**
     * 文件状态（0:正常, 1:删除）
     */
    private Integer status;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}