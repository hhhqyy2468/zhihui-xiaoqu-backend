package com.hyu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.system.domain.SysDictData;
import com.hyu.system.domain.SysDictType;
import com.hyu.system.service.ISysDictTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据字典信息控制器
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/dict/type")
@Validated
public class SysDictTypeController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public PageResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String dictName,
                           @RequestParam(required = false) String dictType,
                           @RequestParam(required = false) String status) {
        log.info("分页查询字典类型列表, pageNum: {}, pageSize: {}, dictName: {}, dictType: {}, status: {}",
                 pageNum, pageSize, dictName, dictType, status);

        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        SysDictType dictTypeQuery = new SysDictType();
        dictTypeQuery.setDictName(dictName);
        dictTypeQuery.setDictType(dictType);
        dictTypeQuery.setStatus(status);

        Page<SysDictType> result = dictTypeService.selectDictTypePage(page, dictTypeQuery);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    public AjaxResult getInfo(@NotNull(message = "字典ID不能为空") @PathVariable Long dictId) {
        log.info("查询字典类型详细, dictId: {}", dictId);
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    public AjaxResult add(@Valid @RequestBody SysDictType dict) {
        log.info("新增字典类型, dict: {}", dict);
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return AjaxResult.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    public AjaxResult edit(@Valid @RequestBody SysDictType dict) {
        log.info("修改字典类型, dict: {}", dict);
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return AjaxResult.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    public AjaxResult remove(@NotNull(message = "字典ID不能为空") @PathVariable Long[] dictIds) {
        log.info("删除字典类型, dictIds: {}", dictIds);
        dictTypeService.deleteDictTypeByIds(dictIds);
        return AjaxResult.success();
    }

    /**
     * 刷新字典缓存
     */
    @DeleteMapping("/refreshCache")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    public AjaxResult refreshCache() {
        log.info("刷新字典缓存");
        dictTypeService.resetDictCache();
        return AjaxResult.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        log.info("获取字典选择框列表");
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return AjaxResult.success(dictTypes);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping("/data/{dictType}")
    public AjaxResult getDictDataByType(@PathVariable String dictType) {
        log.info("根据字典类型查询字典数据信息, dictType: {}", dictType);
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        return AjaxResult.success(data);
    }

    /**
     * 根据字典类型查询字典详细信息
     */
    @GetMapping("/type/{dictType}")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    public AjaxResult getDictTypeByType(@PathVariable String dictType) {
        log.info("根据字典类型查询字典详细信息, dictType: {}", dictType);
        SysDictType dictTypeDetail = dictTypeService.selectDictTypeByType(dictType);
        return AjaxResult.success(dictTypeDetail);
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int result) {
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}