package com.hyu.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.property.domain.FeeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 费用类型Mapper接口
 *
 * @author hyu
 */
@Mapper
public interface FeeTypeMapper extends BaseMapper<FeeType> {

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
     * @return 费用类型
     */
    FeeType checkFeeCodeUnique(FeeType feeType);

    /**
     * 批量删除费用类型
     *
     * @param feeTypeIds 需要删除的费用类型ID
     * @return 结果
     */
    int deleteFeeTypeByIds(Long[] feeTypeIds);

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
     * 检查费用类型是否有关联账单
     *
     * @param feeTypeId 费用类型ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkFeeTypeExistBill(Long feeTypeId);
}