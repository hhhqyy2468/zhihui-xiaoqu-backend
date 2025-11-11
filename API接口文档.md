# 社区物业管理系统 - API接口文档

## 文档说明

**版本：** V1.0.0
**作者：** 开发团队
**更新时间：** 2025-01-09
**遵循规范：** 阿里巴巴Java开发手册

### 接口规范
- **协议：** HTTP/HTTPS
- **数据格式：** JSON
- **字符编码：** UTF-8
- **API版本：** /api/v1
- **认证方式：** JWT Token

### 通用响应格式

#### 成功响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "timestamp": 1704787200000
}
```

#### 错误响应
```json
{
  "code": 400,
  "msg": "错误描述信息",
  "data": null,
  "timestamp": 1704787200000
}
```

#### 验证错误响应（422）
```json
{
  "code": 422,
  "msg": "参数验证失败",
  "data": {
    "username": ["用户名不能为空", "用户名长度必须在2-50之间"],
    "email": ["邮箱格式不正确"]
  },
  "timestamp": 1704787200000
}
```

### HTTP状态码说明
- **200**: 请求成功
- **400**: 请求参数错误
- **401**: 未授权/Token已过期，需要重新登录
- **403**: 无权限访问，权限不足
- **404**: 请求的资源不存在
- **405**: 请求方法不允许
- **408**: 请求超时
- **409**: 资源冲突（如数据已存在）
- **422**: 请求参数验证失败
- **429**: 请求过于频繁，需要限流
- **500**: 服务器内部错误
- **502**: 网关错误
- **503**: 服务不可用
- **504**: 网关超时

### 业务错误码说明

#### 认证相关（1000-1099）
- **1001**: 用户名或密码错误
- **1002**: 账号已被禁用
- **1003**: 账号已过期
- **1004**: Token已过期
- **1005**: Token无效
- **1006**: 未登录或登录已过期

#### 权限相关（1100-1199）
- **1101**: 无权限访问
- **1102**: 无权限执行此操作
- **1103**: 角色权限不足

#### 参数验证相关（1200-1299）
- **1201**: 请求参数缺失
- **1202**: 请求参数格式错误
- **1203**: 请求参数值无效
- **1204**: 必填字段未填写

#### 数据相关（1300-1399）
- **1301**: 数据不存在
- **1302**: 数据已存在
- **1303**: 数据已被使用，无法删除
- **1304**: 数据状态不允许此操作
- **1305**: 数据关联关系错误

#### 业务逻辑相关（1400-1499）
- **1401**: 操作失败
- **1402**: 操作不允许
- **1403**: 业务规则校验失败
- **1404**: 数据完整性校验失败

#### 系统相关（1500-1599）
- **1501**: 系统异常
- **1502**: 数据库操作失败
- **1503**: 文件上传失败
- **1504**: 文件下载失败
- **1505**: 外部服务调用失败

---

## 1. 认证模块

### 1.1 用户登录
**接口地址：** `POST /api/v1/auth/login`
**接口描述：** 用户登录获取访问令牌
**是否认证：** 否

**请求参数：**
```json
{
  "username": "string",  // 用户名，必填，长度2-50
  "password": "string",  // 密码，必填，长度6-20
  "captcha": "string",   // 验证码，选填
  "rememberMe": boolean  // 记住我，选填，默认false
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT令牌
    "refreshToken": "refresh_token_string",  // 刷新令牌
    "expiresIn": 7200,  // 过期时间（秒）
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "系统管理员",
      "userType": 1,  // 1:管理员 2:物业管理员 3:业主 4:维修人员
      "avatar": "http://example.com/avatar.jpg",
      "roles": ["admin"],
      "permissions": ["system:user:view", "system:user:add"]
    }
  }
}
```

### 1.2 刷新Token
**接口地址：** `POST /api/v1/auth/refresh`
**接口描述：** 刷新访问令牌
**是否认证：** 否

**请求参数：**
```json
{
  "refreshToken": "string"  // 刷新令牌，必填
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // 新JWT令牌
    "refreshToken": "new_refresh_token_string",          // 新刷新令牌
    "expiresIn": 7200  // 过期时间（秒）
  }
}
```

### 1.3 用户登出
**接口地址：** `POST /api/v1/auth/logout`
**接口描述：** 用户登出，将token加入黑名单
**是否认证：** 是

**请求头：**
```
Authorization: Bearer {token}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "登出成功",
  "data": null
}
```

### 1.4 获取当前用户信息
**接口地址：** `GET /api/v1/auth/info`
**接口描述：** 获取当前登录用户信息
**是否认证：** 是

**请求头：**
```
Authorization: Bearer {token}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "phone": "13800138000",
    "email": "admin@example.com",
    "userType": 1,  // 1:管理员 2:物业管理员 3:业主 4:维修人员
    "avatar": "http://example.com/avatar.jpg",
    "dept": {
      "deptId": 1,
      "deptName": "物业总公司"
    },
    "roles": ["admin"],
    "permissions": ["system:user:view", "system:user:add"],
    "lastLoginTime": "2025-01-09 10:00:00",
    "lastLoginIp": "192.168.1.100",
    "createTime": "2025-01-01 09:00:00"
  }
}
```

### 1.5 修改密码
**接口地址：** `PUT /api/v1/auth/password`
**接口描述：** 修改当前用户密码
**是否认证：** 是

**请求参数：**
```json
{
  "oldPassword": "string",     // 原密码，必填
  "newPassword": "string",     // 新密码，必填，长度6-20
  "confirmPassword": "string"  // 确认新密码，必填
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "密码修改成功",
  "data": null
}
```

### 1.6 修改个人信息
**接口地址：** `PUT /api/v1/auth/profile`
**接口描述：** 修改当前用户个人信息
**是否认证：** 是

**请求参数：**
```json
{
  "realName": "string",    // 真实姓名，选填，长度2-20
  "phone": "string",       // 手机号，选填，11位数字
  "email": "string",       // 邮箱，选填
  "avatar": "string",      // 头像URL，选填
  "remark": "string"       // 备注，选填，长度0-500
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "信息修改成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "phone": "13800138000",
    "email": "admin@example.com",
    "avatar": "http://example.com/avatar.jpg",
    "updateTime": "2025-01-09 10:30:00"
  }
}
```

### 1.7 获取验证码
**接口地址：** `GET /api/v1/auth/captcha`
**接口描述：** 获取登录验证码图片
**是否认证：** 否

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "captchaId": "captcha_20250109_001",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",  // Base64图片
    "expireTime": 300  // 验证码过期时间（秒）
  }
}
```

### 1.8 验证token有效性
**接口地址：** `GET /api/v1/auth/verify`
**接口描述：** 验证token是否有效
**是否认证：** 是

**请求头：**
```
Authorization: Bearer {token}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "Token有效",
  "data": {
    "valid": true,
    "userId": 1,
    "username": "admin",
    "expireTime": 1704797999000  // 过期时间戳
  }
}
```

---

## 2. 系统管理模块

### 2.1 用户管理

#### 2.1.1 分页查询用户列表
**接口地址：** `GET /api/v1/system/user/list`
**接口描述：** 分页查询用户列表
**是否认证：** 是
**权限要求：** system:user:view

