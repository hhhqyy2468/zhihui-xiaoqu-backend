package com.hyu.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典数据表 sys_dict_data
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict_data")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 1, max = 100, message = "字典标签长度必须在1-100之间")
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @NotBlank(message = "字典键值不能为空")
    @Size(min = 1, max = 100, message = "字典键值长度必须在1-100之间")
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 1, max = 100, message = "字典类型长度必须在1-100之间")
    @TableField("dict_type")
    private String dictType;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

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
}