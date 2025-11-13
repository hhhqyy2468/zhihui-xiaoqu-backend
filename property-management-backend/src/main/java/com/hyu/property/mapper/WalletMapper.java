package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包Mapper接口
 */
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {

    /**
     * 根据拥有者ID查询钱包
     */
    Wallet selectWalletByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 插入钱包
     */
    int insertWallet(Wallet wallet);

    /**
     * 更新钱包
     */
    int updateWallet(Wallet wallet);

    /**
     * 更新余额
     */
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    /**
     * 更新总充值金额
     */
    int updateTotalRecharge(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 更新总消费金额
     */
    int updateTotalConsume(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 更新最后交易时间
     */
    int updateLastTransactionTime(@Param("id") Long id, @Param("time") Date time);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
