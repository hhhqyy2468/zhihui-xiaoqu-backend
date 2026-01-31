-- 创建车位租赁合同表
CREATE TABLE IF NOT EXISTS parking_rental_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '合同ID',
    contract_no VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    parking_space_id BIGINT NOT NULL COMMENT '车位ID',
    space_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    owner_name VARCHAR(100) NOT NULL COMMENT '业主姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    vehicle_number VARCHAR(20) NOT NULL COMMENT '车辆号码',
    vehicle_brand VARCHAR(100) COMMENT '车辆品牌',
    vehicle_color VARCHAR(50) COMMENT '车辆颜色',
    monthly_rent DECIMAL(10,2) NOT NULL COMMENT '月租金',
    rental_months INT NOT NULL COMMENT '租赁月数',
    start_date DATE NOT NULL COMMENT '租赁开始日期',
    end_date DATE NOT NULL COMMENT '租赁结束日期',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '已付金额',
    contract_status TINYINT DEFAULT 1 COMMENT '合同状态：1-进行中 2-已到期 3-已终止',
    sign_date DATE COMMENT '签订日期',
    terminate_date DATE COMMENT '终止日期',
    terminate_reason VARCHAR(500) COMMENT '终止原因',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_contract_no (contract_no),
    KEY idx_application_id (application_id),
    KEY idx_parking_space_id (parking_space_id),
    KEY idx_owner_id (owner_id),
    KEY idx_contract_status (contract_status),
    KEY idx_end_date (end_date),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位租赁合同表';

-- 插入测试数据（可选）
-- INSERT INTO parking_rental_contract (contract_no, application_id, parking_space_id, space_no, owner_id, owner_name, contact_phone, vehicle_number, monthly_rent, rental_months, start_date, end_date, total_amount, contract_status, sign_date)
-- SELECT
--     CONCAT('C', LPAD(FLOOR(RAND() * 100000), 6, '0')) as contract_no,
--     a.id as application_id,
--     a.parking_space_id,
--     a.space_no,
--     a.owner_id,
--     a.owner_name,
--     a.contact_phone,
--     a.vehicle_number,
--     p.monthly_rent,
--     a.rental_months,
--     a.rental_start_date,
--     DATE_ADD(a.rental_start_date, INTERVAL a.rental_months MONTH) as end_date,
--     p.monthly_rent * a.rental_months as total_amount,
--     1 as contract_status,
--     CURDATE() as sign_date
-- FROM parking_rental_application a
-- JOIN parking_space p ON a.parking_space_id = p.id
-- WHERE a.application_status = 2
--   AND NOT EXISTS (SELECT 1 FROM parking_rental_contract c WHERE c.application_id = a.id)
-- LIMIT 5;
