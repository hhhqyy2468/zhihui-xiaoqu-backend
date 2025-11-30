package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Notice;

import java.util.List;
import java.util.Map;

/**
 * 公告Service接口
 *
 * @author hyu
 */
public interface INoticeService extends IService<Notice> {

    /**
     * 分页查询公告列表
     *
     * @param page 分页参数
     * @param notice 公告信息
     * @return 公告分页数据
     */
    Page<Notice> selectNoticePage(Page<Notice> page, Notice notice);

    /**
     * 发布公告
     *
     * @param id 公告ID
     * @return 结果
     */
    boolean publishNotice(Long id);

    /**
     * 更新置顶状态
     *
     * @param id 公告ID
     * @param isTop 是否置顶
     * @return 结果
     */
    boolean updateTopStatus(Long id, Integer isTop);

    /**
     * 撤回公告
     *
     * @param id 公告ID
     * @return 结果
     */
    boolean withdrawNotice(Long id);

    /**
     * 获取用户可见的公告列表
     *
     * @param buildingId 楼栋ID
     * @param unitId 单元ID
     * @return 公告列表
     */
    List<Notice> getUserNotices(Long buildingId, Long unitId);

    /**
     * 标记公告已读
     *
     * @param id 公告ID
     * @return 结果
     */
    boolean markAsRead(Long id);

    /**
     * 获取公告统计信息
     *
     * @param id 公告ID
     * @return 统计信息
     */
    Map<String, Object> getNoticeStats(Long id);

    /**
     * 获取公告概览统计
     *
     * @return 概览统计信息
     */
    Map<String, Object> getOverviewStats();
}