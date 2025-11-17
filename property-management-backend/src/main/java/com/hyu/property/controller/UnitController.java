package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.Unit;
import com.hyu.property.service.IUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 单元管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/unit")
@Validated
public class UnitController {

    @Autowired
    private IUnitService unitService;

    /**
     * 分页查询单元列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:unit:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long buildingId,
                          @RequestParam(required = false) String unitNo,
                          @RequestParam(required = false) String unitName) {
        log.info("分页查询单元列表, pageNum: {}, pageSize: {}, buildingId: {}, unitNo: {}, unitName: {}",
                pageNum, pageSize, buildingId, unitNo, unitName);

        Page<Unit> page = new Page<>(pageNum, pageSize);
        Unit unit = new Unit();
        unit.setBuildingId(buildingId);
        unit.setUnitNo(unitNo);
        unit.setUnitName(unitName);

        Page<Unit> result = unitService.selectUnitPage(page, unit);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取单元详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:unit:list')")
    public AjaxResult getInfo(@NotNull(message = "单元ID不能为空") @PathVariable Long id) {
        log.info("获取单元详细信息, id: {}", id);
        return AjaxResult.success(unitService.getById(id));
    }

    /**
     * 新增单元
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:unit:add')")
    public AjaxResult add(@Valid @RequestBody Unit unit) {
        log.info("新增单元, unit: {}", unit);
        unit.setCreateBy(SecurityUtils.getUsername());
        return toAjax(unitService.save(unit));
    }

    /**
     * 修改保存单元
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:unit:edit')")
    public AjaxResult edit(@Valid @RequestBody Unit unit) {
        log.info("修改单元, unit: {}", unit);
        unit.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(unitService.updateById(unit));
    }

    /**
     * 删除单元
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:unit:delete')")
    public AjaxResult remove(@NotNull(message = "单元ID不能为空") @PathVariable Long[] ids) {
        log.info("删除单元, ids: {}", ids);
        return toAjax(unitService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 根据楼栋ID获取单元列表
     */
    @GetMapping("/by-building/{buildingId}")
    @PreAuthorize("@ss.hasPermi('property:unit:list')")
    public AjaxResult getUnitsByBuildingId(@NotNull(message = "楼栋ID不能为空") @PathVariable Long buildingId) {
        log.info("根据楼栋ID获取单元列表, buildingId: {}", buildingId);
        return AjaxResult.success(unitService.getUnitsByBuildingId(buildingId));
    }

    /**
     * 获取单元下的房产列表
     */
    @GetMapping("/{id}/houses")
    @PreAuthorize("@ss.hasPermi('property:unit:list')")
    public AjaxResult getUnitHouses(@NotNull(message = "单元ID不能为空") @PathVariable Long id) {
        log.info("获取单元房产列表, unitId: {}", id);
        return AjaxResult.success(unitService.getUnitHouses(id));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}