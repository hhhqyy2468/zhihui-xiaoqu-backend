# 基于Spring Boot + Vue的社区物业管理系统 — 后端服务

## 项目概述

本系统是河南科技职业大学信息工程学院2026届本科毕业设计项目，课题名称为"基于Spring Boot + Vue的社区物业管理系统设计与实现"。系统采用前后端分离架构，本仓库为后端服务部分，基于Spring Boot 2.7.18框架开发，为社区物业管理提供完整的RESTful API服务。

系统面向社区物业管理的实际需求，旨在解决传统物业管理中费用收缴效率低、信息传递不及时、服务响应慢等问题，通过信息化手段实现业主与物业之间的高效互动，推动社区物业管理的数字化转型。

### 项目信息

| 项目 | 内容 |
|------|------|
| 课题名称 | 基于Spring Boot + Vue的社区物业管理系统设计与实现 |
| 学校 | 河南科技职业大学 |
| 学院 | 信息工程学院 |
| 专业/班级 | 22级软件工程技术/2班 |
| 学生姓名 | 胡浩宇 |
| 学号 | 22013410080 |
| 指导老师 | 张威振 |

---

## 技术栈

### 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 应用开发框架，提供自动配置、内嵌Tomcat服务器等特性 |
| Spring Security | 2.7.18 | 安全认证与授权框架，实现用户身份验证和接口权限控制 |
| MyBatis-Plus | 3.5.3.1 | MyBatis增强工具，简化CRUD操作，提供分页插件、条件构造器等功能 |
| Spring AOP | 2.7.18 | 面向切面编程，用于操作日志自动记录等横切关注点处理 |
| Spring Validation | 2.7.18 | 参数校验框架，实现请求参数的自动验证 |

### 数据存储

| 技术 | 版本 | 说明 |
|------|------|------|
| MySQL | 8.0 | 关系型数据库，存储系统全部业务数据，共28张数据表 |
| Redis | 6.0+ | 内存数据库，用于缓存验证码、JWT Token黑名单等临时数据 |
| Flyway | - | 数据库版本迁移工具（已集成，当前未启用） |

### 安全与认证

| 技术 | 版本 | 说明 |
|------|------|------|
| JWT (jjwt) | 0.11.5 | JSON Web Token，基于HS512算法实现无状态用户认证 |
| Spring Security Filter Chain | - | 自定义JWT认证过滤器，拦截请求进行Token校验 |

### 工具库

| 技术 | 版本 | 说明 |
|------|------|------|
| Lombok | - | 简化Java Bean开发，自动生成getter/setter/构造器等方法 |
| Hutool | 5.8.18 | Java工具类库，提供加密、日期处理、文件操作等丰富工具方法 |
| FastJSON | 2.0.25 | 阿里巴巴高性能JSON序列化/反序列化库 |
| Apache POI | 5.2.4 | Excel文件读写处理，支持账单、日志等数据的Excel导出功能 |
| Apache Commons Lang3 | - | 通用工具类库，提供字符串处理、对象操作等功能 |
| Knife4j | 4.4.0 | 基于Swagger的API接口文档增强工具，提供在线调试能力 |

### 开发与运行环境

| 环境 | 要求 |
|------|------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| 操作系统 | Windows / Linux / macOS |

---

## 系统架构设计

### 整体架构

系统采用经典的B/S（浏览器/服务器）架构，前后端完全分离：

