package com.hyu.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.ParkingRentalApplication;

import java.util.List;

/**
 * 车位租赁申请Service接口
 *
 * @author system
 * @date 2025-11-18
 */
public interface IParkingRentalApplicationService {
    /**
     * 查询租赁申请
     *
     * @param id 租赁申请主键
     * @return 租赁申请
     */
    ParkingRentalApplication selectParkingRentalApplicationById(Long id);

    /**
     * 查询租赁申请列表
     *
     * @param parkingRentalApplication 租赁申请
     * @return 租赁申请集合
     */
    List<ParkingRentalApplication> selectParkingRentalApplicationList(ParkingRentalApplication parkingRentalApplication);

    /**
     * 新增租赁申请
     *
     * @param parkingRentalApplication 租赁申请
     * @return 结果
     */
    int insertParkingRentalApplication(ParkingRentalApplication parkingRentalApplication);

    /**
     * 修改租赁申请
     *
     * @param parkingRentalApplication 租赁申请
     * @return 结果
     */
    int updateParkingRentalApplication(ParkingRentalApplication parkingRentalApplication);

    /**
     * 批量删除租赁申请
     *
     * @param ids 需要删除的租赁申请主键集合
     * @return 结果
     */
    int deleteParkingRentalApplicationByIds(Long[] ids);

    /**
     * 删除租赁申请信息
     *
     * @param id 租赁申请主键
     * @return 结果
     */
    int deleteParkingRentalApplicationById(Long id);

    /**
     * 分页查询租赁申请
     *
     * @param page 分页对象
     * @param parkingRentalApplication 查询条件
     * @return 分页结果
     */
    Page<ParkingRentalApplication> selectApplicationPage(Page<ParkingRentalApplication> page, ParkingRentalApplication parkingRentalApplication);

    /**
     * 查询待审核申请列表
     *
     * @return 待审核申请集合
     */
    List<ParkingRentalApplication> selectPendingApplications();

    /**
     * 审核租赁申请
     *
     * @param id 申请ID
     * @param status 审核状态
     * @param reviewRemark 审核备注
     * @param reviewUserId 审核人ID
     * @param reviewUserName 审核人姓名
     * @return 结果
     */
    boolean reviewApplication(Long id, Integer status, String reviewRemark, Long reviewUserId, String reviewUserName);
}