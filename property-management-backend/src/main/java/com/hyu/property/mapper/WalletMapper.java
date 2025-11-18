package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.Wallet;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 钱包Mapper接口
 *
 * @author hyu
 */
public interface WalletMapper extends BaseMapper<Wallet> {

    /**
     * 查询钱包列表（包含用户信息）
     *
     * @param page 分页参数
     * @param wallet 钱包信息
     * @return 钱包列表
     */
    Page<Wallet> selectWalletPage(Page<Wallet> page, @Param("wallet") Wallet wallet);

    /**
     * 查询钱包列表（支持模糊查询）
     *
     * @param page 分页参数
     * @param ownerName 业主姓名（模糊查询）
     * @param phone 手机号（模糊查询）
     * @param userId 用户ID
     * @param minBalance 最小余额
     * @param maxBalance 最大余额
     * @param status 钱包状态
     * @return 钱包列表
     */
    Page<Wallet> selectWalletPageWithFuzzySearch(Page<Wallet> page,
                                                @Param("ownerName") String ownerName,
                                                @Param("phone") String phone,
                                                @Param("userId") Long userId,
                                                @Param("minBalance") BigDecimal minBalance,
                                                @Param("maxBalance") BigDecimal maxBalance,
                                                @Param("status") Integer status);

}
