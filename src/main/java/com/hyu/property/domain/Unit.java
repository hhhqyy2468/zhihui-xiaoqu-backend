package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 单元表 unit
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("unit")
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋ID
     */
    @NotNull(message = "楼栋ID不能为空")
    @TableField("building_id")
    private Long buildingId;

    /**
     * 单元编号
     */
    @NotBlank(message = "单元编号不能为空")
    @Size(max = 50, message = "单元编号长度不能超过50")
    @TableField("unit_no")
    private String unitNo;

    /**
     * 单元名称
     */
    @NotBlank(message = "单元名称不能为空")
    @Size(max = 100, message = "单元名称长度不能超过100")
    @TableField("unit_name")
    private String unitName;

    /**
     * 楼层数
     */
    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数必须大于0")
    @Max(value = 100, message = "楼层数不能超过100")
    @TableField("floor_count")
    private Integer floorCount;

    /**
     * 每层房间数
     */
    @NotNull(message = "每层房间数不能为空")
    @Min(value = 1, message = "每层房间数必须大于0")
    @Max(value = 20, message = "每层房间数不能超过20")
    @TableField("room_count_per_floor")
    private Integer roomCountPerFloor;

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

    // ==================== 非数据库字段（关联查询用） ====================

    /**
     * 楼栋名称（关联查询用）
     */
    @TableField(exist = false)
    private String buildingName;

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