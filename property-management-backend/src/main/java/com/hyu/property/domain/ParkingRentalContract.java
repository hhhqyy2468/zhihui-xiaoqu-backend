package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 车位租赁合同对象 parking_rental_contract
 *
 * @author system
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("parking_rental_contract")
public class ParkingRentalContract implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 合同ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同编号 */
    private String contractNo;

    /** 申请ID */
    private Long applicationId;

    /** 车位ID */
    private Long parkingSpaceId;

    /** 车位编号 */
    private String spaceNo;

    /** 业主ID */
    private Long ownerId;

    /** 业主姓名 */
    private String ownerName;

    /** 联系电话 */
    private String contactPhone;

    /** 车辆号码 */
    private String vehicleNumber;

    /** 车辆品牌 */
    private String vehicleBrand;

    /** 车辆颜色 */
    private String vehicleColor;

    /** 月租金 */
    private BigDecimal monthlyRent;

    /** 租赁月数 */
    private Integer rentalMonths;

    /** 租赁开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 租赁结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 已付金额 */
    private BigDecimal paidAmount;

    /** 合同状态：1-进行中 2-已到期 3-已终止 */
    private Integer contractStatus;

    /** 签订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date signDate;

    /** 终止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date terminateDate;

    /** 终止原因 */
    private String terminateReason;

    /** 备注 */
    private String remark;

    /** 删除标记：0-未删除 1-已删除 */
    private Integer deleted;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 合同状态名称（非数据库字段） */
    @TableField(exist = false)
    private String contractStatusName;

    /** 车位位置（非数据库字段） */
    @TableField(exist = false)
    private String location;

    /** 剩余天数（非数据库字段） */
    @TableField(exist = false)
    private Long remainingDays;
}
