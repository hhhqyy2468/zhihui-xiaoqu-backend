package com.hyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableAspectJAutoProxy
@MapperScan({"com.hyu.system.mapper", "com.hyu.property.mapper"})
public class PropertyManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyManagementBackendApplication.class, args);
    }

}
