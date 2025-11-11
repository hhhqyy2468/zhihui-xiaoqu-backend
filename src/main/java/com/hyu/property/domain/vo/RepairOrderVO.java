package com.hyu.property.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class RepairOrderVO {

    /**
     * 维修工单ID
     */
    private Long id;

    /**
     * 工单编号
     */
    private String orderNo;

    /**
     * 报修人ID（业主）
     */
    private Long userId;

    /**
     * 报修人姓名
     */
    private String userName;

    /**
     * 房产ID
     */
    private Long houseId;

    /**
     * 房间编号
     */
    private String houseNo;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 单元名称
     */
    private String unitName;

    /**
     * 维修类型
     */
    private String repairType;

    /**
     * 维修类型名称
     */
    private String repairTypeName;

    /**
     * 故障描述
     */
    private String faultDescription;

    /**
     * 故障图片URLs
     */
    private String imageUrls;

    /**
     * 紧急程度：1-普通 2-紧急
     */
    private Integer urgencyLevel;

    /**
     * 紧急程度名称
     */
    private String urgencyLevelName;

    /**
     * 预约维修时间
     */
    private LocalDateTime appointmentTime;

    /**
     * 工单状态：1-待派工 2-已派工 3-进行中 4-待验收 5-已完成
     */
    private Integer orderStatus;

    /**
     * 工单状态名称
     */
    private String orderStatusName;

    /**
     * 维修人员ID
     */
    private Long workerId;

    /**
     * 维修人员姓名
     */
    private String workerName;

    /**
     * 派工时间
     */
    private LocalDateTime assignTime;

    /**
     * 要求完成时间
     */
    private LocalDateTime requiredFinishTime;

    /**
     * 实际故障原因
     */
    private String actualFault;

    /**
     * 维修内容
     */
    private String repairContent;

    /**
     * 维修后图片URLs
     */
    private String repairImageUrls;

    /**
     * 维修费用
     */
    private BigDecimal repairCost;

    /**
     * 更换配件
     */
    private String partsReplaced;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 验收结果：1-合格 2-不合格
     */
    private Integer acceptanceResult;

    /**
     * 验收结果名称
     */
    private String acceptanceResultName;

    /**
     * 验收评分：1-5星
     */
    private Integer acceptanceRating;

    /**
     * 验收意见
     */
    private String acceptanceComment;

    /**
     * 验收时间
     */
    private LocalDateTime acceptanceTime;

    /**
     * 返工次数
     */
    private Integer reworkCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 报修时间
     */
    private LocalDateTime reportTime;

    /**
     * 处理时长（小时）
     */
    private Long processingDuration;

    /**
     * 是否超时
     */
    private Boolean isOverdue;
}