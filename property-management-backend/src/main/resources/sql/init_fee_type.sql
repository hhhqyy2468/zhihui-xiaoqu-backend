-- 初始化费用类型基础数据
INSERT INTO fee_type (type_name, type_code, unit_price, billing_unit, billing_cycle, description, status, create_by, create_time) VALUES
('物业费', 'PROPERTY_FEE', 2.80, '元/平方米·月', 1, '小区日常维护、保洁、安保、绿化等基础服务费用', 1, 'system', NOW()),
('车位管理费', 'PARKING_MANAGEMENT_FEE', 150.00, '元/月·位', 1, '车位日常管理、维护、照明、监控等费用', 1, 'system', NOW()),
('生活垃圾清运费', 'GARBAGE_COLLECTION_FEE', 15.00, '元/月·户', 1, '生活垃圾收集、清运、处理费用', 1, 'system', NOW()),
('电梯使用费', 'ELEVATOR_USAGE_FEE', 35.00, '元/月·户', 1, '电梯运行、维护保养、年检、电费等', 1, 'system', NOW()),
('二次供水加压费', 'WATER_PRESSURE_FEE', 8.00, '元/月·户', 1, '高层住宅二次供水设备运行、维护费用', 1, 'system', NOW()),
('公共能耗费', 'PUBLIC_ENERGY_FEE', 25.00, '元/月·户', 1, '公共区域照明、消防系统、监控设备等电费分摊', 1, 'system', NOW()),
('房屋专项维修资金', 'HOUSING_MAINTENANCE_FUND', 1200.00, '元/年·套', 3, '房屋共用部位、共用设施设备维修资金', 1, 'system', NOW()),
('智能安防费', 'SMART_SECURITY_FEE', 20.00, '元/月·户', 1, '门禁系统、监控设备、智能安防系统维护费', 1, 'system', NOW()),
('公共区域清洁费', 'PUBLIC_CLEANING_FEE', 18.00, '元/月·户', 1, '大堂、楼道、公共区域日常清洁消毒费用', 1, 'system', NOW()),
('绿化养护费', 'LANDSCAPING_FEE', 12.00, '元/月·户', 1, '小区绿化植物养护、草坪修剪、病虫害防治等', 1, 'system', NOW());

-- 添加索引
CREATE INDEX idx_fee_type_code ON fee_type(type_code);
CREATE INDEX idx_fee_type_status ON fee_type(status);
CREATE INDEX idx_fee_type_deleted ON fee_type(deleted);