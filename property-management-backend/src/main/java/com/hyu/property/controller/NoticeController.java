package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.Notice;
import com.hyu.property.domain.dto.NoticeDTO;
import com.hyu.property.domain.vo.NoticeVO;
import com.hyu.property.service.INoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 公告Controller
 *
 * @author system
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/property/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final INoticeService noticeService;

    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    private AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    /**
     * 查询公告列表
     */
    @GetMapping("/list")
    public AjaxResult list(Notice notice) {
        List<NoticeVO> list = noticeService.selectNoticeList(notice);
        return success(list);
    }

    /**
     * 获取公告详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(noticeService.selectNoticeById(id));
    }

    /**
     * 新增公告
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NoticeDTO noticeDTO) {
        return toAjax(noticeService.insertNotice(noticeDTO));
    }

    /**
     * 修改公告
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody NoticeDTO noticeDTO) {
        return toAjax(noticeService.updateNotice(noticeDTO));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(noticeService.deleteNoticeByIds(Arrays.asList(ids)));
    }

    // ========== 业务接口 ==========

    /**
     * 发布公告
     */
    @PostMapping("/publish")
    public AjaxResult publishNotice(@Validated @RequestBody NoticeDTO noticeDTO) {
        return toAjax(noticeService.publishNotice(noticeDTO));
    }

    /**
     * 撤回公告
     */
    @PutMapping("/withdraw/{id}")
    public AjaxResult withdrawNotice(@PathVariable Long id) {
        return toAjax(noticeService.withdrawNotice(id));
    }

    /**
     * 设置公告置顶
     */
    @PutMapping("/top/{id}")
    public AjaxResult setTopNotice(@PathVariable Long id, @RequestParam Integer isTop) {
        return toAjax(noticeService.setTopNotice(id, isTop));
    }

    /**
     * 用户查看公告详情
     */
    @GetMapping("/view/{id}")
    public AjaxResult viewNoticeDetail(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        NoticeVO noticeVO = noticeService.viewNoticeDetail(id, currentUserId);
        return success(noticeVO);
    }

    /**
     * 用户标记公告已读
     */
    @PostMapping("/read/{id}")
    public AjaxResult markNoticeAsRead(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        return toAjax(noticeService.markNoticeAsRead(id, currentUserId));
    }

    // ========== 查询接口 ==========

    /**
     * 查询用户可见的公告列表
     */
    @GetMapping("/user")
    public AjaxResult getNoticeListForUser(@RequestParam(required = false) Long buildingId,
                                          @RequestParam(required = false) Long unitId) {
        Long currentUserId = SecurityUtils.getUserId();
        List<NoticeVO> list = noticeService.selectNoticeListForUser(currentUserId, buildingId, unitId);
        return success(list);
    }

    /**
     * 查询置顶公告列表
     */
    @GetMapping("/top")
    public AjaxResult getTopNoticeList() {
        List<NoticeVO> list = noticeService.selectTopNoticeList();
        return success(list);
    }

    /**
     * 获取公告阅读统计
     */
    @GetMapping("/statistics/{id}")
    public AjaxResult getNoticeReadStatistics(@PathVariable Long id) {
        Map<String, Object> stats = noticeService.getNoticeReadStatistics(id);
        return success(stats);
    }

    /**
     * 获取公告的未读用户列表
     */
    @GetMapping("/unread/{id}")
    public AjaxResult getUnreadUsers(@PathVariable Long id) {
        List<Map<String, Object>> users = noticeService.getUnreadUsers(id);
        return success(users);
    }

    /**
     * 获取公告统计数据
     */
    @GetMapping("/statistics")
    public AjaxResult getNoticeStatistics() {
        Map<String, Object> stats = noticeService.getNoticeStatistics();
        return success(stats);
    }

    /**
     * 获取公告类型统计
     */
    @GetMapping("/type-stats")
    public AjaxResult getNoticeTypeStats() {
        List<NoticeVO> stats = noticeService.getNoticeTypeStats();
        return success(stats);
    }

    /**
     * 获取阅读率最高的公告
     */
    @GetMapping("/top-read")
    public AjaxResult getTopReadNotices(@RequestParam(defaultValue = "10") Integer limit) {
        List<NoticeVO> list = noticeService.getTopReadNotices(limit);
        return success(list);
    }

    /**
     * 手动触发过期公告检查
     */
    @PostMapping("/check-expired")
    public AjaxResult checkExpiredNotices() {
        noticeService.checkExpiredNotices();
        return success("过期公告检查已触发");
    }
}