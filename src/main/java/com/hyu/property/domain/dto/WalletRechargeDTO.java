package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 钱包充值数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class WalletRechargeDTO {

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额不能小于0.01")
    private BigDecimal amount;

    /**
     * 支付密码
     */
    @NotNull(message = "支付密码不能为空")
    private String payPassword;
}