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
}