```
┌──────────────────────────────────────────────────────────────┐
│                     客户端（浏览器）                            │
│              Vue 3.4 + Element Plus 2.4 + ECharts             │
│                      端口: 3000                               │
└─────────────────────────────┬────────────────────────────────┘
                              │ HTTP请求 (Vite代理 /api → :8080)
┌─────────────────────────────▼────────────────────────────────┐
│                   后端服务 (Spring Boot 2.7.18)                │
│                         端口: 8080                            │
│  ┌──────────────┐  ┌───────────────┐  ┌──────────────────┐  │
│  │ Controller层  │→│  Service层     │→│   Mapper层        │  │
│  │ (22个控制器)  │  │ (21个服务实现)  │  │ (16个Mapper接口)  │  │
│  └──────────────┘  └───────────────┘  └────────┬─────────┘  │
│  ┌──────────────┐  ┌───────────────┐           │            │
│  │ Security层   │  │  AOP切面层     │           │            │
│  │ (JWT过滤器)  │  │ (操作日志记录) │           │            │
│  └──────────────┘  └───────────────┘           │            │
└────────────────────────────────────────────────┼────────────┘
                    │                            │
       ┌────────────▼────────┐     ┌─────────────▼──────────┐
       │       Redis          │     │      MySQL 8.0         │
       │  (验证码/Token缓存)   │     │ property_management库  │
       │  localhost:6379      │     │  28张业务数据表          │
       └─────────────────────┘     └────────────────────────┘
```

### 后端项目结构

