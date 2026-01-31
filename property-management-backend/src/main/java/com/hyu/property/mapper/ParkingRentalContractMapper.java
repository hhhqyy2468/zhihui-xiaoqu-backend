package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.ParkingRentalContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车位租赁合同Mapper接口
 *
 * @author system
 * @date 2025-01-31
 */
@Mapper
public interface ParkingRentalContractMapper extends BaseMapper<ParkingRentalContract> {

    /**
     * 查询租赁合同列表
     *
     * @param contract 租赁合同
     * @return 租赁合同集合
     */
    List<ParkingRentalContract> selectContractList(ParkingRentalContract contract);

    /**
     * 查询即将到期的合同
     *
     * @param days 天数
     * @return 租赁合同集合
     */
    List<ParkingRentalContract> selectExpiringContracts(@Param("days") int days);

    /**
     * 查询已到期但未更新的合同
     *
     * @return 租赁合同集合
     */
    List<ParkingRentalContract> selectExpiredContracts();

    /**
     * 根据合同ID查询合同详情（包含车位位置）
     *
     * @param id 合同ID
     * @return 租赁合同
     */
    ParkingRentalContract selectContractById(@Param("id") Long id);
}
