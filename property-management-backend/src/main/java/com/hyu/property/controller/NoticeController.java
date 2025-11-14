package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.Notice;
import com.hyu.property.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 公告管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/notice")
@Validated
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    /**
     * 分页查询公告列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String noticeTitle,
                          @RequestParam(required = false) String noticeType,
                          @RequestParam(required = false) Integer isTop,
                          @RequestParam(required = false) Integer publishScope,
                          @RequestParam(required = false) Integer noticeStatus,
                          @RequestParam(required = false) Long publisherId,
                          @RequestParam(required = false) String publisherName) {
        log.info("分页查询公告列表, pageNum: {}, pageSize: {}, noticeTitle: {}, noticeType: {}, isTop: {}, publishScope: {}, noticeStatus: {}, publisherId: {}, publisherName: {}",
                pageNum, pageSize, noticeTitle, noticeType, isTop, publishScope, noticeStatus, publisherId, publisherName);

        Page<Notice> page = new Page<>(pageNum, pageSize);
        Notice notice = new Notice();
        notice.setNoticeTitle(noticeTitle);
        notice.setNoticeType(noticeType);
        notice.setIsTop(isTop);
        notice.setPublishScope(publishScope);
        notice.setNoticeStatus(noticeStatus);
        notice.setPublisherId(publisherId);
        notice.setPublisherName(publisherName);

        Page<Notice> result = noticeService.selectNoticePage(page, notice);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取公告详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult getInfo(@NotNull(message = "公告ID不能为空") @PathVariable Long id) {
        log.info("获取公告详细信息, id: {}", id);
        return AjaxResult.success(noticeService.getById(id));
    }

    /**
     * 新增公告
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:notice:add')")
    public AjaxResult add(@Valid @RequestBody Notice notice) {
        log.info("新增公告, notice: {}", notice);
        notice.setCreateBy(SecurityUtils.getUsername());
        notice.setPublisherId(SecurityUtils.getUserId());
        notice.setPublisherName(SecurityUtils.getUsername());
        return toAjax(noticeService.save(notice));
    }

    /**
     * 修改保存公告
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    public AjaxResult edit(@Valid @RequestBody Notice notice) {
        log.info("修改公告, notice: {}", notice);
        notice.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(noticeService.updateById(notice));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:notice:delete')")
    public AjaxResult remove(@NotNull(message = "公告ID不能为空") @PathVariable Long[] ids) {
        log.info("删除公告, ids: {}", ids);
        return toAjax(noticeService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}