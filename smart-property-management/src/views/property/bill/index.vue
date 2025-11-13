<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">账单管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>费用管理</el-breadcrumb-item>
        <el-breadcrumb-item>账单管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
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
        <el-form-item label="业主姓名">
          <el-input
            v-model="searchForm.ownerName"
            placeholder="请输入业主姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="房间编号">
          <el-input
            v-model="searchForm.houseCode"
            placeholder="请输入房间编号"
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
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增账单
      </el-button>
      <el-button
        type="success"
        @click="handleBatchGenerate"
      >
        <el-icon><Promotion /></el-icon>
        批量生成账单
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
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
        <el-table-column prop="ownerName" label="业主姓名" width="120" />
        <el-table-column prop="houseCode" label="房间编号" width="140" />
        <el-table-column prop="feeName" label="费用类型" width="120" />
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
        <el-table-column prop="dueDate" label="缴费截止时间" width="120">
          <template #default="{ row }">
            <span :class="{ 'overdue': isOverdue(row.dueDate, row.billStatus) }">
              {{ row.dueDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.billStatus === 0"
              link
              type="success"
              @click="handlePay(row)"
            >
              缴费
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
            >
              删除
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

    <!-- 新增/编辑账单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="业主" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="房产" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请选择房产" />
        </el-form-item>
        <el-form-item label="费用类型" prop="feeTypeId">
          <el-select v-model="form.feeTypeId" placeholder="请选择费用类型" style="width: 100%">
            <el-option
              v-for="item in feeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账期" prop="billPeriod">
          <el-date-picker
            v-model="form.billPeriod"
            type="month"
            placeholder="请选择账期"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="应缴金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="缴费截止时间" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            placeholder="请选择缴费截止时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量生成账单对话框 -->
    <el-dialog
      v-model="generateDialogVisible"
      title="批量生成账单"
      width="600px"
    >
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="账期" required>
          <el-date-picker
            v-model="generateForm.billPeriod"
            type="month"
            placeholder="请选择账期"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="费用类型" required>
          <el-select
            v-model="generateForm.feeTypeId"
            placeholder="请选择费用类型"
            style="width: 200px"
          >
            <el-option
              v-for="item in feeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生成范围" required>
          <el-radio-group v-model="generateForm.generateRange">
            <el-radio :label="1">全部房产</el-radio>
            <el-radio :label="2">指定楼栋</el-radio>
            <el-radio :label="3">指定单元</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="目标选择" v-if="generateForm.generateRange > 1">
          <el-select
            v-model="generateForm.targetIds"
            placeholder="请选择目标"
            multiple
            style="width: 300px"
          >
            <el-option
              v-for="item in targetOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="generateLoading"
          @click="handleGenerateSubmit"
        >
          确定生成
        </el-button>
      </template>
    </el-dialog>

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
          {{ payForm.feeName }}
        </el-descriptions-item>
        <el-descriptions-item label="应缴金额">
          <span class="amount-text">¥{{ payForm.amount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="缴费方式">
          <el-select v-model="payForm.paymentMethod" style="width: 200px">
            <el-option label="现金" value="cash" />
            <el-option label="银行转账" value="bank" />
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="钱包支付" value="wallet" />
          </el-select>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button
          type="success"
          :loading="payLoading"
          @click="handlePaySubmit"
        >
          确认缴费
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Promotion } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const generateDialogVisible = ref(false)
const payDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const generateLoading = ref(false)
const payLoading = ref(false)

// 搜索表单
const searchForm = reactive({
  billNo: '',
  ownerName: '',
  houseCode: '',
  feeTypeId: '',
  billStatus: '',
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

// 选项数据
const feeTypeOptions = ref([
  { label: '物业费', value: 1 },
  { label: '停车费', value: 2 },
  { label: '垃圾处理费', value: 3 },
  { label: '电梯费', value: 4 },
  { label: '公共照明费', value: 5 },
  { label: '水费', value: 6 },
  { label: '电费', value: 7 },
  { label: '维修基金', value: 8 }
])

const billStatusOptions = ref([
  { label: '待缴费', value: 0 },
  { label: '已缴费', value: 1 },
  { label: '部分缴费', value: 2 },
  { label: '逾期', value: 3 },
  { label: '已作废', value: 4 }
])

const buildingOptions = ref([
  { label: '1号楼', value: 1 },
  { label: '2号楼', value: 2 },
  { label: '3号楼', value: 3 },
  { label: '4号楼', value: 4 }
])

const unitOptions = ref([
  { label: '1单元', value: 1 },
  { label: '2单元', value: 2 },
  { label: '3单元', value: 3 },
  { label: '4单元', value: 4 }
])

// 表单数据
const form = reactive({
  billId: null,
  billNo: '',
  ownerId: '',
  houseId: '',
  feeTypeId: '',
  billPeriod: '',
  amount: 0,
  dueDate: '',
  remark: ''
})

// 批量生成表单
const generateForm = reactive({
  billPeriod: '',
  feeTypeId: '',
  generateRange: 1,
  targetIds: []
})

// 缴费表单
const payForm = reactive({
  billId: null,
  billNo: '',
  feeName: '',
  amount: 0,
  paymentMethod: 'cash'
})

// 表单规则
const formRules = {
  ownerId: [
    { required: true, message: '请选择业主', trigger: 'change' }
  ],
  houseId: [
    { required: true, message: '请选择房产', trigger: 'change' }
  ],
  feeTypeId: [
    { required: true, message: '请选择费用类型', trigger: 'change' }
  ],
  billPeriod: [
    { required: true, message: '请选择账期', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入应缴金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于等于0', trigger: 'blur' }
  ],
  dueDate: [
    { required: true, message: '请选择缴费截止时间', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑账单' : '新增账单')
const targetOptions = computed(() => {
  return generateForm.generateRange === 2 ? buildingOptions.value : unitOptions.value
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
    0: 'warning', // 待缴费
    1: 'success', // 已缴费
    2: 'info',    // 部分缴费
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
  if (status === 1) return false // 已缴费不算逾期
  return new Date(dueDate) < new Date()
}

// 生成模拟数据
const generateMockData = () => {
  const bills = []
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const statuses = [0, 1, 2, 3]
  const currentYear = new Date().getFullYear()
  const currentMonth = new Date().getMonth() + 1

  for (let i = 0; i < 100; i++) {
    const feeType = feeTypeOptions.value[Math.floor(Math.random() * feeTypeOptions.value.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const amount = 50 + Math.floor(Math.random() * 500)
    const paidAmount = status === 1 ? amount : (status === 2 ? Math.floor(amount * 0.6) : 0)
    const monthOffset = Math.floor(Math.random() * 6) // 最近6个月
    const billingMonth = currentMonth - monthOffset
    const billingYear = billingMonth > 0 ? currentYear : currentYear - 1
    const actualMonth = billingMonth > 0 ? billingMonth : billingMonth + 12

    bills.push({
      billId: i + 1,
      billNo: `BILL${(i + 1).toString().padStart(6, '0')}`,
      ownerName: owners[i % owners.length],
      houseCode: `H${Math.floor(Math.random() * 4 + 1)}${Math.floor(Math.random() * 18 + 1)}${Math.floor(Math.random() * 3 + 1)}`,
      feeName: feeType.label,
      feeTypeId: feeType.value,
      billPeriod: `${billingYear}-${actualMonth.toString().padStart(2, '0')}`,
      amount: amount,
      paidAmount: paidAmount,
      billStatus: status,
      dueDate: `${billingYear}-${actualMonth.toString().padStart(2, '0')}-25`,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    })
  }

  return bills
}

// 加载账单数据
const loadBills = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockData()
    tableData.value = mockData.slice(
      (pagination.current - 1) * pagination.pageSize,
      pagination.current * pagination.pageSize
    )
    pagination.total = mockData.length
    loading.value = false
  }, 500)
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
    ownerName: '',
    houseCode: '',
    feeTypeId: '',
    billStatus: '',
    billPeriod: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    billId: null,
    billNo: '',
    ownerId: '',
    houseId: '',
    feeTypeId: '',
    billPeriod: '',
    amount: 0,
    dueDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除账单"${row.billNo}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadBills()
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的账单')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的${selectedRows.value.length}个账单吗？`,
    '批量删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量删除成功')
    loadBills()
  })
}

// 批量生成账单
const handleBatchGenerate = () => {
  Object.assign(generateForm, {
    billPeriod: '',
    feeTypeId: '',
    generateRange: 1,
    targetIds: []
  })
  generateDialogVisible.value = true
}

// 提交批量生成
const handleGenerateSubmit = () => {
  if (!generateForm.billPeriod || !generateForm.feeTypeId) {
    ElMessage.warning('请填写完整的生成条件')
    return
  }

  if (generateForm.generateRange > 1 && generateForm.targetIds.length === 0) {
    ElMessage.warning('请选择生成目标')
    return
  }

  generateLoading.value = true
  setTimeout(() => {
    ElMessage.success('批量生成账单成功')
    generateDialogVisible.value = false
    loadBills()
    generateLoading.value = false
  }, 2000)
}

// 缴费
const handlePay = (row) => {
  Object.assign(payForm, {
    billId: row.billId,
    billNo: row.billNo,
    feeName: row.feeName,
    amount: row.amount,
    paymentMethod: 'cash'
  })
  payDialogVisible.value = true
}

// 提交缴费
const handlePaySubmit = () => {
  payLoading.value = true
  setTimeout(() => {
    ElMessage.success('缴费成功')
    payDialogVisible.value = false
    loadBills()
    payLoading.value = false
  }, 1000)
}

// 查看详情
const handleViewDetail = (row) => {
  ElMessage.info(`查看账单${row.billNo}的详细信息`)
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadBills()
    }
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

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 初始化
onMounted(() => {
  loadBills()
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
}

.overdue {
  color: #f56c6c;
  font-weight: bold;
}
</style>