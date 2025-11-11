package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房产Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {

    /**
     * 查询房产列表
     *
     * @param house 房产信息
     * @return 房产集合
     */
    List<House> selectHouseList(House house);

    /**
     * 根据单元ID查询房产列表（下拉框用）
     *
     * @param unitId 单元ID
     * @return 房产列表
     */
    List<House> selectHouseListByUnitId(Long unitId);

    /**
     * 根据房产ID查询房产详细信息
     *
     * @param houseId 房产ID
     * @return 房产信息
     */
    House selectHouseById(Long houseId);

    /**
     * 校验房产编号是否唯一（同一单元下）
     *
     * @param unitId 单元ID
     * @param floorNum 楼层
     * @param roomNum 房间号
     * @return 房产信息
     */
    House checkHouseCodeUnique(@Param("unitId") Long unitId, @Param("floorNum") Integer floorNum, @Param("roomNum") String roomNum);

    /**
     * 新增房产信息
     *
     * @param house 房产信息
     * @return 结果
     */
    int insertHouse(House house);

    /**
     * 修改房产信息
     *
     * @param house 房产信息
     * @return 结果
     */
    int updateHouse(House house);

    /**
     * 删除房产信息
     *
     * @param houseId 房产ID
     * @return 结果
     */
    int deleteHouseById(Long houseId);

    /**
     * 批量删除房产信息
     *
     * @param houseIds 需要删除的房产ID数组
     * @return 结果
     */
    int deleteHouseByIds(Long[] houseIds);
}