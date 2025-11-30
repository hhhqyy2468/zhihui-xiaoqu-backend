package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房产表 house
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("house")
public class House implements Serializable {

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
     * 单元ID
     */
    @NotNull(message = "单元ID不能为空")
    @TableField("unit_id")
    private Long unitId;

    /**
     * 房间编号（楼栋-单元-房号）
     */
    @NotBlank(message = "房间编号不能为空")
    @Size(max = 50, message = "房间编号长度不能超过50")
    @TableField("house_no")
    private String houseNo;

    /**
     * 楼层
     */
    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层必须大于0")
    @Max(value = 100, message = "楼层不能超过100")
    @TableField("floor")
    private Integer floor;

    /**
     * 门牌号
     */
    @NotBlank(message = "门牌号不能为空")
    @Size(max = 50, message = "门牌号长度不能超过50")
    @TableField("room_number")
    private String roomNumber;

    /**
     * 户型（如：三室两厅）
     */
    @Size(max = 50, message = "户型长度不能超过50")
    @TableField("house_type")
    private String houseType;

    /**
     * 建筑面积（平方米）
     */
    @NotNull(message = "建筑面积不能为空")
    @DecimalMin(value = "0.01", message = "建筑面积必须大于0")
    @Digits(integer = 8, fraction = 2, message = "建筑面积格式不正确")
    @TableField("building_area")
    private BigDecimal buildingArea;

    /**
     * 使用面积（平方米）
     */
    @Digits(integer = 8, fraction = 2, message = "使用面积格式不正确")
    @TableField("usable_area")
    private BigDecimal usableArea;

    /**
     * 房产状态：1-空置 2-已售 3-已租
     */
    @NotNull(message = "房产状态不能为空")
    @Min(value = 1, message = "房产状态值不正确")
    @Max(value = 3, message = "房产状态值不正确")
    @TableField("house_status")
    private Integer houseStatus;

  
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
     * 单元名称（关联查询用）
     */
    @TableField(exist = false)
    private String unitName;

  }