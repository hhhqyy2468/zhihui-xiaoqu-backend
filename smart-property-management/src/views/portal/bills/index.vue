<template>
  <div class="portal-bills">
    <!-- 账单概览 -->
    <div class="bills-overview">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-content">
              <div class="overview-icon" style="background: rgba(245, 108, 108, 0.1);">
                <el-icon :size="24" color="#F56C6C"><Money /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">¥{{ overviewData.unpaidAmount.toLocaleString() }}</div>
                <div class="overview-label">待缴金额</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-content">
              <div class="overview-icon" style="background: rgba(103, 194, 58, 0.1);">
                <el-icon :size="24" color="#67C23A"><CircleCheck /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">¥{{ overviewData.paidAmount.toLocaleString() }}</div>
                <div class="overview-label">已缴金额</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-content">
              <div class="overview-icon" style="background: rgba(64, 158, 255, 0.1);">
                <el-icon :size="24" color="#409EFF"><Document /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ overviewData.totalBills }}</div>
                <div class="overview-label">总账单数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-content">
              <div class="overview-icon" style="background: rgba(230, 162, 60, 0.1);">
                <el-icon :size="24" color="#E6A23C"><Clock /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ overviewData.overdueCount }}</div>
                <div class="overview-label">逾期账单</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="账单状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="待缴费" :value="0" />
              <el-option label="已缴费" :value="1" />
              <el-option label="已逾期" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="账单类型">
            <el-select v-model="filterForm.billType" placeholder="全部类型" clearable style="width: 120px">
              <el-option label="物业费" value="property" />
              <el-option label="停车费" value="parking" />
              <el-option label="水电费" value="utility" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 账单列表 -->
    <div class="bills-list">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>账单列表</span>
            <div class="header-actions">
              <el-button @click="handleBatchPay" :disabled="selectedBills.length === 0">
                批量缴费
              </el-button>
              <el-button @click="handleExport">
                导出账单
              </el-button>
            </div>
          </div>
        </template>

        <el-table
          ref="tableRef"
          :data="billsData"
          :loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="billNo" label="账单编号" width="180" />
          <el-table-column prop="billType" label="账单类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getBillTypeTag(row.billType)">
                {{ getBillTypeName(row.billType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="billPeriod" label="账单周期" width="120" />
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount.toLocaleString() }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="缴费期限" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                @click="handleViewDetail(row)"
              >
                详情
              </el-button>
              <el-button
                v-if="row.status === 0 || row.status === 2"
                link
                type="success"
                @click="handlePay(row)"
              >
                缴费
              </el-button>
              <el-button
                link
                type="info"
                @click="handleDownload(row)"
              >
                下载
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 账单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="账单详情"
      width="600px"
    >
      <div v-if="currentBill">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单编号">
            {{ currentBill.billNo }}
          </el-descriptions-item>
          <el-descriptions-item label="账单类型">
            <el-tag :type="getBillTypeTag(currentBill.billType)">
              {{ getBillTypeName(currentBill.billType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账单周期">
            {{ currentBill.billPeriod }}
          </el-descriptions-item>
          <el-descriptions-item label="缴费期限">
            {{ currentBill.dueDate }}
          </el-descriptions-item>
          <el-descriptions-item label="账单金额">
            <span class="amount-text">¥{{ currentBill.amount.toLocaleString() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="账单状态">
            <el-tag :type="getStatusTag(currentBill.status)">
              {{ getStatusName(currentBill.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ currentBill.createTime }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="bill-details" v-if="currentBill.details">
          <h4>费用明细</h4>
          <el-table :data="currentBill.details" border>
            <el-table-column prop="itemName" label="费用项目" />
            <el-table-column prop="unitPrice" label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.unitPrice.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                ¥{{ row.amount.toLocaleString() }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="bill-actions">
          <el-button
            v-if="currentBill.status === 0 || currentBill.status === 2"
            type="primary"
            @click="handlePay(currentBill)"
          >
            立即缴费
          </el-button>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 缴费对话框 -->
    <el-dialog
      v-model="payDialogVisible"
      title="账单缴费"
      width="500px"
    >
      <div v-if="payBill">
        <div class="pay-summary">
          <h3>缴费账单</h3>
          <div class="pay-bill-info">
            <div class="bill-row">
              <span>账单编号：</span>
              <span>{{ payBill.billNo }}</span>
            </div>
            <div class="bill-row">
              <span>账单类型：</span>
              <span>{{ getBillTypeName(payBill.billType) }}</span>
            </div>
            <div class="bill-row amount-row">
              <span>缴费金额：</span>
              <span class="amount-text">¥{{ payBill.amount.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <el-form :model="payForm" label-width="80px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="payForm.paymentMethod">
              <el-radio value="wallet">余额支付</el-radio>
              <el-radio value="wechat">微信支付</el-radio>
              <el-radio value="alipay">支付宝</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="支付密码" v-if="payForm.paymentMethod === 'wallet'">
            <el-input
              v-model="payForm.password"
              type="password"
              placeholder="请输入支付密码"
              show-password
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitPay"
            :loading="payLoading"
          >
            确认缴费
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量缴费对话框 -->
    <el-dialog
      v-model="batchPayDialogVisible"
      title="批量缴费"
      width="600px"
    >
      <div class="batch-pay-content">
        <div class="batch-summary">
          <h4>批量缴费账单</h4>
          <div class="selected-bills">
            <div v-for="bill in selectedBills" :key="bill.billId" class="selected-bill">
              <span>{{ bill.billNo }}</span>
              <span class="amount-text">¥{{ bill.amount.toLocaleString() }}</span>
            </div>
          </div>
          <div class="total-amount">
            <span>总金额：</span>
            <span class="amount-text">¥{{ totalAmount.toLocaleString() }}</span>
          </div>
        </div>

        <el-divider />

        <el-form :model="batchPayForm" label-width="80px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="batchPayForm.paymentMethod">
              <el-radio value="wallet">余额支付</el-radio>
              <el-radio value="wechat">微信支付</el-radio>
              <el-radio value="alipay">支付宝</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchPayDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitBatchPay"
            :loading="batchPayLoading"
          >
            确认缴费
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Money,
  CircleCheck,
  Document,
  Clock
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const payDialogVisible = ref(false)
const batchPayDialogVisible = ref(false)
const payLoading = ref(false)
const batchPayLoading = ref(false)

const tableRef = ref()
const currentBill = ref(null)
const payBill = ref(null)
const selectedBills = ref([])

// 概览数据
const overviewData = ref({
  unpaidAmount: 3580.50,
  paidAmount: 12500.00,
  totalBills: 12,
  overdueCount: 2
})

// 筛选表单
const filterForm = reactive({
  status: '',
  billType: '',
  dateRange: []
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 账单数据
const billsData = ref([])

// 缴费表单
const payForm = reactive({
  paymentMethod: 'wallet',
  password: ''
})

// 批量缴费表单
const batchPayForm = reactive({
  paymentMethod: 'wallet'
})

// 计算总金额
const totalAmount = computed(() => {
  return selectedBills.value.reduce((sum, bill) => sum + bill.amount, 0)
})

// 获取模拟数据
const getMockBills = () => {
  const mockBills = []
  const billTypes = ['property', 'parking', 'utility', 'other']
  const statuses = [0, 1, 2] // 0-待缴费 1-已缴费 2-已逾期

  for (let i = 1; i <= 25; i++) {
    const billType = billTypes[Math.floor(Math.random() * billTypes.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const amount = Math.floor(Math.random() * 2000) + 100
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + Math.floor(Math.random() * 30) - 15)

    mockBills.push({
      billId: i,
      billNo: `BILL${String(i).padStart(6, '0')}`,
      billType: billType,
      billPeriod: '2024年11月',
      amount: amount,
      dueDate: dueDate.toISOString().split('T')[0],
      status: status,
      createTime: '2024-11-01 10:00:00',
      details: [
        {
          itemName: getBillItemName(billType),
          unitPrice: amount,
          quantity: 1,
          amount: amount
        }
      ]
    })
  }

  // 按状态排序：待缴费、逾期、已缴费
  mockBills.sort((a, b) => {
    if (a.status === 0 && b.status !== 0) return -1
    if (a.status === 2 && b.status === 1) return -1
    if (a.status === 1 && b.status !== 1) return 1
    return 0
  })

  pagination.total = mockBills.length
  return mockBills
}

// 获取账单项名称
const getBillItemName = (billType) => {
  const itemMap = {
    'property': '物业费',
    'parking': '停车费',
    'utility': '水电费',
    'other': '其他费用'
  }
  return itemMap[billType] || '费用'
}

// 获取账单类型名称
const getBillTypeName = (type) => {
  const typeMap = {
    'property': '物业费',
    'parking': '停车费',
    'utility': '水电费',
    'other': '其他'
  }
  return typeMap[type] || '未知'
}

// 获取账单类型标签
const getBillTypeTag = (type) => {
  const tagMap = {
    'property': 'primary',
    'parking': 'success',
    'utility': 'warning',
    'other': 'info'
  }
  return tagMap[type] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '待缴费',
    1: '已缴费',
    2: '已逾期'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 待缴费
    1: 'success', // 已缴费
    2: 'danger'   // 已逾期
  }
  return tagMap[status] || 'info'
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      billsData.value = getMockBills()
      loading.value = false
    }, 500)
  } catch (error) {
    loading.value = false
    ElMessage.error('获取数据失败')
  }
}

// 筛选
const handleFilter = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(filterForm, {
    status: '',
    billType: '',
    dateRange: []
  })
  handleFilter()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedBills.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchData()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.current = page
  fetchData()
}

// 查看详情
const handleViewDetail = (row) => {
  currentBill.value = { ...row }
  detailDialogVisible.value = true
}

// 缴费
const handlePay = (row) => {
  payBill.value = { ...row }
  payDialogVisible.value = true
}

// 批量缴费
const handleBatchPay = () => {
  if (selectedBills.value.length === 0) {
    ElMessage.warning('请选择要缴费的账单')
    return
  }
  batchPayDialogVisible.value = true
}

// 提交缴费
const handleSubmitPay = async () => {
  if (payForm.paymentMethod === 'wallet' && !payForm.password) {
    ElMessage.warning('请输入支付密码')
    return
  }

  payLoading.value = true

  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      payLoading.value = false
      fetchData()
    }, 2000)
  } catch (error) {
    payLoading.value = false
    ElMessage.error('缴费失败')
  }
}

// 提交批量缴费
const handleSubmitBatchPay = async () => {
  batchPayLoading.value = true

  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success(`批量缴费成功，共缴费 ${selectedBills.value.length} 笔账单`)
      batchPayDialogVisible.value = false
      batchPayLoading.value = false
      selectedBills.value = []
      fetchData()
    }, 2000)
  } catch (error) {
    batchPayLoading.value = false
    ElMessage.error('批量缴费失败')
  }
}

// 下载账单
const handleDownload = (row) => {
  ElMessage.success(`正在下载账单 ${row.billNo}`)
}

// 导出账单
const handleExport = () => {
  ElMessage.success('账单导出成功')
}

// 组件挂载
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.portal-bills {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  .bills-overview {
    margin-bottom: 20px;

    .overview-card {
      .overview-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .overview-icon {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .overview-info {
          .overview-value {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            margin-bottom: 4px;
          }

          .overview-label {
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }

  .filter-section {
    margin-bottom: 20px;
  }

  .bills-list {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }

    .amount-text {
      color: #F56C6C;
      font-weight: bold;
    }

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }

  .bill-details {
    margin-top: 20px;

    h4 {
      margin-bottom: 16px;
      color: #333;
    }
  }

  .bill-actions {
    margin-top: 20px;
    text-align: center;
  }

  .pay-summary {
    h3 {
      margin-bottom: 16px;
      color: #333;
    }

    .pay-bill-info {
      .bill-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 12px;
        font-size: 14px;

        &.amount-row {
          font-size: 16px;
          font-weight: bold;
        }
      }
    }
  }

  .batch-pay-content {
    .batch-summary {
      h4 {
        margin-bottom: 16px;
        color: #333;
      }

      .selected-bills {
        margin-bottom: 16px;
        max-height: 200px;
        overflow-y: auto;

        .selected-bill {
          display: flex;
          justify-content: space-between;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }
        }
      }

      .total-amount {
        display: flex;
        justify-content: space-between;
        font-size: 16px;
        font-weight: bold;
        padding-top: 16px;
        border-top: 1px solid #eee;
      }
    }
  }
}

@media (max-width: 768px) {
  .portal-bills {
    padding: 10px;

    .bills-overview .el-col {
      margin-bottom: 10px;
    }
  }
}
</style>