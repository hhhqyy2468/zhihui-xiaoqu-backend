package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.service.IBuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 楼栋信息
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/building")
@Validated
public class BuildingController {

    @Autowired
    private IBuildingService buildingService;

    /**
     * 分页查询楼栋列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    public PageResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String buildingCode,
                           @RequestParam(required = false) String buildingName,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) Integer buildYearStart,
                           @RequestParam(required = false) Integer buildYearEnd,
                           @RequestParam(required = false) Integer status) {
        log.info("分页查询楼栋列表, pageNum: {}, pageSize: {}, buildingCode: {}, buildingName: {}, address: {}, buildYearStart: {}, buildYearEnd: {}, status: {}",
                pageNum, pageSize, buildingCode, buildingName, address, buildYearStart, buildYearEnd, status);

        Page<Building> page = new Page<>(pageNum, pageSize);
        Building building = new Building();
        building.setBuildingCode(buildingCode);
        building.setBuildingName(buildingName);
        building.setAddress(address);
        building.setBuildYearStart(buildYearStart);
        building.setBuildYearEnd(buildYearEnd);
        building.setStatus(status);

        Page<Building> result = buildingService.selectBuildingPage(page, building);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    /**
     * 获取楼栋详细信息
     */
    @GetMapping(value = "/{buildingId}")
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    public AjaxResult getInfo(@NotNull(message = "楼栋ID不能为空") @PathVariable Long buildingId) {
        log.info("获取楼栋详细信息, buildingId: {}", buildingId);
        return AjaxResult.success(buildingService.selectBuildingById(buildingId));
    }

    /**
     * 新增楼栋
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:building:add')")
    public AjaxResult add(@Valid @RequestBody Building building) {
        log.info("新增楼栋, building: {}", building);
        return toAjax(buildingService.insertBuilding(building));
    }

    /**
     * 修改保存楼栋
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:building:edit')")
    public AjaxResult edit(@Valid @RequestBody Building building) {
        log.info("修改楼栋, building: {}", building);
        return toAjax(buildingService.updateBuilding(building));
    }

    /**
     * 删除楼栋
     */
    @DeleteMapping("/{buildingIds}")
    @PreAuthorize("@ss.hasPermi('property:building:delete')")
    public AjaxResult remove(@NotNull(message = "楼栋ID不能为空") @PathVariable Long[] buildingIds) {
        log.info("删除楼栋, buildingIds: {}", buildingIds);
        return toAjax(buildingService.deleteBuildingByIds(buildingIds));
    }

    /**
     * 获取所有楼栋（下拉框用）
     */
    @GetMapping("/all")
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    public AjaxResult listAll() {
        log.info("获取所有楼栋列表");
        return AjaxResult.success(buildingService.selectBuildingAll());
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}