**请求参数：**
```
pageNum: int        // 页码，默认1
pageSize: int       // 每页条数，默认10
username: string    // 用户名，模糊查询
realName: string    // 真实姓名，模糊查询
phone: string       // 手机号，模糊查询
userType: int       // 用户类型，1:管理员 2:物业管理员 3:业主 4:维修人员
status: int         // 状态，0:禁用 1:正常
beginTime: string   // 开始时间，格式：yyyy-MM-dd
endTime: string     // 结束时间，格式：yyyy-MM-dd
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "userId": 1,
        "username": "admin",
        "realName": "系统管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "userType": 1,
        "status": 1,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00",
        "roles": ["admin"],
        "dept": {
          "deptId": 1,
          "deptName": "物业总公司"
        }
      }
    ]
  }
}
```

#### 2.1.2 新增用户
**接口地址：** `POST /api/v1/system/user`
**接口描述：** 新增用户
**是否认证：** 是
**权限要求：** system:user:add

**请求参数：**
```json
{
  "username": "string",     // 用户名，必填，长度2-50，唯一
  "password": "string",     // 密码，必填，长度6-20
  "realName": "string",     // 真实姓名，必填，长度2-20
  "phone": "string",        // 手机号，必填，11位数字
  "email": "string",        // 邮箱，选填
  "userType": 1,           // 用户类型，必填
  "status": 1,             // 状态，必填，0:禁用 1:正常
  "roleIds": [1, 2],       // 角色ID数组，必填
  "deptId": 1,             // 部门ID，必填
  "remark": "string"       // 备注，选填
}
```

#### 2.1.3 修改用户
**接口地址：** `PUT /api/v1/system/user`
**接口描述：** 修改用户信息
**是否认证：** 是
**权限要求：** system:user:edit

#### 2.1.4 删除用户
**接口地址：** `DELETE /api/v1/system/user/{userIds}`
**接口描述：** 批量删除用户
**是否认证：** 是
**权限要求：** system:user:delete

#### 2.1.5 重置密码
**接口地址：** `PUT /api/v1/system/user/resetPwd`
**接口描述：** 重置用户密码
**是否认证：** 是
**权限要求：** system:user:resetPassword

**请求参数：**
```json
{
  "userId": 1,           // 用户ID，必填
  "newPassword": "string" // 新密码，必填，长度6-20
}
```

### 2.2 角色管理

#### 2.2.1 分页查询角色列表
**接口地址：** `GET /api/v1/system/role/list`
**接口描述：** 分页查询角色列表
**是否认证：** 是
**权限要求：** system:role:view

**请求参数：**
```
pageNum: int       // 页码，默认1
pageSize: int      // 每页条数，默认10
roleName: string   // 角色名称，模糊查询
status: int        // 状态，0:禁用 1:正常
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 10,
    "rows": [
      {
        "roleId": 1,
        "roleName": "系统管理员",
        "roleKey": "admin",
        "roleSort": 1,
        "status": 1,
        "remark": "系统管理员角色",
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00",
        "menuIds": [1, 2, 3, 4]  // 关联的菜单ID数组
      }
    ]
  }
}
```

#### 2.2.2 新增角色
**接口地址：** `POST /api/v1/system/role`
**接口描述：** 新增角色
**是否认证：** 是
**权限要求：** system:role:add

**请求参数：**
```json
{
  "roleName": "string",    // 角色名称，必填，长度2-30
  "roleKey": "string",     // 权限字符，必填，长度2-100，唯一
  "roleSort": 1,          // 显示顺序，必填
  "status": 1,            // 状态，必填
  "remark": "string",      // 备注，选填
  "menuIds": [1, 2, 3]   // 菜单ID数组，必填
}
```

### 2.3 菜单管理

#### 2.3.1 获取菜单树
**接口地址：** `GET /api/v1/system/menu/tree`
**接口描述：** 获取菜单树结构
**是否认证：** 是
**权限要求：** system:menu:view

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "menuId": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "orderNum": 1,
      "path": "/system",
      "component": "",
      "menuType": "M",  // M:目录 C:菜单 F:按钮
      "visible": "0",   // 0:显示 1:隐藏
      "status": "0",    // 0:正常 1:停用
      "perms": "",
      "icon": "system",
      "children": [
        {
          "menuId": 2,
          "menuName": "用户管理",
          "parentId": 1,
          "orderNum": 1,
          "path": "user",
          "component": "system/user/index",
          "menuType": "C",
          "visible": "0",
          "status": "0",
          "perms": "system:user:list",
          "icon": "user",
          "children": []
        }
      ]
    }
  ]
}
```

### 2.4 字典管理

#### 2.4.1 获取字典类型列表
**接口地址：** `GET /api/v1/system/dict/type/list`
**接口描述：** 获取字典类型列表
**是否认证：** 是
**权限要求：** system:dict:list

#### 2.4.2 根据字典类型获取字典数据
**接口地址：** `GET /api/v1/system/dict/data/{dictType}`
**接口描述：** 根据字典类型获取字典数据
**是否认证：** 是
**权限要求：** system:dict:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "dictCode": 1,
      "dictSort": 1,
      "dictLabel": "正常",
      "dictValue": "0",
      "dictType": "sys_normal_status",
      "cssClass": "",
      "listClass": "primary",
      "isDefault": "Y",
      "status": "0",
      "remark": "正常状态"
    }
  ]
}
```

---

## 3. 物业管理模块

### 3.1 楼栋管理

#### 3.1.1 分页查询楼栋列表
**接口地址：** `GET /api/v1/property/building/list`
**接口描述：** 分页查询楼栋列表
**是否认证：** 是
**权限要求：** property:building:list

**请求参数：**
```
pageNum: int           // 页码，默认1
pageSize: int          // 每页条数，默认10
buildingCode: string   // 楼栋编号，模糊查询
buildingName: string   // 楼栋名称，模糊查询
address: string        // 地址，模糊查询
buildYearStart: int    // 建筑年份开始
buildYearEnd: int      // 建筑年份结束
status: int           // 状态，0:停用 1:正常
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 20,
    "rows": [
      {
        "buildingId": 1,
        "buildingCode": "B001",
        "buildingName": "1号楼",
        "floors": 18,
        "units": 3,
        "totalArea": 5400.50,
        "address": "北京市朝阳区XX街道XX号",
        "buildYear": 2020,
        "structureType": "钢筋混凝土",
        "usageType": "住宅",
        "status": 1,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00",
        "remark": "1号楼相关信息"
      }
    ]
  }
}
```

#### 3.1.2 新增楼栋
**接口地址：** `POST /api/v1/property/building`
**接口描述：** 新增楼栋
**是否认证：** 是
**权限要求：** property:building:add

**请求参数：**
```json
{
  "buildingCode": "string",    // 楼栋编号，必填，唯一，长度2-20
  "buildingName": "string",    // 楼栋名称，必填，长度2-50
  "floors": 18,               // 楼层数，必填，1-99
  "units": 3,                 // 单元数，必填，1-20
  "totalArea": 5400.50,       // 总建筑面积，必填，精确到2位小数
  "address": "string",         // 详细地址，必填，长度2-200
  "buildYear": 2020,          // 建筑年份，必填，1900-当前年份
  "structureType": "string",  // 结构类型，选填
  "usageType": "string",      // 使用类型，选填
  "status": 1,                // 状态，必填
  "remark": "string"          // 备注，选填
}
```

#### 3.1.3 修改楼栋
**接口地址：** `PUT /api/v1/property/building`
**接口描述：** 修改楼栋信息
**是否认证：** 是
**权限要求：** property:building:edit

