package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.RepairOrder;

import java.util.List;
import java.util.Map;

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

    /**
     * 维修师傅接单
     *
     * @param id 维修工单ID
     * @return 结果
     */
    boolean acceptOrder(Long id);

    /**
     * 完成维修
     *
     * @param id 维修工单ID
     * @param params 完成维修参数
     * @return 结果
     */
    boolean completeOrder(Long id, Map<String, Object> params);

    /**
     * 验收维修
     *
     * @param id 维修工单ID
     * @param params 验收参数
     * @return 结果
     */
    boolean inspectOrder(Long id, Map<String, Object> params);
}