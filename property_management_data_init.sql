-- =============================================
-- 物业管理系统补充初始化数据
-- 针对property_management数据库
-- =============================================

USE property_management;

-- =============================================
-- 补充物业管理管理员角色分配
-- =============================================

-- 为物业管理员分配角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r
WHERE u.username = 'manager01' AND r.role_key = 'manager';

-- =============================================
-- 补充楼栋数据
-- =============================================

INSERT INTO building (building_no, building_name, floor_count, unit_count, address, build_year, remark, create_by) VALUES
('A001', 'A栋', 18, 4, '幸福小区1号楼', 2020, '高档住宅楼，配电梯', 'system'),
('B002', 'B栋', 15, 3, '幸福小区2号楼', 2021, '经济型住宅楼', 'system'),
('C003', 'C栋', 12, 2, '幸福小区3号楼', 2019, '小户型公寓楼', 'system'),
('D004', 'D栋', 20, 5, '幸福小区4号楼', 2022, '豪华住宅楼', 'system'),
('E005', 'E栋', 10, 2, '幸福小区5号楼', 2018, '老年公寓楼', 'system');

-- =============================================
-- 补充单元数据
-- =============================================

INSERT INTO unit (building_id, unit_no, unit_name, floor_count, room_count_per_floor, remark, create_by) VALUES
-- A栋单元
(1, 'A1', 'A栋一单元', 18, 4, '主力户型', 'system'),
(1, 'A2', 'A栋二单元', 18, 4, '主力户型', 'system'),
(1, 'A3', 'A栋三单元', 18, 3, '大户型', 'system'),
(1, 'A4', 'A栋四单元', 18, 2, '复式户型', 'system'),
-- B栋单元
(2, 'B1', 'B栋一单元', 15, 6, '小户型', 'system'),
(2, 'B2', 'B栋二单元', 15, 4, '标准户型', 'system'),
(2, 'B3', 'B栋三单元', 15, 3, '大户型', 'system'),
-- C栋单元
(3, 'C1', 'C栋一单元', 12, 8, '单身公寓', 'system'),
(3, 'C2', 'C栋二单元', 12, 6, '一室一厅', 'system'),
-- D栋单元
(4, 'D1', 'D栋一单元', 20, 4, '豪华三房', 'system'),
(4, 'D2', 'D栋二单元', 20, 4, '豪华四房', 'system'),
(4, 'D3', 'D栋三单元', 20, 3, '复式豪宅', 'system'),
(4, 'D4', 'D栋四单元', 20, 2, '顶层复式', 'system'),
(4, 'D5', 'D栋五单元', 20, 6, '精品两房', 'system'),
-- E栋单元
(5, 'E1', 'E栋一单元', 10, 10, '养老公寓', 'system'),
(5, 'E2', 'E栋二单元', 10, 8, '养老公寓', 'system');

-- =============================================
-- 补充房产数据
-- =============================================

INSERT INTO house (building_id, unit_id, house_no, floor, room_number, house_type, building_area, usable_area, house_status, property_owner, remark, create_by) VALUES
-- A栋一单元 (1-4房/层)
(1, 1, 'A001-0101', 1, '0101', '三室两厅', 120.50, 95.00, 2, '张三', '南北通透', 'system'),
(1, 1, 'A001-0102', 1, '0102', '三室两厅', 120.50, 95.00, 2, '李四', '南北通透', 'system'),
(1, 1, 'A001-0103', 1, '0103', '三室两厅', 118.00, 93.00, 2, '王五', '朝南户型', 'system'),
(1, 1, 'A001-0104', 1, '0104', '两室两厅', 95.00, 75.00, 1, NULL, '朝东户型', 'system'),
(1, 1, 'A001-0201', 2, '0201', '三室两厅', 120.50, 95.00, 2, '赵六', '南北通透', 'system'),
(1, 1, 'A001-0202', 2, '0202', '三室两厅', 120.50, 95.00, 2, '钱七', '南北通透', 'system'),
(1, 1, 'A001-0203', 2, '0203', '三室两厅', 118.00, 93.00, 1, NULL, '朝南户型', 'system'),
(1, 1, 'A001-0204', 2, '0204', '两室两厅', 95.00, 75.00, 2, '孙八', '朝东户型', 'system'),