#### 3.1.4 删除楼栋
**接口地址：** `DELETE /api/v1/property/building/{buildingIds}`
**接口描述：** 批量删除楼栋
**是否认证：** 是
**权限要求：** property:building:delete

#### 3.1.5 获取所有楼栋（下拉框用）
**接口地址：** `GET /api/v1/property/building/all`
**接口描述：** 获取所有正常状态的楼栋
**是否认证：** 是
**权限要求：** property:building:list

### 3.2 单元管理

#### 3.2.1 分页查询单元列表
**接口地址：** `GET /api/v1/property/unit/list`
**接口描述：** 分页查询单元列表
**是否认证：** 是
**权限要求：** property:unit:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
unitCode: string      // 单元编号，模糊查询
unitName: string      // 单元名称，模糊查询
buildingId: int       // 楼栋ID，精确查询
status: int          // 状态
```

#### 3.2.2 根据楼栋查询单元列表
**接口地址：** `GET /api/v1/property/unit/listByBuilding/{buildingId}`
**接口描述：** 根据楼栋ID获取单元列表（下拉框用）
**是否认证：** 是
**权限要求：** property:unit:list

#### 3.2.3 新增单元
**接口地址：** `POST /api/v1/property/unit`
**接口描述：** 新增单元
**是否认证：** 是
**权限要求：** property:unit:add

**请求参数：**
```json
{
  "buildingId": 1,          // 楼栋ID，必填
  "unitCode": "string",      // 单元编号，必填，长度2-20
  "unitName": "string",      // 单元名称，必填，长度2-50
  "floors": 18,             // 楼层数，必填，1-99
  "roomsPerFloor": 4,       // 每层房间数，必填，1-20
  "status": 1,              // 状态，必填
  "remark": "string"        // 备注，选填
}
```

#### 3.2.4 修改单元
**接口地址：** `PUT /api/v1/property/unit`
**接口描述：** 修改单元信息
**是否认证：** 是
**权限要求：** property:unit:edit

**请求参数：**
```json
{
  "unitId": 1,              // 单元ID，必填
  "buildingId": 1,          // 楼栋ID，必填
  "unitCode": "string",      // 单元编号，必填，长度2-20
  "unitName": "string",      // 单元名称，必填，长度2-50
  "floors": 18,             // 楼层数，必填，1-99
  "roomsPerFloor": 4,       // 每层房间数，必填，1-20
  "status": 1,              // 状态，必填
  "remark": "string"        // 备注，选填
}
```

#### 3.2.5 删除单元
**接口地址：** `DELETE /api/v1/property/unit/{unitIds}`
**接口描述：** 删除单元（支持批量删除）
**是否认证：** 是
**权限要求：** property:unit:remove

**请求参数：**
```
unitIds: string  // 单元ID数组，逗号分隔，如"1,2,3"
```

#### 3.2.6 获取单元详情
**接口地址：** `GET /api/v1/property/unit/{unitId}`
**接口描述：** 获取单元详细信息
**是否认证：** 是
**权限要求：** property:unit:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "unitId": 1,
    "buildingId": 1,
    "buildingName": "1号楼",
    "unitCode": "U001",
    "unitName": "1单元",
    "floors": 18,
    "roomsPerFloor": 4,
    "totalRooms": 72,
    "status": 1,
    "createTime": "2025-01-09 10:00:00",
    "updateTime": "2025-01-09 10:00:00",
    "remark": "1单元相关信息"
  }
}
```

### 3.3 房产管理

#### 3.3.1 分页查询房产列表
**接口地址：** `GET /api/v1/property/house/list`
**接口描述：** 分页查询房产列表
**是否认证：** 是
**权限要求：** property:house:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
houseCode: string     // 房间编号，模糊查询
buildingId: int       // 楼栋ID，精确查询
unitId: int           // 单元ID，精确查询
floorNum: int         // 楼层，精确查询
houseStatus: int      // 房产状态，1:空置 2:已售 3:已租 4:自住
ownerName: string     // 产权人姓名，模糊查询
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "houseId": 1,
        "houseCode": "1-1-101",
        "buildingId": 1,
        "buildingName": "1号楼",
        "unitId": 1,
        "unitName": "1单元",
        "floorNum": 1,
        "roomNum": "101",
        "houseType": "三室两厅",
        "buildArea": 128.50,
        "useArea": 105.30,
        "houseStatus": 4,
        "ownerId": 1,
        "ownerName": "张三",
        "ownerPhone": "13800138000",
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 3.3.2 新增房产
**接口地址：** `POST /api/v1/property/house`
**接口描述：** 新增房产
**是否认证：** 是
**权限要求：** property:house:add

**请求参数：**
```json
{
  "buildingId": 1,         // 楼栋ID，必填
  "unitId": 1,             // 单元ID，必填
  "floorNum": 1,           // 楼层，必填，1-99
  "roomNum": "101",        // 房间号，必填，长度1-10
  "houseType": "string",   // 户型，选填，长度2-50
  "buildArea": 128.50,     // 建筑面积，必填，精确到2位小数
  "useArea": 105.30,       // 使用面积，选填，精确到2位小数
  "houseStatus": 1,        // 房产状态，必填
  "ownerId": 1,            // 产权人ID，选填
  "remark": "string"       // 备注，选填
}
```

#### 3.3.3 修改房产
**接口地址：** `PUT /api/v1/property/house`
**接口描述：** 修改房产信息
**是否认证：** 是
**权限要求：** property:house:edit

**请求参数：**
```json
{
  "houseId": 1,            // 房产ID，必填
  "buildingId": 1,         // 楼栋ID，必填
  "unitId": 1,             // 单元ID，必填
  "floorNum": 1,           // 楼层，必填，1-99
  "roomNum": "101",        // 房间号，必填，长度1-10
  "houseType": "string",   // 户型，选填，长度2-50
  "buildArea": 128.50,     // 建筑面积，必填，精确到2位小数
  "useArea": 105.30,       // 使用面积，选填，精确到2位小数
  "houseStatus": 1,        // 房产状态，必填
  "ownerId": 1,            // 产权人ID，选填
  "remark": "string"       // 备注，选填
}
```

#### 3.3.4 删除房产
**接口地址：** `DELETE /api/v1/property/house/{houseIds}`
**接口描述：** 删除房产（支持批量删除）
**是否认证：** 是
**权限要求：** property:house:remove

**请求参数：**
```
houseIds: string  // 房产ID数组，逗号分隔，如"1,2,3"
```

#### 3.3.5 获取房产详情
**接口地址：** `GET /api/v1/property/house/{houseId}`
**接口描述：** 获取房产详细信息
**是否认证：** 是
**权限要求：** property:house:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "houseId": 1,
    "houseCode": "1-1-101",
    "buildingId": 1,
    "buildingName": "1号楼",
    "unitId": 1,
    "unitName": "1单元",
    "floorNum": 1,
    "roomNum": "101",
    "houseType": "三室两厅",
    "buildArea": 128.50,
    "useArea": 105.30,
    "houseStatus": 1,
    "ownerId": 1,
    "ownerName": "张三",
    "ownerPhone": "13800138000",
    "createTime": "2025-01-09 10:00:00",
    "updateTime": "2025-01-09 10:00:00",
    "remark": "101室相关信息"
  }
}
```

#### 3.3.6 根据单元查询房产列表
**接口地址：** `GET /api/v1/property/house/listByUnit/{unitId}`
**接口描述：** 根据单元ID获取房产列表（下拉框用）
**是否认证：** 是
**权限要求：** property:house:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "houseId": 1,
      "houseCode": "1-1-101",
      "floorNum": 1,
      "roomNum": "101",
      "houseType": "三室两厅",
      "houseStatus": 1
    }
  ]
}
```

