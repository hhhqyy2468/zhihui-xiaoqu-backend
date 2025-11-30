package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.UserHouse;

import java.util.List;

/**
 * 用户房产关联Service接口
 *
 * @author hyu
 */
public interface IUserHouseService {

    /**
     * 查询用户房产关联
     *
     * @param id 用户房产关联主键
     * @return 用户房产关联
     */
    UserHouse selectUserHouseById(Long id);

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
    Page<UserHouse> selectUserHousePage(Page<UserHouse> page, UserHouse userHouse);

    /**
     * 根据用户ID查询房产关联
     *
     * @param userId 用户ID
     * @return 房产关联列表
     */
    List<UserHouse> selectUserHouseByUserId(Long userId);

    /**
     * 根据房产ID查询用户关联
     *
     * @param houseId 房产ID
     * @return 用户关联列表
     */
    List<UserHouse> selectUserHouseByHouseId(Long houseId);

    /**
     * 根据用户ID查询当前房产关联
     *
     * @param userId 用户ID
     * @return 当前房产关联列表
     */
    List<UserHouse> selectCurrentUserHouseByUserId(Long userId);

    /**
     * 新增用户房产关联
     *
     * @param userHouse 用户房产关联
     * @return 结果
     */
    int insertUserHouse(UserHouse userHouse);

    /**
     * 修改用户房产关联
     *
     * @param userHouse 用户房产关联
     * @return 结果
     */
    int updateUserHouse(UserHouse userHouse);

    /**
     * 批量删除用户房产关联
     *
     * @param ids 需要删除的用户房产关联主键集合
     * @return 结果
     */
    int deleteUserHouseByIds(Long[] ids);

    /**
     * 删除用户房产关联信息
     *
     * @param id 用户房产关联主键
     * @return 结果
     */
    int deleteUserHouseById(Long id);

    /**
     * 根据用户ID删除房产关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserHouseByUserId(Long userId);

    /**
     * 分配房产给用户
     *
     * @param userId 用户ID
     * @param houseId 房产ID
     * @param relationType 关系类型
     * @return 结果
     */
    int assignHouseToUser(Long userId, Long houseId, Integer relationType);

    /**
     * 取消用户房产关联
     *
     * @param userId 用户ID
     * @param houseId 房产ID
     * @return 结果
     */
    int unassignHouseFromUser(Long userId, Long houseId);

    /**
     * 根据条件更新用户房产关联
     *
     * @param userHouse 用户房产关联
     * @param userId 用户ID
     * @param isCurrent 是否当前居住
     * @return 结果
     */
    int updateUserHouseByCondition(UserHouse userHouse, Long userId, Boolean isCurrent);

    /**
     * 获取所有当前居住的住户
     *
     * @return 当前居住的住户列表
     */
    List<UserHouse> getCurrentResidents();
}