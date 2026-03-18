<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">系统日志</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
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
                    <span class="value">{{ row.requestMethod || row.method || 'GET' }}</span>
                  </div>
                  <div class="expand-item">
                    <span class="label">请求URL：</span>
                    <span class="value">{{ row.operUrl || '-' }}</span>
                  </div>
                  <div class="expand-item">
                    <span class="label">请求参数：</span>
                    <pre class="json-content">{{ row.operParam || '{}' }}</pre>
                  </div>
                  <div class="expand-item">
                    <span class="label">响应结果：</span>
                    <pre class="json-content">{{ row.jsonResult || '{}' }}</pre>
                  </div>
                  <div class="expand-item">
                    <span class="label">异常信息：</span>
                    <pre class="error-content">{{ row.errorMsg || '无异常' }}</pre>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="id" label="日志编号" width="100" />
            <el-table-column prop="title" label="操作模块" />
            <el-table-column prop="businessType" label="操作类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getBusinessTypeColor(row.businessType)">
                  {{ row.businessTypeName || getBusinessTypeName(row.businessType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="requestMethod" label="请求方式" width="100" />
            <el-table-column prop="operName" label="操作人员" width="120" />
            <el-table-column prop="operIp" label="操作地址" width="140" />
            <el-table-column prop="operLocation" label="操作地点" width="140" />
            <el-table-column prop="status" label="操作状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'success' : 'danger'">
                  {{ row.statusName || (row.status === 0 ? '成功' : '失败') }}
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
    </el-tabs>

    <!-- 操作日志详情对话框 -->
    <el-dialog
      v-model="operationDetailVisible"
      title="操作日志详情"
      width="800px"
    >
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日志编号">{{ operationDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ operationDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getBusinessTypeColor(operationDetail.businessType)">
              {{ operationDetail.businessTypeName || getBusinessTypeName(operationDetail.businessType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="请求方式">{{ operationDetail.requestMethod || operationDetail.method }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ operationDetail.operName }}</el-descriptions-item>
          <el-descriptions-item label="请求URL" :span="2">{{ operationDetail.operUrl }}</el-descriptions-item>
          <el-descriptions-item label="操作地址">{{ operationDetail.operIp }}</el-descriptions-item>
          <el-descriptions-item label="操作地点">{{ operationDetail.operLocation }}</el-descriptions-item>
          <el-descriptions-item label="操作状态">
            <el-tag :type="operationDetail.status === 0 ? 'success' : 'danger'">
              {{ operationDetail.statusName || (operationDetail.status === 0 ? '成功' : '失败') }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ formatDateTime(operationDetail.operTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h4>请求参数</h4>
          <pre class="json-content">{{ operationDetail.operParam || '{}' }}</pre>
        </div>

        <div class="detail-section">
          <h4>响应结果</h4>
          <pre class="json-content">{{ operationDetail.jsonResult || '{}' }}</pre>
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
          <el-descriptions-item label="访问编号">{{ loginDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="登录账号">{{ loginDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="用户姓名">{{ loginDetail.userName }}</el-descriptions-item>
          <el-descriptions-item label="登录IP">{{ loginDetail.ipaddr }}</el-descriptions-item>
          <el-descriptions-item label="登录地点">{{ loginDetail.loginLocation }}</el-descriptions-item>
          <el-descriptions-item label="浏览器">{{ loginDetail.browser }}</el-descriptions-item>
          <el-descriptions-item label="操作系统">{{ loginDetail.os }}</el-descriptions-item>
          <el-descriptions-item label="登录状态">
            <el-tag :type="loginDetail.status === 0 ? 'success' : 'danger'">
              {{ loginDetail.statusName || (loginDetail.status === 0 ? '成功' : '失败') }}
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'
import {
  listOperLog, delOperLog, cleanOperLog,
  listLoginLog, delLoginLog, cleanLoginLog
} from '@/api/log'

// Router
const router = useRouter()
const route = useRoute()

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

// 获取操作类型名称（businessTypeName后端已返回，保留本地映射作兜底）
const getBusinessTypeName = (type) => {
  const typeMap = { 0: '其他', 1: '新增', 2: '修改', 3: '删除', 4: '查询', 5: '导入', 6: '导出' }
  return typeMap[type] || '其他'
}

const getBusinessTypeColor = (type) => {
  const colorMap = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger', 4: 'info', 5: 'primary', 6: 'success' }
  return colorMap[type] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 加载操作日志（对接真实API）
// 后端status: 0=成功 1=失败
const loadOperationLogs = async () => {
  operationLoading.value = true
  try {
    const params = {
      pageNum: operationCurrentPage.value,
      pageSize: operationPageSize.value
    }
    if (operationSearchForm.operator) params.username = operationSearchForm.operator
    if (operationSearchForm.status !== '') params.status = Number(operationSearchForm.status) === 1 ? 0 : (Number(operationSearchForm.status) === 0 ? 1 : '')
    if (operationSearchForm.dateRange && operationSearchForm.dateRange.length === 2) {
      params.beginTime = formatDateTimeParam(operationSearchForm.dateRange[0])
      params.endTime = formatDateTimeParam(operationSearchForm.dateRange[1])
    }
    const res = await listOperLog(params)
    if (res && res.data) {
      const data = res.data
      if (Array.isArray(data)) {
        operationLogList.value = data
        operationTotal.value = data.length
      } else if (data.rows) {
        operationLogList.value = data.rows
        operationTotal.value = data.total || data.rows.length
      } else if (data.records) {
        operationLogList.value = data.records
        operationTotal.value = data.total || data.records.length
      } else {
        operationLogList.value = Array.isArray(data) ? data : []
        operationTotal.value = operationLogList.value.length
      }
    }
  } catch (e) {
    ElMessage.error('操作日志加载失败：' + (e.message || '未知错误'))
  } finally {
    operationLoading.value = false
  }
}

// 加载登录日志（对接真实API）
// 后端status: 0=成功 1=失败
const loadLoginLogs = async () => {
  loginLoading.value = true
  try {
    const params = {
      pageNum: loginCurrentPage.value,
      pageSize: loginPageSize.value
    }
    if (loginSearchForm.username) params.username = loginSearchForm.username
    if (loginSearchForm.status !== '') params.status = Number(loginSearchForm.status) === 1 ? 0 : (Number(loginSearchForm.status) === 0 ? 1 : '')
    if (loginSearchForm.dateRange && loginSearchForm.dateRange.length === 2) {
      params.beginTime = formatDateTimeParam(loginSearchForm.dateRange[0])
      params.endTime = formatDateTimeParam(loginSearchForm.dateRange[1])
    }
    const res = await listLoginLog(params)
    if (res && res.data) {
      const data = res.data
      if (Array.isArray(data)) {
        loginLogList.value = data
        loginTotal.value = data.length
      } else if (data.rows) {
        loginLogList.value = data.rows
        loginTotal.value = data.total || data.rows.length
      } else if (data.records) {
        loginLogList.value = data.records
        loginTotal.value = data.total || data.records.length
      } else {
        loginLogList.value = []
        loginTotal.value = 0
      }
    }
  } catch (e) {
    ElMessage.error('登录日志加载失败：' + (e.message || '未知错误'))
  } finally {
    loginLoading.value = false
  }
}

// 日期时间格式化为后端接受的字符串
const formatDateTimeParam = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toISOString().slice(0, 19).replace('T', ' ')
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'operation') {
    router.push('/log/operation')
    loadOperationLogs()
  } else {
    router.push('/log/login')
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
  Object.assign(operationSearchForm, { operator: '', operationType: '', status: '', dateRange: [] })
  handleOperationSearch()
}

// 登录日志搜索
const handleLoginSearch = () => {
  loginCurrentPage.value = 1
  loadLoginLogs()
}

// 登录日志重置
const handleLoginReset = () => {
  Object.assign(loginSearchForm, { username: '', status: '', dateRange: [] })
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
  // 展开行不需要额外操作，数据已在row中
}

// 清空操作日志（调用后端clean接口，按时间清理）
const handleOperationClear = () => {
  ElMessageBox.confirm(
    '确定要清空所有操作日志吗？此操作不可恢复！',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      // 清理1年前的日志
      const beforeTime = new Date(Date.now() - 365 * 24 * 3600 * 1000).toISOString().slice(0, 19)
      await cleanOperLog(beforeTime)
      operationCurrentPage.value = 1
      await loadOperationLogs()
      ElMessage.success('操作日志清空成功')
    } catch (e) {
      ElMessage.error('清空失败：' + (e.message || '未知错误'))
    }
  }).catch(() => {})
}

// 清空登录日志
const handleLoginClear = () => {
  ElMessageBox.confirm(
    '确定要清空所有登录日志吗？此操作不可恢复！',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      const beforeTime = new Date(Date.now() - 365 * 24 * 3600 * 1000).toISOString().slice(0, 19)
      await cleanLoginLog(beforeTime)
      loginCurrentPage.value = 1
      await loadLoginLogs()
      ElMessage.success('登录日志清空成功')
    } catch (e) {
      ElMessage.error('清空失败：' + (e.message || '未知错误'))
    }
  }).catch(() => {})
}

// 导出操作日志（提示，暂无后端导出接口）
const handleOperationExport = () => {
  ElMessage.info('导出功能暂未开放')
}

// 导出登录日志
const handleLoginExport = () => {
  ElMessage.info('导出功能暂未开放')
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

// 初始化
const initializeActiveTab = () => {
  const path = route.path
  if (path.includes('/log/login')) {
    activeTab.value = 'login'
    loadLoginLogs()
  } else {
    activeTab.value = 'operation'
    loadOperationLogs()
  }
}

watch(
  () => route.path,
  (newPath) => {
    if (newPath.includes('/log/login')) activeTab.value = 'login'
    else if (newPath.includes('/log/operation')) activeTab.value = 'operation'
  }
)

onMounted(() => {
  initializeActiveTab()
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