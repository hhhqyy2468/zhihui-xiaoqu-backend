package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.House;

/**
 * 房产Service接口
 *
 * @author hyu
 */
public interface IHouseService extends IService<House> {

    /**
     * 分页查询房产列表
     *
     * @param page 分页参数
     * @param house 房产信息
     * @return 房产分页数据
     */
    Page<House> selectHousePage(Page<House> page, House house);

    /**
     * 校验房产编号是否唯一
     *
     * @param house 房产信息
     * @return 结果 true唯一 false不唯一
     */
    boolean checkHouseNoUnique(House house);
}