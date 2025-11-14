-- =============================================
-- 补充系统用户初始化数据
-- 确保系统管理员账号存在
-- =============================================

USE property_management;

-- 插入系统管理员用户（如果不存在）
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, resident_type, resident_status, create_by, create_time) VALUES
(1, 'admin', '$2a$10$7JB720yubVSOfvVXbf5CZO0ZYIa9a/f4OiQHt7S5bqNByxVAc5Z8u', '系统管理员', '13800138000', 'admin@example.com', 1, 1, 1, 1, 'system', NOW()),
(2, 'manager01', '$2a$10$7JB720yubVSOfvVXbf5CZO0ZYIa9a/f4OiQHt7S5bqNByxVAc5Z8u', '物业管理员A', '13800138001', 'manager01@example.com', 2, 1, 1, 1, 'system', NOW());

-- 插入测试用的业主用户（如果不存在）
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, resident_type, resident_status, create_by, create_time) VALUES
(3, 'owner001', '$2a$10$7JB720yubVSOfvVXbf5CZO0ZYIa9a/f4OiQHt7S5bqNByxVAc5Z8u', '张三', '13800138003', 'zhangsan@example.com', 3, 1, 1, 1, 'system', NOW()),
(4, 'owner002', '$2a$10$7JB720yubVSOfvVXbf5CZO0ZYIa9a/f4OiQHt7S5bqNByxVAc5Z8u', '李四', '13800138004', 'lisi@example.com', 3, 1, 1, 1, 'system', NOW()),
(5, 'owner003', '$2a$10$7JB720yubVSOfvVXbf5CZO0ZYIa9a/f4OiQHt7S5bqNByxVAc5Z8u', '王五', '13800138005', 'wangwu@example.com', 3, 1, 1, 1, 'system', NOW());

-- 确保管理员角色存在
INSERT IGNORE INTO sys_role (id, role_name, role_key, role_sort, status, create_by, create_time) VALUES
(1, '系统管理员', 'admin', 1, 1, 'system', NOW()),
(2, '物业管理员', 'manager', 2, 1, 'system', NOW());

-- 为用户分配角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> admin
(2, 2);  -- manager01 -> manager

-- 为管理员分配所有菜单权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 为物业管理员分配物业管理相关权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE menu_name IN ('物业管理', '楼栋管理', '单元管理', '房产管理', '业主管理');

-- 更新现有业主用户的信息，确保user_type正确
UPDATE sys_user SET user_type = 3 WHERE username LIKE 'owner%' AND user_type != 3;

-- 确保系统管理员用户状态正常
UPDATE sys_user SET status = 1 WHERE username = 'admin' AND status != 1;