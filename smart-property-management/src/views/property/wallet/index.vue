<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">虚拟钱包管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>费用管理</el-breadcrumb-item>
        <el-breadcrumb-item>虚拟钱包管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="业主姓名">
          <el-input
            v-model="searchForm.ownerName"
            placeholder="请输入业主姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="钱包状态">
          <el-select
            v-model="searchForm.walletStatus"
            placeholder="请选择钱包状态"
            clearable
            style="width: 150px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="冻结" :value="0" />
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
        type="success"
        @click="handleBatchRecharge"
      >
        <el-icon><CreditCard /></el-icon>
        批量充值
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <!-- 钱包表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="ownerName" label="业主姓名" width="120" sortable />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="walletNo" label="钱包编号" width="160" />
        <el-table-column prop="currentBalance" label="当前余额" width="120" sortable>
          <template #default="{ row }">
            <span class="balance-text">¥{{ row.currentBalance.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalRecharge" label="累计充值" width="120">
          <template #default="{ row }">
            <span class="amount-text success">¥{{ row.totalRecharge.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalConsume" label="累计消费" width="120">
          <template #default="{ row }">
            <span class="amount-text danger">¥{{ row.totalConsume.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="walletStatus" label="钱包状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.walletStatus === 1 ? 'success' : 'danger'">
              {{ row.walletStatus === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hasPassword" label="支付密码" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hasPassword ? 'success' : 'warning'">
              {{ row.hasPassword ? '已设置' : '未设置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              link
              type="success"
              @click="handleRecharge(row)"
            >
              充值
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleFreeze(row)"
              v-if="row.walletStatus === 1"
            >
              冻结
            </el-button>
            <el-button
              link
              type="success"
              @click="handleUnfreeze(row)"
              v-else
            >
              解冻
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

    <!-- 钱包详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="钱包详情"
      width="900px"
    >
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="业主姓名">
              {{ currentWallet.ownerName }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ currentWallet.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="钱包编号">
              {{ currentWallet.walletNo }}
            </el-descriptions-item>
            <el-descriptions-item label="钱包状态">
              <el-tag :type="currentWallet.walletStatus === 1 ? 'success' : 'danger'">
                {{ currentWallet.walletStatus === 1 ? '正常' : '冻结' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前余额">
              <span class="balance-text">¥{{ currentWallet.currentBalance?.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="累计充值">
              <span class="amount-text success">¥{{ currentWallet.totalRecharge?.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="累计消费">
              <span class="amount-text danger">¥{{ currentWallet.totalConsume?.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ currentWallet.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="最后更新">
              {{ currentWallet.updateTime }}
            </el-descriptions-item>
            <el-descriptions-item label="支付密码状态">
              <el-tag :type="currentWallet.hasPassword ? 'success' : 'warning'">
                {{ currentWallet.hasPassword ? '已设置' : '未设置' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 充值记录 -->
        <el-tab-pane label="充值记录" name="recharge">
          <el-table :data="rechargeData" border max-height="400">
            <el-table-column prop="transactionNo" label="交易单号" width="160" />
            <el-table-column prop="amount" label="充值金额" width="120">
              <template #default="{ row }">
                <span class="amount-text success">+¥{{ row.amount.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="充值方式" width="120">
              <template #default="{ row }">
                {{ getPaymentMethodName(row.paymentMethod) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="充值时间" width="180" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 消费记录 -->
        <el-tab-pane label="消费记录" name="consume">
          <el-table :data="consumeData" border max-height="400">
            <el-table-column prop="transactionNo" label="交易单号" width="160" />
            <el-table-column prop="amount" label="消费金额" width="120">
              <template #default="{ row }">
                <span class="amount-text danger">-¥{{ row.amount.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="消费类型" width="120">
              <template #default="{ row }">
                {{ getConsumeTypeName(row.type) }}
              </template>
            </el-table-column>
            <el-table-column prop="relatedBillNo" label="关联账单" width="140" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="消费时间" width="180" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="钱包充值"
      width="500px"
    >
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="业主姓名">
          <el-input v-model="rechargeForm.ownerName" disabled />
        </el-form-item>
        <el-form-item label="当前余额">
          <span class="balance-text">¥{{ rechargeForm.currentBalance?.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            style="width: 200px"
          />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        <el-form-item label="充值方式" prop="paymentMethod">
          <el-select v-model="rechargeForm.paymentMethod" style="width: 200px">
            <el-option
              v-for="item in paymentMethodOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="rechargeForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="rechargeLoading"
          @click="handleRechargeSubmit"
        >
          确认充值
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量充值对话框 -->
    <el-dialog
      v-model="batchRechargeDialogVisible"
      title="批量充值"
      width="600px"
    >
      <el-form :model="batchRechargeForm" :rules="batchRechargeRules" ref="batchRechargeFormRef" label-width="100px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="batchRechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            style="width: 200px"
          />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        <el-form-item label="充值方式" prop="paymentMethod">
          <el-select v-model="batchRechargeForm.paymentMethod" style="width: 200px">
            <el-option
              v-for="item in paymentMethodOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标选择">
          <el-radio-group v-model="batchRechargeForm.targetType">
            <el-radio :label="1">全部业主</el-radio>
            <el-radio :label="2">指定楼栋</el-radio>
            <el-radio :label="3">指定业主</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="batchRechargeForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="batchRechargeDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="batchRechargeLoading"
          @click="handleBatchRechargeSubmit"
        >
          确认批量充值
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, CreditCard } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const rechargeFormRef = ref()
const batchRechargeFormRef = ref()
const loading = ref(false)
const rechargeLoading = ref(false)
const batchRechargeLoading = ref(false)
const detailDialogVisible = ref(false)
const rechargeDialogVisible = ref(false)
const batchRechargeDialogVisible = ref(false)
const selectedRows = ref([])
const activeTab = ref('basic')

// 搜索表单
const searchForm = reactive({
  ownerName: '',
  phone: '',
  walletStatus: ''
})

// 表格数据
const tableData = ref([])
const rechargeData = ref([])
const consumeData = ref([])
const currentWallet = ref({})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 选项数据
const paymentMethodOptions = ref([
  { label: '现金', value: 'cash' },
  { label: '银行转账', value: 'bank' },
  { label: '微信支付', value: 'wechat' },
  { label: '支付宝', value: 'alipay' }
])

const consumeTypeOptions = ref([
  { label: '物业费', value: 'property_fee' },
  { label: '停车费', value: 'parking_fee' },
  { label: '其他费用', value: 'other' }
])

// 充值表单
const rechargeForm = reactive({
  walletId: null,
  ownerName: '',
  currentBalance: 0,
  amount: 0,
  paymentMethod: 'cash',
  remark: ''
})

// 批量充值表单
const batchRechargeForm = reactive({
  amount: 0,
  paymentMethod: 'cash',
  targetType: 1,
  targetIds: [],
  remark: ''
})

// 表单规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, max: 10000, message: '充值金额必须在1-10000元之间', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择充值方式', trigger: 'change' }
  ]
}

const batchRechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, max: 10000, message: '充值金额必须在1-10000元之间', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择充值方式', trigger: 'change' }
  ]
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取支付方式名称
const getPaymentMethodName = (method) => {
  const option = paymentMethodOptions.value.find(item => item.value === method)
  return option ? option.label : '未知'
}

// 获取消费类型名称
const getConsumeTypeName = (type) => {
  const option = consumeTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 生成模拟数据
const generateMockData = () => {
  const wallets = []
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑一', '冯二']

  for (let i = 0; i < 50; i++) {
    const currentBalance = Math.floor(Math.random() * 10000)
    const totalRecharge = currentBalance + Math.floor(Math.random() * 5000)
    const totalConsume = totalRecharge - currentBalance

    wallets.push({
      walletId: i + 1,
      ownerName: owners[i % owners.length],
      phone: '138****' + Math.floor(Math.random() * 10000).toString().padStart(4, '0'),
      walletNo: `WLT${(i + 1).toString().padStart(8, '0')}`,
      currentBalance: currentBalance,
      totalRecharge: totalRecharge,
      totalConsume: totalConsume,
      walletStatus: Math.random() > 0.9 ? 0 : 1, // 10%冻结
      hasPassword: Math.random() > 0.2, // 80%已设置密码
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      updateTime: new Date('2024-01-15 14:30:00').toISOString()
    })
  }

  return wallets
}

// 生成模拟充值记录
const generateMockRechargeData = (walletId) => {
  const records = []
  for (let i = 0; i < 10; i++) {
    records.push({
      transactionNo: `REC${walletId}${(i + 1).toString().padStart(4, '0')}`,
      amount: 100 + Math.floor(Math.random() * 1000),
      paymentMethod: ['cash', 'bank', 'wechat', 'alipay'][Math.floor(Math.random() * 4)],
      status: Math.random() > 0.1 ? 1 : 0,
      createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      remark: i % 3 === 0 ? '定期充值' : '临时充值'
    })
  }
  return records
}

// 生成模拟消费记录
const generateMockConsumeData = (walletId) => {
  const records = []
  for (let i = 0; i < 15; i++) {
    records.push({
      transactionNo: `CON${walletId}${(i + 1).toString().padStart(4, '0')}`,
      amount: 50 + Math.floor(Math.random() * 500),
      type: ['property_fee', 'parking_fee', 'other'][Math.floor(Math.random() * 3)],
      relatedBillNo: `BILL${Math.floor(Math.random() * 100000).toString().padStart(6, '0')}`,
      status: Math.random() > 0.05 ? 1 : 0,
      createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      remark: '自动扣费'
    })
  }
  return records
}

// 加载钱包数据
const loadWallets = () => {
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
  loadWallets()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    ownerName: '',
    phone: '',
    walletStatus: ''
  })
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查看详情
const handleViewDetail = (row) => {
  currentWallet.value = { ...row }
  rechargeData.value = generateMockRechargeData(row.walletId)
  consumeData.value = generateMockConsumeData(row.walletId)
  activeTab.value = 'basic'
  detailDialogVisible.value = true
}

// 充值
const handleRecharge = (row) => {
  Object.assign(rechargeForm, {
    walletId: row.walletId,
    ownerName: row.ownerName,
    currentBalance: row.currentBalance,
    amount: 0,
    paymentMethod: 'cash',
    remark: ''
  })
  rechargeDialogVisible.value = true
}

// 提交充值
const handleRechargeSubmit = () => {
  if (!rechargeFormRef.value) return

  rechargeFormRef.value.validate((valid) => {
    if (valid) {
      rechargeLoading.value = true
      setTimeout(() => {
        ElMessage.success('充值成功')
        rechargeDialogVisible.value = false
        loadWallets()
        rechargeLoading.value = false
      }, 1000)
    }
  })
}

// 批量充值
const handleBatchRecharge = () => {
  Object.assign(batchRechargeForm, {
    amount: 0,
    paymentMethod: 'cash',
    targetType: 1,
    targetIds: [],
    remark: ''
  })
  batchRechargeDialogVisible.value = true
}

// 提交批量充值
const handleBatchRechargeSubmit = () => {
  if (!batchRechargeFormRef.value) return

  batchRechargeFormRef.value.validate((valid) => {
    if (valid) {
      batchRechargeLoading.value = true
      setTimeout(() => {
        ElMessage.success('批量充值成功')
        batchRechargeDialogVisible.value = false
        loadWallets()
        batchRechargeLoading.value = false
      }, 2000)
    }
  })
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置业主"${row.ownerName}"的支付密码吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('密码重置成功，新密码为：123456')
  }).catch(() => {
    // 用户取消操作
  })
}

// 冻结钱包
const handleFreeze = (row) => {
  ElMessageBox.confirm(
    `确定要冻结业主"${row.ownerName}"的钱包吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('钱包冻结成功')
    loadWallets()
  }).catch(() => {
    // 用户取消操作
  })
}

// 解冻钱包
const handleUnfreeze = (row) => {
  ElMessageBox.confirm(
    `确定要解冻业主"${row.ownerName}"的钱包吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('钱包解冻成功')
    loadWallets()
  }).catch(() => {
    // 用户取消操作
  })
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadWallets()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadWallets()
}

// 初始化
onMounted(() => {
  loadWallets()
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

.balance-text {
  color: #67c23a;
  font-weight: bold;
  font-size: 16px;
}

.amount-text {
  font-weight: bold;

  &.success {
    color: #67c23a;
  }

  &.danger {
    color: #f56c6c;
  }
}
</style>