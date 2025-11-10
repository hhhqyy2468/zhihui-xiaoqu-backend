package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单元表实体类
 */
@Data
@TableName("unit")
public class Unit {

    /**
     * 单元ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋ID
     */
    private Long buildingId;

    /**
     * 单元编号
     */
    private String unitNo;

    /**
     * 单元名称
     */
    private String unitName;

    /**
     * 楼层数
     */
    private Integer floorCount;

    /**
     * 每层房间数
     */
    private Integer roomCountPerFloor;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    private Integer deleted;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}