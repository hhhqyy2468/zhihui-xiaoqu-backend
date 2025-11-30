package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.dto.*;
import com.hyu.property.domain.vo.*;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.service.IPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 业主门户Controller
 *
 * @author system
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/api/v1/portal")
@RequiredArgsConstructor
public class PortalController {

    private final IPortalService portalService;

    private AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    // ========== 我的账单 ==========

    /**
     * 获取我的账单列表
     */
    @GetMapping("/bill/myList")
    public AjaxResult getMyBillList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Integer billStatus,
                                      @RequestParam(required = false) String billPeriod) {
        List<Object> list = portalService.getMyBillList(billStatus, billPeriod, pageNum, pageSize);
        return success(list);
    }

    /**
     * 批量缴费
     */
    @PostMapping("/bill/payBatch")
    public AjaxResult payBillsBatch(@RequestBody BillPayBatchDTO payBatchDTO) {
        Map<String, Object> result = portalService.payBillsBatch(payBatchDTO);
        return success(result);
    }

    // ========== 我的钱包 ==========

    /**
     * 获取我的钱包信息
     */
    @GetMapping("/wallet/myInfo")
    public AjaxResult getMyWalletInfo() {
        Object walletInfo = portalService.getMyWalletInfo();
        return success(walletInfo);
    }

    /**
     * 钱包充值
     */
    @PostMapping("/wallet/recharge")
    public AjaxResult rechargeWallet(@RequestBody WalletRechargeDTO rechargeDTO) {
        Map<String, Object> result = portalService.rechargeWallet(rechargeDTO);
        return success(result);
    }

    /**
     * 设置支付密码
     */
    @PostMapping("/wallet/setPassword")
    public AjaxResult setPayPassword(@RequestBody WalletSetPasswordDTO setPasswordDTO) {
        Map<String, Object> result = portalService.setPayPassword(setPasswordDTO);
        return success(result);
    }

    /**
     * 修改支付密码
     */
    @PutMapping("/wallet/changePassword")
    public AjaxResult changePayPassword(@RequestBody WalletChangePasswordDTO changePasswordDTO) {
        Map<String, Object> result = portalService.changePayPassword(changePasswordDTO);
        return success(result);
    }

    /**
     * 获取我的交易明细
     */
    @GetMapping("/wallet/transactions")
    public AjaxResult getMyTransactionList(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(required = false) String transactionType) {
        List<Object> list = portalService.getMyTransactionList(transactionType, pageNum, pageSize);
        return success(list);
    }

    // ========== 我的投诉 ==========

    /**
     * 获取我的投诉列表
     */
    @GetMapping("/complaint/myList")
    public AjaxResult getMyComplaintList(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(required = false) Integer complaintStatus) {
        List<Object> list = portalService.getMyComplaintList(complaintStatus, pageNum, pageSize);
        return success(list);
    }

    /**
     * 获取我的投诉详情
     */
    @GetMapping("/complaint/detail/{complaintId}")
    public AjaxResult getMyComplaintDetail(@PathVariable Long complaintId) {
        Object detail = portalService.getMyComplaintDetail(complaintId);
        return success(detail);
    }

    // ========== 我的报修 ==========

    /**
     * 获取我的报修列表
     */
    @GetMapping("/repair/myList")
    public AjaxResult getMyRepairList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Integer orderStatus) {
        List<RepairOrder> list = portalService.getMyRepairList(orderStatus, pageNum, pageSize);
        return success(list);
    }

    /**
     * 获取我的报修详情
     */
    @GetMapping("/repair/detail/{repairId}")
    public AjaxResult getMyRepairDetail(@PathVariable Long repairId) {
        RepairOrder detail = portalService.getMyRepairDetail(repairId);
        return success(detail);
    }

    // ========== 我的房产 ==========

    /**
     * 获取我的房产列表
     */
    @GetMapping("/house/myList")
    public AjaxResult getMyHouseList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Object> list = portalService.getMyHouseList(pageNum, pageSize);
        return success(list);
    }

    // ========== 我的车位 ==========

    /**
     * 获取我的车位列表
     */
    @GetMapping("/parking/myList")
    public AjaxResult getMyParkingList(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) Integer rentalStatus) {
        List<Object> list = portalService.getMyParkingList(rentalStatus, pageNum, pageSize);
        return success(list);
    }

    /**
     * 获取我的车位详情
     */
    @GetMapping("/parking/detail/{rentalId}")
    public AjaxResult getMyParkingDetail(@PathVariable Long rentalId) {
        Object detail = portalService.getMyParkingDetail(rentalId);
        return success(detail);
    }

    // ========== 业主门户首页统计 ==========

    /**
     * 获取业主门户首页统计数据
     */
    @GetMapping("/statistics")
    public AjaxResult getPortalStatistics() {
        Map<String, Object> stats = portalService.getPortalStatistics();
        return success(stats);
    }

    /**
     * 获取待办事项统计
     */
    @GetMapping("/pending-count")
    public AjaxResult getPendingCount() {
        Map<String, Object> result = portalService.getPendingCount();
        return success(result);
    }
}