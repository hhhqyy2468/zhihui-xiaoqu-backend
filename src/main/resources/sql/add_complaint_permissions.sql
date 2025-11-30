-- 添加投诉管理菜单和权限

-- 添加投诉管理主菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `perms`, `icon`, `order_num`, `status`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES ('投诉管理', 22, 'C', 'complaint', 'property:complaint:list', 'documentation', 3, 1, 'admin', NOW(), 'admin', NOW());

-- 获取刚插入的投诉管理菜单ID（这里假设是34，实际情况可能需要调整）
SET @complaint_menu_id = LAST_INSERT_ID();

-- 添加投诉管理子菜单（功能按钮）
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `perms`, `icon`, `order_num`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('投诉查询', @complaint_menu_id, 'F', NULL, 'property:complaint:list', '#', 1, 1, 'admin', NOW(), 'admin', NOW()),
('投诉新增', @complaint_menu_id, 'F', NULL, 'property:complaint:add', '#', 2, 1, 'admin', NOW(), 'admin', NOW()),
('投诉修改', @complaint_menu_id, 'F', NULL, 'property:complaint:edit', '#', 3, 1, 'admin', NOW(), 'admin', NOW()),
('投诉删除', @complaint_menu_id, 'F', NULL, 'property:complaint:remove', '#', 4, 1, 'admin', NOW(), 'admin', NOW()),
('投诉派单', @complaint_menu_id, 'F', NULL, 'property:complaint:assign', '#', 5, 1, 'admin', NOW(), 'admin', NOW()),
('投诉处理', @complaint_menu_id, 'F', NULL, 'property:complaint:handle', '#', 6, 1, 'admin', NOW(), 'admin', NOW()),
('投诉评价', @complaint_menu_id, 'F', NULL, 'property:complaint:rate', '#', 7, 1, 'admin', NOW(), 'admin', NOW()),
('我的投诉', @complaint_menu_id, 'F', NULL, 'property:complaint:my', '#', 8, 1, 'admin', NOW(), 'admin', NOW()),
('投诉上传', @complaint_menu_id, 'F', NULL, 'property:complaint:upload', '#', 9, 1, 'admin', NOW(), 'admin', NOW());

-- 为系统管理员分配投诉管理权限（role_id = 1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu` WHERE perms LIKE 'property:complaint:%' AND id NOT IN (
    SELECT menu_id FROM `sys_role_menu` WHERE role_id = 1
);

-- 为物业管理员分配投诉管理权限（role_id = 2）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 2, id FROM `sys_menu` WHERE perms IN (
    'property:complaint:list',
    'property:complaint:add',
    'property:complaint:edit',
    'property:complaint:remove',
    'property:complaint:assign',
    'property:complaint:handle',
    'property:complaint:upload'
) AND perms LIKE 'property:complaint:%' AND id NOT IN (
    SELECT menu_id FROM `sys_role_menu` WHERE role_id = 2
);

-- 为业主分配投诉管理权限（role_id = 3）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 3, id FROM `sys_menu` WHERE perms IN (
    'property:complaint:list',
    'property:complaint:add',
    'property:complaint:my',
    'property:complaint:rate',
    'property:complaint:upload'
) AND perms LIKE 'property:complaint:%' AND id NOT IN (
    SELECT menu_id FROM `sys_role_menu` WHERE role_id = 3
);

-- 为维修人员分配投诉管理权限（role_id = 4）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 4, id FROM `sys_menu` WHERE perms IN (
    'property:complaint:list',
    'property:complaint:handle'
) AND perms LIKE 'property:complaint:%' AND id NOT IN (
    SELECT menu_id FROM `sys_role_menu` WHERE role_id = 4
);