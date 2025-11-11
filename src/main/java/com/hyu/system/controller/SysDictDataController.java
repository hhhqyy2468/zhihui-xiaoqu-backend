package com.hyu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.system.domain.SysDictData;
import com.hyu.system.service.ISysDictDataService;
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
@RequestMapping("/api/v1/system/dict/data")
@Validated
public class SysDictDataController {

    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public PageResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) String dictLabel,
                           @RequestParam(required = false) String dictType,
                           @RequestParam(required = false) String status) {
        log.info("分页查询字典数据列表, pageNum: {}, pageSize: {}, dictLabel: {}, dictType: {}, status: {}",
                 pageNum, pageSize, dictLabel, dictType, status);

        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        SysDictData dictData = new SysDictData();
        dictData.setDictLabel(dictLabel);
        dictData.setDictType(dictType);
        dictData.setStatus(status);

        Page<SysDictData> result = dictDataService.selectDictDataPage(page, dictData);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    /**
     * 查询字典数据详细
     */
    @GetMapping(value = "/{dictCode}")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    public AjaxResult getInfo(@NotNull(message = "字典数据ID不能为空") @PathVariable Long dictCode) {
        log.info("查询字典数据详细, dictCode: {}", dictCode);
        return AjaxResult.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping("/type/{dictType}")
    public AjaxResult getDictDataByType(@PathVariable String dictType) {
        log.info("根据字典类型查询字典数据信息, dictType: {}", dictType);
        List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
        return AjaxResult.success(data);
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    public AjaxResult add(@Valid @RequestBody SysDictData dict) {
        log.info("新增字典数据, dict: {}", dict);
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典数据
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    public AjaxResult edit(@Valid @RequestBody SysDictData dict) {
        log.info("修改保存字典数据, dict: {}", dict);
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{dictCodes}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    public AjaxResult remove(@NotNull(message = "字典数据ID不能为空") @PathVariable Long[] dictCodes) {
        log.info("删除字典数据, dictCodes: {}", dictCodes);
        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }

    /**
     * 获取字典数据选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect(@RequestParam(required = false) String dictType) {
        log.info("获取字典数据选择框列表, dictType: {}", dictType);
        if (StringUtils.isNotEmpty(dictType)) {
            List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
            return AjaxResult.success(data);
        }
        return AjaxResult.success("请指定字典类型");
    }

    /**
     * 查询所有字典数据列表
     */
    @GetMapping("/all")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public AjaxResult listAll() {
        log.info("查询所有字典数据列表");
        // TODO: 实现查询所有字典数据的方法
        return AjaxResult.success("功能待实现");
    }

    /**
     * 导出字典数据
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    public AjaxResult export(SysDictData dict) {
        log.info("导出字典数据, dict: {}", dict);
        // TODO: 实现字典数据导出功能
        return AjaxResult.success("功能待实现");
    }

    /**
     * 批量导入字典数据
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('system:dict:import')")
    public AjaxResult importData(@RequestBody List<SysDictData> dictDataList) {
        log.info("批量导入字典数据, dictDataList: {}", dictDataList);
        // TODO: 实现字典数据批量导入功能
        return AjaxResult.success("功能待实现");
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(int result) {
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}