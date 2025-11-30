package com.hyu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import com.hyu.property.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFRow;
import java.math.BigDecimal;

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

    @Autowired
    private IWalletService walletService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
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

        // 手动分页查询
        List<SysUser> allUsers = userService.list(queryWrapper);
        Long total = (long) allUsers.size();

        // 计算分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allUsers.size());

        List<SysUser> pageRecords = new ArrayList<>();
        if (start < allUsers.size()) {
            pageRecords = allUsers.subList(start, end);
        }

        return AjaxResult.success("查询成功", PageResult.success(total, pageRecords));
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
        boolean result = userService.save(user);

        if (result) {
            // 为新用户创建钱包记录
            try {
                boolean walletCreated = walletService.createWallet(user.getUserId(), BigDecimal.ZERO);
                if (walletCreated) {
                    log.info("为用户 {} 成功创建钱包记录", user.getUserId());
                } else {
                    log.warn("为用户 {} 创建钱包记录失败，可能钱包已存在", user.getUserId());
                }
            } catch (Exception e) {
                log.error("为用户 {} 创建钱包记录时发生错误", user.getUserId(), e);
                // 不影响用户创建结果，只记录错误
            }
        }

        return toAjax(result);
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
     * 统一用户操作接口（新增、修改、删除、角色分配等）
     */
    @PostMapping("/operation")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public AjaxResult operation(@RequestBody UserOperationRequest request) {
        log.info("用户操作接口, operation: {}, data: {}", request.getOperation(), request.getData());

        switch (request.getOperation()) {
            case "add":
                return toAjax(userService.save(request.getData()));
            case "edit":
                return toAjax(userService.updateUserInfo(request.getData()));
            case "delete":
                return toAjax(userService.removeByIds(request.getIds()));
            case "assignRoles":
                return toAjax(userService.assignUserRoles(request.getData().getUserId(), request.getRoleIds()));
            case "resetPwd":
                return toAjax(userService.resetUserPassword(request.getData().getUserId(), request.getData().getPassword()));
            case "changeStatus":
                return toAjax(userService.updateUserInfo(request.getData()));
            default:
                return AjaxResult.error("不支持的操作类型: " + request.getOperation());
        }
    }

    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/{userId}/roles")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public AjaxResult getUserRoles(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        log.info("获取用户的角色ID列表, userId: {}", userId);
        return AjaxResult.success(userService.getUserRoleIds(userId));
    }

    /**
     * 导出用户Excel
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    public void exportUsers(HttpServletResponse response,
                            @RequestParam(required = false) String username,
                            @RequestParam(required = false) String realName,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) Integer status,
                            @RequestParam(required = false) Integer userType,
                            @RequestParam(required = false) String beginTime,
                            @RequestParam(required = false) String endTime) throws IOException {
        log.info("导出用户数据, username: {}, realName: {}, phone: {}, status: {}, userType: {}, beginTime: {}, endTime: {}",
                username, realName, phone, status, userType, beginTime, endTime);

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

        // 查询数据
        List<SysUser> userList = userService.list(queryWrapper);

        // 创建工作簿
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet("用户数据");

            // 创建表头
            String[] headers = {"用户ID", "用户名", "真实姓名", "手机号", "邮箱", "用户类型", "状态", "创建时间", "更新时间"};
            SXSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (SysUser user : userList) {
                SXSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getRealName());
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(5).setCellValue(getUserTypeName(user.getUserType()));
                row.createCell(6).setCellValue(user.getStatus() == 1 ? "启用" : "禁用");
                row.createCell(7).setCellValue(user.getCreateTime() != null ? user.getCreateTime().toString() : "");
                row.createCell(8).setCellValue(user.getUpdateTime() != null ? user.getUpdateTime().toString() : "");
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("用户数据", "UTF-8").replaceAll("\\+", "%20");
            //String fileName = URLEncoder.encode("用户数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("{\"code\":500,\"msg\":\"导出失败\"}");
        }
    }

    /**
     * 获取用户类型名称
     */
    private String getUserTypeName(Integer userType) {
        if (userType == null) return "";
        switch (userType) {
            case 1: return "系统管理员";
            case 2: return "物业管理员";
            case 3: return "业主";
            case 4: return "维修人员";
            default: return "未知";
        }
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 用户操作请求DTO
     */
    public static class UserOperationRequest {
        private String operation;
        private SysUser data;
        private List<Long> ids;
        private List<Long> roleIds;

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public SysUser getData() {
            return data;
        }

        public void setData(SysUser data) {
            this.data = data;
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        public List<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
    }
}