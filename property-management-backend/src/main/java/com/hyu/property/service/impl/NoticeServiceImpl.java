package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Notice;
import com.hyu.property.domain.NoticeRead;
import com.hyu.property.mapper.NoticeMapper;
import com.hyu.property.mapper.NoticeReadMapper;
import com.hyu.property.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 公告Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Autowired
    private NoticeReadMapper noticeReadMapper;

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 是否成功
     */
    @Override
    public boolean save(Notice notice) {
        try {
            log.info("开始保存公告: {}", notice);

            // 设置默认值
            if (notice.getNoticeStatus() == null) {
                notice.setNoticeStatus(0); // 草稿状态
            }
            if (notice.getIsTop() == null) {
                notice.setIsTop(0); // 不置顶
            }
            if (notice.getReadCount() == null) {
                notice.setReadCount(0); // 阅读次数初始化为0
            }
            if (notice.getDeleted() == null) {
                notice.setDeleted(0); // 未删除
            }

            // 设置创建时间
            notice.setCreateTime(LocalDateTime.now());

            // 如果是发布状态，设置发布时间
            if (notice.getNoticeStatus() == 1) {
                notice.setPublishTime(LocalDateTime.now());
            }

            log.info("保存公告前的数据: {}", notice);
            boolean result = super.save(notice);
            log.info("公告保存结果: {}, 生成ID: {}", result, notice.getId());

            return result;
        } catch (Exception e) {
            log.error("保存公告失败", e);
            return false;
        }
    }

    /**
     * 更新公告
     *
     * @param notice 公告信息
     * @return 是否成功
     */
    @Override
    public boolean updateById(Notice notice) {
        try {
            log.info("开始更新公告: {}", notice);

            // 设置更新时间
            notice.setUpdateTime(LocalDateTime.now());

            // 如果状态改为发布状态，设置发布时间
            Notice existingNotice = getById(notice.getId());
            if (existingNotice != null && existingNotice.getNoticeStatus() != 1 && notice.getNoticeStatus() == 1) {
                notice.setPublishTime(LocalDateTime.now());
            }

            log.info("更新公告前的数据: {}", notice);
            boolean result = super.updateById(notice);
            log.info("公告更新结果: {}", result);

            return result;
        } catch (Exception e) {
            log.error("更新公告失败", e);
            return false;
        }
    }

    /**
     * 分页查询公告列表
     *
     * @param page 分页参数
     * @param notice 公告信息
     * @return 公告分页数据
     */
    @Override
    public Page<Notice> selectNoticePage(Page<Notice> page, Notice notice) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(notice.getNoticeTitle()), "notice_title", notice.getNoticeTitle())
                   .eq(StringUtils.isNotEmpty(notice.getNoticeType()), "notice_type", notice.getNoticeType())
                   .eq(notice.getIsTop() != null, "is_top", notice.getIsTop())
                   .eq(notice.getPublishScope() != null, "publish_scope", notice.getPublishScope())
                   .eq(notice.getNoticeStatus() != null, "notice_status", notice.getNoticeStatus())
                   .eq(notice.getPublisherId() != null, "publisher_id", notice.getPublisherId())
                   .like(StringUtils.isNotEmpty(notice.getPublisherName()), "publisher_name", notice.getPublisherName())
                   .eq("deleted", 0) // 固定查询未删除的记录
                   .orderByDesc("is_top")
                   .orderByDesc("create_time");

        log.info("查询公告列表，查询条件: {}, 分页参数: pageNum={}, pageSize={}",
                queryWrapper.getTargetSql(), page.getCurrent(), page.getSize());

        Page<Notice> result = page(page, queryWrapper);
        log.info("查询结果: 总记录数={}, 当前页记录数={}", result.getTotal(), result.getRecords().size());

        return result;
    }

    @Override
    public boolean publishNotice(Long id) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setNoticeStatus(1); // 已发布
        notice.setPublishTime(LocalDateTime.now());
        return updateById(notice);
    }

    @Override
    public boolean updateTopStatus(Long id, Integer isTop) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setIsTop(isTop);
        return updateById(notice);
    }

    @Override
    public boolean withdrawNotice(Long id) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setNoticeStatus(3); // 已撤回
        return updateById(notice);
    }

    @Override
    public List<Notice> getUserNotices(Long buildingId, Long unitId) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notice_status", 1) // 已发布
                   .eq("deleted", 0)
                   .and(wrapper -> wrapper.eq("publish_scope", 1) // 全部
                           .or(w -> w.eq("publish_scope", 2) // 指定范围
                                   .and(subWrapper -> subWrapper
                                           .apply("find_in_set({0}, target_building_ids)", buildingId)
                                           .or()
                                           .apply("find_in_set({0}, target_unit_ids)", unitId))))
                   .and(wrapper -> wrapper.isNull("effective_start_time")
                           .or(w -> w.le("effective_start_time", LocalDateTime.now())))
                   .and(wrapper -> wrapper.isNull("effective_end_time")
                           .or(w -> w.ge("effective_end_time", LocalDateTime.now())))
                   .orderByDesc("is_top")
                   .orderByDesc("publish_time");

        return list(queryWrapper);
    }

    @Override
    public boolean markAsRead(Long id) {
        try {
            // 检查是否已读
            QueryWrapper<NoticeRead> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("notice_id", id)
                       .eq("user_id", SecurityUtils.getUserId());

            NoticeRead existRead = noticeReadMapper.selectOne(queryWrapper);
            if (existRead != null) {
                return true; // 已读
            }

            // 记录阅读
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setNoticeId(id);
            noticeRead.setUserId(SecurityUtils.getUserId());
            noticeRead.setReadTime(LocalDateTime.now());

            int result = noticeReadMapper.insert(noticeRead);

            // 增加阅读次数
            if (result > 0) {
                baseMapper.incrementReadCount(id);
            }

            return result > 0;
        } catch (Exception e) {
            log.error("标记公告已读失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getNoticeStats(Long id) {
        Map<String, Object> stats = new HashMap<>();

        Notice notice = getById(id);
        if (notice == null) {
            return stats;
        }

        // 总阅读次数
        stats.put("readCount", notice.getReadCount() != null ? notice.getReadCount() : 0);

        // 查询实际阅读人数
        QueryWrapper<NoticeRead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notice_id", id);
        Long readUserCount = noticeReadMapper.selectCount(queryWrapper);
        stats.put("readUserCount", readUserCount != null ? readUserCount.intValue() : 0);

        // 应读人数（简化计算，根据发布范围估算）
        Integer targetUserCount = calculateTargetUserCount(notice);
        stats.put("targetUserCount", targetUserCount);

        // 阅读率
        if (targetUserCount > 0) {
            double readRate = (double) readUserCount / targetUserCount * 100;
            stats.put("readRate", Math.round(readRate * 100.0) / 100.0);
        } else {
            stats.put("readRate", 0.0);
        }

        return stats;
    }

    @Override
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> overview = new HashMap<>();

        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        // 总公告数
        long totalCount = count(queryWrapper);
        overview.put("totalCount", (int) totalCount);

        // 已发布数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0).eq("notice_status", 1);
        long publishedCount = count(queryWrapper);
        overview.put("publishedCount", (int) publishedCount);

        // 置顶公告数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0).eq("is_top", 1).eq("notice_status", 1);
        long topCount = count(queryWrapper);
        overview.put("topCount", (int) topCount);

        // 过期公告数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0).eq("notice_status", 2);
        long expiredCount = count(queryWrapper);
        overview.put("expiredCount", (int) expiredCount);

        return overview;
    }

    /**
     * 计算应读人数
     */
    private Integer calculateTargetUserCount(Notice notice) {
        // 简化实现，实际应该根据发布范围查询具体用户数
        if (notice.getPublishScope() == 1) {
            // 全部用户，这里返回一个估算值
            return 100;
        } else if (notice.getPublishScope() == 2) {
            // 指定范围用户，根据楼栋和单元数量估算
            int count = 0;
            if (notice.getTargetBuildingIds() != null && !notice.getTargetBuildingIds().trim().isEmpty()) {
                String[] buildingIds = notice.getTargetBuildingIds().split(",");
                count += buildingIds.length * 50; // 每个楼栋约50用户
            }
            if (notice.getTargetUnitIds() != null && !notice.getTargetUnitIds().trim().isEmpty()) {
                String[] unitIds = notice.getTargetUnitIds().split(",");
                count += unitIds.length * 20; // 每个单元约20用户
            }
            return Math.max(count, 20); // 至少20人
        } else {
            return 20;
        }
    }
}