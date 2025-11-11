package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 单元信息表 property_unit
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_unit")
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "unit_id", type = IdType.AUTO)
    private Long unitId;

    /**
     * 楼栋ID
     */
    @NotNull(message = "楼栋ID不能为空")
    @TableField("building_id")
    private Long buildingId;

    /**
     * 楼栋名称（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String buildingName;

    /**
     * 单元编号
     */
    @NotBlank(message = "单元编号不能为空")
    @Size(min = 2, max = 20, message = "单元编号长度必须在2-20之间")
    @TableField("unit_code")
    private String unitCode;

    /**
     * 单元名称
     */
    @NotBlank(message = "单元名称不能为空")
    @Size(min = 2, max = 50, message = "单元名称长度必须在2-50之间")
    @TableField("unit_name")
    private String unitName;

    /**
     * 楼层数
     */
    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数必须大于0")
    @Max(value = 99, message = "楼层数不能超过99")
    @TableField("floors")
    private Integer floors;

    /**
     * 每层房间数
     */
    @NotNull(message = "每层房间数不能为空")
    @Min(value = 1, message = "每层房间数必须大于0")
    @Max(value = 20, message = "每层房间数不能超过20")
    @TableField("rooms_per_floor")
    private Integer roomsPerFloor;

    /**
     * 总房间数（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private Integer totalRooms;

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
}