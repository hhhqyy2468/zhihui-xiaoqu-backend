package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用类型表 fee_type
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fee_type")
public class FeeType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 费用类型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 费用名称
     */
    @NotBlank(message = "费用名称不能为空")
    @Size(min = 2, max = 50, message = "费用名称长度必须在2-50之间")
    @TableField("type_name")
    private String typeName;

    /**
     * 费用编码
     */
    @NotBlank(message = "费用编码不能为空")
    @Size(min = 2, max = 50, message = "费用编码长度必须在2-50之间")
    @TableField("type_code")
    private String typeCode;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 计费单位
     */
    @Size(max = 20, message = "计费单位长度不能超过20")
    @TableField("billing_unit")
    private String billingUnit;

    /**
     * 计费周期 1:月 2:季 3:年
     */
    @TableField("billing_cycle")
    private Integer billingCycle;

    /**
     * 费用说明
     */
    @Size(max = 500, message = "费用说明长度不能超过500")
    @TableField("description")
    private String description;

    /**
     * 状态 1:启用 0:禁用
     */
    @TableField("status")
    private Integer status;

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

    // 为了兼容前端，添加getter方法
    public Long getFeeTypeId() {
        return id;
    }

    public void setFeeTypeId(Long feeTypeId) {
        this.id = feeTypeId;
    }

    public String getFeeName() {
        return typeName;
    }

    public void setFeeName(String feeName) {
        this.typeName = feeName;
    }

    public String getFeeCode() {
        return typeCode;
    }

    public void setFeeCode(String feeCode) {
        this.typeCode = feeCode;
    }

    public String getUnitType() {
        return billingUnit;
    }

    public void setUnitType(String unitType) {
        this.billingUnit = unitType;
    }
}