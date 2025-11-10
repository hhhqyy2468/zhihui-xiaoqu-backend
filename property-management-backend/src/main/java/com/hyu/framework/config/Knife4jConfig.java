package com.hyu.framework.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧小区物业管理系统 API")
                        .description("基于Spring Boot 3 + MyBatis Plus + Vue.js的智慧小区物业管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("HYU开发团队")
                                .email("hhqyy2468@example.com")
                                .url("https://github.com/hhhqyy2468"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("智慧小区物业管理系统")
                        .url("https://github.com/hhhqyy2468/zhihui-xiaoqu-backend"));
    }
}