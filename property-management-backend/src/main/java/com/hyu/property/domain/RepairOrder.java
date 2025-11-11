package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单对象 repair_order
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("repair_order")
public class RepairOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 维修工单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 报修人ID（业主）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报修人姓名（冗余字段）
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
     * 维修类型（数据字典）
     */
    @TableField("repair_type")
    private String repairType;

    /**
     * 故障描述
     */
    @TableField("fault_description")
    private String faultDescription;

    /**
     * 故障图片URLs（多个用逗号分隔，最多5张）
     */
    @TableField("image_urls")
    private String imageUrls;

    /**
     * 紧急程度：1-普通 2-紧急
     */
    @TableField("urgency_level")
    private Integer urgencyLevel;

    /**
     * 预约维修时间
     */
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    /**
     * 工单状态：1-待派工 2-已派工 3-进行中 4-待验收 5-已完成
     */
    @TableField("order_status")
    private Integer orderStatus;

    /**
     * 维修人员ID
     */
    @TableField("worker_id")
    private Long workerId;

    /**
     * 维修人员姓名（冗余字段）
     */
    @TableField("worker_name")
    private String workerName;

    /**
     * 派工时间
     */
    @TableField("assign_time")
    private LocalDateTime assignTime;

    /**
     * 要求完成时间
     */
    @TableField("required_finish_time")
    private LocalDateTime requiredFinishTime;

    /**
     * 实际故障原因
     */
    @TableField("actual_fault")
    private String actualFault;

    /**
     * 维修内容
     */
    @TableField("repair_content")
    private String repairContent;

    /**
     * 维修后图片URLs
     */
    @TableField("repair_image_urls")
    private String repairImageUrls;

    /**
     * 维修费用
     */
    @TableField("repair_cost")
    private BigDecimal repairCost;

    /**
     * 更换配件
     */
    @TableField("parts_replaced")
    private String partsReplaced;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;

    /**
     * 验收结果：1-合格 2-不合格
     */
    @TableField("acceptance_result")
    private Integer acceptanceResult;

    /**
     * 验收评分：1-5星
     */
    @TableField("acceptance_rating")
    private Integer acceptanceRating;

    /**
     * 验收意见
     */
    @TableField("acceptance_comment")
    private String acceptanceComment;

    /**
     * 验收时间
     */
    @TableField("acceptance_time")
    private LocalDateTime acceptanceTime;

    /**
     * 返工次数
     */
    @TableField("rework_count")
    private Integer reworkCount;

    /**
     * 自动验收时间（待验收后3天）
     */
    @TableField("auto_accept_time")
    private LocalDateTime autoAcceptTime;

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
     * 楼栋ID（关联查询）
     */
    @TableField(exist = false)
    private Long buildingId;

    /**
     * 楼栋名称（关联查询）
     */
    @TableField(exist = false)
    private String buildingName;

    /**
     * 单元ID（关联查询）
     */
    @TableField(exist = false)
    private Long unitId;

    /**
     * 单元名称（关联查询）
     */
    @TableField(exist = false)
    private String unitName;

    /**
     * 维修类型名称（数据字典关联查询）
     */
    @TableField(exist = false)
    private String repairTypeName;
}