```
property-management-backend/
├── pom.xml                                          # Maven项目配置文件
└── src/main/
    ├── java/com/hyu/
    │   ├── PropertyManagementBackendApplication.java # 启动类 (@EnableScheduling, @EnableAsync)
    │   ├── controller/
    │   │   └── AuthController.java                  # 认证控制器 (/api/v1/auth/*)
    │   ├── common/
    │   │   ├── core/domain/
    │   │   │   ├── AjaxResult.java                  # 统一响应结果封装
    │   │   │   └── PageResult.java                  # 分页查询结果封装
    │   │   ├── domain/
    │   │   │   ├── LoginBody.java                   # 登录请求体
    │   │   │   ├── LoginUser.java                   # 登录用户信息（含权限）
    │   │   │   └── RegisterBody.java                # 注册请求体
    │   │   ├── exception/
    │   │   │   ├── BusinessException.java           # 自定义业务异常
    │   │   │   └── GlobalExceptionHandler.java      # 全局异常处理器
    │   │   └── utils/
    │   │       ├── JwtUtils.java                    # JWT令牌生成与解析工具
    │   │       ├── RedisUtils.java                  # Redis操作封装工具
    │   │       ├── SecurityUtils.java               # 安全上下文工具（获取当前用户）
    │   │       ├── PasswordUtils.java               # 密码加密与校验工具
    │   │       ├── CaptchaUtils.java                # 图形验证码生成工具
    │   │       ├── StringUtils.java                 # 字符串处理工具
    │   │       └── excel/ExcelExportUtil.java       # Excel数据导出工具
    │   ├── framework/
    │   │   ├── config/
    │   │   │   ├── MybatisPlusConfig.java           # MyBatis-Plus分页插件配置
    │   │   │   ├── MyBatisConfig.java               # MyBatis基础配置
    │   │   │   ├── RedisConfig.java                 # Redis序列化配置
    │   │   │   └── Knife4jConfig.java               # API文档配置
    │   │   ├── security/
    │   │   │   ├── config/SecurityConfig.java       # Spring Security核心配置
    │   │   │   ├── filter/JwtAuthenticationTokenFilter.java  # JWT认证过滤器
    │   │   │   ├── handle/AccessDeniedHandlerImpl.java       # 权限不足处理器
    │   │   │   ├── handle/AuthenticationEntryPointImpl.java  # 未认证处理器
    │   │   │   ├── handle/PermissionService.java             # 自定义权限校验服务
    │   │   │   └── service/UserDetailsServiceImpl.java       # 用户认证信息加载服务
    │   │   └── aspectj/
    │   │       └── OperLogAspect.java               # 操作日志AOP切面
    │   ├── system/                                  # 系统管理模块
    │   │   ├── domain/
    │   │   │   ├── SysUser.java                     # 用户实体
    │   │   │   ├── SysRole.java                     # 角色实体
    │   │   │   ├── SysMenu.java                     # 菜单权限实体
    │   │   │   ├── SysUserRole.java                 # 用户角色关联实体
    │   │   │   ├── SysDictType.java                 # 字典类型实体
    │   │   │   └── SysDictData.java                 # 字典数据实体
    │   │   ├── mapper/                              # 6个Mapper接口
    │   │   ├── service/                             # 6个Service接口
    │   │   │   └── impl/                            # 6个Service实现类
    │   │   └── controller/
    │   │       ├── SysUserController.java           # 用户管理接口
    │   │       ├── SysRoleController.java           # 角色管理接口
    │   │       ├── SysMenuController.java           # 菜单管理接口
    │   │       ├── SysDictTypeController.java       # 字典类型接口
    │   │       └── SysDictDataController.java       # 字典数据接口
    │   └── property/                                # 物业业务模块
    │       ├── domain/                              # 实体类（含dto/vo子包）
    │       │   ├── Building.java                    # 楼栋实体
    │       │   ├── Unit.java                        # 单元实体
    │       │   ├── House.java                       # 房产实体
    │       │   ├── Owner.java                       # 业主实体
    │       │   ├── Bill.java                        # 账单实体
    │       │   ├── FeeType.java                     # 费用类型实体
    │       │   ├── Wallet.java                      # 虚拟钱包实体
    │       │   ├── WalletTransaction.java           # 钱包交易记录实体
    │       │   ├── RepairOrder.java                 # 维修工单实体
    │       │   ├── Complaint.java                   # 投诉实体
    │       │   ├── ParkingSpace.java                # 车位实体
    │       │   ├── ParkingRentalApplication.java    # 车位租赁申请实体
    │       │   ├── ParkingRentalContract.java       # 车位租赁合同实体
    │       │   ├── PaymentReceipt.java              # 缴费收据实体
    │       │   ├── Notice.java                      # 公告实体
    │       │   ├── NoticeRead.java                  # 公告已读记录实体
    │       │   ├── SysOperLog.java                  # 操作日志实体
    │       │   ├── SysLoginLog.java                 # 登录日志实体
    │       │   ├── dto/                             # 7个数据传输对象
    │       │   └── vo/                              # 7个视图对象
    │       ├── mapper/                              # 16个Mapper接口
    │       ├── service/                             # 16个Service接口
    │       │   └── impl/                            # 21个Service实现类
    │       └── controller/
    │           ├── BuildingController.java          # 楼栋管理
    │           ├── UnitController.java              # 单元管理
    │           ├── HouseController.java             # 房产管理
    │           ├── OwnerController.java             # 业主管理
    │           ├── BillController.java              # 账单管理（含业主端缴费）
    │           ├── FeeTypeController.java           # 费用类型管理
    │           ├── WalletController.java            # 虚拟钱包管理
    │           ├── WalletTransactionController.java # 钱包交易记录
    │           ├── RepairOrderController.java       # 维修工单管理
    │           ├── ComplaintController.java         # 投诉管理
    │           ├── ParkingSpaceController.java      # 车位管理
    │           ├── ParkingRentalApplicationController.java  # 车位租赁申请
    │           ├── ParkingRentalContractController.java     # 车位租赁合同
    │           ├── PaymentReceiptController.java    # 缴费收据
    │           ├── NoticeController.java            # 公告管理
    │           ├── PortalController.java            # 业主门户接口
    │           ├── UserHouseController.java         # 用户房产关联
    │           ├── CommonController.java            # 通用接口（文件上传等）
    │           ├── WorkbenchController.java         # 维修人员工作台
    │           ├── DashboardController.java         # 首页数据统计
    │           ├── SysOperLogController.java        # 操作日志查询
    │           └── SysLoginLogController.java       # 登录日志查询
    └── resources/
        ├── application.yaml                         # 应用配置文件
        └── mapper/                                  # MyBatis XML映射文件
            ├── system/                              # 系统模块（4个XML）
            └── property/                            # 物业模块（14个XML）
```

### 请求处理流程

系统采用标准的Spring MVC请求处理流程，结合Spring Security和JWT实现安全认证：

