package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 业主信息表 property_owner
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("property_owner")
public class Owner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "owner_id", type = IdType.AUTO)
    private Long ownerId;

    /**
     * 业主姓名
     */
    @NotBlank(message = "业主姓名不能为空")
    @Size(min = 2, max = 20, message = "业主姓名长度必须在2-20之间")
    @TableField("owner_name")
    private String ownerName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    private String phone;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    @TableField("id_card")
    private String idCard;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;

    /**
     * 业主类型
     */
    @NotNull(message = "业主类型不能为空")
    @TableField("owner_type")
    private Integer ownerType;

    /**
     * 业主状态
     */
    @NotNull(message = "业主状态不能为空")
    @TableField("owner_status")
    private Integer ownerStatus;

    /**
     * 入住日期
     */
    @TableField("move_in_date")
    private Date moveInDate;

    /**
     * 搬离日期
     */
    @TableField("move_out_date")
    private Date moveOutDate;

    /**
     * 关联房产信息（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private List<HouseInfo> houses;

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
     * 房产信息内部类
     */
    @Data
    public static class HouseInfo {
        private Long houseId;
        private String houseCode;
        private String buildingName;
        private Integer relationType; // 1:产权人 2:使用人
    }
}