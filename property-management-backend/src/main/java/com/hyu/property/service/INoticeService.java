package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Notice;

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
}