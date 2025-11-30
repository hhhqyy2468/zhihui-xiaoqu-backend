package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.Owner;
import com.hyu.property.domain.vo.OwnerVO;
import org.springframework.web.multipart.MultipartFile;

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
    Page<OwnerVO> selectOwnerPage(Page<OwnerVO> page, OwnerVO owner);

    /**
     * 查询业主列表
     *
     * @param owner 业主信息
     * @return 业主集合
     */
    List<OwnerVO> selectOwnerList(OwnerVO owner);

    /**
     * 根据业主ID查询业主信息
     *
     * @param ownerId 业主ID
     * @return 业主信息
     */
    Owner selectOwnerById(Long ownerId);

    /**
     * 根据ID获取业主详细信息
     *
     * @param id 业主ID
     * @return 业主信息
     */
    OwnerVO getOwnerById(Long id);

    /**
     * 校验用户名是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean checkUsernameUnique(OwnerVO owner);

    /**
     * 校验手机号是否唯一
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean checkPhoneUnique(OwnerVO owner);

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
     * 新增业主
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean insertOwner(OwnerVO owner);

    /**
     * 修改业主信息
     *
     * @param owner 业主信息
     * @return 结果
     */
    int updateOwner(Owner owner);

    /**
     * 修改业主
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean updateOwner(OwnerVO owner);

    /**
     * 批量删除业主信息
     *
     * @param ownerIds 需要删除的业主ID数组
     * @return 结果
     */
    int deleteOwnerByIds(Long[] ownerIds);

    /**
     * 批量删除业主
     *
     * @param ids 需要删除的业主ID数组
     * @return 结果
     */
    boolean deleteOwnersByIds(Long[] ids);

    /**
     * 删除业主信息
     *
     * @param ownerId 业主ID
     * @return 结果
     */
    int deleteOwnerById(Long ownerId);

    /**
     * 重置业主密码
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean resetPassword(OwnerVO owner);

    /**
     * 修改业主状态
     *
     * @param owner 业主信息
     * @return 结果
     */
    boolean updateOwnerStatus(OwnerVO owner);

    /**
     * 根据房产ID获取业主列表
     *
     * @param houseId 房产ID
     * @return 业主列表
     */
    List<OwnerVO> getOwnersByHouseId(Long houseId);

    /**
     * 导出业主数据
     *
     * @param list 业主列表
     * @return 导出结果
     */
    String exportOwner(List<OwnerVO> list);

    /**
     * 导入业主数据
     *
     * @param file 导入文件
     * @param updateSupport 是否更新支持
     * @return 结果
     * @throws Exception 异常
     */
    String importOwner(MultipartFile file, boolean updateSupport) throws Exception;

    /**
     * 获取业主导入模板
     *
     * @return 模板数据
     */
    String importTemplate();

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