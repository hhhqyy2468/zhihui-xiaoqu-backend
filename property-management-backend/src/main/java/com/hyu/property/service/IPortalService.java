package com.hyu.property.service;

import com.hyu.property.domain.dto.*;
import com.hyu.property.domain.RepairOrder;

import java.util.List;
import java.util.Map;

/**
 * 业主门户Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface IPortalService {

    // ========== 我的账单 ==========

    /**
     * 获取我的账单列表
     *
     * @param billStatus 账单状态
     * @param billPeriod 账期
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 账单列表
     */
    public List<Object> getMyBillList(Integer billStatus, String billPeriod, Integer pageNum, Integer pageSize);

    /**
     * 批量缴费
     *
     * @param payBatchDTO 批量缴费参数
     * @return 缴费结果
     */
    public Map<String, Object> payBillsBatch(BillPayBatchDTO payBatchDTO);

    // ========== 我的钱包 ==========

    /**
     * 获取我的钱包信息
     *
     * @return 钱包信息
     */
    public Object getMyWalletInfo();

    /**
     * 钱包充值
     *
     * @param rechargeDTO 充值参数
     * @return 充值结果
     */
    public Map<String, Object> rechargeWallet(WalletRechargeDTO rechargeDTO);

    /**
     * 设置支付密码
     *
     * @param setPasswordDTO 设置密码参数
     * @return 设置结果
     */
    public Map<String, Object> setPayPassword(WalletSetPasswordDTO setPasswordDTO);

    /**
     * 修改支付密码
     *
     * @param changePasswordDTO 修改密码参数
     * @return 修改结果
     */
    public Map<String, Object> changePayPassword(WalletChangePasswordDTO changePasswordDTO);

    /**
     * 获取我的交易明细
     *
     * @param transactionType 交易类型
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 交易明细
     */
    public List<Object> getMyTransactionList(String transactionType, Integer pageNum, Integer pageSize);

    // ========== 我的投诉 ==========

    /**
     * 获取我的投诉列表
     *
     * @param complaintStatus 投诉状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 投诉列表
     */
    public List<Object> getMyComplaintList(Integer complaintStatus, Integer pageNum, Integer pageSize);

    /**
     * 获取我的投诉详情
     *
     * @param complaintId 投诉ID
     * @return 投诉详情
     */
    public Object getMyComplaintDetail(Long complaintId);

    // ========== 我的报修 ==========

    /**
     * 获取我的报修列表
     *
     * @param orderStatus 工单状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 报修列表
     */
    public List<RepairOrder> getMyRepairList(Integer orderStatus, Integer pageNum, Integer pageSize);

    /**
     * 获取我的报修详情
     *
     * @param repairId 报修ID
     * @return 报修详情
     */
    public RepairOrder getMyRepairDetail(Long repairId);

    // ========== 我的房产 ==========

    /**
     * 获取我的房产列表
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 房产列表
     */
    public List<Object> getMyHouseList(Integer pageNum, Integer pageSize);

    // ========== 我的车位 ==========

    /**
     * 获取我的车位列表
     *
     * @param rentalStatus 租赁状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 车位列表
     */
    public List<Object> getMyParkingList(Integer rentalStatus, Integer pageNum, Integer pageSize);

    /**
     * 获取我的车位详情
     *
     * @param rentalId 租赁ID
     * @return 车位详情
     */
    public Object getMyParkingDetail(Long rentalId);

    // ========== 业主门户首页统计 ==========

    /**
     * 获取业主门户首页统计数据
     *
     * @return 统计数据
     */
    public Map<String, Object> getPortalStatistics();

    /**
     * 获取待办事项统计
     *
     * @return 待办事项数量
     */
    public Map<String, Object> getPendingCount();
}