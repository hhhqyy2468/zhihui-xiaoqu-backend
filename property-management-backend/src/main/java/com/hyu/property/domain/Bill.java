package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单表 property_bill
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账单ID
     */
    @TableId(value = "bill_id", type = IdType.AUTO)
    private Long billId;

    /**
     * 账单编号
     */
    @NotBlank(message = "账单编号不能为空")
    @Size(min = 5, max = 50, message = "账单编号长度必须在5-50之间")
    @TableField("bill_no")
    private String billNo;

    /**
     * 业主ID
     */
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 业主姓名
     */
    @TableField(exist = false)
    private String ownerName;

    /**
     * 业主电话
     */
    @TableField(exist = false)
    private String ownerPhone;

    /**
     * 房产ID
     */
    @NotNull(message = "房产ID不能为空")
    @TableField("house_id")
    private Long houseId;

    /**
     * 房产编号
     */
    @TableField(exist = false)
    private String houseCode;

    /**
     * 楼栋名称
     */
    @TableField(exist = false)
    private String buildingName;

    /**
     * 费用类型ID
     */
    @NotNull(message = "费用类型ID不能为空")
    @TableField("fee_type_id")
    private Long feeTypeId;

    /**
     * 费用名称
     */
    @TableField(exist = false)
    private String feeName;

    /**
     * 账期
     */
    @NotBlank(message = "账期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "账期格式不正确，应为yyyy-MM")
    @TableField("bill_period")
    private String billPeriod;

    /**
     * 计费周期
     */
    @TableField("billing_cycle")
    private String billingCycle;

    /**
     * 应缴金额
     */
    @NotNull(message = "应缴金额不能为空")
    @DecimalMin(value = "0.01", message = "应缴金额必须大于0")
    @Digits(integer = 12, fraction = 2, message = "应缴金额格式不正确")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 已缴金额
     */
    @DecimalMin(value = "0.00", message = "已缴金额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "已缴金额格式不正确")
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /**
     * 账单状态 1:待缴费 2:已缴费 3:已超期
     */
    @NotNull(message = "账单状态不能为空")
    @Min(value = 1, message = "账单状态值无效")
    @Max(value = 3, message = "账单状态值无效")
    @TableField("bill_status")
    private Integer billStatus;

    /**
     * 缴费截止日期
     */
    @NotNull(message = "缴费截止日期不能为空")
    @TableField("due_date")
    private Date dueDate;

    /**
     * 缴费日期
     */
    @TableField("paid_date")
    private Date paidDate;

    /**
     * 缴费日期
     */
    @TableField("pay_time")
    private Date payTime;

    /**
     * 收据编号
     */
    @TableField("receipt_no")
    private String receiptNo;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    @TableField("remark")
    private String remark;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}