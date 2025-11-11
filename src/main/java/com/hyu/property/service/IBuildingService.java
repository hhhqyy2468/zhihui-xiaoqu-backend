package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Building;

import java.util.List;

/**
 * 楼栋Service接口
 *
 * @author hyu
 */
public interface IBuildingService extends IService<Building> {

    /**
     * 查询楼栋列表
     *
     * @param page 分页参数
     * @param building 楼栋信息
     * @return 楼栋分页数据
     */
    Page<Building> selectBuildingPage(Page<Building> page, Building building);

    /**
     * 查询所有正常状态的楼栋（下拉框用）
     *
     * @return 楼栋列表
     */
    List<Building> selectBuildingAll();

    /**
     * 根据楼栋ID查询楼栋信息
     *
     * @param buildingId 楼栋ID
     * @return 楼栋信息
     */
    Building selectBuildingById(Long buildingId);

    /**
     * 校验楼栋编号是否唯一
     *
     * @param building 楼栋信息
     * @return 结果
     */
    boolean checkBuildingCodeUnique(Building building);

    /**
     * 新增楼栋信息
     *
     * @param building 楼栋信息
     * @return 结果
     */
    int insertBuilding(Building building);

    /**
     * 修改楼栋信息
     *
     * @param building 楼栋信息
     * @return 结果
     */
    int updateBuilding(Building building);

    /**
     * 批量删除楼栋信息
     *
     * @param buildingIds 需要删除的楼栋ID数组
     * @return 结果
     */
    int deleteBuildingByIds(Long[] buildingIds);

    /**
     * 删除楼栋信息
     *
     * @param buildingId 楼栋ID
     * @return 结果
     */
    int deleteBuildingById(Long buildingId);
}