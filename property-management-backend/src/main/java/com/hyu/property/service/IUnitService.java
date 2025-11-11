package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Unit;

import java.util.List;

/**
 * 单元Service接口
 *
 * @author hyu
 */
public interface IUnitService extends IService<Unit> {

    /**
     * 查询单元列表
     *
     * @param page 分页参数
     * @param unit 单元信息
     * @return 单元分页数据
     */
    Page<Unit> selectUnitPage(Page<Unit> page, Unit unit);

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
     * 校验单元编号是否唯一
     *
     * @param unit 单元信息
     * @return 结果
     */
    boolean checkUnitCodeUnique(Unit unit);

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
     * 批量删除单元信息
     *
     * @param unitIds 需要删除的单元ID数组
     * @return 结果
     */
    int deleteUnitByIds(Long[] unitIds);

    /**
     * 删除单元信息
     *
     * @param unitId 单元ID
     * @return 结果
     */
    int deleteUnitById(Long unitId);
}