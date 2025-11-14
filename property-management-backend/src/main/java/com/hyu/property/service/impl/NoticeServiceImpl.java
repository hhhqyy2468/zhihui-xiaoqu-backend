package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Notice;
import com.hyu.property.mapper.NoticeMapper;
import com.hyu.property.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 公告Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

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
                   .eq(notice.getDeleted() != null, "deleted", 0)
                   .orderByDesc("is_top")
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
    }
}