### 3.4 业主管理

#### 3.4.1 分页查询业主列表
**接口地址：** `GET /api/v1/property/owner/list`
**接口描述：** 分页查询业主列表
**是否认证：** 是
**权限要求：** property:owner:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
ownerName: string     // 业主姓名，模糊查询
phone: string         // 手机号，模糊查询
idCard: string        // 身份证号，模糊查询
houseCode: string     // 房产编号，模糊查询
ownerStatus: int      // 业主状态，1:正常 2:搬离
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "rows": [
      {
        "ownerId": 1,
        "ownerName": "张三",
        "phone": "13800138000",
        "idCard": "110101199001011234",
        "email": "zhangsan@example.com",
        "ownerType": 1,      // 1:业主 2:租户
        "ownerStatus": 1,    // 1:正常 2:搬离
        "moveInDate": "2020-01-01",
        "moveOutDate": null,
        "houses": [          // 关联房产
          {
            "houseId": 1,
            "houseCode": "1-1-101",
            "buildingName": "1号楼",
            "relationType": 1  // 1:产权人 2:使用人
          }
        ],
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 3.4.2 新增业主
**接口地址：** `POST /api/v1/property/owner`
**接口描述：** 新增业主
**是否认证：** 是
**权限要求：** property:owner:add

**请求参数：**
```json
{
  "ownerName": "string",    // 业主姓名，必填，长度2-20
  "phone": "string",        // 手机号，必填，11位数字
  "idCard": "string",       // 身份证号，必填，18位
  "email": "string",        // 邮箱，选填
  "ownerType": 1,          // 业主类型，必填，1:业主 2:租户
  "moveInDate": "string",   // 入住日期，必填，格式：yyyy-MM-dd
  "houseIds": [1, 2],      // 关联房产ID数组，必填
  "relationTypes": [1, 2],  // 关系类型数组，必填，与houseIds对应
  "remark": "string"       // 备注，选填
}
```

#### 3.4.3 业主搬离
**接口地址：** `PUT /api/v1/property/owner/moveOut/{ownerId}`
**接口描述：** 业主搬离处理
**是否认证：** 是
**权限要求：** property:owner:edit

**请求参数：**
```json
{
  "moveOutDate": "string",  // 搬离日期，必填，格式：yyyy-MM-dd
  "moveOutReason": "string" // 搬离原因，选填，长度2-200
}
```

---

## 4. 费用管理模块

### 4.1 费用类型管理

#### 4.1.1 分页查询费用类型列表
**接口地址：** `GET /api/v1/property/feeType/list`
**接口描述：** 分页查询费用类型列表
**是否认证：** 是
**权限要求：** property:feeType:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
feeName: string       // 费用名称，模糊查询
feeCode: string       // 费用编码，模糊查询
status: int          // 状态
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 10,
    "rows": [
      {
        "feeTypeId": 1,
        "feeName": "物业费",
        "feeCode": "WYF",
        "unitPrice": 2.50,
        "unitType": "元/平方米/月",
        "billingCycle": 1,  // 1:月 2:季 3:年
        "status": 1,
        "description": "小区物业管理费用",
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 4.1.2 新增费用类型
**接口地址：** `POST /api/v1/property/feeType`
**接口描述：** 新增费用类型
**是否认证：** 是
**权限要求：** property:feeType:add

**请求参数：**
```json
{
  "feeName": "string",      // 费用名称，必填，长度2-50
  "feeCode": "string",      // 费用编码，必填，长度2-20，唯一
  "unitPrice": 2.50,        // 单价，必填，精确到2位小数
  "unitType": "string",     // 计费单位，必填，长度2-50
  "billingCycle": 1,        // 计费周期，必填，1:月 2:季 3:年
  "status": 1,             // 状态，必填
  "description": "string"   // 费用说明，选填，长度2-500
}
```

#### 4.1.3 修改费用类型
**接口地址：** `PUT /api/v1/property/feeType`
**接口描述：** 修改费用类型信息
**是否认证：** 是
**权限要求：** property:feeType:edit

**请求参数：**
```json
{
  "feeTypeId": 1,           // 费用类型ID，必填
  "feeName": "string",      // 费用名称，必填，长度2-50
  "feeCode": "string",      // 费用编码，必填，长度2-20，唯一
  "unitPrice": 2.50,        // 单价，必填，精确到2位小数
  "unitType": "string",     // 计费单位，必填，长度2-50
  "billingCycle": 1,        // 计费周期，必填，1:月 2:季 3:年
  "status": 1,             // 状态，必填
  "description": "string"   // 费用说明，选填，长度2-500
}
```

#### 4.1.4 删除费用类型
**接口地址：** `DELETE /api/v1/property/feeType/{feeTypeIds}`
**接口描述：** 删除费用类型（支持批量删除）
**是否认证：** 是
**权限要求：** property:feeType:remove

**请求参数：**
```
feeTypeIds: string  // 费用类型ID数组，逗号分隔，如"1,2,3"
```

#### 4.1.5 获取费用类型详情
**接口地址：** `GET /api/v1/property/feeType/{feeTypeId}`
**接口描述：** 获取费用类型详细信息
**是否认证：** 是
**权限要求：** property:feeType:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "feeTypeId": 1,
    "feeName": "物业费",
    "feeCode": "WYF",
    "unitPrice": 2.50,
    "unitType": "元/平方米/月",
    "billingCycle": 1,
    "status": 1,
    "description": "小区物业管理费用",
    "createTime": "2025-01-09 10:00:00",
    "updateTime": "2025-01-09 10:00:00"
  }
}
```

#### 4.1.6 获取所有费用类型（下拉框用）
**接口地址：** `GET /api/v1/property/feeType/all`
**接口描述：** 获取所有费用类型列表（下拉框用）
**是否认证：** 是
**权限要求：** property:feeType:list

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "feeTypeId": 1,
      "feeName": "物业费",
      "feeCode": "WYF",
      "unitPrice": 2.50,
      "status": 1
    },
    {
      "feeTypeId": 2,
      "feeName": "水费",
      "feeCode": "SF",
      "unitPrice": 4.50,
      "status": 1
    }
  ]
}
```

### 4.2 账单管理

#### 4.2.1 分页查询账单列表
**接口地址：** `GET /api/v1/property/bill/list`
**接口描述：** 分页查询账单列表
**是否认证：** 是
**权限要求：** property:bill:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
billNo: string        // 账单编号，模糊查询
ownerName: string     // 业主姓名，模糊查询
houseCode: string     // 房产编号，模糊查询
feeTypeId: int        // 费用类型ID，精确查询
billStatus: int       // 账单状态，1:待缴费 2:已缴费 3:已超期
billPeriod: string    // 账期，格式：yyyy-MM
beginTime: string     // 开始时间
endTime: string       // 结束时间
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 200,
    "rows": [
      {
        "billId": 1,
        "billNo": "BILL202501001",
        "ownerId": 1,
        "ownerName": "张三",
        "ownerPhone": "13800138000",
        "houseId": 1,
        "houseCode": "1-1-101",
        "buildingName": "1号楼",
        "feeTypeId": 1,
        "feeName": "物业费",
        "billPeriod": "2025-01",
        "amount": 128.50,
        "paidAmount": 0.00,
        "billStatus": 1,
        "dueDate": "2025-01-31",
        "paidDate": null,
        "receiptNo": null,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 4.2.2 生成账单
**接口地址：** `POST /api/v1/property/bill/generate`
**接口描述：** 批量生成账单
**是否认证：** 是
**权限要求：** property:bill:generate

**请求参数：**
```json
{
  "feeTypeId": 1,         // 费用类型ID，必填
  "billPeriod": "2025-01", // 账期，必填，格式：yyyy-MM
  "dueDate": "2025-01-31", // 缴费截止日期，必填
  "buildingIds": [1, 2],  // 楼栋ID数组，选填，为空则全部楼栋
  "unitIds": [1, 2],      // 单元ID数组，选填
  "houseIds": [1, 2, 3]   // 房产ID数组，选填
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "账单生成成功",
  "data": {
    "totalCount": 100,    // 总生成数量
    "successCount": 98,   // 成功数量
    "failCount": 2,       // 失败数量
    "failList": [         // 失败列表
      {
        "houseId": 1,
        "houseCode": "1-1-101",
        "reason": "该房产已存在账单"
      }
    ]
  }
}
```

#### 4.2.3 在线缴费
**接口地址：** `POST /api/v1/property/bill/pay`
**接口描述：** 在线缴费（使用虚拟钱包）
**是否认证：** 是
**权限要求：** property:bill:pay

**请求参数：**
```json
{
  "billIds": [1, 2, 3],     // 账单ID数组，必填
  "payPassword": "123456"   // 支付密码，必填，6位数字
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "缴费成功",
  "data": {
    "payId": 1001,          // 支付记录ID
    "totalAmount": 385.50,  // 总支付金额
    "paidBills": [          // 已支付账单
      {
        "billId": 1,
        "billNo": "BILL202501001",
        "amount": 128.50,
        "receiptNo": "RCP202501001"  // 收据编号
      }
    ],
    "failBills": [],        // 支付失败账单
    "walletBalance": 500.00 // 支付后钱包余额
  }
}
```

### 4.3 虚拟钱包管理

#### 4.3.1 分页查询钱包列表
**接口地址：** `GET /api/v1/property/wallet/list`
**接口描述：** 分页查询钱包列表
**是否认证：** 是
**权限要求：** property:wallet:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
ownerName: string     // 业主姓名，模糊查询
phone: string         // 手机号，模糊查询
walletStatus: int     // 钱包状态，1:正常 2:冻结
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "rows": [
      {
        "walletId": 1,
        "ownerId": 1,
        "ownerName": "张三",
        "phone": "13800138000",
        "balance": 500.00,
        "totalRecharge": 1000.00,
        "totalConsume": 500.00,
        "walletStatus": 1,
        "hasPayPassword": true,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 4.3.2 钱包充值
**接口地址：** `POST /api/v1/property/wallet/recharge`
**接口描述：** 钱包充值（模拟充值）
**是否认证：** 是
**权限要求：** property:wallet:recharge

**请求参数：**
```json
{
  "walletId": 1,          // 钱包ID，必填
  "rechargeAmount": 100.00, // 充值金额，必填，0.01-10000
  "payPassword": "123456"  // 支付密码，必填
}
```

#### 4.3.3 重置支付密码
**接口地址：** `PUT /api/v1/property/wallet/resetPassword/{walletId}`
**接口描述：** 重置支付密码
**是否认证：** 是
**权限要求：** property:wallet:resetPassword

**请求参数：**
```json
{
  "newPassword": "123456"  // 新支付密码，必填，6位数字
}
```

#### 4.3.4 获取钱包交易明细
**接口地址：** `GET /api/v1/property/wallet/transactions/{walletId}`
**接口描述：** 获取钱包交易明细
**是否认证：** 是
**权限要求：** property:wallet:detail

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
transType: string     // 交易类型，RECHARGE:充值 CONSUME:消费
beginTime: string     // 开始时间
endTime: string       // 结束时间
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 30,
    "rows": [
      {
        "transId": 1,
        "transNo": "TRX202501001",
        "transType": "RECHARGE",
        "transAmount": 100.00,
        "balanceBefore": 400.00,
        "balanceAfter": 500.00,
        "transTime": "2025-01-09 10:00:00",
        "remark": "钱包充值",
        "relatedId": 1,        // 关联ID（如账单ID）
        "relatedNo": "BILL202501001"  // 关联编号
      }
    ]
  }
}
```

---

## 5. 服务管理模块

### 5.1 投诉管理

#### 5.1.1 分页查询投诉列表
**接口地址：** `GET /api/v1/property/complaint/list`
**接口描述：** 分页查询投诉列表
**是否认证：** 是
**权限要求：** property:complaint:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
complaintNo: string   // 投诉单号，模糊查询
complainant: string   // 投诉人，模糊查询
houseCode: string     // 房产编号，模糊查询
complaintType: string // 投诉类型，精确查询
urgentLevel: int      // 紧急程度，1:普通 2:紧急
complaintStatus: int  // 投诉状态，1:待处理 2:处理中 3:已处理 4:已关闭
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "rows": [
      {
        "complaintId": 1,
        "complaintNo": "COMP202501001",
        "ownerId": 1,
        "complainant": "张三",
        "phone": "13800138000",
        "houseId": 1,
        "houseCode": "1-1-101",
        "complaintType": "环境卫生",
        "urgentLevel": 2,
        "complaintStatus": 1,
        "complaintContent": "楼道垃圾堆积严重",
        "images": ["http://example.com/image1.jpg"],
        "handleBy": null,
        "handleTime": null,
        "handleResult": null,
        "handleImages": [],
        "satisfaction": null,
        "evaluateTime": null,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 5.1.2 提交投诉（业主）
**接口地址：** `POST /api/v1/property/complaint`
**接口描述：** 提交投诉申请
**是否认证：** 是
**权限要求：** 无（业主可提交）

**请求参数：**
```json
{
  "complaintType": "string",   // 投诉类型，必填，长度2-50
  "urgentLevel": 1,           // 紧急程度，必填，1:普通 2:紧急
  "complaintContent": "string", // 投诉内容，必填，长度10-1000
  "houseId": 1,               // 房产ID，必填
  "images": ["string"]         // 图片URL数组，选填，最多5张
}
```

#### 5.1.3 分配处理人
**接口地址：** `PUT /api/v1/property/complaint/assign/{complaintId}`
**接口描述：** 分配投诉处理人
**是否认证：** 是
**权限要求：** property:complaint:assign

**请求参数：**
```json
{
  "handleBy": 1,            // 处理人ID，必填
  "remark": "string"        // 分配备注，选填
}
```

#### 5.1.4 处理投诉
**接口地址：** `PUT /api/v1/property/complaint/handle/{complaintId}`
**接口描述：** 处理投诉
**是否认证：** 是
**权限要求：** property:complaint:handle

**请求参数：**
```json
{
  "handleResult": "string",  // 处理结果，必填，长度10-500
  "handleImages": ["string"], // 处理图片URL数组，选填，最多5张
  "remark": "string"        // 处理备注，选填
}
```

#### 5.1.5 投诉评价（业主）
**接口地址：** `PUT /api/v1/property/complaint/evaluate/{complaintId}`
**接口描述：** 对投诉处理结果进行评价
**是否认证：** 是
**权限要求：** 无（业主可评价）

**请求参数：**
```json
{
  "satisfaction": 1,       // 满意度，必填，1:满意 2:一般 3:不满意
  "evaluateContent": "string" // 评价内容，选填，长度10-200
}
```

### 5.2 维修管理

#### 5.2.1 分页查询维修工单列表
**接口地址：** `GET /api/v1/property/repair/list`
**接口描述：** 分页查询维修工单列表
**是否认证：** 是
**权限要求：** property:repair:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
repairNo: string      // 工单编号，模糊查询
reporter: string      // 报修人，模糊查询
houseCode: string     // 房产编号，模糊查询
repairType: string    // 维修类型，精确查询
urgentLevel: int      // 紧急程度，1:普通 2:紧急
repairStatus: int     // 工单状态，1:待派工 2:已派工 3:进行中 4:待验收 5:已完成
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 80,
    "rows": [
      {
        "repairId": 1,
        "repairNo": "REP202501001",
        "ownerId": 1,
        "reporter": "张三",
        "phone": "13800138000",
        "houseId": 1,
        "houseCode": "1-1-101",
        "repairType": "水电",
        "urgentLevel": 1,
        "repairStatus": 1,
        "faultDesc": "水龙头漏水",
        "images": ["http://example.com/image1.jpg"],
        "assignedTo": null,
        "assignedTime": null,
        "acceptTime": null,
        "startRepairTime": null,
        "completeTime": null,
        "acceptTime": null,
        "acceptResult": null,
        "acceptScore": null,
        "repairContent": null,
        "repairImages": [],
        "repairCost": null,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 5.2.2 提交报修申请（业主）
**接口地址：** `POST /api/v1/property/repair`
**接口描述：** 提交维修申请
**是否认证：** 是
**权限要求：** 无（业主可提交）

**请求参数：**
```json
{
  "repairType": "string",    // 维修类型，必填，长度2-50
  "urgentLevel": 1,         // 紧急程度，必填，1:普通 2:紧急
  "faultDesc": "string",     // 故障描述，必填，长度10-500
  "houseId": 1,             // 房产ID，必填
  "appointTime": "string",  // 预约时间，选填，格式：yyyy-MM-dd HH:mm
  "images": ["string"]      // 故障图片URL数组，选填，最多5张
}
```

#### 5.2.3 派工
**接口地址：** `PUT /api/v1/property/repair/assign/{repairId}`
**接口描述：** 维修工单派工
**是否认证：** 是
**权限要求：** property:repair:assign

**请求参数：**
```json
{
  "assignedTo": 1,          // 维修人员ID，必填
  "requireCompleteTime": "string", // 要求完成时间，选填
  "remark": "string"        // 派工备注，选填
}
```

#### 5.2.4 接单（维修人员）
**接口地址：** `PUT /api/v1/property/repair/accept/{repairId}`
**接口描述：** 维修人员接单
**是否认证：** 是
**权限要求：** 无（维修人员可接单）

**请求参数：**
```json
{
  "acceptTime": "string",   // 接单时间，选填，默认当前时间
  "remark": "string"        // 接单备注，选填
}
```

#### 5.2.5 开始维修
**接口地址：** `PUT /api/v1/property/repair/start/{repairId}`
**接口描述：** 开始维修
**是否认证：** 是
**权限要求：** 无（维修人员可操作）

#### 5.2.6 完成维修
**接口地址：** `PUT /api/v1/property/repair/complete/{repairId}`
**接口描述：** 完成维修
**是否认证：** 是
**权限要求：** 无（维修人员可操作）

**请求参数：**
```json
{
  "repairContent": "string",  // 维修内容，必填，长度10-500
  "repairImages": ["string"], // 维修后图片URL数组，选填，最多5张
  "repairCost": 50.00,       // 维修费用，选填，精确到2位小数
  "remark": "string"         // 维修备注，选填
}
```

#### 5.2.7 验收（业主）
**接口地址：** `PUT /api/v1/property/repair/accept/{repairId}`
**接口描述：** 维修验收
**是否认证：** 是
**权限要求：** 无（业主可验收）

**请求参数：**
```json
{
  "acceptResult": 1,        // 验收结果，必填，1:合格 2:不合格
  "acceptScore": 5,         // 评分，必填，1-5星
  "acceptContent": "string", // 验收评价，选填，长度10-200
  "acceptImages": ["string"] // 验收图片URL数组，选填，最多5张
}
```

---

## 6. 停车管理模块

### 6.1 车位管理

#### 6.1.1 分页查询车位列表
**接口地址：** `GET /api/v1/property/parking/space/list`
**接口描述：** 分页查询车位列表
**是否认证：** 是
**权限要求：** property:parking:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
spaceCode: string     // 车位编号，模糊查询
location: string      // 位置，模糊查询
spaceType: int        // 车位类型，1:标准 2:大型 3:超大型
spaceStatus: int      // 车位状态，1:空闲 2:已租 3:维修
buildingId: int       // 楼栋ID，精确查询
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "spaceId": 1,
        "spaceCode": "A-001",
        "location": "地下1层A区",
        "spaceType": 1,
        "monthlyRent": 200.00,
        "spaceStatus": 1,
        "buildingId": 1,
        "buildingName": "1号楼",
        "currentRentId": null,
        "currentOwner": null,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 6.1.2 新增车位
**接口地址：** `POST /api/v1/property/parking/space`
**接口描述：** 新增车位
**是否认证：** 是
**权限要求：** property:parking:add

**请求参数：**
```json
{
  "spaceCode": "string",     // 车位编号，必填，长度2-20，唯一
  "location": "string",      // 位置描述，必填，长度2-100
  "spaceType": 1,           // 车位类型，必填，1:标准 2:大型 3:超大型
  "monthlyRent": 200.00,     // 月租金，必填，精确到2位小数
  "buildingId": 1,          // 关联楼栋ID，选填
  "remark": "string"        // 备注，选填
}
```

### 6.2 车位租赁管理

#### 6.2.1 分页查询租赁列表
**接口地址：** `GET /api/v1/property/parking/rental/list`
**接口描述：** 分页查询车位租赁列表
**是否认证：** 是
**权限要求：** property:rental:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
rentalNo: string      // 租赁合同号，模糊查询
ownerName: string     // 业主姓名，模糊查询
plateNumber: string   // 车牌号，模糊查询
spaceCode: string     // 车位编号，模糊查询
rentalStatus: int     // 租赁状态，1:正常 2:即将到期 3:已到期 4:已退租
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 60,
    "rows": [
      {
        "rentalId": 1,
        "rentalNo": "PR202501001",
        "ownerId": 1,
        "ownerName": "张三",
        "phone": "13800138000",
        "spaceId": 1,
        "spaceCode": "A-001",
        "plateNumber": "京A12345",
        "vehicleType": "小轿车",
        "rentalType": 1,       // 1:月租 2:季租 3:年租
        "rentalStartDate": "2025-01-01",
        "rentalEndDate": "2025-12-31",
        "monthlyRent": 200.00,
        "rentalStatus": 1,
        "autoRenew": true,
        "createTime": "2025-01-09 10:00:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 6.2.2 申请租赁（业主）
**接口地址：** `POST /api/v1/property/parking/rental/apply`
**接口描述：** 申请车位租赁
**是否认证：** 是
**权限要求：** 无（业主可申请）

**请求参数：**
```json
{
  "spaceId": 1,             // 车位ID，必填
  "plateNumber": "string",  // 车牌号，必填，长度7-8
  "vehicleType": "string",  // 车辆类型，必填，长度2-20
  "rentalType": 1,          // 租赁类型，必填，1:月租 2:季租 3:年租
  "rentalMonths": 12        // 租赁月数，必填，1-36
}
```

#### 6.2.3 审核租赁申请
**接口地址：** `PUT /api/v1/property/parking/rental/audit/{rentalId}`
**接口描述：** 审核车位租赁申请
**是否认证：** 是
**权限要求：** property:rental:audit

**请求参数：**
```json
{
  "auditStatus": 1,         // 审核状态，必填，1:通过 2:拒绝
  "auditRemark": "string",  // 审核意见，选填，长度2-200
  "rentalMonths": 12       // 批准月数，审核通过时必填
}
```

#### 6.2.4 续租
**接口地址：** `POST /api/v1/property/parking/rental/renew`
**接口描述：** 车位续租
**是否认证：** 是
**权限要求：** 无（业主可操作）

**请求参数：**
```json
{
  "rentalId": 1,          // 原租赁ID，必填
  "rentalMonths": 6,      // 续租月数，必填，1-36
  "payPassword": "123456" // 支付密码，必填
}
```

#### 6.2.5 退租
**接口地址：** `PUT /api/v1/property/parking/rental/return/{rentalId}`
**接口描述：** 车位退租
**是否认证：** 是
**权限要求：** property:rental:return

**请求参数：**
```json
{
  "returnDate": "string",  // 退租日期，必填，格式：yyyy-MM-dd
  "returnReason": "string" // 退租原因，选填，长度2-200
}
```

---

## 7. 公告管理模块

### 7.1 公告发布

#### 7.1.1 分页查询公告列表
**接口地址：** `GET /api/v1/property/notice/list`
**接口描述：** 分页查询公告列表
**是否认证：** 是
**权限要求：** property:notice:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
title: string         // 公告标题，模糊查询
noticeType: string    // 公告类型，精确查询
status: int          // 状态，0:草稿 1:已发布 2:已撤回
isTop: int           // 是否置顶，0:否 1:是
publishBy: string     // 发布人，模糊查询
beginTime: string     // 开始时间
endTime: string       // 结束时间
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 30,
    "rows": [
      {
        "noticeId": 1,
        "title": "春节期间物业服务中心放假通知",
        "noticeType": "通知",
        "content": "尊敬的业主：春节期间物业服务中心放假安排...",
        "status": 1,
        "isTop": 1,
        "publishBy": "物业管理员",
        "publishTime": "2025-01-09 10:00:00",
        "startTime": "2025-01-10 00:00:00",
        "endTime": "2025-02-20 23:59:59",
        "readCount": 45,
        "totalCount": 52,
        "attachments": [
          {
            "fileName": "放假安排.pdf",
            "filePath": "http://example.com/file.pdf",
            "fileSize": 1024
          }
        ],
        "targetType": 1,  // 1:全部 2:指定楼栋 3:指定单元 4:指定业主
        "targetIds": [1, 2, 3],
        "createTime": "2025-01-09 09:30:00",
        "updateTime": "2025-01-09 10:00:00"
      }
    ]
  }
}
```

#### 7.1.2 新增公告
**接口地址：** `POST /api/v1/property/notice`
**接口描述：** 新增公告
**是否认证：** 是
**权限要求：** property:notice:add

**请求参数：**
```json
{
  "title": "string",              // 公告标题，必填，长度2-50
  "noticeType": "string",         // 公告类型，必填，长度2-20
  "content": "string",            // 公告内容，必填，长度10-5000
  "isTop": 0,                    // 是否置顶，必填，0:否 1:是
  "startTime": "2025-01-10",      // 开始时间，必填，格式：yyyy-MM-dd
  "endTime": "2025-02-20",        // 结束时间，必填，格式：yyyy-MM-dd
  "targetType": 1,               // 发布范围，必填
  "targetIds": [1, 2, 3],        // 目标ID数组，根据targetType确定
  "attachments": [               // 附件数组，选填
    {
      "fileName": "string",     // 文件名，必填
      "filePath": "string",     // 文件路径，必填
      "fileSize": 1024          // 文件大小，字节
    }
  ],
  "status": 0                   // 状态，0:草稿 1:发布，默认0
}
```

#### 7.1.3 发布公告
**接口地址：** `PUT /api/v1/property/notice/publish/{noticeId}`
**接口描述：** 发布公告
**是否认证：** 是
**权限要求：** property:notice:publish

#### 7.1.4 撤回公告
**接口地址：** `PUT /api/v1/property/notice/withdraw/{noticeId}`
**接口描述：** 撤回已发布的公告
**是否认证：** 是
**权限要求：** property:notice:withdraw

#### 7.1.5 获取公告阅读统计
**接口地址：** `GET /api/v1/property/notice/readStats/{noticeId}`
**接口描述：** 获取公告阅读统计信息
**是否认证：** 是
**权限要求：** property:notice:detail

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "noticeId": 1,
    "title": "春节期间物业服务中心放假通知",
    "totalCount": 52,        // 应读总数
    "readCount": 45,         // 已读数量
    "unreadCount": 7,        // 未读数量
    "readRate": 86.54,       // 阅读率
    "unreadUsers": [         // 未读用户列表
      {
        "ownerId": 1,
        "ownerName": "李四",
        "phone": "13800138001",
        "houseCode": "1-2-201"
      }
    ]
  }
}
```

### 7.2 公告查看（业主）

#### 7.2.1 获取我的公告列表
**接口地址：** `GET /api/v1/portal/notice/myList`
**接口描述：** 获取业主可见的公告列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
noticeType: string    // 公告类型，选填
isRead: int          // 是否已读，0:未读 1:已读，选填
```

#### 7.2.2 查看公告详情
**接口地址：** `GET /api/v1/portal/notice/{noticeId}`
**接口描述：** 查看公告详情（自动标记已读）
**是否认证：** 是
**权限要求：** 无（业主可查看）

---

## 8. 系统日志模块

### 8.1 操作日志

#### 8.1.1 分页查询操作日志
**接口地址：** `GET /api/v1/system/log/operation/list`
**接口描述：** 分页查询操作日志
**是否认证：** 是
**权限要求：** system:log:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
username: string      // 操作用户，模糊查询
operation: string     // 操作类型，模糊查询
status: int          // 操作状态，0:失败 1:成功
beginTime: string     // 开始时间
endTime: string       // 结束时间
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 1000,
    "rows": [
      {
        "operId": 1,
        "username": "admin",
        "realName": "系统管理员",
        "operation": "新增用户",
        "method": "POST",
        "requestMethod": "com.property.system.controller.SysUserController.add",
        "operUrl": "/api/v1/system/user",
        "operIp": "192.168.1.100",
        "operLocation": "内网IP",
        "operParam": "{\"username\":\"test\",\"realName\":\"测试用户\"}",
        "jsonResult": "{\"code\":200,\"msg\":\"操作成功\"}",
        "status": 1,
        "operTime": "2025-01-09 10:00:00",
        "costTime": 125
      }
    ]
  }
}
```

### 8.2 登录日志

#### 8.2.1 分页查询登录日志
**接口地址：** `GET /api/v1/system/log/login/list`
**接口描述：** 分页查询登录日志
**是否认证：** 是
**权限要求：** system:log:list

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
username: string      // 用户名，模糊查询
status: int          // 登录状态，0:失败 1:成功
beginTime: string     // 开始时间
endTime: string       // 结束时间
```

