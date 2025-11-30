package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 虚拟钱包对象 wallet
 *
 * @author hyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（业主）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 当前余额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 累计充值金额
     */
    @TableField("total_recharge")
    private BigDecimal totalRecharge;

    /**
     * 累计消费金额
     */
    @TableField("total_consume")
    private BigDecimal totalConsume;

    /**
     * 支付密码（BCrypt加密）
     */
    @TableField("pay_password")
    private String payPassword;

    /**
     * 支付密码错误次数
     */
    @TableField("pay_password_error_count")
    private Integer payPasswordErrorCount;

    /**
     * 支付密码锁定时间
     */
    @TableField("pay_password_lock_time")
    private LocalDateTime payPasswordLockTime;

    /**
     * 钱包状态：1-正常 2-冻结
     */
    @TableField("status")
    private Integer status;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 用户名称（关联查询）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户手机号（关联查询）
     */
    @TableField(exist = false)
    private String userPhone;

    /**
     * 支付密码状态：0-未设置，1-已设置
     */
    @TableField("password_status")
    private Integer passwordStatus;

    }