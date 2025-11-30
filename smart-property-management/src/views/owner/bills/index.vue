<template>
  <div class="owner-bills-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的账单</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的账单</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 账单统计卡片 -->
    <div class="bill-overview">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="total-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Document /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待缴费账单</div>
                <div class="card-amount">{{ billStats.pendingCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="pending-amount-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Money /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待缴总金额</div>
                <div class="card-amount pending">¥{{ (billStats.pendingAmount || 0).toFixed(2) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overdue-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Warning /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">逾期账单</div>
                <div class="card-amount overdue">{{ billStats.overdueCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="paid-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Select /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">已缴费账单</div>
                <div class="card-amount paid">{{ billStats.paidCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="账单编号">
          <el-input
            v-model="searchForm.billNo"
            placeholder="请输入账单编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select
            v-model="searchForm.feeTypeId"
            placeholder="请选择费用类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in feeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账单状态">
          <el-select
            v-model="searchForm.billStatus"
            placeholder="请选择账单状态"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="item in billStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账期">
          <el-date-picker
            v-model="searchForm.billPeriod"
            type="month"
            placeholder="请选择账期"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 150px"
          />
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
        type="success"
        :disabled="!canBatchPay"
        @click="handleBatchPay"
      >
        <el-icon><Money /></el-icon>
        批量缴费 ({{ selectedRows.length }})
      </el-button>
      <el-button
        type="primary"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出账单
      </el-button>
    </div>

    <!-- 账单表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="billNo" label="账单编号" width="160" sortable />
        <el-table-column prop="feeTypeName" label="费用类型" width="120">
          <template #default="{ row }">
            {{ row.feeTypeName || row.fee_name || '未知费用类型' }}
          </template>
        </el-table-column>
        <el-table-column prop="billPeriod" label="账期" width="100" />
        <el-table-column prop="amount" label="应缴金额" width="120" sortable>
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="实缴金额" width="120">
          <template #default="{ row }">
            <span class="amount-text" :class="getPaidAmountClass(row.paidAmount, row.amount)">
              ¥{{ row.paidAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="billStatus" label="账单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getBillStatusTag(row.billStatus)">
              {{ getBillStatusName(row.billStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="缴费截止时间" width="140">
          <template #default="{ row }">
            <span :class="{ 'overdue': isOverdue(row.dueDate, row.billStatus) }">
              {{ row.dueDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
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
              v-if="canPayBill(row.billStatus)"
              link
              type="success"
              @click="handlePay(row)"
            >
              {{ row.billStatus === 3 ? '补缴' : '缴费' }}
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
    </div>

    <!-- 缴费对话框 -->
    <el-dialog
      v-model="payDialogVisible"
      title="账单缴费"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="账单编号">
          {{ payForm.billNo }}
        </el-descriptions-item>
        <el-descriptions-item label="费用类型">
          {{ payForm.feeTypeName }}
        </el-descriptions-item>
        <el-descriptions-item label="应缴金额">
          <span class="amount-text">¥{{ payForm.amount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="缴费方式">
          <el-radio-group v-model="payForm.paymentMethod">
            <el-radio label="wallet">钱包支付</el-radio>
            <el-radio label="cash">现金支付</el-radio>
            <el-radio label="bank">银行转账</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </el-descriptions-item>
        <el-descriptions-item v-if="payForm.paymentMethod === 'wallet'" label="支付密码">
          <el-input
            v-model="payForm.payPassword"
            type="password"
            placeholder="请输入6位支付密码"
            maxlength="6"
            show-password
            style="width: 200px"
          />
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button
          type="success"
          :loading="payLoading"
          :disabled="payForm.paymentMethod === 'wallet' && !payForm.payPassword"
          @click="handlePaySubmit"
        >
          确认缴费
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量缴费对话框 -->
    <el-dialog
      v-model="batchPayDialogVisible"
      title="批量缴费"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账单数量" span="2">
          {{ batchPayForm.bills.length }} 个
        </el-descriptions-item>
        <el-descriptions-item label="总应缴金额" span="2">
          <span class="amount-text">¥{{ getTotalAmount().toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="缴费方式" span="2">
          <el-radio-group v-model="batchPayForm.paymentMethod">
            <el-radio label="wallet">钱包支付</el-radio>
            <el-radio label="cash">现金支付</el-radio>
            <el-radio label="bank">银行转账</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </el-descriptions-item>
        <el-descriptions-item v-if="batchPayForm.paymentMethod === 'wallet'" label="支付密码" span="2">
          <el-input
            v-model="batchPayForm.payPassword"
            type="password"
            placeholder="请输入6位支付密码"
            maxlength="6"
            show-password
            style="width: 200px"
          />
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="batchPayDialogVisible = false">取消</el-button>
        <el-button
          type="success"
          :loading="batchPayLoading"
          :disabled="batchPayForm.paymentMethod === 'wallet' && !batchPayForm.payPassword"
          @click="handleBatchPaySubmit"
        >
          确认批量缴费
        </el-button>
      </template>
    </el-dialog>

    <!-- 账单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="账单详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账单编号" span="2">
          <el-text type="primary">{{ detailForm.billNo }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="费用类型">
          {{ detailForm.feeTypeName }}
        </el-descriptions-item>
        <el-descriptions-item label="计费周期">
          {{ detailForm.billPeriod }}
        </el-descriptions-item>
        <el-descriptions-item label="账单状态">
          <el-tag :type="getBillStatusTag(detailForm.billStatus)">
            {{ getBillStatusName(detailForm.billStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="应缴金额">
          <span class="amount-text">¥{{ (detailForm.amount || 0).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实缴金额">
          <span class="amount-text" :class="getPaidAmountClass(detailForm.paidAmount || 0, detailForm.amount || 0)">
            ¥{{ (detailForm.paidAmount || 0).toFixed(2) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="折扣金额">
          <span class="amount-text discount">¥{{ (detailForm.discountAmount || 0).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="缴费方式">
          {{ detailForm.payMethod || '未缴费' }}
        </el-descriptions-item>
        <el-descriptions-item label="缴费时间">
          {{ detailForm.payTime || '未缴费' }}
        </el-descriptions-item>
        <el-descriptions-item label="缴费截止时间">
          <span :class="{ 'overdue': isOverdue(detailForm.dueDate, detailForm.billStatus) }">
            {{ detailForm.dueDate }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" span="2">
          {{ formatDateTime(detailForm.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" span="2" v-if="detailForm.updateTime">
          {{ formatDateTime(detailForm.updateTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" span="2">
          {{ detailForm.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="detailForm.billStatus !== 2"
          type="success"
          @click="handlePayFromDetail"
        >
          立即缴费
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document, Money, Warning, Select, Search, Refresh, Download
} from '@element-plus/icons-vue'
import {
  getMyBillList,
  getMyBillDetail,
  payBill,
  batchPayBills,
  exportBillsToExcel
} from '@/api/bill'
import { getAllFeeTypes } from '@/api/feeType'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const payLoading = ref(false)
const batchPayLoading = ref(false)
const detailDialogVisible = ref(false)
const payDialogVisible = ref(false)
const batchPayDialogVisible = ref(false)
const selectedRows = ref([])

// 获取当前用户信息
const userStore = useUserStore()
const getCurrentUserId = () => {
  const userId = userStore.userInfo?.id
  if (!userId) {
    console.error('无法获取用户ID，用户信息:', userStore.userInfo)
    throw new Error('用户未登录或用户信息不完整')
  }
  return userId
}

// 搜索表单
const searchForm = reactive({
  billNo: '',
  feeTypeId: null,
  billStatus: null,
  billPeriod: ''
})

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 账单统计
const billStats = ref({
  pendingCount: 0,
  pendingAmount: 0,
  overdueCount: 0,
  paidCount: 0
})

// 选项数据 - 从API动态加载
const feeTypeOptions = ref([])

const billStatusOptions = ref([
  { label: '全部', value: null },
  { label: '待缴费', value: 1 },
  { label: '已缴费', value: 2 },
  { label: '部分缴费', value: 0 },
  { label: '逾期', value: 3 },
  { label: '已作废', value: 4 }
])

// 缴费表单
const payForm = reactive({
  billId: null,
  billNo: '',
  feeTypeName: '',
  amount: 0,
  paymentMethod: 'wallet',
  payPassword: ''
})

// 批量缴费表单
const batchPayForm = reactive({
  bills: [],
  paymentMethod: 'wallet',
  payPassword: ''
})

// 详情表单
const detailForm = reactive({
  billNo: '',
  feeTypeName: '',
  billPeriod: '',
  amount: 0,
  paidAmount: 0,
  discountAmount: 0,
  billStatus: 1,
  payMethod: '',
  payTime: '',
  dueDate: '',
  createTime: '',
  updateTime: '',
  remark: ''
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取账单状态名称
const getBillStatusName = (status) => {
  const option = billStatusOptions.value.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取账单状态标签
const getBillStatusTag = (status) => {
  const tagMap = {
    1: 'warning', // 待缴费
    2: 'success', // 已缴费
    0: 'info',    // 部分缴费
    3: 'danger',  // 逾期
    4: 'info'     // 已作废
  }
  return tagMap[status] || 'info'
}

// 获取实缴金额样式
const getPaidAmountClass = (paidAmount, amount) => {
  if (paidAmount >= amount) return 'paid-full'
  if (paidAmount > 0) return 'paid-partial'
  return 'paid-none'
}

// 判断是否逾期
const isOverdue = (dueDate, status) => {
  if (status === 2) return false // 已缴费不算逾期
  if (!dueDate) return false // 没有截止时间不算逾期
  return new Date(dueDate) < new Date()
}

// 判断是否可以缴费
const canPayBill = (status) => {
  // 只有待缴费(1)、部分缴费(0)、逾期(3)可以缴费
  // 已缴费(2)和已作废(4)不能缴费
  return [0, 1, 3].includes(status)
}

// 计算属性 - 是否可以批量缴费
const canBatchPay = computed(() => {
  if (selectedRows.value.length === 0) return false
  return selectedRows.value.every(row => canPayBill(row.billStatus))
})

// 获取总金额
const getTotalAmount = () => {
  return batchPayForm.bills.reduce((total, bill) => total + bill.amount, 0)
}

// 加载账单数据
const loadBills = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }

    console.log('调用业主账单列表API，参数:', params)
    const response = await getMyBillList(params)
    console.log('业主账单API完整响应:', response)
    if (response.code === 200) {
      console.log('业主账单API返回数据:', response.data.records)
      console.log('账单记录数量:', response.data.records?.length)
      console.log('分页总数:', response.data.total)
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0

      // 更新统计数据
      updateBillStats()
    } else {
      ElMessage.error(response.msg || '查询失败')
    }
  } catch (error) {
    console.error('加载账单数据失败:', error)
    ElMessage.error('加载账单数据失败')
  } finally {
    loading.value = false
  }
}

// 更新账单统计
const updateBillStats = () => {
  const stats = {
    pendingCount: 0,
    pendingAmount: 0,
    overdueCount: 0,
    paidCount: 0
  }

  tableData.value.forEach(bill => {
    if (bill.billStatus === 1) {
      stats.pendingCount++
      stats.pendingAmount += bill.amount - bill.paidAmount
    }
    if (bill.billStatus === 3) {
      stats.overdueCount++
    }
    if (bill.billStatus === 2) {
      stats.paidCount++
    }
  })

  billStats.value = stats
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadBills()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    billNo: '',
    feeTypeId: null,
    billStatus: null,
    billPeriod: ''
  })
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    const response = await getMyBillDetail(row.billId)
    if (response.code === 200) {
      Object.assign(detailForm, response.data)
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取详情失败')
    }
  } catch (error) {
    console.error('获取账单详情失败:', error)
    ElMessage.error('获取账单详情失败')
  }
}

// 缴费
const handlePay = (row) => {
  Object.assign(payForm, {
    billId: row.billId,
    billNo: row.billNo,
    feeTypeName: row.feeTypeName,
    amount: row.amount,
    paymentMethod: 'wallet',
    payPassword: ''
  })
  payDialogVisible.value = true
}

// 提交缴费
const handlePaySubmit = async () => {
  if (payForm.paymentMethod === 'wallet' && !payForm.payPassword) {
    ElMessage.warning('请输入支付密码')
    return
  }

  payLoading.value = true
  try {
    const data = {
      billId: payForm.billId,
      paymentMethod: payForm.paymentMethod,
      payPassword: payForm.paymentMethod === 'wallet' ? payForm.payPassword : undefined
    }

    const response = await payBill(data)
    if (response.code === 200) {
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      loadBills()
    } else {
      ElMessage.error(response.msg || '缴费失败')
    }
  } catch (error) {
    console.error('缴费失败:', error)
    ElMessage.error(error.message || '缴费失败')
  } finally {
    payLoading.value = false
  }
}

// 批量缴费
const handleBatchPay = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要缴费的账单')
    return
  }

  Object.assign(batchPayForm, {
    bills: [...selectedRows.value],
    paymentMethod: 'wallet',
    payPassword: ''
  })
  batchPayDialogVisible.value = true
}

// 提交批量缴费
const handleBatchPaySubmit = async () => {
  if (batchPayForm.paymentMethod === 'wallet' && !batchPayForm.payPassword) {
    ElMessage.warning('请输入支付密码')
    return
  }

  batchPayLoading.value = true
  try {
    const data = {
      billIds: batchPayForm.bills.map(bill => bill.billId),
      paymentMethod: batchPayForm.paymentMethod,
      payPassword: batchPayForm.paymentMethod === 'wallet' ? batchPayForm.payPassword : undefined
    }

    const response = await batchPayBills(data)
    if (response.code === 200) {
      ElMessage.success('批量缴费成功')
      batchPayDialogVisible.value = false
      selectedRows.value = []
      loadBills()
    } else {
      ElMessage.error(response.msg || '批量缴费失败')
    }
  } catch (error) {
    console.error('批量缴费失败:', error)
    ElMessage.error(error.message || '批量缴费失败')
  } finally {
    batchPayLoading.value = false
  }
}

// 从详情页缴费
const handlePayFromDetail = () => {
  detailDialogVisible.value = false
  const bill = {
    billId: detailForm.billId,
    billNo: detailForm.billNo,
    feeTypeName: detailForm.feeTypeName,
    amount: detailForm.amount
  }
  handlePay(bill)
}

// 导出账单
const handleExport = () => {
  const params = {
    ...searchForm,
    pageNum: 1,
    pageSize: 1000 // 导出全部数据
  }

  exportBillsToExcel(params).then(response => {
    // 创建下载链接
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `我的账单_${new Date().toLocaleDateString()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  }).catch(error => {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  })
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadBills()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadBills()
}

// 加载费用类型选项
const loadFeeTypeOptions = async () => {
  console.log('开始加载费用类型选项...')
  try {
    const response = await getAllFeeTypes()
    console.log('费用类型API响应:', response)
    if (response.code === 200 && response.data) {
      feeTypeOptions.value = response.data.map(item => ({
        label: item.typeName || item.type_name,
        value: item.id || item.fee_type_id || item.feeTypeId
      }))
      console.log('处理后的费用类型选项:', feeTypeOptions.value)
    } else {
      console.error('获取费用类型失败:', response.msg)
      ElMessage.error('获取费用类型失败')
    }
  } catch (error) {
    console.error('加载费用类型失败:', error)
    ElMessage.error('加载费用类型失败')
  }
}

// 初始化
onMounted(() => {
  try {
    // 确保用户信息已加载
    if (!userStore.userInfo?.id) {
      console.warn('用户信息未加载，尝试重新获取')
      userStore.getUserInfo()
    }

    // 加载费用类型和账单数据
    loadFeeTypeOptions()
    loadBills()
  } catch (error) {
    console.error('初始化账单页面失败:', error)
    ElMessage.error('初始化账单页面失败，请重新登录')
  }
})
</script>

<style lang="scss" scoped>
.owner-bills-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;

  .page-title {
    margin: 0 0 16px 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

// 账单统计卡片
.bill-overview {
  margin-bottom: 24px;

  .el-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .card-content {
      display: flex;
      align-items: center;
      padding: 8px 0;

      .card-icon {
        margin-right: 16px;
        color: #409EFF;
      }

      .card-info {
        flex: 1;

        .card-title {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }

        .card-amount {
          font-size: 24px;
          font-weight: 600;
          color: #303133;

          &.pending {
            color: #E6A23C;
          }

          &.overdue {
            color: #F56C6C;
          }

          &.paid {
            color: #67C23A;
          }
        }
      }
    }
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

.amount-text {
  color: #f56c6c;
  font-weight: bold;

  &.paid-full {
    color: #67c23a;
  }

  &.paid-partial {
    color: #e6a23c;
  }

  &.paid-none {
    color: #f56c6c;
  }

  &.discount {
    color: #909399;
    text-decoration: line-through;
  }
}

.overdue {
  color: #f56c6c;
  font-weight: bold;
}

// 响应式设计
@media (max-width: 768px) {
  .bill-overview {
    .el-col {
      margin-bottom: 16px;
    }
  }

  .search-section {
    .el-form {
      .el-form-item {
        margin-bottom: 16px;
        display: block;

        .el-input,
        .el-select,
        .el-date-picker {
          width: 100% !important;
        }
      }
    }
  }

  .action-section {
    .el-button {
      width: 100%;
      margin-bottom: 8px;
    }
  }
}
</style>