package com.hyu.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.Building;
import com.hyu.property.service.IBuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼栋管理控制器
 */
@Tag(name = "楼栋管理")
@RestController
@RequestMapping("/property/building")
public class BuildingController {

    @Autowired
    private IBuildingService buildingService;

    /**
     * 分页查询楼栋列表
     */
    @Operation(summary = "分页查询楼栋列表")
    @GetMapping("/list")
    public AjaxResult<IPage<Building>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buildingNo,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) Integer buildYearStart,
            @RequestParam(required = false) Integer buildYearEnd) {
        
        QueryWrapper<Building> wrapper = new QueryWrapper<>();
        
        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.like("building_no", buildingNo);
        }
        
        if (buildingName != null && !buildingName.isEmpty()) {
            wrapper.like("building_name", buildingName);
        }
        
        if (buildYearStart != null) {
            wrapper.ge("build_year", buildYearStart);
        }
        
        if (buildYearEnd != null) {
            wrapper.le("build_year", buildYearEnd);
        }
        
        IPage<Building> page = buildingService.page(new Page<>(pageNum, pageSize), wrapper);
        return AjaxResult.success(page);
    }

    /**
     * 获取楼栋详情
     */
    @Operation(summary = "获取楼栋详情")
    @GetMapping("/{id}")
    public AjaxResult<Building> detail(@PathVariable Long id) {
        Building building = buildingService.getById(id);
        return AjaxResult.success(building);
    }

    /**
     * 新增楼栋
     */
    @Operation(summary = "新增楼栋")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Building building) {
        boolean result = buildingService.save(building);
        return result ? AjaxResult.success() : AjaxResult.error("新增失败");
    }

    /**
     * 编辑楼栋
     */
    @Operation(summary = "编辑楼栋")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Building building) {
        boolean result = buildingService.updateById(building);
        return result ? AjaxResult.success() : AjaxResult.error("编辑失败");
    }

    /**
     * 删除楼栋
     */
    @Operation(summary = "删除楼栋")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable List<Long> ids) {
        boolean result = buildingService.removeByIds(ids);
        return result ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    /**
     * 获取所有楼栋（下拉框用）
     */
    @Operation(summary = "获取所有楼栋")
    @GetMapping("/all")
    public AjaxResult<List<Building>> all() {
        List<Building> list = buildingService.list();
        return AjaxResult.success(list);
    }
}