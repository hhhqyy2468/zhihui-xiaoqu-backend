package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Wallet;

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
     * @return 钱包集合信息
     */
    Page<Wallet> selectWalletPage(Page<Wallet> page, Wallet wallet);

    /**
     * 根据业主ID查询钱包
     *
     * @param ownerId 业主ID
     * @return 钱包
     */
    Wallet selectWalletByOwnerId(Long ownerId);

    /**
     * 根据钱包ID查询钱包
     *
     * @param walletId 钱包ID
     * @return 钱包
     */
    Wallet selectWalletById(Long walletId);

    /**
     * 新增钱包
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    int insertWallet(Wallet wallet);

    /**
     * 修改钱包
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    int updateWallet(Wallet wallet);

    /**
     * 充值
     *
     * @param ownerId 业主ID
     * @param amount 充值金额
     * @return 结果
     */
    boolean recharge(Long ownerId, BigDecimal amount);

    /**
     * 扣款
     *
     * @param ownerId 业主ID
     * @param amount 扣款金额
     * @return 结果
     */
    boolean deduct(Long ownerId, BigDecimal amount);

    /**
     * 冻结钱包
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    boolean freezeWallet(Long ownerId);

    /**
     * 解冻钱包
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    boolean unfreezeWallet(Long ownerId);

    /**
     * 检查余额是否充足
     *
     * @param ownerId 业主ID
     * @param amount 检查金额
     * @return 结果 true:充足 false:不足
     */
    boolean checkBalance(Long ownerId, BigDecimal amount);
}