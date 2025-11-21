package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户房产关联表 user_house
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_house")
public class UserHouse implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    /**
     * 房产ID
     */
    @NotNull(message = "房产ID不能为空")
    @TableField("house_id")
    private Long houseId;

    /**
     * 关系类型 1-业主 2-租户 3-家庭成员
     */
    @NotNull(message = "关系类型不能为空")
    @TableField("relation_type")
    private Integer relationType;

    /**
     * 开始时间
     */
    @TableField("start_date")
    private Date startDate;

    /**
     * 结束时间
     */
    @TableField("end_date")
    private Date endDate;

    /**
     * 是否当前 0-否 1-是
     */
    @TableField("is_current")
    private Boolean isCurrent;

    /**
     * 备注
     */
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

    // ==================== 非数据库字段（关联查询用） ====================

    /**
     * 用户姓名（关联查询用）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 房产编号（关联查询用）
     */
    @TableField(exist = false)
    private String houseNo;

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

    /**
     * 门牌号（关联查询用）
     */
    @TableField(exist = false)
    private String roomNumber;

    /**
     * 楼层（关联查询用）
     */
    @TableField(exist = false)
    private Integer floor;

    /**
     * 户型（关联查询用）
     */
    @TableField(exist = false)
    private String houseType;

    /**
     * 建筑面积（关联查询用）
     */
    @TableField(exist = false)
    private java.math.BigDecimal buildingArea;

    /**
     * 使用面积（关联查询用）
     */
    @TableField(exist = false)
    private java.math.BigDecimal usableArea;

    /**
     * 房产状态（关联查询用）
     */
    @TableField(exist = false)
    private Integer houseStatus;

    /**
     * 楼栋ID（关联查询用）
     */
    @TableField(exist = false)
    private Long buildingId;

    /**
     * 单元ID（关联查询用）
     */
    @TableField(exist = false)
    private Long unitId;
}