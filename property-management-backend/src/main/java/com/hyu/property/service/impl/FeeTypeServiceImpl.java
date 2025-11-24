package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.FeeType;
import com.hyu.property.mapper.FeeTypeMapper;
import com.hyu.property.service.IFeeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 费用类型Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class FeeTypeServiceImpl extends ServiceImpl<FeeTypeMapper, FeeType> implements IFeeTypeService {

    @Autowired
    private FeeTypeMapper feeTypeMapper;

    /**
     * 分页查询费用类型列表
     *
     * @param page 分页参数
     * @param feeType 费用类型信息
     * @return 费用类型集合信息
     */
    @Override
    public Page<FeeType> selectFeeTypePage(Page<FeeType> page, FeeType feeType) {
        LambdaQueryWrapper<FeeType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(feeType.getTypeName()), FeeType::getTypeName, feeType.getTypeName())
                   .like(StringUtils.isNotEmpty(feeType.getTypeCode()), FeeType::getTypeCode, feeType.getTypeCode())
                   .eq(feeType.getStatus() != null, FeeType::getStatus, feeType.getStatus())
                   .orderByDesc(FeeType::getCreateTime);
        return page(page, queryWrapper);
    }

    /**
     * 查询费用类型列表
     *
     * @param feeType 费用类型信息
     * @return 费用类型集合信息
     */
    @Override
    public List<FeeType> selectFeeTypeList(FeeType feeType) {
        LambdaQueryWrapper<FeeType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(feeType.getTypeName()), FeeType::getTypeName, feeType.getTypeName())
                   .like(StringUtils.isNotEmpty(feeType.getTypeCode()), FeeType::getTypeCode, feeType.getTypeCode())
                   .eq(feeType.getStatus() != null, FeeType::getStatus, feeType.getStatus())
                   .orderByDesc(FeeType::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 查询所有费用类型（下拉框用）
     *
     * @return 费用类型集合信息
     */
    @Override
    public List<FeeType> selectFeeTypeAll() {
        LambdaQueryWrapper<FeeType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FeeType::getStatus, 1);
        queryWrapper.orderByAsc(FeeType::getId);
        return list(queryWrapper);
    }

    /**
     * 根据费用类型ID查询费用类型
     *
     * @param feeTypeId 费用类型ID
     * @return 费用类型
     */
    @Override
    public FeeType selectFeeTypeById(Long feeTypeId) {
        return feeTypeMapper.selectFeeTypeById(feeTypeId);
    }

    /**
     * 校验费用编码是否唯一
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    @Override
    public boolean checkFeeCodeUnique(FeeType feeType) {
        Long feeId = feeType.getId() == null ? -1L : feeType.getId();
        FeeType info = feeTypeMapper.checkFeeCodeUnique(feeType);
        return info == null || info.getId().equals(feeId);
    }

    /**
     * 新增费用类型
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    @Override
    public int insertFeeType(FeeType feeType) {
        return feeTypeMapper.insertFeeType(feeType);
    }

    /**
     * 修改费用类型
     *
     * @param feeType 费用类型信息
     * @return 结果
     */
    @Override
    public int updateFeeType(FeeType feeType) {
        return feeTypeMapper.updateFeeType(feeType);
    }

    /**
     * 批量删除费用类型
     *
     * @param feeTypeIds 需要删除的费用类型ID
     * @return 结果
     */
    @Override
     public int deleteFeeTypeByIds(Long[] feeTypeIds) {
        return feeTypeMapper.deleteFeeTypeByIds(feeTypeIds);
    }

    /**
     * 删除费用类型信息
     *
     * @param feeTypeId 费用类型ID
     * @return 结果
     */
    @Override
    public boolean deleteFeeTypeById(Long feeTypeId) {
        // 检查是否有关联账单
        boolean existBill = feeTypeMapper.checkFeeTypeExistBill(feeTypeId);
        if (existBill) {
            throw new RuntimeException("该费用类型已关联账单，无法删除");
        }
        return removeById(feeTypeId);
    }

    /**
     * 获取用于账单生成的有效费用类型
     * 排除维修费和停车费
     *
     * @return 有效费用类型列表
     */
    @Override
    public List<FeeType> getActiveFeeTypesForBillGeneration() {
        LambdaQueryWrapper<FeeType> queryWrapper = new LambdaQueryWrapper<>();
        // 只查询启用状态的费用类型
        queryWrapper.eq(FeeType::getStatus, 1)
                   // 排除维修费 (REPAIR_FEE)
                   .ne(FeeType::getTypeCode, "REPAIR_FEE")
                   // 排除停车费 (PARKING_FEE 和 parking_fee)
                   .ne(FeeType::getTypeCode, "PARKING_FEE")
                   .ne(FeeType::getTypeCode, "parking_fee")
                   // 按ID排序
                   .orderByAsc(FeeType::getId);

        List<FeeType> feeTypes = list(queryWrapper);
        log.debug("获取到 {} 个用于账单生成的有效费用类型", feeTypes.size());

        return feeTypes;
    }
}