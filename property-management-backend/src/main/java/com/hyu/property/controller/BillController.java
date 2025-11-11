package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.Bill;
import com.hyu.property.service.IBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
}