package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.domain.House;
import com.hyu.property.domain.Unit;
import com.hyu.property.domain.UserHouse;
import com.hyu.property.domain.vo.HouseVO;
import com.hyu.property.mapper.HouseMapper;
import com.hyu.property.service.IHouseService;
import com.hyu.property.service.IBuildingService;
import com.hyu.property.service.IUnitService;
import com.hyu.property.service.IUserHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private IUserHouseService userHouseService;

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
     * 分页查询房产列表（包含产权人信息）
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    @Override
    public Page<HouseVO> selectHouseVOPage(Page<HouseVO> page, House house) {
        try {
            // 使用自定义查询获取包含产权人信息的房产数据
            List<HouseVO> houseVOList = baseMapper.selectHouseVOPage(house);

            // 手动实现分页逻辑
            int total = houseVOList.size();
            int pageSize = (int) page.getSize();
            int pageNum = (int) page.getCurrent();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);

            List<HouseVO> records = new ArrayList<>();
            if (startIndex < total) {
                records = houseVOList.subList(startIndex, endIndex);
            }

            // 设置分页结果
            page.setRecords(records);
            page.setTotal(total);

            return page;
        } catch (Exception e) {
            log.error("分页查询房产列表失败", e);
            return new Page<>();
        }
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

    /**
     * 获取可分配的房产列表（空置状态的房产）
     *
     * @param userId 用户ID
     * @return 可分配房产列表
     */
    @Override
    public List<HouseVO> getAvailableHouses(Long userId) {
        try {
            return baseMapper.selectAvailableHouses(userId);
        } catch (Exception e) {
            log.error("获取可分配房产列表失败, userId: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 分配房产给用户
     *
     * @param userId 用户ID
     * @param houseIds 房产ID列表
     * @param relationType 关系类型 1-业主 2-租户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isCurrent 是否当前居住
     * @return 分配结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignHouses(Long userId, List<Long> houseIds, Integer relationType,
                               String startDate, String endDate, Boolean isCurrent) {
        try {
            // 转换日期格式
            Date start = null;
            Date end = null;
            if (StringUtils.isNotEmpty(startDate)) {
                try {
                    // 尝试解析 ISO 8601 格式的日期时间
                    Instant instant = Instant.parse(startDate);
                    start = Date.from(instant);
                } catch (Exception e) {
                    // 如果失败，尝试解析简单的日期格式
                    start = Date.from(LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
            }
            if (StringUtils.isNotEmpty(endDate)) {
                try {
                    // 尝试解析 ISO 8601 格式的日期时间
                    Instant instant = Instant.parse(endDate);
                    end = Date.from(instant);
                } catch (Exception e) {
                    // 如果失败，尝试解析简单的日期格式
                    end = Date.from(LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
            }

            // 如果设置为当前居住，先取消该用户的其他当前居住状态
            if (isCurrent != null && isCurrent) {
                UserHouse updateRecord = new UserHouse();
                updateRecord.setIsCurrent(false);
                userHouseService.updateUserHouseByCondition(updateRecord, userId, true);
            }

            // 批量创建用户房产关联记录
            for (Long houseId : houseIds) {
                UserHouse userHouse = new UserHouse();
                userHouse.setUserId(userId);
                userHouse.setHouseId(houseId);
                userHouse.setRelationType(relationType);
                userHouse.setStartDate(start);
                userHouse.setEndDate(end);
                userHouse.setIsCurrent(isCurrent);
                userHouse.setRemark("系统分配");

                if (userHouseService.insertUserHouse(userHouse) <= 0) {
                    throw new RuntimeException("分配房产失败");
                }

                // 同时更新House表的状态
                House house = new House();
                house.setId(houseId);
                // 根据关系类型设置房产状态：1-业主设为已售(2)，2-租户设为已租(3)
                if (relationType != null && relationType == 1) {
                    house.setHouseStatus(2); // 已售
                } else if (relationType != null && relationType == 2) {
                    house.setHouseStatus(3); // 已租
                } else {
                    house.setHouseStatus(2); // 默认设为已售
                }

                if (baseMapper.updateById(house) <= 0) {
                    throw new RuntimeException("更新房产状态失败");
                }
            }

            log.info("成功为用户分配{}套房产, userId: {}, houseIds: {}", houseIds.size(), userId, houseIds);
            return true;

        } catch (Exception e) {
            log.error("分配房产失败, userId: {}, houseIds: {}", userId, houseIds, e);
            throw new RuntimeException("分配房产失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名分配房产给用户
     *
     * @param username 用户名
     * @param houseId 房产ID
     * @param relationType 关系类型 1-业主 2-租户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isCurrent 是否当前居住
     * @return 分配结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignHouseToUserByUsername(String username, Long houseId, Integer relationType,
                                             String startDate, String endDate, Boolean isCurrent) {
        try {
            // 根据用户名查询用户ID
            Long userId = getUserIdByUsername(username);
            if (userId == null) {
                throw new RuntimeException("用户不存在: " + username);
            }

            // 使用默认值：当前居住，开始时间为当前时间
            Boolean currentIsCurrent = isCurrent != null ? isCurrent : true;
            String currentStartDate = startDate != null ? startDate : java.time.LocalDate.now().toString();

            // 调用原有的分配方法
            return assignHouses(userId, java.util.Arrays.asList(houseId), relationType, currentStartDate, endDate, currentIsCurrent);

        } catch (Exception e) {
            log.error("根据用户名分配房产失败, username: {}, houseId: {}", username, houseId, e);
            throw new RuntimeException("分配房产失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    @Override
    public Long getUserIdByUsername(String username) {
        try {
            return baseMapper.selectUserIdByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名查询用户ID失败, username: {}", username, e);
            return null;
        }
    }

    /**
     * 根据用户名移除用户的房产
     *
     * @param username 用户名
     * @param houseId 房产ID
     * @return 移除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeHouseFromUserByUsername(String username, Long houseId) {
        try {
            // 根据用户名查询用户ID
            Long userId = getUserIdByUsername(username);
            if (userId == null) {
                throw new RuntimeException("用户不存在: " + username);
            }

            // 删除用户房产关联记录
            boolean result = userHouseService.unassignHouseFromUser(userId, houseId) > 0;

            if (result) {
                // 同时更新House表的状态为空置
                House house = new House();
                house.setId(houseId);
                house.setHouseStatus(1); // 1-空置

                if (baseMapper.updateById(house) <= 0) {
                    throw new RuntimeException("更新房产状态失败");
                }

                log.info("成功移除用户房产, username: {}, houseId: {}", username, houseId);
                return true;
            } else {
                throw new RuntimeException("移除房产关联记录失败");
            }

        } catch (Exception e) {
            log.error("移除用户房产失败, username: {}, houseId: {}", username, houseId, e);
            throw new RuntimeException("移除房产失败: " + e.getMessage());
        }
    }

    /**
     * 根据房产ID查询房产信息
     *
     * @param houseId 房产ID
     * @return 房产信息
     */
    @Override
    public House selectHouseById(Long houseId) {
        try {
            return baseMapper.selectById(houseId);
        } catch (Exception e) {
            log.error("根据房产ID查询房产信息失败, houseId: {}", houseId, e);
            return null;
        }
    }
}