```
客户端HTTP请求
    │
    ▼
JwtAuthenticationTokenFilter（JWT认证过滤器）
    │ 从请求头提取Token → JwtUtils解析验证 → 加载用户信息到SecurityContext
    ▼
Spring Security FilterChain（安全过滤器链）
    │ 检查接口权限 → @PreAuthorize("@ss.hasPermi('xxx')") 注解校验
    ▼
Controller（控制器层）
    │ 接收请求参数 → 参数校验(@Valid) → 调用Service
    ▼
Service（业务逻辑层）
    │ 业务处理 → 事务管理(@Transactional) → 调用Mapper
    ▼
Mapper + MyBatis-Plus（数据访问层）
    │ 执行SQL → 操作MySQL数据库
    ▼
AjaxResult（统一响应格式）→ JSON响应 → 客户端
```

同时，OperLogAspect（操作日志切面）会拦截标注了 `@Log` 注解的Controller方法，自动记录操作日志到 `sys_oper_log` 表。

---

## 用户角色与权限体系

系统设计了四种用户角色，通过 `user_type` 字段区分，各角色拥有不同的功能权限：

| user_type | 角色名称 | 登录后首页 | 功能范围 |
|-----------|----------|-----------|---------|
| 1 | 系统管理员 | 数据大屏 `/analytics/dashboard` | 全部功能：系统管理（用户/角色/字典）、物业管理、费用管理、服务管理、停车管理、公告管理、系统日志、数据大屏 |
| 2 | 物业管理员 | 工作台 `/dashboard` | 物业管理（楼栋/单元/房产/业主）、费用管理（费用类型/账单/钱包）、服务管理（投诉/维修）、停车管理（车位/租赁）、公告管理 |
| 3 | 业主 | 业主首页 `/portal/dashboard` | 业主门户：我的账单、我的钱包、我的房产、我的车位、我的投诉、我的报修、社区公告 |
| 4 | 维修人员 | 待接单 `/work/pending` | 我的工作（待接单/进行中/待验收/已完成）、社区公告查看 |

### 权限控制机制

- **接口级权限**：后端通过 `@PreAuthorize("@ss.hasPermi('xxx')")` 注解实现，PermissionService 根据用户角色和菜单权限进行校验
- **页面级权限**：前端通过自定义指令 `v-user-type`、`v-permission`、`v-role` 控制菜单和按钮的显示
- **Token机制**：JWT Token有效期2小时（7200000ms），Refresh Token有效期7天（604800000ms），采用HS512签名算法
- **验证码**：登录时生成图形验证码，存储于Redis，有效期5分钟

---

## 功能模块详细说明

### 一、系统管理模块

系统管理模块为系统管理员提供基础的系统配置和维护功能。

#### 1.1 用户管理（SysUserController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 用户列表 | `/api/v1/system/user/list` | GET | 分页查询用户列表，支持按用户名、手机号、状态筛选 |
| 新增用户 | `/api/v1/system/user` | POST | 创建新用户，分配角色 |
| 修改用户 | `/api/v1/system/user` | PUT | 修改用户信息 |
| 删除用户 | `/api/v1/system/user/{userIds}` | DELETE | 批量删除用户 |
| 重置密码 | `/api/v1/system/user/resetPwd` | PUT | 管理员重置用户密码 |
| 状态变更 | `/api/v1/system/user/changeStatus` | PUT | 启用/禁用用户 |
| 导出Excel | `/api/v1/system/user/export` | GET | 导出用户数据为Excel文件 |

#### 1.2 角色管理（SysRoleController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 角色列表 | `/api/v1/system/role/list` | GET | 分页查询角色列表 |
| 新增角色 | `/api/v1/system/role` | POST | 创建角色并分配菜单权限 |
| 修改角色 | `/api/v1/system/role` | PUT | 修改角色信息和权限 |
| 删除角色 | `/api/v1/system/role/{roleIds}` | DELETE | 批量删除角色 |

#### 1.3 菜单管理（SysMenuController）

管理系统菜单和权限标识，支持树形结构展示。

#### 1.4 字典管理（SysDictTypeController / SysDictDataController）

管理系统中的数据字典，如费用类型、投诉类型、维修状态等枚举值，实现数据的统一维护。

