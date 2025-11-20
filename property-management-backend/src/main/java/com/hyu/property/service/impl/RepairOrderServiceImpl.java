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
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
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

    @Autowired
    private ISysUserService userService;

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

        // 设置报修时间和真实姓名（前端显示用）
        resultPage.getRecords().forEach(order -> {
            if (order.getCreateTime() != null) {
                order.setReportTime(order.getCreateTime());
            }

            // 获取用户真实姓名
            if (order.getUserId() != null) {
                SysUser user = userService.getById(order.getUserId());
                if (user != null && StringUtils.isNotEmpty(user.getRealName())) {
                    order.setRealName(user.getRealName());
                }
            }

            // 获取维修人员电话
            if (order.getWorkerId() != null) {
                SysUser worker = userService.getById(order.getWorkerId());
                if (worker != null && StringUtils.isNotEmpty(worker.getPhone())) {
                    order.setWorkerPhone(worker.getPhone());
                }
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
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null) {
            return null;
        }

        // 设置报修时间和真实姓名
        if (repairOrder.getCreateTime() != null) {
            repairOrder.setReportTime(repairOrder.getCreateTime());
        }

        // 获取用户真实姓名
        if (repairOrder.getUserId() != null) {
            SysUser user = userService.getById(repairOrder.getUserId());
            if (user != null && StringUtils.isNotEmpty(user.getRealName())) {
                repairOrder.setRealName(user.getRealName());
            }
        }

        // 获取维修人员电话
        if (repairOrder.getWorkerId() != null) {
            SysUser worker = userService.getById(repairOrder.getWorkerId());
            if (worker != null && StringUtils.isNotEmpty(worker.getPhone())) {
                repairOrder.setWorkerPhone(worker.getPhone());
            }
        }

        return repairOrder;
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
     * 获取维修人员列表
     */
    @Override
    public List<Map<String, Object>> getRepairerList() {
        try {
            // 查询所有状态正常的维修人员 (user_type = 4)
            List<Map<String, Object>> repairers = userService.list(
                new QueryWrapper<SysUser>()
                    .eq("status", 1) // 正常状态
                    .eq("user_type", 4) // 维修人员
                    .orderByAsc("real_name")
            ).stream()
            .filter(user -> user.getRealName() != null && !user.getRealName().isEmpty())
            .map(user -> {
                Map<String, Object> repairer = new HashMap<>();
                repairer.put("value", user.getUserId());
                repairer.put("label", user.getRealName() + (user.getPhone() != null ? "-" + user.getPhone() : ""));
                repairer.put("realName", user.getRealName());
                repairer.put("phone", user.getPhone());
                return repairer;
            })
            .collect(Collectors.toList());

            log.info("获取到{}个维修人员", repairers.size());
            return repairers;
        } catch (Exception e) {
            log.error("获取维修人员列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 系统管理员派工
     */
    @Override
    public boolean assignOrder(Long id, Map<String, Object> params) {
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null || repairOrder.getOrderStatus() != 0) {
            log.warn("维修工单状态不正确，无法派工: {}", id);
            return false;
        }

        try {
            // 获取派工参数
            Long repairerId = Long.valueOf(params.get("repairerId").toString());
            String expectedCompleteTime = params.get("expectedCompleteTime").toString();
            BigDecimal repairCost = new BigDecimal(params.get("repairCost").toString());
            String remark = params.get("remark") != null ? params.get("remark").toString() : null;

            // 获取维修人员信息
            SysUser worker = userService.getById(repairerId);
            if (worker == null) {
                log.warn("维修人员不存在: {}", repairerId);
                return false;
            }

            // 更新工单信息
            repairOrder.setWorkerId(repairerId);
            repairOrder.setWorkerName(worker.getRealName());
            repairOrder.setRequiredFinishTime(LocalDateTime.parse(expectedCompleteTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            repairOrder.setRepairCost(repairCost);
            repairOrder.setAssignTime(LocalDateTime.now());
            repairOrder.setOrderStatus(1); // 状态改为已派工
            repairOrder.setUpdateBy(SecurityUtils.getUsername());

            return updateById(repairOrder);
        } catch (Exception e) {
            log.error("派工失败: {}", e.getMessage(), e);
            return false;
        }
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
        repairOrder.setActualFault((String) params.get("faultReason")); // 前端发送的是 faultReason
        repairOrder.setRepairContent((String) params.get("faultReason")); // 使用故障原因作为维修内容
        repairOrder.setPartsReplaced((String) params.get("partsUsed")); // 前端发送的是 partsUsed

        // 处理维修费用 - 只有维修师傅提供了费用才更新，否则保持派工时设定的费用
        if (params.get("repairCost") != null && StringUtils.isNotEmpty(params.get("repairCost").toString())) {
            String repairCostStr = params.get("repairCost").toString();
            BigDecimal repairCost = new BigDecimal(repairCostStr);
            repairOrder.setRepairCost(repairCost);
        }
        // 如果没有提供费用，保持原有的repairCost不变

        // 保存维修后图片信息（如果有）
        String afterImages = (String) params.get("afterImages");
        if (StringUtils.isNotEmpty(afterImages)) {
            repairOrder.setRepairImageUrls(afterImages);
        }

        repairOrder.setFinishTime(LocalDateTime.now());
        repairOrder.setOrderStatus(4); // 状态改为待验收
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
     * 业主评价维修工单
     */
    @Override
    public boolean rateOrder(Long id, Map<String, Object> params) {
        RepairOrder repairOrder = getById(id);
        if (repairOrder == null || repairOrder.getOrderStatus() != 4) {
            log.warn("维修工单状态不正确，无法评价: {}", id);
            return false;
        }

        // 更新评价信息
        repairOrder.setServiceRating(Integer.valueOf(params.get("serviceRating").toString()));
        repairOrder.setResponseRating(Integer.valueOf(params.get("responseRating").toString()));
        repairOrder.setProfessionalRating(Integer.valueOf(params.get("professionalRating").toString()));
        repairOrder.setOverallRating(Integer.valueOf(params.get("overallRating").toString()));
        repairOrder.setComment((String) params.get("comment"));
        repairOrder.setRatingTime(LocalDateTime.now());
        repairOrder.setOrderStatus(5); // 状态改为已归档
        repairOrder.setUpdateBy(SecurityUtils.getUsername());

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