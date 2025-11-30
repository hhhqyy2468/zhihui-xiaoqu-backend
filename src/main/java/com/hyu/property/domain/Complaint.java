package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投诉对象 complaint
 *
 * @author system
 * @date 2025-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("complaint")
public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 投诉ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 投诉单号
     */
    @TableField("complaint_no")
    private String complaintNo;

    /**
     * 投诉人ID（业主）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 投诉人姓名（冗余字段）
     */
    @TableField("user_name")
    private String userName;

    /**
     * 房产ID
     */
    @TableField("house_id")
    private Long houseId;

    /**
     * 房间编号（冗余字段）
     */
    @TableField("house_no")
    private String houseNo;

    /**
     * 投诉类型（数据字典）
     */
    @TableField("complaint_type")
    private String complaintType;

    /**
     * 投诉内容
     */
    @TableField("complaint_content")
    private String complaintContent;

    /**
     * 图片URLs（多个用逗号分隔，最多5张）
     */
    @TableField("image_urls")
    private String imageUrls;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 紧急程度：1-普通 2-紧急
     */
    @TableField("urgency_level")
    private Integer urgencyLevel;

    /**
     * 投诉状态：1-待处理 2-处理中 3-已处理 4-已关闭
     */
    @TableField("complaint_status")
    private Integer complaintStatus;

    /**
     * 处理人ID
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理人姓名（冗余字段）
     */
    @TableField("handler_name")
    private String handlerName;

    /**
     * 处理内容
     */
    @TableField("handle_content")
    private String handleContent;

    /**
     * 处理图片URLs
     */
    @TableField("handle_image_urls")
    private String handleImageUrls;

    /**
     * 处理完成时间
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /**
     * 满意度评价：1-满意 2-一般 3-不满意
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 评价内容
     */
    @TableField("rating_content")
    private String ratingContent;

    /**
     * 评价时间
     */
    @TableField("rating_time")
    private LocalDateTime ratingTime;

    /**
     * 自动关闭时间（处理完成后7天）
     */
    @TableField("auto_close_time")
    private LocalDateTime autoCloseTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 投诉时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    // 关联字段（不对应数据库字段）
    @TableField(exist = false)
    private String complaintTypeName; // 投诉类型名称

    @TableField(exist = false)
    private String complaintStatusName; // 投诉状态名称

    @TableField(exist = false)
    private String urgencyLevelName; // 紧急程度名称

    @TableField(exist = false)
    private String ratingName; // 评价等级名称

    @TableField(exist = false)
    private java.util.Map<String, Object> params; // 请求参数
}