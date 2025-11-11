package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.SysOperLog;
import com.hyu.property.domain.vo.SysOperLogVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 查询操作日志
     *
     * @param id 操作日志主键
     * @return 操作日志
     */
    public SysOperLogVO selectSysOperLogById(Long id);

    /**
     * 分页查询操作日志列表
     *
     * @param sysOperLog 操作日志
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 操作日志集合
     */
    public List<SysOperLogVO> selectOperLogList(SysOperLog sysOperLog, Integer pageNum, Integer pageSize);

    /**
     * 删除操作日志
     *
     * @param id 操作日志主键
     * @return 结果
     */
    public int deleteSysOperLogById(Long id);

    /**
     * 批量删除操作日志
     *
     * @param ids 需要删除的操作日志主键
     * @return 结果
     */
    public int deleteSysOperLogByIds(Long[] ids);

    /**
     * 新增操作日志
     *
     * @param sysOperLog 操作日志
     * @return 结果
     */
    public int insertSysOperLog(SysOperLog sysOperLog);

    /**
     * 记录操作日志（异步）
     *
     * @param title 模块标题
     * @param businessType 业务类型（0其它 1新增 2修改 3删除 4查询）
     * @param method 方法名称
     * @param requestMethod 请求方式
     * @param operatorType 操作类别（0其它 1后台用户 2手机端用户）
     * @param operName 操作人员
     * @param operUserId 操作人员ID
     * @param operUrl 请求URL
     * @param operIp 操作地址
     * @param operLocation 操作地点
     * @param operParam 请求参数
     * @param jsonResult 返回参数
     * @param status 操作状态
     * @param errorMsg 错误消息
     */
    public void recordOperLog(String title, Integer businessType, String method, String requestMethod,
                            Integer operatorType, String operName, Long operUserId, String operUrl,
                            String operIp, String operLocation, String operParam, String jsonResult,
                            Integer status, String errorMsg);

    /**
     * 查询操作日志统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getOperLogStatistics();

    /**
     * 清理操作日志
     *
     * @param beforeTime 保留时间点
     * @return 清理数量
     */
    public int cleanOperLog(LocalDateTime beforeTime);

    /**
     * 根据用户查询操作日志
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 操作日志集合
     */
    public List<SysOperLogVO> selectOperLogByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询异常操作日志
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 异常操作日志集合
     */
    public List<SysOperLogVO> selectAbnormalOperLog(Integer pageNum, Integer pageSize);
}