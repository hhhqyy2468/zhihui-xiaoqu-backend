package com.hyu.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyu.property.domain.FeeType;

import java.util.List;

/**
 * 费用类型Service接口
 *
 * @author hyu
 */
public interface IFeeTypeService extends IService<FeeType> {

    /**
     * 分页查询费用类型列表
     *
     * @param page 分页参数
     * @param feeType 费用类型信息
     * @return 费用类型集合信息
     */
    Page<FeeType> selectFeeTypePage(Page<FeeType> page, FeeType feeType);

    /**
     * 查询费用类型列表
     *
     * @param feeType 费用类型信息
     * @return 费用类型集合信息
     */
    List<FeeType> selectFeeTypeList(FeeType feeType);

    /**
     * 查询所有费用类型（下拉框用）
     *
     * @return 费用类型集合信息
     */
    List<FeeType> selectFeeTypeAll();

    /**
     * 根据费用类型ID查询费用类型
     *
     * @param feeTypeId 费用类型ID
     * @return 费用类型
     */
    FeeType selectFeeTypeById(Long feeTypeId);

    /**
     * 校验费用编码是否唯一
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    boolean checkFeeCodeUnique(FeeType feeType);

    /**
     * 新增费用类型
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    int insertFeeType(FeeType feeType);

    /**
     * 修改费用类型
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    int updateFeeType(FeeType feeType);

    /**
     * 批量删除费用类型
     *
     * @param feeTypeIds 需要删除的费用类型ID
     * @return 结果
     */
    int deleteFeeTypeByIds(Long[] feeTypeIds);

    /**
     * 删除费用类型信息
     *
     * @param feeTypeId 费用类型ID
     * @return 结果
     */
    boolean deleteFeeTypeById(Long feeTypeId);

    /**
     * 获取用于账单生成的有效费用类型
     * 排除维修费和停车费
     *
     * @return 有效费用类型列表
     */
    List<FeeType> getActiveFeeTypesForBillGeneration();
}