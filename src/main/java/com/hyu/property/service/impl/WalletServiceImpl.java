package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Wallet;
import com.hyu.property.mapper.WalletMapper;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {

    @Autowired
    private WalletMapper walletMapper;

    /**
     * 分页查询钱包列表
     *
     * @param page 分页参数
     * @param wallet 钱包信息
     * @return 钱包集合信息
     */
    @Override
    public Page<Wallet> selectWalletPage(Page<Wallet> page, Wallet wallet) {
        LambdaQueryWrapper<Wallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(wallet.getOwnerId() != null, Wallet::getOwnerId, wallet.getOwnerId())
                   .eq(wallet.getStatus() != null, Wallet::getStatus, wallet.getStatus())
                   .orderByDesc(Wallet::getCreateTime);
        return page(page, queryWrapper);
    }

    /**
     * 根据业主ID查询钱包
     *
     * @param ownerId 业主ID
     * @return 钱包
     */
    @Override
    public Wallet selectWalletByOwnerId(Long ownerId) {
        return walletMapper.selectWalletByOwnerId(ownerId);
    }

    /**
     * 根据钱包ID查询钱包
     *
     * @param walletId 钱包ID
     * @return 钱包
     */
    @Override
    public Wallet selectWalletById(Long walletId) {
        return walletMapper.selectById(walletId);
    }

    /**
     * 新增钱包
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    @Override
    public int insertWallet(Wallet wallet) {
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalRecharge(BigDecimal.ZERO);
        wallet.setTotalConsume(BigDecimal.ZERO);
        wallet.setStatus(1); // 正常
        wallet.setCreateTime(new Date());
        return walletMapper.insertWallet(wallet);
    }

    /**
     * 修改钱包
     *
     * @param wallet 钱包信息
     * @return 结果
     */
    @Override
    public int updateWallet(Wallet wallet) {
        return walletMapper.updateWallet(wallet);
    }

    /**
     * 充值
     *
     * @param ownerId 业主ID
     * @param amount 充值金额
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recharge(Long ownerId, BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("充值金额必须大于0");
            }

            // 查询钱包
            Wallet wallet = walletMapper.selectWalletByOwnerId(ownerId);
            if (wallet == null) {
                // 创建钱包
                wallet = new Wallet();
                wallet.setOwnerId(ownerId);
                wallet.setBalance(amount);
                wallet.setTotalRecharge(amount);
                wallet.setTotalConsume(BigDecimal.ZERO);
                wallet.setStatus(1);
                wallet.setCreateTime(new Date());
                walletMapper.insertWallet(wallet);
            } else {
                // 更新钱包
                BigDecimal newBalance = wallet.getBalance().add(amount);
                BigDecimal newTotalRecharge = wallet.getTotalRecharge().add(amount);

                walletMapper.updateBalance(wallet.getWalletId(), newBalance);
                walletMapper.updateTotalRecharge(wallet.getWalletId(), newTotalRecharge);
                walletMapper.updateLastTransactionTime(wallet.getWalletId(), new Date());
            }

            return true;
        } catch (Exception e) {
            log.error("充值失败", e);
            throw new RuntimeException("充值失败：" + e.getMessage());
        }
    }

    /**
     * 扣款
     *
     * @param ownerId 业主ID
     * @param amount 扣款金额
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deduct(Long ownerId, BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("扣款金额必须大于0");
            }

            // 查询钱包
            Wallet wallet = walletMapper.selectWalletByOwnerId(ownerId);
            if (wallet == null) {
                throw new RuntimeException("钱包不存在");
            }

            if (wallet.getStatus() != 1) {
                throw new RuntimeException("钱包已被冻结");
            }

            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("钱包余额不足");
            }

            // 更新钱包
            BigDecimal newBalance = wallet.getBalance().subtract(amount);
            BigDecimal newTotalConsume = wallet.getTotalConsume().add(amount);

            walletMapper.updateBalance(wallet.getWalletId(), newBalance);
            walletMapper.updateTotalConsume(wallet.getWalletId(), newTotalConsume);
            walletMapper.updateLastTransactionTime(wallet.getWalletId(), new Date());

            return true;
        } catch (Exception e) {
            log.error("扣款失败", e);
            throw new RuntimeException("扣款失败：" + e.getMessage());
        }
    }

    /**
     * 冻结钱包
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    @Override
    public boolean freezeWallet(Long ownerId) {
        Wallet wallet = walletMapper.selectWalletByOwnerId(ownerId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        int result = walletMapper.updateStatus(wallet.getWalletId(), 0); // 冻结
        return result > 0;
    }

    /**
     * 解冻钱包
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    @Override
    public boolean unfreezeWallet(Long ownerId) {
        Wallet wallet = walletMapper.selectWalletByOwnerId(ownerId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        int result = walletMapper.updateStatus(wallet.getWalletId(), 1); // 正常
        return result > 0;
    }

    /**
     * 检查余额是否充足
     *
     * @param ownerId 业主ID
     * @param amount 检查金额
     * @return 结果 true:充足 false:不足
     */
    @Override
    public boolean checkBalance(Long ownerId, BigDecimal amount) {
        Wallet wallet = walletMapper.selectWalletByOwnerId(ownerId);
        if (wallet == null) {
            return false;
        }
        return wallet.getBalance().compareTo(amount) >= 0;
    }
}