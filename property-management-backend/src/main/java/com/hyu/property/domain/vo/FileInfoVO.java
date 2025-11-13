package com.hyu.property.domain.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件信息VO
 */
@Data
public class FileInfoVO {

    private Long fileId;

    private String fileName;

    private String originalName;

    private String filePath;

    private String fileUrl;

    private Long fileSize;

    private String fileType;

    private String contentType;

    private String fileCategory;

    private Long uploadUserId;

    private String businessType;

    private int status;

    private String uploadTime;

    private String uploadUser;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
