<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">系统日志</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>系统管理</el-breadcrumb-item>
        <el-breadcrumb-item>系统日志</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 操作日志 -->
      <el-tab-pane label="操作日志" name="operation">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="operationSearchForm" inline>
            <el-form-item label="操作人员">
              <el-input
                v-model="operationSearchForm.operator"
                placeholder="请输入操作人员"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="操作类型">
              <el-select
                v-model="operationSearchForm.operationType"
                placeholder="请选择操作类型"
                clearable
                style="width: 150px"
              >
                <el-option label="新增" value="1" />
                <el-option label="修改" value="2" />
                <el-option label="删除" value="3" />
                <el-option label="查询" value="4" />
                <el-option label="导入" value="5" />
                <el-option label="导出" value="6" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作状态">
              <el-select
                v-model="operationSearchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="成功" value="1" />
                <el-option label="失败" value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作时间">
              <el-date-picker
                v-model="operationSearchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="width: 350px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleOperationSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleOperationReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            type="danger"
            @click="handleOperationClear"
            v-permission="'system:log:clear'"
          >
            <el-icon><Delete /></el-icon>
            清空日志
          </el-button>
          <el-button
            type="success"
            @click="handleOperationExport"
          >
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </div>

        <!-- 操作日志表格 -->
        <div class="table-section">
          <el-table
            v-loading="operationLoading"
            :data="operationLogList"
            @expand-change="handleOperationExpand"
          >
            <el-table-column type="expand">
              <template #default="{ row }">
                <div class="expand-content">
                  <div class="expand-item">
                    <span class="label">请求方法：</span>
                    <span class="value">{{ row.method || 'GET' }}</span>
                  </div>
                  <div class="expand-item">
                    <span class="label">请求参数：</span>
                    <pre class="json-content">{{ row.requestParam || '{}' }}</pre>
                  </div>
                  <div class="expand-item">
                    <span class="label">响应结果：</span>
                    <pre class="json-content">{{ row.responseResult || '{}' }}</pre>
                  </div>
                  <div class="expand-item">
                    <span class="label">异常信息：</span>
                    <pre class="error-content">{{ row.errorMsg || '无异常' }}</pre>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="logId" label="日志编号" width="100" />
            <el-table-column prop="title" label="操作模块" />
            <el-table-column prop="businessType" label="操作类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getBusinessTypeColor(row.businessType)">
                  {{ getBusinessTypeName(row.businessType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="method" label="请求方式" width="100" />
            <el-table-column prop="operator" label="操作人员" width="120" />
            <el-table-column prop="deptName" label="部门名称" width="120" />
            <el-table-column prop="operIp" label="操作地址" width="140" />
            <el-table-column prop="operLocation" label="操作地点" width="140" />
            <el-table-column prop="status" label="操作状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="operTime" label="操作时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.operTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="handleOperationDetail(row)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="operationCurrentPage"
              v-model:page-size="operationPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="operationTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleOperationSizeChange"
              @current-change="handleOperationCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 登录日志 -->
      <el-tab-pane label="登录日志" name="login">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="loginSearchForm" inline>
            <el-form-item label="登录账号">
              <el-input
                v-model="loginSearchForm.username"
                placeholder="请输入登录账号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="登录状态">
              <el-select
                v-model="loginSearchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="成功" value="1" />
                <el-option label="失败" value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="登录时间">
              <el-date-picker
                v-model="loginSearchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="width: 350px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLoginSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleLoginReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            type="danger"
            @click="handleLoginClear"
            v-permission="'system:log:clear'"
          >
            <el-icon><Delete /></el-icon>
            清空日志
          </el-button>
          <el-button
            type="success"
            @click="handleLoginExport"
          >
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </div>

        <!-- 登录日志表格 -->
        <div class="table-section">
          <el-table v-loading="loginLoading" :data="loginLogList">
            <el-table-column prop="infoId" label="访问编号" width="100" />
            <el-table-column prop="username" label="登录账号" width="150" />
            <el-table-column prop="ipaddr" label="登录IP" width="140" />
            <el-table-column prop="loginLocation" label="登录地点" width="150" />
            <el-table-column prop="browser" label="浏览器" width="120" />
            <el-table-column prop="os" label="操作系统" width="120" />
            <el-table-column prop="status" label="登录状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="msg" label="操作信息" show-overflow-tooltip />
            <el-table-column prop="loginTime" label="登录时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.loginTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="handleLoginDetail(row)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="loginCurrentPage"
              v-model:page-size="loginPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="loginTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLoginSizeChange"
              @current-change="handleLoginCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 操作日志详情对话框 -->
    <el-dialog
      v-model="operationDetailVisible"
      title="操作日志详情"
      width="800px"
    >
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日志编号">{{ operationDetail.logId }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ operationDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getBusinessTypeColor(operationDetail.businessType)">
              {{ getBusinessTypeName(operationDetail.businessType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="请求方式">{{ operationDetail.method }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ operationDetail.operator }}</el-descriptions-item>
          <el-descriptions-item label="部门名称">{{ operationDetail.deptName }}</el-descriptions-item>
          <el-descriptions-item label="操作地址">{{ operationDetail.operIp }}</el-descriptions-item>
          <el-descriptions-item label="操作地点">{{ operationDetail.operLocation }}</el-descriptions-item>
          <el-descriptions-item label="操作状态">
            <el-tag :type="operationDetail.status === 1 ? 'success' : 'danger'">
              {{ operationDetail.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ formatDateTime(operationDetail.operTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h4>请求参数</h4>
          <pre class="json-content">{{ operationDetail.requestParam || '{}' }}</pre>
        </div>

        <div class="detail-section">
          <h4>响应结果</h4>
          <pre class="json-content">{{ operationDetail.responseResult || '{}' }}</pre>
        </div>

        <div class="detail-section" v-if="operationDetail.errorMsg">
          <h4>异常信息</h4>
          <pre class="error-content">{{ operationDetail.errorMsg }}</pre>
        </div>
      </div>
    </el-dialog>

    <!-- 登录日志详情对话框 -->
    <el-dialog
      v-model="loginDetailVisible"
      title="登录日志详情"
      width="600px"
    >
      <div class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="访问编号">{{ loginDetail.infoId }}</el-descriptions-item>
          <el-descriptions-item label="登录账号">{{ loginDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="登录IP">{{ loginDetail.ipaddr }}</el-descriptions-item>
          <el-descriptions-item label="登录地点">{{ loginDetail.loginLocation }}</el-descriptions-item>
          <el-descriptions-item label="浏览器">{{ loginDetail.browser }}</el-descriptions-item>
          <el-descriptions-item label="操作系统">{{ loginDetail.os }}</el-descriptions-item>
          <el-descriptions-item label="登录状态">
            <el-tag :type="loginDetail.status === 1 ? 'success' : 'danger'">
              {{ loginDetail.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作信息">{{ loginDetail.msg }}</el-descriptions-item>
          <el-descriptions-item label="登录时间">{{ formatDateTime(loginDetail.loginTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('operation')
const operationLoading = ref(false)
const loginLoading = ref(false)
const operationDetailVisible = ref(false)
const loginDetailVisible = ref(false)

// 搜索表单
const operationSearchForm = reactive({
  operator: '',
  operationType: '',
  status: '',
  dateRange: []
})

const loginSearchForm = reactive({
  username: '',
  status: '',
  dateRange: []
})

// 日志数据
const operationLogList = ref([])
const loginLogList = ref([])
const operationDetail = ref({})
const loginDetail = ref({})

// 分页
const operationCurrentPage = ref(1)
const operationPageSize = ref(10)
const operationTotal = ref(0)

const loginCurrentPage = ref(1)
const loginPageSize = ref(10)
const loginTotal = ref(0)

// 获取操作类型名称
const getBusinessTypeName = (type) => {
  const typeMap = {
    1: '新增',
    2: '修改',
    3: '删除',
    4: '查询',
    5: '导入',
    6: '导出'
  }
  return typeMap[type] || '其他'
}

// 获取操作类型颜色
const getBusinessTypeColor = (type) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info',
    5: 'primary',
    6: 'success'
  }
  return colorMap[type] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生成模拟操作日志数据
const generateMockOperationLogs = () => {
  const logs = []
  const modules = ['用户管理', '角色管理', '菜单管理', '字典管理', '楼栋管理', '房产管理', '账单管理', '投诉管理']
  const operators = ['系统管理员', '物业经理', '张三', '李四']
  const departments = ['系统管理部', '物业管理部', '财务部', '客服部']
  const locations = ['北京市朝阳区', '上海市浦东新区', '广州市天河区', '深圳市南山区']

  for (let i = 1; i <= 50; i++) {
    logs.push({
      logId: i,
      title: modules[Math.floor(Math.random() * modules.length)],
      businessType: Math.floor(Math.random() * 6) + 1,
      method: ['GET', 'POST', 'PUT', 'DELETE'][Math.floor(Math.random() * 4)],
      operator: operators[Math.floor(Math.random() * operators.length)],
      deptName: departments[Math.floor(Math.random() * departments.length)],
      operIp: `192.168.1.${Math.floor(Math.random() * 255)}`,
      operLocation: locations[Math.floor(Math.random() * locations.length)],
      status: Math.random() > 0.1 ? 1 : 0,
      operTime: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000),
      requestParam: JSON.stringify({ id: Math.floor(Math.random() * 1000), name: '测试数据' }),
      responseResult: JSON.stringify({ code: 200, msg: '操作成功', data: {} }),
      errorMsg: Math.random() > 0.9 ? '模拟异常信息：数据库连接失败' : ''
    })
  }

  // 模拟分页数据
  const start = (operationCurrentPage.value - 1) * operationPageSize.value
  const end = start + operationPageSize.value
  operationTotal.value = logs.length
  return logs.slice(start, end)
}

// 生成模拟登录日志数据
const generateMockLoginLogs = () => {
  const logs = []
  const users = ['admin', 'manager', 'owner', 'worker']
  const browsers = ['Chrome', 'Firefox', 'Safari', 'Edge']
  const os = ['Windows 10', 'Windows 11', 'macOS', 'Linux']
  const locations = ['北京市朝阳区', '上海市浦东新区', '广州市天河区', '深圳市南山区']

  for (let i = 1; i <= 30; i++) {
    logs.push({
      infoId: i,
      username: users[Math.floor(Math.random() * users.length)],
      ipaddr: `192.168.1.${Math.floor(Math.random() * 255)}`,
      loginLocation: locations[Math.floor(Math.random() * locations.length)],
      browser: browsers[Math.floor(Math.random() * browsers.length)],
      os: os[Math.floor(Math.random() * os.length)],
      status: Math.random() > 0.2 ? 1 : 0,
      msg: Math.random() > 0.2 ? '登录成功' : '用户名或密码错误',
      loginTime: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000)
    })
  }

  // 模拟分页数据
  const start = (loginCurrentPage.value - 1) * loginPageSize.value
  const end = start + loginPageSize.value
  loginTotal.value = logs.length
  return logs.slice(start, end)
}

// 加载操作日志
const loadOperationLogs = () => {
  operationLoading.value = true
  setTimeout(() => {
    operationLogList.value = generateMockOperationLogs()
    operationLoading.value = false
  }, 500)
}

// 加载登录日志
const loadLoginLogs = () => {
  loginLoading.value = true
  setTimeout(() => {
    loginLogList.value = generateMockLoginLogs()
    loginLoading.value = false
  }, 500)
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'operation') {
    loadOperationLogs()
  } else {
    loadLoginLogs()
  }
}

// 操作日志搜索
const handleOperationSearch = () => {
  operationCurrentPage.value = 1
  loadOperationLogs()
}

// 操作日志重置
const handleOperationReset = () => {
  Object.assign(operationSearchForm, {
    operator: '',
    operationType: '',
    status: '',
    dateRange: []
  })
  handleOperationSearch()
}

// 登录日志搜索
const handleLoginSearch = () => {
  loginCurrentPage.value = 1
  loadLoginLogs()
}

// 登录日志重置
const handleLoginReset = () => {
  Object.assign(loginSearchForm, {
    username: '',
    status: '',
    dateRange: []
  })
  handleLoginSearch()
}

// 操作日志详情
const handleOperationDetail = (row) => {
  operationDetail.value = { ...row }
  operationDetailVisible.value = true
}

// 登录日志详情
const handleLoginDetail = (row) => {
  loginDetail.value = { ...row }
  loginDetailVisible.value = true
}

// 操作日志展开
const handleOperationExpand = (row, expandedRows) => {
  // 展开时可以加载更多详细信息
  console.log('展开行:', row, expandedRows)
}

// 清空操作日志
const handleOperationClear = () => {
  ElMessageBox.confirm(
    '确定要清空所有操作日志吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    operationLogList.value = []
    operationTotal.value = 0
    ElMessage.success('操作日志清空成功')
  })
}

// 清空登录日志
const handleLoginClear = () => {
  ElMessageBox.confirm(
    '确定要清空所有登录日志吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    loginLogList.value = []
    loginTotal.value = 0
    ElMessage.success('登录日志清空成功')
  })
}

// 导出操作日志
const handleOperationExport = () => {
  ElMessage.success('操作日志导出成功')
}

// 导出登录日志
const handleLoginExport = () => {
  ElMessage.success('登录日志导出成功')
}

// 分页处理
const handleOperationSizeChange = (val) => {
  operationPageSize.value = val
  loadOperationLogs()
}

const handleOperationCurrentChange = (val) => {
  operationCurrentPage.value = val
  loadOperationLogs()
}

const handleLoginSizeChange = (val) => {
  loginPageSize.value = val
  loadLoginLogs()
}

const handleLoginCurrentChange = (val) => {
  loginCurrentPage.value = val
  loadLoginLogs()
}

onMounted(() => {
  loadOperationLogs()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    margin: 0 0 16px 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.search-section,
.action-section {
  margin-bottom: 20px;
}

.table-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
}

.expand-content {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;

  .expand-item {
    margin-bottom: 16px;

    .label {
      font-weight: 600;
      color: #606266;
      margin-right: 8px;
    }

    .value {
      color: #303133;
    }
  }
}

.detail-content {
  .detail-section {
    margin-top: 20px;

    h4 {
      margin: 0 0 10px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .json-content,
  .error-content {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
    font-size: 12px;
    line-height: 1.5;
    color: #303133;
    margin: 0;
    overflow-x: auto;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .error-content {
    color: #f56c6c;
    background: #fef0f0;
  }
}
</style>