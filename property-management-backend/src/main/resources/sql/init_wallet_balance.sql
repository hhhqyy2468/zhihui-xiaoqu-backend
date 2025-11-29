-- 初始化业主钱包，每个业主初始余额10000元
-- 为所有用户类型为3（业主）的用户创建钱包

INSERT INTO wallet (user_id, balance, total_recharge, total_consume, status, version, create_time, update_time, password_status)
SELECT
    id as user_id,
    10000.00 as balance,
    10000.00 as total_recharge,
    0.00 as total_consume,
    1 as status,
    0 as version,
    NOW() as create_time,
    NOW() as update_time,
    0 as password_status  -- 初始状态：未设置支付密码
FROM sys_user
WHERE user_type = 3
AND deleted = 0
AND status = 1
AND id NOT IN (SELECT user_id FROM wallet WHERE user_id IS NOT NULL);

-- 为每个创建的钱包生成初始充值交易记录
INSERT INTO wallet_transaction (
    transaction_no,
    user_id,
    wallet_id,
    transaction_type,
    amount,
    balance_before,
    balance_after,
    transaction_status,
    remark,
    create_time
)
SELECT
    CONCAT('WAL', UNIX_TIMESTAMP(), LPAD(u.id, 6, '0')) as transaction_no,
    u.id as user_id,
    w.id as wallet_id,
    1 as transaction_type, -- 充值
    10000.00 as amount,
    0.00 as balance_before,
    10000.00 as balance_after,
    1 as transaction_status, -- 成功
    '系统初始化钱包余额' as remark,
    NOW() as create_time
FROM sys_user u
JOIN wallet w ON u.id = w.user_id
WHERE u.user_type = 3
AND u.deleted = 0
AND u.status = 1
AND NOT EXISTS (
    SELECT 1 FROM wallet_transaction wt
    WHERE wt.wallet_id = w.id
    AND wt.remark = '系统初始化钱包余额'
);

-- 显示初始化结果
SELECT
    COUNT(*) as total_wallets_created,
    SUM(balance) as total_balance_initialized
FROM wallet w
JOIN sys_user u ON w.user_id = u.id
WHERE u.user_type = 3 AND u.deleted = 0 AND u.status = 1;