package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 车位租赁申请对象 parking_rental_application
 *
 * @author system
 * @date 2025-11-18
 */
@Data
@TableName("parking_rental_application")
public class ParkingRentalApplication {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

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

    /** 期望开始租赁日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date rentalStartDate;

    /** 期望结束租赁日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date rentalEndDate;

    /** 租赁月数 */
    private Integer rentalMonths;

    /** 申请原因 */
    private String applicationReason;

    /** 申请状态：1-待审核 2-已通过 3-已驳回 */
    private Integer applicationStatus;

    /** 申请状态名称 */
    @TableField(exist = false)
    private String applicationStatusName;

    /** 审核人ID */
    private Long reviewUserId;

    /** 审核人姓名 */
    private String reviewUserName;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /** 审核备注 */
    private String reviewRemark;

    /** 删除标记 */
    private Integer deleted;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 车位位置 */
    @TableField(exist = false)
    private String location;

    /** 月租金 */
    @TableField(exist = false)
    private Double monthlyRent;
}