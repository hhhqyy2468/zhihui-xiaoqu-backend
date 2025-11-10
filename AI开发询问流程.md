# AI 开发询问流程（自主开发版）

## 技术栈选择

### 前端技术栈
- **框架：** Vue 3 + Vite
- **UI组件：** Element Plus
- **状态管理：** Pinia
- **路由：** Vue Router 4
- **HTTP客户端：** Axios
- **图标：** Element Plus Icons
- **样式：** SCSS
- **布局：** 类似Ruo-Yi的侧边栏布局

### 后端技术栈
- **框架：** Spring Boot 3.2.x
- **ORM：** MyBatis Plus 3.5.x
- **数据库：** MySQL 8.0
- **缓存：** Redis
- **安全：** Spring Security + JWT
- **工具：** Lombok, Hutool
- **文档：** Knife4j (Swagger 3)

## 第一阶段：前端项目初始化与基础框架搭建

### 1.1 创建Vue 3项目
```bash
npm create vue@latest property-management-frontend
cd property-management-frontend
npm install
```

### 1.2 安装依赖包
```bash
# UI组件库
npm install element-plus @element-plus/icons-vue

# 状态管理
npm install pinia

# HTTP客户端
npm install axios

# 路由
npm install vue-router@4

# 工具库
npm install lodash-es dayjs

# 开发依赖
npm install -D sass @types/node
```

### 1.3 项目结构规划
```
src/
├── api/                 # API接口定义
├── assets/              # 静态资源
├── components/          # 公共组件
│   ├── Layout/         # 布局组件
│   ├── Table/          # 表格组件
│   ├── Form/           # 表单组件
│   └── Upload/         # 上传组件
├── router/              # 路由配置
├── stores/              # Pinia状态管理
├── styles/              # 全局样式
├── utils/               # 工具函数
├── views/               # 页面组件
│   ├── login/          # 登录页
│   ├── system/         # 系统管理
│   ├── property/       # 物业管理
│   └── portal/         # 业主门户
├── App.vue             # 根组件
└── main.js             # 入口文件
```

### 1.4 基础配置
- Vite配置（代理、别名等）
- ESLint + Prettier代码规范
- 环境变量配置
- Axios请求封装

## 第二阶段：前端基础框架开发

### 2.1 布局框架开发
**主布局组件：** `src/components/Layout/index.vue`

**功能要求：**
- 顶部导航栏（Logo（把C:\Users\30567\Desktop\AI开发物业管理\logo.png复制到需要的位置）、用户信息、退出登录）
- 左侧菜单栏（折叠/展开、多级菜单）
- 主内容区域（面包屑导航）
- 响应式布局支持

### 2.2 通用组件开发
**表格组件：** `src/components/Table/index.vue`
- 支持分页、排序、筛选
- 工具栏（新增、删除、导出等）
- 操作列（编辑、删除等）

**表单组件：** `src/components/Form/index.vue`
- 动态表单生成
- 表单验证
- 联动效果

**上传组件：** `src/components/Upload/index.vue`
- 单文件/多文件上传
- 图片预览
- 上传进度显示

### 2.3 路由与权限
**路由配置：** `src/router/index.js`
- 基于角色的路由权限控制
- 动态路由加载
- 路由守卫

**权限指令：** `src/directives/permission.js`
- v-permission指令
- 按钮级权限控制

### 2.4 状态管理
**用户状态：** `src/stores/user.js`
- 用户信息、Token管理
- 登录/登出逻辑

**应用状态：** `src/stores/app.js`
- 侧边栏状态、主题设置

## 第三阶段：前端页面开发（共12个模块）

### 模块1：登录页面
**页面路径：** `/views/login/index.vue`

**功能要求：**
- 用户名密码登录
- 记住密码功能
- 登录状态保持

### 模块2：系统管理 - 用户管理
**页面路径：** `/views/system/user/index.vue`

