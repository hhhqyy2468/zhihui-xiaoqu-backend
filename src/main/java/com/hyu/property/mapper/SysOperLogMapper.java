package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.SysOperLog;
import com.hyu.property.domain.vo.SysOperLogVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Mapper接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 查询操作日志列表（关联查询）
     *
     * @param sysOperLog 操作日志
     * @return 操作日志集合
     */
    List<SysOperLogVO> selectOperLogList(SysOperLog sysOperLog);

    /**
     * 查询操作日志详情（关联查询）
     *
     * @param id 操作日志主键
     * @return 操作日志
     */
    SysOperLogVO selectOperLogById(@Param("id") Long id);

    /**
     * 根据用户ID查询操作日志列表
     *
     * @param userId 用户ID
     * @return 操作日志集合
     */
    List<SysOperLogVO> selectOperLogsByUserId(@Param("userId") Long userId);

    /**
     * 根据业务类型查询操作日志列表
     *
     * @param businessType 业务类型
     * @return 操作日志集合
     */
    List<SysOperLogVO> selectOperLogsByBusinessType(@Param("businessType") Integer businessType);

    /**
     * 查询异常操作日志列表
     *
     * @return 异常操作日志集合
     */
    List<SysOperLogVO> selectAbnormalOperLogs();

    /**
     * 查询高频操作日志列表
     *
     * @param limit 限制数量
     * @return 高频操作日志集合
     */
    List<SysOperLogVO> selectHighFrequencyOperLogs(@Param("limit") Integer limit);

    /**
     * 统计各业务类型操作数量
     *
     * @return 统计结果
     */
    List<SysOperLogVO> selectBusinessTypeStatistics();

    /**
     * 统计各用户操作数量
     *
     * @return 统计结果
     */
    List<SysOperLogVO> selectUserOperationStatistics();

    /**
     * 删除指定时间之前的操作日志
     *
     * @param beforeTime 时间点
     * @return 删除数量
     */
    int deleteOperLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询指定时间范围内的操作日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志集合
     */
    List<SysOperLogVO> selectOperLogsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日操作统计
     *
     * @return 统计结果
     */
    SysOperLogVO selectTodayStatistics();

    /**
     * 查询最近登录用户的操作日志
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 操作日志集合
     */
    List<SysOperLogVO> selectRecentOperLogsByUser(@Param("userId") Long userId, @Param("limit") Integer limit);
}