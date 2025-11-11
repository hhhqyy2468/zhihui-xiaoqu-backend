package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公告数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class NoticeDTO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题最多200个字符")
    private String noticeTitle;

    /**
     * 公告类型
     */
    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String noticeContent;

    /**
     * 附件URLs
     */
    private String attachmentUrls;

    /**
     * 是否置顶：0-否 1-是
     */
    private Integer isTop;

    /**
     * 发布范围：1-全部 2-指定楼栋 3-指定单元
     */
    @NotNull(message = "发布范围不能为空")
    private Integer publishScope;

    /**
     * 目标楼栋IDs（逗号分隔）
     */
    private String targetBuildingIds;

    /**
     * 目标单元IDs（逗号分隔）
     */
    private String targetUnitIds;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEndTime;
}