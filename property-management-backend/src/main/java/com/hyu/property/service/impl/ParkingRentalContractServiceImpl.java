package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.*;
import com.hyu.property.mapper.ParkingRentalContractMapper;
import com.hyu.property.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 车位租赁合同Service业务层处理
 *
 * @author system
 * @date 2025-01-31
 */
@Slf4j
@Service
public class ParkingRentalContractServiceImpl extends ServiceImpl<ParkingRentalContractMapper, ParkingRentalContract>
        implements IParkingRentalContractService {

    @Autowired
    private IParkingRentalApplicationService applicationService;

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private IBillService billService;

    @Autowired
    private IFeeTypeService feeTypeService;

    @Autowired
    private IOwnerService ownerService;

    @Autowired
    private IUserHouseService userHouseService;

    @Override
    public Page<ParkingRentalContract> selectContractPage(Page<ParkingRentalContract> page, ParkingRentalContract contract) {
        QueryWrapper<ParkingRentalContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.isNotEmpty(contract.getContractNo())) {
            queryWrapper.like("contract_no", contract.getContractNo());
        }
        if (StringUtils.isNotEmpty(contract.getSpaceNo())) {
            queryWrapper.like("space_no", contract.getSpaceNo());
        }
        if (StringUtils.isNotEmpty(contract.getOwnerName())) {
            queryWrapper.like("owner_name", contract.getOwnerName());
        }
        if (StringUtils.isNotEmpty(contract.getVehicleNumber())) {
            queryWrapper.like("vehicle_number", contract.getVehicleNumber());
        }
        if (contract.getContractStatus() != null) {
            queryWrapper.eq("contract_status", contract.getContractStatus());
        }

        queryWrapper.orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public List<ParkingRentalContract> selectContractList(ParkingRentalContract contract) {
        return baseMapper.selectContractList(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingRentalContract generateContract(Long applicationId) {
        // 1. 查询申请信息
        ParkingRentalApplication application = applicationService.selectParkingRentalApplicationById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        // 2. 查询车位信息
        ParkingSpace parkingSpace = parkingSpaceService.getById(application.getParkingSpaceId());
        if (parkingSpace == null) {
            throw new RuntimeException("车位不存在");
        }

        // 3. 计算租赁结束日期和总金额
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(application.getRentalStartDate());
        calendar.add(Calendar.MONTH, application.getRentalMonths());
        Date endDate = calendar.getTime();

        BigDecimal totalAmount = parkingSpace.getMonthlyRent()
                .multiply(new BigDecimal(application.getRentalMonths()));

        // 4. 创建合同
        ParkingRentalContract contract = new ParkingRentalContract();
        contract.setContractNo(generateContractNo());
        contract.setApplicationId(applicationId);
        contract.setParkingSpaceId(application.getParkingSpaceId());
        contract.setSpaceNo(application.getSpaceNo());
        contract.setOwnerId(application.getOwnerId());
        contract.setOwnerName(application.getOwnerName());
        contract.setContactPhone(application.getContactPhone());
        contract.setVehicleNumber(application.getVehicleNumber());
        contract.setVehicleBrand(application.getVehicleBrand());
        contract.setVehicleColor(application.getVehicleColor());
        contract.setMonthlyRent(parkingSpace.getMonthlyRent());
        contract.setRentalMonths(application.getRentalMonths());
        contract.setStartDate(application.getRentalStartDate());
        contract.setEndDate(endDate);
        contract.setTotalAmount(totalAmount);
        contract.setPaidAmount(BigDecimal.ZERO);
        contract.setContractStatus(1); // 进行中
        contract.setSignDate(new Date());
        contract.setDeleted(0);

        this.save(contract);

        // 5. 生成租赁账单
        generateBill(contract, application);

        log.info("生成租赁合同成功，合同编号：{}", contract.getContractNo());
        return contract;
    }

    /**
     * 生成账单
     */
    private void generateBill(ParkingRentalContract contract, ParkingRentalApplication application) {
        try {
            // 查询车位租赁费类型
            QueryWrapper<FeeType> wrapper = new QueryWrapper<>();
            wrapper.eq("type_code", "PARKING_RENTAL_FEE");
            FeeType feeType = feeTypeService.getOne(wrapper);

            if (feeType == null) {
                log.warn("未找到车位租赁费类型，跳过账单生成");
                return;
            }

            // 获取业主房产ID
            Long houseId = getOwnerDefaultHouse(application.getOwnerId());
            if (houseId == null) {
                log.warn("业主未绑定房产，无法生成账单。业主ID：{}", application.getOwnerId());
                return;
            }

            // 创建账单
            Bill bill = new Bill();
            bill.setBillNo(generateBillNo());
            bill.setUserId(application.getOwnerId());
            bill.setHouseId(houseId);
            bill.setFeeTypeId(feeType.getId());
            bill.setFeeTypeName(feeType.getTypeName());
            bill.setBillPeriod(new SimpleDateFormat("yyyy-MM").format(contract.getStartDate()));
            bill.setAmount(contract.getTotalAmount());
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setBillStatus(1); // 待缴费
            bill.setDueDate(calculateDueDate(contract.getStartDate()));
            bill.setRemark("车位租赁费 - " + contract.getSpaceNo() + " (" +
                    new SimpleDateFormat("yyyy-MM-dd").format(contract.getStartDate()) + " 至 " +
                    new SimpleDateFormat("yyyy-MM-dd").format(contract.getEndDate()) + ")");
            bill.setDeleted(0);

            billService.save(bill);
            log.info("生成车位租赁账单成功，账单编号：{}", bill.getBillNo());
        } catch (Exception e) {
            log.error("生成车位租赁账单失败", e);
            // 账单生成失败不影响合同生成
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean terminateContract(Long contractId, String terminateReason) {
        ParkingRentalContract contract = this.getById(contractId);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }

        if (contract.getContractStatus() != 1) {
            throw new RuntimeException("只能终止进行中的合同");
        }

        // 更新合同状态
        contract.setContractStatus(3); // 已终止
        contract.setTerminateDate(new Date());
        contract.setTerminateReason(terminateReason);
        boolean result = this.updateById(contract);

        if (result) {
            // 释放车位
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId(contract.getParkingSpaceId());
            parkingSpace.setSpaceStatus(1); // 空闲
            parkingSpaceService.updateById(parkingSpace);

            log.info("终止合同成功，车位已释放。合同编号：{}", contract.getContractNo());
        }

        return result;
    }

    @Override
    public List<ParkingRentalContract> getExpiringContracts(int days) {
        return baseMapper.selectExpiringContracts(days);
    }

    @Override
    public List<ParkingRentalContract> getExpiredContracts() {
        return baseMapper.selectExpiredContracts();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpiredContracts() {
        List<ParkingRentalContract> expiredContracts = getExpiredContracts();
        int count = 0;

        for (ParkingRentalContract contract : expiredContracts) {
            // 更新合同状态
            contract.setContractStatus(2); // 已到期
            this.updateById(contract);

            // 释放车位
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId(contract.getParkingSpaceId());
            parkingSpace.setSpaceStatus(1); // 空闲
            parkingSpaceService.updateById(parkingSpace);

            count++;
        }

        log.info("更新过期合同状态完成，共更新{}个合同", count);
        return count;
    }

    @Override
    public Map<String, Object> getContractStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<ParkingRentalContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        // 总合同数
        long totalCount = this.count(queryWrapper);
        stats.put("totalCount", totalCount);

        // 进行中合同数
        queryWrapper.eq("contract_status", 1);
        long activeCount = this.count(queryWrapper);
        stats.put("activeCount", activeCount);

        // 已到期合同数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 2);
        long expiredCount = this.count(queryWrapper);
        stats.put("expiredCount", expiredCount);

        // 已终止合同数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 3);
        long terminatedCount = this.count(queryWrapper);
        stats.put("terminatedCount", terminatedCount);

        // 总金额
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 1);
        List<ParkingRentalContract> activeContracts = this.list(queryWrapper);
        BigDecimal totalAmount = activeContracts.stream()
                .map(ParkingRentalContract::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);

        return stats;
    }

    /**
     * 生成合同编号
     */
    private String generateContractNo() {
        return "C" + System.currentTimeMillis();
    }

    /**
     * 生成账单编号
     */
    private String generateBillNo() {
        return "B" + System.currentTimeMillis();
    }

    /**
     * 获取业主默认房产
     */
    private Long getOwnerDefaultHouse(Long ownerId) {
        // 查询业主的当前房产作为默认房产
        List<UserHouse> userHouses = userHouseService.selectCurrentUserHouseByUserId(ownerId);
        if (userHouses != null && !userHouses.isEmpty()) {
            // 过滤出业主类型的房产（relation_type = 1）
            for (UserHouse userHouse : userHouses) {
                if (userHouse.getRelationType() != null && userHouse.getRelationType() == 1) {
                    return userHouse.getHouseId();
                }
            }
            // 如果没有业主类型，返回第一个当前房产
            return userHouses.get(0).getHouseId();
        }
        return null;
    }

    /**
     * 计算缴费截止日期（默认30天后）
     */
    private Date calculateDueDate(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        return calendar.getTime();
    }
}
