package com.hyu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.system.domain.SysRole;
import com.hyu.system.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色信息
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/role")
@Validated
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:role:view')")
    public PageResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String roleName,
                           @RequestParam(required = false) Integer status) {
        log.info("分页查询角色列表, pageNum: {}, pageSize: {}, roleName: {}, status: {}", pageNum, pageSize, roleName, status);

        Page<SysRole> page = new Page<>(pageNum, pageSize);
        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setStatus(status);

        Page<SysRole> result = roleService.selectRolePage(page, role);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    /**
     * 获取角色详细信息
     */
    @GetMapping(value = "/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:role:view')")
    public AjaxResult getInfo(@NotNull(message = "角色ID不能为空") @PathVariable Long roleId) {
        log.info("获取角色详细信息, roleId: {}", roleId);
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    public AjaxResult add(@Valid @RequestBody SysRole role) {
        log.info("新增角色, role: {}", role);
        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUsername());
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改保存角色
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult edit(@Valid @RequestBody SysRole role) {
        log.info("修改角色, role: {}", role);
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());

        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @PutMapping("/dataScope")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        log.info("修改角色数据权限, role: {}", role);
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        log.info("修改角色状态, role: {}", role);
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    @PreAuthorize("@ss.hasPermi('system:role:delete')")
    public AjaxResult remove(@NotNull(message = "角色ID不能为空") @PathVariable Long[] roleIds) {
        log.info("删除角色, roleIds: {}", roleIds);
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        log.info("获取角色选择框列表");
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @GetMapping("/authUser/allocatedList")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public AjaxResult allocatedList(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String roleName,
                                   @RequestParam(required = false) String userName) {
        log.info("查询已分配用户角色列表, pageNum: {}, pageSize: {}, roleName: {}, userName: {}", pageNum, pageSize, roleName, userName);
        // TODO: 实现已分配用户角色列表查询
        return AjaxResult.success("功能待实现");
    }

    /**
     * 查询未分配用户角色列表
     */
    @GetMapping("/authUser/unallocatedList")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public AjaxResult unallocatedList(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String roleName,
                                     @RequestParam(required = false) String userName) {
        log.info("查询未分配用户角色列表, pageNum: {}, pageSize: {}, roleName: {}, userName: {}", pageNum, pageSize, roleName, userName);
        // TODO: 实现未分配用户角色列表查询
        return AjaxResult.success("功能待实现");
    }

    /**
     * 取消授权用户
     */
    @PutMapping("/authUser/cancel")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult cancelAuthUser(@RequestBody SysRole userRole) {
        log.info("取消授权用户, userRole: {}", userRole);
        return toAjax(roleService.deleteUserRoleInfo(userRole.getUserId(), userRole.getRoleId()));
    }

    /**
     * 批量取消授权用户
     */
    @PutMapping("/authUser/cancelAll")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds) {
        log.info("批量取消授权用户, roleId: {}, userIds: {}", roleId, userIds);
        return toAjax(roleService.deleteUserRoleInfo(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PutMapping("/authUser/selectAll")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds) {
        log.info("批量选择用户授权, roleId: {}, userIds: {}", roleId, userIds);
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthRoleUsers(roleId, userIds));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}