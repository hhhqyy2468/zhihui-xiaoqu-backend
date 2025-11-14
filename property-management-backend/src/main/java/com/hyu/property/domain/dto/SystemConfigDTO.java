package com.hyu.property.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统配置数据传输对象
 *
 * @author system
 * @date 2025-11-11
 */
@Data
public class SystemConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 系统版本
     */
    private String version;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 系统描述
     */
    private String description;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 系统Logo
     */
    private String systemLogo;

    /**
     * 版权信息
     */
    private String copyright;

    /**
     * 备案号
     */
    private String recordNo;

    /**
     * 上传文件最大大小（字节）
     */
    private Long uploadMaxSize;

    /**
     * 允许上传的图片类型
     */
    private List<String> imageTypes;

    /**
     * 允许上传的文档类型
     */
    private List<String> documentTypes;

    /**
     * 允许上传的视频类型
     */
    private List<String> videoTypes;

    /**
     * 允许上传的音频类型
     */
    private List<String> audioTypes;

    /**
     * 允许上传的其他类型
     */
    private List<String> otherTypes;

    /**
     * 是否启用水印
     */
    private Boolean watermarkEnabled;

    /**
     * 水印文本
     */
    private String watermarkText;

    /**
     * 是否启用文件预览
     */
    private Boolean previewEnabled;

    /**
     * 系统主题
     */
    private String theme;

    /**
     * 系统语言
     */
    private String language;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 是否开启验证码
     */
    private Boolean captchaEnabled;

    /**
     * 是否开启日志记录
     */
    private Boolean logEnabled;

    /**
     * 是否开启操作日志
     */
    private Boolean operLogEnabled;

    /**
     * 是否开启登录日志
     */
    private Boolean loginLogEnabled;
}