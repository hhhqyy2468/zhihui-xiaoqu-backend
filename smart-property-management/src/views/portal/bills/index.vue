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
          <el-form-item label="账单编号">
            <el-input
              v-model="filterForm.billNo"
              placeholder="请输入账单编号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="账单状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="全部" :value="null" />
              <el-option
                v-for="item in billStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="账单类型">
            <el-select v-model="filterForm.billType" placeholder="全部类型" clearable style="width: 120px">
              <el-option
                v-for="item in feeTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
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
          <el-table-column
            type="selection"
            width="55"
            :selectable="checkSelectable"
          />
          <el-table-column prop="billNo" label="账单编号" width="200" show-overflow-tooltip />
          <el-table-column prop="feeName" label="账单类型" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              <el-tag :type="getBillTypeTag(row.feeTypeId)">
                {{ row.feeName || row.feeTypeName || getBillTypeName(row.feeTypeId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="billPeriod" label="账单周期" width="120" />
          <el-table-column prop="amount" label="金额" width="130">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount.toLocaleString() }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="缴费期限" width="130" />
          <el-table-column prop="billStatus" label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.billStatus)">
                {{ getStatusName(row.billStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="190" show-overflow-tooltip />
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
                v-if="row.billStatus === 1 || row.billStatus === 0"
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
            <el-tag :type="getBillTypeTag(currentBill.feeTypeId)">
              {{ currentBill.feeName || currentBill.feeTypeName || getBillTypeName(currentBill.feeTypeId) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账单周期">
            {{ currentBill.billPeriod }}
          </el-descriptions-item>
          <el-descriptions-item label="缴费期限">
            {{ currentBill.dueDate }}
          </el-descriptions-item>
          <el-descriptions-item label="账单金额">
            <span class="amount-text">¥{{ (currentBill.amount || 0).toLocaleString() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="账单状态">
            <el-tag :type="getStatusTag(currentBill.billStatus)">
              {{ getStatusName(currentBill.billStatus) }}
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
                ¥{{ (row.unitPrice || 0).toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                ¥{{ (row.amount || 0).toLocaleString() }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="bill-actions">
          <el-button
            v-if="currentBill.billStatus === 1 || currentBill.billStatus === 0"
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
              <span>{{ payBill.feeName || payBill.feeTypeName }}</span>
            </div>
            <div class="bill-row amount-row">
              <span>缴费金额：</span>
              <span class="amount-text">¥{{ (payBill.amount || 0).toLocaleString() }}</span>
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
          <el-form-item v-if="batchPayForm.paymentMethod === 'wallet'" label="支付密码">
            <el-input
              v-model="batchPayForm.payPassword"
              type="password"
              placeholder="请输入6位支付密码"
              maxlength="6"
              show-password
            />
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
import { getMyBillList, getMyBillDetail, payBill as payBillApi, batchPayBills, getMyBillStatistics, exportBillsToExcel } from '@/api/bill'
import { getAllFeeTypes } from '@/api/feeType'
import { getDictDataByType } from '@/api/dict'

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const payDialogVisible = ref(false)
const batchPayDialogVisible = ref(false)
const payLoading = ref(false)
const batchPayLoading = ref(false)

const tableRef = ref()
const currentBill = ref(null)
const selectedBill = ref(null)
const selectedBills = ref([])
const payBill = ref(null)

// 概览数据
const overviewData = ref({
  unpaidAmount: 3580.50,
  paidAmount: 12500.00,
  totalBills: 12,
  overdueCount: 2
})

// 费用类型选项 - 从API动态加载
const feeTypeOptions = ref([])
// 账单状态选项 - 从字典API动态加载
const billStatusOptions = ref([])

// 加载费用类型选项
const loadFeeTypeOptions = async () => {
  console.log('Portal页面 - 开始加载费用类型选项...')
  try {
    const response = await getAllFeeTypes()
    console.log('Portal页面 - 费用类型API响应:', response)
    if (response.code === 200 && response.data) {
      feeTypeOptions.value = response.data.map(item => ({
        label: item.typeName || item.type_name || item.feeName,
        value: item.id || item.fee_type_id || item.feeTypeId
      }))
      console.log('Portal页面 - 处理后的费用类型选项:', feeTypeOptions.value)
    } else {
      console.error('Portal页面 - 获取费用类型失败:', response.msg)
      ElMessage.error('获取费用类型失败')
    }
  } catch (error) {
    console.error('Portal页面 - 加载费用类型失败:', error)
    ElMessage.error('加载费用类型失败')
  }
}

// 加载账单状态字典数据
const loadBillStatusOptions = async () => {
  console.log('Portal页面 - 开始加载账单状态选项...')
  try {
    const response = await getDictDataByType('bill_status')
    console.log('Portal页面 - 账单状态API响应:', response)
    if (response.code === 200 && response.data) {
      billStatusOptions.value = response.data
        .map(item => ({
          label: item.dictLabel,
          value: parseInt(item.dictValue)
        }))
      console.log('Portal页面 - 处理后的账单状态选项:', billStatusOptions.value)
    } else {
      console.error('Portal页面 - 获取账单状态失败:', response.msg)
      // 使用备用数据
      billStatusOptions.value = [
        { label: '待缴费', value: 1 },
        { label: '已缴费', value: 2 },
        { label: '部分缴费', value: 0 }
      ]
    }
  } catch (error) {
    console.error('Portal页面 - 加载账单状态失败:', error)
    // 使用备用数据
    billStatusOptions.value = [
      { label: '待缴费', value: 1 },
      { label: '已缴费', value: 2 },
      { label: '部分缴费', value: 0 }
    ]
  }
}

// 筛选表单
const filterForm = reactive({
  billNo: '',
  status: null,
  billType: null,
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
  paymentMethod: 'wallet',
  payPassword: ''
})

// 计算总金额（只计算待缴费的账单）
const totalAmount = computed(() => {
  return selectedBills.value
    .filter(bill => bill.billStatus === 1)  // 只计算待缴费的账单
    .reduce((sum, bill) => sum + bill.amount, 0)
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
const getBillTypeName = (feeTypeId) => {
  const option = feeTypeOptions.value.find(item => item.value === feeTypeId)
  return option ? option.label : '未知'
}

// 获取账单类型标签
const getBillTypeTag = (feeTypeId) => {
  const tags = ['primary', 'success', 'warning', 'danger', 'info']
  if (!feeTypeId) return 'info'
  return tags[(feeTypeId - 1) % tags.length]
}

// 获取状态名称
const getStatusName = (status) => {
  const statusOption = billStatusOptions.value.find(item => item.value === status)
  return statusOption ? statusOption.label : '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning',  // 部分缴费
    1: 'danger',    // 待缴费
    2: 'success',   // 已缴费
    3: 'info',      // 已超期
    4: 'info',      // 已作废
    5: 'info'       // 已作废
  }
  return tagMap[status] || 'info'
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 调用真实的API
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      billNo: filterForm.billNo || undefined,
      feeTypeId: filterForm.billType || undefined,
      billStatus: filterForm.status || undefined,
      billPeriod: filterForm.dateRange && filterForm.dateRange.length === 2 ?
        `${new Date(filterForm.dateRange[0]).getFullYear()}-${String(new Date(filterForm.dateRange[0]).getMonth() + 1).padStart(2, '0')}` : undefined
    }

    const response = await getMyBillList(params)
    if (response.code === 200) {
      billsData.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取账单数据失败')
    }
  } catch (error) {
    console.error('获取账单数据失败:', error)
    ElMessage.error('获取账单数据失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await getMyBillStatistics()
    if (response.code === 200 && response.data) {
      overviewData.value = {
        unpaidAmount: response.data.unpaidAmount || 0,
        paidAmount: response.data.paidAmount || 0,
        totalBills: response.data.totalBills || 0,
        overdueCount: response.data.overdueCount || 0
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 失败时使用默认值
    overviewData.value = {
      unpaidAmount: 0,
      paidAmount: 0,
      totalBills: pagination.total || 0,
      overdueCount: 0
    }
  }
}

// 筛选
const handleFilter = () => {
  pagination.current = 1
  fetchStatistics()
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(filterForm, {
    billNo: '',
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

// 检查账单是否可选（只能选择待缴费的账单）
const checkSelectable = (row) => {
  return row.billStatus === 1  // 1=待缴费
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
const handleViewDetail = async (row) => {
  try {
    const response = await getMyBillDetail(row.billId)
    if (response.code === 200) {
      currentBill.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取账单详情失败')
    }
  } catch (error) {
    console.error('获取账单详情失败:', error)
    ElMessage.error('获取账单详情失败')
  }
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
    const response = await payBillApi({
      billId: payBill.value.billId,
      paymentMethod: payForm.paymentMethod,
      payPassword: payForm.password
    })

    if (response.code === 200) {
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      payLoading.value = false
      // 重置表单和数据
      payForm.password = ''
      payBill.value = null
      fetchStatistics() // 刷新统计数据
      fetchData() // 刷新列表
    } else {
      ElMessage.error(response.msg || '缴费失败')
      payLoading.value = false
    }
  } catch (error) {
    console.error('缴费失败:', error)
    payLoading.value = false
  }
}

// 提交批量缴费
const handleSubmitBatchPay = async () => {
  if (batchPayForm.paymentMethod === 'wallet' && !batchPayForm.payPassword) {
    ElMessage.warning('请输入支付密码')
    return
  }

  batchPayLoading.value = true

  try {
    const billIds = selectedBills.value.map(bill => bill.billId)
    const response = await batchPayBills({
      billIds: billIds,
      paymentMethod: batchPayForm.paymentMethod,
      payPassword: batchPayForm.payPassword
    })

    if (response.code === 200) {
      ElMessage.success(`批量缴费成功，共缴费 ${selectedBills.value.length} 笔账单`)
      batchPayDialogVisible.value = false
      batchPayLoading.value = false
      selectedBills.value = []
      fetchStatistics() // 刷新统计数据
      fetchData() // 刷新列表
    } else {
      ElMessage.error(response.msg || '批量缴费失败')
      batchPayLoading.value = false
    }
  } catch (error) {
    console.error('批量缴费失败:', error)
    batchPayLoading.value = false
  }
}

// 下载单个账单
const handleDownload = async (row) => {
  try {
    const res = await exportBillsToExcel({ billId: row.billId })
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `账单_${row.billNo}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error('下载失败')
  }
}

// 导出全部账单（按当前筛选条件）
const handleExport = async () => {
  try {
    const params = {
      billNo: filterForm.billNo || undefined,
      billStatus: filterForm.status || undefined,
      feeTypeId: filterForm.billType || undefined,
      beginTime: filterForm.dateRange?.[0] || undefined,
      endTime: filterForm.dateRange?.[1] || undefined
    }
    const res = await exportBillsToExcel(params)
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `我的账单_${new Date().toLocaleDateString('zh-CN')}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 组件挂载
onMounted(() => {
  // 先加载费用类型和账单状态，再加载数据和统计
  loadFeeTypeOptions()
  loadBillStatusOptions()
  fetchStatistics()
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