#### 1.5 系统日志

- **操作日志**（SysOperLogController）：通过AOP切面自动记录用户的增删改操作，包括操作人、操作时间、请求参数、响应结果等
- **登录日志**（SysLoginLogController）：记录用户登录/登出行为，包括登录IP、登录时间、登录状态等

### 二、物业管理模块

物业管理模块是系统的核心业务模块，实现了从楼栋到业主的完整物业信息管理链条。

#### 2.1 楼栋管理（BuildingController）

管理小区内的楼栋信息，是物业管理的基础数据。

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 楼栋列表 | `/api/v1/property/building/list` | GET | 分页查询楼栋列表 |
| 新增楼栋 | `/api/v1/property/building` | POST | 添加楼栋信息（楼栋名称、层数、单元数等） |
| 修改楼栋 | `/api/v1/property/building` | PUT | 修改楼栋信息 |
| 删除楼栋 | `/api/v1/property/building/{ids}` | DELETE | 批量删除楼栋 |

#### 2.2 单元管理（UnitController）

管理楼栋下的单元信息，与楼栋形成一对多关系。

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 单元列表 | `/api/v1/property/unit/list` | GET | 分页查询，支持按楼栋筛选 |
| 新增单元 | `/api/v1/property/unit` | POST | 添加单元信息（所属楼栋、单元号、层数等） |
| 修改单元 | `/api/v1/property/unit` | PUT | 修改单元信息 |
| 删除单元 | `/api/v1/property/unit/{ids}` | DELETE | 批量删除单元 |

#### 2.3 房产管理（HouseController）

管理单元下的具体房产信息，是关联业主的核心实体。

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 房产列表 | `/api/v1/property/house/list` | GET | 分页查询，支持按楼栋、单元、房号筛选 |
| 新增房产 | `/api/v1/property/house` | POST | 添加房产信息（所属单元、房号、面积、户型等） |
| 修改房产 | `/api/v1/property/house` | PUT | 修改房产信息 |
| 删除房产 | `/api/v1/property/house/{ids}` | DELETE | 批量删除房产 |

数据关系链：**楼栋(Building) → 单元(Unit) → 房产(House) → 业主(Owner)**

#### 2.4 业主管理（OwnerController）

管理业主基本信息，并通过 `user_house` 关联表实现业主与房产的绑定关系。

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 业主列表 | `/api/v1/property/owner/list` | GET | 分页查询业主列表 |
| 新增业主 | `/api/v1/property/owner` | POST | 添加业主信息 |
| 修改业主 | `/api/v1/property/owner` | PUT | 修改业主信息 |
| 删除业主 | `/api/v1/property/owner/{ids}` | DELETE | 批量删除业主 |

#### 2.5 用户房产关联（UserHouseController）

管理用户（业主账号）与房产之间的绑定关系，一个用户可以绑定多套房产。

### 三、费用管理模块

费用管理模块实现了从费用类型定义、账单生成到在线缴费的完整财务管理流程。

#### 3.1 费用类型管理（FeeTypeController）

定义和维护物业费、水费、电费、停车费等各类费用类型，包括费用名称、计费方式、单价等信息。

#### 3.2 账单管理（BillController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 账单列表 | `/api/v1/property/bill/list` | GET | 分页查询账单，支持按业主、费用类型、状态筛选 |
| 生成账单 | `/api/v1/property/bill` | POST | 为指定房产生成费用账单 |
| 账单缴费 | `/api/v1/property/bill/pay` | POST | 业主通过虚拟钱包在线缴费 |
| 导出账单 | `/api/v1/property/bill/export` | GET | 导出账单数据为Excel |

账单状态流转：**待缴费 → 已缴费 / 已逾期**

#### 3.3 虚拟钱包管理（WalletController）

每个业主拥有一个虚拟钱包，用于在线缴纳各类物业费用。

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 钱包信息 | `/api/v1/property/wallet/info` | GET | 查询钱包余额和基本信息 |
| 钱包充值 | `/api/v1/property/wallet/recharge` | POST | 向钱包充值 |
| 钱包列表 | `/api/v1/property/wallet/list` | GET | 管理员查看所有钱包（管理端） |

