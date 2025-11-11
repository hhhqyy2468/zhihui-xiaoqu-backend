package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志对象 sys_oper_log
 *
 * @author system
 * @date 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块标题
     */
    @TableField("title")
    private String title;

    /**
     * 业务类型：0-其它 1-新增 2-修改 3-删除 4-查询
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 方法名称
     */
    @TableField("method")
    private String method;

    /**
     * 请求方式（GET/POST/PUT/DELETE）
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 操作类别：0-其它 1-后台用户 2-手机端用户
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField("oper_name")
    private String operName;

    /**
     * 操作人员ID
     */
    @TableField("oper_user_id")
    private Long operUserId;

    /**
     * 请求URL
     */
    @TableField("oper_url")
    private String operUrl;

    /**
     * 主机地址
     */
    @TableField("oper_ip")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField("oper_location")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField("oper_param")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField("json_result")
    private String jsonResult;

    /**
     * 操作状态：0-成功 1-失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField("oper_time")
    private LocalDateTime operTime;

    /**
     * 操作时间（创建时间）
     */
    @TableField(value = "oper_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    private String businessTypeName;

    /**
     * 操作类别名称
     */
    @TableField(exist = false)
    private String operatorTypeName;

    /**
     * 操作状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 操作耗时（毫秒）
     */
    @TableField(exist = false)
    private Long costTime;

    /**
     * 操作结果摘要
     */
    @TableField(exist = false)
    private String resultSummary;
}