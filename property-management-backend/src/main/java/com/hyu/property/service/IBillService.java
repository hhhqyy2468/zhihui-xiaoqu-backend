package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Bill;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账单Service接口
 *
 * @author hyu
 */
public interface IBillService extends IService<Bill> {

    /**
     * 分页查询账单列表
     *
     * @param page 分页参数
     * @param bill 账单信息
     * @return 账单集合信息
     */
    Page<Bill> selectBillPage(Page<Bill> page, Bill bill);

    /**
     * 查询账单列表
     *
     * @param bill 账单信息
     * @return 账单集合信息
     */
    List<Bill> selectBillList(Bill bill);

    /**
     * 根据账单ID查询账单
     *
     * @param billId 账单ID
     * @return 账单
     */
    Bill selectBillById(Long billId);

    /**
     * 批量删除账单
     *
     * @param billIds 需要删除的账单ID
     * @return 结果
     */
    int deleteBillByIds(Long[] billIds);

    /**
     * 新增账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    int insertBill(Bill bill);

    /**
     * 修改账单
     *
     * @param bill 账单信息
     * @return 结果
     */
    int updateBill(Bill bill);

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
    Map<String, Object> generateBills(Long feeTypeId, String billPeriod, Date dueDate,
                                        Long[] buildingIds, Long[] unitIds, Long[] houseIds);

    /**
     * 在线缴费
     *
     * @param billIds 账单ID数组
     * @param ownerId 业主ID
     * @return 缴费结果
     */
    Map<String, Object> payBills(Long[] billIds, Long ownerId);

    /**
     * 生成收据编号
     *
     * @return 收据编号
     */
    String generateReceiptNo();

    /**
     * 检查账单编号是否唯一
     *
     * @param billNo 账单编号
     * @return 结果
     */
    boolean checkBillNoUnique(String billNo);

    /**
     * 更新超期账单状态
     *
     * @return 更新数量
     */
    int updateOverdueBills();
}