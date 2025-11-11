package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.House;

import java.util.List;

/**
 * 房产Service接口
 *
 * @author hyu
 */
public interface IHouseService extends IService<House> {

    /**
     * 查询房产列表
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    Page<House> selectHousePage(Page<House> page, House house);

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
     * 校验房产编号是否唯一
     *
     * @param house 房产信息
     * @return 结果
     */
    boolean checkHouseCodeUnique(House house);

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
     * 批量删除房产信息
     *
     * @param houseIds 需要删除的房产ID数组
     * @return 结果
     */
    int deleteHouseByIds(Long[] houseIds);

    /**
     * 删除房产信息
     *
     * @param houseId 房产ID
     * @return 结果
     */
    int deleteHouseById(Long houseId);
}