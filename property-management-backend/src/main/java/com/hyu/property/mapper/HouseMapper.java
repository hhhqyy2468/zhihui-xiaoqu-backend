package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.House;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房产Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {

}