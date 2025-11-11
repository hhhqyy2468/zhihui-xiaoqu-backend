package com.hyu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.system.domain.SysUser;

/**
 * 用户Service接口
 *
 * @author hyu
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectUserByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser selectUserByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    SysUser selectUserByEmail(String email);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(SysUser user);

    /**
     * 修改用户密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updateUserPassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetUserPassword(Long userId, String newPassword);

    /**
     * 校验用户名是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkUsernameUnique(SysUser user);

    /**
     * 校验手机号是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUser user);

    /**
     * 校验邮箱是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkEmailUnique(SysUser user);

    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    boolean deleteUserRoleInfo(Long userId);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    boolean deleteUserRoleInfo(Long roleId, Long[] userIds);

    /**
     * 批量选择用户授权角色
     *
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    boolean insertAuthRoleUsers(Long roleId, Long[] userIds);
}