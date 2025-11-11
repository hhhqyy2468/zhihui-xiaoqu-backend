package com.hyu.system.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.system.domain.SysMenu;
import com.hyu.system.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * 菜单信息控制器
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/menu")
@Validated
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public AjaxResult list(SysMenu menu) {
        log.info("获取菜单列表, menu: {}", menu);
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public AjaxResult getInfo(@NotNull(message = "菜单ID不能为空") @PathVariable Long menuId) {
        log.info("根据菜单编号获取详细信息, menuId: {}", menuId);
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public AjaxResult treeselect(SysMenu menu) {
        log.info("获取菜单下拉树列表, menu: {}", menu);
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public AjaxResult roleMenuTreeselect(@NotNull(message = "角色ID不能为空") @PathVariable Long roleId) {
        log.info("加载对应角色菜单列表树, roleId: {}", roleId);
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);

        // 创建返回结果对象
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("checkedKeys", menuService.selectMenuPermsByRoleId(roleId));
        result.put("menus", menuService.buildMenuTreeSelect(menus));

        return AjaxResult.success(result);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    public AjaxResult add(@Valid @RequestBody SysMenu menu) {
        log.info("新增菜单, menu: {}", menu);
        if (!menuService.checkMenuNameUnique(menu)) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    public AjaxResult edit(@Valid @RequestBody SysMenu menu) {
        log.info("修改菜单, menu: {}", menu);
        if (!menuService.checkMenuNameUnique(menu)) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    public AjaxResult remove(@NotNull(message = "菜单ID不能为空") @PathVariable Long menuId) {
        log.info("删除菜单, menuId: {}", menuId);
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return AjaxResult.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取用户菜单权限标识
     */
    @GetMapping("/perms/{userId}")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public AjaxResult getPerms(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        log.info("获取用户菜单权限标识, userId: {}", userId);
        Set<String> perms = menuService.selectMenuPermsByUserId(userId);
        return AjaxResult.success(perms);
    }

    /**
     * 获取当前用户菜单权限标识
     */
    @GetMapping("/currentPerms")
    public AjaxResult getCurrentPerms() {
        log.info("获取当前用户菜单权限标识");
        Long userId = SecurityUtils.getUserId();
        Set<String> perms = menuService.selectMenuPermsByUserId(userId);
        return AjaxResult.success(perms);
    }

    /**
     * 获取当前用户菜单列表
     */
    @GetMapping("/currentMenus")
    public AjaxResult getCurrentMenus() {
        log.info("获取当前用户菜单列表");
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        return AjaxResult.success(menuService.buildMenuTree(menus));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int result) {
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}