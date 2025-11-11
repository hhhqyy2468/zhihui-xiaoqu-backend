package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Unit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单元Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface UnitMapper extends BaseMapper<Unit> {

    /**
     * 查询单元列表
     *
     * @param unit 单元信息
     * @return 单元集合
     */
    List<Unit> selectUnitList(Unit unit);

    /**
     * 根据楼栋ID查询单元列表（下拉框用）
     *
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    List<Unit> selectUnitListByBuildingId(Long buildingId);

    /**
     * 根据单元ID查询单元信息
     *
     * @param unitId 单元ID
     * @return 单元信息
     */
    Unit selectUnitById(Long unitId);

    /**
     * 校验单元编号是否唯一（同一楼栋下）
     *
     * @param buildingId 楼栋ID
     * @param unitCode 单元编号
     * @return 单元信息
     */
    Unit checkUnitCodeUnique(@Param("buildingId") Long buildingId, @Param("unitCode") String unitCode);

    /**
     * 新增单元信息
     *
     * @param unit 单元信息
     * @return 结果
     */
    int insertUnit(Unit unit);

    /**
     * 修改单元信息
     *
     * @param unit 单元信息
     * @return 结果
     */
    int updateUnit(Unit unit);

    /**
     * 删除单元信息
     *
     * @param unitId 单元ID
     * @return 结果
     */
    int deleteUnitById(Long unitId);

    /**
     * 批量删除单元信息
     *
     * @param unitIds 需要删除的单元ID数组
     * @return 结果
     */
    int deleteUnitByIds(Long[] unitIds);

    /**
     * 检查单元下是否存在房产
     *
     * @param unitId 单元ID
     * @return 数量
     */
    int countHouseByUnitId(Long unitId);
}