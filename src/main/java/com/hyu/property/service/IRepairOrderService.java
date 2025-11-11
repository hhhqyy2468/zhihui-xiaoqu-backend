package com.hyu.property.service;

import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.dto.RepairOrderDTO;
import com.hyu.property.domain.vo.RepairOrderVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 维修工单Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface IRepairOrderService extends IService<RepairOrder> {

    /**
     * 查询维修工单
     *
     * @param id 维修工单主键
     * @return 维修工单
     */
    public RepairOrderVO selectRepairOrderById(Long id);

    /**
     * 查询维修工单列表
     *
     * @param repairOrder 维修工单
     * @return 维修工单集合
     */
    public List<RepairOrderVO> selectRepairOrderList(RepairOrder repairOrder);

    /**
     * 新增维修工单
     *
     * @param repairOrderDTO 维修工单
     * @return 结果
     */
    public int insertRepairOrder(RepairOrderDTO repairOrderDTO);

    /**
     * 修改维修工单
     *
     * @param repairOrderDTO 维修工单
     * @return 结果
     */
    public int updateRepairOrder(RepairOrderDTO repairOrderDTO);

    /**
     * 批量删除维修工单
     *
     * @param ids 需要删除的维修工单主键集合
     * @return 结果
     */
    public int deleteRepairOrderByIds(List<Long> ids);

    /**
     * 删除维修工单信息
     *
     * @param id 维修工单主键
     * @return 结果
     */
    public int deleteRepairOrderById(Long id);

    // ========== 业务方法 ==========

    /**
     * 业主提交报修申请
     *
     * @param repairOrderDTO 报修信息
     * @return 结果
     */
    public int submitRepairRequest(RepairOrderDTO repairOrderDTO);

    /**
     * 物业管理员派工
     *
     * @param id 工单ID
     * @param workerId 维修人员ID
     * @param workerName 维修人员姓名
     * @param requiredFinishTime 要求完成时间
     * @return 结果
     */
    public int assignWorker(Long id, Long workerId, String workerName, java.time.LocalDateTime requiredFinishTime);

    /**
     * 维修人员接单
     *
     * @param id 工单ID
     * @param workerId 维修人员ID
     * @return 结果
     */
    public int acceptOrder(Long id, Long workerId);

    /**
     * 维修人员开始维修
     *
     * @param id 工单ID
     * @param workerId 维修人员ID
     * @return 结果
     */
    public int startRepair(Long id, Long workerId);

    /**
     * 维修人员完成维修
     *
     * @param repairOrderDTO 维修完成信息
     * @return 结果
     */
    public int completeRepair(RepairOrderDTO repairOrderDTO);

    /**
     * 业主验收维修
     *
     * @param id 工单ID
     * @param userId 业主ID
     * @param acceptanceResult 验收结果
     * @param acceptanceRating 验收评分
     * @param acceptanceComment 验收意见
     * @return 结果
     */
    public int acceptRepair(Long id, Long userId, Integer acceptanceResult, Integer acceptanceRating, String acceptanceComment);

    /**
     * 自动验收维修（3天后自动验收）
     *
     * @param id 工单ID
     * @return 结果
     */
    public int autoAcceptRepair(Long id);

    /**
     * 业主取消报修
     *
     * @param id 工单ID
     * @param userId 业主ID
     * @return 结果
     */
    public int cancelRepairOrder(Long id, Long userId);

    /**
     * 根据用户ID查询维修工单列表
     *
     * @param userId 用户ID
     * @return 维修工单集合
     */
    public List<RepairOrderVO> selectRepairOrdersByUserId(Long userId);

    /**
     * 根据维修人员ID查询维修工单列表
     *
     * @param workerId 维修人员ID
     * @return 维修工单集合
     */
    public List<RepairOrderVO> selectRepairOrdersByWorkerId(Long workerId);

    /**
     * 查询待派工的维修工单列表
     *
     * @return 维修工单集合
     */
    public List<RepairOrderVO> selectPendingRepairOrders();

    /**
     * 查询维修统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getRepairStatistics();

    /**
     * 查询维修人员工作量统计
     *
     * @return 工作量统计
     */
    public List<RepairOrderVO> getWorkerWorkloadStats();

    /**
     * 查询维修类型统计
     *
     * @return 维修类型统计
     */
    public List<RepairOrderVO> getRepairTypeStats();

    /**
     * 定时任务：检查待验收的工单，自动验收
     */
    public void checkAutoAcceptRepairOrders();

    /**
     * 定时任务：检查超期的工单
     */
    public void checkOverdueRepairOrders();
}