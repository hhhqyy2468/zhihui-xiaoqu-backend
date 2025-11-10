# 社区物业管理系统 - 后端项目

## 项目简介

本项目是社区物业管理系统的后端服务，基于Spring Boot 3.x + Spring Security + MyBatis Plus + Redis + JWT构建。

## 技术栈

- **Spring Boot 3.5.7** - 基础框架
- **Spring Security** - 安全认证框架
- **MyBatis Plus 3.5.3** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **JWT** - Token认证
- **Knife4j** - API文档
- **Lombok** - 简化开发
- **Hutool** - 工具类库

## 项目结构

```
src/main/java/com/hyu/
├── common/                  # 公共模块
│   ├── core/               # 核心类
│   │   └── domain/         # 通用实体类
│   ├── domain/             # 通用领域对象
│   ├── exception/          # 异常处理
│   └── utils/              # 工具类
├── controller/             # 控制器层
├── framework/              # 框架配置
│   ├── config/             # 配置类
│   ├── security/           # 安全相关
│   └── web/                # Web配置
└── system/                 # 系统管理模块
    ├── domain/             # 实体类
    ├── mapper/             # 数据访问层
    └── service/            # 业务逻辑层
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库配置

创建数据库 `property_management`，执行以下SQL：

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `user_id`        BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`       VARCHAR(50)     NOT NULL                COMMENT '用户名',
    `password`       VARCHAR(100)    NOT NULL                COMMENT '密码',
    `real_name`      VARCHAR(20)     NOT NULL                COMMENT '真实姓名',
    `phone`          VARCHAR(11)     NOT NULL                COMMENT '手机号',
    `email`          VARCHAR(50)                             COMMENT '邮箱',
    `user_type`      TINYINT(1)      NOT NULL DEFAULT 3      COMMENT '用户类型 1:管理员 2:物业管理员 3:业主 4:维修人员',
    `status`         TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '状态 0:禁用 1:正常',
    `dept_id`        BIGINT(20)                              COMMENT '部门ID',
    `avatar`         VARCHAR(255)                            COMMENT '头像URL',
    `last_login_time` DATETIME                               COMMENT '最后登录时间',
    `last_login_ip`  VARCHAR(50)                             COMMENT '最后登录IP',
    `remark`         VARCHAR(500)                            COMMENT '备注',
    `create_by`      VARCHAR(64)                             COMMENT '创建者',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)                             COMMENT '更新者',
    `update_time`    DATETIME                                 ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 创建管理员用户（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`, `dept_id`)
VALUES ('admin', '$2a$10$7JB720yubVSOfvVWbfXCOOxjTOQcQjmrJF1ZM4nAVccp/.rkMlDWy', '系统管理员', '13800138000', 'admin@example.com', 1, 1, 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);
```

### 3. 修改配置

修改 `src/main/resources/application.yml` 中的数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/property_management?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 4. 启动项目

**方式一：使用Maven启动**
```bash
mvn spring-boot:run
```

**方式二：使用启动脚本（Windows）**
```bash
start-dev.bat
```

### 5. 访问接口

项目启动后，可以通过以下地址访问：

- **项目地址**：http://localhost:8080
- **API文档**：http://localhost:8080/doc.html
- **接口前缀**：/api/v1

## 认证模块接口

### 1. 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "captcha": "ABCD",
  "rememberMe": false
}
```

### 2. 获取验证码
```http
GET /api/v1/auth/captcha
```

### 3. 刷新Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your_refresh_token"
}
```

### 4. 用户登出
```http
POST /api/v1/auth/logout
Authorization: Bearer your_token
```

### 5. 获取用户信息
```http
GET /api/v1/auth/info
Authorization: Bearer your_token
```

### 6. 修改密码
```http
PUT /api/v1/auth/password
Authorization: Bearer your_token
Content-Type: application/json

{
  "oldPassword": "old_password",
  "newPassword": "new_password",
  "confirmPassword": "new_password"
}
```

### 7. 修改个人信息
```http
PUT /api/v1/auth/profile
Authorization: Bearer your_token
Content-Type: application/json

{
  "realName": "新姓名",
  "phone": "13800138001",
  "email": "new@example.com",
  "avatar": "http://example.com/avatar.jpg"
}
```

### 8. 验证Token有效性
```http
GET /api/v1/auth/verify
Authorization: Bearer your_token
```

## 开发说明

### 1. 默认账户

- **用户名**：admin
- **密码**：admin123
- **角色**：系统管理员

### 2. Token使用

- 登录成功后返回 `token` 和 `refreshToken`
- 访问需要认证的接口时，在请求头中添加：`Authorization: Bearer {token}`
- Token默认有效期为2小时，刷新Token有效期为7天

### 3. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名或密码错误 |
| 1002 | 账号已被禁用 |
| 1003 | 账号已过期 |
| 1004 | Token已过期 |
| 1005 | Token无效 |
| 1006 | 未登录或登录已过期 |
| 1201 | 参数验证失败 |
| 1202 | 参数格式错误 |
| 1301 | 数据不存在 |
| 1401 | 操作失败 |

## 注意事项

1. 确保MySQL和Redis服务已启动
2. 数据库字符集建议使用utf8mb4
3. 生产环境请修改JWT密钥和Redis密码
4. 建议配置反向代理（Nginx）使用

## 联系方式

如有问题，请联系开发团队：
- **邮箱**：dev@example.com
- **文档**：查看API文档 http://localhost:8080/doc.html