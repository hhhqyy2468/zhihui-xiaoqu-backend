package com.hyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hyu.system.mapper")
public class PropertyManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyManagementBackendApplication.class, args);
    }

}
