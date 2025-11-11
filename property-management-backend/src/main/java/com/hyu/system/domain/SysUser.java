package com.hyu.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50之间")
    @TableField("username")
    private String username;

    /** 密码 */
    @TableField("password")
    private String password;

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 20, message = "真实姓名长度必须在2-20之间")
    @TableField("real_name")
    private String realName;

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;

    /** 用户类型 1:管理员 2:物业管理员 3:业主 4:维修人员 */
    @TableField("user_type")
    private Integer userType;

    /** 状态 0:禁用 1:正常 */
    @TableField("status")
    private Integer status;

    /** 部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 头像URL */
    @TableField("avatar")
    private String avatar;

    /** 最后登录时间 */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 创建者 */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 角色列表 */
    @TableField(exist = false)
    private List<String> roles;

    /** 权限列表 */
    @TableField(exist = false)
    private List<String> permissions;

    /** 角色ID数组 */
    @TableField(exist = false)
    private Long[] roleIds;

    /** 创建时间开始范围（查询用） */
    @TableField(exist = false)
    private String beginTime;

    /** 创建时间结束范围（查询用） */
    @TableField(exist = false)
    private String endTime;
}