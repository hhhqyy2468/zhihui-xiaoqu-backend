package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.House;
import com.hyu.property.mapper.HouseMapper;
import com.hyu.property.service.IHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 房产Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements IHouseService {

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
                   .eq(house.getDeleted() != null, "deleted", 0)
                   .orderByDesc("create_time");

        return page(page, queryWrapper);
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
}