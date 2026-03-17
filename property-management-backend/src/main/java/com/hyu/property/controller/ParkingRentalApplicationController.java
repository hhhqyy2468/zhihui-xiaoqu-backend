package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.hyu.property.domain.ParkingRentalApplication;
import com.hyu.property.service.IParkingRentalApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api(tags = "车位租赁申请管理")
public class ParkingRentalApplicationController {

    @Autowired
    private IParkingRentalApplicationService parkingRentalApplicationService;

    /**
     * 查询租赁申请列表
     */
    @ApiOperation("查询租赁申请列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('parking:rental:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String spaceNo,
                             @RequestParam(required = false) String ownerName,
                             @RequestParam(required = false) String vehicleNumber,
                             @RequestParam(required = false) Integer applicationStatus) {
        log.info("查询租赁申请列表, pageNum: {}, pageSize: {}, spaceNo: {}, ownerName: {}, vehicleNumber: {}, applicationStatus: {}",
                pageNum, pageSize, spaceNo, ownerName, vehicleNumber, applicationStatus);

        // 使用Service层的分页方法，在数据库层面分页
        ParkingRentalApplication query = createQueryApplication(spaceNo, ownerName, vehicleNumber, applicationStatus);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ParkingRentalApplication> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ParkingRentalApplication> result =
            parkingRentalApplicationService.selectApplicationPage(page, query);

        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取租赁申请详细信息
     */
    @ApiOperation("获取租赁申请详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('parking:rental:query')")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        log.info("获取租赁申请详细信息, id: {}", id);
        ParkingRentalApplication application = parkingRentalApplicationService.selectParkingRentalApplicationById(id);
        return AjaxResult.success(application);
    }

    /**
     * 新增租赁申请
     */
    @ApiOperation("新增租赁申请")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('parking:rental:add')")
    public AjaxResult add(@RequestBody ParkingRentalApplication parkingRentalApplication) {
        log.info("新增租赁申请, application: {}", parkingRentalApplication);
        return toAjax(parkingRentalApplicationService.insertParkingRentalApplication(parkingRentalApplication));
    }

    /**
     * 修改租赁申请
     */
    @ApiOperation("修改租赁申请")
    @PutMapping
    @PreAuthorize("@ss.hasPermi('parking:rental:edit')")
    public AjaxResult edit(@RequestBody ParkingRentalApplication parkingRentalApplication) {
        log.info("修改租赁申请, application: {}", parkingRentalApplication);
        return toAjax(parkingRentalApplicationService.updateParkingRentalApplication(parkingRentalApplication));
    }

    /**
     * 删除租赁申请
     */
    @ApiOperation("删除租赁申请")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('parking:rental:remove')")
    public AjaxResult remove(@PathVariable Long[] ids) {
        log.info("删除租赁申请, ids: {}", ids);
        return toAjax(parkingRentalApplicationService.deleteParkingRentalApplicationByIds(ids));
    }

    /**
     * 审核租赁申请
     */
    @ApiOperation("审核租赁申请")
    @PutMapping("/{id}/review")
    @PreAuthorize("@ss.hasPermi('parking:rental:review')")
    public AjaxResult review(@PathVariable("id") Long id,
                          @RequestParam Integer status,
                          @RequestParam(required = false) String reviewRemark) {
        log.info("审核租赁申请, id: {}, status: {}, reviewRemark: {}", id, status, reviewRemark);

        // 从SecurityContext获取当前登录用户信息
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserName = SecurityUtils.getUsername();

        boolean result = parkingRentalApplicationService.reviewApplication(
            id, status, reviewRemark, currentUserId, currentUserName);

        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取待审核申请列表
     */
    @ApiOperation("获取待审核申请列表")
    @GetMapping("/pending")
    @PreAuthorize("@ss.hasPermi('parking:rental:list')")
    public AjaxResult getPendingApplications() {
        log.info("获取待审核申请列表");
        List<ParkingRentalApplication> applications = parkingRentalApplicationService.selectPendingApplications();
        return AjaxResult.success(applications);
    }

    /**
     * 业主提交车位租赁申请（无需权限）
     */
    @ApiOperation("业主提交车位租赁申请")
    @PostMapping("/my")
    public AjaxResult submitMyApplication(@RequestBody ParkingRentalApplication parkingRentalApplication) {
        log.info("业主提交车位租赁申请, parkingSpaceId: {}, vehicleNumber: {}",
                parkingRentalApplication.getParkingSpaceId(), parkingRentalApplication.getVehicleNumber());

        // 从当前登录用户获取业主信息
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserName = SecurityUtils.getUsername();

        parkingRentalApplication.setOwnerId(currentUserId);
        parkingRentalApplication.setOwnerName(currentUserName);

        int result = parkingRentalApplicationService.insertParkingRentalApplication(parkingRentalApplication);
        return result > 0 ? AjaxResult.success("申请提交成功，请等待管理员审核") : AjaxResult.error("申请提交失败");
    }

    /**
     * 查询当前用户的租赁申请记录（我的申请）
     */
    @ApiOperation("查询我的租赁申请记录")
    @GetMapping("/my")
    public AjaxResult getMyApplications() {
        Long currentUserId = SecurityUtils.getUserId();
        log.info("查询当前用户的租赁申请记录, userId: {}", currentUserId);

        ParkingRentalApplication query = new ParkingRentalApplication();
        query.setOwnerId(currentUserId);

        List<ParkingRentalApplication> applications = parkingRentalApplicationService.selectParkingRentalApplicationList(query);
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