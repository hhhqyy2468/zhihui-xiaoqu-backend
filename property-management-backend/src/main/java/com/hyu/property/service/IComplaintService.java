package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Complaint;

import java.util.List;
import java.util.Map;

/**
 * 投诉Service接口
 *
 * @author system
 * @date 2025-11-20
 */
public interface IComplaintService extends IService<Complaint> {

    /**
     * 分页查询投诉列表
     *
     * @param page 分页参数
     * @param complaint 投诉信息
     * @return 投诉分页数据
     */
    Page<Complaint> selectComplaintPage(Page<Complaint> page, Complaint complaint);

    /**
     * 校验投诉单号是否唯一
     *
     * @param complaint 投诉信息
     * @return 结果 true唯一 false不唯一
     */
    boolean checkComplaintNoUnique(Complaint complaint);

    /**
     * 查询投诉详情
     *
     * @param id 投诉主键
     * @return 投诉
     */
    Complaint selectComplaintById(Long id);

    /**
     * 根据用户ID查询投诉列表
     *
     * @param userId 用户ID
     * @return 投诉集合
     */
    List<Complaint> selectComplaintsByUserId(Long userId);

    /**
     * 新增投诉
     *
     * @param complaint 投诉信息
     * @return 结果
     */
    int insertComplaint(Complaint complaint);

    /**
     * 修改投诉
     *
     * @param complaint 投诉信息
     * @return 结果
     */
    int updateComplaint(Complaint complaint);

    /**
     * 批量删除投诉
     *
     * @param ids 需要删除的投诉主键集合
     * @return 结果
     */
    int deleteComplaintByIds(Long[] ids);

    /**
     * 删除投诉信息
     *
     * @param id 投诉主键
     * @return 结果
     */
    int deleteComplaintById(Long id);

    /**
     * 处理投诉
     *
     * @param id 投诉ID
     * @param params 处理参数
     * @return 结果
     */
    int handleComplaint(Long id, Map<String, Object> params);

    /**
     * 评价投诉
     *
     * @param id 投诉ID
     * @param params 评价参数
     * @return 结果
     */
    int rateComplaint(Long id, Map<String, Object> params);

    /**
     * 关闭投诉
     *
     * @param id 投诉ID
     * @return 结果
     */
    int closeComplaint(Long id);

    /**
     * 派单处理
     *
     * @param id 投诉ID
     * @param handlerId 处理人ID
     * @param remark 备注信息
     * @return 结果
     */
    int assignComplaint(Long id, Long handlerId, String remark);

    /**
     * 生成投诉单号
     *
     * @return 投诉单号
     */
    String generateComplaintNo();

    /**
     * 根据处理人ID查询投诉列表
     *
     * @param handlerId 处理人ID
     * @return 投诉集合
     */
    List<Complaint> selectComplaintsByHandlerId(Long handlerId);

    /**
     * 统计投诉数量
     *
     * @param complaint 投诉信息
     * @return 统计结果
     */
    Map<String, Object> countComplaintStats(Complaint complaint);

    /**
     * 获取可用的处理人列表（物业管理员）
     *
     * @return 处理人列表
     */
    List<Map<String, Object>> getAvailableHandlers();
}