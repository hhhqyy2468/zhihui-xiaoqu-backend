package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.domain.House;
import com.hyu.property.domain.Unit;
import com.hyu.property.mapper.HouseMapper;
import com.hyu.property.mapper.UnitMapper;
import com.hyu.property.service.IUnitService;
import com.hyu.property.service.IBuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单元Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements IUnitService {

    @Autowired
    private IBuildingService buildingService;

    @Autowired
    private HouseMapper houseMapper;

    /**
     * 分页查询单元列表
     *
     * @param page 分页参数
     * @param unit 单元信息
     * @return 单元分页数据
     */
    @Override
    public Page<Unit> selectUnitPage(Page<Unit> page, Unit unit) {
        Page<Unit> result = baseMapper.selectUnitList(page, unit);

        // 填充楼栋名称
        if (result != null && result.getRecords() != null) {
            for (Unit unitRecord : result.getRecords()) {
                if (unitRecord.getBuildingId() != null) {
                    unitRecord.setBuildingName(getBuildingNameById(unitRecord.getBuildingId()));
                }
            }
        }

        return result;
    }

    /**
     * 根据楼栋ID查询单元列表（下拉框用）
     *
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    @Override
    public List<Unit> selectUnitListByBuildingId(Long buildingId) {
        QueryWrapper<Unit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_id", buildingId)
                   .eq("deleted", 0)
                   .orderByAsc("unit_no");
        return list(queryWrapper);
    }

    /**
     * 根据楼栋ID获取单元列表
     *
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    @Override
    public List<Unit> getUnitsByBuildingId(Long buildingId) {
        return selectUnitListByBuildingId(buildingId);
    }

    /**
     * 根据单元ID查询单元信息
     *
     * @param unitId 单元ID
     * @return 单元信息
     */
    @Override
    public Unit selectUnitById(Long unitId) {
        return getById(unitId);
    }

    /**
     * 校验单元编号是否唯一
     *
     * @param unit 单元信息
     * @return 结果
     */
    @Override
    public boolean checkUnitCodeUnique(Unit unit) {
        Long unitId = unit.getId() == null ? -1L : unit.getId();
        QueryWrapper<Unit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_id", unit.getBuildingId())
                   .eq("unit_no", unit.getUnitNo())
                   .eq("deleted", 0);

        Unit info = getOne(queryWrapper);
        if (info != null && !info.getId().equals(unitId)) {
            return false;
        }
        return true;
    }

    /**
     * 新增单元信息
     *
     * @param unit 单元信息
     * @return 结果
     */
    @Override
    public int insertUnit(Unit unit) {
        return save(unit) ? 1 : 0;
    }

    /**
     * 修改单元信息
     *
     * @param unit 单元信息
     * @return 结果
     */
    @Override
    public int updateUnit(Unit unit) {
        return updateById(unit) ? 1 : 0;
    }

    /**
     * 批量删除单元信息
     *
     * @param unitIds 需要删除的单元ID数组
     * @return 结果
     */
    @Override
    public int deleteUnitByIds(Long[] unitIds) {
        return removeByIds(java.util.Arrays.asList(unitIds)) ? unitIds.length : 0;
    }

    /**
     * 删除单元信息
     *
     * @param unitId 单元ID
     * @return 结果
     */
    @Override
    public int deleteUnitById(Long unitId) {
        return removeById(unitId) ? 1 : 0;
    }

    /**
     * 获取单元下的房产列表
     *
     * @param unitId 单元ID
     * @return 房产列表
     */
    @Override
    public List<House> getUnitHouses(Long unitId) {
        QueryWrapper<House> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unit_id", unitId)
                   .eq("deleted", 0)
                   .orderByAsc("house_no");
        return houseMapper.selectList(queryWrapper);
    }

    /**
     * 根据楼栋ID获取楼栋名称
     *
     * @param buildingId 楼栋ID
     * @return 楼栋名称
     */
    private String getBuildingNameById(Long buildingId) {
        try {
            Building building = buildingService.getById(buildingId);
            return building != null ? building.getBuildingName() : "";
        } catch (Exception e) {
            log.error("获取楼栋名称失败, buildingId: {}", buildingId, e);
            return "";
        }
    }
}