package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告阅读记录对象 notice_read
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notice_read")
public class NoticeRead implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告ID
     */
    @TableField("notice_id")
    private Long noticeId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 公告标题（关联查询）
     */
    @TableField(exist = false)
    private String noticeTitle;

    /**
     * 公告类型（关联查询）
     */
    @TableField(exist = false)
    private String noticeType;

    /**
     * 用户姓名（关联查询）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户手机号（关联查询）
     */
    @TableField(exist = false)
    private String userPhone;

    /**
     * 房间编号（关联查询）
     */
    @TableField(exist = false)
    private String houseNo;
}