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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 发布公告
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    public AjaxResult publish(@NotNull(message = "公告ID不能为空") @PathVariable Long id) {
        log.info("发布公告, id: {}", id);
        return toAjax(noticeService.publishNotice(id));
    }

    /**
     * 置顶公告
     */
    @PutMapping("/{id}/top")
    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    public AjaxResult setTop(@NotNull(message = "公告ID不能为空") @PathVariable Long id,
                            @RequestParam Integer isTop) {
        log.info("设置公告置顶状态, id: {}, isTop: {}", id, isTop);
        return toAjax(noticeService.updateTopStatus(id, isTop));
    }

    /**
     * 撤回公告
     */
    @PutMapping("/{id}/withdraw")
    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    public AjaxResult withdraw(@NotNull(message = "公告ID不能为空") @PathVariable Long id) {
        log.info("撤回公告, id: {}", id);
        return toAjax(noticeService.withdrawNotice(id));
    }

    /**
     * 获取用户可见的公告列表
     */
    @GetMapping("/user")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult getUserNotices(@RequestParam(required = false) Long buildingId,
                                    @RequestParam(required = false) Long unitId) {
        log.info("获取用户公告列表, buildingId: {}, unitId: {}", buildingId, unitId);
        return AjaxResult.success(noticeService.getUserNotices(buildingId, unitId));
    }

    /**
     * 标记公告已读
     */
    @PostMapping("/{id}/read")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult markAsRead(@NotNull(message = "公告ID不能为空") @PathVariable Long id) {
        log.info("标记公告已读, id: {}", id);
        return toAjax(noticeService.markAsRead(id));
    }

    /**
     * 获取公告统计信息
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult getNoticeStats(@NotNull(message = "公告ID不能为空") @PathVariable Long id) {
        log.info("获取公告统计信息, id: {}", id);
        return AjaxResult.success(noticeService.getNoticeStats(id));
    }

    /**
     * 获取公告概览统计
     */
    @GetMapping("/stats/overview")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult getOverviewStats() {
        log.info("获取公告概览统计");
        return AjaxResult.success(noticeService.getOverviewStats());
    }

    /**
     * 获取公告管理统计页面数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    public AjaxResult getStatistics() {
        log.info("获取公告管理统计页面数据");

        Map<String, Object> data = new HashMap<>();

        // 获取概览统计
        Map<String, Object> overview = noticeService.getOverviewStats();
        data.put("overview", overview);

        // 获取最近的公告列表（前10条）
        Page<Notice> page = new Page<>(1, 10);
        Notice noticeQuery = new Notice();
        noticeQuery.setNoticeStatus(1); // 已发布
        Page<Notice> recentNotices = noticeService.selectNoticePage(page, noticeQuery);
        data.put("recentNotices", recentNotices.getRecords());

        // 获取热门公告（按阅读次数排序，前5条）
        Page<Notice> hotPage = new Page<>(1, 5);
        Notice hotQuery = new Notice();
        hotQuery.setNoticeStatus(1);
        Page<Notice> hotNotices = noticeService.selectNoticePage(hotPage, hotQuery);
        // 按阅读次数排序
        List<Notice> sortedByReadCount = hotNotices.getRecords();
        sortedByReadCount.sort((a, b) -> {
            Integer readCountA = a.getReadCount() != null ? a.getReadCount() : 0;
            Integer readCountB = b.getReadCount() != null ? b.getReadCount() : 0;
            return readCountB.compareTo(readCountA);
        });
        data.put("hotNotices", sortedByReadCount);

        return AjaxResult.success(data);
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}