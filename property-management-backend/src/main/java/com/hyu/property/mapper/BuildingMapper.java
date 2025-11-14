package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Building;
import org.apache.ibatis.annotations.Mapper;

/**
 * 楼栋Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {

}