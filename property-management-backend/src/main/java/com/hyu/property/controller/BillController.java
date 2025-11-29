package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.excel.BillExcelUtil;
import com.hyu.common.utils.print.BillPrintUtil;
import com.hyu.property.domain.Bill;
import com.hyu.property.service.IBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 * 账单Controller
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/bill")
@Validated
public class BillController {

    @Autowired
    private IBillService billService;

    /**
     * 分页查询账单列表
     */
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('property:bill:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          Bill bill) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bill> pageParam =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bill> result =
            billService.selectBillPage(pageParam, bill);
        return AjaxResult.success(result);
    }

    /**
     * 查询账单列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:bill:list')")
    public AjaxResult listAll(Bill bill) {
        List<Bill> list = billService.selectBillList(bill);
        return AjaxResult.success(list);
    }

    /**
     * 获取账单详细信息
     */
    @GetMapping("/{billId}")
    @PreAuthorize("@ss.hasPermi('property:bill:query')")
    public AjaxResult getInfo(@PathVariable("billId") Long billId) {
        Bill bill = billService.selectBillById(billId);
        return AjaxResult.success(bill);
    }

    /**
     * 新增账单
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:bill:add')")
    public AjaxResult add(@RequestBody Bill bill) {
        int result = billService.insertBill(bill);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改账单
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:bill:edit')")
    public AjaxResult edit(@RequestBody Bill bill) {
        int result = billService.updateBill(bill);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除账单
     */
    @DeleteMapping("/{billIds}")
    @PreAuthorize("@ss.hasPermi('property:bill:remove')")
    public AjaxResult remove(@PathVariable Long[] billIds) {
        int result = billService.deleteBillByIds(billIds);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 生成账单
     */
    @PostMapping("/generate")
    @PreAuthorize("@ss.hasPermi('property:bill:generate')")
    public AjaxResult generateBills(@RequestBody Map<String, Object> params) {
        Long feeTypeId = Long.valueOf(params.get("feeTypeId").toString());
        String billPeriod = params.get("billPeriod").toString();
        Date dueDate = (Date) params.get("dueDate");
        Long[] buildingIds = (Long[]) params.get("buildingIds");
        Long[] unitIds = (Long[]) params.get("unitIds");
        Long[] houseIds = (Long[]) params.get("houseIds");

        Map<String, Object> result = billService.generateBills(feeTypeId, billPeriod, dueDate, buildingIds, unitIds, houseIds);

        if ((Integer) result.get("code") == 200) {
            return AjaxResult.success((String) result.get("msg"), result.get("data"));
        } else {
            return AjaxResult.error(result.get("msg").toString());
        }
    }

    /**
     * 在线缴费
     */
    @PostMapping("/pay")
    @PreAuthorize("@ss.hasPermi('property:bill:pay')")
    public AjaxResult payBills(@RequestBody Map<String, Object> params) {
        Long[] billIds = (Long[]) params.get("billIds");
        Long ownerId = Long.valueOf(params.get("ownerId").toString());

        Map<String, Object> result = billService.payBills(billIds, ownerId);

        if ((Integer) result.get("code") == 200) {
            return AjaxResult.success(result.get("msg").toString(), result.get("data"));
        } else {
            return AjaxResult.error(result.get("msg").toString());
        }
    }

    /**
     * 更新超期账单状态
     */
    @PostMapping("/overdue")
    @PreAuthorize("@ss.hasPermi('property:bill:overdue')")
    public AjaxResult updateOverdueBills() {
        int count = billService.updateOverdueBills();
        return AjaxResult.success("更新超期账单成功", count);
    }

    /**
     * 导出账单列表到Excel
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('property:bill:export')")
    public void exportBillsToExcel(Bill bill, HttpServletResponse response) {
        try {
            // 查询账单数据
            List<Bill> billList = billService.selectBillList(bill);

            // 生成文件名
            String fileName = "账单明细_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // 导出Excel
            BillExcelUtil.exportBillsToExcel(billList, response, fileName);

            log.info("成功导出{}条账单记录", billList.size());

        } catch (Exception e) {
            log.error("导出账单Excel失败", e);
            throw new RuntimeException("导出账单Excel失败", e);
        }
    }

    /**
     * 批量导出账单到Excel
     */
    @PostMapping("/export/batch")
    @PreAuthorize("@ss.hasPermi('property:bill:export')")
    public void exportBillsBatchToExcel(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        try {
            // 获取账单ID列表
            Long[] billIds = (Long[]) params.get("billIds");
            if (billIds == null || billIds.length == 0) {
                throw new IllegalArgumentException("账单ID列表不能为空");
            }

            // 查询指定账单
            List<Bill> billList = billService.selectBillByIds(billIds);

            // 生成文件名
            String fileName = "批量账单导出_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // 导出Excel
            BillExcelUtil.exportBillsToExcel(billList, response, fileName);

            log.info("成功批量导出{}条账单记录", billList.size());

        } catch (Exception e) {
            log.error("批量导出账单Excel失败", e);
            throw new RuntimeException("批量导出账单Excel失败", e);
        }
    }

    /**
     * 批量打印账单
     */
    @PostMapping("/print/batch")
    @PreAuthorize("@ss.hasPermi('property:bill:print')")
    public AjaxResult batchPrintBills(@RequestBody Map<String, Object> params) {
        try {
            // 获取账单ID列表
            Long[] billIds = (Long[]) params.get("billIds");
            if (billIds == null || billIds.length == 0) {
                return AjaxResult.error("账单ID列表不能为空");
            }

            // 检查批量打印数量限制
            if (billIds.length > 50) {
                return AjaxResult.error("批量打印最多支持50个账单");
            }

            // 查询指定账单
            List<Bill> billList = billService.selectBillByIds(billIds);
            if (billList.isEmpty()) {
                return AjaxResult.error("未找到有效的账单");
            }

            // 生成打印PDF字节数组
            byte[] pdfBytes = BillPrintUtil.generateBatchPrintPdf(billList, params);

            // 构建下载响应
            String fileName = "批量账单打印_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

            // 返回PDF数据供前端下载
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("data", java.util.Base64.getEncoder().encodeToString(pdfBytes));
            result.put("size", pdfBytes.length);
            return AjaxResult.success("批量打印账单成功", result);

        } catch (IllegalArgumentException e) {
            log.warn("批量打印账单参数错误：{}", e.getMessage());
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("批量打印账单失败", e);
            return AjaxResult.error("批量打印账单失败：" + e.getMessage());
        }
    }

    // ==================== 业主端API ====================

    /**
     * 获取我的账单列表
     */
    @GetMapping("/my/list")
    public AjaxResult getMyBillList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String billNo,
                                           @RequestParam(required = false) Long feeTypeId,
                                           @RequestParam(required = false) Integer billStatus,
                                           @RequestParam(required = false) String billPeriod) {
        log.info("获取我的账单列表, userId: {}, page: {}, size: {}, billNo: {}, feeTypeId: {}, billStatus: {}, billPeriod: {}",
                userId, page, size, billNo, feeTypeId, billStatus, billPeriod);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bill> pageParam =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);