#### 3.4 钱包交易记录（WalletTransactionController）

记录钱包的每一笔充值、扣款交易，支持按时间、类型查询交易明细。

#### 3.5 缴费收据（PaymentReceiptController）

缴费成功后自动生成电子收据，记录缴费金额、缴费时间、费用类型等信息，支持查询和导出。

### 四、服务管理模块

服务管理模块实现了业主投诉和维修报修的完整闭环处理流程。

#### 4.1 投诉管理（ComplaintController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 投诉列表 | `/api/v1/property/complaint/list` | GET | 分页查询投诉记录 |
| 提交投诉 | `/api/v1/property/complaint` | POST | 业主提交投诉 |
| 处理投诉 | `/api/v1/property/complaint/handle` | PUT | 物业处理投诉 |
| 评价投诉 | `/api/v1/property/complaint/evaluate` | PUT | 业主对处理结果评价 |

投诉状态流转：**待处理 → 处理中 → 已处理 → 已评价 → 已关闭**

#### 4.2 维修工单管理（RepairOrderController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 工单列表 | `/api/v1/property/repair/list` | GET | 分页查询维修工单 |
| 提交报修 | `/api/v1/property/repair` | POST | 业主提交维修申请（支持上传图片） |
| 派工 | `/api/v1/property/repair/assign` | PUT | 物业管理员将工单派给维修人员 |
| 接单 | `/api/v1/property/repair/accept` | PUT | 维修人员接受工单 |
| 完成维修 | `/api/v1/property/repair/complete` | PUT | 维修人员标记维修完成 |
| 验收 | `/api/v1/property/repair/verify` | PUT | 业主验收维修结果 |
| 评价 | `/api/v1/property/repair/evaluate` | PUT | 业主对维修服务评价 |

维修工单状态流转：**待派工 → 待接单 → 维修中 → 待验收 → 已完成（已评价）**

### 五、停车管理模块

停车管理模块实现了车位信息管理和车位租赁的完整业务流程。

#### 5.1 车位管理（ParkingSpaceController）

管理小区内的车位信息，包括车位编号、位置、类型（地上/地下）、状态（空闲/已租/已售）、月租金等。

#### 5.2 车位租赁申请（ParkingRentalApplicationController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 申请列表 | `/api/v1/parking/rental-application/list` | GET | 查询租赁申请列表 |
| 提交申请 | `/api/v1/parking/rental-application` | POST | 业主提交车位租赁申请 |
| 审核申请 | `/api/v1/parking/rental-application/audit` | PUT | 物业审核租赁申请 |

#### 5.3 车位租赁合同（ParkingRentalContractController）

审核通过后自动生成租赁合同，记录租赁期限、月租金、合同状态等信息。支持合同续租和退租操作。

租赁流程：**提交申请 → 物业审核 → 生成合同 → 缴费激活 → 到期续租/退租**

### 六、公告管理模块（NoticeController）

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 公告列表 | `/api/v1/property/notice/list` | GET | 分页查询公告列表 |
| 发布公告 | `/api/v1/property/notice` | POST | 发布新公告（支持指定推送范围） |
| 撤回公告 | `/api/v1/property/notice/revoke` | PUT | 撤回已发布的公告 |
| 阅读统计 | `/api/v1/property/notice/statistics` | GET | 查看公告的阅读情况统计 |
| 标记已读 | `/api/v1/property/notice/read` | POST | 业主标记公告为已读 |

公告状态：**草稿 → 已发布 → 已撤回**，通过 `notice_read` 表追踪每位业主的阅读状态。

### 七、数据统计模块（DashboardController）

为管理员提供首页数据大屏和工作台的统计数据：

- 房产总数、入住率统计
- 费用收缴率统计
- 维修工单完成率统计
- 投诉处理情况统计
- 今日登录数统计
- 各类数据的趋势图表数据

### 八、业主门户模块（PortalController）

