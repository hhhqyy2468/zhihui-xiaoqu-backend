package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.domain.House;
import com.hyu.property.domain.Unit;
import com.hyu.property.mapper.HouseMapper;
import com.hyu.property.service.IHouseService;
import com.hyu.property.service.IBuildingService;
import com.hyu.property.service.IUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 房产Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements IHouseService {

    @Autowired
    private IBuildingService buildingService;

    @Autowired
    private IUnitService unitService;

    /**
     * 分页查询房产列表
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    @Override
    public Page<House> selectHousePage(Page<House> page, House house) {
        QueryWrapper<House> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(house.getBuildingId() != null, "building_id", house.getBuildingId())
                   .eq(house.getUnitId() != null, "unit_id", house.getUnitId())
                   .like(StringUtils.isNotEmpty(house.getHouseNo()), "house_no", house.getHouseNo())
                   .like(StringUtils.isNotEmpty(house.getRoomNumber()), "room_number", house.getRoomNumber())
                   .eq(house.getHouseStatus() != null, "house_status", house.getHouseStatus())
                   .eq("deleted", 0)
                   .orderByDesc("create_time");

        Page<House> result = page(page, queryWrapper);

        // 填充楼栋和单元名称
        if (result != null && result.getRecords() != null) {
            for (House houseRecord : result.getRecords()) {
                if (houseRecord.getBuildingId() != null) {
                    houseRecord.setBuildingName(getBuildingNameById(houseRecord.getBuildingId()));
                }
                if (houseRecord.getUnitId() != null) {
                    houseRecord.setUnitName(getUnitNameById(houseRecord.getUnitId()));
                }
            }
        }

        return result;
    }

    /**
     * 校验房产编号是否唯一
     *
     * @param house 房产信息
     * @return 结果 true唯一 false不唯一
     */
    @Override
    public boolean checkHouseNoUnique(House house) {
        Long houseId = house.getId() == null ? -1L : house.getId();
        QueryWrapper<House> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("house_no", house.getHouseNo())
                   .eq("deleted", 0);

        House info = getOne(queryWrapper);
        if (info != null && !info.getId().equals(houseId)) {
            return false;
        }
        return true;
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

    /**
     * 根据单元ID获取单元名称
     *
     * @param unitId 单元ID
     * @return 单元名称
     */
    private String getUnitNameById(Long unitId) {
        try {
            Unit unit = unitService.getById(unitId);
            return unit != null ? unit.getUnitName() : "";
        } catch (Exception e) {
            log.error("获取单元名称失败, unitId: {}", unitId, e);
            return "";
        }
    }
}