package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.ParkingSpace;
import com.hyu.property.service.IParkingSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 车位管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/parking/space")
@Validated
public class ParkingSpaceController {

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    /**
     * 分页查询车位列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('parking:space:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String spaceNo,
                          @RequestParam(required = false) String location,
                          @RequestParam(required = false) Integer status) {
        log.info("分页查询车位列表, pageNum: {}, pageSize: {}, spaceNo: {}, location: {}, status: {}",
                pageNum, pageSize, spaceNo, location, status);

        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        ParkingSpace parkingSpace = new ParkingSpace();
        parkingSpace.setSpaceNo(spaceNo);
        parkingSpace.setLocation(location);
        parkingSpace.setSpaceStatus(status);

        Page<ParkingSpace> result = parkingSpaceService.selectParkingSpacePage(page, parkingSpace);
        return AjaxResult.success("查询成功", com.hyu.common.core.domain.PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取车位详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('parking:space:list')")
    public AjaxResult getInfo(@NotNull(message = "车位ID不能为空") @PathVariable Long id) {
        log.info("获取车位详细信息, id: {}", id);
        ParkingSpace parkingSpace = parkingSpaceService.selectParkingSpaceById(id);
        if (parkingSpace != null) {
            // 添加前端需要的字段映射
            addFrontendFields(parkingSpace);
        }
        return AjaxResult.success(parkingSpace);
    }

    /**
     * 新增车位
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('parking:space:add')")
    public AjaxResult add(@Valid @RequestBody ParkingSpace parkingSpace) {
        log.info("新增车位, parkingSpace: {}", parkingSpace);
        parkingSpace.setCreateBy(SecurityUtils.getUsername());
        return toAjax(parkingSpaceService.insertParkingSpace(parkingSpace));
    }

    /**
     * 修改保存车位
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('parking:space:edit')")
    public AjaxResult edit(@Valid @RequestBody ParkingSpace parkingSpace) {
        log.info("修改车位, parkingSpace: {}", parkingSpace);
        parkingSpace.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(parkingSpaceService.updateParkingSpace(parkingSpace));
    }

    /**
     * 删除车位
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('parking:space:delete')")
    public AjaxResult remove(@NotNull(message = "车位ID不能为空") @PathVariable Long[] ids) {
        log.info("删除车位, ids: {}", ids);
        return toAjax(parkingSpaceService.deleteParkingSpaceByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 获取车位统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermi('parking:space:list')")
    public AjaxResult getStatistics() {
        log.info("获取车位统计数据");
        Map<String, Object> stats = parkingSpaceService.getParkingSpaceStats();
        return AjaxResult.success(stats);
    }

    /**
     * 添加前端需要的字段映射
     */
    private void addFrontendFields(ParkingSpace parkingSpace) {
        // 前端期望 status 字段，后端是 spaceStatus
        if (parkingSpace.getSpaceStatus() != null) {
            parkingSpace.setSpaceStatusName(getStatusName(parkingSpace.getSpaceStatus()));
        }

        // 前端期望 ownerName 字段
        parkingSpace.setCurrentTenant("");

        // 设置是否可用
        parkingSpace.setIsAvailable(parkingSpace.getSpaceStatus() != null && parkingSpace.getSpaceStatus() == 1);
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "空闲";
            case 2:
                return "已租";
            case 3:
                return "维修中";
            default:
                return "未知";
        }
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}