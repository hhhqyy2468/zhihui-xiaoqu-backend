package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Owner;

import java.util.Date;
import java.util.List;

/**
 * 业主Service接口
 *
 * @author hyu
 */
public interface IOwnerService extends IService<Owner> {

    /**
     * 查询业主列表
     *
     * @param page 分页参数
     * @param owner 业主信息
     * @return 业主分页数据
     */
    Page<Owner> selectOwnerPage(Page<Owner> page, Owner owner);

    /**
     * 根据业主ID查询业主信息
     *
     * @param ownerId 业主ID
     * @return 业主信息
     */
    Owner selectOwnerById(Long ownerId);

    /**
     * 校验手机号是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean checkPhoneUnique(Owner owner);

    /**
     * 校验身份证号是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean checkIdCardUnique(Owner owner);

    /**
     * 新增业主信息
     *
     * @param owner 业主信息
     * @return 结果
     */
    int insertOwner(Owner owner);

    /**
     * 修改业主信息
     *
     * @param owner 业主信息
     * @return 结果
     */
    int updateOwner(Owner owner);

    /**
     * 批量删除业主信息
     *
     * @param ownerIds 需要删除的业主ID数组
     * @return 结果
     */
    int deleteOwnerByIds(Long[] ownerIds);

    /**
     * 删除业主信息
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    int deleteOwnerById(Long ownerId);

    /**
     * 业主搬离处理
     *
     * @param ownerId 业主ID
     * @param moveOutDate 搬离日期
     * @param moveOutReason 搬离原因
     * @return 结果
     */
    int ownerMoveOut(Long ownerId, Date moveOutDate, String moveOutReason);
}