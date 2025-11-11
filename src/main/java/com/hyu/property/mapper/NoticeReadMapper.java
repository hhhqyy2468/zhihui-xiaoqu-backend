package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.NoticeRead;
import com.hyu.property.domain.vo.NoticeReadVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告阅读记录Mapper接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface NoticeReadMapper extends BaseMapper<NoticeRead> {

    /**
     * 查询公告阅读记录列表（关联查询）
     *
     * @param noticeRead 公告阅读记录
     * @return 公告阅读记录集合
     */
    List<NoticeReadVO> selectNoticeReadList(NoticeRead noticeRead);

    /**
     * 根据公告ID查询阅读记录列表
     *
     * @param noticeId 公告ID
     * @return 阅读记录集合
     */
    List<NoticeReadVO> selectNoticeReadsByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * 根据用户ID查询阅读记录列表
     *
     * @param userId 用户ID
     * @return 阅读记录集合
     */
    List<NoticeReadVO> selectNoticeReadsByUserId(@Param("userId") Long userId);

    /**
     * 查询公告的未读用户列表
     *
     * @param noticeId 公告ID
     * @return 未读用户列表
     */
    List<NoticeReadVO> selectUnreadUsersByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * 查询用户是否已读公告
     *
     * @param noticeId 公告ID
     * @param userId 用户ID
     * @return 阅读记录
     */
    NoticeRead selectUserNoticeRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 统计公告阅读人数
     *
     * @param noticeId 公告ID
     * @return 阅读人数
     */
    Integer countReadUsersByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * 批量插入阅读记录
     *
     * @param noticeReadList 阅读记录列表
     * @return 插入结果
     */
    int batchInsertNoticeRead(@Param("list") List<NoticeRead> noticeReadList);

    /**
     * 删除公告的所有阅读记录
     *
     * @param noticeId 公告ID
     * @return 删除结果
     */
    int deleteNoticeReadsByNoticeId(@Param("noticeId") Long noticeId);
}