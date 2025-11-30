package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.domain.LoginUser;
import com.hyu.property.domain.Wallet;
import com.hyu.property.domain.dto.WalletRechargeDTO;
import com.hyu.property.domain.dto.WalletSetPasswordDTO;
import com.hyu.property.domain.dto.WalletChangePasswordDTO;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private com.hyu.property.mapper.WalletMapper walletMapper;

    /**
     * 分页查询钱包列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:wallet:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) String ownerName,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) BigDecimal minBalance,
                          @RequestParam(required = false) BigDecimal maxBalance,
                          @RequestParam(required = false) String status) {
        log.info("分页查询钱包列表, pageNum: {}, pageSize: {}, userId: {}, ownerName: {}, phone: {}, minBalance: {}, maxBalance: {}, status: {}",
                pageNum, pageSize, userId, ownerName, phone, minBalance, maxBalance, status);

        // 转换status参数
        Integer statusValue = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusValue = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}", status);
            }
        }

        // 使用自定义查询支持模糊搜索
        Page<Wallet> page = new Page<>(pageNum, pageSize);
        Page<Wallet> result = walletMapper.selectWalletPageWithFuzzySearch(
            page, ownerName, phone, userId, minBalance, maxBalance, statusValue);

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
     * 根据用户ID获取钱包
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getByUserId(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        log.info("根据用户ID获取钱包, userId: {}", userId);
        Wallet wallet = walletService.getByUserId(userId);
        if (wallet == null) {
            return AjaxResult.error("钱包不存在");
        }
        return AjaxResult.success(wallet);
    }

    /**
     * 设置支付密码
     */
    @PostMapping("/set-password")
    public AjaxResult setPayPassword(@Valid @RequestBody WalletSetPasswordDTO setPasswordDTO) {
        log.info("设置支付密码, userId: {}", setPasswordDTO.getUserId());
        try {
            boolean result = walletService.setPayPassword(setPasswordDTO);
            return result ? AjaxResult.success("设置成功") : AjaxResult.error("设置失败");
        } catch (Exception e) {
            log.error("设置支付密码失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改支付密码
     */
    @PostMapping("/change-password")
    public AjaxResult changePayPassword(@Valid @RequestBody WalletChangePasswordDTO changePasswordDTO) {
        log.info("修改支付密码, userId: {}", changePasswordDTO.getUserId());
        try {
            boolean result = walletService.changePayPassword(changePasswordDTO);
            return result ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
        } catch (Exception e) {
            log.error("修改支付密码失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 虚拟充值（给自己充值）
     */
    @PostMapping("/virtual-recharge")
    public AjaxResult virtualRecharge(@Valid @RequestBody WalletRechargeDTO rechargeDTO) {
        Long userId = getCurrentUserId();
        log.info("用户{}进行虚拟充值, amount: {}", userId, rechargeDTO.getAmount());

        try {
            // 检查用户钱包是否已开通支付密码
            Wallet wallet = walletService.getByUserId(userId);
            if (wallet == null) {
                log.warn("用户{}的钱包不存在", userId);
                return AjaxResult.error("钱包不存在，请联系管理员开通");
            }

            if (wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
                log.warn("用户{}的支付密码未设置", userId);
                return AjaxResult.error("请先设置支付密码后再进行充值操作");
            }

            // 执行充值
            boolean result = walletService.virtualRecharge(userId, rechargeDTO);
            if (result) {
                log.info("用户{}虚拟充值成功，金额: {}", userId, rechargeDTO.getAmount());
                return AjaxResult.success("充值成功");
            } else {
                log.error("用户{}虚拟充值失败", userId);
                return AjaxResult.error("充值失败，请稍后重试");
            }
        } catch (RuntimeException e) {
            log.warn("用户{}虚拟充值失败: {}", userId, e.getMessage());
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}虚拟充值系统异常", userId, e);
            return AjaxResult.error("系统繁忙，请稍后重试");
        }
    }

    /**
     * 管理员充值（给指定用户充值）
     */
    @PostMapping("/admin-recharge")
    @PreAuthorize("@ss.hasPermi('property:wallet:recharge')")
    public AjaxResult adminRecharge(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        log.info("管理员充值, userId: {}, amount: {}", userId, amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("充值金额必须大于0");
            }
            if (amount.compareTo(new BigDecimal("10000")) > 0) {
                return AjaxResult.error("单次充值金额不能超过10000元");
            }

            boolean result = walletService.adminRecharge(userId, amount);
            return result ? AjaxResult.success("充值成功") : AjaxResult.error("充值失败");
        } catch (Exception e) {
            log.error("管理员充值失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量充值（为所有业主充值）
     */
    @PostMapping("/batch-recharge")
    @PreAuthorize("@ss.hasPermi('property:wallet:batchRecharge')")
    public AjaxResult batchRecharge(@RequestParam BigDecimal amount) {
        log.info("批量充值给所有业主, amount: {}", amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("充值金额必须大于0");
            }
            if (amount.compareTo(new BigDecimal("10000")) > 0) {
                return AjaxResult.error("单次批量充值金额不能超过10000元");
            }

            int count = walletService.batchRechargeForAllOwners(amount);
            return AjaxResult.success("批量充值成功", "共为 " + count + " 位业主充值 " + amount + " 元");
        } catch (Exception e) {
            log.error("批量充值失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 冻结钱包
     */
    @PostMapping("/freeze/{id}")
    @PreAuthorize("@ss.hasPermi('property:wallet:freeze')")
    public AjaxResult freeze(@PathVariable Long id) {
        log.info("冻结钱包, id: {}", id);
        try {
            boolean result = walletService.freezeWallet(id);
            return result ? AjaxResult.success("冻结成功") : AjaxResult.error("冻结失败");
        } catch (Exception e) {
            log.error("冻结钱包失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 解冻钱包
     */
    @PostMapping("/unfreeze/{id}")
    @PreAuthorize("@ss.hasPermi('property:wallet:freeze')")
    public AjaxResult unfreeze(@PathVariable Long id) {
        log.info("解冻钱包, id: {}", id);
        try {
            boolean result = walletService.unfreezeWallet(id);
            return result ? AjaxResult.success("解冻成功") : AjaxResult.error("解冻失败");
        } catch (Exception e) {
            log.error("解冻钱包失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 重置支付密码
     */
    @PostMapping("/reset-password/{id}")
    @PreAuthorize("@ss.hasPermi('property:wallet:resetPassword')")
    public AjaxResult resetPayPassword(@PathVariable Long id) {
        log.info("重置支付密码, id: {}", id);
        try {
            boolean result = walletService.resetPayPassword(id);
            return result ? AjaxResult.success("重置成功，新密码为：123456（已清除）") : AjaxResult.error("重置失败");
        } catch (Exception e) {
            log.error("重置支付密码失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取当前用户ID（从Spring Security上下文获取）
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            return loginUser.getUserId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}