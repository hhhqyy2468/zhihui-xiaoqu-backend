package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.property.domain.SysLoginLog;
import com.hyu.property.domain.vo.SysLoginLogVO;
import com.hyu.property.mapper.SysLoginLogMapper;
import com.hyu.property.service.ISysLoginLogService;
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
 * 登录日志Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;

    /**
     * 查询登录日志
     */
    @Override
    public SysLoginLogVO selectSysLoginLogById(Long id) {
        return sysLoginLogMapper.selectLoginLogById(id);
    }

    /**
     * 分页查询登录日志列表
     */
    @Override
    public List<SysLoginLogVO> selectLoginLogList(SysLoginLog sysLoginLog, Integer pageNum, Integer pageSize) {
        // 这里可以添加分页逻辑
        return sysLoginLogMapper.selectLoginLogList(sysLoginLog);
    }

    /**
     * 删除登录日志
     */
    @Override
    public int deleteSysLoginLogById(Long id) {
        return sysLoginLogMapper.deleteById(id);
    }

    /**
     * 批量删除登录日志
     */
    @Override
    public int deleteSysLoginLogByIds(Long[] ids) {
        return sysLoginLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 新增登录日志
     */
    @Override
    public int insertSysLoginLog(SysLoginLog sysLoginLog) {
        return sysLoginLogMapper.insert(sysLoginLog);
    }

    /**
     * 记录登录日志（异步）
     */
    @Override
    @Async
    public void recordLoginLog(String username, Long userId, Integer status, String message,
                             String ipaddr, String browser, String os) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setUserId(userId);
            loginLog.setStatus(status);
            loginLog.setMsg(message);
            loginLog.setIpaddr(ipaddr);
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            loginLog.setLoginTime(LocalDateTime.now());

            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志异常", e);
        }
    }

    /**
     * 查询登录日志统计信息
     */
    @Override
    public Map<String, Object> getLoginLogStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 总登录次数
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        SysLoginLogVO weekStats = sysLoginLogMapper.selectLoginRateStatistics(weekAgo, now);
        if (weekStats != null) {
            stats.put("weekTotalCount", weekStats.getId());
            stats.put("weekSuccessCount", weekStats.getStatus());
            stats.put("weekFailCount", weekStats.getUserId());
        }

        // 用户登录统计
        List<SysLoginLogVO> userStats = sysLoginLogMapper.selectUserLoginStatistics();
        stats.put("userStats", userStats);

        // 时间段统计
        List<SysLoginLogVO> timeSlotStats = sysLoginLogMapper.selectTimeSlotStatistics();
        stats.put("timeSlotStats", timeSlotStats);

        return stats;
    }

    /**
     * 查询今日登录统计
     */
    @Override
    public Map<String, Object> getTodayLoginStatistics() {
        Map<String, Object> stats = new HashMap<>();

        SysLoginLogVO todayStats = sysLoginLogMapper.selectTodayLoginStatistics();
        if (todayStats != null) {
            stats.put("totalCount", todayStats.getId());
            stats.put("successCount", todayStats.getStatus());
            stats.put("failCount", todayStats.getUserId());
            stats.put("uniqueUsers", todayStats.getUserType());
        }

        return stats;
    }

    /**
     * 清理登录日志
     */
    @Override
    public int cleanLoginLog(LocalDateTime beforeTime) {
        log.info("开始清理{}之前的登录日志", beforeTime);
        int count = sysLoginLogMapper.deleteLoginLogsBefore(beforeTime);
        log.info("清理登录日志完成，共删除{}条记录", count);
        return count;
    }

    /**
     * 根据用户查询登录日志
     */
    @Override
    public List<SysLoginLogVO> selectLoginLogByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return sysLoginLogMapper.selectRecentLoginLogsByUser(userId, pageSize);
    }

    /**
     * 查询失败登录日志
     */
    @Override
    public List<SysLoginLogVO> selectFailedLoginLog(Integer pageNum, Integer pageSize) {
        return sysLoginLogMapper.selectFailedLoginLogs();
    }

    /**
     * 查询异常登录日志
     */
    @Override
    public List<SysLoginLogVO> selectAbnormalLoginLog(Integer pageNum, Integer pageSize) {
        return sysLoginLogMapper.selectAbnormalLoginLogs();
    }

    /**
     * 查询用户最近登录记录
     */
    @Override
    public List<SysLoginLogVO> selectRecentLoginLogs(Long userId, Integer limit) {
        return sysLoginLogMapper.selectRecentLoginLogsByUser(userId, limit);
    }
}