**功能要求：**
- 搜索区域：用户名、真实姓名、手机号、用户类型、状态
- 表格列：用户名、真实姓名、手机号、用户类型、状态、创建时间、操作
- 操作按钮：新增、编辑、删除、重置密码、分配角色
- 表单字段：用户名、密码、真实姓名、手机号、用户类型、角色选择

### 模块3：系统管理 - 角色管理
**页面路径：** `/views/system/role/index.vue`

**功能要求：**
- 搜索区域：角色名称、状态
- 表格列：角色名称、角色标识、状态、创建时间、操作
- 操作按钮：新增、编辑、删除、权限分配
- 表单字段：角色名称、角色标识、菜单权限树、状态

### 模块4：系统管理 - 菜单管理
**页面路径：** `/views/system/menu/index.vue`

**功能要求：**
- 表格列：菜单名称、图标、路径、排序、类型、状态、操作
- 操作按钮：新增、编辑、删除
- 表单字段：上级菜单、菜单类型、菜单名称、路由地址、权限标识、图标、排序

### 模块5：楼栋管理
**页面路径：** `/views/property/building/index.vue`

**功能要求：**
- 搜索区域：楼栋编号、楼栋名称、建筑年份范围
- 表格列：楼栋编号、楼栋名称、楼层数、单元数、详细地址、建筑年份、操作
- 表单字段：楼栋编号、楼栋名称、楼层数、单元数、详细地址、建筑年份、备注

### 模块6：单元管理
**页面路径：** `/views/property/unit/index.vue`

**功能要求：**
- 搜索区域：单元编号、单元名称、所属楼栋
- 表格列：单元编号、单元名称、所属楼栋、楼层数、每层房间数、操作
- 表单字段：所属楼栋、单元编号、单元名称、楼层数、每层房间数、备注

### 模块7：房产管理
**页面路径：** `/views/property/house/index.vue`

**功能要求：**
- 搜索区域：房间编号、楼栋筛选、单元筛选、房产状态
- 表格列：房间编号、楼栋名称、单元名称、门牌号、楼层、户型、建筑面积、房产状态、产权人、操作
- 表单字段：楼栋选择、单元选择、房间编号、门牌号、楼层、户型、建筑面积、使用面积、房产状态、产权人

### 模块8：住户管理
**页面路径：** `/views/property/resident/index.vue`

**功能要求：**
- 搜索区域：用户名、真实姓名、手机号、用户类型、住户类型、住户状态
- 表格列：用户名、真实姓名、手机号、用户类型、住户类型、入住日期、住户状态、操作
- 表单字段：用户类型、住户类型、真实姓名、手机号、身份证号、房产绑定、入住日期

### 模块9：费用类型管理
**页面路径：** `/views/property/feetype/index.vue`

**功能要求：**
- 搜索区域：类型名称、类型编码、状态
- 表格列：类型名称、类型编码、单价、计费单位、计费周期、状态、操作
- 表单字段：类型名称、类型编码、单价、计费单位、计费周期、费用说明、状态

### 模块10：账单管理
**页面路径：** `/views/property/bill/index.vue`

**功能要求：**
- 搜索区域：账单编号、业主姓名、房间编号、费用类型、账单状态、账期
- 表格列：账单编号、业主姓名、房间编号、费用类型、账期、应缴金额、实缴金额、账单状态、操作
- 表单字段：业主选择、房产选择、费用类型、账期、应缴金额、缴费截止日期
- 特殊功能：批量生成账单

### 模块11：投诉管理
**页面路径：** `/views/property/complaint/index.vue`

**功能要求：**
- 搜索区域：投诉单号、投诉人、投诉类型、投诉状态
- 表格列：投诉单号、投诉人、房间编号、投诉类型、投诉状态、投诉时间、操作
- 表单字段：投诉人、房间编号、投诉类型、紧急程度、投诉内容、图片上传
- 特殊功能：分配处理人、处理投诉、查看评价

