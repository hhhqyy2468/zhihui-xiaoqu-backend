package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.UserHouse;
import com.hyu.property.service.IUserHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户房产关联Controller
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/user-house")
@Validated
public class UserHouseController {

    @Autowired
    private IUserHouseService userHouseService;

    /**
     * 查询用户房产关联列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) Long userId,
                           @RequestParam(required = false) Long houseId,
                           @RequestParam(required = false) Integer relationType,
                           @RequestParam(required = false) Boolean isCurrent) {
        log.info("查询用户房产关联列表, pageNum: {}, pageSize: {}, userId: {}, houseId: {}, relationType: {}",
                pageNum, pageSize, userId, houseId, relationType);

        Page<UserHouse> page = new Page<>(pageNum, pageSize);
        UserHouse userHouse = new UserHouse();
        userHouse.setUserId(userId);
        userHouse.setHouseId(houseId);
        userHouse.setRelationType(relationType);
        userHouse.setIsCurrent(isCurrent);

        Page<UserHouse> result = userHouseService.selectUserHousePage(page, userHouse);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取用户房产关联详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult getInfo(@NotNull(message = "关联ID不能为空") @PathVariable Long id) {
        log.info("获取用户房产关联详细信息, id: {}", id);
        return AjaxResult.success(userHouseService.selectUserHouseById(id));
    }

    /**
     * 根据用户ID查询房产列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult getHousesByUserId(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        log.info("根据用户ID查询房产列表, userId: {}", userId);
        List<UserHouse> houseList = userHouseService.selectUserHouseByUserId(userId);
        return AjaxResult.success("查询成功", houseList);
    }

    /**
     * 根据房产ID查询用户列表
     */
    @GetMapping("/house/{houseId}")
    @PreAuthorize("@ss.hasPermi('property:house:list')")
    public AjaxResult getUsersByHouseId(@NotNull(message = "房产ID不能为空") @PathVariable Long houseId) {
        log.info("根据房产ID查询用户列表, houseId: {}", houseId);
        List<UserHouse> userList = userHouseService.selectUserHouseByHouseId(houseId);
        return AjaxResult.success("查询成功", userList);
    }

    /**
     * 分配房产给用户
     */
    @PostMapping("/assign")
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult assignHouse(@RequestParam Long userId,
                                @RequestParam Long houseId,
                                @RequestParam(defaultValue = "1") Integer relationType) {
        log.info("分配房产给用户, userId: {}, houseId: {}, relationType: {}", userId, houseId, relationType);

        int result = userHouseService.assignHouseToUser(userId, houseId, relationType);
        if (result > 0) {
            return AjaxResult.success("分配成功");
        } else {
            return AjaxResult.error("分配失败，该房产可能已经分配给该用户");
        }
    }

    /**
     * 取消用户房产关联
     */
    @DeleteMapping("/unassign")
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult unassignHouse(@RequestParam Long userId, @RequestParam Long houseId) {
        log.info("取消用户房产关联, userId: {}, houseId: {}", userId, houseId);

        int result = userHouseService.unassignHouseFromUser(userId, houseId);
        if (result > 0) {
            return AjaxResult.success("取消成功");
        } else {
            return AjaxResult.error("取消失败");
        }
    }

    /**
     * 新增用户房产关联
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:owner:add')")
    public AjaxResult add(@Valid @RequestBody UserHouse userHouse) {
        log.info("新增用户房产关联, userHouse: {}", userHouse);
        userHouse.setCreateBy(SecurityUtils.getUsername());
        return toAjax(userHouseService.insertUserHouse(userHouse));
    }

    /**
     * 修改保存用户房产关联
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult edit(@Valid @RequestBody UserHouse userHouse) {
        log.info("修改用户房产关联, userHouse: {}", userHouse);
        userHouse.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userHouseService.updateUserHouse(userHouse));
    }

    /**
     * 删除用户房产关联
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:owner:delete')")
    public AjaxResult remove(@NotNull(message = "关联ID不能为空") @PathVariable Long[] ids) {
        log.info("删除用户房产关联, ids: {}", ids);
        return toAjax(userHouseService.deleteUserHouseByIds(ids));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Integer result) {
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}