-- A栋二单元
(1, 2, 'A002-0101', 1, '0101', '三室两厅', 120.50, 95.00, 2, '周九', '南北通透', 'system'),
(1, 2, 'A002-0102', 1, '0102', '三室两厅', 120.50, 95.00, 1, NULL, '南北通透', 'system'),
(1, 2, 'A002-0103', 1, '0103', '三室两厅', 118.00, 93.00, 2, '吴十', '朝南户型', 'system'),
(1, 2, 'A002-0104', 1, '0104', '两室两厅', 95.00, 75.00, 2, '郑十一', '朝东户型', 'system'),

-- B栋一单元 (6房/层)
(2, 5, 'B001-0101', 1, '0101', '一室一厅', 55.00, 40.00, 2, '王十二', '朝南单身公寓', 'system'),
(2, 5, 'B001-0102', 1, '0102', '一室一厅', 55.00, 40.00, 2, '李十三', '朝南单身公寓', 'system'),
(2, 5, 'B001-0103', 1, '0103', '两室一厅', 75.00, 55.00, 1, NULL, '标准小户型', 'system'),
(2, 5, 'B001-0104', 1, '0104', '两室一厅', 75.00, 55.00, 1, NULL, '标准小户型', 'system'),
(2, 5, 'B001-0105', 1, '0105', '两室一厅', 78.00, 58.00, 2, '刘十五', '朝东小户型', 'system'),
(2, 5, 'B001-0106', 1, '0106', '两室一厅', 78.00, 58.00, 1, NULL, '朝东小户型', 'system'),

-- C栋一单元 (8房/层)
(3, 8, 'C001-0101', 1, '0101', '单身公寓', 35.00, 25.00, 2, '陈十六', '精装修单身公寓', 'system'),
(3, 8, 'C001-0102', 1, '0102', '单身公寓', 35.00, 25.00, 1, NULL, '精装修单身公寓', 'system'),
(3, 8, 'C001-0103', 1, '0103', '单身公寓', 35.00, 25.00, 2, '杨十七', '精装修单身公寓', 'system'),
(3, 8, 'C001-0104', 1, '0104', '单身公寓', 38.00, 28.00, 1, NULL, '朝南单身公寓', 'system'),
(3, 8, 'C001-0105', 1, '0105', '单身公寓', 38.00, 28.00, 2, '黄十八', '朝南单身公寓', 'system'),
(3, 8, 'C001-0106', 1, '0106', '单身公寓', 38.00, 28.00, 1, NULL, '朝南单身公寓', 'system'),
(3, 8, 'C001-0107', 1, '0107', '一室一厅', 45.00, 32.00, 2, '赵十九', '标准单身公寓', 'system'),
(3, 8, 'C001-0108', 1, '0108', '一室一厅', 45.00, 32.00, 1, NULL, '标准单身公寓', 'system'),

-- 更多房产数据...
(4, 10, 'D001-0101', 1, '0101', '四室两厅', 150.00, 120.00, 2, '周二十', '豪华四房', 'system'),
(4, 10, 'D001-0102', 1, '0102', '四室两厅', 150.00, 120.00, 1, NULL, '豪华四房', 'system'),
(4, 10, 'D001-0103', 1, '0103', '四室两厅', 145.00, 115.00, 2, '吴二十一', '豪华四房', 'system'),
(4, 10, 'D001-0104', 1, '0104', '四室两厅', 145.00, 115.00, 1, NULL, '豪华四房', 'system');

-- =============================================
-- 补充用户房产关联数据
-- =============================================

