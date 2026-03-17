package com.hyu.framework.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

/**
 * Knife4j API文档配置
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hyu"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("物业管理系统 API 文档")
                .description("社区物业管理系统接口文档，包含所有REST API")
                .version("1.0.0")
                .contact(new Contact("hyu", "", ""))
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(
                new ApiKey("Authorization", "Authorization", "header")
        );
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(
                                new SecurityReference("Authorization",
                                        new AuthorizationScope[]{new AuthorizationScope("global", "全局")})
                        ))
                        .build()
        );
    }
}
