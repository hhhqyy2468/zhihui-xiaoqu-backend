package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 工作台统计数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class WorkbenchStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 待接单数量
     */
    @NotNull(message = "待接单数量不能为空")
    @Min(value = 0, message = "待接单数量不能小于0")
    private Integer pendingCount;

    /**
     * 进行中数量
     */
    @NotNull(message = "进行中数量不能为空")
    @Min(value = 0, message = "进行中数量不能小于0")
    private Integer processingCount;

    /**
     * 待验收数量
     */
    @NotNull(message = "待验收数量不能为空")
    @Min(value = 0, message = "待验收数量不能小于0")
    private Integer pendingAcceptCount;

    /**
     * 已完成数量
     */
    @NotNull(message = "已完成数量不能为空")
    @Min(value = 0, message = "已完成数量不能小于0")
    private Integer completedCount;

    /**
     * 今日完成数量
     */
    @NotNull(message = "今日完成数量不能为空")
    @Min(value = 0, message = "今日完成数量不能小于0")
    private Integer todayCompleted;

    /**
     * 本月完成数量
     */
    @NotNull(message = "本月完成数量不能为空")
    @Min(value = 0, message = "本月完成数量不能小于0")
    private Integer monthCompleted;
}