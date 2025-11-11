package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyu.property.domain.Owner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业主Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface OwnerMapper extends BaseMapper<Owner> {

    /**
     * 查询业主列表
     *
     * @param owner 业主信息
     * @return 业主集合
     */
    List<Owner> selectOwnerList(Owner owner);

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
     * @param phone 手机号
     * @return 业主信息
     */
    Owner checkPhoneUnique(String phone);

    /**
     * 校验身份证号是否唯一
     *
     * @param idCard 身份证号
     * @return 业主信息
     */
    Owner checkIdCardUnique(String idCard);

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
     * 删除业主信息
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    int deleteOwnerById(Long ownerId);

    /**
     * 批量删除业主信息
     *
     * @param ownerIds 需要删除的业主ID数组
     * @return 结果
     */
    int deleteOwnerByIds(Long[] ownerIds);

    /**
     * 业主搬离处理
     *
     * @param ownerId 业主ID
     * @param moveOutReason 搬离原因
     * @return 结果
     */
    int ownerMoveOut(@Param("ownerId") Long ownerId, @Param("moveOutReason") String moveOutReason);

    /**
     * 查询业主关联的房产数量
     *
     * @param ownerId 业主ID
     * @return 数量
     */
    int countHouseByOwnerId(Long ownerId);
}