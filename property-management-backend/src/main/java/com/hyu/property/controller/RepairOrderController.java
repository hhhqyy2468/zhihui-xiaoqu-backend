package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.dto.RepairOrderDTO;
import com.hyu.property.domain.vo.RepairOrderVO;
import com.hyu.property.service.IRepairOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 维修工单Controller
 *
 * @author system
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/property/repair")
@RequiredArgsConstructor
public class RepairOrderController {

    private final IRepairOrderService repairOrderService;

    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    private AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    /**
     * 查询维修工单列表
     */
    @GetMapping("/list")
    public AjaxResult list(RepairOrder repairOrder) {
        List<RepairOrderVO> list = repairOrderService.selectRepairOrderList(repairOrder);
        return success(list);
    }

    /**
     * 获取维修工单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(repairOrderService.selectRepairOrderById(id));
    }

    /**
     * 新增维修工单
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody RepairOrderDTO repairOrderDTO) {
        return toAjax(repairOrderService.insertRepairOrder(repairOrderDTO));
    }

    /**
     * 修改维修工单
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody RepairOrderDTO repairOrderDTO) {
        return toAjax(repairOrderService.updateRepairOrder(repairOrderDTO));
    }

    /**
     * 删除维修工单
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(repairOrderService.deleteRepairOrderByIds(Arrays.asList(ids)));
    }

    // ========== 业务接口 ==========

    /**
     * 业主提交报修申请
     */
    @PostMapping("/submit")
    public AjaxResult submitRepairRequest(@Validated @RequestBody RepairOrderDTO repairOrderDTO) {
        return toAjax(repairOrderService.submitRepairRequest(repairOrderDTO));
    }

    /**
     * 物业管理员派工
     */
    @PutMapping("/assign/{id}")
    public AjaxResult assignWorker(
            @PathVariable Long id,
            @RequestParam Long workerId,
            @RequestParam String workerName,
            @RequestParam(required = false) String requiredFinishTime) {

        java.time.LocalDateTime finishTime = null;
        if (requiredFinishTime != null && !requiredFinishTime.isEmpty()) {
            finishTime = java.time.LocalDateTime.parse(requiredFinishTime);
        }

        return toAjax(repairOrderService.assignWorker(id, workerId, workerName, finishTime));
    }

    /**
     * 维修人员接单
     */
    @PutMapping("/accept/{id}")
    public AjaxResult acceptOrder(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        return toAjax(repairOrderService.acceptOrder(id, currentUserId));
    }

    /**
     * 维修人员开始维修
     */
    @PutMapping("/start/{id}")
    public AjaxResult startRepair(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        return toAjax(repairOrderService.startRepair(id, currentUserId));
    }

    /**
     * 维修人员完成维修
     */
    @PutMapping("/complete")
    public AjaxResult completeRepair(@Validated @RequestBody RepairOrderDTO repairOrderDTO) {
        return toAjax(repairOrderService.completeRepair(repairOrderDTO));
    }

    /**
     * 业主验收维修
     */
    @PutMapping("/acceptance/{id}")
    public AjaxResult acceptRepair(
            @PathVariable Long id,
            @RequestParam Integer acceptanceResult,
            @RequestParam(required = false) Integer acceptanceRating,
            @RequestParam(required = false) String acceptanceComment) {

        Long currentUserId = SecurityUtils.getUserId();
        return toAjax(repairOrderService.acceptRepair(id, currentUserId, acceptanceResult, acceptanceRating, acceptanceComment));
    }

    /**
     * 业主取消报修
     */
    @DeleteMapping("/cancel/{id}")
    public AjaxResult cancelRepairOrder(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        return toAjax(repairOrderService.cancelRepairOrder(id, currentUserId));
    }

    // ========== 查询接口 ==========

    /**
     * 根据用户ID查询维修工单列表（业主）
     */
    @GetMapping("/my")
    public AjaxResult getMyRepairOrders() {
        Long currentUserId = SecurityUtils.getUserId();
        List<RepairOrderVO> list = repairOrderService.selectRepairOrdersByUserId(currentUserId);
        return success(list);
    }

    /**
     * 根据维修人员ID查询维修工单列表（维修人员）
     */
    @GetMapping("/worker")
    public AjaxResult getWorkerRepairOrders() {
        Long currentUserId = SecurityUtils.getUserId();
        List<RepairOrderVO> list = repairOrderService.selectRepairOrdersByWorkerId(currentUserId);
        return success(list);
    }

    /**
     * 查询待派工的维修工单列表（物业管理员）
     */
    @GetMapping("/pending")
    public AjaxResult getPendingRepairOrders() {
        List<RepairOrderVO> list = repairOrderService.selectPendingRepairOrders();
        return success(list);
    }

    /**
     * 查询维修统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getRepairStatistics() {
        Map<String, Object> stats = repairOrderService.getRepairStatistics();
        return success(stats);
    }

    /**
     * 查询维修人员工作量统计
     */
    @GetMapping("/workload")
    public AjaxResult getWorkerWorkloadStats() {
        List<RepairOrderVO> stats = repairOrderService.getWorkerWorkloadStats();
        return success(stats);
    }

    /**
     * 查询维修类型统计
     */
    @GetMapping("/type-stats")
    public AjaxResult getRepairTypeStats() {
        List<RepairOrderVO> stats = repairOrderService.getRepairTypeStats();
        return success(stats);
    }
}