package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.vo.OwnerVO;
import com.hyu.property.service.IOwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 业主管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/owner")
@Validated
public class OwnerController {

    @Autowired
    private IOwnerService ownerService;

    /**
     * 分页查询业主列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String realName,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) Integer residentStatus,
                          @RequestParam(required = false) String beginTime,
                          @RequestParam(required = false) String endTime) {
        log.info("分页查询业主列表, pageNum: {}, pageSize: {}, username: {}, realName: {}, phone: {}, residentStatus: {}",
                pageNum, pageSize, username, realName, phone, residentStatus);

        Page<OwnerVO> page = new Page<>(pageNum, pageSize);
        OwnerVO owner = new OwnerVO();
        owner.setUsername(username);
        owner.setRealName(realName);
        owner.setPhone(phone);
        owner.setResidentStatus(residentStatus);
        owner.setBeginTime(beginTime);
        owner.setEndTime(endTime);

        Page<OwnerVO> result = ownerService.selectOwnerPage(page, owner);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取业主详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult getInfo(@NotNull(message = "业主ID不能为空") @PathVariable Long id) {
        log.info("获取业主详细信息, id: {}", id);
        return AjaxResult.success(ownerService.getOwnerById(id));
    }

    /**
     * 新增业主
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:owner:add')")
    public AjaxResult add(@Valid @RequestBody OwnerVO owner) {
        log.info("新增业主, owner: {}", owner);
        if (!ownerService.checkUsernameUnique(owner)) {
            return AjaxResult.error("新增业主'" + owner.getUsername() + "'失败，登录账号已存在");
        }
        if (!ownerService.checkPhoneUnique(owner)) {
            return AjaxResult.error("新增业主'" + owner.getUsername() + "'失败，手机号码已存在");
        }
        owner.setCreateBy(SecurityUtils.getUsername());
        return toAjax(ownerService.insertOwner(owner));
    }

    /**
     * 修改保存业主
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult edit(@Valid @RequestBody OwnerVO owner) {
        log.info("修改业主, owner: {}", owner);
        if (!ownerService.checkUsernameUnique(owner)) {
            return AjaxResult.error("修改业主'" + owner.getUsername() + "'失败，登录账号已存在");
        }
        if (!ownerService.checkPhoneUnique(owner)) {
            return AjaxResult.error("修改业主'" + owner.getUsername() + "'失败，手机号码已存在");
        }
        owner.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(ownerService.updateOwner(owner));
    }

    /**
     * 删除业主
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:owner:delete')")
    public AjaxResult remove(@NotNull(message = "业主ID不能为空") @PathVariable Long[] ids) {
        log.info("删除业主, ids: {}", ids);
        return toAjax(ownerService.deleteOwnersByIds(ids));
    }

    /**
     * 重置业主密码
     */
    @PutMapping("/reset-password")
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult resetPassword(@RequestBody OwnerVO owner) {
        log.info("重置业主密码, ownerId: {}", owner.getUserId());
        owner.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(ownerService.resetPassword(owner));
    }

    /**
     * 修改业主状态
     */
    @PutMapping("/change-status")
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    public AjaxResult changeStatus(@RequestBody OwnerVO owner) {
        log.info("修改业主状态, ownerId: {}, status: {}", owner.getUserId(), owner.getStatus());
        owner.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(ownerService.updateOwnerStatus(owner));
    }

    /**
     * 根据房产ID获取业主列表
     */
    @GetMapping("/by-house/{houseId}")
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    public AjaxResult getOwnersByHouseId(@NotNull(message = "房产ID不能为空") @PathVariable Long houseId) {
        log.info("根据房产ID获取业主列表, houseId: {}", houseId);
        return AjaxResult.success(ownerService.getOwnersByHouseId(houseId));
    }

  
    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}