为业主提供统一的门户入口，聚合展示与业主相关的各类信息：

- 我的房产信息
- 待缴费账单
- 维修工单进度
- 投诉处理进度
- 社区公告通知

### 九、维修人员工作台（WorkbenchController）

为维修人员提供专属的工作界面，按工单状态分类展示：

- 待接单：等待维修人员接受的工单
- 进行中：正在维修的工单
- 待验收：维修完成等待业主验收的工单
- 已完成：已验收完成的工单

### 十、通用功能模块（CommonController）

提供文件上传等通用功能接口，支持维修工单图片上传、用户头像上传等场景。

---

## 数据库设计

### 数据库概览

系统使用MySQL 8.0数据库，数据库名为 `property_management`，共包含28张数据表，按功能可分为以下几类：

### 数据表清单

#### 系统管理表（7张）

| 表名 | 注释 | 说明 |
|------|------|------|
| sys_user | 用户表 | 存储所有用户信息（管理员、物业、业主、维修人员） |
| sys_role | 角色表 | 定义系统角色 |
| sys_menu | 菜单权限表 | 定义菜单和权限标识 |
| sys_user_role | 用户角色关联表 | 用户与角色的多对多关系 |
| sys_role_menu | 角色菜单关联表 | 角色与菜单权限的多对多关系 |
| sys_dict_type | 字典类型表 | 数据字典分类 |
| sys_dict_data | 字典数据表 | 数据字典具体值 |

#### 物业管理表（4张）

| 表名 | 注释 | 说明 |
|------|------|------|
| building | 楼栋表 | 小区楼栋基本信息 |
| unit | 单元表 | 楼栋下的单元信息 |
| house | 房产表 | 具体房产信息（户型、面积、状态等） |
| user_house | 用户房产关联表 | 用户（业主）与房产的绑定关系 |

#### 费用管理表（5张）

| 表名 | 注释 | 说明 |
|------|------|------|
| fee_type | 费用类型表 | 物业费、水费、电费等费用类型定义 |
| bill | 账单表 | 业主费用账单 |
| wallet | 虚拟钱包表 | 业主虚拟钱包（余额、支付密码） |
| wallet_transaction | 钱包交易记录表 | 充值、扣款等交易流水 |
| payment_receipt | 缴费收据表 | 缴费成功后的电子收据 |

#### 服务管理表（2张）

| 表名 | 注释 | 说明 |
|------|------|------|
| repair_order | 维修工单表 | 维修报修工单（含状态流转、派工信息） |
| complaint | 投诉表 | 业主投诉记录（含处理和评价信息） |

#### 停车管理表（4张）

| 表名 | 注释 | 说明 |
|------|------|------|
| parking_space | 车位表 | 车位基本信息（编号、位置、状态、租金） |
| parking_rental_application | 车位租赁申请表 | 业主提交的车位租赁申请 |
| parking_rental_contract | 车位租赁合同表 | 审核通过后生成的租赁合同 |
| parking_rental | 车位租赁记录表 | 车位租赁历史记录 |

#### 公告管理表（2张）

| 表名 | 注释 | 说明 |
|------|------|------|
| notice | 公告表 | 公告内容、发布状态、推送范围 |
| notice_read | 公告阅读记录表 | 追踪每位业主的公告阅读状态 |

#### 日志管理表（2张）

| 表名 | 注释 | 说明 |
|------|------|------|
| sys_oper_log | 操作日志表 | 记录用户的增删改操作 |
| sys_login_log | 登录日志表 | 记录用户登录/登出行为 |

#### 其他（2张）

| 表名 | 注释 | 说明 |
|------|------|------|
| payment_record | 缴费记录表 | 缴费流水记录 |
| flyway_schema_history | Flyway迁移记录 | 数据库版本迁移历史（框架自动生成） |

### 核心实体关系

