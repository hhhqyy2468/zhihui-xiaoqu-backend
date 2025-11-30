package com.hyu.property.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author hyu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${repair.upload.path:./uploads/images}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置外部上传目录的静态资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 保持原有的classpath资源映射（兼容旧图片）
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(CorsConfiguration.ALL)
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }
}