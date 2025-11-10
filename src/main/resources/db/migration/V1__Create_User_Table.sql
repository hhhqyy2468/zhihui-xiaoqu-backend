-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `user_id`        BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`       VARCHAR(50)     NOT NULL                COMMENT '用户名',
    `password`       VARCHAR(100)    NOT NULL                COMMENT '密码',
    `real_name`      VARCHAR(20)     NOT NULL                COMMENT '真实姓名',
    `phone`          VARCHAR(11)     NOT NULL                COMMENT '手机号',
    `email`          VARCHAR(50)                             COMMENT '邮箱',
    `user_type`      TINYINT(1)      NOT NULL DEFAULT 3      COMMENT '用户类型 1:管理员 2:物业管理员 3:业主 4:维修人员',
    `status`         TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '状态 0:禁用 1:正常',
    `dept_id`        BIGINT(20)                              COMMENT '部门ID',
    `avatar`         VARCHAR(255)                            COMMENT '头像URL',
    `last_login_time` DATETIME                               COMMENT '最后登录时间',
    `last_login_ip`  VARCHAR(50)                             COMMENT '最后登录IP',
    `remark`         VARCHAR(500)                            COMMENT '备注',
    `create_by`      VARCHAR(64)                             COMMENT '创建者',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)                             COMMENT '更新者',
    `update_time`    DATETIME                                 ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 创建管理员用户（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`, `dept_id`)
VALUES ('admin', '$2a$10$7JB720yubVSOfvVWbfXCOOxjTOQcQjmrJF1ZM4nAVccp/.rkMlDWy', '系统管理员', '13800138000', 'admin@example.com', 1, 1, 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);