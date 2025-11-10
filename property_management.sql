-- =============================================
-- 社区物业管理系统数据库设计
-- 数据库版本: MySQL 8.0
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 创建日期: 2025-09-29
-- 设计规范: 遵循阿里《Java开发手册》，不使用外键
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_management;

-- =============================================
-- 一、用户权限模块（5张表）
-- =============================================

-- 1. 用户表（增强版）
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    id_card VARCHAR(18) COMMENT '身份证号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    avatar VARCHAR(255) COMMENT '头像URL',
    
    -- 新增住户相关字段
    resident_type TINYINT COMMENT '住户类型：1-业主 2-租户（仅业主角色有值）',
    check_in_date DATE COMMENT '入住日期',
    resident_status TINYINT DEFAULT 1 COMMENT '住户状态：1-在住 2-已搬离',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    
    -- 原有字段
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    user_type TINYINT NOT NULL COMMENT '用户类型：1-系统管理员 2-物业管理员 3-业主 4-维修人员',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status),
    INDEX idx_resident_status (resident_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符串',
    role_sort INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4. 菜单权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示根菜单',
    menu_type CHAR(1) NOT NULL COMMENT '菜单类型：M-目录 C-菜单 F-按钮',
    path VARCHAR(200) COMMENT '路由地址',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 5. 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- =============================================
-- 二、数据字典模块（2张表）
-- =============================================

-- 6. 数据字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典主键',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_type VARCHAR(100) NOT NULL UNIQUE COMMENT '字典类型',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 7. 数据字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典编码',
    dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典键值',
    dict_sort INT DEFAULT 0 COMMENT '显示排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- =============================================
-- 三、住户房产模块（4张表）
-- =============================================

-- 8. 楼栋表
DROP TABLE IF EXISTS building;
CREATE TABLE building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋ID',
    building_no VARCHAR(50) NOT NULL UNIQUE COMMENT '楼栋编号',
    building_name VARCHAR(100) NOT NULL COMMENT '楼栋名称',
    floor_count INT NOT NULL COMMENT '楼层数',
    unit_count INT NOT NULL COMMENT '单元数',
    address VARCHAR(255) COMMENT '详细地址',
    build_year INT COMMENT '建筑年份',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- 9. 单元表
DROP TABLE IF EXISTS unit;
CREATE TABLE unit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '单元ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    unit_no VARCHAR(50) NOT NULL COMMENT '单元编号',
    unit_name VARCHAR(100) NOT NULL COMMENT '单元名称',
    floor_count INT NOT NULL COMMENT '楼层数',
    room_count_per_floor INT NOT NULL COMMENT '每层房间数',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单元表';

-- 10. 房产表
DROP TABLE IF EXISTS house;
CREATE TABLE house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房产ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    unit_id BIGINT NOT NULL COMMENT '单元ID',
    house_no VARCHAR(50) NOT NULL UNIQUE COMMENT '房间编号（楼栋-单元-房号）',
    floor INT NOT NULL COMMENT '楼层',
    room_number VARCHAR(50) NOT NULL COMMENT '门牌号',
    house_type VARCHAR(50) COMMENT '户型（如：三室两厅）',
    building_area DECIMAL(10,2) NOT NULL COMMENT '建筑面积（平方米）',
    usable_area DECIMAL(10,2) COMMENT '使用面积（平方米）',
    house_status TINYINT DEFAULT 1 COMMENT '房产状态：1-空置 2-已售 3-已租 4-自住',
    property_owner VARCHAR(100) COMMENT '产权人姓名',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_building_id (building_id),
    INDEX idx_unit_id (unit_id),
    INDEX idx_house_status (house_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房产表';

-- 11. 用户房产关联表
DROP TABLE IF EXISTS user_house;
CREATE TABLE user_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    house_id BIGINT NOT NULL COMMENT '房产ID',
    relation_type TINYINT NOT NULL COMMENT '关系类型：1-业主 2-租户',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    is_current TINYINT DEFAULT 1 COMMENT '是否当前居住：0-否 1-是',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_house_id (house_id),
    INDEX idx_is_current (is_current),
    UNIQUE KEY uk_user_house_current (user_id, house_id, is_current)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户房产关联表';

-- =============================================
-- 四、虚拟钱包模块（2张表）
-- =============================================

-- 12. 虚拟钱包表
DROP TABLE IF EXISTS wallet;
CREATE TABLE wallet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '钱包ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID（业主）',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '当前余额',
    total_recharge DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计充值金额',
    total_consume DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费金额',
    pay_password VARCHAR(100) COMMENT '支付密码（BCrypt加密）',
    pay_password_error_count INT DEFAULT 0 COMMENT '支付密码错误次数',
    pay_password_lock_time DATETIME COMMENT '支付密码锁定时间',
    status TINYINT DEFAULT 1 COMMENT '钱包状态：1-正常 2-冻结',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='虚拟钱包表';

-- 13. 钱包交易记录表
DROP TABLE IF EXISTS wallet_transaction;
CREATE TABLE wallet_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID',
    transaction_no VARCHAR(64) NOT NULL UNIQUE COMMENT '交易流水号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    wallet_id BIGINT NOT NULL COMMENT '钱包ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-充值 2-消费 3-退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    balance_before DECIMAL(10,2) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
    related_bill_id BIGINT COMMENT '关联账单ID（消费时）',
    related_order_no VARCHAR(64) COMMENT '关联订单号',
    transaction_status TINYINT DEFAULT 1 COMMENT '交易状态：1-成功 2-失败',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_transaction_no (transaction_no),
    INDEX idx_user_id (user_id),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易记录表';

-- =============================================
-- 五、费用管理模块（4张表）
-- =============================================

-- 14. 费用类型表
DROP TABLE IF EXISTS fee_type;
CREATE TABLE fee_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '费用类型ID',
    type_name VARCHAR(50) NOT NULL COMMENT '费用类型名称（物业费/水费/电费/停车费等）',
    type_code VARCHAR(50) NOT NULL UNIQUE COMMENT '费用类型编码',
    unit_price DECIMAL(10,2) COMMENT '单价',
    billing_unit VARCHAR(20) COMMENT '计费单位（月/季/年/平方米/度）',
    billing_cycle TINYINT COMMENT '计费周期：1-月 2-季 3-年',
    description VARCHAR(500) COMMENT '费用说明',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用类型表';

-- 15. 账单表（已删除滞纳金字段）
DROP TABLE IF EXISTS bill;
CREATE TABLE bill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    bill_no VARCHAR(64) NOT NULL UNIQUE COMMENT '账单编号',
    user_id BIGINT NOT NULL COMMENT '业主用户ID',
    house_id BIGINT NOT NULL COMMENT '房产ID',
    fee_type_id BIGINT NOT NULL COMMENT '费用类型ID',
    fee_type_name VARCHAR(50) NOT NULL COMMENT '费用类型名称（冗余字段，便于查询）',
    billing_period VARCHAR(20) NOT NULL COMMENT '账期（如：2025-01）',
    amount DECIMAL(10,2) NOT NULL COMMENT '应缴金额',
    paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实缴金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '减免金额',
    bill_status TINYINT DEFAULT 1 COMMENT '账单状态：1-待缴费 2-已缴费 3-已超期',
    due_date DATE COMMENT '缴费截止日期',
    paid_time DATETIME COMMENT '缴费时间',
    pay_method TINYINT COMMENT '支付方式：1-钱包支付',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_bill_no (bill_no),
    INDEX idx_user_id (user_id),
    INDEX idx_house_id (house_id),
    INDEX idx_bill_status (bill_status),
    INDEX idx_billing_period (billing_period),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- 16. 缴费记录表（已删除经办人字段）
DROP TABLE IF EXISTS payment_record;
CREATE TABLE payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费记录ID',
    payment_no VARCHAR(64) NOT NULL UNIQUE COMMENT '缴费流水号',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    bill_no VARCHAR(64) NOT NULL COMMENT '账单编号（冗余字段）',
    user_id BIGINT NOT NULL COMMENT '业主用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    pay_method TINYINT NOT NULL COMMENT '支付方式：1-钱包支付',
    wallet_transaction_id BIGINT COMMENT '钱包交易ID（钱包支付时）',
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '缴费时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_payment_no (payment_no),
    INDEX idx_bill_id (bill_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_time (payment_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费记录表';

-- 17. 电子收据表（已删除经办人字段）
DROP TABLE IF EXISTS receipt;
CREATE TABLE receipt (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收据ID',
    receipt_no VARCHAR(64) NOT NULL UNIQUE COMMENT '收据编号',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    payment_record_id BIGINT NOT NULL COMMENT '缴费记录ID',
    user_id BIGINT NOT NULL COMMENT '业主用户ID',
    user_name VARCHAR(50) NOT NULL COMMENT '业主姓名（冗余字段）',
    house_id BIGINT NOT NULL COMMENT '房产ID',
    house_no VARCHAR(50) NOT NULL COMMENT '房间编号（冗余字段）',
    fee_type_name VARCHAR(50) NOT NULL COMMENT '费用类型名称',
    billing_period VARCHAR(20) NOT NULL COMMENT '账期',
    amount DECIMAL(10,2) NOT NULL COMMENT '收费金额',
    pay_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    receipt_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收据生成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_receipt_no (receipt_no),
    INDEX idx_bill_id (bill_id),
    INDEX idx_user_id (user_id),
    INDEX idx_receipt_time (receipt_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电子收据表';

-- =============================================
-- 六、投诉管理模块（1张表）
-- =============================================

-- 18. 投诉表
DROP TABLE IF EXISTS complaint;
CREATE TABLE complaint (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投诉ID',
    complaint_no VARCHAR(64) NOT NULL UNIQUE COMMENT '投诉单号',
    user_id BIGINT NOT NULL COMMENT '投诉人ID（业主）',
    user_name VARCHAR(50) NOT NULL COMMENT '投诉人姓名（冗余字段）',
    house_id BIGINT NOT NULL COMMENT '房产ID',
    house_no VARCHAR(50) NOT NULL COMMENT '房间编号（冗余字段）',
    complaint_type VARCHAR(50) NOT NULL COMMENT '投诉类型（数据字典）',
    complaint_content TEXT NOT NULL COMMENT '投诉内容',
    image_urls VARCHAR(1000) COMMENT '图片URLs（多个用逗号分隔，最多5张）',
    urgency_level TINYINT DEFAULT 1 COMMENT '紧急程度：1-普通 2-紧急',
    complaint_status TINYINT DEFAULT 1 COMMENT '投诉状态：1-待处理 2-处理中 3-已处理 4-已关闭',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名（冗余字段）',
    handle_content TEXT COMMENT '处理内容',
    handle_image_urls VARCHAR(1000) COMMENT '处理图片URLs',
    handle_time DATETIME COMMENT '处理完成时间',
    rating TINYINT COMMENT '满意度评价：1-满意 2-一般 3-不满意',
    rating_content VARCHAR(500) COMMENT '评价内容',
    rating_time DATETIME COMMENT '评价时间',
    auto_close_time DATETIME COMMENT '自动关闭时间（处理完成后7天）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投诉时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_complaint_no (complaint_no),
    INDEX idx_user_id (user_id),
    INDEX idx_complaint_status (complaint_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉表';

-- =============================================
-- 七、维修管理模块（1张表）
-- =============================================

-- 19. 维修工单表
DROP TABLE IF EXISTS repair_order;
CREATE TABLE repair_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '维修工单ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '工单编号',
    user_id BIGINT NOT NULL COMMENT '报修人ID（业主）',
    user_name VARCHAR(50) NOT NULL COMMENT '报修人姓名（冗余字段）',
    house_id BIGINT NOT NULL COMMENT '房产ID',
    house_no VARCHAR(50) NOT NULL COMMENT '房间编号（冗余字段）',
    repair_type VARCHAR(50) NOT NULL COMMENT '维修类型（数据字典）',
    fault_description TEXT NOT NULL COMMENT '故障描述',
    image_urls VARCHAR(1000) COMMENT '故障图片URLs（多个用逗号分隔，最多5张）',
    urgency_level TINYINT DEFAULT 1 COMMENT '紧急程度：1-普通 2-紧急',
    appointment_time DATETIME COMMENT '预约维修时间',
    order_status TINYINT DEFAULT 1 COMMENT '工单状态：1-待派工 2-已派工 3-进行中 4-待验收 5-已完成',
    worker_id BIGINT COMMENT '维修人员ID',
    worker_name VARCHAR(50) COMMENT '维修人员姓名（冗余字段）',
    assign_time DATETIME COMMENT '派工时间',
    required_finish_time DATETIME COMMENT '要求完成时间',
    actual_fault VARCHAR(500) COMMENT '实际故障原因',
    repair_content TEXT COMMENT '维修内容',
    repair_image_urls VARCHAR(1000) COMMENT '维修后图片URLs',
    repair_cost DECIMAL(10,2) DEFAULT 0.00 COMMENT '维修费用',
    parts_replaced VARCHAR(500) COMMENT '更换配件',
    finish_time DATETIME COMMENT '完成时间',
    acceptance_result TINYINT COMMENT '验收结果：1-合格 2-不合格',
    acceptance_rating TINYINT COMMENT '验收评分：1-5星',
    acceptance_comment VARCHAR(500) COMMENT '验收意见',
    acceptance_time DATETIME COMMENT '验收时间',
    rework_count INT DEFAULT 0 COMMENT '返工次数',
    auto_accept_time DATETIME COMMENT '自动验收时间（待验收后3天）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_worker_id (worker_id),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修工单表';

-- =============================================
-- 八、公告管理模块（2张表）
-- =============================================

-- 20. 公告表
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    notice_title VARCHAR(200) NOT NULL COMMENT '公告标题',
    notice_type VARCHAR(50) NOT NULL COMMENT '公告类型（数据字典）',
    notice_content TEXT NOT NULL COMMENT '公告内容',
    attachment_urls VARCHAR(1000) COMMENT '附件URLs（多个用逗号分隔，最多3个）',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
    publish_scope TINYINT DEFAULT 1 COMMENT '发布范围：1-全部 2-指定楼栋 3-指定单元',
    target_building_ids VARCHAR(500) COMMENT '目标楼栋IDs（逗号分隔）',
    target_unit_ids VARCHAR(500) COMMENT '目标单元IDs（逗号分隔）',
    effective_start_time DATETIME COMMENT '生效开始时间',
    effective_end_time DATETIME COMMENT '生效结束时间',
    notice_status TINYINT DEFAULT 1 COMMENT '公告状态：1-已发布 2-已过期 3-已撤回',
    publisher_id BIGINT NOT NULL COMMENT '发布人ID',
    publisher_name VARCHAR(50) NOT NULL COMMENT '发布人姓名（冗余字段）',
    read_count INT DEFAULT 0 COMMENT '阅读次数',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_notice_type (notice_type),
    INDEX idx_notice_status (notice_status),
    INDEX idx_is_top (is_top),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 21. 公告阅读记录表
DROP TABLE IF EXISTS notice_read;
CREATE TABLE notice_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    notice_id BIGINT NOT NULL COMMENT '公告ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    read_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY uk_notice_user (notice_id, user_id),
    INDEX idx_notice_id (notice_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告阅读记录表';

-- =============================================
-- 九、停车管理模块（2张表）
-- =============================================

-- 22. 车位表（已删除车位类型和小时单价字段）
DROP TABLE IF EXISTS parking_space;
CREATE TABLE parking_space (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车位ID',
    space_no VARCHAR(50) NOT NULL UNIQUE COMMENT '车位编号',
    location VARCHAR(100) COMMENT '车位位置（地下/地上，区域）',
    space_status TINYINT DEFAULT 1 COMMENT '车位状态：1-空闲 2-已租 3-维修中',
    monthly_rent DECIMAL(10,2) COMMENT '月租金',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_space_status (space_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位表';

-- 23. 车位租赁记录表
DROP TABLE IF EXISTS parking_rental;
CREATE TABLE parking_rental (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租赁记录ID',
    rental_no VARCHAR(64) NOT NULL UNIQUE COMMENT '租赁合同编号',
    user_id BIGINT NOT NULL COMMENT '业主用户ID',
    user_name VARCHAR(50) NOT NULL COMMENT '业主姓名（冗余字段）',
    space_id BIGINT NOT NULL COMMENT '车位ID',
    space_no VARCHAR(50) NOT NULL COMMENT '车位编号（冗余字段）',
    vehicle_plate VARCHAR(20) NOT NULL COMMENT '车牌号',
    vehicle_brand VARCHAR(100) COMMENT '车辆品牌型号',
    vehicle_color VARCHAR(20) COMMENT '车辆颜色',
    owner_name VARCHAR(50) COMMENT '车主姓名',
    rental_period TINYINT NOT NULL COMMENT '租赁期限：1-月租 2-季租 3-半年租 4-年租',
    start_date DATE NOT NULL COMMENT '租赁开始日期',
    end_date DATE NOT NULL COMMENT '租赁结束日期',
    rental_amount DECIMAL(10,2) NOT NULL COMMENT '租金总额',
    rental_status TINYINT DEFAULT 1 COMMENT '租赁状态：1-待审核 2-生效中 3-已到期 4-已退租',
    bill_id BIGINT COMMENT '关联账单ID',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    audit_time DATETIME COMMENT '审核时间',
    auditor_id BIGINT COMMENT '审核人ID',
    refund_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额（提前退租）',
    refund_time DATETIME COMMENT '退款时间',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_rental_no (rental_no),
    INDEX idx_user_id (user_id),
    INDEX idx_space_id (space_id),
    INDEX idx_vehicle_plate (vehicle_plate),
    INDEX idx_rental_status (rental_status),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位租赁记录表';

-- =============================================
-- 十、系统日志模块（2张表）
-- =============================================

-- 24. 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志主键',
    title VARCHAR(50) COMMENT '模块标题',
    business_type TINYINT DEFAULT 0 COMMENT '业务类型：0-其它 1-新增 2-修改 3-删除 4-查询',
    method VARCHAR(200) COMMENT '方法名称',
    request_method VARCHAR(10) COMMENT '请求方式（GET/POST/PUT/DELETE）',
    operator_type TINYINT DEFAULT 0 COMMENT '操作类别：0-其它 1-后台用户 2-手机端用户',
    oper_name VARCHAR(50) COMMENT '操作人员',
    oper_user_id BIGINT COMMENT '操作人员ID',
    oper_url VARCHAR(500) COMMENT '请求URL',
    oper_ip VARCHAR(50) COMMENT '主机地址',
    oper_location VARCHAR(255) COMMENT '操作地点',
    oper_param TEXT COMMENT '请求参数',
    json_result TEXT COMMENT '返回参数',
    status TINYINT DEFAULT 0 COMMENT '操作状态：0-成功 1-失败',
    error_msg VARCHAR(2000) COMMENT '错误消息',
    oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_business_type (business_type),
    INDEX idx_oper_user_id (oper_user_id),
    INDEX idx_oper_time (oper_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 25. 登录日志表
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '访问ID',
    username VARCHAR(50) COMMENT '用户账号',
    user_id BIGINT COMMENT '用户ID',
    ipaddr VARCHAR(50) COMMENT '登录IP地址',
    login_location VARCHAR(255) COMMENT '登录地点',
    browser VARCHAR(50) COMMENT '浏览器类型',
    os VARCHAR(50) COMMENT '操作系统',
    status TINYINT DEFAULT 0 COMMENT '登录状态：0-成功 1-失败',
    msg VARCHAR(255) COMMENT '提示消息',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    INDEX idx_username (username),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- =============================================
-- 数据库表设计完成
-- 共25张表，涵盖10个业务模块
-- =============================================

-- =============================================
-- 初始化基础数据
-- =============================================

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_key, role_sort, status, remark, create_by) VALUES
('系统管理员', 'admin', 1, 1, '系统最高权限', 'system'),
('物业管理员', 'manager', 2, 1, '物业管理人员', 'system'),
('业主', 'owner', 3, 1, '小区业主', 'system'),
('维修人员', 'worker', 4, 1, '维修服务人员', 'system');

INSERT INTO sys_user (username, password, real_name, phone, user_type, status, create_by) 
VALUES ('manager01', '123456', '物业管理员A', '13800138003', 2, 1, 'system');

INSERT INTO sys_user (username, password, real_name, phone, user_type, status, create_by) VALUES
('admin', '123456', '系统管理员', '13800138000', 1, 1, 'system');

-- 初始化维修人员A和B（密码：123456）
INSERT INTO sys_user (username, password, real_name, phone, user_type, status, create_by) VALUES
('worker_a', '123456', '维修人员A', '13800138001', 4, 1, 'system'),
('worker_b', '123456', '维修人员B', '13800138002', 4, 1, 'system');

-- 初始化20个业主用户（密码：123456）
INSERT INTO sys_user (username, password, real_name, phone, user_type, status, resident_type, resident_status, create_by) VALUES
('owner001', '123456', '张三', '13800138101', 3, 1, 1, 1, 'system'),
('owner002', '123456', '李四', '13800138102', 3, 1, 1, 1, 'system'),
('owner003', '123456', '王五', '13800138103', 3, 1, 1, 1, 'system'),
('owner004', '123456', '赵六', '13800138104', 3, 1, 1, 1, 'system'),
('owner005', '123456', '钱七', '13800138105', 3, 1, 1, 1, 'system'),
('owner006', '123456', '孙八', '13800138106', 3, 1, 1, 1, 'system'),
('owner007', '123456', '周九', '13800138107', 3, 1, 1, 1, 'system'),
('owner008', '123456', '吴十', '13800138108', 3, 1, 1, 1, 'system'),
('owner009', '123456', '郑十一', '13800138109', 3, 1, 1, 1, 'system'),
('owner010', '123456', '王十二', '13800138110', 3, 1, 1, 1, 'system'),
('owner011', '123456', '李十三', '13800138111', 3, 1, 1, 1, 'system'),
('owner012', '123456', '张十四', '13800138112', 3, 1, 1, 1, 'system'),
('owner013', '123456', '刘十五', '13800138113', 3, 1, 1, 1, 'system'),
('owner014', '123456', '陈十六', '13800138114', 3, 1, 1, 1, 'system'),
('owner015', '123456', '杨十七', '13800138115', 3, 1, 1, 1, 'system'),
('owner016', '123456', '黄十八', '13800138116', 3, 1, 1, 1, 'system'),
('owner017', '123456', '赵十九', '13800138117', 3, 1, 1, 1, 'system'),
('owner018', '123456', '周二十', '13800138118', 3, 1, 1, 1, 'system'),
('owner019', '123456', '吴二十一', '13800138119', 3, 1, 1, 1, 'system'),
('owner020', '123456', '郑二十二', '13800138120', 3, 1, 1, 1, 'system');

-- 为用户分配角色
INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r 
WHERE u.username = 'admin' AND r.role_key = 'admin';

INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r 
WHERE u.username IN ('worker_a', 'worker_b') AND r.role_key = 'worker';

INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r 
WHERE u.username LIKE 'owner%' AND r.role_key = 'owner';

-- 初始化数据字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by) VALUES
('费用类型', 'fee_type', 1, '物业费用类型', 'system'),
('投诉类型', 'complaint_type', 1, '投诉问题类型', 'system'),
('维修类型', 'repair_type', 1, '维修服务类型', 'system'),
('公告类型', 'notice_type', 1, '公告通知类型', 'system'),
('支付方式', 'pay_method', 1, '支付方式类型', 'system');

-- 初始化数据字典数据 - 费用类型
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, status, create_by) VALUES
('fee_type', '物业费', 'property_fee', 1, 1, 'system'),
('fee_type', '水费', 'water_fee', 2, 1, 'system'),
('fee_type', '电费', 'electricity_fee', 3, 1, 'system'),
('fee_type', '停车费', 'parking_fee', 4, 1, 'system'),
('fee_type', '垃圾清运费', 'garbage_fee', 5, 1, 'system');

-- 初始化数据字典数据 - 投诉类型
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, status, create_by) VALUES
('complaint_type', '环境卫生', 'sanitation', 1, 1, 'system'),
('complaint_type', '噪音扰民', 'noise', 2, 1, 'system'),
('complaint_type', '设施损坏', 'facility_damage', 3, 1, 'system'),
('complaint_type', '服务态度', 'service_attitude', 4, 1, 'system'),
('complaint_type', '其他问题', 'other', 5, 1, 'system');

-- 初始化数据字典数据 - 维修类型
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, status, create_by) VALUES
('repair_type', '水电维修', 'water_electricity', 1, 1, 'system'),
('repair_type', '门窗维修', 'door_window', 2, 1, 'system'),
('repair_type', '电梯维修', 'elevator', 3, 1, 'system'),
('repair_type', '公共设施', 'public_facility', 4, 1, 'system'),
('repair_type', '其他维修', 'other', 5, 1, 'system');

-- 初始化数据字典数据 - 公告类型
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, status, create_by) VALUES
('notice_type', '通知公告', 'notice', 1, 1, 'system'),
('notice_type', '停水停电', 'outage', 2, 1, 'system'),
('notice_type', '社区活动', 'activity', 3, 1, 'system'),
('notice_type', '政策通知', 'policy', 4, 1, 'system'),
('notice_type', '温馨提示', 'reminder', 5, 1, 'system');

-- 初始化数据字典数据 - 支付方式（只保留钱包支付）
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, status, create_by) VALUES
('pay_method', '钱包支付', '1', 1, 1, 'system');

-- =============================================
-- 数据库设计说明
-- =============================================
/*
设计规范：
1. 所有表使用 InnoDB 引擎，支持事务
2. 字符集统一使用 utf8mb4，支持 emoji 和特殊字符
3. 主键统一使用 BIGINT 自增
4. 时间字段统一使用 DATETIME 类型
5. 金额字段统一使用 DECIMAL(10,2) 类型
6. 不使用外键约束，在应用层维护数据一致性
7. 重要字段添加索引，提高查询效率
8. 采用逻辑删除（deleted 字段），不物理删除数据
9. 统一添加创建人、创建时间、更新人、更新时间字段
10. 冗余必要的字段（如姓名、编号），减少表关联查询

表结构汇总：
一、用户权限模块（5张表）
  1. sys_user - 用户表（增强版，包含住户信息）
  2. sys_role - 角色表
  3. sys_user_role - 用户角色关联表
  4. sys_menu - 菜单权限表
  5. sys_role_menu - 角色菜单关联表

二、数据字典模块（2张表）
  6. sys_dict_type - 数据字典类型表
  7. sys_dict_data - 数据字典数据表

三、住户房产模块（4张表）
  8. building - 楼栋表
  9. unit - 单元表
  10. house - 房产表
  11. user_house - 用户房产关联表

四、虚拟钱包模块（2张表）
  12. wallet - 虚拟钱包表
  13. wallet_transaction - 钱包交易记录表

五、费用管理模块（4张表）
  14. fee_type - 费用类型表
  15. bill - 账单表
  16. payment_record - 缴费记录表
  17. receipt - 电子收据表

六、投诉管理模块（1张表）
  18. complaint - 投诉表

七、维修管理模块（1张表）
  19. repair_order - 维修工单表

八、公告管理模块（2张表）
  20. notice - 公告表
  21. notice_read - 公告阅读记录表

九、停车管理模块（2张表）
  22. parking_space - 车位表
  23. parking_rental - 车位租赁记录表

十、系统日志模块（2张表）
  24. sys_oper_log - 操作日志表
  25. sys_login_log - 登录日志表

总计：25张表

核心业务表关系：
- sys_user 是用户核心表，关联 wallet（业主钱包）、user_house（房产关系）
- house 是房产核心表，关联 user_house（用户关系）、bill（账单）
- bill 是账单核心表，关联 payment_record（缴费记录）、receipt（电子收据）
- wallet 是钱包核心表，关联 wallet_transaction（交易记录）
- 所有业务单据表都有唯一编号字段（xxxx_no）

性能优化建议：
1. 对高频查询字段建立索引
2. 钱包表使用乐观锁（version 字段）防止并发问题
3. 交易记录表按月分表（数据量大时）
4. 日志表定期归档（保留6个月数据）
5. 使用 Redis 缓存热点数据（用户信息、字典数据等）

业务流程对应表说明：
1. 用户管理流程 → sys_user, sys_role, sys_user_role
2. 住户与房产管理流程 → sys_user, building, unit, house, user_house
3. 虚拟钱包管理流程 → wallet, wallet_transaction
4. 费用管理流程 → fee_type, bill, payment_record, receipt
5. 投诉管理流程 → complaint
6. 维修管理流程 → repair_order
7. 公告管理流程 → notice, notice_read
8. 停车管理流程 → parking_space, parking_rental
9. 统计报表流程 → 基于各业务表统计
10. 系统维护流程 → sys_oper_log, sys_login_log

重要更新说明：
1. 删除滞纳金相关字段（bill.late_fee）
2. 删除线下支付相关字段（operator_id, operator_name等）
3. 简化车位管理，删除车位类型区分（space_type, hourly_rate）
4. 支付方式只保留钱包支付
5. 初始化维修人员A和B，以及20个业主用户
*/