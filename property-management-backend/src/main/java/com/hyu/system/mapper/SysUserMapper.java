package com.hyu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser selectUserByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    SysUser selectUserByEmail(@Param("email") String email);

    /**
     * 查询用户所属角色组
     *
     * @param username 用户名
     * @return 结果
     */
    String[] selectUserRoleGroup(@Param("username") String username);

    /**
     * 查询用户所属岗位组
     *
     * @param username 用户名
     * @return 结果
     */
    String[] selectUserPostGroup(@Param("username") String username);

    /**
     * 通过用户ID查询用户和角色关联
     *
     * @param userId 用户ID
     * @return 用户和角色关联列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 新增用户角色信息
     *
     * @param userRole 用户角色信息
     * @return 结果
     */
    int insertUserRole(@Param("userRole") java.util.Map<String, Object> userRole);

    /**
     * 删除用户和角色关联信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联信息
     *
     * @param userIds 用户ID数组
     * @return 结果
     */
    int deleteUserRoleByUserIds(Long[] userIds);

    /**
     * 批量新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色ID数组
     * @return 结果
     */
    int batchInsertUserRole(@Param("userId") Long userId, @Param("roleIds") Long[] roleIds);
}