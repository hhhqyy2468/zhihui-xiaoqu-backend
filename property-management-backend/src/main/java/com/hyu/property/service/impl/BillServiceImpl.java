package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Bill;
import com.hyu.property.domain.PaymentReceipt;
import com.hyu.property.mapper.BillMapper;
import com.hyu.property.service.IBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 账单Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements IBillService {

    @Autowired
    private BillMapper billMapper;

    @Autowired(required = false)
    private com.hyu.property.service.IParkingRentalContractService parkingRentalContractService;

    @Autowired(required = false)
    private com.hyu.property.service.IParkingSpaceService parkingSpaceService;

    @Autowired(required = false)
    private com.hyu.property.service.IWalletService walletService;

    @Autowired(required = false)
    private com.hyu.property.service.IWalletTransactionService walletTransactionService;

    @Autowired(required = false)
    private com.hyu.property.service.IPaymentReceiptService paymentReceiptService;

    /**
     * 分页查询账单列表
     *
     * @param page 分页参数
     * @param bill 账单信息
     * @return 账单集合信息
     */
    @Override
    public Page<Bill> selectBillPage(Page<Bill> page, Bill bill) {
        return billMapper.selectBillPage(page, bill);
    }

    /**
     * 查询账单列表
     *
     * @param bill 账单信息
     * @return 账单集合信息
     */
    @Override
    public List<Bill> selectBillList(Bill bill) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(bill.getBillNo()), Bill::getBillNo, bill.getBillNo())
                   .eq(bill.getUserId() != null, Bill::getUserId, bill.getUserId())
                   .eq(bill.getFeeTypeId() != null, Bill::getFeeTypeId, bill.getFeeTypeId())
                   .eq(bill.getBillStatus() != null, Bill::getBillStatus, bill.getBillStatus())
                   .like(StringUtils.isNotEmpty(bill.getBillPeriod()), Bill::getBillPeriod, bill.getBillPeriod())
                   .orderByDesc(Bill::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 根据账单ID查询账单
     *
     * @param billId 账单ID
     * @return 账单
     */
    @Override
    public Bill selectBillById(Long billId) {
        return billMapper.selectBillById(billId);
    }

    /**
     * 批量删除账单
     *
     * @param billIds 需要删除的账单ID
     * @return 结果
     */
    @Override
    public int deleteBillByIds(Long[] billIds) {
        return billMapper.deleteBillByIds(billIds);
    }

    /**
     * 新增账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    @Override
    public int insertBill(Bill bill) {
        bill.setBillNo(generateBillNo());
        bill.setCreateTime(new Date());
        return billMapper.insertBill(bill);
    }

    /**
     * 修改账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    @Override
    public int updateBill(Bill bill) {
        return billMapper.updateBill(bill);
    }

    /**
     * 生成账单
     *
     * @param feeTypeId 费用类型ID
     * @param billPeriod 账期
     * @param dueDate 缴费截止日期
     * @param buildingIds 楼栋ID数组
     * @param unitIds 单元ID数组
     * @param houseIds 房产ID数组
     * @return 生成结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> generateBills(Long feeTypeId, String billPeriod, Date dueDate,
                                           Long[] buildingIds, Long[] unitIds, Long[] houseIds) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 查询符合条件的房产
            List<Map<String, Object>> houseList = billMapper.selectHouseForBill(buildingIds, unitIds, houseIds);

            if (houseList.isEmpty()) {
                result.put("code", 500);
                result.put("msg", "未找到符合条件的房产");
                return result;
            }

            // 获取费用类型信息
            Map<String, Object> feeType = billMapper.selectFeeTypeById(feeTypeId);
            if (feeType == null) {
                result.put("code", 500);
                result.put("msg", "费用类型不存在");
                return result;
            }

            BigDecimal unitPrice = (BigDecimal) feeType.get("unit_price");
            String billingCycle = (String) feeType.get("billing_cycle");

            // 生成账单
            List<Bill> bills = new ArrayList<>();
            for (Map<String, Object> house : houseList) {
                Bill bill = new Bill();
                bill.setBillNo(generateBillNo());
                bill.setUserId((Long) house.get("owner_id"));
                bill.setFeeTypeId(feeTypeId);
                bill.setHouseId((Long) house.get("house_id"));
                bill.setBillPeriod(billPeriod);
                                bill.setAmount(unitPrice);
                bill.setDueDate(dueDate);
                bill.setBillStatus(1); // 未缴费
                bill.setCreateTime(new Date());

                bills.add(bill);
            }

            // 批量插入账单
            int count = 0;
            for (Bill bill : bills) {
                count += billMapper.insertBill(bill);
            }

            result.put("code", 200);
            result.put("msg", "账单生成成功");
            result.put("data", count);

        } catch (Exception e) {
            log.error("生成账单失败", e);
            result.put("code", 500);
            result.put("msg", "账单生成失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 在线缴费
     *
     * @param billIds 账单ID数组
     * @param ownerId 业主ID
     * @return 缴费结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> payBills(Long[] billIds, Long ownerId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 查询账单信息
            List<Bill> bills = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Long billId : billIds) {
                Bill bill = billMapper.selectBillById(billId);
                if (bill == null) {
                    result.put("code", 500);
                    result.put("msg", "账单不存在：" + billId);
                    return result;
                }
                if (!bill.getUserId().equals(ownerId)) {
                    result.put("code", 500);
                    result.put("msg", "账单不属于当前业主：" + billId);
                    return result;
                }
                if (bill.getBillStatus() != 1) {
                    result.put("code", 500);
                    result.put("msg", "账单状态不正确：" + billId);
                    return result;
                }
                bills.add(bill);
                totalAmount = totalAmount.add(bill.getAmount());
            }

            // 检查钱包余额
            BigDecimal balance = billMapper.selectWalletBalance(ownerId);
            if (balance == null || balance.compareTo(totalAmount) < 0) {
                result.put("code", 500);
                result.put("msg", "钱包余额不足");
                return result;
            }
            billMapper.updateWalletBalance(ownerId, balance.subtract(totalAmount));

            // 更新账单状态
            for (Bill bill : bills) {
                bill.setBillStatus(2); // 已缴费
                bill.setPaidTime(new Date());
                billMapper.updateBill(bill);

              // 插入缴费交易记录
                billMapper.insertPaymentTransaction(ownerId, bill.getBillId(), bill.getAmount(),
                    "缴费：" + bill.getBillNo(), generateTransactionNo());
            }

            result.put("code", 200);
            result.put("msg", "缴费成功");
            result.put("data", totalAmount);

        } catch (Exception e) {
            log.error("缴费失败", e);
            result.put("code", 500);
            result.put("msg", "缴费失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 生成收据编号
     *
     * @return 收据编号
     */
    @Override
    public String generateReceiptNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        long timestamp = System.currentTimeMillis();
        return "RCP" + date + timestamp;
    }

    /**
     * 检查账单编号是否唯一
     *
     * @param billNo 账单编号
     * @return 结果
     */
    @Override
    public boolean checkBillNoUnique(String billNo) {
        Bill bill = billMapper.checkBillNoUnique(billNo);
        return bill == null;
    }

    /**
     * 更新超期账单状态
     *
     * @return 更新数量
     */
    @Override
    public int updateOverdueBills() {
        return billMapper.updateOverdueBills();
    }

    /**
     * 检查指定用户、房产、费用类型和计费周期的账单是否已存在
     *
     * @param userId 用户ID
     * @param houseId 房产ID
     * @param feeTypeId 费用类型ID
     * @param billingPeriod 计费周期
     * @return 是否存在
     */
    @Override
    public boolean existsBill(Long userId, Long houseId, Long feeTypeId, String billingPeriod) {
        try {
            LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Bill::getUserId, userId)
                       .eq(Bill::getHouseId, houseId)
                       .eq(Bill::getFeeTypeId, feeTypeId)
                       .eq(Bill::getBillPeriod, billingPeriod)
                       .eq(Bill::getDeleted, 0);

            long count = count(queryWrapper);
            log.debug("检查账单是否存在 - userId: {}, houseId: {}, feeTypeId: {}, billingPeriod: {}, 存在数量: {}",
                    userId, houseId, feeTypeId, billingPeriod, count);

            return count > 0;
        } catch (Exception e) {
            log.error("检查账单是否存在时发生错误 - userId: {}, houseId: {}, feeTypeId: {}, billingPeriod: {}",
                    userId, houseId, feeTypeId, billingPeriod, e);
            return false;
        }
    }

    /**
     * 生成账单编号
     */
    private String generateBillNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        long timestamp = System.currentTimeMillis();
        return "BIL" + date + timestamp;
    }

    /**
     * 根据账单ID列表查询账单
     *
     * @param billIds 账单ID数组
     * @return 账单列表
     */
    @Override
    public List<Bill> selectBillByIds(Long[] billIds) {
        if (billIds == null || billIds.length == 0) {
            return new ArrayList<>();
        }

        try {
            LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Bill::getBillId, Arrays.asList(billIds))
                       .eq(Bill::getDeleted, 0)
                       .orderByDesc(Bill::getCreateTime);

            List<Bill> billList = list(queryWrapper);
            log.debug("根据账单ID列表查询到{}条账单记录", billList.size());
            return billList;

        } catch (Exception e) {
            log.error("根据账单ID列表查询账单失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 生成交易流水号
     */
    private String generateTransactionNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        long timestamp = System.currentTimeMillis();
        return "TXN" + date + timestamp;
    }

    // ==================== 业主端API实现 ====================

    /**
     * 分页查询我的账单列表
     */
    @Override
    public Page<Bill> selectMyBillPage(Page<Bill> page, Long userId, String billNo, Long feeTypeId, Integer billStatus, String billPeriod) {
        try {
            return billMapper.selectMyBillPage(page, userId, billNo, feeTypeId, billStatus, billPeriod);
        } catch (Exception e) {
            log.error("查询我的账单列表失败，用户ID: {}", userId, e);
            return new Page<>();
        }
    }

    /**
     * 根据账单ID查询我的账单详情
     */
    @Override
    public Bill selectMyBillById(Long billId, Long userId) {
        try {
            return billMapper.selectMyBillById(billId, userId);
        } catch (Exception e) {
            log.error("查询我的账单详情失败，账单ID: {}, 用户ID: {}", billId, userId, e);
            return null;
        }
    }

    /**
     * 在线缴费
     */
    @Override
    public Map<String, Object> payBill(Long billId, Long userId, String paymentMethod, String payPassword) {
        try {
            // 查询账单
            Bill bill = selectMyBillById(billId, userId);
            if (bill == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 404);
                result.put("msg", "账单不存在或无权限访问");
                return result;
            }

            // 检查账单状态
            if (bill.getBillStatus().equals(2)) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("msg", "账单已缴费");
                return result;
            }

            if (bill.getBillStatus().equals(4)) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("msg", "账单已作废");
                return result;
            }

            String transactionNo = null; // 用于保存交易流水号

            // 检查缴费方式
            if ("wallet".equals(paymentMethod)) {
                // 验证钱包支付密码
                if (StringUtils.isBlank(payPassword)) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 400);
                    result.put("msg", "请输入支付密码");
                    return result;
                }

                // 验证支付密码
                boolean passwordValid = walletService.verifyPayPassword(userId, payPassword);
                if (!passwordValid) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 400);
                    result.put("msg", "支付密码错误");
                    return result;
                }

                // 调用钱包服务扣款
                Map<String, Object> deductResult = walletService.deduct(userId, bill.getAmount(), billId, bill.getBillNo());
                if ((Integer) deductResult.get("code") != 200) {
                    return deductResult;
                }

                // 保存交易流水号用于生成收据
                @SuppressWarnings("unchecked")
                Map<String, Object> deductData = (Map<String, Object>) deductResult.get("data");
                transactionNo = (String) deductData.get("transactionNo");

                log.info("用户{}使用钱包支付账单{}成功，金额: {}, 交易流水: {}", userId, billId, bill.getAmount(), transactionNo);
            }

            // 更新账单状态
            bill.setBillStatus(2); // 已缴费
            bill.setPaidAmount(bill.getAmount()); // 实缴金额等于应缴金额
            // 将支付方式字符串转换为数字
            Integer payMethodInt = 1; // 默认现金
            if ("bank".equals(paymentMethod)) {
                payMethodInt = 2; // 银行转账
            } else if ("wechat".equals(paymentMethod) || "alipay".equals(paymentMethod)) {
                payMethodInt = 3; // 在线支付
            } else if ("wallet".equals(paymentMethod)) {
                payMethodInt = 4; // 钱包支付
            }
            bill.setPayMethod(payMethodInt);
            bill.setPaidTime(new java.util.Date());
            bill.setUpdateTime(new java.util.Date());

            boolean result = updateById(bill);
            if (result) {
                log.info("账单缴费成功，账单ID: {}, 用户ID: {}, 支付方式: {}", billId, userId, paymentMethod);

                // 检查并激活车位租赁合同
                try {
                    activateParkingContractIfNeeded(bill);
                } catch (Exception e) {
                    log.error("激活车位租赁合同失败", e);
                    // 激活失败不影响缴费结果，只记录日志
                }

                // 生成缴费收据
                try {
                    PaymentReceipt receipt = paymentReceiptService.generateReceipt(billId, transactionNo);
                    log.info("生成缴费收据成功，收据编号: {}", receipt.getReceiptNo());
                } catch (Exception e) {
                    log.error("生成缴费收据失败", e);
                    // 收据生成失败不影响缴费结果，只记录日志
                }

                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("msg", "缴费成功");
                response.put("data", bill);
                return response;
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("msg", "缴费失败");
                return response;
            }

        } catch (Exception e) {
            log.error("在线缴费失败，账单ID: {}, 用户ID: {}", billId, userId, e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("msg", "系统异常，缴费失败");
            return result;
        }
    }

    /**
     * 批量在线缴费
     */
    @Override
    public Map<String, Object> batchPayBills(Long[] billIds, Long userId, String paymentMethod, String payPassword) {
        try {
            if (billIds == null || billIds.length == 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("msg", "请选择要缴费的账单");
                return result;
            }

            // 查询所有账单
            List<Bill> bills = selectBillByIds(billIds);
            List<Bill> validBills = new ArrayList<>();

            // 验证账单
            for (Bill bill : bills) {
                if (bill.getUserId().equals(userId) && bill.getBillStatus().equals(1)) {
                    validBills.add(bill);
                }
            }

            if (validBills.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("msg", "没有可缴费的账单");
                return result;
            }

            // 检查缴费方式
            if ("wallet".equals(paymentMethod)) {
                // 验证钱包支付密码
                if (StringUtils.isBlank(payPassword)) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 400);
                    result.put("msg", "请输入支付密码");
                    return result;
                }

                // 计算总金额
                BigDecimal totalAmount = validBills.stream()
                        .map(Bill::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 验证支付密码
                boolean passwordValid = walletService.verifyPayPassword(userId, payPassword);
                if (!passwordValid) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 400);
                    result.put("msg", "支付密码错误");
                    return result;
                }

                // 调用钱包服务批量扣款（为每个账单分别扣款并创建交易记录）
                List<String> transactionNos = new ArrayList<>();
                for (Bill bill : validBills) {
                    Map<String, Object> deductResult = walletService.deduct(userId, bill.getAmount(), bill.getBillId(), bill.getBillNo());
                    if ((Integer) deductResult.get("code") != 200) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("code", 500);
                        result.put("msg", "扣款失败：" + deductResult.get("msg"));
                        return result;
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) deductResult.get("data");
                    transactionNos.add((String) data.get("transactionNo"));
                }

                log.info("用户{}使用钱包批量支付{}个账单成功，总金额: {}, 交易流水: {}", userId, validBills.size(), totalAmount, transactionNos);
            }

            // 批量更新账单状态
            int successCount = 0;
            for (Bill bill : validBills) {
                bill.setBillStatus(2); // 已缴费
                bill.setPaidAmount(bill.getAmount()); // 实缴金额等于应缴金额
                // 将支付方式字符串转换为数字
                Integer payMethodInt = 1; // 默认现金
                if ("bank".equals(paymentMethod)) {
                    payMethodInt = 2; // 银行转账
                } else if ("wechat".equals(paymentMethod) || "alipay".equals(paymentMethod)) {
                    payMethodInt = 3; // 在线支付
                } else if ("wallet".equals(paymentMethod)) {
                    payMethodInt = 4; // 钱包支付
                }
                bill.setPayMethod(payMethodInt);
                bill.setPaidTime(new java.util.Date());
                bill.setUpdateTime(new java.util.Date());

                if (updateById(bill)) {
                    successCount++;
                    // 检查并激活车位租赁合同
                    try {
                        activateParkingContractIfNeeded(bill);
                    } catch (Exception e) {
                        log.error("激活车位租赁合同失败", e);
                        // 激活失败不影响缴费结果，只记录日志
                    }
                }
            }

            if (successCount > 0) {
                log.info("批量缴费完成，成功{}个，失败{}个", successCount, validBills.size() - successCount);
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("msg", "批量缴费成功");

                Map<String, Object> data = new HashMap<>();
                data.put("totalCount", validBills.size());
                data.put("successCount", successCount);
                data.put("failCount", validBills.size() - successCount);

                response.put("data", data);
                return response;
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("msg", "批量缴费失败");
                return response;
            }

        } catch (Exception e) {
            log.error("批量缴费失败，用户ID: {}", userId, e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("msg", "系统异常，批量缴费失败");
            return result;
        }
    }

    /**
     * 检查并激活车位租赁合同
     * 当车位租赁账单付款后，将合同状态从"待付款"改为"进行中"，并占用车位
     * 同时更新合同的已付金额
     */
    private void activateParkingContractIfNeeded(Bill bill) {
        if (parkingRentalContractService == null || parkingSpaceService == null) {
            log.debug("车位租赁服务未注入，跳过合同激活");
            return;
        }

        try {
            // 检查是否是车位租赁费类型账单（通过备注判断）
            String remark = bill.getRemark();
            if (remark != null && (remark.contains("车位") || remark.contains("租赁"))) {
                log.debug("检测到车位租赁账单，尝试激活合同。账单ID: {}, 费用类型ID: {}", bill.getBillId(), bill.getFeeTypeId());

                // 查询该用户的待付款车位租赁合同
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.hyu.property.domain.ParkingRentalContract> queryWrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                queryWrapper.eq("owner_id", bill.getUserId())
                           .eq("contract_status", 1) // 待付款状态
                           .eq("deleted", 0)
                           .orderByDesc("create_time")
                           .last("LIMIT 1");

                com.hyu.property.domain.ParkingRentalContract contract =
                    parkingRentalContractService.getOne(queryWrapper);

                if (contract != null) {
                    log.info("找到待付款合同，开始激活。合同ID: {}, 车位ID: {}", contract.getId(), contract.getParkingSpaceId());

                    // 更新合同的已付金额
                    BigDecimal currentPaidAmount = contract.getPaidAmount() != null ? contract.getPaidAmount() : BigDecimal.ZERO;
                    contract.setPaidAmount(currentPaidAmount.add(bill.getAmount()));

                    // 更新合同状态为进行中
                    contract.setContractStatus(2); // 进行中
                    parkingRentalContractService.updateById(contract);

                    // 更新车位状态为已租
                    com.hyu.property.domain.ParkingSpace parkingSpace = new com.hyu.property.domain.ParkingSpace();
                    parkingSpace.setId(contract.getParkingSpaceId());
                    parkingSpace.setSpaceStatus(2); // 已租
                    parkingSpaceService.updateById(parkingSpace);

                    log.info("车位租赁合同已激活，已付金额已更新。合同ID: {}, 车位ID: {}, 车位编号: {}, 本次支付: {}, 总已付: {}",
                            contract.getId(), contract.getParkingSpaceId(), contract.getSpaceNo(),
                            bill.getAmount(), contract.getPaidAmount());
                } else {
                    log.warn("未找到待付款的车位租赁合同。用户ID: {}", bill.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("激活车位租赁合同时发生错误，账单ID: {}", bill.getBillId(), e);
            throw e;
        }
    }

    /**
     * 获取我的账单统计数据
     *
     * @param userId 用户ID
     * @return 统计数据（待缴金额、已缴金额、总账单数、逾期数）
     */
    @Override
    public Map<String, Object> getMyBillStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 查询该用户的所有账单
            LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Bill::getUserId, userId)
                       .eq(Bill::getDeleted, 0);

            List<Bill> allBills = list(queryWrapper);

            // 初始化统计值
            BigDecimal unpaidAmount = BigDecimal.ZERO;
            BigDecimal paidAmount = BigDecimal.ZERO;
            int totalBills = allBills.size();
            int overdueCount = 0;

            // 遍历所有账单进行统计
            for (Bill bill : allBills) {
                Integer status = bill.getBillStatus();
                BigDecimal amount = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;

                if (status == null) {
                    continue;
                }

                switch (status) {
                    case 0: // 部分缴费
                        BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
                        paidAmount = paidAmount.add(paid);
                        unpaidAmount = unpaidAmount.add(amount.subtract(paid));
                        break;
                    case 1: // 待缴费
                        unpaidAmount = unpaidAmount.add(amount);
                        break;
                    case 2: // 已缴费
                        BigDecimal paidAmt = bill.getPaidAmount() != null ? bill.getPaidAmount() : amount;
                        paidAmount = paidAmount.add(paidAmt);
                        break;
                    case 3: // 已超期
                        unpaidAmount = unpaidAmount.add(amount);
                        overdueCount++;
                        break;
                    default:
                        break;
                }
            }

            statistics.put("unpaidAmount", unpaidAmount);
            statistics.put("paidAmount", paidAmount);
            statistics.put("totalBills", totalBills);
            statistics.put("overdueCount", overdueCount);

            log.debug("用户{}的账单统计：待缴{}，已缴{}，总数{}，逾期{}",
                    userId, unpaidAmount, paidAmount, totalBills, overdueCount);

        } catch (Exception e) {
            log.error("获取用户{}的账单统计数据失败", userId, e);
            // 返回默认值
            statistics.put("unpaidAmount", BigDecimal.ZERO);
            statistics.put("paidAmount", BigDecimal.ZERO);
            statistics.put("totalBills", 0);
            statistics.put("overdueCount", 0);
        }

        return statistics;
    }
}