---

## 9. 业主门户模块

### 9.1 我的账单

#### 9.1.1 获取我的账单列表
**接口地址：** `GET /api/v1/portal/bill/myList`
**接口描述：** 获取业主本人的账单列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
billStatus: int       // 账单状态，1:待缴费 2:已缴费 3:已超期
billPeriod: string    // 账期，格式：yyyy-MM
```

#### 9.1.2 批量缴费
**接口地址：** `POST /api/v1/portal/bill/payBatch`
**接口描述：** 业主批量缴纳账单
**是否认证：** 是
**权限要求：** 无（业主可操作）

**请求参数：**
```json
{
  "billIds": [1, 2, 3],     // 账单ID数组，必填
  "payPassword": "123456"   // 支付密码，必填
}
```

### 9.2 我的钱包

#### 9.2.1 获取我的钱包信息
**接口地址：** `GET /api/v1/portal/wallet/myInfo`
**接口描述：** 获取业主本人的钱包信息
**是否认证：** 是
**权限要求：** 无（业主可查看）

#### 9.2.2 钱包充值
**接口地址：** `POST /api/v1/portal/wallet/recharge`
**接口描述：** 业主钱包充值
**是否认证：** 是
**权限要求：** 无（业主可操作）

#### 9.2.3 设置支付密码
**接口地址：** `POST /api/v1/portal/wallet/setPassword`
**接口描述：** 首次设置支付密码
**是否认证：** 是
**权限要求：** 无（业主可操作）

**请求参数：**
```json
{
  "payPassword": "123456",  // 支付密码，必填，6位数字
  "confirmPassword": "123456" // 确认密码，必填
}
```

#### 9.2.4 修改支付密码
**接口地址：** `PUT /api/v1/portal/wallet/changePassword`
**接口描述：** 修改支付密码
**是否认证：** 是
**权限要求：** 无（业主可操作）

**请求参数：**
```json
{
  "oldPassword": "123456",   // 原支付密码，必填
  "newPassword": "654321",   // 新支付密码，必填
  "confirmPassword": "654321" // 确认新密码，必填
}
```

### 9.3 我的投诉

#### 9.3.1 获取我的投诉列表
**接口地址：** `GET /api/v1/portal/complaint/myList`
**接口描述：** 获取业主本人的投诉列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

### 9.4 我的报修

#### 9.4.1 获取我的报修列表
**接口地址：** `GET /api/v1/portal/repair/myList`
**接口描述：** 获取业主本人的报修列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

### 9.5 我的房产

#### 9.5.1 获取我的房产列表
**接口地址：** `GET /api/v1/portal/house/myList`
**接口描述：** 获取业主本人的房产列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

### 9.6 我的车位

#### 9.6.1 获取我的车位列表
**接口地址：** `GET /api/v1/portal/parking/myList`
**接口描述：** 获取业主本人的车位列表
**是否认证：** 是
**权限要求：** 无（业主可查看）

---

## 10. 工作台模块

### 10.1 维修人员工作台

#### 10.1.1 获取工作台统计
**接口地址：** `GET /api/v1/work/dashboard/stats`
**接口描述：** 获取维修人员工作台统计数据
**是否认证：** 是
**权限要求：** 维修人员

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "pendingCount": 5,      // 待接单数量
    "processingCount": 3,   // 进行中数量
    "pendingAcceptCount": 2, // 待验收数量
    "completedCount": 15,   // 已完成数量
    "todayCompleted": 2,    // 今日完成数量
    "monthCompleted": 28    // 本月完成数量
  }
}
```

