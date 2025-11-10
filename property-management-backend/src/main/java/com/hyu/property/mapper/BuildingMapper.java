package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Building;
import org.apache.ibatis.annotations.Mapper;

/**
 * 楼栋表 Mapper 接口
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {

}