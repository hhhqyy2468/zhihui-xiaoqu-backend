package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.ParkingSpace;
import com.hyu.property.mapper.ParkingSpaceMapper;
import com.hyu.property.service.IParkingSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车位Service业务层处理
 *
 * @author system
 * @date 2025-11-11
 */
@Slf4j
@Service
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements IParkingSpaceService {

    @Override
    public Page<ParkingSpace> selectParkingSpacePage(Page<ParkingSpace> page, ParkingSpace parkingSpace) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.isNotEmpty(parkingSpace.getSpaceNo())) {
            queryWrapper.like("space_no", parkingSpace.getSpaceNo());
        }
        if (StringUtils.isNotEmpty(parkingSpace.getLocation())) {
            queryWrapper.like("location", parkingSpace.getLocation());
        }
        if (parkingSpace.getSpaceStatus() != null) {
            queryWrapper.eq("space_status", parkingSpace.getSpaceStatus());
        }

        queryWrapper.orderByDesc("create_time");

        return this.page(page, queryWrapper);
    }

    @Override
    public List<ParkingSpace> selectParkingSpaceList(ParkingSpace parkingSpace) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.isNotEmpty(parkingSpace.getSpaceNo())) {
            queryWrapper.like("space_no", parkingSpace.getSpaceNo());
        }
        if (StringUtils.isNotEmpty(parkingSpace.getLocation())) {
            queryWrapper.like("location", parkingSpace.getLocation());
        }
        if (parkingSpace.getSpaceStatus() != null) {
            queryWrapper.eq("space_status", parkingSpace.getSpaceStatus());
        }

        queryWrapper.orderByDesc("create_time");

        return this.list(queryWrapper);
    }

    @Override
    public ParkingSpace selectParkingSpaceById(Long id) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("deleted", 0);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean insertParkingSpace(ParkingSpace parkingSpace) {
        parkingSpace.setDeleted(0);
        // 设置状态名称
        setSpaceStatusName(parkingSpace);
        return this.save(parkingSpace);
    }

    @Override
    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        // 设置状态名称
        setSpaceStatusName(parkingSpace);
        return this.updateById(parkingSpace);
    }

    @Override
    public boolean deleteParkingSpaceByIds(List<Long> ids) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        ParkingSpace updateEntity = new ParkingSpace();
        updateEntity.setDeleted(1);
        return this.update(updateEntity, queryWrapper);
    }

    @Override
    public boolean deleteParkingSpaceById(Long id) {
        ParkingSpace parkingSpace = new ParkingSpace();
        parkingSpace.setId(id);
        parkingSpace.setDeleted(1);
        return this.updateById(parkingSpace);
    }

    @Override
    public Map<String, Object> getParkingSpaceStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        // 总车位数
        long totalCount = this.count(queryWrapper);
        stats.put("totalCount", totalCount);

        // 空闲车位数
        queryWrapper.eq("space_status", 1);
        long availableCount = this.count(queryWrapper);
        stats.put("availableCount", availableCount);

        // 已租车位数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("space_status", 2);
        long rentedCount = this.count(queryWrapper);
        stats.put("rentedCount", rentedCount);

        // 维修中车位数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("space_status", 3);
        long maintenanceCount = this.count(queryWrapper);
        stats.put("maintenanceCount", maintenanceCount);

        // 使用率
        double usageRate = totalCount > 0 ? (double) rentedCount / totalCount * 100 : 0;
        stats.put("usageRate", Math.round(usageRate * 100.0) / 100.0);

        return stats;
    }

    /**
     * 设置车位状态名称
     */
    private void setSpaceStatusName(ParkingSpace parkingSpace) {
        if (parkingSpace.getSpaceStatus() != null) {
            switch (parkingSpace.getSpaceStatus()) {
                case 1:
                    parkingSpace.setSpaceStatusName("空闲");
                    break;
                case 2:
                    parkingSpace.setSpaceStatusName("已租");
                    break;
                case 3:
                    parkingSpace.setSpaceStatusName("维修中");
                    break;
                default:
                    parkingSpace.setSpaceStatusName("未知");
                    break;
            }
        }
    }
}