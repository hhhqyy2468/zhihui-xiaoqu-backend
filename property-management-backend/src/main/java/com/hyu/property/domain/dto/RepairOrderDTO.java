package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 维修工单数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class RepairOrderDTO {

    /**
     * 工单ID
     */
    private Long id;

    /**
     * 工单编号
     */
    private String orderNo;

    /**
     * 房产ID
     */
    @NotNull(message = "房产ID不能为空")
    private Long houseId;

    /**
     * 维修类型
     */
    @NotBlank(message = "维修类型不能为空")
    private String repairType;

    /**
     * 故障描述
     */
    @NotBlank(message = "故障描述不能为空")
    private String faultDescription;

    /**
     * 故障图片URLs
     */
    private String imageUrls;

    /**
     * 紧急程度：1-普通 2-紧急
     */
    @NotNull(message = "紧急程度不能为空")
    private Integer urgencyLevel;

    /**
     * 预约维修时间
     */
    private LocalDateTime appointmentTime;

    // ===== 派工相关 =====
    /**
     * 维修人员ID
     */
    private Long workerId;

    /**
     * 维修人员姓名
     */
    private String workerName;

    /**
     * 要求完成时间
     */
    private LocalDateTime requiredFinishTime;

    // ===== 维修执行相关 =====
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
    private java.math.BigDecimal repairCost;

    /**
     * 更换配件
     */
    private String partsReplaced;

    // ===== 验收相关 =====
    /**
     * 验收结果：1-合格 2-不合格
     */
    private Integer acceptanceResult;

    /**
     * 验收评分：1-5星
     */
    private Integer acceptanceRating;

    /**
     * 验收意见
     */
    private String acceptanceComment;
}