package com.hyu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

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
     * @param roleIds 角色ID列表
     * @return 结果
     */
    int batchInsertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);
}