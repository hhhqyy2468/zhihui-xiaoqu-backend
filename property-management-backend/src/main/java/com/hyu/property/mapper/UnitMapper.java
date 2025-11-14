package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 分页查询单元列表
     *
     * @param page 分页参数
     * @param unit 单元信息
     * @return 单元分页数据
     */
    Page<Unit> selectUnitList(Page<Unit> page, Unit unit);

    /**
     * 查询单元列表
     *
     * @param unit 单元信息
     * @return 单元集合
     */
    List<Unit> selectUnitList(Unit unit);

    /**
     * 根据楼栋ID查询单元列表
     *
     * @param buildingId 楼栋ID
     * @return 单元集合
     */
    List<Unit> selectUnitListByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 根据单元ID查询单元信息
     *
     * @param unitId 单元ID
     * @return 单元信息
     */
    Unit selectUnitById(@Param("unitId") Long unitId);

    /**
     * 校验单元编号是否唯一
     *
     * @param unit 单元信息
     * @return 结果
     */
    Unit checkUnitCodeUnique(Unit unit);

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
    int deleteUnitById(@Param("unitId") Long unitId);

    /**
     * 批量删除单元信息
     *
     * @param unitIds 需要删除的单元ID数组
     * @return 结果
     */
    int deleteUnitByIds(@Param("unitIds") Long[] unitIds);
}