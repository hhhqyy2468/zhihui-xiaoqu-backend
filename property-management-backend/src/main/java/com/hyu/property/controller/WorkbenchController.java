package com.hyu.property.controller;

import com.hyu.property.domain.dto.WorkbenchStatsDTO;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.service.IWorkbenchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台Controller
 *
 * @author system
 * @date 2025-11-11
 */
public class WorkbenchController {

    private static final Logger log = LoggerFactory.getLogger(WorkbenchController.class);
    private final IWorkbenchService workbenchService;

    public WorkbenchController(IWorkbenchService workbenchService) {
        this.workbenchService = workbenchService;
    }

    /**
     * 获取工作台统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getWorkbenchStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            WorkbenchStatsDTO stats = workbenchService.getWorkbenchStats();
            result.put("code", 200);
            result.put("message", "获取工作台统计数据成功");
            result.put("data", stats);
        } catch (Exception e) {
            log.error("获取工作台统计数据异常", e);
            result.put("code", 500);
            result.put("message", "获取工作台统计数据失败");
        }
        return result;
    }

    /**
     * 获取我的工单列表
     */
    @GetMapping("/my-orders")
    public Map<String, Object> getMyRepairOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(required = false) Integer repairStatus) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RepairOrder> orders = workbenchService.getMyRepairOrderList(pageNum, pageSize, repairStatus);
            result.put("code", 200);
            result.put("message", "获取我的工单列表成功");
            result.put("data", orders);
        } catch (Exception e) {
            log.error("获取我的工单列表异常", e);
            result.put("code", 500);
            result.put("message", "获取我的工单列表失败");
        }
        return result;
    }

    /**
     * 获取工单详情
     */
    @GetMapping("/order/{orderId}")
    public Map<String, Object> getRepairOrderDetail(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该调用维修工单服务获取详情
            Map<String, Object> orderDetail = new HashMap<>();
            orderDetail.put("orderId", orderId);
            orderDetail.put("title", "模拟工单详情");
            result.put("code", 200);
            result.put("message", "获取工单详情成功");
            result.put("data", orderDetail);
        } catch (Exception e) {
            log.error("获取工单详情异常", e);
            result.put("code", 500);
            result.put("message", "获取工单详情失败");
        }
        return result;
    }

    /**
     * 接单
     */
    @PostMapping("/order/{orderId}/accept")
    public Map<String, Object> acceptOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该调用维修工单服务进行接单操作
            result.put("code", 200);
            result.put("message", "接单成功");
        } catch (Exception e) {
            log.error("接单异常", e);
            result.put("code", 500);
            result.put("message", "接单失败");
        }
        return result;
    }

    /**
     * 开始处理
     */
    @PostMapping("/order/{orderId}/start")
    public Map<String, Object> startProcessing(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该调用维修工单服务开始处理
            result.put("code", 200);
            result.put("message", "开始处理成功");
        } catch (Exception e) {
            log.error("开始处理异常", e);
            result.put("code", 500);
            result.put("message", "开始处理失败");
        }
        return result;
    }

    /**
     * 完成工单
     */
    @PostMapping("/order/{orderId}/complete")
    public Map<String, Object> completeOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该调用维修工单服务完成工单
            result.put("code", 200);
            result.put("message", "完成工单成功");
        } catch (Exception e) {
            log.error("完成工单异常", e);
            result.put("code", 500);
            result.put("message", "完成工单失败");
        }
        return result;
    }
}