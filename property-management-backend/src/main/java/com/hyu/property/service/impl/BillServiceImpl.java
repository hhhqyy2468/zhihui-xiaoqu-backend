package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Bill;
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

    /**
     * 分页查询账单列表
     *
     * @param page 分页参数
     * @param bill 账单信息
     * @return 账单集合信息
     */
    @Override
    public Page<Bill> selectBillPage(Page<Bill> page, Bill bill) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(bill.getBillNo()), Bill::getBillNo, bill.getBillNo())
                   .eq(bill.getOwnerId() != null, Bill::getOwnerId, bill.getOwnerId())
                   .eq(bill.getFeeTypeId() != null, Bill::getFeeTypeId, bill.getFeeTypeId())
                   .eq(bill.getBillStatus() != null, Bill::getBillStatus, bill.getBillStatus())
                   .like(StringUtils.isNotEmpty(bill.getBillPeriod()), Bill::getBillPeriod, bill.getBillPeriod())
                   .orderByDesc(Bill::getCreateTime);
        return page(page, queryWrapper);
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
                   .eq(bill.getOwnerId() != null, Bill::getOwnerId, bill.getOwnerId())
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
                bill.setOwnerId((Long) house.get("owner_id"));
                bill.setFeeTypeId(feeTypeId);
                bill.setHouseId((Long) house.get("house_id"));
                bill.setBillPeriod(billPeriod);
                bill.setBillingCycle(billingCycle);
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
                if (!bill.getOwnerId().equals(ownerId)) {
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
            if (balance.compareTo(totalAmount) < 0) {
                result.put("code", 500);
                result.put("msg", "钱包余额不足");
                return result;
            }

            // 扣减钱包余额
            billMapper.updateWalletBalance(ownerId, balance.subtract(totalAmount));

            // 更新账单状态
            for (Bill bill : bills) {
                bill.setBillStatus(2); // 已缴费
                bill.setPayTime(new Date());
                bill.setReceiptNo(generateReceiptNo());
                billMapper.updateBill(bill);

                // 添加交易记录
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
     * 生成账单编号
     */
    private String generateBillNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        long timestamp = System.currentTimeMillis();
        return "BIL" + date + timestamp;
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
}