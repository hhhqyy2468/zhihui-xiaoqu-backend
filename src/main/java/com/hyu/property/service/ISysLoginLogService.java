package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.SysLoginLog;
import com.hyu.property.domain.vo.SysLoginLogVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface ISysLoginLogService extends IService<SysLoginLog> {

    /**
     * 查询登录日志
     *
     * @param id 登录日志主键
     * @return 登录日志
     */
    public SysLoginLogVO selectSysLoginLogById(Long id);

    /**
     * 分页查询登录日志列表
     *
     * @param sysLoginLog 登录日志
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 登录日志集合
     */
    public List<SysLoginLogVO> selectLoginLogList(SysLoginLog sysLoginLog, Integer pageNum, Integer pageSize);

    /**
     * 删除登录日志
     *
     * @param id 登录日志主键
     * @return 结果
     */
    public int deleteSysLoginLogById(Long id);

    /**
     * 批量删除登录日志
     *
     * @param ids 需要删除的登录日志主键
     * @return 结果
     */
    public int deleteSysLoginLogByIds(Long[] ids);

    /**
     * 新增登录日志
     *
     * @param sysLoginLog 登录日志
     * @return 结果
     */
    public int insertSysLoginLog(SysLoginLog sysLoginLog);

    /**
     * 记录登录日志（异步）
     *
     * @param username 用户账号
     * @param userId 用户ID
     * @param status 登录状态（0成功 1失败）
     * @param message 提示消息
     * @param ipaddr 登录IP地址
     * @param browser 浏览器类型
     * @param os 操作系统
     */
    public void recordLoginLog(String username, Long userId, Integer status, String message,
                             String ipaddr, String browser, String os);

    /**
     * 查询登录日志统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getLoginLogStatistics();

    /**
     * 查询今日登录统计
     *
     * @return 今日统计信息
     */
    public Map<String, Object> getTodayLoginStatistics();

    /**
     * 清理登录日志
     *
     * @param beforeTime 保留时间点
     * @return 清理数量
     */
    public int cleanLoginLog(LocalDateTime beforeTime);

    /**
     * 根据用户查询登录日志
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 登录日志集合
     */
    public List<SysLoginLogVO> selectLoginLogByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询失败登录日志
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 失败登录日志集合
     */
    public List<SysLoginLogVO> selectFailedLoginLog(Integer pageNum, Integer pageSize);

    /**
     * 查询异常登录日志
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 异常登录日志集合
     */
    public List<SysLoginLogVO> selectAbnormalLoginLog(Integer pageNum, Integer pageSize);

    /**
     * 查询用户最近登录记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 登录日志集合
     */
    public List<SysLoginLogVO> selectRecentLoginLogs(Long userId, Integer limit);
}