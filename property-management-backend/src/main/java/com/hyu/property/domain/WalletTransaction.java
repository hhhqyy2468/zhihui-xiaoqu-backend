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
@TableName("wallet_transaction")
public class WalletTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 交易流水号
     */
    @NotBlank(message = "交易流水号不能为空")
    @Size(min = 10, max = 50, message = "交易流水号长度必须在10-50之间")
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    /**
     * 钱包ID
     */
    @NotNull(message = "钱包ID不能为空")
    @TableField("wallet_id")
    private Long walletId;

    /**
     * 交易类型 1:充值 2:消费 3:退款
     */
    @NotNull(message = "交易类型不能为空")
    @Min(value = 1, message = "交易类型值无效")
    @Max(value = 3, message = "交易类型值无效")
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
    @TableField("balance_before")
    private BigDecimal balanceBefore;

    /**
     * 交易后余额
     */
    @NotNull(message = "交易后余额不能为空")
    @DecimalMin(value = "0.00", message = "交易后余额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "交易后余额格式不正确")
    @TableField("balance_after")
    private BigDecimal balanceAfter;

    /**
     * 关联账单ID（消费时）
     */
    @TableField("related_bill_id")
    private Long relatedBillId;

    /**
     * 关联订单号
     */
    @TableField("related_order_no")
    private String relatedOrderNo;

    /**
     * 交易状态 1:成功 2:失败
     */
    @NotNull(message = "交易状态不能为空")
    @Min(value = 1, message = "交易状态值无效")
    @Max(value = 2, message = "交易状态值无效")
    @TableField("transaction_status")
    private Integer transactionStatus;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private java.util.Date createTime;
}