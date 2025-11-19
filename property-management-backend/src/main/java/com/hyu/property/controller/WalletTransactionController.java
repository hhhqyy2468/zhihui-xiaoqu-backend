package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.WalletTransaction;
import com.hyu.property.service.IWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 钱包交易记录Controller
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/wallet/transaction")
@Validated
public class WalletTransactionController {

    @Autowired
    private IWalletTransactionService walletTransactionService;

    /**
     * 分页查询交易记录列表
     */
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('property:transaction:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          WalletTransaction transaction) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<WalletTransaction> pageParam =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<WalletTransaction> result =
            walletTransactionService.selectTransactionPage(pageParam, transaction);
        return AjaxResult.success(result);
    }

    /**
     * 查询交易记录列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:transaction:list')")
    public AjaxResult listAll(WalletTransaction transaction) {
        List<WalletTransaction> list = walletTransactionService.selectTransactionList(transaction);
        return AjaxResult.success(list);
    }

    /**
     * 获取交易记录详细信息
     */
    @GetMapping("/{transactionId}")
    @PreAuthorize("@ss.hasPermi('property:transaction:query')")
    public AjaxResult getInfo(@PathVariable("transactionId") Long transactionId) {
        WalletTransaction transaction = walletTransactionService.selectTransactionById(transactionId);
        return AjaxResult.success(transaction);
    }

    /**
     * 根据流水号查询交易记录
     */
    @GetMapping("/no/{transactionNo}")
    @PreAuthorize("@ss.hasPermi('property:transaction:query')")
    public AjaxResult getInfoByNo(@PathVariable("transactionNo") String transactionNo) {
        WalletTransaction transaction = walletTransactionService.selectTransactionByNo(transactionNo);
        if (transaction == null) {
            return AjaxResult.error("交易记录不存在");
        }
        return AjaxResult.success(transaction);
    }

    /**
     * 根据业主ID查询交易记录
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("@ss.hasPermi('property:transaction:query')")
    public AjaxResult getInfoByOwnerId(@PathVariable("ownerId") Long ownerId) {
        List<WalletTransaction> list = walletTransactionService.selectTransactionByOwnerId(ownerId);
        return AjaxResult.success(list);
    }

    /**
     * 新增交易记录
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:transaction:add')")
    public AjaxResult add(@Validated @RequestBody WalletTransaction transaction) {
        int result = walletTransactionService.insertTransaction(transaction);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改交易记录
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:transaction:edit')")
    public AjaxResult edit(@Validated @RequestBody WalletTransaction transaction) {
        int result = walletTransactionService.updateTransaction(transaction);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除交易记录
     */
    @DeleteMapping("/{transactionIds}")
    @PreAuthorize("@ss.hasPermi('property:transaction:remove')")
    public AjaxResult remove(@PathVariable Long[] transactionIds) {
        int result = 0;
        for (Long transactionId : transactionIds) {
            result += walletTransactionService.deleteTransactionById(transactionId);
        }
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}