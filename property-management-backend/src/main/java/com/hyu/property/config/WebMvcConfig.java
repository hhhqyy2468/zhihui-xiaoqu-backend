package com.hyu.property.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author hyu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // 也可以配置整个static目录
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}