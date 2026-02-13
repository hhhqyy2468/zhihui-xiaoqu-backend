package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.PaymentReceipt;

import java.util.List;

/**
 * 缴费收据Service接口
 *
 * @author hyu
 */
public interface IPaymentReceiptService extends IService<PaymentReceipt> {

    /**
     * 分页查询收据列表
     *
     * @param page 分页参数
     * @param receipt 收据信息
     * @return 收据分页数据
     */
    Page<PaymentReceipt> selectReceiptPage(Page<PaymentReceipt> page, PaymentReceipt receipt);

    /**
     * 查询收据列表
     *
     * @param receipt 收据信息
     * @return 收据集合
     */
    List<PaymentReceipt> selectReceiptList(PaymentReceipt receipt);

    /**
     * 根据收据ID查询收据
     *
     * @param receiptId 收据ID
     * @return 收据
     */
    PaymentReceipt selectReceiptById(Long receiptId);

    /**
     * 根据账单ID查询收据
     *
     * @param billId 账单ID
     * @return 收据
     */
    PaymentReceipt selectReceiptByBillId(Long billId);

    /**
     * 根据用户ID查询收据列表
     *
     * @param userId 用户ID
     * @return 收据列表
     */
    List<PaymentReceipt> selectReceiptByUserId(Long userId);

    /**
     * 根据收据编号查询收据
     *
     * @param receiptNo 收据编号
     * @return 收据
     */
    PaymentReceipt selectReceiptByNo(String receiptNo);

    /**
     * 生成收据
     *
     * @param billId 账单ID
     * @param transactionNo 交易流水号（钱包支付时）
     * @return 收据
     */
    PaymentReceipt generateReceipt(Long billId, String transactionNo);

    /**
     * 检查收据编号是否唯一
     *
     * @param receiptNo 收据编号
     * @return 是否唯一
     */
    boolean checkReceiptNoUnique(String receiptNo);

    /**
     * 生成收据编号
     *
     * @return 收据编号
     */
    String generateReceiptNo();
}
