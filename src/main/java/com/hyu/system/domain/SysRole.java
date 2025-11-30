package com.hyu.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色表 sys_role
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 30, message = "角色名称长度必须在2-30之间")
    @TableField("role_name")
    private String roleName;

    /**
     * 权限字符
     */
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 2, max = 100, message = "权限字符长度必须在2-100之间")
    @TableField("role_key")
    private String roleKey;

    /**
     * 显示顺序
     */
    @TableField("role_sort")
    private Integer roleSort;

    /**
     * 角色状态（0禁用 1启用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标记（0未删除 1已删除）
     */
    @TableField("deleted")
    private Integer deleted;

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
     * 用户是否存在此角色标识 默认不存在
     */
    @TableField(exist = false)
    private boolean flag = false;

    /**
     * 菜单组
     */
    @TableField(exist = false)
    private List<Long> menuIds;

    /**
     * 部门组（数据权限）
     */
    @TableField(exist = false)
    private List<Long> deptIds;

    /**
     * 角色菜单
     */
    @TableField(exist = false)
    private List<SysMenu> menus;

    /**
     * 用户ID（用于权限关联）
     */
    @TableField(exist = false)
    private Long userId;

    /**
     * 是否管理员
     */
    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }
}