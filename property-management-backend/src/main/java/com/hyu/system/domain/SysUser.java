package com.hyu.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 */
@Data
@TableName("sys_user")
public class SysUser {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 住户类型：1-业主 2-租户（仅业主角色有值）
     */
    private Integer residentType;

    /**
     * 入住日期
     */
    private LocalDate checkInDate;

    /**
     * 住户状态：1-在住 2-已搬离
     */
    private Integer residentStatus;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 用户类型：1-系统管理员 2-物业管理员 3-业主 4-维修人员
     */
    private Integer userType;

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