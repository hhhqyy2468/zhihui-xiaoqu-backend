package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.dto.RepairOrderDTO;
import com.hyu.property.domain.vo.RepairOrderVO;
import com.hyu.property.mapper.RepairOrderMapper;
import com.hyu.property.service.IRepairOrderService;
import com.hyu.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维修工单Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements IRepairOrderService {

    private final RepairOrderMapper repairOrderMapper;
    private final ISysUserService userService;

    /**
     * 查询维修工单
     *
     * @param id 维修工单主键
     * @return 维修工单
     */
    @Override
    public RepairOrderVO selectRepairOrderById(Long id) {
        return repairOrderMapper.selectRepairOrderById(id);
    }

    /**
     * 查询维修工单列表
     *
     * @param repairOrder 维修工单
     * @return 维修工单
     */
    @Override
    public List<RepairOrderVO> selectRepairOrderList(RepairOrder repairOrder) {
        return repairOrderMapper.selectRepairOrderList(repairOrder);
    }

    /**
     * 新增维修工单
     *
     * @param repairOrderDTO 维修工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepairOrder(RepairOrderDTO repairOrderDTO) {
        RepairOrder repairOrder = new RepairOrder();
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        return repairOrderMapper.insert(repairOrder);
    }

    /**
     * 修改维修工单
     *
     * @param repairOrderDTO 维修工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRepairOrder(RepairOrderDTO repairOrderDTO) {
        RepairOrder repairOrder = new RepairOrder();
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        return repairOrderMapper.updateById(repairOrder);
    }

    /**
     * 批量删除维修工单
     *
     * @param ids 需要删除的维修工单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepairOrderByIds(List<Long> ids) {
        return repairOrderMapper.deleteBatchIds(ids);
    }

    /**
     * 删除维修工单信息
     *
     * @param id 维修工单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepairOrderById(Long id) {
        return repairOrderMapper.deleteById(id);
    }

    // ========== 业务方法 ==========

    /**
     * 业主提交报修申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitRepairRequest(RepairOrderDTO repairOrderDTO) {
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserName = SecurityUtils.getUsername();

        // 查询用户房产信息
        // TODO: 根据houseId查询房产信息，验证权限

        RepairOrder repairOrder = new RepairOrder();
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        repairOrder.setUserId(currentUserId);
        repairOrder.setUserName(currentUserName);
        repairOrder.setOrderStatus(1); // 待派工
        repairOrder.setReworkCount(0);
        repairOrder.setDeleted(0);

        return repairOrderMapper.insert(repairOrder);
    }

    /**
     * 物业管理员派工
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignWorker(Long id, Long workerId, String workerName, LocalDateTime requiredFinishTime) {
        RepairOrder updateOrder = new RepairOrder();
        updateOrder.setId(id);
        updateOrder.setWorkerId(workerId);
        updateOrder.setWorkerName(workerName);
        updateOrder.setRequiredFinishTime(requiredFinishTime);
        updateOrder.setOrderStatus(2); // 已派工
        updateOrder.setAssignTime(LocalDateTime.now());

        return repairOrderMapper.updateById(updateOrder);
    }

    /**
     * 维修人员接单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int acceptOrder(Long id, Long workerId) {
        RepairOrder repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            throw new RuntimeException("维修工单不存在");
        }
        if (!repairOrder.getWorkerId().equals(workerId)) {
            throw new RuntimeException("无权操作此工单");
        }
        if (!repairOrder.getOrderStatus().equals(2)) {
            throw new RuntimeException("工单状态不正确，无法接单");
        }

        RepairOrder updateOrder = new RepairOrder();
        updateOrder.setId(id);
        updateOrder.setOrderStatus(3); // 进行中
        return repairOrderMapper.updateById(updateOrder);
    }

    /**
     * 维修人员开始维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startRepair(Long id, Long workerId) {
        RepairOrder repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            throw new RuntimeException("维修工单不存在");
        }
        if (!repairOrder.getWorkerId().equals(workerId)) {
            throw new RuntimeException("无权操作此工单");
        }
        if (!repairOrder.getOrderStatus().equals(3)) {
            throw new RuntimeException("工单状态不正确，无法开始维修");
        }

        // 更新状态为进行中
        return repairOrderMapper.updateOrderStatus(id, 3);
    }

    /**
     * 维修人员完成维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeRepair(RepairOrderDTO repairOrderDTO) {
        Long currentUserId = SecurityUtils.getUserId();
        RepairOrder repairOrder = repairOrderMapper.selectById(repairOrderDTO.getId());

        if (repairOrder == null) {
            throw new RuntimeException("维修工单不存在");
        }
        if (!repairOrder.getWorkerId().equals(currentUserId)) {
            throw new RuntimeException("无权操作此工单");
        }
        if (!repairOrder.getOrderStatus().equals(3)) {
            throw new RuntimeException("工单状态不正确，无法完成维修");
        }

        RepairOrder updateOrder = new RepairOrder();
        updateOrder.setId(repairOrderDTO.getId());
        updateOrder.setActualFault(repairOrderDTO.getActualFault());
        updateOrder.setRepairContent(repairOrderDTO.getRepairContent());
        updateOrder.setRepairImageUrls(repairOrderDTO.getRepairImageUrls());
        updateOrder.setRepairCost(repairOrderDTO.getRepairCost());
        updateOrder.setPartsReplaced(repairOrderDTO.getPartsReplaced());
        updateOrder.setOrderStatus(4); // 待验收
        updateOrder.setFinishTime(LocalDateTime.now());
        updateOrder.setAutoAcceptTime(LocalDateTime.now().plusDays(3)); // 3天后自动验收

        return repairOrderMapper.updateById(updateOrder);
    }

    /**
     * 业主验收维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int acceptRepair(Long id, Long userId, Integer acceptanceResult, Integer acceptanceRating, String acceptanceComment) {
        RepairOrder repairOrder = repairOrderMapper.selectById(id);

        if (repairOrder == null) {
            throw new RuntimeException("维修工单不存在");
        }
        if (!repairOrder.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此工单");
        }
        if (!repairOrder.getOrderStatus().equals(4)) {
            throw new RuntimeException("工单状态不正确，无法验收");
        }

        RepairOrder updateOrder = new RepairOrder();
        updateOrder.setId(id);
        updateOrder.setAcceptanceResult(acceptanceResult);
        updateOrder.setAcceptanceRating(acceptanceRating);
        updateOrder.setAcceptanceComment(acceptanceComment);
        updateOrder.setAcceptanceTime(LocalDateTime.now());

        if (acceptanceResult == 1) {
            // 验收合格
            updateOrder.setOrderStatus(5); // 已完成
        } else {
            // 验收不合格，增加返工次数
            updateOrder.setOrderStatus(3); // 进行中
            repairOrderMapper.incrementReworkCount(id);

            // 检查返工次数，最多返工2次
            if (repairOrder.getReworkCount() >= 2) {
                throw new RuntimeException("返工次数已达上限，请联系管理员");
            }
        }

        return repairOrderMapper.updateById(updateOrder);
    }

    /**
     * 自动验收维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoAcceptRepair(Long id) {
        RepairOrder repairOrder = repairOrderMapper.selectById(id);

        if (repairOrder == null || !repairOrder.getOrderStatus().equals(4)) {
            return 0;
        }

        RepairOrder updateOrder = new RepairOrder();
        updateOrder.setId(id);
        updateOrder.setAcceptanceResult(1); // 默认合格
        updateOrder.setAcceptanceRating(5); // 默认5星
        updateOrder.setAcceptanceComment("系统自动验收");
        updateOrder.setAcceptanceTime(LocalDateTime.now());
        updateOrder.setOrderStatus(5); // 已完成

        return repairOrderMapper.updateById(updateOrder);
    }

    /**
     * 业主取消报修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelRepairOrder(Long id, Long userId) {
        RepairOrder repairOrder = repairOrderMapper.selectById(id);

        if (repairOrder == null) {
            throw new RuntimeException("维修工单不存在");
        }
        if (!repairOrder.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此工单");
        }

        // 只有待派工状态才能取消
        if (!repairOrder.getOrderStatus().equals(1)) {
            throw new RuntimeException("工单已派工，无法取消");
        }

        return repairOrderMapper.deleteById(id);
    }

    /**
     * 根据用户ID查询维修工单列表
     */
    @Override
    public List<RepairOrderVO> selectRepairOrdersByUserId(Long userId) {
        return repairOrderMapper.selectRepairOrdersByUserId(userId);
    }

    /**
     * 根据维修人员ID查询维修工单列表
     */
    @Override
    public List<RepairOrderVO> selectRepairOrdersByWorkerId(Long workerId) {
        return repairOrderMapper.selectRepairOrdersByWorkerId(workerId);
    }

    /**
     * 查询待派工的维修工单列表
     */
    @Override
    public List<RepairOrderVO> selectPendingRepairOrders() {
        return repairOrderMapper.selectPendingRepairOrders();
    }

    /**
     * 查询维修统计信息
     */
    @Override
    public Map<String, Object> getRepairStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("pendingCount", repairOrderMapper.countByOrderStatus(1)); // 待派工
        stats.put("assignedCount", repairOrderMapper.countByOrderStatus(2)); // 已派工
        stats.put("processingCount", repairOrderMapper.countByOrderStatus(3)); // 进行中
        stats.put("waitingAcceptanceCount", repairOrderMapper.countByOrderStatus(4)); // 待验收
        stats.put("completedCount", repairOrderMapper.countByOrderStatus(5)); // 已完成

        return stats;
    }

    /**
     * 查询维修人员工作量统计
     */
    @Override
    public List<RepairOrderVO> getWorkerWorkloadStats() {
        return repairOrderMapper.selectWorkerWorkloadStats();
    }

    /**
     * 查询维修类型统计
     */
    @Override
    public List<RepairOrderVO> getRepairTypeStats() {
        return repairOrderMapper.selectRepairTypeStats();
    }

    /**
     * 定时任务：检查待验收的工单，自动验收
     */
    @Override
    public void checkAutoAcceptRepairOrders() {
        log.info("开始检查待验收的维修工单，执行自动验收");
        // TODO: 查询超过3天的待验收工单，执行自动验收
        log.info("自动验收检查完成");
    }

    /**
     * 定时任务：检查超期的工单
     */
    @Override
    public void checkOverdueRepairOrders() {
        log.info("开始检查超期的维修工单");
        // TODO: 查询超期的工单，发送提醒
        log.info("超期工单检查完成");
    }
}