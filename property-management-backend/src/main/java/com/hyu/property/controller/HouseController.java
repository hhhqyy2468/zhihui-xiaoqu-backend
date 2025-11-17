package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.property.domain.House;
import com.hyu.property.domain.vo.HouseVO;
import com.hyu.property.service.IHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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

        Page<HouseVO> page = new Page<>(pageNum, pageSize);
        House house = new House();
        house.setBuildingId(buildingId);
        house.setUnitId(unitId);
        house.setHouseNo(houseNo);
        house.setRoomNumber(roomNumber);
        house.setHouseStatus(houseStatus);

        Page<HouseVO> result = houseService.selectHouseVOPage(page, house);
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
        // house.setCreateBy(SecurityUtils.getUsername());
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
        // house.setUpdateBy(SecurityUtils.getUsername());
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
     * 获取可分配的房产列表
     */
    @GetMapping("/available")
    @PreAuthorize("@ss.hasPermi('property:house:list')")
    public AjaxResult getAvailableHouses(@RequestParam Long userId) {
        log.info("获取可分配房产列表, userId: {}", userId);
        List<HouseVO> houses = houseService.getAvailableHouses(userId);
        return AjaxResult.success("查询成功", houses);
    }

    /**
     * 分配房产给用户
     */
    @PostMapping("/assign")
    @PreAuthorize("@ss.hasPermi('property:house:assign')")
    public AjaxResult assignHouses(@RequestBody AssignHouseRequest request) {
        log.info("分配房产给用户, userId: {}, houseIds: {}, relationType: {}",
                request.getUserId(), request.getHouseIds(), request.getRelationType());

        boolean result = houseService.assignHouses(
                request.getUserId(),
                request.getHouseIds(),
                request.getRelationType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsCurrent()
        );

        if (result) {
            return AjaxResult.success("房产分配成功");
        } else {
            return AjaxResult.error("房产分配失败");
        }
    }

    /**
     * 根据用户名分配房产给用户
     */
    @PostMapping("/assign-by-username")
    @PreAuthorize("@ss.hasPermi('property:house:assign')")
    public AjaxResult assignHouseByUsername(@RequestBody AssignHouseByUsernameRequest request) {
        log.info("根据用户名分配房产, username: {}, houseId: {}, relationType: {}",
                request.getUsername(), request.getHouseId(), request.getRelationType());

        try {
            boolean result = houseService.assignHouseToUserByUsername(
                    request.getUsername(),
                    request.getHouseId(),
                    request.getRelationType(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getIsCurrent()
            );

            if (result) {
                return AjaxResult.success("房产分配成功");
            } else {
                return AjaxResult.error("房产分配失败");
            }
        } catch (Exception e) {
            log.error("房产分配失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 分配房产请求对象
     */
    public static class AssignHouseRequest {
        private Long userId;
        private List<Long> houseIds;
        private Integer relationType;
        private String startDate;
        private String endDate;
        private Boolean isCurrent;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public List<Long> getHouseIds() { return houseIds; }
        public void setHouseIds(List<Long> houseIds) { this.houseIds = houseIds; }

        public Integer getRelationType() { return relationType; }
        public void setRelationType(Integer relationType) { this.relationType = relationType; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public Boolean getIsCurrent() { return isCurrent; }
        public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }
    }

    /**
     * 根据用户名分配房产请求对象
     */
    public static class AssignHouseByUsernameRequest {
        private String username;
        private Long houseId;
        private Integer relationType;
        private String startDate;
        private String endDate;
        private Boolean isCurrent;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public Long getHouseId() { return houseId; }
        public void setHouseId(Long houseId) { this.houseId = houseId; }

        public Integer getRelationType() { return relationType; }
        public void setRelationType(Integer relationType) { this.relationType = relationType; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public Boolean getIsCurrent() { return isCurrent; }
        public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }
    }

  
    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}