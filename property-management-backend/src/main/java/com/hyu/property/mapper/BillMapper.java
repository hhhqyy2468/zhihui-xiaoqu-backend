package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 账单Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 分页查询账单列表
     *
     * @param page 分页参数
     * @param bill 账单信息
     * @return 账单集合信息
     */
    Page<Bill> selectBillPage(Page<Bill> page, Bill bill);

    /**
     * 查询账单列表
     *
     * @param bill 账单信息
     * @return 账单集合信息
     */
    List<Bill> selectBillList(Bill bill);

    /**
     * 根据账单ID查询账单
     *
     * @param billId 账单ID
     * @return 账单
     */
    Bill selectBillById(Long billId);

    /**
     * 根据账单编号查询账单
     *
     * @param billNo 账单编号
     * @return 账单
     */
    Bill selectBillByNo(String billNo);

    /**
     * 批量删除账单
     *
     * @param billIds 需要删除的账单ID
     * @return 结果
     */
    int deleteBillByIds(Long[] billIds);

    /**
     * 新增账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    int insertBill(Bill bill);

    /**
     * 修改账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    int updateBill(Bill bill);

    /**
     * 批量生成账单
     *
     * @param bills 账单列表
     * @return 结果
     */
    int batchInsertBills(List<Bill> bills);

    /**
     * 更新账单状态
     *
     * @param billId 账单ID
     * @param billStatus 账单状态
     * @return 结果
     */
    int updateBillStatus(@Param("billId") Long billId, @Param("billStatus") Integer billStatus);

    /**
     * 更新缴费信息
     *
     * @param billId 账单ID
     * @param paidAmount 已缴金额
     * @param paidDate 缴费日期
     * @param receiptNo 收据编号
     * @param billStatus 账单状态
     * @return 结果
     */
    int updatePaymentInfo(@Param("billId") Long billId,
                           @Param("paidAmount") java.math.BigDecimal paidAmount,
                           @Param("paidDate") java.util.Date paidDate,
                           @Param("receiptNo") String receiptNo,
                           @Param("billStatus") Integer billStatus);

    /**
     * 检查账单编号是否唯一
     *
     * @param billNo 账单编号
     * @return 账单
     */
    Bill checkBillNoUnique(String billNo);

    /**
     * 查询符合条件的房产用于生成账单
     *
     * @param buildingIds 楼栋ID数组
     * @param unitIds 单元ID数组
     * @param houseIds 房产ID数组
     * @return 房产列表
     */
    List<Map<String, Object>> selectHouseForBill(@Param("buildingIds") Long[] buildingIds,
                                                @Param("unitIds") Long[] unitIds,
                                                @Param("houseIds") Long[] houseIds);

    /**
     * 根据费用类型ID查询费用类型信息
     *
     * @param feeTypeId 费用类型ID
     * @return 费用类型信息
     */
    Map<String, Object> selectFeeTypeById(Long feeTypeId);

    /**
     * 根据业主ID查询钱包余额
     *
     * @param ownerId 业主ID
     * @return 钱包余额
     */
    BigDecimal selectWalletBalance(Long ownerId);

    /**
     * 更新钱包余额
     *
     * @param ownerId 业主ID
     * @param balance 新余额
     * @return 结果
     */
    int updateWalletBalance(@Param("ownerId") Long ownerId, @Param("balance") BigDecimal balance);

    /**
     * 插入缴费交易记录
     *
     * @param ownerId 业主ID
     * @param billId 账单ID
     * @param amount 金额
     * @param description 描述
     * @param transactionNo 交易流水号
     * @return 结果
     */
    int insertPaymentTransaction(@Param("ownerId") Long ownerId,
                                @Param("billId") Long billId,
                                @Param("amount") BigDecimal amount,
                                @Param("description") String description,
                                @Param("transactionNo") String transactionNo);

    /**
     * 更新超期账单状态
     *
     * @return 更新数量
     */
    int updateOverdueBills();
}