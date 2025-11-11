package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用类型表 property_fee_type
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_fee_type")
public class FeeType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 费用类型ID
     */
    @TableId(value = "fee_type_id", type = IdType.AUTO)
    private Long feeTypeId;

    /**
     * 费用名称
     */
    @NotBlank(message = "费用名称不能为空")
    @Size(min = 2, max = 50, message = "费用名称长度必须在2-50之间")
    @TableField("fee_name")
    private String feeName;

    /**
     * 费用编码
     */
    @NotBlank(message = "费用编码不能为空")
    @Size(min = 2, max = 20, message = "费用编码长度必须在2-20之间")
    @TableField("fee_code")
    private String feeCode;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    @Digits(integer = 10, fraction = 2, message = "单价格式不正确")
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 计费单位
     */
    @NotBlank(message = "计费单位不能为空")
    @Size(min = 2, max = 50, message = "计费单位长度必须在2-50之间")
    @TableField("unit_type")
    private String unitType;

    /**
     * 计费周期 1:月 2:季 3:年
     */
    @NotNull(message = "计费周期不能为空")
    @Min(value = 1, message = "计费周期值无效")
    @Max(value = 3, message = "计费周期值无效")
    @TableField("billing_cycle")
    private Integer billingCycle;

    /**
     * 状态 1:启用 0:禁用
     */
    @NotNull(message = "状态不能为空")
    @TableField("status")
    private Integer status;

    /**
     * 费用说明
     */
    @Size(max = 500, message = "费用说明长度不能超过500")
    @TableField("description")
    private String description;

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