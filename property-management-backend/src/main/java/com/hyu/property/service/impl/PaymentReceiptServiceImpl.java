package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Bill;
import com.hyu.property.domain.PaymentReceipt;
import com.hyu.property.mapper.BillMapper;
import com.hyu.property.mapper.PaymentReceiptMapper;
import com.hyu.property.service.IBillService;
import com.hyu.property.service.IPaymentReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 缴费收据Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class PaymentReceiptServiceImpl extends ServiceImpl<PaymentReceiptMapper, PaymentReceipt> implements IPaymentReceiptService {

    @Autowired
    private PaymentReceiptMapper paymentReceiptMapper;

    @Autowired(required = false)
    private BillMapper billMapper;

    @Autowired(required = false)
    private com.hyu.system.mapper.SysUserMapper sysUserMapper;

    /**
     * 分页查询收据列表
     *
     * @param page 分页参数
     * @param receipt 收据信息
     * @return 收据分页数据
     */
    @Override
    public Page<PaymentReceipt> selectReceiptPage(Page<PaymentReceipt> page, PaymentReceipt receipt) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(receipt.getUserId() != null, PaymentReceipt::getUserId, receipt.getUserId())
                   .eq(receipt.getBillId() != null, PaymentReceipt::getBillId, receipt.getBillId())
                   .like(StringUtils.isNotEmpty(receipt.getReceiptNo()), PaymentReceipt::getReceiptNo, receipt.getReceiptNo())
                   .like(StringUtils.isNotEmpty(receipt.getBillNo()), PaymentReceipt::getBillNo, receipt.getBillNo())
                   .eq(receipt.getStatus() != null, PaymentReceipt::getStatus, receipt.getStatus())
                   .orderByDesc(PaymentReceipt::getCreateTime);
        return page(page, queryWrapper);
    }

    /**
     * 查询收据列表
     *
     * @param receipt 收据信息
     * @return 收据集合
     */
    @Override
    public List<PaymentReceipt> selectReceiptList(PaymentReceipt receipt) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(receipt.getUserId() != null, PaymentReceipt::getUserId, receipt.getUserId())
                   .eq(receipt.getBillId() != null, PaymentReceipt::getBillId, receipt.getBillId())
                   .like(StringUtils.isNotEmpty(receipt.getReceiptNo()), PaymentReceipt::getReceiptNo, receipt.getReceiptNo())
                   .like(StringUtils.isNotEmpty(receipt.getBillNo()), PaymentReceipt::getBillNo, receipt.getBillNo())
                   .eq(receipt.getStatus() != null, PaymentReceipt::getStatus, receipt.getStatus())
                   .orderByDesc(PaymentReceipt::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 根据收据ID查询收据
     *
     * @param receiptId 收据ID
     * @return 收据
     */
    @Override
    public PaymentReceipt selectReceiptById(Long receiptId) {
        return getById(receiptId);
    }

    /**
     * 根据账单ID查询收据
     *
     * @param billId 账单ID
     * @return 收据
     */
    @Override
    public PaymentReceipt selectReceiptByBillId(Long billId) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentReceipt::getBillId, billId);
        return getOne(queryWrapper);
    }

    /**
     * 根据用户ID查询收据列表
     *
     * @param userId 用户ID
     * @return 收据列表
     */
    @Override
    public List<PaymentReceipt> selectReceiptByUserId(Long userId) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentReceipt::getUserId, userId)
                   .orderByDesc(PaymentReceipt::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 根据收据编号查询收据
     *
     * @param receiptNo 收据编号
     * @return 收据
     */
    @Override
    public PaymentReceipt selectReceiptByNo(String receiptNo) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentReceipt::getReceiptNo, receiptNo);
        return getOne(queryWrapper);
    }

    /**
     * 生成收据
     *
     * @param billId 账单ID
     * @param transactionNo 交易流水号（钱包支付时）
     * @return 收据
     */
    @Override
    @Transactional
    public PaymentReceipt generateReceipt(Long billId, String transactionNo) {
        // 查询账单信息
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }

        // 检查是否已存在收据
        PaymentReceipt existingReceipt = selectReceiptByBillId(billId);
        if (existingReceipt != null) {
            throw new RuntimeException("该账单已生成收据：" + existingReceipt.getReceiptNo());
        }

        // 查询用户名称
        String userName = bill.getOwnerName();
        if (userName == null || userName.isEmpty()) {
            // 尝试从sys_user表查询
            if (sysUserMapper != null) {
                try {
                    com.hyu.system.domain.SysUser user = sysUserMapper.selectById(bill.getUserId());
                    if (user != null) {
                        userName = user.getRealName();
                    }
                } catch (Exception e) {
                    log.warn("查询用户名称失败，用户ID: {}", bill.getUserId(), e);
                }
            }
            if (userName == null || userName.isEmpty()) {
                userName = "未知用户";
            }
        }

        // 创建收据
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setReceiptNo(generateReceiptNo());
        receipt.setBillId(billId);
        receipt.setBillNo(bill.getBillNo());
        receipt.setUserId(bill.getUserId());
        receipt.setUserName(userName);
        receipt.setFeeType(bill.getFeeName() != null ? bill.getFeeName() : bill.getFeeTypeName());
        receipt.setBillPeriod(bill.getBillPeriod());
        receipt.setAmount(bill.getAmount());
        receipt.setPaidAmount(bill.getPaidAmount() != null ? bill.getPaidAmount() : bill.getAmount());
        receipt.setPaymentMethod(bill.getPayMethod());
        receipt.setPaymentMethodName(getPaymentMethodName(bill.getPayMethod()));
        receipt.setTransactionNo(transactionNo);
        receipt.setStatus(1); // 正常
        receipt.setCreateTime(LocalDateTime.now());
        receipt.setUpdateTime(LocalDateTime.now());

        save(receipt);

        log.info("生成缴费收据成功，收据编号: {}, 账单编号: {}, 金额: {}",
                receipt.getReceiptNo(), bill.getBillNo(), receipt.getPaidAmount());

        return receipt;
    }

    /**
     * 检查收据编号是否唯一
     *
     * @param receiptNo 收据编号
     * @return 是否唯一
     */
    @Override
    public boolean checkReceiptNoUnique(String receiptNo) {
        LambdaQueryWrapper<PaymentReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentReceipt::getReceiptNo, receiptNo);
        return count(queryWrapper) == 0;
    }

    /**
     * 生成收据编号
     *
     * @return 收据编号
     */
    @Override
    public String generateReceiptNo() {
        // 格式: RCP + 年月日 + 6位随机码
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RCP" + dateStr + randomStr;
    }

    /**
     * 获取支付方式名称
     *
     * @param payMethod 支付方式代码
     * @return 支付方式名称
     */
    private String getPaymentMethodName(Integer payMethod) {
        if (payMethod == null) {
            return "未知";
        }
        switch (payMethod) {
            case 1:
                return "现金";
            case 2:
                return "银行转账";
            case 3:
                return "在线支付";
            case 4:
                return "钱包支付";
            default:
                return "未知";
        }
    }
}
