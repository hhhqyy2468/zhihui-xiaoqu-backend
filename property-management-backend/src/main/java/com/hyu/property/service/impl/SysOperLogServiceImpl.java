package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.property.domain.SysOperLog;
import com.hyu.property.domain.vo.SysOperLogVO;
import com.hyu.property.mapper.SysOperLogMapper;
import com.hyu.property.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    /**
     * 查询操作日志
     */
    @Override
    public SysOperLogVO selectSysOperLogById(Long id) {
        return sysOperLogMapper.selectOperLogById(id);
    }

    /**
     * 分页查询操作日志列表
     */
    @Override
    public List<SysOperLogVO> selectOperLogList(SysOperLog sysOperLog, Integer pageNum, Integer pageSize) {
        // 这里可以添加分页逻辑
        return sysOperLogMapper.selectOperLogList(sysOperLog);
    }

    /**
     * 删除操作日志
     */
    @Override
    public int deleteSysOperLogById(Long id) {
        return sysOperLogMapper.deleteById(id);
    }

    /**
     * 批量删除操作日志
     */
    @Override
    public int deleteSysOperLogByIds(Long[] ids) {
        return sysOperLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 新增操作日志
     */
    @Override
    public int insertSysOperLog(SysOperLog sysOperLog) {
        return sysOperLogMapper.insert(sysOperLog);
    }

    /**
     * 记录操作日志（异步）
     */
    @Override
    @Async
    public void recordOperLog(String title, Integer businessType, String method, String requestMethod,
                            Integer operatorType, String operName, Long operUserId, String operUrl,
                            String operIp, String operLocation, String operParam, String jsonResult,
                            Integer status, String errorMsg) {
        try {
            SysOperLog operLog = new SysOperLog();
            operLog.setTitle(title);
            operLog.setBusinessType(businessType);
            operLog.setMethod(method);
            operLog.setRequestMethod(requestMethod);
            operLog.setOperatorType(operatorType);
            operLog.setOperName(operName);
            operLog.setOperUserId(operUserId);
            operLog.setOperUrl(operUrl);
            operLog.setOperIp(operIp);
            operLog.setOperLocation(operLocation);
            operLog.setOperParam(operParam);
            operLog.setJsonResult(jsonResult);
            operLog.setStatus(status);
            operLog.setErrorMsg(errorMsg);
            operLog.setOperTime(LocalDateTime.now());

            sysOperLogMapper.insert(operLog);
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
        }
    }

    /**
     * 查询操作日志统计信息
     */
    @Override
    public Map<String, Object> getOperLogStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 今日统计
        SysOperLogVO todayStats = sysOperLogMapper.selectTodayStatistics();
        if (todayStats != null) {
            stats.put("todayCount", todayStats.getId());
            stats.put("successCount", todayStats.getBusinessType());
            stats.put("failCount", todayStats.getOperatorType());
        }

        // 业务类型统计
        List<SysOperLogVO> businessTypeStats = sysOperLogMapper.selectBusinessTypeStatistics();
        stats.put("businessTypeStats", businessTypeStats);

        // 用户操作统计
        List<SysOperLogVO> userStats = sysOperLogMapper.selectUserOperationStatistics();
        stats.put("userStats", userStats);

        return stats;
    }

    /**
     * 清理操作日志
     */
    @Override
    public int cleanOperLog(LocalDateTime beforeTime) {
        log.info("开始清理{}之前的操作日志", beforeTime);
        int count = sysOperLogMapper.deleteOperLogsBefore(beforeTime);
        log.info("清理操作日志完成，共删除{}条记录", count);
        return count;
    }

    /**
     * 根据用户查询操作日志
     */
    @Override
    public List<SysOperLogVO> selectOperLogByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return sysOperLogMapper.selectRecentOperLogsByUser(userId, pageSize);
    }

    /**
     * 查询异常操作日志
     */
    @Override
    public List<SysOperLogVO> selectAbnormalOperLog(Integer pageNum, Integer pageSize) {
        return sysOperLogMapper.selectAbnormalOperLogs();
    }
}