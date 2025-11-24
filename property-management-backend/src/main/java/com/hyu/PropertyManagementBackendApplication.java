package com.hyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan({"com.hyu.system.mapper", "com.hyu.property.mapper"})
public class PropertyManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyManagementBackendApplication.class, args);
    }

}