        // 如果未传入userId，从当前登录用户获取
        if (userId == null) {
            userId = getCurrentUserId();
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bill> result =
                billService.selectMyBillPage(pageParam, userId, billNo, feeTypeId, billStatus, billPeriod);

        return AjaxResult.success(result);
    }

    /**
     * 获取我的账单详情
     */
    @GetMapping("/my/{billId}")
    public AjaxResult getMyBillDetail(@PathVariable Long billId) {
        log.info("获取我的账单详情, billId: {}", billId);

        Long userId = getCurrentUserId();
        Bill bill = billService.selectMyBillById(billId, userId);

        if (bill == null) {
            return AjaxResult.error("账单不存在或无权访问");
        }

        return AjaxResult.success(bill);
    }

    /**
     * 在线缴费（业主端）
     */
    @PostMapping("/owner/pay")
    public AjaxResult payBill(@RequestBody Map<String, Object> params) {
        log.info("在线缴费, params: {}", params);

        try {
            Long billId = Long.valueOf(params.get("billId").toString());
            String paymentMethod = params.get("paymentMethod").toString();
            String payPassword = params.get("payPassword") != null ? params.get("payPassword").toString() : null;

            Long userId = getCurrentUserId();

            Map<String, Object> result = billService.payBill(billId, userId, paymentMethod, payPassword);

            if ((Integer) result.get("code") == 200) {
                return AjaxResult.success(result.get("msg").toString(), result.get("data"));
            } else {
                return AjaxResult.error(result.get("msg").toString());
            }
        } catch (Exception e) {
            log.error("在线缴费失败", e);
            return AjaxResult.error("缴费失败：" + e.getMessage());
        }
    }

    /**
     * 批量缴费（业主端）
     */
    @PostMapping("/owner/pay/batch")
    public AjaxResult batchPayBills(@RequestBody Map<String, Object> params) {
        log.info("批量缴费, params: {}", params);

        try {
            Long[] billIds = (Long[]) params.get("billIds");
            String paymentMethod = params.get("paymentMethod").toString();
            String payPassword = params.get("payPassword") != null ? params.get("payPassword").toString() : null;

            Long userId = getCurrentUserId();

            Map<String, Object> result = billService.batchPayBills(billIds, userId, paymentMethod, payPassword);

            if ((Integer) result.get("code") == 200) {
                return AjaxResult.success(result.get("msg").toString(), result.get("data"));
            } else {
                return AjaxResult.error(result.get("msg").toString());
            }
        } catch (Exception e) {
            log.error("批量缴费失败", e);
            return AjaxResult.error("批量缴费失败：" + e.getMessage());
        }
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