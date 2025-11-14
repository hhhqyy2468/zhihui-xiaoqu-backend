package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.House;
import com.hyu.property.service.IHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 房产管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/house")
@Validated
public class HouseController {

    @Autowired
    private IHouseService houseService;

    /**
     * 分页查询房产列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:house:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long buildingId,
                          @RequestParam(required = false) Long unitId,
                          @RequestParam(required = false) String houseNo,
                          @RequestParam(required = false) String roomNumber,
                          @RequestParam(required = false) Integer houseStatus) {
        log.info("分页查询房产列表, pageNum: {}, pageSize: {}, buildingId: {}, unitId: {}, houseNo: {}, roomNumber: {}, houseStatus: {}",
                pageNum, pageSize, buildingId, unitId, houseNo, roomNumber, houseStatus);

        Page<House> page = new Page<>(pageNum, pageSize);
        House house = new House();
        house.setBuildingId(buildingId);
        house.setUnitId(unitId);
        house.setHouseNo(houseNo);
        house.setRoomNumber(roomNumber);
        house.setHouseStatus(houseStatus);

        Page<House> result = houseService.selectHousePage(page, house);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取房产详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:house:list')")
    public AjaxResult getInfo(@NotNull(message = "房产ID不能为空") @PathVariable Long id) {
        log.info("获取房产详细信息, id: {}", id);
        return AjaxResult.success(houseService.getById(id));
    }

    /**
     * 新增房产
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:house:add')")
    public AjaxResult add(@Valid @RequestBody House house) {
        log.info("新增房产, house: {}", house);
        if (!houseService.checkHouseNoUnique(house)) {
            return AjaxResult.error("新增房产'" + house.getHouseNo() + "'失败，房产编号已存在");
        }
        house.setCreateBy(SecurityUtils.getUsername());
        return toAjax(houseService.save(house));
    }

    /**
     * 修改保存房产
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:house:edit')")
    public AjaxResult edit(@Valid @RequestBody House house) {
        log.info("修改房产, house: {}", house);
        if (!houseService.checkHouseNoUnique(house)) {
            return AjaxResult.error("修改房产'" + house.getHouseNo() + "'失败，房产编号已存在");
        }
        house.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(houseService.updateById(house));
    }

    /**
     * 删除房产
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:house:delete')")
    public AjaxResult remove(@NotNull(message = "房产ID不能为空") @PathVariable Long[] ids) {
        log.info("删除房产, ids: {}", ids);
        return toAjax(houseService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}