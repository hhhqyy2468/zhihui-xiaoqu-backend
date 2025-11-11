package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包交易记录表 property_wallet_transaction
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_wallet_transaction")
public class WalletTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易ID
     */
    @TableId(value = "transaction_id", type = IdType.AUTO)
    private Long transactionId;

    /**
     * 交易流水号
     */
    @NotBlank(message = "交易流水号不能为空")
    @Size(min = 10, max = 50, message = "交易流水号长度必须在10-50之间")
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 业主ID
     */
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 交易类型 1:充值 2:消费
     */
    @NotNull(message = "交易类型不能为空")
    @Min(value = 1, message = "交易类型值无效")
    @Max(value = 2, message = "交易类型值无效")
    @TableField("transaction_type")
    private Integer transactionType;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    @Digits(integer = 12, fraction = 2, message = "交易金额格式不正确")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 交易前余额
     */
    @NotNull(message = "交易前余额不能为空")
    @DecimalMin(value = "0.00", message = "交易前余额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "交易前余额格式不正确")
    @TableField("before_balance")
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    @NotNull(message = "交易后余额不能为空")
    @DecimalMin(value = "0.00", message = "交易后余额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "交易后余额格式不正确")
    @TableField("after_balance")
    private BigDecimal afterBalance;

    /**
     * 账单ID（消费时关联）
     */
    @TableField("bill_id")
    private Long billId;

    /**
     * 账单编号
     */
    @TableField(exist = false)
    private String billNo;

    /**
     * 交易说明
     */
    @NotBlank(message = "交易说明不能为空")
    @Size(min = 2, max = 200, message = "交易说明长度必须在2-200之间")
    @TableField("description")
    private String description;

    /**
     * 交易状态 1:成功 2:失败
     */
    @NotNull(message = "交易状态不能为空")
    @Min(value = 1, message = "交易状态值无效")
    @Max(value = 2, message = "交易状态值无效")
    @TableField("status")
    private Integer status;

    /**
     * 失败原因
     */
    @Size(max = 500, message = "失败原因长度不能超过500")
    @TableField("fail_reason")
    private String failReason;

    /**
     * 交易时间
     */
    @TableField("transaction_time")
    private Date transactionTime;

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