package com.hyu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 用户信息控制器
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/user")
@Validated
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public PageResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String realName,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) Integer status,
                           @RequestParam(required = false) Integer userType,
                           @RequestParam(required = false) String beginTime,
                           @RequestParam(required = false) String endTime) {
        log.info("分页查询用户列表, pageNum: {}, pageSize: {}, username: {}, realName: {}, phone: {}, status: {}, userType: {}, beginTime: {}, endTime: {}",
                 pageNum, pageSize, username, realName, phone, status, userType, beginTime, endTime);

        Page<SysUser> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(username), "username", username)
                   .like(StringUtils.isNotEmpty(realName), "real_name", realName)
                   .like(StringUtils.isNotEmpty(phone), "phone", phone)
                   .eq(status != null, "status", status)
                   .eq(userType != null, "user_type", userType)
                   .ge(StringUtils.isNotEmpty(beginTime), "create_time", beginTime)
                   .le(StringUtils.isNotEmpty(endTime), "create_time", endTime)
                   .orderByDesc("create_time");

        Page<SysUser> result = userService.page(page, queryWrapper);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping(value = "/{userId}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public AjaxResult getInfo(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        log.info("获取用户详细信息, userId: {}", userId);
        return AjaxResult.success(userService.getById(userId));
    }

    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    public AjaxResult add(@Valid @RequestBody SysUser user) {
        log.info("新增用户, user: {}", user);

        if (!userService.checkUsernameUnique(user)) {
            return AjaxResult.error("新增用户'" + user.getUsername() + "'失败，用户名已存在");
        }
        if (StringUtils.isNotEmpty(user.getPhone()) && !userService.checkPhoneUnique(user)) {
            return AjaxResult.error("新增用户'" + user.getUsername() + "'失败，手机号已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return AjaxResult.error("新增用户'" + user.getUsername() + "'失败，邮箱已存在");
        }

        user.setCreateBy(SecurityUtils.getUsername());
        return toAjax(userService.save(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public AjaxResult edit(@Valid @RequestBody SysUser user) {
        log.info("修改用户, user: {}", user);

        if (!userService.checkUsernameUnique(user)) {
            return AjaxResult.error("修改用户'" + user.getUsername() + "'失败，用户名已存在");
        }
        if (StringUtils.isNotEmpty(user.getPhone()) && !userService.checkPhoneUnique(user)) {
            return AjaxResult.error("修改用户'" + user.getUsername() + "'失败，手机号已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return AjaxResult.error("修改用户'" + user.getUsername() + "'失败，邮箱已存在");
        }

        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserInfo(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    public AjaxResult remove(@NotNull(message = "用户ID不能为空") @PathVariable Long[] userIds) {
        log.info("删除用户, userIds: {}", Arrays.toString(userIds));
        return toAjax(userService.removeByIds(Arrays.asList(userIds)));
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/resetPwd")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        log.info("重置用户密码, userId: {}", user.getUserId());
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetUserPassword(user.getUserId(), "123456"));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        log.info("修改用户状态, userId: {}, status: {}", user.getUserId(), user.getStatus());
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserInfo(user));
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public AjaxResult getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户信息, username: {}", username);
        return AjaxResult.success(userService.selectUserByUsername(username));
    }

    /**
     * 根据手机号获取用户信息
     */
    @GetMapping("/phone/{phone}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public AjaxResult getUserByPhone(@PathVariable String phone) {
        log.info("根据手机号获取用户信息, phone: {}", phone);
        return AjaxResult.success(userService.selectUserByPhone(phone));
    }

    /**
     * 根据邮箱获取用户信息
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public AjaxResult getUserByEmail(@PathVariable String email) {
        log.info("根据邮箱获取用户信息, email: {}", email);
        return AjaxResult.success(userService.selectUserByEmail(email));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}