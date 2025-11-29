package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.UserHouse;
import com.hyu.property.mapper.UserHouseMapper;
import com.hyu.property.service.IUserHouseService;
import com.hyu.property.service.IWalletService;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户房产关联Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouse> implements IUserHouseService {

    @Autowired
    private IWalletService walletService;

    /**
     * 查询用户房产关联
     *
     * @param id 用户房产关联主键
     * @return 用户房产关联
     */
    @Override
    public UserHouse selectUserHouseById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询用户房产关联列表
     *
     * @param userHouse 用户房产关联
     * @return 用户房产关联集合
     */
    @Override
    public List<UserHouse> selectUserHouseList(UserHouse userHouse) {
        return baseMapper.selectUserHouseList(userHouse);
    }

    /**
     * 分页查询用户房产关联列表
     *
     * @param page 分页参数
     * @param userHouse 用户房产关联
     * @return 用户房产关联分页数据
     */
    @Override
    public Page<UserHouse> selectUserHousePage(Page<UserHouse> page, UserHouse userHouse) {
        return baseMapper.selectUserHousePage(page, userHouse);
    }

    /**
     * 根据用户ID查询房产关联
     *
     * @param userId 用户ID
     * @return 房产关联列表
     */
    @Override
    public List<UserHouse> selectUserHouseByUserId(Long userId) {
        return baseMapper.selectUserHouseByUserId(userId);
    }

    /**
     * 根据房产ID查询用户关联
     *
     * @param houseId 房产ID
     * @return 用户关联列表
     */
    @Override
    public List<UserHouse> selectUserHouseByHouseId(Long houseId) {
        return baseMapper.selectUserHouseByHouseId(houseId);
    }

    /**
     * 根据用户ID查询当前房产关联
     *
     * @param userId 用户ID
     * @return 当前房产关联列表
     */
    @Override
    public List<UserHouse> selectCurrentUserHouseByUserId(Long userId) {
        return baseMapper.selectCurrentUserHouseByUserId(userId);
    }

    /**
     * 新增用户房产关联
     *
     * @param userHouse 用户房产关联
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUserHouse(UserHouse userHouse) {
        userHouse.setCreateBy(SecurityUtils.getUsername());
        userHouse.setCreateTime(new Date());
        userHouse.setIsCurrent(true); // 默认为当前房产
        int result = baseMapper.insert(userHouse);

        // 自动为用户创建钱包记录
        if (result > 0) {
            try {
                boolean walletCreated = walletService.createWallet(userHouse.getUserId(), BigDecimal.ZERO);
                if (walletCreated) {
                    log.info("为用户 {} 成功创建钱包记录", userHouse.getUserId());
                } else {
                    log.warn("为用户 {} 创建钱包记录失败，可能钱包已存在", userHouse.getUserId());
                }
            } catch (Exception e) {
                log.error("为用户 {} 创建钱包记录时发生错误", userHouse.getUserId(), e);
                // 不影响插入结果，只记录错误
            }
        }

        return result;
    }

    /**
     * 修改用户房产关联
     *
     * @param userHouse 用户房产关联
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUserHouse(UserHouse userHouse) {
        userHouse.setUpdateBy(SecurityUtils.getUsername());
        userHouse.setUpdateTime(new Date());
        return baseMapper.updateById(userHouse);
    }

    /**
     * 批量删除用户房产关联
     *
     * @param ids 需要删除的用户房产关联主键集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserHouseByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(java.util.Arrays.asList(ids));
    }

    /**
     * 删除用户房产关联信息
     *
     * @param id 用户房产关联主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserHouseById(Long id) {
        return baseMapper.deleteById(id);
    }

    /**
     * 根据用户ID删除房产关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserHouseByUserId(Long userId) {
        return baseMapper.deleteUserHouseByUserId(userId);
    }

    /**
     * 分配房产给用户
     *
     * @param userId 用户ID
     * @param houseId 房产ID
     * @param relationType 关系类型
     * @return 结果
     */
    @Override
    @Transactional
    public int assignHouseToUser(Long userId, Long houseId, Integer relationType) {
        try {
            // 检查是否已经存在关联
            QueryWrapper<UserHouse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("house_id", houseId);
            UserHouse existingRelation = getOne(queryWrapper);

            if (existingRelation != null) {
                log.warn("用户房产关联已存在, userId: {}, houseId: {}", userId, houseId);
                return 0;
            }

            // 创建新的用户房产关联
            UserHouse userHouse = new UserHouse();
            userHouse.setUserId(userId);
            userHouse.setHouseId(houseId);
            userHouse.setRelationType(relationType);
            userHouse.setStartDate(new Date());
            userHouse.setIsCurrent(true);
            userHouse.setRemark("系统分配房产");

            return insertUserHouse(userHouse);
        } catch (Exception e) {
            log.error("分配房产给用户失败, userId: {}, houseId: {}", userId, houseId, e);
            return 0;
        }
    }

    /**
     * 取消用户房产关联
     *
     * @param userId 用户ID
     * @param houseId 房产ID
     * @return 结果
     */
    @Override
    @Transactional
    public int unassignHouseFromUser(Long userId, Long houseId) {
        try {
            QueryWrapper<UserHouse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("house_id", houseId);

            return baseMapper.delete(queryWrapper);
        } catch (Exception e) {
            log.error("取消用户房产关联失败, userId: {}, houseId: {}", userId, houseId, e);
            return 0;
        }
    }

    /**
     * 根据条件更新用户房产关联
     *
     * @param userHouse 用户房产关联
     * @param userId 用户ID
     * @param isCurrent 是否当前居住
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUserHouseByCondition(UserHouse userHouse, Long userId, Boolean isCurrent) {
        try {
            QueryWrapper<UserHouse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("is_current", isCurrent);

            return baseMapper.update(userHouse, queryWrapper);
        } catch (Exception e) {
            log.error("根据条件更新用户房产关联失败, userId: {}, isCurrent: {}", userId, isCurrent, e);
            return 0;
        }
    }

    /**
     * 获取所有当前居住的住户
     *
     * @return 当前居住的住户列表
     */
    @Override
    public List<UserHouse> getCurrentResidents() {
        try {
            QueryWrapper<UserHouse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_current", 1)  // 只查询当前居住的住户
                       .orderByAsc("user_id", "house_id");

            List<UserHouse> residents = list(queryWrapper);
            log.debug("获取到 {} 个当前居住的住户", residents.size());

            return residents;
        } catch (Exception e) {
            log.error("获取当前居住住户列表失败", e);
            return new ArrayList<>();
        }
    }
}