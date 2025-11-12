<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card total-balance">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.totalBalance.toLocaleString() }}</div>
              <div class="stat-label">钱包总余额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card today-income">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.todayIncome.toLocaleString() }}</div>
              <div class="stat-label">今日充值</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card active-wallets">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.activeWallets }}</div>
              <div class="stat-label">活跃钱包</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card today-transactions">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Switch /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayTransactions }}</div>
              <div class="stat-label">今日交易笔数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要功能区 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>钱包管理</h3>
            <span class="subtitle">业主钱包余额和交易记录管理</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showRechargeDialog">
              <el-icon><Plus /></el-icon>
              充值
            </el-button>
            <el-button type="warning" @click="showRefundDialog">
              <el-icon><RefreshLeft /></el-icon>
              退款
            </el-button>
            <el-button @click="exportTransactions">
              <el-icon><Download /></el-icon>
              导出记录
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="filter-section">
        <el-form :model="queryParams" :inline="true" @submit.prevent>
          <el-form-item label="业主姓名">
            <el-input
              v-model="queryParams.ownerName"
              placeholder="请输入业主姓名"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="钱包状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="正常" value="正常" />
              <el-option label="冻结" value="冻结" />
              <el-option label="注销" value="注销" />
            </el-select>
          </el-form-item>
          <el-form-item label="余额范围">
            <el-input-number
              v-model="queryParams.minBalance"
              placeholder="最小余额"
              :precision="2"
              :min="0"
              style="width: 120px"
            />
            <span style="margin: 0 8px;">-</span>
            <el-input-number
              v-model="queryParams.maxBalance"
              placeholder="最大余额"
              :precision="2"
              :min="0"
              style="width: 120px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <el-table
          v-loading="loading"
          :data="walletList"
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="ownerName" label="业主姓名" width="100" />
          <el-table-column prop="ownerPhone" label="手机号码" width="120">
            <template #default="{ row }">
              <span>{{ maskPhone(row.ownerPhone) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="houseInfo" label="房产信息" width="180" />
          <el-table-column prop="balance" label="当前余额" width="120" align="right">
            <template #default="{ row }">
              <span class="balance" :class="{ 'low-balance': row.balance < 100 }">
                ¥{{ row.balance.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="frozenAmount" label="冻结金额" width="120" align="right">
            <template #default="{ row }">
              <span v-if="row.frozenAmount > 0" class="frozen-amount">
                ¥{{ row.frozenAmount.toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalIncome" label="累计充值" width="120" align="right">
            <template #default="{ row }">
              <span class="income">¥{{ row.totalIncome.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalExpense" label="累计消费" width="120" align="right">
            <template #default="{ row }">
              <span class="expense">¥{{ row.totalExpense.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="钱包状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastTransactionTime" label="最后交易" width="160" />
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button type="success" link @click="handleRecharge(row)">
                <el-icon><Plus /></el-icon>
                充值
              </el-button>
              <el-button type="warning" link @click="handleFreeze(row)" v-if="row.status === '正常'">
                <el-icon><Lock /></el-icon>
                冻结
              </el-button>
              <el-button type="info" link @click="handleUnfreeze(row)" v-if="row.status === '冻结'">
                <el-icon><Unlock /></el-icon>
                解冻
              </el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, row)">
                <el-button type="info" link>
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="transactions">
                      <el-icon><List /></el-icon>交易记录
                    </el-dropdown-item>
                    <el-dropdown-item command="statistics">
                      <el-icon><TrendCharts /></el-icon>统计报表
                    </el-dropdown-item>
                    <el-dropdown-item command="adjust" divided>
                      <el-icon><EditPen /></el-icon>余额调整
                    </el-dropdown-item>
                    <el-dropdown-item command="reset">
                      <el-icon><RefreshRight /></el-icon>重置密码
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>

      <!-- 批量操作 -->
      <div v-if="selectedWallets.length > 0" class="batch-actions">
        <div class="batch-info">
          已选择 {{ selectedWallets.length }} 个钱包
        </div>
        <div class="batch-buttons">
          <el-button type="success" @click="batchRecharge">批量充值</el-button>
          <el-button type="warning" @click="batchFreeze">批量冻结</el-button>
          <el-button type="danger" @click="batchAdjust">批量调整</el-button>
        </div>
      </div>
    </el-card>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="钱包充值"
      width="500px"
    >
      <el-form
        ref="rechargeFormRef"
        :model="rechargeForm"
        :rules="rechargeRules"
        label-width="100px"
      >
        <el-form-item label="业主">
          <el-select
            v-model="rechargeForm.ownerId"
            placeholder="请选择业主"
            filterable
            remote
            :remote-method="searchOwners"
            :loading="ownerLoading"
            style="width: 100%"
            @change="onOwnerChange"
          >
            <el-option
              v-for="owner in ownerOptions"
              :key="owner.id"
              :label="`${owner.name} - ${owner.houseInfo}`"
              :value="owner.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :precision="2"
            :min="0.01"
            :max="999999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="充值方式" prop="paymentMethod">
          <el-select v-model="rechargeForm.paymentMethod" placeholder="请选择充值方式">
            <el-option label="现金" value="现金" />
            <el-option label="银行转账" value="银行转账" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="微信支付" value="微信支付" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="充值说明" prop="remark">
          <el-input
            v-model="rechargeForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入充值说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRecharge" :loading="submitting">
            确认充值
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog
      v-model="refundDialogVisible"
      title="退款操作"
      width="500px"
    >
      <el-form
        ref="refundFormRef"
        :model="refundForm"
        :rules="refundRules"
        label-width="100px"
      >
        <el-form-item label="业主">
          <el-select
            v-model="refundForm.ownerId"
            placeholder="请选择业主"
            filterable
            remote
            :remote-method="searchOwners"
            :loading="ownerLoading"
            style="width: 100%"
            @change="onRefundOwnerChange"
          >
            <el-option
              v-for="owner in ownerOptions"
              :key="owner.id"
              :label="`${owner.name} - ${owner.houseInfo}`"
              :value="owner.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="当前余额">
          <el-input :value="refundForm.currentBalance ? `¥${refundForm.currentBalance.toFixed(2)}` : '请选择业主'" disabled />
        </el-form-item>
        <el-form-item label="退款金额" prop="amount">
          <el-input-number
            v-model="refundForm.amount"
            :precision="2"
            :min="0.01"
            :max="refundForm.currentBalance || 999999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="退款原因" prop="reason">
          <el-select v-model="refundForm.reason" placeholder="请选择退款原因">
            <el-option label="误充金额" value="误充金额" />
            <el-option label="服务取消" value="服务取消" />
            <el-option label="系统错误" value="系统错误" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款说明" prop="remark">
          <el-input
            v-model="refundForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入退款说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRefund" :loading="submitting">
            确认退款
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 钱包详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="钱包详情"
      width="1000px"
    >
      <div v-if="currentWallet" class="wallet-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="3" border>
          <el-descriptions-item label="业主姓名">{{ currentWallet.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ maskPhone(currentWallet.ownerPhone) }}</el-descriptions-item>
          <el-descriptions-item label="房产信息">{{ currentWallet.houseInfo }}</el-descriptions-item>
          <el-descriptions-item label="当前余额">
            <span class="balance-highlight">¥{{ currentWallet.balance.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="冻结金额">
            <span v-if="currentWallet.frozenAmount > 0" class="frozen-amount">
              ¥{{ currentWallet.frozenAmount.toFixed(2) }}
            </span>
            <span v-else>¥0.00</span>
          </el-descriptions-item>
          <el-descriptions-item label="可用余额">
            <span class="available-balance">¥{{ (currentWallet.balance - currentWallet.frozenAmount).toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="累计充值">¥{{ currentWallet.totalIncome.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="累计消费">¥{{ currentWallet.totalExpense.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="钱包状态">
            <el-tag :type="getStatusTag(currentWallet.status)">{{ currentWallet.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 交易记录 -->
        <div class="transaction-section">
          <h4>交易记录</h4>
          <el-table :data="transactionList" stripe>
            <el-table-column prop="transactionNo" label="交易编号" width="180" />
            <el-table-column prop="transactionType" label="交易类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTransactionTypeTag(row.transactionType)">
                  {{ row.transactionType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120" align="right">
              <template #default="{ row }">
                <span :class="getAmountClass(row.transactionType)">
                  {{ row.transactionType === '充值' || row.transactionType === '退款' ? '+' : '-' }}
                  ¥{{ row.amount.toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="balance" label="余额" width="120" align="right">
              <template #default="{ row }">
                ¥{{ row.balance.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式" width="100" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column prop="createTime" label="交易时间" width="160" />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 余额调整对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="余额调整"
      width="500px"
    >
      <el-form
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustRules"
        label-width="100px"
      >
        <el-form-item label="调整类型">
          <el-radio-group v-model="adjustForm.adjustType">
            <el-radio value="add">增加余额</el-radio>
            <el-radio value="subtract">减少余额</el-radio>
            <el-radio value="set">设置余额</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="当前余额">
          <el-input :value="adjustForm.currentBalance ? `¥${adjustForm.currentBalance.toFixed(2)}` : '请选择业主'" disabled />
        </el-form-item>
        <el-form-item label="调整金额" prop="amount">
          <el-input-number
            v-model="adjustForm.amount"
            :precision="2"
            :min="0"
            :max="999999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="adjustForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adjustDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdjust" :loading="submitting">
            确认调整
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, Download, View, Wallet, TrendCharts,
  User, Switch, RefreshLeft, Lock, Unlock, List, EditPen,
  RefreshRight, ArrowDown
} from '@element-plus/icons-vue'
import { walletApi, ownerApi } from '@/api'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const ownerLoading = ref(false)
const walletList = ref([])
const selectedWallets = ref([])
const ownerOptions = ref([])
const transactionList = ref([])
const total = ref(0)

// 统计数据
const statistics = ref({
  totalBalance: 0,
  todayIncome: 0,
  activeWallets: 0,
  todayTransactions: 0
})

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 20,
  ownerName: '',
  status: '',
  minBalance: null,
  maxBalance: null
})

// 对话框状态
const rechargeDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const adjustDialogVisible = ref(false)

// 表单数据
const rechargeForm = reactive({
  ownerId: null,
  amount: 0,
  paymentMethod: '',
  remark: ''
})

const refundForm = reactive({
  ownerId: null,
  currentBalance: 0,
  amount: 0,
  reason: '',
  remark: ''
})

const adjustForm = reactive({
  ownerId: null,
  currentBalance: 0,
  adjustType: 'add',
  amount: 0,
  reason: ''
})

// 当前查看的钱包
const currentWallet = ref(null)

// 表单引用
const rechargeFormRef = ref(null)
const refundFormRef = ref(null)
const adjustFormRef = ref(null)

// 表单验证规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '充值金额必须大于0', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择充值方式', trigger: 'change' }
  ]
}

const refundRules = {
  amount: [
    { required: true, message: '请输入退款金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '退款金额必须大于0', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请选择退款原因', trigger: 'change' }
  ]
}

const adjustRules = {
  amount: [
    { required: true, message: '请输入调整金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '调整金额必须大于等于0', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入调整原因', trigger: 'blur' }
  ]
}

// 计算属性
const getStatusTag = (status) => {
  const map = { '正常': 'success', '冻结': 'warning', '注销': 'danger' }
  return map[status] || 'info'
}

const getTransactionTypeTag = (type) => {
  const map = { '充值': 'success', '消费': 'danger', '退款': 'warning', '冻结': 'info' }
  return map[type] || ''
}

const getAmountClass = (type) => {
  return type === '充值' || type === '退款' ? 'income-text' : 'expense-text'
}

// 手机号脱敏
const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 获取钱包列表
const getWalletList = async () => {
  try {
    loading.value = true
    const response = await walletApi.getList(queryParams)
    const result = handleResponse(response)
    walletList.value = result.data.records
    total.value = result.data.total
  } catch (error) {
    handleError(error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const response = await walletApi.getStatistics()
    const result = handleResponse(response)
    statistics.value = result.data
  } catch (error) {
    handleError(error)
  }
}

// 搜索业主
const searchOwners = async (query) => {
  try {
    if (!query) return
    ownerLoading.value = true
    const response = await ownerApi.getList({
      page: 1,
      size: 20,
      name: query
    })
    const result = handleResponse(response)
    ownerOptions.value = result.data.records.map(owner => ({
      id: owner.id,
      name: owner.name,
      houseInfo: `${owner.buildingName}-${owner.unitName}-${owner.houseNo}`,
      phone: owner.phone,
      balance: owner.wallet?.balance || 0
    }))
  } catch (error) {
    handleError(error)
  } finally {
    ownerLoading.value = false
  }
}

// 业主选择变化
const onOwnerChange = (ownerId) => {
  const owner = ownerOptions.value.find(o => o.id === ownerId)
  if (owner) {
    rechargeForm.ownerName = owner.name
  }
}

const onRefundOwnerChange = (ownerId) => {
  const owner = ownerOptions.value.find(o => o.id === ownerId)
  if (owner) {
    refundForm.currentBalance = owner.balance || 0
    adjustForm.currentBalance = owner.balance || 0
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  getWalletList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.ownerName = ''
  queryParams.status = ''
  queryParams.minBalance = null
  queryParams.maxBalance = null
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedWallets.value = selection
}

// 显示充值对话框
const showRechargeDialog = () => {
  rechargeDialogVisible.value = true
  resetRechargeForm()
}

const handleRecharge = (row) => {
  rechargeDialogVisible.value = true
  Object.assign(rechargeForm, {
    ownerId: row.ownerId,
    ownerName: row.ownerName,
    amount: 0,
    paymentMethod: '',
    remark: ''
  })
}

const resetRechargeForm = () => {
  Object.assign(rechargeForm, {
    ownerId: null,
    amount: 0,
    paymentMethod: '',
    remark: ''
  })
  rechargeFormRef.value?.resetFields()
}

// 提交充值
const submitRecharge = async () => {
  try {
    await rechargeFormRef.value.validate()
    submitting.value = true

    const response = await walletApi.recharge(rechargeForm)
    handleResponse(response)
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  } finally {
    submitting.value = false
  }
}

// 显示退款对话框
const showRefundDialog = () => {
  refundDialogVisible.value = true
  resetRefundForm()
}

const resetRefundForm = () => {
  Object.assign(refundForm, {
    ownerId: null,
    currentBalance: 0,
    amount: 0,
    reason: '',
    remark: ''
  })
  refundFormRef.value?.resetFields()
}

// 提交退款
const submitRefund = async () => {
  try {
    await refundFormRef.value.validate()
    submitting.value = true

    const response = await walletApi.refund(refundForm)
    handleResponse(response)
    ElMessage.success('退款成功')
    refundDialogVisible.value = false
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  } finally {
    submitting.value = false
  }
}

// 查看详情
const handleView = async (row) => {
  try {
    const response = await walletApi.getDetail(row.ownerId)
    const result = handleResponse(response)
    currentWallet.value = result.data.wallet
    transactionList.value = result.data.transactions || []
    detailDialogVisible.value = true
  } catch (error) {
    handleError(error)
  }
}

// 冻结/解冻钱包
const handleFreeze = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认冻结业主 ${row.ownerName} 的钱包？`,
      '冻结确认',
      { type: 'warning' }
    )

    const response = await walletApi.freeze({ ownerId: row.ownerId })
    handleResponse(response)
    ElMessage.success('冻结成功')
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const handleUnfreeze = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认解冻业主 ${row.ownerName} 的钱包？`,
      '解冻确认',
      { type: 'warning' }
    )

    const response = await walletApi.unfreeze({ ownerId: row.ownerId })
    handleResponse(response)
    ElMessage.success('解冻成功')
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 命令处理
const handleCommand = (command, row) => {
  switch (command) {
    case 'transactions':
      showTransactions(row)
      break
    case 'statistics':
      showStatistics(row)
      break
    case 'adjust':
      showAdjustDialog(row)
      break
    case 'reset':
      resetPassword(row)
      break
  }
}

const showTransactions = (row) => {
  // 显示交易记录对话框
  ElMessage.info('交易记录功能开发中')
}

const showStatistics = (row) => {
  // 显示统计报表
  ElMessage.info('统计报表功能开发中')
}

const showAdjustDialog = (row) => {
  adjustDialogVisible.value = true
  Object.assign(adjustForm, {
    ownerId: row.ownerId,
    currentBalance: row.balance,
    adjustType: 'add',
    amount: 0,
    reason: ''
  })
}

// 提交余额调整
const submitAdjust = async () => {
  try {
    await adjustFormRef.value.validate()
    submitting.value = true

    const response = await walletApi.adjustBalance(adjustForm)
    handleResponse(response)
    ElMessage.success('余额调整成功')
    adjustDialogVisible.value = false
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  } finally {
    submitting.value = false
  }
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认重置业主 ${row.ownerName} 的支付密码？`,
      '重置密码确认',
      { type: 'warning' }
    )

    const response = await walletApi.resetPassword({ ownerId: row.ownerId })
    handleResponse(response)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 批量操作
const batchRecharge = () => {
  ElMessage.info('批量充值功能开发中')
}

const batchFreeze = async () => {
  try {
    const normalWallets = selectedWallets.value.filter(wallet => wallet.status === '正常')
    if (normalWallets.length === 0) {
      ElMessage.warning('请选择正常状态的钱包')
      return
    }

    await ElMessageBox.confirm(
      `确认冻结选中的 ${normalWallets.length} 个钱包？`,
      '批量冻结确认',
      { type: 'warning' }
    )

    const ownerIds = normalWallets.map(wallet => wallet.ownerId)
    const response = await walletApi.batchFreeze({ ownerIds })
    handleResponse(response)
    ElMessage.success('批量冻结成功')
    getWalletList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchAdjust = () => {
  ElMessage.info('批量调整功能开发中')
}

// 导出交易记录
const exportTransactions = async () => {
  try {
    const response = await walletApi.exportTransactions(queryParams)
    // 处理文件下载
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `钱包交易记录_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    handleError(error)
  }
}

// 初始化
onMounted(() => {
  getWalletList()
  getStatistics()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

// 统计卡片
.statistics-cards {
  margin-bottom: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px;
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      font-size: 24px;
      color: white;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }

    &.total-balance {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.today-income {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.active-wallets {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.today-transactions {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: white;

        .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 主卡片
.main-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
      }

      .subtitle {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }

    .header-right {
      display: flex;
      gap: 10px;
    }
  }
}

// 筛选区域
.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

// 表格区域
.table-section {
  .balance {
    font-weight: bold;
    color: #67c23a;

    &.low-balance {
      color: #f56c6c;
    }
  }

  .frozen-amount {
    color: #e6a23c;
    font-weight: bold;
  }

  .income {
    color: #67c23a;
    font-weight: bold;
  }

  .expense {
    color: #f56c6c;
    font-weight: bold;
  }
}

// 批量操作
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 15px 20px;
  background-color: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;

  .batch-info {
    font-size: 14px;
    color: #1e40af;
  }

  .batch-buttons {
    display: flex;
    gap: 10px;
  }
}

// 钱包详情
.wallet-detail {
  .balance-highlight {
    font-size: 18px;
    font-weight: bold;
    color: #67c23a;
  }

  .available-balance {
    font-weight: bold;
    color: #409eff;
  }

  .transaction-section {
    margin-top: 20px;

    h4 {
      margin-bottom: 10px;
      color: #303133;
    }

    .income-text {
      color: #67c23a;
      font-weight: bold;
    }

    .expense-text {
      color: #f56c6c;
      font-weight: bold;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .statistics-cards {
    .el-col {
      margin-bottom: 10px;
    }
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start !important;

    .header-right {
      margin-top: 10px;
      width: 100%;
      justify-content: flex-end;
    }
  }

  .filter-section {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>