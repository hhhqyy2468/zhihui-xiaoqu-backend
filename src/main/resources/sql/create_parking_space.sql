-- 创建车位表
CREATE TABLE IF NOT EXISTS parking_space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '车位ID',
    space_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    location VARCHAR(200) NOT NULL COMMENT '车位位置（地下/地上，区域，层，区，号位）',
    space_status TINYINT NOT NULL DEFAULT 1 COMMENT '车位状态：1-空闲 2-已租 3-维修中',
    monthly_rent DECIMAL(10,2) DEFAULT 0.00 COMMENT '月租金',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_space_no (space_no),
    KEY idx_location (location),
    KEY idx_space_status (space_status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位表';