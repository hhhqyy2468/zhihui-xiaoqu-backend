package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费收据对象 payment_receipt
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("payment_receipt")
public class PaymentReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 收据编号
     */
    @TableField("receipt_no")
    private String receiptNo;

    /**
     * 账单ID
     */
    @TableField("bill_id")
    private Long billId;

    /**
     * 账单编号
     */
    @TableField("bill_no")
    private String billNo;

    /**
     * 用户ID（业主）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    private String userName;

    /**
     * 费用类型
     */
    @TableField("fee_type")
    private String feeType;

    /**
     * 账期
     */
    @TableField("bill_period")
    private String billPeriod;

    /**
     * 应缴金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 实缴金额
     */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /**
     * 支付方式：1-现金 2-银行转账 3-在线支付 4-钱包支付
     */
    @TableField("payment_method")
    private Integer paymentMethod;

    /**
     * 支付方式名称
     */
    @TableField("payment_method_name")
    private String paymentMethodName;

    /**
     * 交易流水号（钱包支付时有值）
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 收据状态：1-正常 2-已作废
     */
    @TableField("status")
    private Integer status;

    /**
     * 操作员ID（管理员代缴时）
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 操作员名称
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