INSERT INTO user_house (user_id, house_id, relation_type, start_date, end_date, is_current, remark, create_by) VALUES
-- 张三的房产
(3, 1, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 李四的房产
(4, 2, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 王五的房产
(5, 3, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 赵六的房产
(6, 5, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 钱七的房产
(7, 6, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 孙八的房产
(8, 8, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 周九的房产
(9, 9, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 吴十的房产
(10, 11, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 郑十一的房产
(11, 12, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 王十二的房产
(12, 13, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 李十三的房产
(13, 14, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 刘十五的房产
(14, 17, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 陈十六的房产
(15, 18, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 杨十七的房产
(16, 20, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 黄十八的房产
(17, 21, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 赵十九的房产
(18, 23, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 周二十的房产
(19, 25, 1, '2021-01-01', NULL, 1, '业主房产', 'system'),
-- 吴二十一的房产
(20, 27, 1, '2021-01-01', NULL, 1, '业主房产', 'system');

-- =============================================
-- 补充业主的详细信息（更新sys_user表）
-- =============================================

UPDATE sys_user SET
    id_card = '110101199001011234',
    email = 'zhangsan@example.com',
    gender = 1,
    resident_type = 1,
    check_in_date = '2021-01-01',
    emergency_contact = '张父',
    emergency_phone = '13900139001'
WHERE username = 'owner001';

UPDATE sys_user SET
    id_card = '110101199002022345',
    email = 'lisi@example.com',
    gender = 1,
    resident_type = 1,
    check_in_date = '2021-01-01',
    emergency_contact = '李母',
    emergency_phone = '13900139002'
WHERE username = 'owner002';

UPDATE sys_user SET
    id_card = '110101199003033456',
    email = 'wangwu@example.com',
    gender = 1,
    resident_type = 1,
    check_in_date = '2021-01-01',
    emergency_contact = '王妻',
    emergency_phone = '13900139003'
WHERE username = 'owner003';

-- 为更多业主补充信息
UPDATE sys_user SET
    gender = CASE
        WHEN username IN ('owner001', 'owner002', 'owner003', 'owner004', 'owner005', 'owner006', 'owner007', 'owner008', 'owner009', 'owner010') THEN 1
        WHEN username IN ('owner011', 'owner012', 'owner013', 'owner014', 'owner015', 'owner016', 'owner017', 'owner018', 'owner019', 'owner020') THEN 1
        ELSE 0
    END,
    resident_type = 1,
    check_in_date = '2021-01-01'
WHERE username LIKE 'owner%';

-- =============================================
-- 补充费用类型数据
-- =============================================

INSERT INTO fee_type (type_name, type_code, unit_price, billing_unit, billing_cycle, description, status, create_by) VALUES
('物业管理费', 'property_management', 2.50, '平方米/月', 1, '基础物业服务费', 1, 'system'),
('电梯维护费', 'elevator_maintenance', 0.80, '平方米/月', 1, '电梯日常维护保养', 1, 'system'),
('公共照明费', 'public_lighting', 15.00, '元/月', 1, '楼道公共区域照明', 1, 'system'),
('垃圾清运费', 'garbage_disposal', 20.00, '元/月', 1, '生活垃圾清运处理', 1, 'system'),
('绿化养护费', 'landscaping', 0.50, '平方米/月', 1, '小区绿化日常养护', 1, 'system'),
('安保服务费', 'security_service', 0.30, '平方米/月', 1, '24小时安保巡逻服务', 1, 'system');

-- =============================================
-- 补充菜单权限数据
-- =============================================

INSERT INTO sys_menu (menu_name, parent_id, menu_type, path, perms, icon, order_num, status, create_by) VALUES
('物业管理', 0, 'M', '/property', NULL, 'building', 1, 1, 'system'),
('楼栋管理', (SELECT id FROM sys_menu WHERE menu_name = '物业管理' AND parent_id = 0), 'C', '/property/building', 'property:building:list', 'build', 1, 1, 'system'),
('单元管理', (SELECT id FROM sys_menu WHERE menu_name = '物业管理' AND parent_id = 0), 'C', '/property/unit', 'property:unit:list', 'component', 2, 1, 'system'),
('房产管理', (SELECT id FROM sys_menu WHERE menu_name = '物业管理' AND parent_id = 0), 'C', '/property/house', 'property:house:list', 'home', 3, 1, 'system'),
('业主管理', (SELECT id FROM sys_menu WHERE menu_name = '物业管理' AND parent_id = 0), 'C', '/property/owner', 'property:owner:list', 'peoples', 4, 1, 'system');

-- 为管理员分配物业管理权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM sys_role r, sys_menu m
WHERE r.role_key = 'admin' AND m.menu_name IN ('物业管理', '楼栋管理', '单元管理', '房产管理', '业主管理');

-- 为物业管理员分配物业管理权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM sys_role r, sys_menu m
WHERE r.role_key = 'manager' AND m.menu_name IN ('物业管理', '楼栋管理', '单元管理', '房产管理', '业主管理');

-- =============================================
-- 补充一些公告数据
-- =============================================

INSERT INTO notice (notice_title, notice_type, notice_content, is_top, publish_scope, publisher_id, publisher_name, publish_time, create_by) VALUES
('春节期间物业服务安排通知', 'notice', '亲爱的业主朋友们：\n\n春节期间（2月10日-2月17日），物业服务正常进行。具体安排如下：\n1. 24小时客服热线保持畅通\n2. 安保巡逻正常进行\n3. 紧急维修服务正常\n4. 非紧急维修2月18日恢复正常\n\n祝大家春节快乐，阖家幸福！', 1, 1, 2, '物业管理员A', '2024-02-01 09:00:00', 'manager01'),
('小区绿化养护通知', 'reminder', '为提升小区环境质量，物业将于本周末对小区绿化进行全面养护，包括修剪草坪、清理枯枝、施肥等工作。施工期间请注意安全，给您带来的不便敬请谅解。', 0, 1, 2, '物业管理员A', '2024-02-15 14:30:00', 'manager01'),
('电梯年度检验通知', 'policy', '根据特种设备安全法规定，本小区电梯将于下月进行年度检验。检验期间部分电梯可能暂停使用，具体时间安排将另行通知。请各位业主提前做好出行安排。', 0, 1, 2, '物业管理员A', '2024-02-20 10:15:00', 'manager01');

-- =============================================
-- 补充一些投诉数据
-- =============================================

INSERT INTO complaint (complaint_no, user_id, user_name, house_id, house_no, complaint_type, complaint_content, urgency_level, complaint_status, create_time) VALUES
('CMP202402010001', 3, '张三', 1, 'A001-0101', 'sanitation', '楼下垃圾桶经常满溢，希望物业能增加清理频次，特别是在周末。', 1, 3, '2024-02-01 08:30:00'),
('CMP202402020002', 4, '李四', 2, 'A001-0102', 'noise', '楼上住户经常在深夜制造噪音，影响休息，希望物业能协调解决。', 2, 2, '2024-02-02 22:15:00'),
('CMP202402030003', 5, '王五', 3, 'A001-0103', 'facility_damage', '单元门禁系统损坏，无法正常刷卡开门，存在安全隐患。', 2, 1, '2024-02-03 16:45:00');

-- =============================================
-- 补充一些维修工单数据
-- =============================================

INSERT INTO repair_order (order_no, user_id, user_name, house_id, house_no, repair_type, fault_description, urgency_level, order_status, create_time) VALUES
('RO202402010001', 8, '孙八', 8, 'A001-0204', 'water_electricity', '厨房水龙头漏水，需要维修更换。', 1, 5, '2024-02-01 10:20:00'),
('RO202402020002', 9, '周九', 9, 'A002-0101', 'door_window', '卧室窗户无法正常关闭，漏风严重。', 1, 4, '2024-02-02 14:30:00'),
('RO202402030003', 10, '吴十', 11, 'A002-0103', 'public_facility', '楼道灯不亮，需要更换灯泡。', 1, 2, '2024-02-03 09:15:00');

-- 更新维修工单的处理状态
UPDATE repair_order SET
    worker_id = 4,
    worker_name = '维修人员A',
    assign_time = '2024-02-01 11:00:00',
    finish_time = '2024-02-01 12:30:00',
    repair_content = '已更换厨房水龙头，测试无漏水现象。',
    repair_cost = 85.00
WHERE order_no = 'RO202402010001';

UPDATE repair_order SET
    worker_id = 5,
    worker_name = '维修人员B',
    assign_time = '2024-02-02 15:00:00',
    acceptance_time = '2024-02-03 10:00:00',
    acceptance_result = 2,
    acceptance_rating = 3,
    acceptance_comment = '窗户关闭仍有异响，需要进一步调整。'
WHERE order_no = 'RO202402020002';

UPDATE repair_order SET
    worker_id = 4,
    worker_name = '维修人员A',
    assign_time = '2024-02-03 10:00:00',
    required_finish_time = '2024-02-03 18:00:00'
WHERE order_no = 'RO202402030003';

-- =============================================
-- 补充一些账单数据
-- =============================================

INSERT INTO bill (bill_no, user_id, house_id, fee_type_id, fee_type_name, billing_period, amount, paid_amount, bill_status, due_date, create_by) VALUES
('BILL2024010001', 3, 1, 1, '物业管理费', '2024-01', 301.25, 301.25, 2, '2024-01-31', 'system'),
('BILL2024010002', 4, 2, 1, '物业管理费', '2024-01', 301.25, 0.00, 1, '2024-01-31', 'system'),
('BILL2024010003', 5, 3, 1, '物业管理费', '2024-01', 295.00, 295.00, 2, '2024-01-31', 'system'),
('BILL2024010004', 6, 5, 1, '物业管理费', '2024-01', 301.25, 0.00, 1, '2024-01-31', 'system'),
('BILL2024010005', 7, 6, 1, '物业管理费', '2024-01', 301.25, 301.25, 2, '2024-01-31', 'system'),
('BILL2024020001', 3, 1, 1, '物业管理费', '2024-02', 301.25, 0.00, 1, '2024-02-29', 'system'),
('BILL2024020002', 4, 2, 1, '物业管理费', '2024-02', 301.25, 0.00, 1, '2024-02-29', 'system'),
('BILL2024020003', 5, 3, 1, '物业管理费', '2024-02', 295.00, 295.00, 2, '2024-02-29', 'system');

-- 补充缴费记录
INSERT INTO payment_record (payment_no, bill_id, bill_no, user_id, amount, pay_method, payment_time, remark) VALUES
('PAY2024010001', 1, 'BILL2024010001', 3, 301.25, 1, '2024-01-25 14:30:00', '钱包支付'),
('PAY2024010002', 3, 'BILL2024010003', 5, 295.00, 1, '2024-01-28 10:15:00', '钱包支付'),
('PAY2024010003', 5, 'BILL2024010005', 7, 301.25, 1, '2024-01-30 16:45:00', '钱包支付'),
('PAY2024020001', 7, 'BILL2024020003', 5, 295.00, 1, '2024-02-15 09:20:00', '钱包支付');

-- 更新账单的缴费状态
UPDATE bill SET
    paid_time = '2024-01-25 14:30:00'
WHERE bill_no = 'BILL2024010001';

UPDATE bill SET
    paid_time = '2024-01-28 10:15:00'
WHERE bill_no = 'BILL2024010003';

UPDATE bill SET
    paid_time = '2024-01-30 16:45:00'
WHERE bill_no = 'BILL2024010005';

UPDATE bill SET
    paid_time = '2024-02-15 09:20:00'
WHERE bill_no = 'BILL2024020003';

-- =============================================
-- 初始化完成提示
-- =============================================

SELECT '物业管理系统初始化数据补充完成！' AS message,
       '已补充楼栋、单元、房产、用户房产关联、业主详细信息、费用类型、菜单权限、公告、投诉、维修工单、账单等数据' AS details;