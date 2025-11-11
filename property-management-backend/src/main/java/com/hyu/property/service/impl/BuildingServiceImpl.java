package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.mapper.BuildingMapper;
import com.hyu.property.service.IBuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 楼栋Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements IBuildingService {

    @Autowired
    private BuildingMapper buildingMapper;

    /**
     * 查询楼栋列表
     *
     * @param page 分页参数
     * @param building 楼栋信息
     * @return 楼栋分页数据
     */
    @Override
    public Page<Building> selectBuildingPage(Page<Building> page, Building building) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(building.getBuildingCode()), "building_code", building.getBuildingCode())
                   .like(StringUtils.isNotEmpty(building.getBuildingName()), "building_name", building.getBuildingName())
                   .like(StringUtils.isNotEmpty(building.getAddress()), "address", building.getAddress())
                   .ge(building.getBuildYearStart() != null, "build_year", building.getBuildYearStart())
                   .le(building.getBuildYearEnd() != null, "build_year", building.getBuildYearEnd())
                   .eq(building.getStatus() != null, "status", building.getStatus())
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
    }

    /**
     * 查询所有正常状态的楼栋（下拉框用）
     *
     * @return 楼栋列表
     */
    @Override
    public List<Building> selectBuildingAll() {
        return buildingMapper.selectBuildingAll();
    }

    /**
     * 根据楼栋ID查询楼栋信息
     *
     * @param buildingId 楼栋ID
     * @return 楼栋信息
     */
    @Override
    public Building selectBuildingById(Long buildingId) {
        return buildingMapper.selectBuildingById(buildingId);
    }

    /**
     * 校验楼栋编号是否唯一
     *
     * @param building 楼栋信息
     * @return 结果
     */
    @Override
    public boolean checkBuildingCodeUnique(Building building) {
        Long buildingId = StringUtils.isNull(building.getBuildingId()) ? -1L : building.getBuildingId();
        Building info = buildingMapper.checkBuildingCodeUnique(building.getBuildingCode());
        if (StringUtils.isNotNull(info) && info.getBuildingId().longValue() != buildingId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 新增楼栋信息
     *
     * @param building 楼栋信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBuilding(Building building) {
        if (!checkBuildingCodeUnique(building)) {
            throw new BusinessException("新增楼栋'" + building.getBuildingName() + "'失败，楼栋编号已存在");
        }
        building.setCreateBy(SecurityUtils.getUsername());
        return buildingMapper.insertBuilding(building);
    }

    /**
     * 修改楼栋信息
     *
     * @param building 楼栋信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBuilding(Building building) {
        if (!checkBuildingCodeUnique(building)) {
            throw new BusinessException("修改楼栋'" + building.getBuildingName() + "'失败，楼栋编号已存在");
        }
        building.setUpdateBy(SecurityUtils.getUsername());
        return buildingMapper.updateBuilding(building);
    }

    /**
     * 批量删除楼栋信息
     *
     * @param buildingIds 需要删除的楼栋ID数组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBuildingByIds(Long[] buildingIds) {
        for (Long buildingId : buildingIds) {
            // 检查楼栋下是否存在单元
            if (buildingMapper.countUnitByBuildingId(buildingId) > 0) {
                Building buildingInfo = buildingMapper.selectBuildingById(buildingId);
                throw new BusinessException("楼栋'" + buildingInfo.getBuildingName() + "'下存在单元，不能删除");
            }
            // 检查楼栋下是否存在房产
            if (buildingMapper.countHouseByBuildingId(buildingId) > 0) {
                Building buildingInfo = buildingMapper.selectBuildingById(buildingId);
                throw new BusinessException("楼栋'" + buildingInfo.getBuildingName() + "'下存在房产，不能删除");
            }
        }
        return buildingMapper.deleteBuildingByIds(buildingIds);
    }

    /**
     * 删除楼栋信息
     *
     * @param buildingId 楼栋ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBuildingById(Long buildingId) {
        // 检查楼栋下是否存在单元
        if (buildingMapper.countUnitByBuildingId(buildingId) > 0) {
            Building buildingInfo = buildingMapper.selectBuildingById(buildingId);
            throw new BusinessException("楼栋'" + buildingInfo.getBuildingName() + "'下存在单元，不能删除");
        }
        // 检查楼栋下是否存在房产
        if (buildingMapper.countHouseByBuildingId(buildingId) > 0) {
            Building buildingInfo = buildingMapper.selectBuildingById(buildingId);
            throw new BusinessException("楼栋'" + buildingInfo.getBuildingName() + "'下存在房产，不能删除");
        }
        return buildingMapper.deleteBuildingById(buildingId);
    }
}