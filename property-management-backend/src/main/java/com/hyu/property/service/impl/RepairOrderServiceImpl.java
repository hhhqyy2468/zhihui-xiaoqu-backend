package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.mapper.RepairOrderMapper;
import com.hyu.property.service.IRepairOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 维修工单Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements IRepairOrderService {

    /**
     * 分页查询维修工单列表
     *
     * @param page 分页参数
     * @param repairOrder 维修工单信息
     * @return 维修工单分页数据
     */
    @Override
    public Page<RepairOrder> selectRepairOrderPage(Page<RepairOrder> page, RepairOrder repairOrder) {
        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(repairOrder.getOrderNo()), "order_no", repairOrder.getOrderNo())
                   .eq(repairOrder.getUserId() != null, "user_id", repairOrder.getUserId())
                   .like(StringUtils.isNotEmpty(repairOrder.getUserName()), "user_name", repairOrder.getUserName())
                   .eq(repairOrder.getHouseId() != null, "house_id", repairOrder.getHouseId())
                   .like(StringUtils.isNotEmpty(repairOrder.getHouseNo()), "house_no", repairOrder.getHouseNo())
                   .eq(StringUtils.isNotEmpty(repairOrder.getRepairType()), "repair_type", repairOrder.getRepairType())
                   .eq(repairOrder.getUrgencyLevel() != null, "urgency_level", repairOrder.getUrgencyLevel())
                   .eq(repairOrder.getOrderStatus() != null, "order_status", repairOrder.getOrderStatus())
                   .eq(repairOrder.getWorkerId() != null, "worker_id", repairOrder.getWorkerId())
                   .like(StringUtils.isNotEmpty(repairOrder.getWorkerName()), "worker_name", repairOrder.getWorkerName())
                   .eq(repairOrder.getDeleted() != null, "deleted", 0)
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
    }

    /**
     * 校验工单编号是否唯一
     *
     * @param repairOrder 维修工单信息
     * @return 结果 true唯一 false不唯一
     */
    @Override
    public boolean checkOrderNoUnique(RepairOrder repairOrder) {
        Long repairOrderId = repairOrder.getId() == null ? -1L : repairOrder.getId();
        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", repairOrder.getOrderNo())
                   .eq("deleted", 0);

        RepairOrder info = getOne(queryWrapper);
        if (info != null && !info.getId().equals(repairOrderId)) {
            return false;
        }
        return true;
    }

    /**
     * 查询维修工单详情
     *
     * @param id 维修工单主键
     * @return 维修工单
     */
    @Override
    public RepairOrder selectRepairOrderById(Long id) {
        return getById(id);
    }

    /**
     * 根据用户ID查询维修工单列表
     *
     * @param userId 用户ID
     * @return 维修工单集合
     */
    @Override
    public List<RepairOrder> selectRepairOrdersByUserId(Long userId) {
        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 0)
                   .orderByDesc("create_time");
        return list(queryWrapper);
    }
}