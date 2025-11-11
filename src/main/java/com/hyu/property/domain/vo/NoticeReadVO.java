package com.hyu.property.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告阅读记录视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class NoticeReadVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 公告标题（关联查询）
     */
    private String noticeTitle;

    /**
     * 公告类型（关联查询）
     */
    private String noticeType;

    /**
     * 用户姓名（关联查询）
     */
    private String userName;

    /**
     * 用户手机号（关联查询）
     */
    private String userPhone;

    /**
     * 房间编号（关联查询）
     */
    private String houseNo;
}