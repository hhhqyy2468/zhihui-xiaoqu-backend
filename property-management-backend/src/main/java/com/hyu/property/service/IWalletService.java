package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Wallet;
import com.hyu.property.domain.dto.WalletRechargeDTO;
import com.hyu.property.domain.dto.WalletSetPasswordDTO;
import com.hyu.property.domain.dto.WalletChangePasswordDTO;

import java.math.BigDecimal;

/**
 * 钱包Service接口
 *
 * @author hyu
 */
public interface IWalletService extends IService<Wallet> {

    /**
     * 分页查询钱包列表
     *
     * @param page 分页参数
     * @param wallet 钱包信息
     * @return 钱包分页数据
     */
    Page<Wallet> selectWalletPage(Page<Wallet> page, Wallet wallet);

    /**
     * 钱包充值
     *
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 结果
     */
    boolean recharge(Long userId, BigDecimal amount);

    /**
     * 更新钱包信息
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    int updateWallet(Wallet wallet);

    /**
     * 根据用户ID获取钱包
     *
     * @param userId 用户ID
     * @return 钱包信息
     */
    Wallet getByUserId(Long userId);

    /**
     * 设置支付密码
     *
     * @param setPasswordDTO 设置密码DTO
     * @return 结果
     */
    boolean setPayPassword(WalletSetPasswordDTO setPasswordDTO);

    /**
     * 修改支付密码
     *
     * @param changePasswordDTO 修改密码DTO
     * @return 结果
     */
    boolean changePayPassword(WalletChangePasswordDTO changePasswordDTO);

    /**
     * 验证支付密码
     *
     * @param userId 用户ID
     * @param payPassword 支付密码
     * @return 验证结果
     */
    boolean verifyPayPassword(Long userId, String payPassword);

    /**
     * 虚拟充值（创建交易记录）
     *
     * @param userId 用户ID
     * @param rechargeDTO 充值DTO
     * @return 结果
     */
    boolean virtualRecharge(Long userId, WalletRechargeDTO rechargeDTO);

    /**
     * 检查钱包是否存在
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);

    /**
     * 创建用户钱包
     *
     * @param userId 用户ID
     * @param initialBalance 初始余额
     * @return 结果
     */
    boolean createWallet(Long userId, BigDecimal initialBalance);

    /**
     * 批量充值给所有业主
     *
     * @param amount 充值金额
     * @return 充值的业主数量
     */
    int batchRechargeForAllOwners(BigDecimal amount);

    /**
     * 管理员充值（给指定用户充值）
     *
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 结果
     */
    boolean adminRecharge(Long userId, BigDecimal amount);

    /**
     * 冻结钱包
     *
     * @param id 钱包ID
     * @return 结果
     */
    boolean freezeWallet(Long id);

    /**
     * 解冻钱包
     *
     * @param id 钱包ID
     * @return 结果
     */
    boolean unfreezeWallet(Long id);

    /**
     * 重置支付密码
     *
     * @param id 钱包ID
     * @return 结果
     */
    boolean resetPayPassword(Long id);
}