### 模块12：维修管理
**页面路径：** `/views/property/repair/index.vue`

**功能要求：**
- 搜索区域：工单编号、报修人、维修类型、工单状态
- 表格列：工单编号、报修人、房间编号、维修类型、工单状态、报修时间、维修人员、操作
- 表单字段：报修人、房间编号、维修类型、紧急程度、故障描述、图片上传
- 特殊功能：派工、查看进度、验收

## 第四阶段：前端特殊功能模块开发

### 模块13：虚拟钱包管理
**页面路径：** `/views/property/wallet/index.vue`

**功能要求：**
- 搜索区域：业主姓名、手机号、钱包状态
- 表格列：业主姓名、手机号、当前余额、累计充值、累计消费、钱包状态、操作
- 钱包详情对话框：基本信息、充值记录、消费记录
- 特殊功能：重置支付密码、冻结/解冻钱包、查看交易明细

### 模块14：停车管理
**页面路径：** `/views/property/parking/index.vue`

**功能要求：**
- 车位管理标签页：车位编号、位置、月租金、车位状态
- 租赁管理标签页：租赁合同号、业主姓名、车牌号、租赁期限、租赁状态
- 特殊功能：审核租赁申请、续租、退租

### 模块15：公告管理
**页面路径：** `/views/property/notice/index.vue`

**功能要求：**
- 搜索区域：公告标题、公告类型、公告状态、发布时间范围
- 表格列：公告标题、公告类型、发布人、发布时间、阅读次数、是否置顶、公告状态、操作
- 表单字段：公告标题、公告类型、公告内容（富文本编辑器）、附件上传、发布范围
- 特殊功能：查看阅读统计、撤回公告、公告预览

### 模块16：业主门户
**页面路径：** `/views/portal/index.vue`

**功能要求：** 使用标签页方式组织
- 我的账单：待缴费/已缴费账单列表、在线支付功能
- 我的钱包：余额显示、充值功能、交易记录、支付密码管理
- 我的房产：名下房产列表
- 我的车位：租赁中的车位信息、续租功能
- 我的投诉：投诉记录列表、提交新投诉、查看处理进度
- 我的报修：报修记录列表、提交新报修、查看维修进度
- 社区公告：公告列表、查看公告详情

## 第五阶段：后端项目初始化与基础框架搭建（修正版）

### 5.1 环境准备与依赖管理
**注意事项：**
- 基于现有的 `property-management-backend` 项目进行开发
- 使用现有的 `com.hyu` 包结构，保持一致性
- 确保与现有 `property_management.sql` 数据库脚本兼容

### 5.2 核心依赖版本（兼容Spring Boot 3.2.x）
```xml
<!-- Hutool工具类 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.26</version>
</dependency>

<!-- JWT（使用Spring Boot 3兼容版本） -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Knife4j接口文档（Spring Boot 3兼容版本） -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>

<!-- FastJSON2 -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.47</version>
</dependency>

<!-- 文件上传 -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>

<!-- 参数校验（Spring Boot 3使用Jakarta） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Redis缓存 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 连接池 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.20</version>
</dependency>
```

