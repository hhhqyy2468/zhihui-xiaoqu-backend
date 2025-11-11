package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 虚拟钱包表 property_wallet
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包ID
     */
    @TableId(value = "wallet_id", type = IdType.AUTO)
    private Long walletId;

    /**
     * 业主ID
     */
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 钱包余额
     */
    @NotNull(message = "钱包余额不能为空")
    @DecimalMin(value = "0.00", message = "钱包余额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "钱包余额格式不正确")
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 支付密码（加密存储）
     */
    @TableField("pay_password")
    private String payPassword;

    /**
     * 累计充值金额
     */
    @DecimalMin(value = "0.00", message = "累计充值金额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "累计充值金额格式不正确")
    @TableField("total_recharge")
    private BigDecimal totalRecharge;

    /**
     * 累计消费金额
     */
    @DecimalMin(value = "0.00", message = "累计消费金额不能小于0")
    @Digits(integer = 12, fraction = 2, message = "累计消费金额格式不正确")
    @TableField("total_consume")
    private BigDecimal totalConsume;

    /**
     * 钱包状态 1:正常 0:冻结
     */
    @NotNull(message = "钱包状态不能为空")
    @Min(value = 0, message = "钱包状态值无效")
    @Max(value = 1, message = "钱包状态值无效")
    @TableField("status")
    private Integer status;

    /**
     * 最后交易时间
     */
    @TableField("last_transaction_time")
    private Date lastTransactionTime;

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