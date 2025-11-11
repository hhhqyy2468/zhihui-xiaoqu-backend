package com.hyu.property.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class SysLoginLogVO {

    /**
     * 访问ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名（关联查询）
     */
    private String userName;

    /**
     * 用户类型（关联查询）
     */
    private Integer userType;

    /**
     * 用户类型名称（关联查询）
     */
    private String userTypeName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态：0-成功 1-失败
     */
    private Integer status;

    /**
     * 登录状态名称
     */
    private String statusName;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private LocalDateTime loginTime;

    /**
     * 是否为新设备
     */
    private Boolean isNewDevice;

    /**
     * 登录频率（同一IP近24小时登录次数）
     */
    private Integer loginFrequency;

    /**
     * 风险等级：0-低风险 1-中风险 2-高风险
     */
    private Integer riskLevel;

    /**
     * 风险等级名称
     */
    private String riskLevelName;

    /**
     * 是否异常登录
     */
    private Boolean isAbnormal;

    /**
     * 登录设备信息
     */
    private String deviceInfo;
}