package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.SysOperLog;
import com.hyu.property.domain.vo.SysOperLogVO;
import com.hyu.property.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Controller
 *
 * @author system
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/api/v1/system/log/operation")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService sysOperLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String operation,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String beginTime,
                          @RequestParam(required = false) String endTime) {
        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setOperName(username);
        sysOperLog.setStatus(status);

        List<SysOperLogVO> list = sysOperLogService.selectOperLogList(sysOperLog, pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 获取操作日志详细信息
     */
    @GetMapping("/{operId}")
    public AjaxResult getInfo(@PathVariable Long operId) {
        return AjaxResult.success(sysOperLogService.selectSysOperLogById(operId));
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds) {
        return toAjax(sysOperLogService.deleteSysOperLogByIds(operIds));
    }

    /**
     * 获取操作日志统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics() {
        Map<String, Object> stats = sysOperLogService.getOperLogStatistics();
        return AjaxResult.success(stats);
    }

    /**
     * 清理操作日志
     */
    @DeleteMapping("/clean")
    public AjaxResult clean(@RequestParam String beforeTime) {
        LocalDateTime cleanTime = LocalDateTime.parse(beforeTime);
        int count = sysOperLogService.cleanOperLog(cleanTime);
        return AjaxResult.success("清理完成，共删除" + count + "条记录");
    }

    /**
     * 查询指定用户的操作日志
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getUserOperLogs(@PathVariable Long userId,
                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysOperLogVO> list = sysOperLogService.selectOperLogByUserId(userId, pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 查询异常操作日志
     */
    @GetMapping("/abnormal")
    public AjaxResult getAbnormalOperLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysOperLogVO> list = sysOperLogService.selectAbnormalOperLog(pageNum, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 记录操作日志（系统内部使用）
     */
    @PostMapping("/record")
    public AjaxResult recordOperLog(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        Integer businessType = (Integer) params.get("businessType");
        String method = (String) params.get("method");
        String requestMethod = (String) params.get("requestMethod");
        Integer operatorType = (Integer) params.get("operatorType");
        String operName = (String) params.get("operName");
        Long operUserId = params.get("operUserId") != null ? Long.valueOf(params.get("operUserId").toString()) : null;
        String operUrl = (String) params.get("operUrl");
        String operIp = (String) params.get("operIp");
        String operLocation = (String) params.get("operLocation");
        String operParam = (String) params.get("operParam");
        String jsonResult = (String) params.get("jsonResult");
        Integer status = (Integer) params.get("status");
        String errorMsg = (String) params.get("errorMsg");

        sysOperLogService.recordOperLog(title, businessType, method, requestMethod, operatorType,
                operName, operUserId, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg);

        return AjaxResult.success();
    }

    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}