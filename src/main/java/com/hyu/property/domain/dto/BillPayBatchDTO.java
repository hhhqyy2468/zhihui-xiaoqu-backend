package com.hyu.property.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量缴费数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class BillPayBatchDTO {

    /**
     * 账单ID数组
     */
    @NotNull(message = "账单ID不能为空")
    private List<Long> billIds;

    /**
     * 支付密码
     */
    @NotNull(message = "支付密码不能为空")
    @Size(min = 6, max = 6, message = "支付密码必须为6位数字")
    private String payPassword;
}