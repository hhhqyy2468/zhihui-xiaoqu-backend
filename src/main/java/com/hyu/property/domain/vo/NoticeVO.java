package com.hyu.property.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class NoticeVO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型
     */
    private String noticeType;

    /**
     * 公告类型名称
     */
    private String noticeTypeName;

    /**
     * 公告内容
     */
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
    private Integer publishScope;

    /**
     * 发布范围名称
     */
    private String publishScopeName;

    /**
     * 目标楼栋IDs
     */
    private String targetBuildingIds;

    /**
     * 目标楼栋名称列表
     */
    private String targetBuildingNames;

    /**
     * 目标单元IDs
     */
    private String targetUnitIds;

    /**
     * 目标单元名称列表
     */
    private String targetUnitNames;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEndTime;

    /**
     * 公告状态：1-已发布 2-已过期 3-已撤回
     */
    private Integer noticeStatus;

    /**
     * 公告状态名称
     */
    private String noticeStatusName;

    /**
     * 发布人ID
     */
    private Long publisherId;

    /**
     * 发布人姓名
     */
    private String publisherName;

    /**
     * 阅读次数
     */
    private Integer readCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否已读（针对当前用户）
     */
    private Boolean isRead;

    /**
     * 阅读时间（针对当前用户）
     */
    private LocalDateTime readTime;

    /**
     * 应读人数
     */
    private Integer targetUserCount;

    /**
     * 已读人数
     */
    private Integer readUserCount;

    /**
     * 阅读率
     */
    private Double readRate;

    /**
     * 是否在有效期
     */
    private Boolean isEffective;

    /**
     * 内容摘要（前100字符）
     */
    private String contentSummary;
}