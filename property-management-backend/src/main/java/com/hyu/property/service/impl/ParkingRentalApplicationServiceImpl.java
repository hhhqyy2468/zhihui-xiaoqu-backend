package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.ParkingRentalApplication;
import com.hyu.property.domain.ParkingRentalContract;
import com.hyu.property.domain.ParkingSpace;
import com.hyu.property.mapper.ParkingRentalApplicationMapper;
import com.hyu.property.service.IParkingRentalApplicationService;
import com.hyu.property.service.IParkingRentalContractService;
import com.hyu.property.service.IParkingSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 车位租赁申请Service业务层处理
 *
 * @author system
 * @date 2025-11-18
 */
@Slf4j
@Service
public class ParkingRentalApplicationServiceImpl extends ServiceImpl<ParkingRentalApplicationMapper, ParkingRentalApplication> implements IParkingRentalApplicationService {

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Lazy
    @Autowired
    private IParkingRentalContractService contractService;

    @Override
    public Page<ParkingRentalApplication> selectApplicationPage(Page<ParkingRentalApplication> page, ParkingRentalApplication application) {
        QueryWrapper<ParkingRentalApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.isNotEmpty(application.getSpaceNo())) {
            queryWrapper.like("space_no", application.getSpaceNo());
        }
        if (StringUtils.isNotEmpty(application.getOwnerName())) {
            queryWrapper.like("owner_name", application.getOwnerName());
        }
        if (StringUtils.isNotEmpty(application.getVehicleNumber())) {
            queryWrapper.like("vehicle_number", application.getVehicleNumber());
        }
        if (application.getApplicationStatus() != null) {
            queryWrapper.eq("application_status", application.getApplicationStatus());
        }

        queryWrapper.orderByDesc("create_time");

        return this.page(page, queryWrapper);
    }

    @Override
    public List<ParkingRentalApplication> selectParkingRentalApplicationList(ParkingRentalApplication parkingRentalApplication) {
        return baseMapper.selectParkingRentalApplicationList(parkingRentalApplication);
    }

    @Override
    public ParkingRentalApplication selectParkingRentalApplicationById(Long id) {
        return baseMapper.selectParkingRentalApplicationById(id);
    }

    @Override
    public int insertParkingRentalApplication(ParkingRentalApplication parkingRentalApplication) {
        parkingRentalApplication.setApplicationStatus(1); // 待审核
        parkingRentalApplication.setDeleted(0);
        return baseMapper.insert(parkingRentalApplication);
    }

    @Override
    public int updateParkingRentalApplication(ParkingRentalApplication parkingRentalApplication) {
        return baseMapper.updateById(parkingRentalApplication);
    }

    @Override
    public int deleteParkingRentalApplicationByIds(Long[] ids) {
        QueryWrapper<ParkingRentalApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", Arrays.asList(ids));
        ParkingRentalApplication updateEntity = new ParkingRentalApplication();
        updateEntity.setDeleted(1);
        return baseMapper.update(updateEntity, queryWrapper);
    }

    @Override
    public int deleteParkingRentalApplicationById(Long id) {
        ParkingRentalApplication application = new ParkingRentalApplication();
        application.setId(id);
        application.setDeleted(1);
        return baseMapper.updateById(application);
    }

    @Override
    public List<ParkingRentalApplication> selectPendingApplications() {
        return baseMapper.selectPendingApplications();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewApplication(Long id, Integer status, String reviewRemark, Long reviewUserId, String reviewUserName) {
        // 1. 更新申请状态
        ParkingRentalApplication application = new ParkingRentalApplication();
        application.setId(id);
        application.setApplicationStatus(status);
        application.setReviewRemark(reviewRemark);
        application.setReviewUserId(reviewUserId);
        application.setReviewUserName(reviewUserName);
        application.setReviewTime(new Date());

        boolean result = baseMapper.updateById(application) > 0;

        // 2. 审核通过时更新车位状态并生成合同
        if (result && status == 2) {
            ParkingRentalApplication fullApplication = baseMapper.selectById(id);
            if (fullApplication != null && fullApplication.getParkingSpaceId() != null) {
                // 更新车位状态为已租
                ParkingSpace parkingSpace = new ParkingSpace();
                parkingSpace.setId(fullApplication.getParkingSpaceId());
                parkingSpace.setSpaceStatus(2); // 已租
                parkingSpaceService.updateById(parkingSpace);
                log.info("审核通过，车位状态已更新为已租。车位ID：{}", fullApplication.getParkingSpaceId());

                // 生成租赁合同和账单
                try {
                    ParkingRentalContract contract = contractService.generateContract(id);
                    log.info("审核通过，租赁合同已生成。合同编号：{}", contract.getContractNo());
                } catch (Exception e) {
                    log.error("生成租赁合同失败", e);
                    // 合同生成失败不影响审核流程，只记录日志
                }
            }
        }

        return result;
    }
}