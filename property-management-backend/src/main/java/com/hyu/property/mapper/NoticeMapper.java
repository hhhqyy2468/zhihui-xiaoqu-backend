package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Notice;
import org.apache.ibatis.annotations.Update;

/**
 * 公告Mapper接口
 *
 * @author hyu
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 增加公告阅读次数
     *
     * @param id 公告ID
     * @return 结果
     */
    @Update("UPDATE notice SET read_count = IFNULL(read_count, 0) + 1 WHERE id = #{id}")
    int incrementReadCount(Long id);
}