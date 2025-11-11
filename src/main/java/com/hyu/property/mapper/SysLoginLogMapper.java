package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.SysLoginLog;
import com.hyu.property.domain.vo.SysLoginLogVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志Mapper接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 查询登录日志列表（关联查询）
     *
     * @param sysLoginLog 登录日志
     * @return 登录日志集合
     */
    List<SysLoginLogVO> selectLoginLogList(SysLoginLog sysLoginLog);

    /**
     * 查询登录日志详情（关联查询）
     *
     * @param id 登录日志主键
     * @return 登录日志
     */
    SysLoginLogVO selectLoginLogById(@Param("id") Long id);

    /**
     * 根据用户ID查询登录日志列表
     *
     * @param userId 用户ID
     * @return 登录日志集合
     */
    List<SysLoginLogVO> selectLoginLogsByUserId(@Param("userId") Long userId);

    /**
     * 根据IP地址查询登录日志列表
     *
     * @param ipaddr IP地址
     * @return 登录日志集合
     */
    List<SysLoginLogVO> selectLoginLogsByIpaddr(@Param("ipaddr") String ipaddr);

    /**
     * 查询失败登录日志列表
     *
     * @return 失败登录日志集合
     */
    List<SysLoginLogVO> selectFailedLoginLogs();

    /**
     * 查询异常登录日志列表
     *
     * @return 异常登录日志集合
     */
    List<SysLoginLogVO> selectAbnormalLoginLogs();

    /**
     * 查询同一IP近24小时登录频率
     *
     * @param ipaddr IP地址
     * @param loginTime 登录时间
     * @return 登录次数
     */
    Integer countLoginFrequencyByIp(@Param("ipaddr") String ipaddr, @Param("loginTime") LocalDateTime loginTime);

    /**
     * 检查是否为新设备登录
     *
     * @param userId 用户ID
     * @param ipaddr IP地址
     * @param browser 浏览器
     * @param os 操作系统
     * @return 是否为新设备
     */
    Boolean checkIsNewDevice(@Param("userId") Long userId, @Param("ipaddr") String ipaddr,
                             @Param("browser") String browser, @Param("os") String os);

    /**
     * 统计各用户登录次数
     *
     * @return 统计结果
     */
    List<SysLoginLogVO> selectUserLoginStatistics();

    /**
     * 统计各时间段登录次数
     *
     * @return 统计结果
     */
    List<SysLoginLogVO> selectTimeSlotStatistics();

    /**
     * 统计登录成功率
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录统计信息
     */
    SysLoginLogVO selectLoginRateStatistics(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 删除指定时间之前的登录日志
     *
     * @param beforeTime 时间点
     * @return 删除数量
     */
    int deleteLoginLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询指定时间范围内的登录日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录日志集合
     */
    List<SysLoginLogVO> selectLoginLogsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日登录统计
     *
     * @return 统计结果
     */
    SysLoginLogVO selectTodayLoginStatistics();

    /**
     * 查询高风险登录日志
     *
     * @return 高风险登录日志集合
     */
    List<SysLoginLogVO> selectHighRiskLoginLogs();

    /**
     * 查询用户最近登录记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 登录日志集合
     */
    List<SysLoginLogVO> selectRecentLoginLogsByUser(@Param("userId") Long userId, @Param("limit") Integer limit);
}