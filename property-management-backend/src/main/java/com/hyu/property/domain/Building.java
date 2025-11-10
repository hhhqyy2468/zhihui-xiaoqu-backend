package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋表实体类
 */
@Data
@TableName("building")
public class Building {

    /**
     * 楼栋ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋编号
     */
    private String buildingNo;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 楼层数
     */
    private Integer floorCount;

    /**
     * 单元数
     */
    private Integer unitCount;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 建筑年份
     */
    private Integer buildYear;

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