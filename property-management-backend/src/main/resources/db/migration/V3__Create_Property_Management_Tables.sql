-- 物业管理模块相关表

-- 楼栋信息表
CREATE TABLE `property_building` (
  `building_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `building_code` varchar(20) NOT NULL COMMENT '楼栋编号',
  `building_name` varchar(50) NOT NULL COMMENT '楼栋名称',
  `floors` int(2) NOT NULL COMMENT '楼层数',
  `units` int(2) NOT NULL COMMENT '单元数',
  `total_area` decimal(10,2) NOT NULL COMMENT '总建筑面积',
  `address` varchar(200) NOT NULL COMMENT '详细地址',
  `build_year` int(4) NOT NULL COMMENT '建筑年份',
  `structure_type` varchar(50) DEFAULT NULL COMMENT '结构类型',
  `usage_type` varchar(50) DEFAULT NULL COMMENT '使用类型',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1正常）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`building_id`),
  UNIQUE KEY `uk_building_code` (`building_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='楼栋信息表';

-- 单元信息表
CREATE TABLE `property_unit` (
  `unit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `building_id` bigint(20) NOT NULL COMMENT '楼栋ID',
  `unit_code` varchar(20) NOT NULL COMMENT '单元编号',
  `unit_name` varchar(50) NOT NULL COMMENT '单元名称',
  `floors` int(2) NOT NULL COMMENT '楼层数',
  `rooms_per_floor` int(2) NOT NULL COMMENT '每层房间数',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1正常）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`unit_id`),
  KEY `fk_unit_building_id` (`building_id`),
  UNIQUE KEY `uk_building_unit_code` (`building_id`, `unit_code`),
  CONSTRAINT `fk_unit_building_id` FOREIGN KEY (`building_id`) REFERENCES `property_building` (`building_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='单元信息表';

-- 房产信息表
CREATE TABLE `property_house` (
  `house_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '房产ID',
  `building_id` bigint(20) NOT NULL COMMENT '楼栋ID',
  `unit_id` bigint(20) NOT NULL COMMENT '单元ID',
  `floor_num` int(2) NOT NULL COMMENT '楼层',
  `room_num` varchar(10) NOT NULL COMMENT '房间号',
  `house_type` varchar(50) DEFAULT NULL COMMENT '户型',
  `build_area` decimal(10,2) NOT NULL COMMENT '建筑面积',
  `use_area` decimal(10,2) DEFAULT NULL COMMENT '使用面积',
  `house_status` int(1) NOT NULL DEFAULT '1' COMMENT '房产状态（1空置 2已售 3已租 4自住）',
  `owner_id` bigint(20) DEFAULT NULL COMMENT '产权人ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`house_id`),
  KEY `fk_house_building_id` (`building_id`),
  KEY `fk_house_unit_id` (`unit_id`),
  KEY `fk_house_owner_id` (`owner_id`),
  UNIQUE KEY `uk_unit_floor_room` (`unit_id`, `floor_num`, `room_num`),
  CONSTRAINT `fk_house_building_id` FOREIGN KEY (`building_id`) REFERENCES `property_building` (`building_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_house_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `property_unit` (`unit_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='房产信息表';

-- 业主信息表
CREATE TABLE `property_owner` (
  `owner_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业主ID',
  `owner_name` varchar(20) NOT NULL COMMENT '业主姓名',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `owner_type` int(1) NOT NULL DEFAULT '1' COMMENT '业主类型（1业主 2租户）',
  `owner_status` int(1) NOT NULL DEFAULT '1' COMMENT '业主状态（1正常 2搬离）',
  `move_in_date` datetime DEFAULT NULL COMMENT '入住日期',
  `move_out_date` datetime DEFAULT NULL COMMENT '搬离日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`owner_id`),
  UNIQUE KEY `uk_owner_phone` (`phone`),
  UNIQUE KEY `uk_owner_id_card` (`id_card`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='业主信息表';

-- 业主房产关联表
CREATE TABLE `property_owner_house` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `house_id` bigint(20) NOT NULL COMMENT '房产ID',
  `relation_type` int(1) NOT NULL DEFAULT '1' COMMENT '关系类型（1产权人 2使用人）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_oh_owner_id` (`owner_id`),
  KEY `fk_oh_house_id` (`house_id`),
  UNIQUE KEY `uk_owner_house_relation` (`owner_id`, `house_id`, `relation_type`),
  CONSTRAINT `fk_oh_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `property_owner` (`owner_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_oh_house_id` FOREIGN KEY (`house_id`) REFERENCES `property_house` (`house_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='业主房产关联表';

-- 插入初始数据

-- 插入楼栋信息
INSERT INTO `property_building` VALUES (1, 'B001', '1号楼', 18, 3, 5400.50, '北京市朝阳区XX街道XX号1号楼', 2020, '钢筋混凝土', '住宅', 1, '1号楼基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_building` VALUES (2, 'B002', '2号楼', 18, 4, 6000.80, '北京市朝阳区XX街道XX号2号楼', 2020, '钢筋混凝土', '住宅', 1, '2号楼基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_building` VALUES (3, 'B003', '3号楼', 12, 2, 3600.30, '北京市朝阳区XX街道XX号3号楼', 2019, '钢筋混凝土', '商业', 1, '3号楼基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);

-- 插入单元信息
INSERT INTO `property_unit` VALUES (1, 1, 'U001', '1单元', 18, 4, 1, '1单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (2, 1, 'U002', '2单元', 18, 4, 1, '2单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (3, 1, 'U003', '3单元', 18, 4, 1, '3单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (4, 2, 'U001', '1单元', 18, 6, 1, '2号楼1单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (5, 2, 'U002', '2单元', 18, 6, 1, '2号楼2单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (6, 2, 'U003', '3单元', 18, 6, 1, '2号楼3单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (7, 2, 'U004', '4单元', 18, 6, 1, '2号楼4单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (8, 3, 'U001', '1单元', 12, 8, 1, '3号楼1单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_unit` VALUES (9, 3, 'U002', '2单元', 12, 8, 1, '3号楼2单元基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);

-- 插入房产信息（1号楼1单元部分房产）
INSERT INTO `property_house` VALUES (1, 1, 1, 1, '101', '三室两厅', 128.50, 105.30, 4, 1, '101室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_house` VALUES (2, 1, 1, 1, '102', '三室两厅', 128.50, 105.30, 4, 1, '102室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_house` VALUES (3, 1, 1, 1, '103', '三室两厅', 128.50, 105.30, 4, 2, '103室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_house` VALUES (4, 1, 1, 1, '104', '三室两厅', 128.50, 105.30, 4, 3, '104室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_house` VALUES (5, 1, 1, 2, '201', '三室两厅', 128.50, 105.30, 4, 4, '201室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_house` VALUES (6, 1, 1, 2, '202', '三室两厅', 128.50, 105.30, 1, NULL, '202室基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);

-- 插入业主信息
INSERT INTO `property_owner` VALUES (1, '张三', '13800138001', '110101199001011234', 'zhangsan@example.com', 1, 1, '2020-01-01', NULL, '张三基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_owner` VALUES (2, '李四', '13800138002', '110101199001011235', 'lisi@example.com', 1, 1, '2020-03-15', NULL, '李四基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_owner` VALUES (3, '王五', '13800138003', '110101199001011236', 'wangwu@example.com', 1, 1, '2020-06-20', NULL, '王五基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);
INSERT INTO `property_owner` VALUES (4, '赵六', '13800138004', '110101199001011237', 'zhaoliu@example.com', 2, 1, '2020-08-10', NULL, '赵六基本信息', 'admin', '2025-01-01 00:00:00', '', NULL);

-- 插入业主房产关联信息
INSERT INTO `property_owner_house` VALUES (1, 1, 1, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (2, 1, 2, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (3, 2, 3, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (4, 3, 4, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (5, 4, 5, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (6, 4, 6, 2, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (7, 2, 1, 1, '2025-01-01 00:00:00');
INSERT INTO `property_owner_house` VALUES (8, 3, 2, 2, '2025-01-01 00:00:00');