package com.hyu.property.service;

import com.hyu.property.domain.dto.WorkbenchStatsDTO;
import com.hyu.property.domain.RepairOrder;

import java.util.List;
import java.util.Map;

/**
 * 工作台Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface IWorkbenchService {

    /**
     * 获取工作台统计数据
     *
     * @return 工作台统计数据
     */
    public WorkbenchStatsDTO getWorkbenchStats();

    /**
     * 获取我的工单列表
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param repairStatus 工单状态
     * @return 工单列表
     */
    public List<RepairOrder> getMyRepairOrderList(Integer pageNum, Integer pageSize, Integer repairStatus);
}