package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志对象 sys_login_log
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 登录IP地址
     */
    @TableField("ipaddr")
    private String ipaddr;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 登录状态：0-成功 1-失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 提示消息
     */
    @TableField("msg")
    private String msg;

    /**
     * 访问时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 访问时间（创建时间）
     */
    @TableField(value = "login_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 登录状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 用户信息（关联查询）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户类型（关联查询）
     */
    @TableField(exist = false)
    private Integer userType;

    /**
     * 用户类型名称（关联查询）
     */
    @TableField(exist = false)
    private String userTypeName;

    /**
     * 是否为新设备
     */
    @TableField(exist = false)
    private Boolean isNewDevice;

    /**
     * 登录频率（同一IP近24小时登录次数）
     */
    @TableField(exist = false)
    private Integer loginFrequency;

    /**
     * 风险等级：0-低风险 1-中风险 2-高风险
     */
    @TableField(exist = false)
    private Integer riskLevel;

    /**
     * 风险等级名称
     */
    @TableField(exist = false)
    private String riskLevelName;
}