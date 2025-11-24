package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.House;
import com.hyu.property.domain.vo.HouseVO;

import java.util.List;

/**
 * 房产Service接口
 *
 * @author hyu
 */
public interface IHouseService extends IService<House> {

    /**
     * 分页查询房产列表
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    Page<House> selectHousePage(Page<House> page, House house);

    /**
     * 分页查询房产列表（包含产权人信息）
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    Page<HouseVO> selectHouseVOPage(Page<HouseVO> page, House house);

    /**
     * 校验房产编号是否唯一
     *
     * @param house 房产信息
     * @return 结果 true唯一 false不唯一
     */
    boolean checkHouseNoUnique(House house);

    /**
     * 获取可分配的房产列表（空置状态的房产）
     *
     * @param userId 用户ID
     * @return 可分配房产列表
     */
    List<HouseVO> getAvailableHouses(Long userId);

    /**
     * 分配房产给用户
     *
     * @param userId 用户ID
     * @param houseIds 房产ID列表
     * @param relationType 关系类型 1-业主 2-租户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isCurrent 是否当前居住
     * @return 分配结果
     */
    boolean assignHouses(Long userId, List<Long> houseIds, Integer relationType,
                         String startDate, String endDate, Boolean isCurrent);

    /**
     * 根据用户名分配房产给用户
     *
     * @param username 用户名
     * @param houseId 房产ID
     * @param relationType 关系类型 1-业主 2-租户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isCurrent 是否当前居住
     * @return 分配结果
     */
    boolean assignHouseToUserByUsername(String username, Long houseId, Integer relationType,
                                       String startDate, String endDate, Boolean isCurrent);

    /**
     * 根据用户名查询用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    Long getUserIdByUsername(String username);

    /**
     * 根据用户名移除用户的房产
     *
     * @param username 用户名
     * @param houseId 房产ID
     * @return 移除结果
     */
    boolean removeHouseFromUserByUsername(String username, Long houseId);

    /**
     * 根据房产ID查询房产信息
     *
     * @param houseId 房产ID
     * @return 房产信息
     */
    House selectHouseById(Long houseId);
}