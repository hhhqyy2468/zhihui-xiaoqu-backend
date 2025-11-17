package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息对象 notification
 *
 * @author hyu
 * @date 2025-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    @TableField("title")
    private String title;

    /**
     * 通知内容
     */
    @TableField("content")
    private String content;

    /**
     * 通知类型：system-系统通知 property-物业通知 bill-账单通知 repair-维修通知 activity-社区活动
     */
    @TableField("type")
    private String type;

    /**
     * 接收人ID（用户ID）
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 接收人姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 发送状态：0-待发送 1-已发送 2-发送失败
     */
    @TableField("send_status")
    private Integer sendStatus;

    /**
     * 阅读状态：0-未读 1-已读
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 关联对象类型
     */
    @TableField("object_type")
    private String objectType;

    /**
     * 关联对象ID
     */
    @TableField("object_id")
    private Long objectId;

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
}