### 5.3 完整项目结构（基于现有项目扩展）
```
src/main/java/com/hyu/
├── PropertyApplication.java          # 应用启动类
├── common/                           # 公共模块
│   ├── config/                       # 配置类
│   │   ├── MybatisPlusConfig.java   # MyBatis Plus配置
│   │   ├── RedisConfig.java         # Redis配置
│   │   ├── WebMvcConfig.java        # Web MVC配置
│   │   ├── CorsConfig.java          # 跨域配置
│   │   └── Knife4jConfig.java       # 接口文档配置
│   ├── constant/                     # 常量类
│   │   ├── HttpStatus.java          # HTTP状态码
│   │   ├── BusinessConstants.java   # 业务常量
│   │   └── SecurityConstants.java   # 安全常量
│   ├── core/                         # 核心组件
│   │   ├── domain/                  # 核心领域对象
│   │   │   ├── AjaxResult.java      # 统一响应结果
│   │   │   ├── PageResult.java      # 分页结果
│   │   │   ├── BaseEntity.java      # 基础实体
│   │   │   └── TreeSelect.java      # 树形选择
│   │   ├── text/                    # 文本处理
│   │   └── page/                    # 分页处理
│   ├── exception/                    # 异常处理
│   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   ├── ServiceException.java   # 业务异常
│   │   ├── ValidationException.java # 参数校验异常
│   │   └── SecurityException.java   # 安全异常
│   └── utils/                        # 工具类
│       ├── JwtUtils.java            # JWT工具
│       ├── StringUtils.java         # 字符串工具
│       ├── DateUtils.java           # 日期工具
│       ├── EncryptionUtils.java     # 加密工具
│       ├── FileUploadUtils.java     # 文件上传工具
│       └── RedisUtils.java          # Redis工具
├── framework/                        # 框架核心
│   ├── security/                     # 安全框架
│   │   ├── config/                  # 安全配置
│   │   │   ├── SecurityConfig.java  # Spring Security配置
│   │   │   └── PasswordConfig.java  # 密码策略配置
│   │   ├── filter/                  # 过滤器
│   │   │   ├── JwtAuthenticationTokenFilter.java # JWT认证过滤器
│   │   │   └── LoginFilter.java     # 登录过滤器
│   │   ├── handle/                  # 处理器
│   │   │   ├── AuthenticationEntryPointImpl.java # 认证入口点
│   │   │   └── AccessDeniedHandlerImpl.java # 权限拒绝处理器
│   │   └── service/                 # 安全服务
│   │       ├── UserDetailsServiceImpl.java # 用户详情服务
│   │       └── PermissionService.java # 权限服务
│   ├── web/                         # Web配置
│   │   ├── config/                  # Web配置类
│   │   └── filter/                  # Web过滤器
│   └── aspectj/                     # AOP切面
│       ├── LogAspect.java           # 操作日志切面
│       ├── DataScopeAspect.java     # 数据权限切面
│       └── RateLimitAspect.java     # 限流切面
├── system/                           # 系统管理模块
│   ├── controller/                   # 控制器
│   │   ├── SysLoginController.java  # 登录控制器
│   │   ├── SysUserController.java   # 用户管理控制器
│   │   ├── SysRoleController.java   # 角色管理控制器
│   │   ├── SysMenuController.java   # 菜单管理控制器
│   │   └── SysProfileController.java # 个人资料控制器
│   ├── service/                      # 服务层
│   │   ├── impl/                    # 服务实现
│   │   └── interface/               # 服务接口
│   ├── mapper/                       # 数据访问层
│   │   ├── SysUserMapper.java       # 用户映射器
│   │   ├── SysRoleMapper.java       # 角色映射器
│   │   ├── SysMenuMapper.java       # 菜单映射器
│   │   └── SysUserRoleMapper.java   # 用户角色映射器
│   └── domain/                       # 实体类
│       ├── SysUser.java             # 用户实体
│       ├── SysRole.java             # 角色实体
│       ├── SysMenu.java             # 菜单实体
│       └── SysUserRole.java         # 用户角色实体
├── property/                         # 物业管理模块
│   ├── controller/                   # 控制器
│   │   ├── BuildingController.java  # 楼栋管理控制器
│   │   ├── UnitController.java      # 单元管理控制器
│   │   ├── HouseController.java     # 房产管理控制器
│   │   ├── ResidentController.java  # 住户管理控制器
│   │   ├── FeeTypeController.java   # 费用类型控制器
│   │   ├── BillController.java      # 账单管理控制器
│   │   ├── ComplaintController.java # 投诉管理控制器
│   │   ├── RepairController.java    # 维修管理控制器
│   │   ├── ParkingController.java   # 停车管理控制器
│   │   ├── NoticeController.java    # 公告管理控制器
│   │   └── WalletController.java    # 钱包管理控制器
│   ├── service/                      # 服务层
│   │   ├── impl/                    # 服务实现
│   │   └── interface/               # 服务接口
│   ├── mapper/                       # 数据访问层
│   │   ├── BuildingMapper.java      # 楼栋映射器
│   │   ├── UnitMapper.java          # 单元映射器
│   │   ├── HouseMapper.java         # 房产映射器
│   │   └── ...其他映射器
│   └── domain/                       # 实体类
│       ├── Building.java            # 楼栋实体
│       ├── Unit.java                # 单元实体
│       ├── House.java               # 房产实体
│       └── ...其他实体
├── portal/                           # 业主门户模块
│   ├── controller/                   # 控制器
│   │   ├── PortalController.java    # 门户主页控制器
│   │   ├── PortalBillController.java # 账单控制器
│   │   ├── PortalWalletController.java # 钱包控制器
│   │   └── PortalComplaintController.java # 投诉控制器
│   ├── service/                      # 服务层
│   ├── mapper/                       # 数据访问层
│   └── domain/                       # 实体类
└── resources/                        # 资源文件
    ├── application.yml               # 主配置文件
    ├── application-dev.yml           # 开发环境配置
    ├── application-prod.yml          # 生产环境配置
    ├── mapper/                       # MyBatis映射文件
    │   ├── system/                   # 系统模块映射
    │   └── property/                 # 物业模块映射
    ├── static/                       # 静态资源
    │   ├── upload/                   # 上传文件目录
    │   └── images/                   # 图片资源
    └── templates/                    # 模板文件
        └── email/                    # 邮件模板
```

