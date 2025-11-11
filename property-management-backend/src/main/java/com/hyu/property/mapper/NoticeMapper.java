package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Notice;
import com.hyu.property.domain.vo.NoticeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告Mapper接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 查询公告列表（关联查询）
     *
     * @param notice 公告
     * @return 公告集合
     */
    List<NoticeVO> selectNoticeList(Notice notice);

    /**
     * 查询公告详情（关联查询）
     *
     * @param id 公告主键
     * @return 公告
     */
    NoticeVO selectNoticeById(@Param("id") Long id);

    /**
     * 查询置顶公告列表
     *
     * @return 置顶公告集合
     */
    List<NoticeVO> selectTopNoticeList();

    /**
     * 查询用户可见的公告列表
     *
     * @param userId 用户ID
     * @param buildingId 楼栋ID
     * @param unitId 单元ID
     * @return 公告集合
     */
    List<NoticeVO> selectNoticeListForUser(@Param("userId") Long userId,
                                          @Param("buildingId") Long buildingId,
                                          @Param("unitId") Long unitId);

    /**
     * 增加公告阅读次数
     *
     * @param id 公告ID
     * @return 更新结果
     */
    int incrementReadCount(@Param("id") Long id);

    /**
     * 更新公告状态
     *
     * @param id 公告ID
     * @param noticeStatus 公告状态
     * @return 更新结果
     */
    int updateNoticeStatus(@Param("id") Long id, @Param("noticeStatus") Integer noticeStatus);

    /**
     * 更新置顶状态
     *
     * @param id 公告ID
     * @param isTop 是否置顶
     * @return 更新结果
     */
    int updateTopStatus(@Param("id") Long id, @Param("isTop") Integer isTop);

    /**
     * 查询置顶公告数量
     *
     * @return 数量
     */
    Integer countTopNotices();

    /**
     * 查询过期的公告列表
     *
     * @return 过期公告集合
     */
    List<Notice> selectExpiredNotices();

    /**
     * 批量更新公告状态为过期
     *
     * @param ids 公告ID集合
     * @return 更新结果
     */
    int batchUpdateToExpired(@Param("ids") List<Long> ids);

    /**
     * 查询公告统计信息
     *
     * @return 统计结果
     */
    List<NoticeVO> selectNoticeStatistics();

    /**
     * 查询公告类型统计
     *
     * @return 统计结果
     */
    List<NoticeVO> selectNoticeTypeStats();

    /**
     * 查询阅读率最高的公告
     *
     * @param limit 限制数量
     * @return 公告集合
     */
    List<NoticeVO> selectTopReadNotices(@Param("limit") Integer limit);
}