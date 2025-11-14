package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Wallet;
import com.hyu.property.mapper.WalletMapper;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {

    /**
     * 分页查询钱包列表
     *
     * @param page 分页参数
     * @param wallet 钱包信息
     * @return 钱包分页数据
     */
    @Override
    public Page<Wallet> selectWalletPage(Page<Wallet> page, Wallet wallet) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(wallet.getUserId() != null, "user_id", wallet.getUserId())
                   .ge(wallet.getBalance() != null, "balance", wallet.getBalance())
                   .le(wallet.getBalance() != null, "balance", wallet.getBalance())
                   .eq(wallet.getStatus() != null, "status", wallet.getStatus())
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
    }

    /**
     * 钱包充值
     *
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 结果
     */
    @Override
    public boolean recharge(Long userId, BigDecimal amount) {
        Wallet wallet = getOne(new QueryWrapper<Wallet>().eq("user_id", userId));
        if (wallet == null) {
            // 创建新钱包
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(amount);
            wallet.setTotalRecharge(amount);
            wallet.setTotalConsume(BigDecimal.ZERO);
            wallet.setStatus(1);
            wallet.setVersion(0);
            wallet.setCreateTime(LocalDateTime.now());
            wallet.setUpdateTime(LocalDateTime.now());
            return save(wallet);
        } else {
            // 更新现有钱包
            wallet.setBalance(wallet.getBalance().add(amount));
            wallet.setTotalRecharge(wallet.getTotalRecharge().add(amount));
            wallet.setUpdateTime(LocalDateTime.now());
            return updateById(wallet);
        }
    }

    /**
     * 更新钱包信息
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    @Override
    public int updateWallet(Wallet wallet) {
        wallet.setUpdateTime(LocalDateTime.now());
        return updateById(wallet) ? 1 : 0;
    }
}