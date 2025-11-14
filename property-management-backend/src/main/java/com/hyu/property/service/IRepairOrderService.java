package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.RepairOrder;

import java.util.List;

/**
 * 维修工单Service接口
 *
 * @author hyu
 */
public interface IRepairOrderService extends IService<RepairOrder> {

    /**
     * 分页查询维修工单列表
     *
     * @param page 分页参数
     * @param repairOrder 维修工单信息
     * @return 维修工单分页数据
     */
    Page<RepairOrder> selectRepairOrderPage(Page<RepairOrder> page, RepairOrder repairOrder);

    /**
     * 校验工单编号是否唯一
     *
     * @param repairOrder 维修工单信息
     * @return 结果 true唯一 false不唯一
     */
    boolean checkOrderNoUnique(RepairOrder repairOrder);

    /**
     * 查询维修工单详情
     *
     * @param id 维修工单主键
     * @return 维修工单
     */
    RepairOrder selectRepairOrderById(Long id);

    /**
     * 根据用户ID查询维修工单列表
     *
     * @param userId 用户ID
     * @return 维修工单集合
     */
    List<RepairOrder> selectRepairOrdersByUserId(Long userId);
}