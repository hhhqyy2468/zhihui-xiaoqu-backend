package com.hyu.property.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class SysOperLogVO {

    /**
     * 日志主键
     */
    private Long id;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 业务类型：0-其它 1-新增 2-修改 3-删除 4-查询
     */
    private Integer businessType;

    /**
     * 业务类型名称
     */
    private String businessTypeName;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式（GET/POST/PUT/DELETE）
     */
    private String requestMethod;

    /**
     * 操作类别：0-其它 1-后台用户 2-手机端用户
     */
    private Integer operatorType;

    /**
     * 操作类别名称
     */
    private String operatorTypeName;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 操作人员ID
     */
    private Long operUserId;

    /**
     * 请求URL
     */
    private String operUrl;

    /**
     * 主机地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回参数
     */
    private String jsonResult;

    /**
     * 操作状态：0-成功 1-失败
     */
    private Integer status;

    /**
     * 操作状态名称
     */
    private String statusName;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    /**
     * 操作耗时（毫秒）
     */
    private Long costTime;

    /**
     * 操作结果摘要
     */
    private String resultSummary;

    /**
     * 用户信息（关联查询）
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
     * 是否异常操作
     */
    private Boolean isAbnormal;
}