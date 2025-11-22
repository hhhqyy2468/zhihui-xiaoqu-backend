package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Building;
import com.hyu.property.domain.House;
import com.hyu.property.domain.Owner;
import com.hyu.property.domain.Unit;
import com.hyu.property.domain.UserHouse;
import com.hyu.property.domain.vo.OwnerVO;
import com.hyu.property.mapper.OwnerMapper;
import com.hyu.property.service.IOwnerService;
import com.hyu.property.service.IBuildingService;
import com.hyu.property.service.IHouseService;
import com.hyu.property.service.IUnitService;
import com.hyu.property.service.IUserHouseService;
import com.hyu.system.domain.SysUser;

import com.hyu.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业主Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class OwnerServiceImpl extends ServiceImpl<OwnerMapper, Owner> implements IOwnerService {

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IBuildingService buildingService;

    @Autowired
    private IUnitService unitService;

    @Autowired
    private IUserHouseService userHouseService;

    /**
     * 分页查询业主列表
     *
     * @param page 分页参数
     * @param owner 业主信息
     * @return 业主分页数据
     */
    @Override
    public Page<OwnerVO> selectOwnerPage(Page<OwnerVO> page, OwnerVO owner) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type", 3) // 只查询业主用户
                   .like(StringUtils.isNotEmpty(owner.getUsername()), "username", owner.getUsername())
                   .like(StringUtils.isNotEmpty(owner.getRealName()), "real_name", owner.getRealName())
                   .like(StringUtils.isNotEmpty(owner.getPhone()), "phone", owner.getPhone())
                   .eq(owner.getResidentStatus() != null, "resident_status", owner.getResidentStatus())
                   .ge(StringUtils.isNotEmpty(owner.getBeginTime()), "create_time", owner.getBeginTime())
                   .le(StringUtils.isNotEmpty(owner.getEndTime()), "create_time", owner.getEndTime())
                   .orderByDesc("create_time");

        Page<SysUser> userPage = new Page<>(page.getCurrent(), page.getSize());
        Page<SysUser> result = sysUserMapper.selectPage(userPage, queryWrapper);

        // 转换为OwnerVO
        Page<OwnerVO> ownerVOPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<OwnerVO> ownerVOList = new ArrayList<>();
        for (SysUser user : result.getRecords()) {
            OwnerVO ownerVO = new OwnerVO();
            BeanUtils.copyProperties(user, ownerVO);
            ownerVO.setUserId(user.getUserId()); // 使用userId字段
            // 添加房产信息
            List<OwnerVO.HouseInfo> houseList = getUserHouseInfo(user.getUserId());
            ownerVO.setHouseList(houseList);

            // 设置入住时间 - 从当前居住的房产中获取
            if (houseList != null && !houseList.isEmpty()) {
                for (OwnerVO.HouseInfo houseInfo : houseList) {
                    if (houseInfo.getIsCurrent() != null && houseInfo.getIsCurrent() && houseInfo.getStartDate() != null) {
                        ownerVO.setCheckInTime(formatDate(houseInfo.getStartDate()));
                        break;
                    }
                }
            }

            ownerVOList.add(ownerVO);
        }
        ownerVOPage.setRecords(ownerVOList);

        return ownerVOPage;
    }

    /**
     * 查询业主列表
     *
     * @param owner 业主信息
     * @return 业主集合
     */
    @Override
    public List<OwnerVO> selectOwnerList(OwnerVO owner) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type", 3)
                   .like(StringUtils.isNotEmpty(owner.getUsername()), "username", owner.getUsername())
                   .like(StringUtils.isNotEmpty(owner.getRealName()), "real_name", owner.getRealName())
                   .like(StringUtils.isNotEmpty(owner.getPhone()), "phone", owner.getPhone())
                   .orderByDesc("create_time");

        List<SysUser> userList = sysUserMapper.selectList(queryWrapper);
        List<OwnerVO> ownerVOList = new ArrayList<>();
        for (SysUser user : userList) {
            OwnerVO ownerVO = new OwnerVO();
            BeanUtils.copyProperties(user, ownerVO);
            ownerVO.setUserId(user.getUserId()); // 使用userId字段
            // 添加房产信息
            List<OwnerVO.HouseInfo> houseList = getUserHouseInfo(user.getUserId());
            ownerVO.setHouseList(houseList);

            // 设置入住时间 - 从当前居住的房产中获取
            if (houseList != null && !houseList.isEmpty()) {
                for (OwnerVO.HouseInfo houseInfo : houseList) {
                    if (houseInfo.getIsCurrent() != null && houseInfo.getIsCurrent() && houseInfo.getStartDate() != null) {
                        ownerVO.setCheckInTime(formatDate(houseInfo.getStartDate()));
                        break;
                    }
                }
            }

            ownerVOList.add(ownerVO);
        }
        return ownerVOList;
    }

    /**
     * 根据业主ID查询业主信息
     *
     * @param ownerId 业主ID
     * @return 业主信息
     */
    @Override
    public Owner selectOwnerById(Long ownerId) {
        return getById(ownerId);
    }

    /**
     * 根据ID获取业主详细信息
     *
     * @param id 业主ID
     * @return 业主信息
     */
    @Override
    public OwnerVO getOwnerById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || !user.getUserType().equals(3)) {
            return null;
        }
        OwnerVO ownerVO = new OwnerVO();
        BeanUtils.copyProperties(user, ownerVO);
        ownerVO.setUserId(user.getUserId()); // 使用userId字段
        // 添加房产信息
        List<OwnerVO.HouseInfo> houseList = getUserHouseInfo(user.getUserId());
        ownerVO.setHouseList(houseList);

        // 设置入住时间 - 从当前居住的房产中获取
        if (houseList != null && !houseList.isEmpty()) {
            for (OwnerVO.HouseInfo houseInfo : houseList) {
                if (houseInfo.getIsCurrent() != null && houseInfo.getIsCurrent() && houseInfo.getStartDate() != null) {
                    ownerVO.setCheckInTime(formatDate(houseInfo.getStartDate()));
                    break;
                }
            }
        }

        return ownerVO;
    }

    /**
     * 校验用户名是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public boolean checkUsernameUnique(OwnerVO owner) {
        Long userId = owner.getUserId() == null ? -1L : owner.getUserId();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", owner.getUsername())
                   .eq("user_type", 3);

        SysUser info = sysUserMapper.selectOne(queryWrapper);
        if (info != null && !info.getUserId().equals(userId)) {
            return false;
        }
        return true;
    }

    /**
     * 校验手机号是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public boolean checkPhoneUnique(OwnerVO owner) {
        Long userId = owner.getUserId() == null ? -1L : owner.getUserId();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", owner.getPhone())
                   .eq("user_type", 3);

        SysUser info = sysUserMapper.selectOne(queryWrapper);
        if (info != null && !info.getUserId().equals(userId)) {
            return false;
        }
        return true;
    }

    /**
     * 校验身份证号是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public boolean checkIdCardUnique(Owner owner) {
        Long ownerId = owner.getOwnerId() == null ? -1L : owner.getOwnerId();
        QueryWrapper<Owner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card", owner.getIdCard());

        Owner info = getOne(queryWrapper);
        if (info != null && !info.getOwnerId().equals(ownerId)) {
            return false;
        }
        return true;
    }

    /**
     * 新增业主信息
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public int insertOwner(Owner owner) {
        return save(owner) ? 1 : 0;
    }

    /**
     * 新增业主
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertOwner(OwnerVO owner) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(owner, user);
        user.setUserType(3); // 设置为业主

        // 加密密码
        if (StringUtils.isNotEmpty(owner.getPassword())) {
            user.setPassword(passwordEncoder.encode(owner.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        }

        user.setResidentType(1); // 默认为业主
        user.setResidentStatus(1); // 默认在住
        user.setStatus(1); // 默认启用

        return sysUserMapper.insert(user) > 0;
    }

    /**
     * 修改业主信息
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public int updateOwner(Owner owner) {
        return updateById(owner) ? 1 : 0;
    }

    /**
     * 修改业主
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateOwner(OwnerVO owner) {
        SysUser user = sysUserMapper.selectById(owner.getUserId());
        if (user == null) {
            return false;
        }

        BeanUtils.copyProperties(owner, user, "id", "createTime", "password");

        // 如果密码不为空，则加密密码
        if (StringUtils.isNotEmpty(owner.getPassword())) {
            user.setPassword(passwordEncoder.encode(owner.getPassword()));
        }

        return sysUserMapper.updateById(user) > 0;
    }

    /**
     * 批量删除业主信息
     *
     * @param ownerIds 需要删除的业主ID数组
     * @return 结果
     */
    @Override
    public int deleteOwnerByIds(Long[] ownerIds) {
        return removeByIds(java.util.Arrays.asList(ownerIds)) ? ownerIds.length : 0;
    }

    /**
     * 批量删除业主
     *
     * @param ids 需要删除的业主ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteOwnersByIds(Long[] ids) {
        for (Long id : ids) {
            sysUserMapper.deleteById(id);
        }
        return true;
    }

    /**
     * 删除业主信息
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    @Override
    public int deleteOwnerById(Long ownerId) {
        return removeById(ownerId) ? 1 : 0;
    }

    /**
     * 重置业主密码
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public boolean resetPassword(OwnerVO owner) {
        SysUser user = sysUserMapper.selectById(owner.getUserId());
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode("123456")); // 重置为默认密码
        return sysUserMapper.updateById(user) > 0;
    }

    /**
     * 修改业主状态
     *
     * @param owner 业主信息
     * @return 结果
     */
    @Override
    public boolean updateOwnerStatus(OwnerVO owner) {
        SysUser user = sysUserMapper.selectById(owner.getUserId());
        if (user == null) {
            return false;
        }
        user.setStatus(owner.getStatus());
        return sysUserMapper.updateById(user) > 0;
    }

    /**
     * 根据房产ID获取业主列表
     *
     * @param houseId 房产ID
     * @return 业主列表
     */
    @Override
    public List<OwnerVO> getOwnersByHouseId(Long houseId) {
        // TODO: 实现根据房产ID查询业主的逻辑
        // 需要关联user_house表
        return new ArrayList<>();
    }

    /**
     * 导出业主数据
     *
     * @param list 业主列表
     * @return 导出结果
     */
    @Override
    public String exportOwner(List<OwnerVO> list) {
        // TODO: 实现Excel导出功能
        return "导出成功";
    }

    /**
     * 导入业主数据
     *
     * @param file 导入文件
     * @param updateSupport 是否更新支持
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public String importOwner(MultipartFile file, boolean updateSupport) throws Exception {
        // TODO: 实现Excel导入功能
        return "导入成功";
    }

    /**
     * 获取业主导入模板
     *
     * @return 模板数据
     */
    @Override
    public String importTemplate() {
        // TODO: 实现导入模板下载
        return "模板下载";
    }

    /**
     * 业主搬离处理
     *
     * @param ownerId 业主ID
     * @param moveOutDate 搬离日期
     * @param moveOutReason 搬离原因
     * @return 结果
     */
    @Override
    public int ownerMoveOut(Long ownerId, Date moveOutDate, String moveOutReason) {
        SysUser user = sysUserMapper.selectById(ownerId);
        if (user != null && user.getUserType().equals(3)) {
            user.setResidentStatus(2); // 已搬离
            return sysUserMapper.updateById(user);
        }
        return 0;
    }

    /**
     * 获取用户房产信息
     *
     * @param userId 用户ID
     * @return 房产信息列表
     */
    private List<OwnerVO.HouseInfo> getUserHouseInfo(Long userId) {
        List<OwnerVO.HouseInfo> houseList = new ArrayList<>();

        try {
            // 查询用户的房产关联关系
            List<UserHouse> userHouseList = userHouseService.selectUserHouseByUserId(userId);

            for (UserHouse userHouse : userHouseList) {
                Long houseId = userHouse.getHouseId();
                if (houseId != null) {
                    // 获取房产详细信息
                    House house = houseService.getById(houseId);
                    if (house != null) {
                        OwnerVO.HouseInfo houseInfo = new OwnerVO.HouseInfo();
                        houseInfo.setHouseId(house.getId());
                        houseInfo.setHouseNo(house.getHouseNo());
                        houseInfo.setHouseType(house.getHouseType());
                        houseInfo.setBuildingAreaNum(house.getBuildingArea() != null ? house.getBuildingArea().doubleValue() : null);
                        houseInfo.setUsableArea(house.getUsableArea() != null ? house.getUsableArea().doubleValue() : null);
                        houseInfo.setHouseStatus(house.getHouseStatus());

                        // 获取楼栋信息
                        if (house.getBuildingId() != null) {
                            Building building = buildingService.getById(house.getBuildingId());
                            if (building != null) {
                                houseInfo.setBuildingName(building.getBuildingName());
                            }
                        }

                        // 获取单元信息
                        if (house.getUnitId() != null) {
                            Unit unit = unitService.getById(house.getUnitId());
                            if (unit != null) {
                                houseInfo.setUnitName(unit.getUnitName());
                            }
                        }

                        // 设置关系类型
                        houseInfo.setRelationTypeNum(userHouse.getRelationType());
                        houseInfo.setStartDate(userHouse.getStartDate());
                        houseInfo.setEndDate(userHouse.getEndDate());
                        houseInfo.setIsCurrent(userHouse.getIsCurrent());

                        houseList.add(houseInfo);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取用户房产信息失败, userId: {}", userId, e);
        }

        return houseList;
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}