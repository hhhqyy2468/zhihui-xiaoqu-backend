package com.hyu.property.controller;

import com.hyu.property.domain.dto.SystemConfigDTO;
import com.hyu.property.domain.dto.UploadResultDTO;
import com.hyu.property.domain.vo.FileInfoVO;
import com.hyu.property.service.ICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用接口Controller
 *
 * @author system
 * @date 2025-11-11
 */
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private final ICommonService commonService;

    public CommonController(ICommonService commonService) {
        this.commonService = commonService;
    }

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam(required = false) String businessType,
                                          @RequestParam(required = false) Long businessId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "上传文件不能为空");
                return result;
            }

            UploadResultDTO uploadResult = commonService.uploadFile(file, businessType, businessId);
            result.put("code", 200);
            result.put("message", "文件上传成功");
            result.put("data", uploadResult);
        } catch (Exception e) {
            log.error("文件上传异常", e);
            result.put("code", 500);
            result.put("message", "文件上传失败");
        }
        return result;
    }

    /**
     * 批量文件上传
     */
    @PostMapping("/upload/batch")
    public Map<String, Object> uploadFiles(@RequestParam("files") MultipartFile[] files,
                                           @RequestParam(required = false) String businessType,
                                           @RequestParam(required = false) Long businessId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (files == null || files.length == 0) {
                result.put("code", 400);
                result.put("message", "上传文件不能为空");
                return result;
            }

            List<Object> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                fileList.add(file);
            }
            List<UploadResultDTO> uploadResults = commonService.uploadFiles(fileList, businessType, businessId);
            result.put("code", 200);
            result.put("message", "批量文件上传成功");
            result.put("data", uploadResults);
        } catch (Exception e) {
            log.error("批量文件上传异常", e);
            result.put("code", 500);
            result.put("message", "批量文件上传失败");
        }
        return result;
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/file/{fileId}")
    public Map<String, Object> getFileInfo(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        try {
            FileInfoVO fileInfo = commonService.getFileInfo(fileId);
            if (fileInfo == null) {
                result.put("code", 404);
                result.put("message", "文件不存在");
                return result;
            }
            result.put("code", 200);
            result.put("message", "获取文件信息成功");
            result.put("data", fileInfo);
        } catch (Exception e) {
            log.error("获取文件信息异常", e);
            result.put("code", 500);
            result.put("message", "获取文件信息失败");
        }
        return result;
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/file/{fileId}")
    public Map<String, Object> deleteFile(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleteResult = commonService.deleteFile(fileId);
            if (deleteResult) {
                result.put("code", 200);
                result.put("message", "删除文件成功");
            } else {
                result.put("code", 500);
                result.put("message", "删除文件失败");
            }
        } catch (Exception e) {
            log.error("删除文件异常", e);
            result.put("code", 500);
            result.put("message", "删除文件失败");
        }
        return result;
    }

    /**
     * 获取字典类型列表
     */
    @GetMapping("/dict/types")
    public Map<String, Object> getDictTypes() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> dictTypes = commonService.getDictTypes();
            result.put("code", 200);
            result.put("message", "获取字典类型列表成功");
            result.put("data", dictTypes);
        } catch (Exception e) {
            log.error("获取字典类型列表异常", e);
            result.put("code", 500);
            result.put("message", "获取字典类型列表失败");
        }
        return result;
    }

    /**
     * 根据字典类型获取字典数据
     */
    @GetMapping("/dict/data/{dictType}")
    public Map<String, Object> getDictDataByType(@PathVariable String dictType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> dictData = commonService.getDictDataByType(dictType);
            result.put("code", 200);
            result.put("message", "获取字典数据成功");
            result.put("data", dictData);
        } catch (Exception e) {
            log.error("获取字典数据异常", e);
            result.put("code", 500);
            result.put("message", "获取字典数据失败");
        }
        return result;
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/system/config")
    public Map<String, Object> getSystemConfig() {
        Map<String, Object> result = new HashMap<>();
        try {
            SystemConfigDTO config = commonService.getSystemConfig();
            result.put("code", 200);
            result.put("message", "获取系统配置成功");
            result.put("data", config);
        } catch (Exception e) {
            log.error("获取系统配置异常", e);
            result.put("code", 500);
            result.put("message", "获取系统配置失败");
        }
        return result;
    }

    /**
     * 文件下载
     */
    @GetMapping("/file/{fileId}/download")
    public Map<String, Object> downloadFile(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该实现文件下载逻辑
            // 由于是模拟实现，返回下载链接
            String downloadUrl = "http://localhost:8080/api/common/file/" + fileId + "/download";
            result.put("code", 200);
            result.put("message", "获取下载链接成功");
            result.put("data", downloadUrl);
        } catch (Exception e) {
            log.error("文件下载异常", e);
            result.put("code", 500);
            result.put("message", "文件下载失败");
        }
        return result;
    }

    /**
     * 获取文件预览URL
     */
    @GetMapping("/file/{fileId}/preview")
    public Map<String, Object> getFilePreviewUrl(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里应该生成文件预览URL
            String previewUrl = "http://localhost:8080/api/common/file/" + fileId + "/preview";
            result.put("code", 200);
            result.put("message", "获取预览URL成功");
            result.put("data", previewUrl);
        } catch (Exception e) {
            log.error("获取文件预览URL异常", e);
            result.put("code", 500);
            result.put("message", "获取文件预览URL失败");
        }
        return result;
    }
}