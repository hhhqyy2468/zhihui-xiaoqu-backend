<template>
  <div class="contract-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">车位租赁合同管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>停车管理</el-breadcrumb-item>
        <el-breadcrumb-item>租赁合同</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalCount || 0 }}</div>
            <div class="stat-label">总合同数</div>
          </div>
          <el-icon class="stat-icon" color="#409EFF"><Document /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.activeCount || 0 }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <el-icon class="stat-icon" color="#67C23A"><SuccessFilled /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.expiredCount || 0 }}</div>
            <div class="stat-label">已到期</div>
          </div>
          <el-icon class="stat-icon" color="#E6A23C"><WarningFilled /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">¥{{ (stats.totalAmount || 0).toFixed(2) }}</div>
            <div class="stat-label">总金额</div>
          </div>
          <el-icon class="stat-icon" color="#F56C6C"><Money /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="合同编号">
          <el-input v-model="searchForm.contractNo" placeholder="请输入合同编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="车位编号">
          <el-input v-model="searchForm.spaceNo" placeholder="请输入车位编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="业主姓名">
          <el-input v-model="searchForm.ownerName" placeholder="请输入业主姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="合同状态">
          <el-select v-model="searchForm.contractStatus" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="进行中" :value="1" />
            <el-option label="已到期" :value="2" />
            <el-option label="已终止" :value="3" />
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

    <!-- 合同表格 -->
    <div class="table-section">
      <el-table v-loading="loading" :data="contractList">
        <el-table-column prop="contractNo" label="合同编号" width="150" />
        <el-table-column prop="spaceNo" label="车位编号" width="120" />
        <el-table-column prop="location" label="车位位置" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="承租人" width="120" />
        <el-table-column prop="vehicleNumber" label="车辆号码" width="120" />
        <el-table-column prop="monthlyRent" label="月租金" width="120">
          <template #default="{ row }">
            ¥{{ row.monthlyRent }}
          </template>
        </el-table-column>
        <el-table-column prop="rentalMonths" label="租赁月数" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="contractStatus" label="合同状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.contractStatus)">
              {{ getStatusName(row.contractStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.contractStatus === 1" link type="warning" @click="handleTerminate(row)">
              终止
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

    <!-- 合同详情对话框 -->
    <el-dialog v-model="detailVisible" title="合同详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ currentContract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <el-tag :type="getStatusColor(currentContract.contractStatus)">
            {{ getStatusName(currentContract.contractStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="车位编号">{{ currentContract.spaceNo }}</el-descriptions-item>
        <el-descriptions-item label="车位位置">{{ currentContract.location }}</el-descriptions-item>
        <el-descriptions-item label="承租人">{{ currentContract.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentContract.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="车辆号码">{{ currentContract.vehicleNumber }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentContract.vehicleBrand }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentContract.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="租赁月数">{{ currentContract.rentalMonths }} 个月</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentContract.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentContract.paidAmount }}</el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ formatDate(currentContract.signDate) }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ formatDate(currentContract.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ formatDate(currentContract.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="终止日期">{{ formatDate(currentContract.terminateDate) }}</el-descriptions-item>
        <el-descriptions-item label="终止原因" :span="2">{{ currentContract.terminateReason }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentContract.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- 终止合同对话框 -->
    <el-dialog v-model="terminateVisible" title="终止合同" width="500px">
      <el-form ref="terminateFormRef" :model="terminateForm" label-width="100px">
        <el-form-item label="合同信息">
          <div>{{ currentContract.contractNo }} - {{ currentContract.ownerName }}</div>
        </el-form-item>
        <el-form-item label="终止原因" prop="terminateReason">
          <el-input
            v-model="terminateForm.terminateReason"
            type="textarea"
            :rows="4"
            placeholder="请输入终止原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitTerminate">确 定</el-button>
        <el-button @click="terminateVisible = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Document,
  SuccessFilled,
  WarningFilled,
  Money
} from '@element-plus/icons-vue'
import {
  listContract,
  getContract,
  terminateContract,
  getContractStats
} from '@/api/parking/rentalContract'

// 响应式数据
const loading = ref(false)
const detailVisible = ref(false)
const terminateVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  contractNo: '',
  spaceNo: '',
  ownerName: '',
  contractStatus: null
})

// 统计数据
const stats = ref({})

// 合同列表
const contractList = ref([])
const currentContract = ref({})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 终止表单
const terminateForm = reactive({
  terminateReason: ''
})
const terminateFormRef = ref()

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '进行中',
    2: '已到期',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 加载合同列表
const loadContracts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const response = await listContract(params)
    if (response.code === 200) {
      contractList.value = response.data.rows
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载合同列表失败')
    }
  } catch (error) {
    console.error('加载合同列表错误:', error)
    ElMessage.error('加载合同列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await getContractStats()
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据错误:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadContracts()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    contractNo: '',
    spaceNo: '',
    ownerName: '',
    contractStatus: null
  })
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const response = await getContract(row.id)
    if (response.code === 200) {
      currentContract.value = response.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取合同详情失败')
  }
}

// 终止合同
const handleTerminate = (row) => {
  currentContract.value = row
  terminateForm.terminateReason = ''
  terminateVisible.value = true
}

// 提交终止
const submitTerminate = async () => {
  if (!terminateForm.terminateReason) {
    ElMessage.warning('请输入终止原因')
    return
  }

  try {
    const response = await terminateContract(
      currentContract.value.id,
      terminateForm.terminateReason
    )
    if (response.code === 200) {
      ElMessage.success('合同终止成功')
      terminateVisible.value = false
      loadContracts()
      loadStats()
    } else {
      ElMessage.error(response.msg || '合同终止失败')
    }
  } catch (error) {
    ElMessage.error('合同终止失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadContracts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadContracts()
}

// 初始化
onMounted(() => {
  loadContracts()
  loadStats()
})
</script>

<style lang="scss" scoped>
.contract-container {
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  overflow: hidden;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  position: relative;
  z-index: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 48px;
  opacity: 0.2;
}

.search-section,
.table-section {
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
