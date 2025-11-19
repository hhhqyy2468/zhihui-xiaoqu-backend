package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.domain.Bill;
import com.hyu.property.mapper.RepairOrderMapper;
import com.hyu.property.service.IRepairOrderService;
import com.hyu.property.service.IBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 维修工单Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements IRepairOrderService {

    @Autowired
    private IBillService billService;

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

        // 精确匹配条件
        queryWrapper.eq(StringUtils.isNotEmpty(repairOrder.getOrderNo()), "order_no", repairOrder.getOrderNo())
                   .eq(repairOrder.getUserId() != null, "user_id", repairOrder.getUserId())
                   .eq(repairOrder.getHouseId() != null, "house_id", repairOrder.getHouseId())
                   .eq(StringUtils.isNotEmpty(repairOrder.getRepairType()), "repair_type", repairOrder.getRepairType())
                   .eq(repairOrder.getUrgencyLevel() != null, "urgency_level", repairOrder.getUrgencyLevel())
                   .eq(repairOrder.getOrderStatus() != null, "order_status", repairOrder.getOrderStatus())
                   .eq(repairOrder.getWorkerId() != null, "worker_id", repairOrder.getWorkerId())
                   .eq(StringUtils.isNotEmpty(repairOrder.getPhone()), "phone", repairOrder.getPhone());

        // 模糊匹配条件
        queryWrapper.like(StringUtils.isNotEmpty(repairOrder.getUserName()), "user_name", repairOrder.getUserName())
                   .like(StringUtils.isNotEmpty(repairOrder.getHouseNo()), "house_no", repairOrder.getHouseNo())
                   .like(StringUtils.isNotEmpty(repairOrder.getWorkerName()), "worker_name", repairOrder.getWorkerName())
                   .like(StringUtils.isNotEmpty(repairOrder.getFaultDescription()), "fault_description", repairOrder.getFaultDescription());

        // 日期范围查询 - 创建时间
        if (repairOrder.getParams() != null) {
            Map<String, Object> params = repairOrder.getParams();
            if (params.get("beginTime") != null && StringUtils.isNotEmpty(params.get("beginTime").toString())) {
                queryWrapper.ge("create_time", params.get("beginTime"));
            }
            if (params.get("endTime") != null && StringUtils.isNotEmpty(params.get("endTime").toString())) {
                queryWrapper.le("create_time", params.get("endTime"));
            }

            // 日期范围查询 - 预约时间
            if (params.get("beginAppointmentTime") != null && StringUtils.isNotEmpty(params.get("beginAppointmentTime").toString())) {
                queryWrapper.ge("appointment_time", params.get("beginAppointmentTime"));
            }
            if (params.get("endAppointmentTime") != null && StringUtils.isNotEmpty(params.get("endAppointmentTime").toString())) {
                queryWrapper.le("appointment_time", params.get("endAppointmentTime"));
            }

            // 日期范围查询 - 完成时间
            if (params.get("beginFinishTime") != null && StringUtils.isNotEmpty(params.get("beginFinishTime").toString())) {
                queryWrapper.ge("finish_time", params.get("beginFinishTime"));
            }
            if (params.get("endFinishTime") != null && StringUtils.isNotEmpty(params.get("endFinishTime").toString())) {
                queryWrapper.le("finish_time", params.get("endFinishTime"));
            }

            // 维修费用范围查询
            if (params.get("minRepairCost") != null && StringUtils.isNotEmpty(params.get("minRepairCost").toString())) {
                queryWrapper.ge("repair_cost", new BigDecimal(params.get("minRepairCost").toString()));
            }
            if (params.get("maxRepairCost") != null && StringUtils.isNotEmpty(params.get("maxRepairCost").toString())) {
                queryWrapper.le("repair_cost", new BigDecimal(params.get("maxRepairCost").toString()));
            }
        }

        // 排除已删除的数据
        queryWrapper.eq("deleted", 0);

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");

        Page<RepairOrder> resultPage = page(page, queryWrapper);

        // 设置报修时间为创建时间（前端显示用）
        resultPage.getRecords().forEach(order -> {
            if (order.getCreateTime() != null) {
                order.setReportTime(order.getCreateTime());
            }
        });

        return resultPage;
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

    /**
     * 维修师傅接单
     */
    @Override
    public boolean acceptOrder(Long id) {
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null || repairOrder.getOrderStatus() != 1) {
            log.warn("维修工单状态不正确，无法接单: {}", id);
            return false;
        }

        repairOrder.setOrderStatus(2); // 状态改为进行中
        repairOrder.setUpdateBy(SecurityUtils.getUsername());
        return updateById(repairOrder);
    }

    /**
     * 完成维修
     */
    @Override
    public boolean completeOrder(Long id, Map<String, Object> params) {
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null || repairOrder.getOrderStatus() != 2) {
            log.warn("维修工单状态不正确，无法完成维修: {}", id);
            return false;
        }

        // 更新维修信息
        repairOrder.setActualFault((String) params.get("actualFault"));
        repairOrder.setRepairContent((String) params.get("repairContent"));
        repairOrder.setRepairCost(new BigDecimal(params.get("repairCost").toString()));
        repairOrder.setPartsReplaced((String) params.get("partsReplaced"));
        repairOrder.setFinishTime(LocalDateTime.now());
        repairOrder.setOrderStatus(3); // 状态改为待验收
        repairOrder.setUpdateBy(SecurityUtils.getUsername());

        return updateById(repairOrder);
    }

    /**
     * 验收维修
     */
    @Override
    public boolean inspectOrder(Long id, Map<String, Object> params) {
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null || repairOrder.getOrderStatus() != 3) {
            log.warn("维修工单状态不正确，无法验收: {}", id);
            return false;
        }

        // 更新验收信息
        repairOrder.setAcceptanceResult(Integer.valueOf(params.get("acceptanceResult").toString()));
        repairOrder.setAcceptanceRating(Integer.valueOf(params.get("acceptanceRating").toString()));
        repairOrder.setAcceptanceComment((String) params.get("acceptanceComment"));
        repairOrder.setAcceptanceTime(LocalDateTime.now());
        repairOrder.setOrderStatus(4); // 状态改为已完成
        repairOrder.setUpdateBy(SecurityUtils.getUsername());

        // 验收通过且维修费用大于0时，自动生成账单
        if (repairOrder.getAcceptanceResult() == 1 &&
            repairOrder.getRepairCost() != null &&
            repairOrder.getRepairCost().compareTo(BigDecimal.ZERO) > 0) {

            Long billId = generateRepairBill(repairOrder);
            if (billId != null) {
                repairOrder.setBillId(billId);
                log.info("维修验收通过，已生成账单: 工单ID={}, 账单ID={}", id, billId);
            }
        }

        return updateById(repairOrder);
    }

    /**
     * 生成维修费用账单
     */
    private Long generateRepairBill(RepairOrder repairOrder) {
        try {
            Bill bill = new Bill();
            // 生成账单编号
            String billNo = "REPAIR_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                          "_" + String.format("%04d", new Random().nextInt(10000));
            bill.setBillNo(billNo);

            // 设置账单信息
            bill.setOwnerId(repairOrder.getUserId());
            bill.setHouseId(repairOrder.getHouseId());
            bill.setFeeTypeId(12L); // 维修费用类型ID
            bill.setBillPeriod(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            bill.setAmount(repairOrder.getRepairCost());
            bill.setBillStatus(1); // 待缴费
            // 转换LocalDateTime为Date
            bill.setDueDate(java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(7)));
            bill.setRemark("维修工单：" + repairOrder.getOrderNo());

            // 保存账单
            int result = billService.insertBill(bill);
            if (result > 0) {
                return bill.getBillId();
            }
        } catch (Exception e) {
            log.error("生成维修费用账单失败", e);
        }
        return null;
    }
}