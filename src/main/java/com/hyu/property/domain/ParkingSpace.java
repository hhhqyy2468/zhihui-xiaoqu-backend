package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车位对象 parking_space
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("parking_space")
public class ParkingSpace implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车位编号
     */
    @TableField("space_no")
    private String spaceNo;

    /**
     * 车位位置（地下/地上，区域）
     */
    @TableField("location")
    private String location;

    /**
     * 车位状态：1-空闲 2-已租 3-维修中
     */
    @TableField("space_status")
    private Integer spaceStatus;

    /**
     * 月租金
     */
    @TableField("monthly_rent")
    private BigDecimal monthlyRent;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 车位状态名称
     */
    @TableField(exist = false)
    private String spaceStatusName;

    /**
     * 是否可用（根据状态判断）
     */
    @TableField(exist = false)
    private Boolean isAvailable;

    /**
     * 当前租户信息
     */
    @TableField(exist = false)
    private String currentTenant;

    /**
     * 租赁到期时间
     */
    @TableField(exist = false)
    private LocalDateTime rentalEndTime;

    /**
     * 车辆信息
     */
    @TableField(exist = false)
    private String vehicleInfo;
}