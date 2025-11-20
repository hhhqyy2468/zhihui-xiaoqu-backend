-- 添加维修评价权限并分配给业主角色
-- 执行时间：2025-11-20

USE property_management;

-- 1. 添加维修评价权限菜单项
INSERT INTO sys_menu (menu_name, parent_id, menu_type, path, perms, icon, order_num, status, create_by, create_time, update_by, update_time)
VALUES ('维修评价', 23, 'F', NULL, 'property:repair:rate', 'star', 32, 0, 'admin', NOW(), 'admin', NOW());

-- 2. 获取刚插入的菜单ID（应该是最新的ID）
SET @repair_rate_menu_id = LAST_INSERT_ID();

-- 3. 给业主角色（假设角色ID为3）添加维修评价权限
-- 先检查是否已有权限关系，避免重复插入
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 3, @repair_rate_menu_id
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu
    WHERE role_id = 3 AND menu_id = @repair_rate_menu_id
);

-- 4. 验证权限是否添加成功
SELECT
    m.id,
    m.menu_name,
    m.perms,
    sr.id as role_id,
    sr.role_name
FROM sys_menu m
JOIN sys_role_menu rm ON m.id = rm.menu_id
JOIN sys_role sr ON rm.role_id = sr.id
WHERE m.perms = 'property:repair:rate';