package com.hyu.property.service.impl;

import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.dto.WorkbenchStatsDTO;
import com.hyu.property.domain.dto.WorkbenchStatsDTO;
// import com.hyu.property.domain.vo.RepairOrder; // Use RepairOrder instead
import com.hyu.property.service.IRepairOrderService;
import com.hyu.property.service.IWorkbenchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 工作台Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbenchServiceImpl implements IWorkbenchService {

    private final IRepairOrderService repairOrderService;

    @Override
    public WorkbenchStatsDTO getWorkbenchStats() {
        WorkbenchStatsDTO stats = new WorkbenchStatsDTO();

        try {
            Long currentUserId = SecurityUtils.getUserId();

            // 获取我的工单列表
            List<RepairOrder> allOrders = repairOrderService.selectRepairOrdersByUserId(currentUserId);

            // 统计各状态工单数量
            int pendingCount = 0;        // 待接单
            int processingCount = 0;     // 进行中
            int pendingAcceptCount = 0;   // 待验收
            int completedCount = 0;      // 已完成
            int todayCompleted = 0;      // 今日完成
            int monthCompleted = 0;      // 本月完成

            LocalDateTime now = LocalDateTime.now();
            String todayStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String monthStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            for (RepairOrder order : allOrders) {
                if (order.getOrderStatus() != null) {
                    switch (order.getOrderStatus()) {
                        case 1: // 待接单
                            pendingCount++;
                            break;
                        case 2: // 进行中
                            processingCount++;
                            break;
                        case 3: // 待验收
                            pendingAcceptCount++;
                            break;
                        case 4: // 已完成
                            completedCount++;
                            // 统计今日完成数量
                            if (order.getUpdateTime() != null) {
                                String updateDate = order.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                if (todayStr.equals(updateDate)) {
                                    todayCompleted++;
                                }
                                // 统计本月完成数量
                                if (monthStr.equals(updateDate)) {
                                    monthCompleted++;
                                }
                            }
                            break;
                    }
                }
            }

            stats.setPendingCount(pendingCount);
            stats.setProcessingCount(processingCount);
            stats.setPendingAcceptCount(pendingAcceptCount);
            stats.setCompletedCount(completedCount);
            stats.setTodayCompleted(todayCompleted);
            stats.setMonthCompleted(monthCompleted);

        } catch (Exception e) {
            log.error("获取工作台统计数据异常", e);
            // 返回默认值
            stats.setPendingCount(0);
            stats.setProcessingCount(0);
            stats.setPendingAcceptCount(0);
            stats.setCompletedCount(0);
            stats.setTodayCompleted(0);
            stats.setMonthCompleted(0);
        }

        return stats;
    }

    @Override
    public List<RepairOrder> getMyRepairOrderList(Integer pageNum, Integer pageSize, Integer repairStatus) {
        try {
            Long currentUserId = SecurityUtils.getUserId();
            List<RepairOrder> allOrders = repairOrderService.selectRepairOrdersByUserId(currentUserId);

            // 如果指定了状态，则进行过滤
            if (repairStatus != null) {
                List<RepairOrder> filteredOrders = new ArrayList<>();
                for (RepairOrder order : allOrders) {
                    if (order.getOrderStatus() != null && order.getOrderStatus().equals(repairStatus)) {
                        filteredOrders.add(order);
                    }
                }

                // 分页处理
                int start = (pageNum - 1) * pageSize;
                int end = Math.min(start + pageSize, filteredOrders.size());

                if (start < filteredOrders.size()) {
                    return filteredOrders.subList(start, end);
                } else {
                    return new ArrayList<>();
                }
            }

            // 分页处理
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, allOrders.size());

            if (start < allOrders.size()) {
                return allOrders.subList(start, end);
            } else {
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("获取我的工单列表异常", e);
            return new ArrayList<>();
        }
    }
}