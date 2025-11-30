package com.hyu.property.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.property.domain.FeeType;
import com.hyu.property.service.IFeeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用类型Controller
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/feetype")
@Validated
public class FeeTypeController {

    @Autowired
    private IFeeTypeService feeTypeService;

    /**
     * 分页查询费用类型列表
     */
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('property:feetype:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String feeName,
                          @RequestParam(required = false) String feeCode,
                          @RequestParam(required = false) Integer status) {
        log.info("分页查询费用类型列表, pageNum: {}, pageSize: {}, feeName: {}, feeCode: {}, status: {}",
                 pageNum, pageSize, feeName, feeCode, status);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<FeeType> pageParam =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);

        // 构建查询条件
        FeeType feeType = new FeeType();
        feeType.setTypeName(feeName);  // 前端的feeName映射到后端的typeName
        feeType.setTypeCode(feeCode);  // 前端的feeCode映射到后端的typeCode
        feeType.setStatus(status);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<FeeType> result =
            feeTypeService.selectFeeTypePage(pageParam, feeType);
        return AjaxResult.success(result);
    }

    /**
     * 查询费用类型列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:feetype:list')")
    public AjaxResult listAll(FeeType feeType) {
        List<FeeType> list = feeTypeService.selectFeeTypeList(feeType);
        return AjaxResult.success(list);
    }

    /**
     * 获取费用类型详细信息
     */
    @GetMapping("/{feeTypeId}")
    @PreAuthorize("@ss.hasPermi('property:feetype:query')")
    public AjaxResult getInfo(@PathVariable("feeTypeId") Long feeTypeId) {
        FeeType feeType = feeTypeService.selectFeeTypeById(feeTypeId);
        return AjaxResult.success(feeType);
    }

    /**
     * 新增费用类型
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:feetype:add')")
    public AjaxResult add(@Validated @RequestBody FeeType feeType) {
        if (!feeTypeService.checkFeeCodeUnique(feeType)) {
            return AjaxResult.error("新增费用类型'" + feeType.getFeeName() + "'失败，费用编码已存在");
        }
        int result = feeTypeService.insertFeeType(feeType);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改费用类型
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:feetype:edit')")
    public AjaxResult edit(@Validated @RequestBody FeeType feeType) {
        if (!feeTypeService.checkFeeCodeUnique(feeType)) {
            return AjaxResult.error("修改费用类型'" + feeType.getFeeName() + "'失败，费用编码已存在");
        }
        int result = feeTypeService.updateFeeType(feeType);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除费用类型
     */
    @DeleteMapping("/{feeTypeIds}")
    @PreAuthorize("@ss.hasPermi('property:feetype:remove')")
    public AjaxResult remove(@PathVariable Long[] feeTypeIds) {
        int result = feeTypeService.deleteFeeTypeByIds(feeTypeIds);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取所有费用类型（下拉框用）
     */
    @GetMapping("/all")
    public AjaxResult all() {
        List<FeeType> list = feeTypeService.selectFeeTypeAll();
        return AjaxResult.success(list);
    }
}