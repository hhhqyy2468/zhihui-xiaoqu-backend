package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {

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
     * 更新钱包余额
     *
     * @param walletId 钱包ID
     * @param balance 余额
     * @return 结果
     */
    int updateBalance(@Param("walletId") Long walletId, @Param("balance") BigDecimal balance);

    /**
     * 更新累计充值金额
     *
     * @param walletId 钱包ID
     * @param totalRecharge 累计充值金额
     * @return 结果
     */
    int updateTotalRecharge(@Param("walletId") Long walletId, @Param("totalRecharge") BigDecimal totalRecharge);

    /**
     * 更新累计消费金额
     *
     * @param walletId 钱包ID
     * @param totalConsume 累计消费金额
     * @return 结果
     */
    int updateTotalConsume(@Param("walletId") Long walletId, @Param("totalConsume") BigDecimal totalConsume);

    /**
     * 更新最后交易时间
     *
     * @param walletId 钱包ID
     * @param lastTransactionTime 最后交易时间
     * @return 结果
     */
    int updateLastTransactionTime(@Param("walletId") Long walletId, @Param("lastTransactionTime") java.util.Date lastTransactionTime);

    /**
     * 冻结/解冻钱包
     *
     * @param walletId 钱包ID
     * @param status 状态
     * @return 结果
     */
    int updateStatus(@Param("walletId") Long walletId, @Param("status") Integer status);
}