### 5.4 核心配置文件

#### 5.4.1 application.yml 主配置
```yaml
server:
  port: 8080
  servlet:
    context-path: /api
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

spring:
  application:
    name: property-management-backend
  profiles:
    active: dev
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/property_management?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20

  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 10s
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0

  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    serialization:
      write-dates-as-timestamps: false

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.hyu.**.domain
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    call-setters-on-nulls: true
    jdbc-type-for-null: 'null'
  global-config:
    db-config:
      id-type: AUTO
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.hyu: debug
    org.springframework.security: debug
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n'
    file: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n'
  file:
    name: logs/property-management.log
    max-size: 10MB
    max-history: 30

knife4j:
  enable: true
  openapi:
    title: 智慧小区物业管理系统 API
    description: 物业管理系统后端接口文档
    version: 1.0.0
    concat: hhqyy2468@example.com
  setting:
    language: zh-CN
    enable-swagger-models: true
    enable-document-manage: true
    swagger-model-name: 实体类列表
    enable-version: false
    enable-reload-cache-parameter: false
    enable-after-script: true
  production: false
  basic:
    enable: false
```

#### 5.4.2 JWT配置
```yaml
jwt:
  secret: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
  expiration: 86400000  # 24小时
  header: Authorization
  prefix: Bearer
```

#### 5.4.3 文件上传配置
```yaml
file:
  upload:
    path: D:/property-upload/
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx
    max-size: 10485760  # 10MB
```

### 5.5 数据库初始化
1. **执行现有数据库脚本**
   ```bash
   mysql -u root -p < property_management.sql
   ```

2. **添加系统初始化数据**
   - 管理员用户数据
   - 基础权限数据
   - 系统参数配置

### 5.6 基础配置实现顺序
1. ✅ 项目依赖配置（已完成）
2. ⏳ 统一响应封装类
3. ⏳ 全局异常处理器
4. ⏳ Spring Security安全配置
5. ⏳ JWT工具类和过滤器
6. ⏳ MyBatis Plus配置
7. ⏳ Redis配置和工具类
8. ⏳ 文件上传配置
9. ⏳ 跨域配置
10. ⏳ Knife4j接口文档配置
11. ⏳ 日志配置
12. ⏳ 参数校验配置

