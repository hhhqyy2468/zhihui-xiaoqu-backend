package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.service.IRepairOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 维修工单管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/repair")
@Validated
public class RepairOrderController {

    @Autowired
    private IRepairOrderService repairOrderService;

    /**
     * 分页查询维修工单列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String orderNo,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) String userName,
                          @RequestParam(required = false) Long houseId,
                          @RequestParam(required = false) String houseNo,
                          @RequestParam(required = false) String repairType,
                          @RequestParam(required = false) Integer urgencyLevel,
                          @RequestParam(required = false) Integer orderStatus,
                          @RequestParam(required = false) Long workerId,
                          @RequestParam(required = false) String workerName) {
        log.info("分页查询维修工单列表, pageNum: {}, pageSize: {}, orderNo: {}, userId: {}, userName: {}, houseId: {}, houseNo: {}, repairType: {}, urgencyLevel: {}, orderStatus: {}, workerId: {}, workerName: {}",
                pageNum, pageSize, orderNo, userId, userName, houseId, houseNo, repairType, urgencyLevel, orderStatus, workerId, workerName);

        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setOrderNo(orderNo);
        repairOrder.setUserId(userId);
        repairOrder.setUserName(userName);
        repairOrder.setHouseId(houseId);
        repairOrder.setHouseNo(houseNo);
        repairOrder.setRepairType(repairType);
        repairOrder.setUrgencyLevel(urgencyLevel);
        repairOrder.setOrderStatus(orderStatus);
        repairOrder.setWorkerId(workerId);
        repairOrder.setWorkerName(workerName);

        Page<RepairOrder> result = repairOrderService.selectRepairOrderPage(page, repairOrder);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取维修工单详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult getInfo(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id) {
        log.info("获取维修工单详细信息, id: {}", id);
        return AjaxResult.success(repairOrderService.getById(id));
    }

    /**
     * 新增维修工单
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:repair:add')")
    public AjaxResult add(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("新增维修工单, repairOrder: {}", repairOrder);
        if (!repairOrderService.checkOrderNoUnique(repairOrder)) {
            return AjaxResult.error("新增维修工单'" + repairOrder.getOrderNo() + "'失败，工单编号已存在");
        }
        repairOrder.setCreateBy(SecurityUtils.getUsername());
        return toAjax(repairOrderService.save(repairOrder));
    }

    /**
     * 修改保存维修工单
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    public AjaxResult edit(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("修改维修工单, repairOrder: {}", repairOrder);
        if (!repairOrderService.checkOrderNoUnique(repairOrder)) {
            return AjaxResult.error("修改维修工单'" + repairOrder.getOrderNo() + "'失败，工单编号已存在");
        }
        repairOrder.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(repairOrderService.updateById(repairOrder));
    }

    /**
     * 删除维修工单
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:repair:delete')")
    public AjaxResult remove(@NotNull(message = "维修工单ID不能为空") @PathVariable Long[] ids) {
        log.info("删除维修工单, ids: {}", ids);
        return toAjax(repairOrderService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}