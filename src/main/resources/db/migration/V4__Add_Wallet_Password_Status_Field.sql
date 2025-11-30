-- V4__Add_Wallet_Password_Status_Field.sql
-- 添加支付密码状态字段，用于准确判断支付密码设置状态
-- 字段：password_status (0: 未设置, 1: 已设置)

-- 为wallet表添加password_status字段
ALTER TABLE wallet ADD COLUMN password_status TINYINT DEFAULT 0 COMMENT '支付密码状态：0-未设置，1-已设置';

-- 根据现有数据更新password_status字段
UPDATE wallet SET password_status = 1 WHERE pay_password IS NOT NULL AND pay_password != '';

-- 为password_status字段添加索引以提升查询性能
CREATE INDEX idx_wallet_password_status ON wallet(password_status);