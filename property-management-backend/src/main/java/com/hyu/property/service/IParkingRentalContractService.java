package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.ParkingRentalContract;

import java.util.List;
import java.util.Map;

/**
 * 车位租赁合同Service接口
 *
 * @author system
 * @date 2025-01-31
 */
public interface IParkingRentalContractService extends IService<ParkingRentalContract> {

    /**
     * 分页查询合同列表
     *
     * @param page 分页参数
     * @param contract 查询条件
     * @return 合同列表
     */
    Page<ParkingRentalContract> selectContractPage(Page<ParkingRentalContract> page, ParkingRentalContract contract);

    /**
     * 查询合同列表
     *
     * @param contract 查询条件
     * @return 合同集合
     */
    List<ParkingRentalContract> selectContractList(ParkingRentalContract contract);

    /**
     * 根据申请ID生成租赁合同
     *
     * @param applicationId 申请ID
     * @return 合同
     */
    ParkingRentalContract generateContract(Long applicationId);

    /**
     * 终止合同
     *
     * @param contractId 合同ID
     * @param terminateReason 终止原因
     * @return 结果
     */
    boolean terminateContract(Long contractId, String terminateReason);

    /**
     * 查询即将到期的合同
     *
     * @param days 天数
     * @return 合同集合
     */
    List<ParkingRentalContract> getExpiringContracts(int days);

    /**
     * 查询已到期的合同
     *
     * @return 合同集合
     */
    List<ParkingRentalContract> getExpiredContracts();

    /**
     * 批量更新过期合同状态
     *
     * @return 更新数量
     */
    int updateExpiredContracts();

    /**
     * 获取合同统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getContractStats();
}