#### 10.1.2 获取我的工单列表
**接口地址：** `GET /api/v1/work/repair/myList`
**接口描述：** 获取维修人员本人的工单列表
**是否认证：** 是
**权限要求：** 维修人员

**请求参数：**
```
pageNum: int          // 页码，默认1
pageSize: int         // 每页条数，默认10
repairStatus: int     // 工单状态，1:待接单 2:进行中 3:待验收 4:已完成
```

---

## 11. 通用接口

### 11.1 文件上传

#### 11.1.1 单文件上传
**接口地址：** `POST /api/v1/common/upload`
**接口描述：** 单文件上传
**是否认证：** 是
**权限要求：** 无

**请求参数：**
```
file: File            // 文件，必填，最大10MB
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "fileName": "image.jpg",
    "filePath": "/uploads/2025/01/09/image.jpg",
    "fileUrl": "http://example.com/uploads/2025/01/09/image.jpg",
    "fileSize": 1024,
    "fileType": "image/jpeg"
  }
}
```

#### 11.1.2 多文件上传
**接口地址：** `POST /api/v1/common/upload/multiple`
**接口描述：** 多文件上传
**是否认证：** 是
**权限要求：** 无

### 11.2 数据字典

#### 11.2.1 获取所有字典类型
**接口地址：** `GET /api/v1/common/dict/types`
**接口描述：** 获取所有可用的字典类型
**是否认证：** 是
**权限要求：** 无

