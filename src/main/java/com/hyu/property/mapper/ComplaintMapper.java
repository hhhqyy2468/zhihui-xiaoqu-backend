package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 投诉Mapper接口
 *
 * @author system
 * @date 2025-11-20
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /**
     * 获取可用的处理人列表（物业管理员）
     *
     * @return 处理人列表
     */
    @Select("SELECT id as value, CONCAT(real_name, '-', username) as label FROM sys_user WHERE user_type = 2 AND status = 1")
    List<Map<String, Object>> selectAvailableHandlers();
}