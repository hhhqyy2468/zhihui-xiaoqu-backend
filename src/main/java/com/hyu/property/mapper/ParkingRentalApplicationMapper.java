package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.ParkingRentalApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车位租赁申请Mapper接口
 *
 * @author system
 * @date 2025-11-18
 */
@Mapper
public interface ParkingRentalApplicationMapper extends BaseMapper<ParkingRentalApplication> {

    /**
     * 查询租赁申请列表
     *
     * @param parkingRentalApplication 租赁申请
     * @return 租赁申请集合
     */
    List<ParkingRentalApplication> selectParkingRentalApplicationList(ParkingRentalApplication parkingRentalApplication);

    /**
     * 查询租赁申请
     *
     * @param id 租赁申请主键
     * @return 租赁申请
     */
    ParkingRentalApplication selectParkingRentalApplicationById(@Param("id") Long id);

    /**
     * 查询待审核申请列表
     *
     * @return 待审核申请集合
     */
    List<ParkingRentalApplication> selectPendingApplications();
}