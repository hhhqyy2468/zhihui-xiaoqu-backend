package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Complaint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投诉Mapper接口
 *
 * @author system
 * @date 2025-11-20
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

}