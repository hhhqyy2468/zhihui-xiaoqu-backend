package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 设置支付密码数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class WalletSetPasswordDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 支付密码
     */
    @NotNull(message = "支付密码不能为空")
    @Size(min = 6, max = 6, message = "支付密码必须为6位数字")
    private String payPassword;

    /**
     * 确认密码
     */
    @NotNull(message = "确认密码不能为空")
    private String confirmPassword;
}