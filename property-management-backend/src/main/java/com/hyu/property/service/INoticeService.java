package com.hyu.property.service;

import com.hyu.property.domain.Notice;
import com.hyu.property.domain.dto.NoticeDTO;
import com.hyu.property.domain.vo.NoticeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 公告Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface INoticeService extends IService<Notice> {

    /**
     * 查询公告
     *
     * @param id 公告主键
     * @return 公告
     */
    public NoticeVO selectNoticeById(Long id);

    /**
     * 查询公告列表
     *
     * @param notice 公告
     * @return 公告集合
     */
    public List<NoticeVO> selectNoticeList(Notice notice);

    /**
     * 新增公告
     *
     * @param noticeDTO 公告
     * @return 结果
     */
    public int insertNotice(NoticeDTO noticeDTO);

    /**
     * 修改公告
     *
     * @param noticeDTO 公告
     * @return 结果
     */
    public int updateNotice(NoticeDTO noticeDTO);

    /**
     * 批量删除公告
     *
     * @param ids 需要删除的公告主键集合
     * @return 结果
     */
    public int deleteNoticeByIds(List<Long> ids);

    /**
     * 删除公告信息
     *
     * @param id 公告主键
     * @return 结果
     */
    public int deleteNoticeById(Long id);

    // ========== 业务方法 ==========

    /**
     * 发布公告
     *
     * @param noticeDTO 公告信息
     * @return 结果
     */
    public int publishNotice(NoticeDTO noticeDTO);

    /**
     * 撤回公告
     *
     * @param id 公告ID
     * @return 结果
     */
    public int withdrawNotice(Long id);

    /**
     * 设置公告置顶
     *
     * @param id 公告ID
     * @param isTop 是否置顶
     * @return 结果
     */
    public int setTopNotice(Long id, Integer isTop);

    /**
     * 用户查看公告详情（自动记录阅读）
     *
     * @param id 公告ID
     * @param userId 用户ID
     * @return 公告详情
     */
    public NoticeVO viewNoticeDetail(Long id, Long userId);

    /**
     * 用户标记公告已读
     *
     * @param id 公告ID
     * @param userId 用户ID
     * @return 结果
     */
    public int markNoticeAsRead(Long id, Long userId);

    /**
     * 查询用户可见的公告列表
     *
     * @param userId 用户ID
     * @param buildingId 楼栋ID
     * @param unitId 单元ID
     * @return 公告集合
     */
    public List<NoticeVO> selectNoticeListForUser(Long userId, Long buildingId, Long unitId);

    /**
     * 查询置顶公告列表
     *
     * @return 置顶公告集合
     */
    public List<NoticeVO> selectTopNoticeList();

    /**
     * 获取公告阅读统计
     *
     * @param id 公告ID
     * @return 阅读统计信息
     */
    public Map<String, Object> getNoticeReadStatistics(Long id);

    /**
     * 获取公告的未读用户列表
     *
     * @param id 公告ID
     * @return 未读用户列表
     */
    public List<Map<String, Object>> getUnreadUsers(Long id);

    /**
     * 批量标记公告已过期
     *
     * @return 处理结果
     */
    public int batchUpdateExpiredNotices();

    /**
     * 获取公告统计数据
     *
     * @return 统计数据
     */
    public Map<String, Object> getNoticeStatistics();

    /**
     * 获取公告类型统计
     *
     * @return 类型统计
     */
    public List<NoticeVO> getNoticeTypeStats();

    /**
     * 获取阅读率最高的公告
     *
     * @param limit 限制数量
     * @return 公告列表
     */
    public List<NoticeVO> getTopReadNotices(Integer limit);

    /**
     * 定时任务：检查并更新过期公告
     */
    public void checkExpiredNotices();
}