### 11.3 系统配置

#### 11.3.1 获取系统配置
**接口地址：** `GET /api/v1/common/config`
**接口描述：** 获取系统配置信息
**是否认证：** 是
**权限要求：** 无

**响应数据：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "systemName": "社区物业管理系统",
    "version": "V1.0.0",
    "uploadMaxSize": 10485760,  // 上传文件最大大小（字节）
    "imageTypes": [".jpg", ".jpeg", ".png", ".gif"],
    "documentTypes": [".pdf", ".doc", ".docx", ".xls", ".xlsx"]
  }
}
```

---

## 接口开发规范

### 1. 代码规范
- 严格遵循阿里巴巴Java开发手册
- 所有public方法必须添加完整的JavaDoc注释
- 参数校验使用`@Validated`注解
- 统一异常处理，避免try-catch滥用

### 2. 接口命名规范
- 查询列表：`GET /api/v1/module/list`
- 详情查询：`GET /api/v1/module/{id}`
- 新增：`POST /api/v1/module`
- 修改：`PUT /api/v1/module`
- 删除：`DELETE /api/v1/module/{ids}`

### 3. 参数校验规范
- 必填参数使用`@NotNull`或`@NotBlank`
- 格式校验使用`@Pattern`、`@Email`等
- 业务校验在Service层进行

### 4. 响应规范
- 成功响应：`AjaxResult.success(data)`
- 失败响应：`AjaxResult.error(msg)`
- 分页响应：`TableDataInfo`

### 5. 日志规范
- 关键操作必须记录操作日志
- 使用`@Log`注解记录操作
- 日志信息包含操作人、时间、内容

### 6. 安全规范
- 所有接口都需要认证（除登录、文件上传等）
- 敏感操作需要权限校验
- 防止SQL注入、XSS攻击
- 密码等敏感信息加密存储

---

## 联系方式

如有接口相关问题，请联系开发团队：
- **技术负责人：** 张三
- **邮箱：** dev@example.com
- **电话：** 400-123-4567

**文档最后更新时间：** 2025-01-09