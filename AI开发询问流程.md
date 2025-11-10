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

## 第五阶段：后端项目初始化与基础框架搭建

### 5.1 创建Spring Boot项目
使用Spring Initializr创建项目，选择以下依赖：
- Spring Boot 3.2.x
- Spring Web
- Spring Security
- Spring Data Redis
- MySQL Driver
- MyBatis Plus Boot Starter
- Validation
- Lombok

### 5.2 添加额外依赖
```xml
<!-- Hutool工具类 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.25</version>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Knife4j接口文档 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>

<!-- FastJSON -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.44</version>
</dependency>
```

### 5.3 项目结构规划
```
src/main/java/com/hyu/
├── common/              # 公共模块
│   ├── config/         # 配置类
│   ├── constant/       # 常量类
│   ├── core/           # 核心组件
│   ├── exception/      # 异常处理
│   └── utils/          # 工具类
├── framework/           # 框架核心
│   ├── security/       # 安全配置
│   ├── web/           # Web配置
│   └── aspectj/       # 切面
├── system/              # 系统管理模块
│   ├── controller/     # 控制器
│   ├── service/        # 服务层
│   ├── mapper/         # 数据访问层
│   └── domain/         # 实体类
├── property/            # 物业管理模块
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   └── domain/
├── portal/              # 业主门户模块
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   └── domain/
└── PropertyApplication.java  # 启动类
```

### 5.4 基础配置
- application.yml配置文件
- MyBatis Plus配置
- Redis配置
- JWT配置
- 跨域配置
- Knife4j配置

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
