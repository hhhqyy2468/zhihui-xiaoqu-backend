package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.SysLoginLog;
import com.hyu.property.domain.vo.SysLoginLogVO;
import com.hyu.property.service.ISysLoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 登录日志Controller
 *
 * @author system
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/api/v1/system/log/login")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final ISysLoginLogService sysLoginLogService;

    /**
     * 分页查询登录日志
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String beginTime,
                          @RequestParam(required = false) String endTime) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUsername(username);
        sysLoginLog.setStatus(status);

        List<SysLoginLogVO> list = sysLoginLogService.selectLoginLogList(sysLoginLog, pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 获取登录日志详细信息
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(sysLoginLogService.selectSysLoginLogById(id));
    }

    /**
     * 删除登录日志
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sysLoginLogService.deleteSysLoginLogByIds(ids));
    }

    /**
     * 获取登录日志统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics() {
        Map<String, Object> stats = sysLoginLogService.getLoginLogStatistics();
        return AjaxResult.success(stats);
    }

    /**
     * 获取今日登录统计
     */
    @GetMapping("/today")
    public AjaxResult getTodayStatistics() {
        Map<String, Object> stats = sysLoginLogService.getTodayLoginStatistics();
        return AjaxResult.success(stats);
    }

    /**
     * 清理登录日志
     */
    @DeleteMapping("/clean")
    public AjaxResult clean(@RequestParam String beforeTime) {
        LocalDateTime cleanTime = LocalDateTime.parse(beforeTime);
        int count = sysLoginLogService.cleanLoginLog(cleanTime);
        return AjaxResult.success("清理完成，共删除" + count + "条记录");
    }

    /**
     * 查询指定用户的登录日志
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getUserLoginLogs(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysLoginLogVO> list = sysLoginLogService.selectLoginLogByUserId(userId, pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 查询失败登录日志
     */
    @GetMapping("/failed")
    public AjaxResult getFailedLoginLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysLoginLogVO> list = sysLoginLogService.selectFailedLoginLog(pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 查询异常登录日志
     */
    @GetMapping("/abnormal")
    public AjaxResult getAbnormalLoginLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysLoginLogVO> list = sysLoginLogService.selectAbnormalLoginLog(pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 记录登录日志（系统内部使用）
     */
    @PostMapping("/record")
    public AjaxResult recordLoginLog(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        Long userId = params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : null;
        Integer status = (Integer) params.get("status");
        String message = (String) params.get("message");
        String ipaddr = (String) params.get("ipaddr");
        String browser = (String) params.get("browser");
        String os = (String) params.get("os");

        sysLoginLogService.recordLoginLog(username, userId, status, message, ipaddr, browser, os);

        return AjaxResult.success();
    }

    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}