## 第六阶段：后端基础框架开发

### 6.1 统一响应封装
**响应类：** `com.property.common.core.domain.AjaxResult`
- 统一返回格式：{code, msg, data}
- 成功/失败方法封装
- 分页数据封装

### 6.2 异常处理
**全局异常处理器：** `com.property.common.exception.GlobalExceptionHandler`
- 业务异常处理
- 参数校验异常处理
- 系统异常处理
- 自定义异常类

### 6.3 安全配置
**Spring Security配置：** `com.property.framework.security.config.SecurityConfig`
- JWT认证过滤器
- 密码加密器
- 跨域处理
- 静态资源处理

### 6.4 JWT工具类
**JWT工具：** `com.property.common.utils.JwtUtils`
- 生成Token
- 解析Token
- 验证Token

### 6.5 数据权限
**数据权限注解：** `@DataScope`
- AOP切面实现
- 支持部门/个人数据权限

## 第七阶段：后端接口开发（共16个模块）

### 7.1 登录认证模块
**控制器：** `com.property.system.controller.SysLoginController`
- 登录接口
- 登出接口
- 获取用户信息接口
- 刷新Token接口

### 7.2 系统管理模块（用户、角色、菜单）
**控制器：** `com.property.system.controller.*`
- 用户管理CRUD
- 角色管理CRUD
- 菜单管理CRUD
- 权限分配接口

### 7.3 楼栋管理模块
**实体类：** `com.property.property.domain.Building`
**控制器：** `com.property.property.controller.BuildingController`
**服务层：** `com.property.property.service.IBuildingService`
**数据层：** `com.property.property.mapper.BuildingMapper`

**接口列表：**
- GET /property/building/list - 分页查询
- GET /property/building/{id} - 详情查询
- POST /property/building - 新增
- PUT /property/building - 修改
- DELETE /property/building/{ids} - 删除
- GET /property/building/all - 获取所有楼栋（下拉框用）

### 7.4 单元管理模块
**实体类：** `com.property.property.domain.Unit`
**控制器：** `com.property.property.controller.UnitController`
**服务层：** `com.property.property.service.IUnitService`
**数据层：** `com.property.property.mapper.UnitMapper`

**接口列表：**
- GET /property/unit/list - 分页查询
- GET /property/unit/listByBuilding/{buildingId} - 根据楼栋查询单元
- POST /property/unit - 新增
- PUT /property/unit - 修改
- DELETE /property/unit/{ids} - 删除

### 7.5 房产管理模块
**实体类：** `com.property.property.domain.House`
**控制器：** `com.property.property.controller.HouseController`
**服务层：** `com.property.property.service.IHouseService`
**数据层：** `com.property.property.mapper.HouseMapper`

### 7.6 住户管理模块
**实体类：** `com.property.system.domain.SysUser`（扩展）
**控制器：** `com.property.system.controller.ResidentController`
**服务层：** `com.property.system.service.IResidentService`

### 7.7 费用类型管理模块
**实体类：** `com.property.property.domain.FeeType`
**控制器：** `com.property.property.controller.FeeTypeController`
**服务层：** `com.property.property.service.IFeeTypeService`

### 7.8 账单管理模块
**实体类：** `com.property.property.domain.Bill`
**控制器：** `com.property.property.controller.BillController`
**服务层：** `com.property.property.service.IBillService`
**特殊功能：** 批量生成账单

### 7.9 虚拟钱包管理模块
**实体类：** `com.property.property.domain.Wallet`
**控制器：** `com.property.property.controller.WalletController`
**服务层：** `com.property.property.service.IWalletService`
**特殊功能：** 充值、支付、密码管理（使用乐观锁）

