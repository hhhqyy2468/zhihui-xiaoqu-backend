package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.UserHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户房产关联Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface UserHouseMapper extends BaseMapper<UserHouse> {

    /**
     * 查询用户房产关联列表
     *
     * @param userHouse 用户房产关联
     * @return 用户房产关联集合
     */
    List<UserHouse> selectUserHouseList(UserHouse userHouse);

    /**
     * 分页查询用户房产关联列表
     *
     * @param page 分页参数
     * @param userHouse 用户房产关联
     * @return 用户房产关联分页数据
     */
    Page<UserHouse> selectUserHousePage(Page<UserHouse> page, @Param("userHouse") UserHouse userHouse);

    /**
     * 根据用户ID查询房产关联
     *
     * @param userId 用户ID
     * @return 房产关联列表
     */
    List<UserHouse> selectUserHouseByUserId(@Param("userId") Long userId);

    /**
     * 根据房产ID查询用户关联
     *
     * @param houseId 房产ID
     * @return 用户关联列表
     */
    List<UserHouse> selectUserHouseByHouseId(@Param("houseId") Long houseId);

    /**
     * 根据用户ID查询当前房产关联
     *
     * @param userId 用户ID
     * @return 当前房产关联列表
     */
    List<UserHouse> selectCurrentUserHouseByUserId(@Param("userId") Long userId);

    /**
     * 删除用户房产关联
     *
     * @param id 用户房产关联主键
     * @return 结果
     */
    int deleteUserHouseById(Long id);

    /**
     * 批量删除用户房产关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteUserHouseByIds(Long[] ids);

    /**
     * 根据用户ID删除房产关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserHouseByUserId(@Param("userId") Long userId);
}