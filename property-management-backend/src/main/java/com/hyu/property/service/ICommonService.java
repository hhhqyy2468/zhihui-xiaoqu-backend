package com.hyu.property.service;

import com.hyu.property.domain.dto.SystemConfigDTO;
import com.hyu.property.domain.dto.UploadResultDTO;
import com.hyu.property.domain.vo.FileInfoVO;

import java.util.List;
import java.util.Map;

/**
 * 通用Service接口
 *
 * @author system
 * @date 2025-11-11
 */
public interface ICommonService {

    /**
     * 单文件上传
     *
     * @param file 文件
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 上传结果
     */
    public UploadResultDTO uploadFile(Object file, String businessType, Long businessId);

    /**
     * 多文件上传
     *
     * @param files 文件列表
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 上传结果列表
     */
    public List<UploadResultDTO> uploadFiles(List<Object> files, String businessType, Long businessId);

    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    public FileInfoVO getFileInfo(Long fileId);

    /**
     * 获取所有字典类型
     *
     * @return 字典类型列表
     */
    public List<Map<String, Object>> getDictTypes();

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    public List<Map<String, Object>> getDictDataByType(String dictType);

    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    public SystemConfigDTO getSystemConfig();

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除结果
     */
    public boolean deleteFile(Long fileId);
}