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
    // @PreAuthorize("@ss.hasPermi('property:building:list')")  // 临时移除权限验证进行测试
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String buildingNo,
                           @RequestParam(required = false) String buildingName,
                           @RequestParam(required = false) String address) {
        log.info("分页查询楼栋列表, pageNum: {}, pageSize: {}, buildingNo: {}, buildingName: {}, address: {}",
                pageNum, pageSize, buildingNo, buildingName, address);

        Page<Building> page = new Page<>(pageNum, pageSize);
        Building building = new Building();
        building.setBuildingNo(buildingNo);
        building.setBuildingName(buildingName);
        building.setAddress(address);

        Page<Building> result = buildingService.selectBuildingPage(page, building);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取楼栋详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    public AjaxResult getInfo(@NotNull(message = "楼栋ID不能为空") @PathVariable Long id) {
        log.info("获取楼栋详细信息, id: {}", id);
        return AjaxResult.success(buildingService.getById(id));
    }

    /**
     * 新增楼栋
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:building:add')")
    public AjaxResult add(@Valid @RequestBody Building building) {
        log.info("新增楼栋, building: {}", building);
        if (!buildingService.checkBuildingNoUnique(building)) {
            return AjaxResult.error("新增楼栋'" + building.getBuildingName() + "'失败，楼栋编号已存在");
        }
        building.setCreateBy(SecurityUtils.getUsername());
        return toAjax(buildingService.save(building));
    }

    /**
     * 修改保存楼栋
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:building:edit')")
    public AjaxResult edit(@Valid @RequestBody Building building) {
        log.info("修改楼栋, building: {}", building);
        if (!buildingService.checkBuildingNoUnique(building)) {
            return AjaxResult.error("修改楼栋'" + building.getBuildingName() + "'失败，楼栋编号已存在");
        }
        building.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(buildingService.updateById(building));
    }

    /**
     * 删除楼栋
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:building:delete')")
    public AjaxResult remove(@NotNull(message = "楼栋ID不能为空") @PathVariable Long[] ids) {
        log.info("删除楼栋, ids: {}", ids);
        return toAjax(buildingService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 获取楼栋下的单元列表
     */
    @GetMapping("/{id}/units")
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    public AjaxResult getBuildingUnits(@NotNull(message = "楼栋ID不能为空") @PathVariable Long id) {
        log.info("获取楼栋单元列表, buildingId: {}", id);
        return AjaxResult.success(buildingService.getBuildingUnits(id));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}