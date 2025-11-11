package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.vo.RepairOrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 维修工单Mapper接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 查询维修工单列表（关联查询）
     *
     * @param repairOrder 维修工单
     * @return 维修工单集合
     */
    List<RepairOrderVO> selectRepairOrderList(RepairOrder repairOrder);

    /**
     * 查询维修工单详情（关联查询）
     *
     * @param id 维修工单主键
     * @return 维修工单
     */
    RepairOrderVO selectRepairOrderById(@Param("id") Long id);

    /**
     * 根据用户ID查询维修工单列表
     *
     * @param userId 用户ID
     * @return 维修工单集合
     */
    List<RepairOrderVO> selectRepairOrdersByUserId(@Param("userId") Long userId);

    /**
     * 根据维修人员ID查询维修工单列表
     *
     * @param workerId 维修人员ID
     * @return 维修工单集合
     */
    List<RepairOrderVO> selectRepairOrdersByWorkerId(@Param("workerId") Long workerId);

    /**
     * 根据状态查询维修工单数量
     *
     * @param orderStatus 工单状态
     * @return 数量
     */
    Integer countByOrderStatus(@Param("orderStatus") Integer orderStatus);

    /**
     * 查询待派工的维修工单列表
     *
     * @return 维修工单集合
     */
    List<RepairOrderVO> selectPendingRepairOrders();

    /**
     * 查询维修人员工作量统计
     *
     * @return 统计结果
     */
    List<RepairOrderVO> selectWorkerWorkloadStats();

    /**
     * 查询维修类型统计
     *
     * @return 统计结果
     */
    List<RepairOrderVO> selectRepairTypeStats();

    /**
     * 更新工单状态
     *
     * @param id 工单ID
     * @param orderStatus 工单状态
     * @return 更新结果
     */
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus);

    /**
     * 增加返工次数
     *
     * @param id 工单ID
     * @return 更新结果
     */
    int incrementReworkCount(@Param("id") Long id);
}