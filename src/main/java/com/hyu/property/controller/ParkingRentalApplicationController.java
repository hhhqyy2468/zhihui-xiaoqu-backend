package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.property.domain.ParkingRentalApplication;
import com.hyu.property.service.IParkingRentalApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车位租赁申请控制器
 *
 * @author system
 * @date 2025-11-18
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/parking/rental/application")
public class ParkingRentalApplicationController {

    @Autowired
    private IParkingRentalApplicationService parkingRentalApplicationService;

    /**
     * 查询租赁申请列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String spaceNo,
                             @RequestParam(required = false) String ownerName,
                             @RequestParam(required = false) String vehicleNumber,
                             @RequestParam(required = false) Integer applicationStatus) {
        log.info("查询租赁申请列表, pageNum: {}, pageSize: {}, spaceNo: {}, ownerName: {}, vehicleNumber: {}, applicationStatus: {}",
                pageNum, pageSize, spaceNo, ownerName, vehicleNumber, applicationStatus);

        List<ParkingRentalApplication> applications = parkingRentalApplicationService.selectParkingRentalApplicationList(
            createQueryApplication(spaceNo, ownerName, vehicleNumber, applicationStatus));

        // 简单分页处理
        int total = applications.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<ParkingRentalApplication> pageData = applications.subList(start < total ? start : total, end);

        return AjaxResult.success("查询成功", PageResult.success((long) total, pageData));
    }

    /**
     * 获取租赁申请详细信息
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        log.info("获取租赁申请详细信息, id: {}", id);
        ParkingRentalApplication application = parkingRentalApplicationService.selectParkingRentalApplicationById(id);
        return AjaxResult.success(application);
    }

    /**
     * 新增租赁申请
     */
    @PostMapping
    public AjaxResult add(@RequestBody ParkingRentalApplication parkingRentalApplication) {
        log.info("新增租赁申请, application: {}", parkingRentalApplication);
        return toAjax(parkingRentalApplicationService.insertParkingRentalApplication(parkingRentalApplication));
    }

    /**
     * 修改租赁申请
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ParkingRentalApplication parkingRentalApplication) {
        log.info("修改租赁申请, application: {}", parkingRentalApplication);
        return toAjax(parkingRentalApplicationService.updateParkingRentalApplication(parkingRentalApplication));
    }

    /**
     * 删除租赁申请
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        log.info("删除租赁申请, ids: {}", ids);
        return toAjax(parkingRentalApplicationService.deleteParkingRentalApplicationByIds(ids));
    }

    /**
     * 审核租赁申请
     */
    @PutMapping("/{id}/review")
    public AjaxResult review(@PathVariable("id") Long id,
                          @RequestParam Integer status,
                          @RequestParam(required = false) String reviewRemark) {
        log.info("审核租赁申请, id: {}, status: {}, reviewRemark: {}", id, status, reviewRemark);

        boolean result = parkingRentalApplicationService.reviewApplication(
            id, status, reviewRemark, 1L, "admin");

        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取待审核申请列表
     */
    @GetMapping("/pending")
    public AjaxResult getPendingApplications() {
        log.info("获取待审核申请列表");
        List<ParkingRentalApplication> applications = parkingRentalApplicationService.selectPendingApplications();
        return AjaxResult.success(applications);
    }

    /**
     * 创建查询条件
     */
    private ParkingRentalApplication createQueryApplication(String spaceNo, String ownerName,
                                                          String vehicleNumber, Integer applicationStatus) {
        ParkingRentalApplication application = new ParkingRentalApplication();
        application.setSpaceNo(spaceNo);
        application.setOwnerName(ownerName);
        application.setVehicleNumber(vehicleNumber);
        application.setApplicationStatus(applicationStatus);
        return application;
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int result) {
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}