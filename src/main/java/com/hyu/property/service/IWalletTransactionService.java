package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.WalletTransaction;

import java.util.List;

/**
 * 钱包交易记录Service接口
 *
 * @author hyu
 */
public interface IWalletTransactionService extends IService<WalletTransaction> {

    /**
     * 分页查询交易记录列表
     *
     * @param page 分页参数
     * @param transaction 交易记录信息
     * @return 交易记录集合信息
     */
    Page<WalletTransaction> selectTransactionPage(Page<WalletTransaction> page, WalletTransaction transaction);

    /**
     * 查询交易记录列表
     *
     * @param transaction 交易记录信息
     * @return 交易记录集合信息
     */
    List<WalletTransaction> selectTransactionList(WalletTransaction transaction);

    /**
     * 根据交易ID查询交易记录
     *
     * @param transactionId 交易ID
     * @return 交易记录
     */
    WalletTransaction selectTransactionById(Long transactionId);

    /**
     * 根据流水号查询交易记录
     *
     * @param transactionNo 交易流水号
     * @return 交易记录
     */
    WalletTransaction selectTransactionByNo(String transactionNo);

    /**
     * 根据业主ID查询交易记录
     *
     * @param ownerId 业主ID
     * @return 交易记录列表
     */
    List<WalletTransaction> selectTransactionByOwnerId(Long ownerId);

    /**
     * 新增交易记录
     *
     * @param transaction 交易记录信息
     * @return 结果
     */
    int insertTransaction(WalletTransaction transaction);

    /**
     * 修改交易记录
     *
     * @param transaction 交易记录信息
     * @return 结果
     */
    int updateTransaction(WalletTransaction transaction);

    /**
     * 删除交易记录
     *
     * @param transactionId 交易记录ID
     * @return 结果
     */
    int deleteTransactionById(Long transactionId);

    /**
     * 检查交易流水号是否唯一
     *
     * @param transactionNo 交易流水号
     * @return 结果
     */
    boolean checkTransactionNoUnique(String transactionNo);

    /**
     * 生成交易流水号
     *
     * @param transactionType 交易类型 1:充值 2:消费
     * @return 交易流水号
     */
    String generateTransactionNo(Integer transactionType);
}