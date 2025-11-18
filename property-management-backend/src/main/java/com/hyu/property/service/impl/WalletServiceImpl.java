package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Wallet;
import com.hyu.property.domain.WalletTransaction;
import com.hyu.property.domain.dto.WalletRechargeDTO;
import com.hyu.property.domain.dto.WalletSetPasswordDTO;
import com.hyu.property.domain.dto.WalletChangePasswordDTO;
import com.hyu.property.mapper.WalletMapper;
import com.hyu.property.service.IWalletService;
import com.hyu.property.service.IWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 钱包Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {

    @Autowired
    private IWalletTransactionService walletTransactionService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询钱包列表
     *
     * @param page 分页参数
     * @param wallet 钱包信息
     * @return 钱包分页数据
     */
    @Override
    public Page<Wallet> selectWalletPage(Page<Wallet> page, Wallet wallet) {
        return baseMapper.selectWalletPage(page, wallet);
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

    /**
     * 根据用户ID获取钱包
     *
     * @param userId 用户ID
     * @return 钱包信息
     */
    @Override
    public Wallet getByUserId(Long userId) {
        return getOne(new QueryWrapper<Wallet>().eq("user_id", userId));
    }

    /**
     * 设置支付密码
     *
     * @param setPasswordDTO 设置密码DTO
     * @return 结果
     */
    @Override
    @Transactional
    public boolean setPayPassword(WalletSetPasswordDTO setPasswordDTO) {
        // 验证两次密码是否一致
        if (!setPasswordDTO.getPayPassword().equals(setPasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 验证密码必须为6位数字
        if (!setPasswordDTO.getPayPassword().matches("\\d{6}")) {
            throw new RuntimeException("支付密码必须为6位数字");
        }

        Wallet wallet = getByUserId(setPasswordDTO.getUserId());
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(setPasswordDTO.getPayPassword());
        wallet.setPayPassword(encodedPassword);
        wallet.setUpdateTime(LocalDateTime.now());

        return updateById(wallet);
    }

    /**
     * 修改支付密码
     *
     * @param changePasswordDTO 修改密码DTO
     * @return 结果
     */
    @Override
    @Transactional
    public boolean changePayPassword(WalletChangePasswordDTO changePasswordDTO) {
        // 验证新密码确认
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }

        // 验证新密码必须为6位数字
        if (!changePasswordDTO.getNewPassword().matches("\\d{6}")) {
            throw new RuntimeException("新支付密码必须为6位数字");
        }

        Wallet wallet = getByUserId(changePasswordDTO.getUserId());
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        // 验证原密码
        if (wallet.getPayPassword() != null &&
            !passwordEncoder.matches(changePasswordDTO.getOldPassword(), wallet.getPayPassword())) {
            throw new RuntimeException("原支付密码错误");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        wallet.setPayPassword(encodedPassword);
        wallet.setUpdateTime(LocalDateTime.now());

        return updateById(wallet);
    }

    /**
     * 验证支付密码
     *
     * @param userId 用户ID
     * @param payPassword 支付密码
     * @return 验证结果
     */
    @Override
    public boolean verifyPayPassword(Long userId, String payPassword) {
        Wallet wallet = getByUserId(userId);
        if (wallet == null || StringUtils.isEmpty(wallet.getPayPassword())) {
            return false;
        }

        // 检查是否被锁定
        if (wallet.getPayPasswordLockTime() != null &&
            wallet.getPayPasswordLockTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("支付密码已被锁定，请稍后再试");
        }

        boolean isValid = passwordEncoder.matches(payPassword, wallet.getPayPassword());

        if (!isValid) {
            // 增加错误次数
            int errorCount = wallet.getPayPasswordErrorCount() == null ? 1 : wallet.getPayPasswordErrorCount() + 1;
            wallet.setPayPasswordErrorCount(errorCount);

            // 如果错误次数达到3次，锁定1小时
            if (errorCount >= 3) {
                wallet.setPayPasswordLockTime(LocalDateTime.now().plusHours(1));
                wallet.setPayPasswordErrorCount(0);
            }
            updateById(wallet);
            throw new RuntimeException("支付密码错误");
        } else {
            // 验证成功，清零错误次数
            if (wallet.getPayPasswordErrorCount() != null && wallet.getPayPasswordErrorCount() > 0) {
                wallet.setPayPasswordErrorCount(0);
                wallet.setPayPasswordLockTime(null);
                updateById(wallet);
            }
        }

        return isValid;
    }

    /**
     * 虚拟充值（创建交易记录）
     *
     * @param userId 用户ID
     * @param rechargeDTO 充值DTO
     * @return 结果
     */
    @Override
    @Transactional
    public boolean virtualRecharge(Long userId, WalletRechargeDTO rechargeDTO) {
        // 验证支付密码
        verifyPayPassword(userId, rechargeDTO.getPayPassword());

        // 获取或创建钱包
        Wallet wallet = getByUserId(userId);
        BigDecimal beforeBalance = wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal afterBalance = beforeBalance.add(rechargeDTO.getAmount());

        // 检查余额上限
        if (afterBalance.compareTo(new BigDecimal("100000")) > 0) {
            throw new RuntimeException("钱包余额不能超过100000元");
        }

        if (wallet == null) {
            // 创建新钱包
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(rechargeDTO.getAmount());
            wallet.setTotalConsume(BigDecimal.ZERO);
            wallet.setStatus(1);
            wallet.setVersion(0);
            wallet.setCreateTime(LocalDateTime.now());
            wallet.setUpdateTime(LocalDateTime.now());
            save(wallet);
        } else {
            // 更新现有钱包
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(wallet.getTotalRecharge().add(rechargeDTO.getAmount()));
            wallet.setUpdateTime(LocalDateTime.now());
            updateById(wallet);
        }

        // 创建交易记录
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setUserId(userId);
        transaction.setWalletId(wallet.getId());
        transaction.setTransactionType(1); // 充值
        transaction.setAmount(rechargeDTO.getAmount());
        transaction.setBalanceBefore(beforeBalance);
        transaction.setBalanceAfter(afterBalance);
        transaction.setTransactionStatus(1); // 成功
        transaction.setRemark("虚拟充值");
        transaction.setCreateTime(new java.util.Date());

        return walletTransactionService.save(transaction);
    }

    /**
     * 检查钱包是否存在
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    @Override
    public boolean existsByUserId(Long userId) {
        return count(new QueryWrapper<Wallet>().eq("user_id", userId)) > 0;
    }

    /**
     * 创建用户钱包
     *
     * @param userId 用户ID
     * @param initialBalance 初始余额
     * @return 结果
     */
    @Override
    public boolean createWallet(Long userId, BigDecimal initialBalance) {
        if (existsByUserId(userId)) {
            return true; // 钱包已存在
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(initialBalance);
        wallet.setTotalRecharge(initialBalance);
        wallet.setTotalConsume(BigDecimal.ZERO);
        wallet.setStatus(1);
        wallet.setVersion(0);
        wallet.setCreateTime(LocalDateTime.now());
        wallet.setUpdateTime(LocalDateTime.now());

        return save(wallet);
    }

    /**
     * 批量充值给所有业主
     *
     * @param amount 充值金额
     * @return 充值的业主数量
     */
    @Override
    @Transactional
    public int batchRechargeForAllOwners(BigDecimal amount) {
        // 直接查询所有业主的钱包（通过join查询sys_user表确认user_type=3）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Wallet> walletQuery =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        walletQuery.exists("SELECT 1 FROM sys_user WHERE id = wallet.user_id AND user_type = 3 AND deleted = 0 AND status = 1");

        java.util.List<Wallet> ownerWallets = list(walletQuery);

        if (ownerWallets.isEmpty()) {
            throw new RuntimeException("没有找到业主钱包");
        }

        int successCount = 0;

        for (Wallet wallet : ownerWallets) {
            try {
                BigDecimal beforeBalance = wallet.getBalance();
                BigDecimal afterBalance = beforeBalance.add(amount);

                // 检查余额上限
                if (afterBalance.compareTo(new BigDecimal("100000")) > 0) {
                    log.warn("业主{}的钱包余额将达到{}，超过上限，跳过充值", wallet.getUserId(), afterBalance);
                    continue;
                }

                // 更新现有钱包
                wallet.setBalance(afterBalance);
                wallet.setTotalRecharge(wallet.getTotalRecharge().add(amount));
                wallet.setUpdateTime(LocalDateTime.now());
                updateById(wallet);

                // 创建交易记录
                WalletTransaction transaction = new WalletTransaction();
                transaction.setTransactionNo(generateTransactionNo());
                transaction.setUserId(wallet.getUserId());
                transaction.setWalletId(wallet.getId());
                transaction.setTransactionType(1); // 充值
                transaction.setAmount(amount);
                transaction.setBalanceBefore(beforeBalance);
                transaction.setBalanceAfter(afterBalance);
                transaction.setTransactionStatus(1); // 成功
                transaction.setRemark("批量充值");
                transaction.setCreateTime(new java.util.Date());
                walletTransactionService.save(transaction);

                successCount++;
                log.info("为业主{}批量充值{}元成功，余额从{}变为{}",
                        wallet.getUserId(), amount, beforeBalance, afterBalance);

            } catch (Exception e) {
                log.error("为业主{}批量充值失败：{}", wallet.getUserId(), e.getMessage(), e);
                // 继续处理其他业主，不因单个失败而中断整个批量操作
            }
        }

        if (successCount == 0) {
            throw new RuntimeException("批量充值失败，没有成功充值的业主");
        }

        return successCount;
    }

    /**
     * 管理员充值（给指定用户充值）
     *
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 结果
     */
    @Override
    @Transactional
    public boolean adminRecharge(Long userId, BigDecimal amount) {
        // 获取或创建钱包
        Wallet wallet = getByUserId(userId);
        BigDecimal beforeBalance = wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal afterBalance = beforeBalance.add(amount);

        // 检查余额上限
        if (afterBalance.compareTo(new BigDecimal("100000")) > 0) {
            throw new RuntimeException("钱包余额不能超过100000元");
        }

        if (wallet == null) {
            // 创建新钱包
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(amount);
            wallet.setTotalConsume(BigDecimal.ZERO);
            wallet.setStatus(1);
            wallet.setVersion(0);
            wallet.setCreateTime(LocalDateTime.now());
            wallet.setUpdateTime(LocalDateTime.now());
            save(wallet);
        } else {
            // 更新现有钱包
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(wallet.getTotalRecharge().add(amount));
            wallet.setUpdateTime(LocalDateTime.now());
            updateById(wallet);
        }

        // 创建交易记录
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setUserId(userId);
        transaction.setWalletId(wallet.getId());
        transaction.setTransactionType(1); // 充值
        transaction.setAmount(amount);
        transaction.setBalanceBefore(beforeBalance);
        transaction.setBalanceAfter(afterBalance);
        transaction.setTransactionStatus(1); // 成功
        transaction.setRemark("管理员充值");
        transaction.setCreateTime(new java.util.Date());

        return walletTransactionService.save(transaction);
    }

    /**
     * 冻结钱包
     *
     * @param id 钱包ID
     * @return 结果
     */
    @Override
    public boolean freezeWallet(Long id) {
        Wallet wallet = getById(id);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        if (wallet.getStatus() == 0) {
            throw new RuntimeException("钱包已经是冻结状态");
        }

        wallet.setStatus(0);
        wallet.setUpdateTime(LocalDateTime.now());

        boolean result = updateById(wallet);
        if (result) {
            log.info("冻结钱包成功, 钱包ID: {}", id);
        }

        return result;
    }

    /**
     * 解冻钱包
     *
     * @param id 钱包ID
     * @return 结果
     */
    @Override
    public boolean unfreezeWallet(Long id) {
        Wallet wallet = getById(id);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        if (wallet.getStatus() == 1) {
            throw new RuntimeException("钱包已经是正常状态");
        }

        wallet.setStatus(1);
        wallet.setUpdateTime(LocalDateTime.now());

        boolean result = updateById(wallet);
        if (result) {
            log.info("解冻钱包成功, 钱包ID: {}", id);
        }

        return result;
    }

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    private String generateTransactionNo() {
        return "WAL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}