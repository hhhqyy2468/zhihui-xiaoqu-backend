package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.Wallet;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 钱包Controller
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
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('property:wallet:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          Wallet wallet) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Wallet> pageParam =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Wallet> result =
            walletService.selectWalletPage(pageParam, wallet);
        return AjaxResult.success(result);
    }

    /**
     * 查询钱包列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:wallet:list')")
    public AjaxResult listAll(Wallet wallet) {
        List<Wallet> list = walletService.selectWalletPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 1000), wallet).getRecords();
        return AjaxResult.success(list);
    }

    /**
     * 获取钱包详细信息
     */
    @GetMapping("/{walletId}")
    @PreAuthorize("@ss.hasPermi('property:wallet:query')")
    public AjaxResult getInfo(@PathVariable("walletId") Long walletId) {
        Wallet wallet = walletService.selectWalletById(walletId);
        return AjaxResult.success(wallet);
    }

    /**
     * 根据业主ID查询钱包
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("@ss.hasPermi('property:wallet:query')")
    public AjaxResult getWalletByOwnerId(@PathVariable("ownerId") Long ownerId) {
        Wallet wallet = walletService.selectWalletByOwnerId(ownerId);
        if (wallet == null) {
            return AjaxResult.error("钱包不存在");
        }
        return AjaxResult.success(wallet);
    }

    /**
     * 新增钱包
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:wallet:add')")
    public AjaxResult add(@Validated @RequestBody Wallet wallet) {
        int result = walletService.insertWallet(wallet);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改钱包
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:wallet:edit')")
    public AjaxResult edit(@Validated @RequestBody Wallet wallet) {
        int result = walletService.updateWallet(wallet);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除钱包
     */
    @DeleteMapping("/{walletIds}")
    @PreAuthorize("@ss.hasPermi('property:wallet:remove')")
    public AjaxResult remove(@PathVariable Long[] walletIds) {
        boolean result = walletService.removeByIds(java.util.Arrays.asList(walletIds));
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 钱包充值
     */
    @PostMapping("/recharge")
    @PreAuthorize("@ss.hasPermi('property:wallet:recharge')")
    public AjaxResult recharge(@RequestBody Map<String, Object> params) {
        Long ownerId = Long.valueOf(params.get("ownerId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        try {
            boolean result = walletService.recharge(ownerId, amount);
            if (result) {
                return AjaxResult.success("充值成功");
            } else {
                return AjaxResult.error("充值失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 钱包扣款
     */
    @PostMapping("/deduct")
    @PreAuthorize("@ss.hasPermi('property:wallet:deduct')")
    public AjaxResult deduct(@RequestBody Map<String, Object> params) {
        Long ownerId = Long.valueOf(params.get("ownerId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        try {
            boolean result = walletService.deduct(ownerId, amount);
            if (result) {
                return AjaxResult.success("扣款成功");
            } else {
                return AjaxResult.error("扣款失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 冻结钱包
     */
    @PostMapping("/freeze/{ownerId}")
    @PreAuthorize("@ss.hasPermi('property:wallet:freeze')")
    public AjaxResult freeze(@PathVariable Long ownerId) {
        try {
            boolean result = walletService.freezeWallet(ownerId);
            if (result) {
                return AjaxResult.success("冻结成功");
            } else {
                return AjaxResult.error("冻结失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 解冻钱包
     */
    @PostMapping("/unfreeze/{ownerId}")
    @PreAuthorize("@ss.hasPermi('property:wallet:unfreeze')")
    public AjaxResult unfreeze(@PathVariable Long ownerId) {
        try {
            boolean result = walletService.unfreezeWallet(ownerId);
            if (result) {
                return AjaxResult.success("解冻成功");
            } else {
                return AjaxResult.error("解冻失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}