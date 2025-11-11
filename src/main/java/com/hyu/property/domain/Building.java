package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 楼栋信息表 property_building
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_building")
public class Building implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "building_id", type = IdType.AUTO)
    private Long buildingId;

    /**
     * 楼栋编号
     */
    @NotBlank(message = "楼栋编号不能为空")
    @Size(min = 2, max = 20, message = "楼栋编号长度必须在2-20之间")
    @TableField("building_code")
    private String buildingCode;

    /**
     * 楼栋名称
     */
    @NotBlank(message = "楼栋名称不能为空")
    @Size(min = 2, max = 50, message = "楼栋名称长度必须在2-50之间")
    @TableField("building_name")
    private String buildingName;

    /**
     * 楼层数
     */
    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数必须大于0")
    @Max(value = 99, message = "楼层数不能超过99")
    @TableField("floors")
    private Integer floors;

    /**
     * 单元数
     */
    @NotNull(message = "单元数不能为空")
    @Min(value = 1, message = "单元数必须大于0")
    @Max(value = 20, message = "单元数不能超过20")
    @TableField("units")
    private Integer units;

    /**
     * 总建筑面积
     */
    @NotNull(message = "总建筑面积不能为空")
    @DecimalMin(value = "0.01", message = "总建筑面积必须大于0")
    @Digits(integer = 10, fraction = 2, message = "总建筑面积格式不正确")
    @TableField("total_area")
    private BigDecimal totalArea;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(min = 2, max = 200, message = "详细地址长度必须在2-200之间")
    @TableField("address")
    private String address;

    /**
     * 建筑年份
     */
    @NotNull(message = "建筑年份不能为空")
    @Min(value = 1900, message = "建筑年份不能早于1900年")
    @Max(value = 2030, message = "建筑年份不能晚于2030年")
    @TableField("build_year")
    private Integer buildYear;

    /**
     * 结构类型
     */
    @Size(max = 50, message = "结构类型长度不能超过50")
    @TableField("structure_type")
    private String structureType;

    /**
     * 使用类型
     */
    @Size(max = 50, message = "使用类型长度不能超过50")
    @TableField("usage_type")
    private String usageType;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    @TableField("remark")
    private String remark;

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

    /**
     * 建筑年份开始范围（查询用）
     */
    @TableField(exist = false)
    private Integer buildYearStart;

    /**
     * 建筑年份结束范围（查询用）
     */
    @TableField(exist = false)
    private Integer buildYearEnd;
}