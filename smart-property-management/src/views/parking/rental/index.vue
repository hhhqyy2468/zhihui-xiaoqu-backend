<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">租赁管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>停车管理</el-breadcrumb-item>
        <el-breadcrumb-item>租赁管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 租赁申请 -->
      <el-tab-pane label="租赁申请" name="applications">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="searchForm" inline>
            <el-form-item label="申请人">
              <el-input
                v-model="searchForm.applicant"
                placeholder="请输入申请人姓名"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位编号">
              <el-input
                v-model="searchForm.spaceNo"
                placeholder="请输入车位编号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="申请状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 150px"
              >
                <el-option label="待审核" value="0" />
                <el-option label="已通过" value="1" />
                <el-option label="已拒绝" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            type="primary"
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新增申请
          </el-button>
          <el-button
            type="success"
            @click="handleBatchApprove"
            :disabled="!selectedApplications.length"
          >
            <el-icon><Check /></el-icon>
            批量通过
          </el-button>
          <el-button
            type="danger"
            @click="handleBatchReject"
            :disabled="!selectedApplications.length"
          >
            <el-icon><Close /></el-icon>
            批量拒绝
          </el-button>
        </div>

        <!-- 申请表格 -->
        <div class="table-section">
          <el-table
            v-loading="loading"
            :data="applicationList"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="applicationNo" label="申请编号" width="150" />
            <el-table-column prop="applicant" label="申请人" width="120" />
            <el-table-column prop="spaceNo" label="车位编号" width="120" />
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="leaseTerm" label="租期(月)" width="100" />
            <el-table-column prop="status" label="申请状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.status)">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.applyTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === '0'"
                  link
                  type="success"
                  @click="handleApprove(row)"
                >
                  通过
                </el-button>
                <el-button
                  v-if="row.status === '0'"
                  link
                  type="danger"
                  @click="handleReject(row)"
                >
                  拒绝
                </el-button>
                <el-button
                  link
                  type="primary"
                  @click="handleView(row)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 租赁记录 -->
      <el-tab-pane label="租赁记录" name="records">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="recordSearchForm" inline>
            <el-form-item label="承租人">
              <el-input
                v-model="recordSearchForm.tenant"
                placeholder="请输入承租人姓名"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位编号">
              <el-input
                v-model="recordSearchForm.spaceNo"
                placeholder="请输入车位编号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="租赁状态">
              <el-select
                v-model="recordSearchForm.leaseStatus"
                placeholder="请选择状态"
                clearable
                style="width: 150px"
              >
                <el-option label="进行中" value="1" />
                <el-option label="已到期" value="2" />
                <el-option label="已终止" value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRecordSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleRecordReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            type="warning"
            @click="handleExportRecords"
          >
            <el-icon><Download /></el-icon>
            导出记录
          </el-button>
        </div>

        <!-- 记录表格 -->
        <div class="table-section">
          <el-table
            v-loading="recordLoading"
            :data="recordList"
          >
            <el-table-column prop="contractNo" label="合同编号" width="150" />
            <el-table-column prop="tenant" label="承租人" width="120" />
            <el-table-column prop="spaceNo" label="车位编号" width="120" />
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="leaseStatus" label="租赁状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getLeaseStatusColor(row.leaseStatus)">
                  {{ getLeaseStatusName(row.leaseStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="handleViewContract(row)"
                >
                  查看合同
                </el-button>
                <el-button
                  v-if="row.leaseStatus === '1'"
                  link
                  type="warning"
                  @click="handleTerminate(row)"
                >
                  终止
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="recordCurrentPage"
              v-model:page-size="recordPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="recordTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleRecordSizeChange"
              @current-change="handleRecordCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>

      </el-tabs>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="申请详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentApplication.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentApplication.applicant }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentApplication.phone }}</el-descriptions-item>
        <el-descriptions-item label="车位编号">{{ currentApplication.spaceNo }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentApplication.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="租期">{{ currentApplication.leaseTerm }}个月</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusColor(currentApplication.status)">
            {{ getStatusName(currentApplication.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(currentApplication.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentApplication.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Check,
  Close,
  Download
} from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('applications')
const loading = ref(false)
const recordLoading = ref(false)
const detailVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  applicant: '',
  spaceNo: '',
  status: ''
})

const recordSearchForm = reactive({
  tenant: '',
  spaceNo: '',
  leaseStatus: ''
})

// 数据
const applicationList = ref([])
const recordList = ref([])
const selectedApplications = ref([])
const currentApplication = ref({})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const recordCurrentPage = ref(1)
const recordPageSize = ref(10)
const recordTotal = ref(0)


// 获取申请状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取申请状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取租赁状态名称
const getLeaseStatusName = (status) => {
  const statusMap = {
    1: '进行中',
    2: '已到期',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

// 获取租赁状态颜色
const getLeaseStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生成模拟申请数据
const generateMockApplications = () => {
  const applications = []
  const applicants = ['张三', '李四', '王五', '赵六', '钱七']
  const statuses = ['0', '1', '2']

  for (let i = 1; i <= 30; i++) {
    applications.push({
      id: i,
      applicationNo: `A${String(i).padStart(6, '0')}`,
      applicant: applicants[Math.floor(Math.random() * applicants.length)],
      phone: `138${String(Math.floor(Math.random() * 100000000)).padStart(8, '0')}`,
      spaceNo: `P${String(Math.floor(Math.random() * 500) + 1).padStart(3, '0')}`,
      monthlyRent: Math.floor(Math.random() * 500) + 200,
      leaseTerm: Math.floor(Math.random() * 12) + 1,
      status: statuses[Math.floor(Math.random() * statuses.length)],
      applyTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000),
      remark: Math.random() > 0.7 ? '希望尽快安排车位' : ''
    })
  }

  return applications
}

// 生成模拟记录数据
const generateMockRecords = () => {
  const records = []
  const tenants = ['张三', '李四', '王五', '赵六', '钱七', '孙八']
  const statuses = ['1', '2', '3']

  for (let i = 1; i <= 50; i++) {
    const leaseTerm = Math.floor(Math.random() * 12) + 6
    const startDate = new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000)
    const endDate = new Date(startDate.getTime() + leaseTerm * 30 * 24 * 60 * 60 * 1000)

    records.push({
      id: i,
      contractNo: `C${String(i).padStart(6, '0')}`,
      tenant: tenants[Math.floor(Math.random() * tenants.length)],
      spaceNo: `P${String(Math.floor(Math.random() * 500) + 1).padStart(3, '0')}`,
      monthlyRent: Math.floor(Math.random() * 500) + 200,
      leaseStatus: statuses[Math.floor(Math.random() * statuses.length)],
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      totalAmount: (Math.floor(Math.random() * 500) + 200) * leaseTerm
    })
  }

  return records
}

// 加载申请数据
const loadApplications = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockApplications()
    applicationList.value = mockData.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    )
    total.value = mockData.length
    loading.value = false
  }, 500)
}

// 加载记录数据
const loadRecords = () => {
  recordLoading.value = true
  setTimeout(() => {
    const mockData = generateMockRecords()
    recordList.value = mockData.slice(
      (recordCurrentPage.value - 1) * recordPageSize.value,
      recordCurrentPage.value * recordPageSize.value
    )
    recordTotal.value = mockData.length
    recordLoading.value = false
  }, 500)
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  if (tabName === 'applications') {
    loadApplications()
  } else if (tabName === 'records') {
    loadRecords()
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadApplications()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    applicant: '',
    spaceNo: '',
    status: ''
  })
  handleSearch()
}

