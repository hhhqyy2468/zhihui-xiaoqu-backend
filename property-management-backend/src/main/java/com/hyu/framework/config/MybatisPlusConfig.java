package com.hyu.framework.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * MyBatis Plus 配置
 *
 * @author hyu
 */
@Configuration
public class MybatisPlusConfig {



    /**
     * 自动填充配置
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 支持 LocalDateTime 类型的时间字段
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 支持 Date 类型的时间字段
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
                // 支持 String 类型的创建人/更新人字段
                this.strictInsertFill(metaObject, "createBy", String.class, "system");
                this.strictInsertFill(metaObject, "updateBy", String.class, "system");
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 支持 LocalDateTime 类型的时间字段
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 支持 Date 类型的时间字段
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
                // 支持 String 类型的更新人字段
                this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
            }
        };
    }
}