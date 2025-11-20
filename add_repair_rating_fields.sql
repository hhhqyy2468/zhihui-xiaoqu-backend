-- 为 repair_order 表添加评价相关字段
-- 执行时间：2025-11-20

USE property_management;

ALTER TABLE repair_order
ADD COLUMN service_rating TINYINT NULL COMMENT '服务评分 1-5星',
ADD COLUMN response_rating TINYINT NULL COMMENT '响应速度评分 1-5星',
ADD COLUMN professional_rating TINYINT NULL COMMENT '专业程度评分 1-5星',
ADD COLUMN overall_rating TINYINT NULL COMMENT '总体评分 1-5星',
ADD COLUMN comment VARCHAR(500) NULL COMMENT '业主评价意见',
ADD COLUMN rating_time DATETIME NULL COMMENT '评价时间';

-- 为新字段添加索引（可选）
CREATE INDEX idx_service_rating ON repair_order (service_rating);
CREATE INDEX idx_overall_rating ON repair_order (overall_rating);
CREATE INDEX idx_rating_time ON repair_order (rating_time);