-- 插入车位租赁费类型
INSERT INTO fee_type (type_name, type_code, unit_price, billing_unit, billing_cycle, description, status, create_by, create_time)
VALUES ('车位租赁费', 'PARKING_RENTAL_FEE', 0.00, '元/月', 1, '车位租赁费用，根据租赁合同生成', 1, 'system', NOW())
ON DUPLICATE KEY UPDATE
  type_name = VALUES(type_name),
  unit_price = VALUES(unit_price),
  billing_unit = VALUES(billing_unit),
  description = VALUES(description),
  update_time = NOW();
