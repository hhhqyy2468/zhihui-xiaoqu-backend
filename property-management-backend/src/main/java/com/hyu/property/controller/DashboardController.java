package com.hyu.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.*;
import com.hyu.property.mapper.*;
import com.hyu.system.domain.SysUser;
import com.hyu.system.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页统计数据Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Api(tags = "数据统计")
public class DashboardController {

    private final BuildingMapper buildingMapper;
    private final HouseMapper houseMapper;
    private final SysUserMapper userMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final BillMapper billMapper;
    private final SysOperLogMapper operLogMapper;
    private final SysLoginLogMapper loginLogMapper;

    /**
     * 系统管理员统计数据
     */
    @ApiOperation("系统管理员统计数据")
    @GetMapping("/admin/stats")
    public AjaxResult adminStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("buildingCount", buildingMapper.selectCount(
                    new LambdaQueryWrapper<Building>().eq(Building::getDeleted, 0)));

            stats.put("houseCount", houseMapper.selectCount(
                    new LambdaQueryWrapper<House>().eq(House::getDeleted, 0)));

            stats.put("userCount", userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1)));

            stats.put("logCount", operLogMapper.selectCount(null));

            stats.put("todayLoginCount", loginLogMapper.selectCount(
                    new QueryWrapper<SysLoginLog>().apply("DATE(login_time) = CURDATE()")));

            stats.put("pendingRepairs", repairOrderMapper.selectCount(
                    new LambdaQueryWrapper<RepairOrder>()
                            .eq(RepairOrder::getOrderStatus, 1)
                            .eq(RepairOrder::getDeleted, 0)));

        } catch (Exception e) {
            log.error("获取管理员统计数据失败", e);
        }
        return AjaxResult.success(stats);
    }

    /**
     * 物业管理员统计数据
     */
    @ApiOperation("物业管理员统计数据")
    @GetMapping("/manager/stats")
    public AjaxResult managerStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("houseCount", houseMapper.selectCount(
                    new LambdaQueryWrapper<House>().eq(House::getDeleted, 0)));

            stats.put("ownerCount", userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUserType, 3)
                            .eq(SysUser::getStatus, 1)));

            stats.put("unpaidBills", billMapper.selectCount(
                    new LambdaQueryWrapper<Bill>().in(Bill::getBillStatus, 1, 0)));

            stats.put("pendingRepairs", repairOrderMapper.selectCount(
                    new LambdaQueryWrapper<RepairOrder>()
                            .in(RepairOrder::getOrderStatus, 1, 2)
                            .eq(RepairOrder::getDeleted, 0)));

        } catch (Exception e) {
            log.error("获取物业管理员统计数据失败", e);
        }
        return AjaxResult.success(stats);
    }
}
