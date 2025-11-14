package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Building;

/**
 * 楼栋Service接口
 *
 * @author hyu
 */
public interface IBuildingService extends IService<Building> {

    /**
     * 分页查询楼栋列表
     *
     * @param page 分页参数
     * @param building 楼栋信息
     * @return 楼栋分页数据
     */
    Page<Building> selectBuildingPage(Page<Building> page, Building building);

    /**
     * 校验楼栋编号是否唯一
     *
     * @param building 楼栋信息
     * @return 结果 true唯一 false不唯一
     */
    boolean checkBuildingNoUnique(Building building);
}