package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Building;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 楼栋Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {

    /**
     * 查询楼栋列表
     *
     * @param building 楼栋信息
     * @return 楼栋集合
     */
    List<Building> selectBuildingList(Building building);

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
     * @param buildingCode 楼栋编号
     * @return 楼栋信息
     */
    Building checkBuildingCodeUnique(String buildingCode);

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
     * 删除楼栋信息
     *
     * @param buildingId 楼栋ID
     * @return 结果
     */
    int deleteBuildingById(Long buildingId);

    /**
     * 批量删除楼栋信息
     *
     * @param buildingIds 需要删除的楼栋ID数组
     * @return 结果
     */
    int deleteBuildingByIds(Long[] buildingIds);

    /**
     * 检查楼栋下是否存在单元
     *
     * @param buildingId 楼栋ID
     * @return 数量
     */
    int countUnitByBuildingId(Long buildingId);

    /**
     * 检查楼栋下是否存在房产
     *
     * @param buildingId 楼栋ID
     * @return 数量
     */
    int countHouseByBuildingId(Long buildingId);
}