### 7.10 投诉管理模块
**实体类：** `com.property.property.domain.Complaint`
**控制器：** `com.property.property.controller.ComplaintController`
**服务层：** `com.property.property.service.IComplaintService`

### 7.11 维修管理模块
**实体类：** `com.property.property.domain.RepairOrder`
**控制器：** `com.property.property.controller.RepairController`
**服务层：** `com.property.property.service.IRepairService`

### 7.12 停车管理模块
**实体类：** `ParkingSpace`, `ParkingRental`
**控制器：** `com.property.property.controller.ParkingController`
**服务层：** `com.property.property.service.IParkingService`

### 7.13 公告管理模块
**实体类：** `Notice`, `NoticeRead`
**控制器：** `com.property.property.controller.NoticeController`
**服务层：** `com.property.property.service.INoticeService`

### 7.14 业主门户模块
**控制器：** `com.property.portal.controller.*`
- 我的账单接口
- 我的钱包接口
- 我的投诉接口
- 我的报修接口
- 在线支付接口

## 第八阶段：系统测试与优化

### 8.1 单元测试
- Service层单元测试
- Controller层接口测试
- 使用JUnit 5 + Mockito

### 8.2 接口测试
- 使用Knife4j进行接口测试
- Postman接口集合
- 接口文档完善

### 8.3 性能优化
- SQL查询优化
- Redis缓存策略
- 前端资源优化

### 8.4 安全加固
- XSS防护
- SQL注入防护
- 敏感数据加密

## 开发注意事项

1. **代码规范：** 严格遵循阿里巴巴Java开发手册
2. **注释规范：** 所有public方法必须添加完整的JavaDoc注释
3. **异常处理：** 统一异常处理，避免try-catch滥用
4. **日志记录：** 关键操作添加操作日志，使用@Log注解
5. **事务管理：** 涉及多表操作时使用@Transactional
6. **参数校验：** 使用@Validated进行参数校验，前端和后端双重校验
7. **接口版本：** 使用/api/v1作为接口前缀
8. **安全规范：** 所有接口必须认证（除登录、文件上传），敏感操作需要权限校验
9. **数据校验：** 使用乐观锁处理并发操作，特别是钱包余额、工单状态等
10. **响应格式：** 统一使用AjaxResult包装响应数据
11. **接口文档：** 所有接口必须完整记录在API接口文档中，包含请求参数、响应格式、权限要求等

## 开发进度记录

### ✅ 第一阶段：前端项目初始化（已完成）
**完成时间：** 2025年11月9日
**完成内容：**
- ✅ Vue 3 + Vite + Element Plus 项目初始化
- ✅ 项目目录结构规划
- ✅ 基础配置（Vite、ESLint、环境变量等）
- ✅ 路由配置与基础布局
- ✅ 登录页面与仪表板页面
- ✅ 状态管理（Pinia）配置
- ✅ 样式系统（SCSS）配置
- ✅ 工具函数库建立

**项目成果：**
- 开发环境搭建完毕
- 基础框架搭建完成
- 核心组件实现完成
- 项目结构规范建立

**详细文档：** [第一阶段开发总结](./开发阶段总结/第一阶段开发总结.md)

### ✅ 第二阶段：前端基础框架开发（已完成）
**完成时间：** 2025年11月9日
**完成内容：**
- ✅ 通用表格组件（Table）- 支持分页、排序、筛选、工具栏
- ✅ 通用表单组件（Form）- 支持18+控件类型、动态验证
- ✅ 文件上传组件（Upload）- 支持拖拽上传、图片预览
- ✅ 权限指令系统（v-permission、v-role、v-user-type）
- ✅ 权限工具函数（完整的权限检查和角色管理）
- ✅ 模拟登录系统（支持4种用户类型：管理员、物业经理、业主、维修工）
- ✅ 布局组件权限集成
- ✅ 权限常量与角色定义

