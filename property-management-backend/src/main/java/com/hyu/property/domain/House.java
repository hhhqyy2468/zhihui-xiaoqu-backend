package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房产信息表 property_house
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_house")
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "house_id", type = IdType.AUTO)
    private Long houseId;

    /**
     * 房间编号（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String houseCode;

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
     * 单元ID
     */
    @NotNull(message = "单元ID不能为空")
    @TableField("unit_id")
    private Long unitId;

    /**
     * 单元名称（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String unitName;

    /**
     * 楼层
     */
    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层必须大于0")
    @Max(value = 99, message = "楼层不能超过99")
    @TableField("floor_num")
    private Integer floorNum;

    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    @Size(min = 1, max = 10, message = "房间号长度必须在1-10之间")
    @TableField("room_num")
    private String roomNum;

    /**
     * 户型
     */
    @Size(max = 50, message = "户型长度不能超过50")
    @TableField("house_type")
    private String houseType;

    /**
     * 建筑面积
     */
    @NotNull(message = "建筑面积不能为空")
    @DecimalMin(value = "0.01", message = "建筑面积必须大于0")
    @Digits(integer = 10, fraction = 2, message = "建筑面积格式不正确")
    @TableField("build_area")
    private BigDecimal buildArea;

    /**
     * 使用面积
     */
    @Digits(integer = 10, fraction = 2, message = "使用面积格式不正确")
    @TableField("use_area")
    private BigDecimal useArea;

    /**
     * 房产状态
     */
    @NotNull(message = "房产状态不能为空")
    @TableField("house_status")
    private Integer houseStatus;

    /**
     * 产权人ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 产权人姓名（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String ownerName;

    /**
     * 产权人电话（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String ownerPhone;

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