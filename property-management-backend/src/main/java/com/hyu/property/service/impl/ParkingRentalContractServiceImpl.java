package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.*;
import com.hyu.property.mapper.ParkingRentalContractMapper;
import com.hyu.property.service.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
//@Slf4j
@Service
public class ParkingRentalContractServiceImpl extends ServiceImpl<ParkingRentalContractMapper, ParkingRentalContract>
        implements IParkingRentalContractService {

    private static final Logger log = LoggerFactory.getLogger(ParkingRentalContractServiceImpl.class);

    @Autowired
    private IParkingRentalApplicationService applicationService;

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Lazy
    @Autowired
    private IBillService billService;

    @Autowired
    private IFeeTypeService feeTypeService;

    @Autowired
    private IOwnerService ownerService;

    @Autowired
    private IUserHouseService userHouseService;

    @Autowired(required = false)
    private com.hyu.property.service.IWalletService walletService;

    @Autowired(required = false)
    private com.hyu.property.service.IWalletTransactionService walletTransactionService;

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
        contract.setContractStatus(1); // 待付款
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
            wrapper.eq("type_code", "PARKING_FEE");
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

        if (contract.getContractStatus() != 2) {
            throw new RuntimeException("只能终止进行中的合同");
        }

        // 计算退款金额
        BigDecimal refundAmount = calculateRefundAmount(contract);
        log.info("合同终止退款计算。合同编号：{}, 已付金额：{}, 退款金额：{}",
                contract.getContractNo(), contract.getPaidAmount(), refundAmount);

        // 更新合同状态
        contract.setContractStatus(4); // 已终止
        contract.setTerminateDate(new Date());
        contract.setTerminateReason(terminateReason);

        // 如果有退款，更新已付金额
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentPaidAmount = contract.getPaidAmount() != null ? contract.getPaidAmount() : BigDecimal.ZERO;
            contract.setPaidAmount(currentPaidAmount.subtract(refundAmount));
        }

        boolean result = this.updateById(contract);

        if (result) {
            // 释放车位
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId(contract.getParkingSpaceId());
            parkingSpace.setSpaceStatus(1); // 空闲
            parkingSpaceService.updateById(parkingSpace);

            // 处理退款
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    processRefund(contract, refundAmount);
                    log.info("终止合同成功，车位已释放，退款已处理。合同编号：{}, 退款金额：{}",
                            contract.getContractNo(), refundAmount);
                } catch (Exception e) {
                    log.error("处理退款失败，合同编号：{}, 退款金额：{}", contract.getContractNo(), refundAmount, e);
                    throw new RuntimeException("退款处理失败：" + e.getMessage());
                }
            } else {
                log.info("终止合同成功，车位已释放，无退款。合同编号：{}", contract.getContractNo());
            }
        }

        return result;
    }

    /**
     * 计算退款金额
     * 公式：(未租天数 / 总租赁天数) × 已付金额
     */
    private BigDecimal calculateRefundAmount(ParkingRentalContract contract) {
        BigDecimal paidAmount = contract.getPaidAmount() != null ? contract.getPaidAmount() : BigDecimal.ZERO;

        // 如果没有已付金额，则不退款
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Date startDate = contract.getStartDate();
        Date endDate = contract.getEndDate();
        Date currentDate = new Date();

        // 计算总租赁天数（毫秒转换为天）
        long totalMillis = endDate.getTime() - startDate.getTime();
        long totalDays = totalMillis / (1000 * 60 * 60 * 24);

        // 计算未租天数（从当前日期到结束日期）
        long remainingMillis = endDate.getTime() - currentDate.getTime();
        long remainingDays = remainingMillis > 0 ? remainingMillis / (1000 * 60 * 60 * 24) : 0;

        // 如果已经超过或等于结束日期，不退款
        if (remainingDays <= 0) {
            return BigDecimal.ZERO;
        }

        // 计算退款金额： (未租天数 / 总租赁天数) × 已付金额
        BigDecimal refundAmount = paidAmount.multiply(new BigDecimal(remainingDays))
                .divide(new BigDecimal(totalDays), 2, BigDecimal.ROUND_HALF_UP);

        log.info("退款计算详情。合同编号：{}, 总天数：{}, 剩余天数：{}, 已付金额：{}, 退款金额：{}",
                contract.getContractNo(), totalDays, remainingDays, paidAmount, refundAmount);

        return refundAmount;
    }

    /**
     * 处理退款到业主钱包
     */
    private void processRefund(ParkingRentalContract contract, BigDecimal refundAmount) {
        Long ownerId = contract.getOwnerId();
        String contractNo = contract.getContractNo();

        // 1. 更新钱包余额（Wallet使用LocalDateTime）
        com.hyu.property.domain.Wallet wallet = walletService.getByUserId(ownerId);
        BigDecimal beforeBalance = wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal afterBalance = beforeBalance.add(refundAmount);

        java.time.LocalDateTime nowDateTime = java.time.LocalDateTime.now();
        java.util.Date nowDate = new java.util.Date();

        if (wallet == null) {
            // 创建新钱包
            wallet = new com.hyu.property.domain.Wallet();
            wallet.setUserId(ownerId);
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(refundAmount);
            wallet.setTotalConsume(BigDecimal.ZERO);
            wallet.setStatus(1);
            wallet.setCreateTime(nowDateTime);
            wallet.setUpdateTime(nowDateTime);
            walletService.save(wallet);
        } else {
            // 更新现有钱包
            wallet.setBalance(afterBalance);
            wallet.setTotalRecharge(wallet.getTotalRecharge().add(refundAmount));
            wallet.setUpdateTime(nowDateTime);
            walletService.updateById(wallet);
        }

        // 2. 创建交易记录（WalletTransaction使用java.util.Date）
        com.hyu.property.domain.WalletTransaction transaction = new com.hyu.property.domain.WalletTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setUserId(ownerId);
        transaction.setWalletId(wallet.getId());
        transaction.setTransactionType(3); // 退款
        transaction.setAmount(refundAmount);
        transaction.setBalanceBefore(beforeBalance);
        transaction.setBalanceAfter(afterBalance);
        transaction.setRelatedOrderNo(contractNo); // 关联合同编号
        transaction.setTransactionStatus(1); // 成功
        transaction.setRemark("车位租赁合同终止退款 - " + contractNo);
        transaction.setCreateTime(nowDate);

        walletTransactionService.save(transaction);
        log.info("退款交易记录已创建。交易流水号：{}, 退款金额：{}", transaction.getTransactionNo(), refundAmount);

        // 3. 创建退款账单（Bill记录）
        createRefundBill(contract, refundAmount, ownerId, nowDate);
    }

    /**
     * 创建退款账单
     */
    private void createRefundBill(ParkingRentalContract contract, BigDecimal refundAmount, Long ownerId, java.util.Date nowDate) {
        if (billService == null) {
            log.warn("BillService 未注入，跳过创建退款账单");
            return;
        }

        try {
            // 查询车位租赁费类型
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.hyu.property.domain.FeeType> feeTypeWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            feeTypeWrapper.eq("type_code", "PARKING_FEE");
            com.hyu.property.domain.FeeType feeType = feeTypeService.getOne(feeTypeWrapper);

            if (feeType == null) {
                log.warn("未找到车位租赁费类型，无法创建退款账单");
                return;
            }

            // 查询业主的房产ID
            Long houseId = getOwnerDefaultHouse(ownerId);
            if (houseId == null) {
                log.warn("业主未绑定房产，无法创建退款账单。业主ID：{}", ownerId);
                return;
            }

            // 生成退款账单编号
            String refundBillNo = "REF" + System.currentTimeMillis();

            // 创建退款账单
            Bill bill = new Bill();
            bill.setBillNo(refundBillNo);
            bill.setUserId(ownerId);
            bill.setHouseId(houseId);
            bill.setFeeTypeId(feeType.getId());
            bill.setFeeTypeName(feeType.getTypeName());

            // 账期使用当前年月
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
            bill.setBillPeriod(sdf.format(nowDate));

            bill.setAmount(refundAmount);
            bill.setPaidAmount(refundAmount); // 退款金额 = 已退金额
            bill.setBillStatus(4); // 4=已退款
            bill.setDueDate(nowDate);
            bill.setPayMethod(4); // 钱包支付方式（退款退回钱包）
            bill.setPaidTime(nowDate);
            bill.setRemark("【退款】车位租赁合同终止退款 - " + contract.getContractNo());
            bill.setDeleted(0);

            int result = billService.insertBill(bill);
            if (result > 0) {
                log.info("退款账单已创建。账单编号：{}, 退款金额：{}", refundBillNo, refundAmount);
            } else {
                log.error("退款账单创建失败。合同编号：{}, 退款金额：{}", contract.getContractNo(), refundAmount);
            }
        } catch (Exception e) {
            log.error("创建退款账单失败。合同编号：{}, 退款金额：{}", contract.getContractNo(), refundAmount, e);
        }
    }

    /**
     * 生成交易流水号
     */
    private String generateTransactionNo() {
        return "REF" + System.currentTimeMillis() + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
            contract.setContractStatus(3); // 已到期
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

        // 待付款合同数
        queryWrapper.eq("contract_status", 1);
        long pendingPaymentCount = this.count(queryWrapper);
        stats.put("pendingPaymentCount", pendingPaymentCount);

        // 进行中合同数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 2);
        long activeCount = this.count(queryWrapper);
        stats.put("activeCount", activeCount);

        // 已到期合同数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 3);
        long expiredCount = this.count(queryWrapper);
        stats.put("expiredCount", expiredCount);

        // 已终止合同数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 4);
        long terminatedCount = this.count(queryWrapper);
        stats.put("terminatedCount", terminatedCount);

        // 总金额（统计进行中的合同）
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("contract_status", 2);
        List<ParkingRentalContract> activeContracts = this.list(queryWrapper);
        BigDecimal totalAmount = activeContracts.stream()
                .map(ParkingRentalContract::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);

        return stats;
    }

    @Override
    public ParkingRentalContract selectContractById(Long id) {
        ParkingRentalContract contract = baseMapper.selectContractById(id);
        if (contract != null) {
            // 设置合同状态名称
            if (contract.getContractStatus() != null) {
                switch (contract.getContractStatus()) {
                    case 1:
                        contract.setContractStatusName("待付款");
                        break;
                    case 2:
                        contract.setContractStatusName("进行中");
                        break;
                    case 3:
                        contract.setContractStatusName("已到期");
                        break;
                    case 4:
                        contract.setContractStatusName("已终止");
                        break;
                    default:
                        contract.setContractStatusName("未知");
                        break;
                }
            }

            // 计算剩余天数（仅进行中的合同）
            if (contract.getEndDate() != null && contract.getContractStatus() == 2) {
                long diff = contract.getEndDate().getTime() - System.currentTimeMillis();
                long days = diff / (1000 * 60 * 60 * 24);
                contract.setRemainingDays(days);
            }
        }
        return contract;
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