**技术亮点：**
- 高度可配置的通用组件
- 完善的RBAC权限体系
- 声明式权限控制
- 开发环境模拟登录

**项目成果：**
- 通用组件库开发完成
- 权限管理系统实现
- 模拟登录系统上线
- 项目技术架构确立

**详细文档：** [第二阶段开发总结](./开发阶段总结/第二阶段开发总结.md)

### ✅ API接口文档设计（已完成）
**完成时间：** 2025年1月9日
**完成内容：**
- ✅ 完整的API接口文档设计（11个模块，100+接口）
- ✅ 严格遵循阿里巴巴Java开发手册规范
- ✅ 详细的请求参数、响应格式、权限要求说明
- ✅ 完善的错误码和状态码定义
- ✅ 接口安全规范和最佳实践
- ✅ 数据校验和并发处理规范

**技术亮点：**
- RESTful API设计规范
- 统一的响应格式和错误处理
- 完善的权限控制和数据校验
- 详细的业务流程接口覆盖
- 前后端分离架构优化

**项目成果：**
- 100+个完整的API接口定义
- 11个核心业务模块全覆盖
- 开发规范和最佳实践确立
- 为后端开发提供完整指导

**文档地址：** [API接口文档](./API接口文档.md)

### 🚀 第三阶段：前端页面开发（进行中）
**计划开始时间：** 2025年11月9日
**预计完成时间：** 2025年11月10日
**开发内容：**
- 🔄 登录页面功能完善
- ⏳ 系统管理页面（用户、角色、菜单管理）
- ⏳ 物业管理页面（楼栋、单元、房产、住户管理）
- ⏳ 财务管理页面（费用类型、账单、钱包管理）
- ⏳ 服务管理页面（投诉、维修管理）
- ⏳ 资源管理页面（停车、公告管理）

### ⏳ 第四阶段：前端特殊功能模块开发
**预计开始时间：** 2025年11月10日
**预计完成时间：** 2025年11月11日

### ⏳ 第五阶段：后端项目初始化与基础框架搭建
**预计开始时间：** 2025年11月11日
**预计完成时间：** 2025年11月12日

### ⏳ 第六阶段：后端基础框架开发
**预计开始时间：** 2025年11月12日
**预计完成时间：** 2025年11月13日

### ⏳ 第七阶段：后端接口开发
**预计开始时间：** 2025年11月13日
**预计完成时间：** 2025年11月15日

### ⏳ 第八阶段：系统测试与优化
**预计开始时间：** 2025年11月15日
**预计完成时间：** 2025年11月16日

## Git提交记录

### 第一阶段提交
```bash
🎉 初始化项目：社区物业管理系统前端
- 完成Vue 3 + Vite + Element Plus 项目初始化
- 建立基础项目结构和配置
- 实现登录页面和仪表板页面
- 配置路由和状态管理
```

### 第二阶段提交
```bash
🎉 第二阶段：前端基础框架开发完成
✨ 新增功能：
- 通用表格组件（Table）：支持分页、排序、筛选、工具栏
- 通用表单组件（Form）：支持18+表单控件类型、动态验证
- 文件上传组件（Upload）：支持拖拽上传、图片预览
- 权限指令系统：v-permission、v-role、v-user-type
- 权限工具函数：完整的权限检查和角色管理
- 模拟登录系统：支持4种用户类型（管理员、物业经理、业主、维修工）

🔧 技术优化：
- 完善组件化架构，提高代码复用性
- 实现细粒度权限控制，支持多级权限验证
- 优化用户状态管理，增强数据持久化
- 改进响应式布局，提升用户体验

🎯 完成进度：第二阶段100%完成，项目基础框架已就绪
```

## 项目仓库信息
- **GitHub仓库：** https://github.com/hhhqyy2468/smart-property-management
- **当前分支：** main
- **最新提交：** 6d3a6b0 - 第二阶段：前端基础框架开发完成
