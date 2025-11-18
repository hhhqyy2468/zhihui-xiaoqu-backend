package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.ParkingSpace;

import java.util.List;

/**
 * 车位管理Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface IParkingSpaceService extends IService<ParkingSpace> {

    /**
     * 分页查询车位列表
     *
     * @param page 分页参数
     * @param parkingSpace 查询条件
     * @return 车位列表
     */
    Page<ParkingSpace> selectParkingSpacePage(Page<ParkingSpace> page, ParkingSpace parkingSpace);

    /**
     * 查询车位列表
     *
     * @param parkingSpace 查询条件
     * @return 车位集合
     */
    List<ParkingSpace> selectParkingSpaceList(ParkingSpace parkingSpace);

    /**
     * 根据ID查询车位
     *
     * @param id 车位ID
     * @return 车位
     */
    ParkingSpace selectParkingSpaceById(Long id);

    /**
     * 新增车位
     *
     * @param parkingSpace 车位信息
     * @return 结果
     */
    boolean insertParkingSpace(ParkingSpace parkingSpace);

    /**
     * 修改车位
     *
     * @param parkingSpace 车位信息
     * @return 结果
     */
    boolean updateParkingSpace(ParkingSpace parkingSpace);

    /**
     * 批量删除车位
     *
     * @param ids 需要删除的车位ID集合
     * @return 结果
     */
    boolean deleteParkingSpaceByIds(List<Long> ids);

    /**
     * 删除车位信息
     *
     * @param id 车位ID
     * @return 结果
     */
    boolean deleteParkingSpaceById(Long id);

    /**
     * 获取车位统计数据
     *
     * @return 统计数据
     */
    java.util.Map<String, Object> getParkingSpaceStats();
}