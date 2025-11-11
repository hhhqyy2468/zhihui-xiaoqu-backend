package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告对象 notice
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    @TableField("notice_title")
    private String noticeTitle;

    /**
     * 公告类型（数据字典）
     */
    @TableField("notice_type")
    private String noticeType;

    /**
     * 公告内容
     */
    @TableField("notice_content")
    private String noticeContent;

    /**
     * 附件URLs（多个用逗号分隔，最多3个）
     */
    @TableField("attachment_urls")
    private String attachmentUrls;

    /**
     * 是否置顶：0-否 1-是
     */
    @TableField("is_top")
    private Integer isTop;

    /**
     * 发布范围：1-全部 2-指定楼栋 3-指定单元
     */
    @TableField("publish_scope")
    private Integer publishScope;

    /**
     * 目标楼栋IDs（逗号分隔）
     */
    @TableField("target_building_ids")
    private String targetBuildingIds;

    /**
     * 目标单元IDs（逗号分隔）
     */
    @TableField("target_unit_ids")
    private String targetUnitIds;

    /**
     * 生效开始时间
     */
    @TableField("effective_start_time")
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    @TableField("effective_end_time")
    private LocalDateTime effectiveEndTime;

    /**
     * 公告状态：1-已发布 2-已过期 3-已撤回
     */
    @TableField("notice_status")
    private Integer noticeStatus;

    /**
     * 发布人ID
     */
    @TableField("publisher_id")
    private Long publisherId;

    /**
     * 发布人姓名（冗余字段）
     */
    @TableField("publisher_name")
    private String publisherName;

    /**
     * 阅读次数
     */
    @TableField("read_count")
    private Integer readCount;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 公告类型名称（数据字典关联查询）
     */
    @TableField(exist = false)
    private String noticeTypeName;

    /**
     * 发布范围名称
     */
    @TableField(exist = false)
    private String publishScopeName;

    /**
     * 公告状态名称
     */
    @TableField(exist = false)
    private String noticeStatusName;

    /**
     * 是否已读（针对当前用户）
     */
    @TableField(exist = false)
    private Boolean isRead;

    /**
     * 阅读时间（针对当前用户）
     */
    @TableField(exist = false)
    private LocalDateTime readTime;

    /**
     * 应读人数
     */
    @TableField(exist = false)
    private Integer targetUserCount;

    /**
     * 已读人数
     */
    @TableField(exist = false)
    private Integer readUserCount;

    /**
     * 阅读率
     */
    @TableField(exist = false)
    private Double readRate;
}