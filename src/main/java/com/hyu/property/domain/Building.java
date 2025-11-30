package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 楼栋表 building
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("building")
public class Building implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋编号
     */
    @NotBlank(message = "楼栋编号不能为空")
    @Size(max = 50, message = "楼栋编号长度不能超过50")
    @TableField("building_no")
    private String buildingNo;

    /**
     * 楼栋名称
     */
    @NotBlank(message = "楼栋名称不能为空")
    @Size(max = 100, message = "楼栋名称长度不能超过100")
    @TableField("building_name")
    private String buildingName;

    /**
     * 楼层数
     */
    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数必须大于0")
    @Max(value = 100, message = "楼层数不能超过100")
    @TableField("floor_count")
    private Integer floorCount;

    /**
     * 单元数
     */
    @NotNull(message = "单元数不能为空")
    @Min(value = 1, message = "单元数必须大于0")
    @Max(value = 20, message = "单元数不能超过20")
    @TableField("unit_count")
    private Integer unitCount;

    /**
     * 详细地址
     */
    @Size(max = 255, message = "详细地址长度不能超过255")
    @TableField("address")
    private String address;

    /**
     * 建筑年份
     */
    @Min(value = 1900, message = "建筑年份不能小于1900")
    @Max(value = 2100, message = "建筑年份不能大于2100")
    @TableField("build_year")
    private Integer buildYear;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    @TableField("remark")
    private String remark;

    /**
     * 删除标记
     */
    @TableField("deleted")
    private Integer deleted;

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
     * 房产总数(计算字段)
     */
    @TableField(exist = false)
    private Integer houseCount;

    /**
     * 已售数量(计算字段)
     */
    @TableField(exist = false)
    private Integer soldCount;

    /**
     * 空置数量(计算字段)
     */
    @TableField(exist = false)
    private Integer vacantCount;
}