package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.dto.WorkbenchStatsDTO;
import com.hyu.property.service.IRepairOrderService;
import com.hyu.property.service.IWorkbenchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作台Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/workbench")
@RequiredArgsConstructor
@Api(tags = "工作台")
public class WorkbenchController {

    private final IWorkbenchService workbenchService;
    private final IRepairOrderService repairOrderService;

    /**
     * 获取工作台统计数据
     */
    @ApiOperation("获取工作台统计数据")
    @GetMapping("/stats")
    public AjaxResult getWorkbenchStats() {
        WorkbenchStatsDTO stats = workbenchService.getWorkbenchStats();
        return AjaxResult.success(stats);
    }

    /**
     * 获取我的工单列表（维修人员）
     */
    @ApiOperation("获取我的工单列表")
    @GetMapping("/my-orders")
    @PreAuthorize("@ss.hasPermi('property:repair:accept')")
    public AjaxResult getMyRepairOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer repairStatus) {
        List<RepairOrder> orders = workbenchService.getMyRepairOrderList(pageNum, pageSize, repairStatus);
        return AjaxResult.success(orders);
    }

    /**
     * 获取工单详情
     */
    @ApiOperation("获取工单详情")
    @GetMapping("/order/{orderId}")
    public AjaxResult getRepairOrderDetail(@PathVariable Long orderId) {
        RepairOrder order = repairOrderService.getById(orderId);
        if (order == null) {
            return AjaxResult.error("工单不存在");
        }
        return AjaxResult.success(order);
    }

    /**
     * 维修人员接单
     */
    @ApiOperation("维修人员接单")
    @PostMapping("/order/{orderId}/accept")
    @PreAuthorize("@ss.hasPermi('property:repair:accept')")
    public AjaxResult acceptOrder(@PathVariable Long orderId) {
        boolean ok = repairOrderService.acceptOrder(orderId);
        return ok ? AjaxResult.success("接单成功") : AjaxResult.error("接单失败");
    }

    /**
     * 提交维修记录（完成处理）
     */
    @ApiOperation("提交维修记录")
    @PostMapping("/order/{orderId}/complete")
    @PreAuthorize("@ss.hasPermi('property:repair:handle')")
    public AjaxResult completeOrder(@PathVariable Long orderId,
                                    @RequestBody Map<String, Object> params) {
        boolean ok = repairOrderService.completeOrder(orderId, params);
        return ok ? AjaxResult.success("提交成功") : AjaxResult.error("提交失败");
    }
}
