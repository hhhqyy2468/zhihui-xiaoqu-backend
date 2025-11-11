package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.Notice;
import com.hyu.property.domain.NoticeRead;
import com.hyu.property.domain.dto.NoticeDTO;
import com.hyu.property.domain.vo.NoticeVO;
import com.hyu.property.mapper.NoticeMapper;
import com.hyu.property.mapper.NoticeReadMapper;
import com.hyu.property.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeReadMapper noticeReadMapper;

    /**
     * 查询公告
     */
    @Override
    public NoticeVO selectNoticeById(Long id) {
        return noticeMapper.selectNoticeById(id);
    }

    /**
     * 查询公告列表
     */
    @Override
    public List<NoticeVO> selectNoticeList(Notice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        return noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        return noticeMapper.updateById(notice);
    }

    /**
     * 批量删除公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeByIds(List<Long> ids) {
        return noticeMapper.deleteBatchIds(ids);
    }

    /**
     * 删除公告信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeById(Long id) {
        return noticeMapper.deleteById(id);
    }

    /**
     * 发布公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishNotice(NoticeDTO noticeDTO) {
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserName = SecurityUtils.getUsername();

        // 检查置顶公告数量
        if (noticeDTO.getIsTop() != null && noticeDTO.getIsTop() == 1) {
            Integer topCount = noticeMapper.countTopNotices();
            if (topCount >= 5) {
                throw new RuntimeException("置顶公告最多5条，请先取消其他公告的置顶");
            }
        }

        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        notice.setPublisherId(currentUserId);
        notice.setPublisherName(currentUserName);
        notice.setNoticeStatus(1); // 已发布
        notice.setPublishTime(LocalDateTime.now());
        notice.setReadCount(0);
        notice.setDeleted(0);

        return noticeMapper.insert(notice);
    }

    /**
     * 撤回公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int withdrawNotice(Long id) {
        return noticeMapper.updateNoticeStatus(id, 3); // 已撤回
    }

    /**
     * 设置公告置顶
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTopNotice(Long id, Integer isTop) {
        if (isTop == 1) {
            Integer topCount = noticeMapper.countTopNotices();
            if (topCount >= 5) {
                throw new RuntimeException("置顶公告最多5条，请先取消其他公告的置顶");
            }
        }
        return noticeMapper.updateTopStatus(id, isTop);
    }

    /**
     * 用户查看公告详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeVO viewNoticeDetail(Long id, Long userId) {
        NoticeVO noticeVO = noticeMapper.selectNoticeById(id);
        if (noticeVO == null) {
            throw new RuntimeException("公告不存在");
        }

        // 检查是否已读
        NoticeRead existRead = noticeReadMapper.selectUserNoticeRead(id, userId);
        if (existRead == null) {
            // 记录阅读
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setNoticeId(id);
            noticeRead.setUserId(userId);
            noticeRead.setReadTime(LocalDateTime.now());
            noticeReadMapper.insert(noticeRead);

            // 增加阅读次数
            noticeMapper.incrementReadCount(id);
        }

        noticeVO.setIsRead(true);
        return noticeVO;
    }

    /**
     * 用户标记公告已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markNoticeAsRead(Long id, Long userId) {
        NoticeRead existRead = noticeReadMapper.selectUserNoticeRead(id, userId);
        if (existRead != null) {
            return 0; // 已经阅读过了
        }

        NoticeRead noticeRead = new NoticeRead();
        noticeRead.setNoticeId(id);
        noticeRead.setUserId(userId);
        noticeRead.setReadTime(LocalDateTime.now());

        int result = noticeReadMapper.insert(noticeRead);
        if (result > 0) {
            noticeMapper.incrementReadCount(id);
        }

        return result;
    }

    /**
     * 查询用户可见的公告列表
     */
    @Override
    public List<NoticeVO> selectNoticeListForUser(Long userId, Long buildingId, Long unitId) {
        return noticeMapper.selectNoticeListForUser(userId, buildingId, unitId);
    }

    /**
     * 查询置顶公告列表
     */
    @Override
    public List<NoticeVO> selectTopNoticeList() {
        return noticeMapper.selectTopNoticeList();
    }

    /**
     * 获取公告阅读统计
     */
    @Override
    public Map<String, Object> getNoticeReadStatistics(Long id) {
        Map<String, Object> stats = new HashMap<>();

        NoticeVO notice = noticeMapper.selectNoticeById(id);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }

        // 计算应读人数
        Integer targetUserCount = calculateTargetUserCount(notice);

        // 已读人数
        Integer readUserCount = noticeReadMapper.countReadUsersByNoticeId(id);

        // 阅读率
        Double readRate = targetUserCount > 0 ? (double) readUserCount / targetUserCount * 100 : 0.0;

        stats.put("targetUserCount", targetUserCount);
        stats.put("readUserCount", readUserCount);
        stats.put("readRate", readRate);
        stats.put("unreadCount", targetUserCount - readUserCount);

        return stats;
    }

    /**
     * 获取公告的未读用户列表
     */
    @Override
    public List<Map<String, Object>> getUnreadUsers(Long id) {
        return noticeReadMapper.selectUnreadUsersByNoticeId(id)
                .stream()
                .map(read -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", read.getUserId());
                    map.put("userName", read.getUserName());
                    map.put("userPhone", read.getUserPhone());
                    map.put("houseNo", read.getHouseNo());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 批量标记公告已过期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateExpiredNotices() {
        List<Notice> expiredNotices = noticeMapper.selectExpiredNotices();
        if (expiredNotices.isEmpty()) {
            return 0;
        }

        List<Long> ids = expiredNotices.stream()
                .map(Notice::getId)
                .collect(java.util.stream.Collectors.toList());

        return noticeMapper.batchUpdateToExpired(ids);
    }

    /**
     * 获取公告统计数据
     */
    @Override
    public Map<String, Object> getNoticeStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCount", noticeMapper.selectNoticeList(new Notice()).size());
        stats.put("publishedCount", countNoticeByStatus(1));
        stats.put("expiredCount", countNoticeByStatus(2));
        stats.put("withdrawnCount", countNoticeByStatus(3));
        stats.put("topCount", noticeMapper.countTopNotices());

        return stats;
    }

    /**
     * 获取公告类型统计
     */
    @Override
    public List<NoticeVO> getNoticeTypeStats() {
        return noticeMapper.selectNoticeTypeStats();
    }

    /**
     * 获取阅读率最高的公告
     */
    @Override
    public List<NoticeVO> getTopReadNotices(Integer limit) {
        return noticeMapper.selectTopReadNotices(limit);
    }

    /**
     * 定时任务：检查并更新过期公告
     */
    @Override
    public void checkExpiredNotices() {
        log.info("开始检查过期公告");
        batchUpdateExpiredNotices();
        log.info("过期公告检查完成");
    }

    /**
     * 判断用户是否有权限查看公告
     */
    private boolean canUserViewNotice(Long userId, NoticeVO notice) {
        if (notice.getPublishScope() == 1) {
            return true; // 全部用户
        }
        // TODO: 根据用户的楼栋、单元信息判断权限
        return true;
    }

    /**
     * 计算应读人数
     */
    private Integer calculateTargetUserCount(NoticeVO notice) {
        // TODO: 根据发布范围计算应读人数
        return 100; // 临时返回，实际需要计算
    }

    /**
     * 统计指定状态的公告数量
     */
    private Integer countNoticeByStatus(Integer status) {
        Notice notice = new Notice();
        notice.setNoticeStatus(status);
        return noticeMapper.selectNoticeList(notice).size();
    }
}