package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.domain.Unit;
import com.hyu.property.mapper.BuildingMapper;
import com.hyu.property.mapper.UnitMapper;
import com.hyu.property.service.IBuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private UnitMapper unitMapper;

    /**
     * 分页查询楼栋列表
     *
     * @param page 分页参数
     * @param building 楼栋信息
     * @return 楼栋分页数据
     */
    @Override
    public Page<Building> selectBuildingPage(Page<Building> page, Building building) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(building.getBuildingNo()), "building_no", building.getBuildingNo())
                   .like(StringUtils.isNotEmpty(building.getBuildingName()), "building_name", building.getBuildingName())
                   .like(StringUtils.isNotEmpty(building.getAddress()), "address", building.getAddress())
                   .eq(building.getDeleted() != null, "deleted", 0)
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
    }

    /**
     * 校验楼栋编号是否唯一
     *
     * @param building 楼栋信息
     * @return 结果 true唯一 false不唯一
     */
    @Override
    public boolean checkBuildingNoUnique(Building building) {
        Long buildingId = building.getId() == null ? -1L : building.getId();
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_no", building.getBuildingNo())
                   .eq("deleted", 0);

        Building info = getOne(queryWrapper);
        if (info != null && !info.getId().equals(buildingId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<Unit> getBuildingUnits(Long buildingId) {
        QueryWrapper<Unit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_id", buildingId)
                   .eq("deleted", 0)
                   .orderByAsc("unit_no");

        return unitMapper.selectList(queryWrapper);
    }
}