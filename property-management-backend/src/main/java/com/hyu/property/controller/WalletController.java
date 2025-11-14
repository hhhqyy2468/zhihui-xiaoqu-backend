package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.property.domain.Wallet;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 钱包管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/wallet")
@Validated
public class WalletController {

    @Autowired
    private IWalletService walletService;

    /**
     * 分页查询钱包列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:wallet:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) BigDecimal minBalance,
                          @RequestParam(required = false) BigDecimal maxBalance,
                          @RequestParam(required = false) Integer status) {
        log.info("分页查询钱包列表, pageNum: {}, pageSize: {}, userId: {}, minBalance: {}, maxBalance: {}, status: {}",
                pageNum, pageSize, userId, minBalance, maxBalance, status);

        Page<Wallet> page = new Page<>(pageNum, pageSize);
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);

        // Handle balance range query
        if (minBalance != null) {
            wallet.setBalance(minBalance);
        }

        wallet.setStatus(status);

        Page<Wallet> result;
        if (maxBalance != null) {
            // For range queries, we need a custom approach
            result = walletService.page(page, new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Wallet>()
                .eq(userId != null, "user_id", userId)
                .ge(minBalance != null, "balance", minBalance)
                .le(maxBalance != null, "balance", maxBalance)
                .eq(status != null, "status", status)
                .orderByDesc("create_time"));
        } else {
            result = walletService.selectWalletPage(page, wallet);
        }

        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取钱包详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:wallet:list')")
    public AjaxResult getInfo(@NotNull(message = "钱包ID不能为空") @PathVariable Long id) {
        log.info("获取钱包详细信息, id: {}", id);
        return AjaxResult.success(walletService.getById(id));
    }

    /**
     * 新增钱包
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:wallet:add')")
    public AjaxResult add(@Valid @RequestBody Wallet wallet) {
        log.info("新增钱包, wallet: {}", wallet);
        return toAjax(walletService.save(wallet));
    }

    /**
     * 修改保存钱包
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:wallet:edit')")
    public AjaxResult edit(@Valid @RequestBody Wallet wallet) {
        log.info("修改钱包, wallet: {}", wallet);
        return toAjax(walletService.updateById(wallet));
    }

    /**
     * 删除钱包
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:wallet:delete')")
    public AjaxResult remove(@NotNull(message = "钱包ID不能为空") @PathVariable Long[] ids) {
        log.info("删除钱包, ids: {}", ids);
        return toAjax(walletService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}