```
sys_user (用户)
    │
    ├── 1:N ── sys_user_role ── N:1 ── sys_role (角色)
    │                                      │
    │                                      └── N:N ── sys_role_menu ── sys_menu (菜单权限)
    │
    ├── 1:N ── user_house ── N:1 ── house (房产)
    │                                  │
    │                                  ├── N:1 ── unit (单元)
    │                                  │            │
    │                                  │            └── N:1 ── building (楼栋)
    │                                  │
    │                                  └── 1:N ── bill (账单) ── N:1 ── fee_type (费用类型)
    │
    ├── 1:1 ── wallet (钱包) ── 1:N ── wallet_transaction (交易记录)
    │
    ├── 1:N ── repair_order (维修工单)
    │
    ├── 1:N ── complaint (投诉)
    │
    └── 1:N ── parking_rental_application (车位租赁申请)
                    │
                    └── 1:1 ── parking_rental_contract (租赁合同)
                                    │
                                    └── N:1 ── parking_space (车位)
```

---

## 认证模块API接口

### 接口列表

| 功能 | 接口 | 方法 | 认证 |
|------|------|------|------|
| 获取验证码 | `/api/v1/auth/captcha` | GET | 否 |
| 用户登录 | `/api/v1/auth/login` | POST | 否 |
| 用户注册 | `/api/v1/auth/register` | POST | 否 |
| 刷新Token | `/api/v1/auth/refresh` | POST | 否 |
| 用户登出 | `/api/v1/auth/logout` | POST | 是 |
| 获取用户信息 | `/api/v1/auth/info` | GET | 是 |
| 修改密码 | `/api/v1/auth/password` | PUT | 是 |
| 修改个人信息 | `/api/v1/auth/profile` | PUT | 是 |
| 验证Token | `/api/v1/auth/verify` | GET | 是 |

### 登录请求示例

```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "captcha": "ABCD",
  "uuid": "验证码UUID"
}
```

### 统一响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
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

---

## 配置说明

### 应用配置（application.yaml）

```yaml
# 服务端口
server:
  port: 8080

# 数据源配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/property_management?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: 123456

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.hyu.**.domain
  global-config:
    db-config:
      id-type: auto                    # 主键自增策略
  configuration:
    map-underscore-to-camel-case: true # 下划线转驼峰映射

# JWT配置
jwt:
  secret: <HS512密钥>
  expiration: 7200000                  # Token有效期：2小时
  refresh-expiration: 604800000        # RefreshToken有效期：7天

# Knife4j API文档
knife4j:
  enable: true
```

---

## 部署与运行

### 环境准备

1. 安装JDK 1.8+并配置环境变量
2. 安装Maven 3.6+
3. 安装MySQL 8.0+，创建数据库 `property_management`
4. 安装Redis 6.0+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/hhhqyy2468/zhihui-xiaoqu-backend.git

# 2. 进入后端项目目录
cd property-management-backend

# 3. 修改数据库和Redis配置
# 编辑 src/main/resources/application.yaml

# 4. 编译打包
mvn clean package -DskipTests

# 5. 运行
java -jar target/property-management-backend-0.0.1-SNAPSHOT.jar
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 后端API | http://localhost:8080/api/v1 |
| API文档 | http://localhost:8080/doc.html |
| 前端页面 | http://localhost:3000 |

### 默认账户

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 系统管理员 | admin | admin123 |

---

## 项目特点与创新

1. **前后端分离架构**：后端提供RESTful API，前端独立部署，降低耦合度，便于独立开发和维护
2. **多角色权限体系**：支持系统管理员、物业管理员、业主、维修人员四种角色，通过RBAC模型实现细粒度权限控制
3. **完整业务闭环**：维修工单从报修→派工→接单→维修→验收→评价，投诉从提交→处理→评价→关闭，实现全流程管理
4. **虚拟钱包系统**：业主拥有虚拟钱包，支持充值和在线缴费，交易记录完整可追溯
5. **数据统计大屏**：提供入住率、收缴率、维修完成率等多维度数据统计，辅助管理决策
6. **操作日志审计**：通过AOP切面自动记录关键操作日志，保障系统安全和可追溯性
7. **JWT无状态认证**：采用JWT Token机制，支持Token刷新，适合分布式部署场景
