package com.hyu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.system.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID查询已分配的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增角色菜单
     */
    int batchInsertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /**
     * 删除角色菜单关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
