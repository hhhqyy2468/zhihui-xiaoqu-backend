package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.WalletTransaction;
import com.hyu.property.mapper.WalletTransactionMapper;
import com.hyu.property.service.IWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 钱包交易记录Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class WalletTransactionServiceImpl extends ServiceImpl<WalletTransactionMapper, WalletTransaction> implements IWalletTransactionService {

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    /**
     * 分页查询交易记录列表
     *
     * @param page 分页参数
     * @param transaction 交易记录信息
     * @return 交易记录集合信息
     */
    @Override
    public Page<WalletTransaction> selectTransactionPage(Page<WalletTransaction> page, WalletTransaction transaction) {
        LambdaQueryWrapper<WalletTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(transaction.getTransactionNo()), WalletTransaction::getTransactionNo, transaction.getTransactionNo())
                   .eq(transaction.getOwnerId() != null, WalletTransaction::getOwnerId, transaction.getOwnerId())
                   .eq(transaction.getTransactionType() != null, WalletTransaction::getTransactionType, transaction.getTransactionType())
                   .eq(transaction.getStatus() != null, WalletTransaction::getStatus, transaction.getStatus())
                   .orderByDesc(WalletTransaction::getCreateTime);
        return page(page, queryWrapper);
    }

    /**
     * 查询交易记录列表
     *
     * @param transaction 交易记录信息
     * @return 交易记录集合信息
     */
    @Override
    public List<WalletTransaction> selectTransactionList(WalletTransaction transaction) {
        LambdaQueryWrapper<WalletTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(transaction.getTransactionNo()), WalletTransaction::getTransactionNo, transaction.getTransactionNo())
                   .eq(transaction.getOwnerId() != null, WalletTransaction::getOwnerId, transaction.getOwnerId())
                   .eq(transaction.getTransactionType() != null, WalletTransaction::getTransactionType, transaction.getTransactionType())
                   .eq(transaction.getStatus() != null, WalletTransaction::getStatus, transaction.getStatus())
                   .orderByDesc(WalletTransaction::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 根据交易ID查询交易记录
     *
     * @param transactionId 交易ID
     * @return 交易记录
     */
    @Override
    public WalletTransaction selectTransactionById(Long transactionId) {
        return walletTransactionMapper.selectTransactionById(transactionId);
    }

    /**
     * 根据流水号查询交易记录
     *
     * @param transactionNo 交易流水号
     * @return 交易记录
     */
    @Override
    public WalletTransaction selectTransactionByNo(String transactionNo) {
        return walletTransactionMapper.selectTransactionByNo(transactionNo);
    }

    /**
     * 根据业主ID查询交易记录
     *
     * @param ownerId 业主ID
     * @return 交易记录列表
     */
    @Override
    public List<WalletTransaction> selectTransactionByOwnerId(Long ownerId) {
        return walletTransactionMapper.selectTransactionByOwnerId(ownerId);
    }

    /**
     * 新增交易记录
     *
     * @param transaction 交易记录信息
     * @return 结果
     */
    @Override
    public int insertTransaction(WalletTransaction transaction) {
        return walletTransactionMapper.insertTransaction(transaction);
    }

    /**
     * 修改交易记录
     *
     * @param transaction 交易记录信息
     * @return 结果
     */
    @Override
    public int updateTransaction(WalletTransaction transaction) {
        return walletTransactionMapper.updateTransaction(transaction);
    }

    /**
     * 删除交易记录
     *
     * @param transactionId 交易记录ID
     * @return 结果
     */
    @Override
    public int deleteTransactionById(Long transactionId) {
        return walletTransactionMapper.deleteTransactionById(transactionId);
    }

    /**
     * 检查交易流水号是否唯一
     *
     * @param transactionNo 交易流水号
     * @return 结果
     */
    @Override
    public boolean checkTransactionNoUnique(String transactionNo) {
        WalletTransaction transaction = walletTransactionMapper.checkTransactionNoUnique(transactionNo);
        return transaction == null;
    }

    /**
     * 生成交易流水号
     *
     * @param transactionType 交易类型
     * @return 交易流水号
     */
    @Override
    public String generateTransactionNo(Integer transactionType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        long timestamp = System.currentTimeMillis();
        return "TXN" + date + timestamp;
    }
}