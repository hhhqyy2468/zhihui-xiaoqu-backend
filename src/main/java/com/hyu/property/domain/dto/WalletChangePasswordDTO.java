package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 修改支付密码数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class WalletChangePasswordDTO {

    /**
     * 原支付密码
     */
    @NotNull(message = "原支付密码不能为空")
    private String oldPassword;

    /**
     * 新支付密码
     */
    @NotNull(message = "新支付密码不能为空")
    @Size(min = 6, max = 6, message = "新支付密码必须为6位数字")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotNull(message = "确认新密码不能为空")
    private String confirmPassword;
}