// 记录搜索
const handleRecordSearch = () => {
  recordCurrentPage.value = 1
  loadRecords()
}

// 记录重置
const handleRecordReset = () => {
  Object.assign(recordSearchForm, {
    tenant: '',
    spaceNo: '',
    leaseStatus: ''
  })
  handleRecordSearch()
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedApplications.value = selection
}

// 新增申请
const handleAdd = () => {
  ElMessage.info('新增租赁申请功能开发中')
}

// 批量通过
const handleBatchApprove = () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要审核的申请')
    return
  }

  ElMessageBox.confirm(
    `确定要通过选中的 ${selectedApplications.value.length} 个申请吗？`,
    '批量通过',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量通过成功')
    loadApplications()
  })
}

// 批量拒绝
const handleBatchReject = () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要拒绝的申请')
    return
  }

  ElMessageBox.confirm(
    `确定要拒绝选中的 ${selectedApplications.value.length} 个申请吗？`,
    '批量拒绝',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量拒绝成功')
    loadApplications()
  })
}

// 通过申请
const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确定要通过申请 ${row.applicationNo} 吗？`,
    '通过申请',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('申请已通过')
    loadApplications()
  })
}

// 拒绝申请
const handleReject = (row) => {
  ElMessageBox.confirm(
    `确定要拒绝申请 ${row.applicationNo} 吗？`,
    '拒绝申请',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('申请已拒绝')
    loadApplications()
  })
}

// 查看详情
const handleView = (row) => {
  currentApplication.value = { ...row }
  detailVisible.value = true
}

// 查看合同
const handleViewContract = (row) => {
  ElMessage.info(`查看合同 ${row.contractNo} 的详细信息`)
}

// 终止合同
const handleTerminate = (row) => {
  ElMessageBox.confirm(
    `确定要终止合同 ${row.contractNo} 吗？`,
    '终止合同',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('合同已终止')
    loadRecords()
  })
}

// 导出记录
const handleExportRecords = () => {
  ElMessage.success('租赁记录导出成功')
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadApplications()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadApplications()
}

const handleRecordSizeChange = (val) => {
  recordPageSize.value = val
  loadRecords()
}

const handleRecordCurrentChange = (val) => {
  recordCurrentPage.value = val
  loadRecords()
}

// 初始化
onMounted(() => {
  loadApplications()
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

</style>