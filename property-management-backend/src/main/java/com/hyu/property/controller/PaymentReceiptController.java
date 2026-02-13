package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.PaymentReceipt;
import com.hyu.property.service.IPaymentReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缴费收据Controller
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/payment-receipt")
public class PaymentReceiptController {

    @Autowired
    private IPaymentReceiptService paymentReceiptService;

    /**
     * 查询缴费收据列表
     */
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('property:receipt:list')")
    public AjaxResult page(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer size,
                           PaymentReceipt receipt) {
        Page<PaymentReceipt> pageParam = new Page<>(page, size);
        Page<PaymentReceipt> result = paymentReceiptService.selectReceiptPage(pageParam, receipt);
        return AjaxResult.success(result);
    }

    /**
     * 查询缴费收据列表（不分页）
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:receipt:list')")
    public AjaxResult list(PaymentReceipt receipt) {
        List<PaymentReceipt> list = paymentReceiptService.selectReceiptList(receipt);
        return AjaxResult.success(list);
    }

    /**
     * 获取缴费收据详细信息
     */
    @GetMapping("/{receiptId}")
    @PreAuthorize("@ss.hasPermi('property:receipt:query')")
    public AjaxResult getInfo(@PathVariable Long receiptId) {
        PaymentReceipt receipt = paymentReceiptService.selectReceiptById(receiptId);
        return AjaxResult.success(receipt);
    }

    /**
     * 根据账单ID查询收据
     */
    @GetMapping("/bill/{billId}")
    @PreAuthorize("@ss.hasPermi('property:receipt:query')")
    public AjaxResult getByBillId(@PathVariable Long billId) {
        PaymentReceipt receipt = paymentReceiptService.selectReceiptByBillId(billId);
        return AjaxResult.success(receipt);
    }

    /**
     * 业主端API - 获取我的缴费收据列表
     */
    @GetMapping("/my/list")
    public AjaxResult getMyReceiptList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getCurrentUserId();
        Page<PaymentReceipt> pageParam = new Page<>(page, size);
        PaymentReceipt query = new PaymentReceipt();
        query.setUserId(userId);
        Page<PaymentReceipt> result = paymentReceiptService.selectReceiptPage(pageParam, query);
        return AjaxResult.success(result);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.hyu.common.domain.LoginUser) {
            com.hyu.common.domain.LoginUser loginUser = (com.hyu.common.domain.LoginUser) authentication.getPrincipal();
            return loginUser.getUserId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
