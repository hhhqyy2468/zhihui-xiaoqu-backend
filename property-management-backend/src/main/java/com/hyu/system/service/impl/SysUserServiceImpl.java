package com.hyu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.PasswordUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.mapper.SysUserMapper;
import com.hyu.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public SysUser selectUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return baseMapper.selectUserByUsername(username);
    }

    @Override
    public SysUser selectUserByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        return baseMapper.selectUserByPhone(phone);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return baseMapper.selectUserByEmail(email);
    }

    @Override
    public boolean updateUserInfo(SysUser user) {
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("用户信息不能为空");
        }

        SysUser existUser = this.getById(user.getUserId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验用户名唯一性
        if (StringUtils.hasText(user.getUsername()) &&
            !user.getUsername().equals(existUser.getUsername())) {
            if (!checkUsernameUnique(user)) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 校验手机号唯一性
        if (StringUtils.hasText(user.getPhone()) &&
            !user.getPhone().equals(existUser.getPhone())) {
            if (!checkPhoneUnique(user)) {
                throw new BusinessException("手机号已存在");
            }
        }

        // 校验邮箱唯一性
        if (StringUtils.hasText(user.getEmail()) &&
            !user.getEmail().equals(existUser.getEmail())) {
            if (!checkEmailUnique(user)) {
                throw new BusinessException("邮箱已存在");
            }
        }

        return this.updateById(user);
    }

    @Override
    public boolean updateUserPassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new BusinessException("参数不能为空");
        }

        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        // 加密新密码
        String encryptedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encryptedPassword);

        return this.updateById(user);
    }

    @Override
    public boolean resetUserPassword(Long userId, String newPassword) {
        if (userId == null || !StringUtils.hasText(newPassword)) {
            throw new BusinessException("参数不能为空");
        }

        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 加密新密码
        String encryptedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encryptedPassword);

        return this.updateById(user);
    }

    @Override
    public boolean checkUsernameUnique(SysUser user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = selectUserByUsername(user.getUsername());
        return info == null || info.getUserId().equals(userId);
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = selectUserByPhone(user.getPhone());
        return info == null || info.getUserId().equals(userId);
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = selectUserByEmail(user.getEmail());
        return info == null || info.getUserId().equals(userId);
    }
}