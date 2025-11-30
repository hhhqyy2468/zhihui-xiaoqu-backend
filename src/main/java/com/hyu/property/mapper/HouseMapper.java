package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.House;
import com.hyu.property.domain.vo.HouseVO;
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
     * 获取可分配的房产列表（空置状态的房产）
     *
     * @param userId 用户ID
     * @return 可分配房产列表
     */
    List<HouseVO> selectAvailableHouses(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    Long selectUserIdByUsername(@Param("username") String username);

    /**
     * 分页查询房产列表（包含产权人信息）
     *
     * @param page 分页参数
     * @param house 房产查询条件
     * @return 房产列表
     */
    List<HouseVO> selectHouseVOPage(@Param("house") House house);

    /**
     * 根据房间编号查询房屋ID
     *
     * @param houseNo 房间编号
     * @return 房屋ID
     */
    Long selectHouseIdByHouseNo(@Param("houseNo") String houseNo);

}