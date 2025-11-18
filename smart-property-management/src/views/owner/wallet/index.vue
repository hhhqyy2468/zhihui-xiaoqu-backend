<template>
  <div class="owner-wallet-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的钱包</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的钱包</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 钱包概览卡片 -->
    <div class="wallet-overview">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="balance-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Wallet /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">当前余额</div>
                <div class="card-amount">¥{{ walletInfo.balance ? walletInfo.balance.toFixed(2) : '0.00' }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="recharge-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Plus /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">累计充值</div>
                <div class="card-amount success">¥{{ walletInfo.totalRecharge ? walletInfo.totalRecharge.toFixed(2) : '0.00' }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="consume-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40"><Minus /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">累计消费</div>
                <div class="card-amount danger">¥{{ walletInfo.totalConsume ? walletInfo.totalConsume.toFixed(2) : '0.00' }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 钱包状态和操作 -->
    <div class="wallet-status">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div class="status-info">
              <div class="status-item">
                <span class="label">钱包编号：</span>
                <span class="value">{{ walletInfo.walletNo || '暂无' }}</span>
              </div>
              <div class="status-item">
                <span class="label">钱包状态：</span>
                <el-tag :type="walletInfo.status === 1 ? 'success' : 'danger'">
                  {{ walletInfo.status === 1 ? '正常' : '冻结' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span class="label">支付密码：</span>
                <el-tag :type="walletInfo.hasPassword ? 'success' : 'warning'">
                  {{ walletInfo.hasPassword ? '已设置' : '未设置' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                @click="showRechargeDialog"
                :disabled="walletInfo.status === 0"
              >
                <el-icon><CreditCard /></el-icon>
                虚拟充值
              </el-button>
              <el-button
                v-if="!walletInfo.hasPassword"
                type="warning"
                size="large"
                @click="showSetPasswordDialog"
              >
                <el-icon><Lock /></el-icon>
                设置支付密码
              </el-button>
              <el-button
                v-else
                type="info"
                size="large"
                @click="showChangePasswordDialog"
              >
                <el-icon><Key /></el-icon>
                修改支付密码
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 交易记录 -->
    <div class="transaction-section">
      <el-card>
        <template #header>
          <div class="transaction-header">
            <span>交易记录</span>
            <div class="header-actions">
              <el-date-picker
                v-model="transactionDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                @change="loadTransactions"
              />
              <el-button type="primary" size="small" @click="loadTransactions">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </div>
          </div>
        </template>

        <el-table v-loading="transactionLoading" :data="transactionList">
          <el-table-column prop="transactionNo" label="交易编号" width="200" />
          <el-table-column prop="transactionType" label="交易类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTransactionTypeColor(row.transactionType)">
                {{ getTransactionTypeName(row.transactionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="{ row }">
              <span :class="getAmountClass(row.transactionType)">
                {{ formatAmount(row.amount, row.transactionType) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="balanceBefore" label="交易前余额" width="120">
            <template #default="{ row }">
              ¥{{ row.balanceBefore ? row.balanceBefore.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="balanceAfter" label="交易后余额" width="120">
            <template #default="{ row }">
              ¥{{ row.balanceAfter ? row.balanceAfter.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="transactionStatus" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.transactionStatus === 1 ? 'success' : 'danger'" size="small">
                {{ row.transactionStatus === 1 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column prop="createTime" label="交易时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="transactionPage.current"
            v-model:page-size="transactionPage.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="transactionPage.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleTransactionSizeChange"
            @current-change="handleTransactionCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 虚拟充值对话框 -->
    <el-dialog v-model="rechargeDialogVisible" title="虚拟充值" width="500px">
      <el-form ref="rechargeFormRef" :model="rechargeForm" :rules="rechargeRules" label-width="120px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="0.01"
            :max="10000"
            :precision="2"
            style="width: 100%"
            placeholder="请输入充值金额"
          />
          <div class="form-tip">单次充值金额不超过10000元</div>
        </el-form-item>
        <el-form-item label="支付密码" prop="payPassword">
          <el-input
            v-model="rechargeForm.payPassword"
            type="password"
            placeholder="请输入6位支付密码"
            maxlength="6"
            show-password
          />
          </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRecharge" :loading="rechargeLoading" :disabled="rechargeButtonDisabled">
            确认充值
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 设置支付密码对话框 -->
    <el-dialog v-model="setPasswordDialogVisible" title="设置支付密码" width="500px">
      <el-form ref="setPasswordFormRef" :model="setPasswordForm" :rules="setPasswordRules" label-width="120px">
        <el-form-item label="支付密码" prop="payPassword">
          <el-input
            v-model="setPasswordForm.payPassword"
            type="password"
            placeholder="请输入6位数字支付密码"
            maxlength="6"
            show-password
          />
          <div class="form-tip">支付密码必须为6位数字</div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="setPasswordForm.confirmPassword"
            type="password"
            placeholder="请再次输入支付密码"
            maxlength="6"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="setPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSetPassword" :loading="setPasswordLoading">
            设置密码
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改支付密码对话框 -->
    <el-dialog v-model="changePasswordDialogVisible" title="修改支付密码" width="500px">
      <el-form ref="changePasswordFormRef" :model="changePasswordForm" :rules="changePasswordRules" label-width="120px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="changePasswordForm.oldPassword"
            type="password"
            placeholder="请输入原支付密码"
            maxlength="6"
            show-password
          />
          </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="changePasswordForm.newPassword"
            type="password"
            placeholder="请输入6位数字新密码"
            maxlength="6"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="changePasswordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            maxlength="6"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleChangePassword" :loading="changePasswordLoading">
            修改密码
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Wallet, Plus, Minus, CreditCard, Lock, Key, Search
} from '@element-plus/icons-vue'
import {
  getWalletByUserId,
  virtualRecharge,
  setPayPassword,
  changePayPassword,
  listTransaction
} from '@/api/wallet'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const transactionLoading = ref(false)
const rechargeLoading = ref(false)
const setPasswordLoading = ref(false)
const changePasswordLoading = ref(false)

// 对话框显示状态
const rechargeDialogVisible = ref(false)
const setPasswordDialogVisible = ref(false)
const changePasswordDialogVisible = ref(false)

// 表单引用
const rechargeFormRef = ref()
const setPasswordFormRef = ref()
const changePasswordFormRef = ref()

// 钱包信息
const walletInfo = ref({})

// 交易记录
const transactionList = ref([])
const transactionDate = ref([])
const transactionPage = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 充值表单
const rechargeForm = reactive({
  amount: 0,
  payPassword: ''
})

// 设置密码表单
const setPasswordForm = reactive({
  userId: null,
  payPassword: '',
  confirmPassword: ''
})

// 修改密码表单
const changePasswordForm = reactive({
  userId: null,
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 0.01, max: 10000, message: '充值金额在0.01-10000元之间', trigger: 'blur' }
  ],
  payPassword: [
    { required: true, message: '请输入支付密码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '支付密码必须为6位数字', trigger: 'blur' }
  ]
}

const setPasswordRules = {
  payPassword: [
    { required: true, message: '请输入支付密码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '支付密码必须为6位数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== setPasswordForm.payPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const changePasswordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '原密码必须为6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '新密码必须为6位数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== changePasswordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算属性
const rechargeButtonDisabled = computed(() => {
  return !rechargeForm.amount || rechargeForm.amount <= 0 || !rechargeForm.payPassword
})

// 获取当前用户信息
const userStore = useUserStore()
const getCurrentUserId = () => {
  // 确保从用户 store 中获取真实的用户 ID
  const userId = userStore.userInfo?.id
  if (!userId) {
    console.error('无法获取用户ID，用户信息:', userStore.userInfo)
    throw new Error('用户未登录或用户信息不完整')
  }
  console.log('获取到用户ID:', userId)
  return userId
}

// 加载钱包信息
const loadWalletInfo = async () => {
  loading.value = true
  try {
    const userId = getCurrentUserId()
    const response = await getWalletByUserId(userId)
    if (response.code === 200 && response.data) {
      const wallet = response.data
      walletInfo.value = {
        ...wallet,
        walletNo: `WAL${wallet.userId?.toString().padStart(6, '0')}`,
        hasPassword: !!wallet.payPassword
      }
    } else {
      ElMessage.error('获取钱包信息失败')
    }
  } catch (error) {
    console.error('加载钱包信息失败:', error)
    ElMessage.error('获取钱包信息失败')
  } finally {
    loading.value = false
  }
}

// 加载交易记录
const loadTransactions = async () => {
  transactionLoading.value = true
  try {
    const userId = getCurrentUserId()
    const params = {
      pageNum: transactionPage.current,
      pageSize: transactionPage.size,
      userId: userId
    }

    // 添加日期范围参数
    if (transactionDate.value && transactionDate.value.length === 2) {
      params.startDate = transactionDate.value[0]
      params.endDate = transactionDate.value[1]
    }

    const response = await listTransaction(params)
    if (response.code === 200) {
      // MyBatis Plus分页数据结构：response.data.records 和 response.data.total
      transactionList.value = response.data.records || []
      transactionPage.total = response.data.total || 0
    } else {
      ElMessage.error('获取交易记录失败')
    }
  } catch (error) {
    console.error('加载交易记录失败:', error)
    ElMessage.error('获取交易记录失败')
  } finally {
    transactionLoading.value = false
  }
}

// 显示充值对话框
const showRechargeDialog = () => {
  Object.assign(rechargeForm, {
    amount: 0,
    payPassword: ''
  })
  rechargeDialogVisible.value = true
}

// 显示设置密码对话框
const showSetPasswordDialog = () => {
  Object.assign(setPasswordForm, {
    userId: getCurrentUserId(),
    payPassword: '',
    confirmPassword: ''
  })
  setPasswordDialogVisible.value = true
}

// 显示修改密码对话框
const showChangePasswordDialog = () => {
  Object.assign(changePasswordForm, {
    userId: getCurrentUserId(),
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  changePasswordDialogVisible.value = true
}

// 处理充值
const handleRecharge = async () => {
  try {
    await rechargeFormRef.value.validate()
    rechargeLoading.value = true

    const response = await virtualRecharge({
      amount: rechargeForm.amount,
      payPassword: rechargeForm.payPassword
    })

    if (response.code === 200) {
      ElMessage.success('充值成功')
      rechargeDialogVisible.value = false
      // 刷新钱包信息和交易记录
      await loadWalletInfo()
      await loadTransactions()
    } else {
      ElMessage.error(response.msg || '充值失败')
    }
  } catch (error) {
    console.error('充值失败:', error)
    ElMessage.error(error.message || '充值失败')
  } finally {
    rechargeLoading.value = false
  }
}

// 设置支付密码
const handleSetPassword = async () => {
  try {
    await setPasswordFormRef.value.validate()
    setPasswordLoading.value = true

    const response = await setPayPassword(setPasswordForm)

    if (response.code === 200) {
      ElMessage.success('支付密码设置成功')
      setPasswordDialogVisible.value = false
      // 刷新钱包信息
      await loadWalletInfo()
    } else {
      ElMessage.error(response.msg || '设置密码失败')
    }
  } catch (error) {
    console.error('设置密码失败:', error)
    ElMessage.error(error.message || '设置密码失败')
  } finally {
    setPasswordLoading.value = false
  }
}

// 修改支付密码
const handleChangePassword = async () => {
  try {
    await changePasswordFormRef.value.validate()
    changePasswordLoading.value = true

    const response = await changePayPassword(changePasswordForm)

    if (response.code === 200) {
      ElMessage.success('支付密码修改成功')
      changePasswordDialogVisible.value = false
    } else {
      ElMessage.error(response.msg || '修改密码失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '修改密码失败')
  } finally {
    changePasswordLoading.value = false
  }
}

// 格式化金额
const formatAmount = (amount, type) => {
  const prefix = type === 1 ? '+' : '-'
  return `${prefix}¥${amount ? amount.toFixed(2) : '0.00'}`
}

// 获取金额样式类
const getAmountClass = (type) => {
  return type === 1 ? 'amount-success' : 'amount-danger'
}

// 获取交易类型名称
const getTransactionTypeName = (type) => {
  const typeMap = {
    1: '充值',
    2: '消费',
    3: '退款'
  }
  return typeMap[type] || '未知'
}

// 获取交易类型颜色
const getTransactionTypeColor = (type) => {
  const colorMap = {
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return colorMap[type] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 分页处理
const handleTransactionSizeChange = (val) => {
  transactionPage.size = val
  loadTransactions()
}

const handleTransactionCurrentChange = (val) => {
  transactionPage.current = val
  loadTransactions()
}

onMounted(async () => {
  try {
    // 确保用户信息已加载
    if (!userStore.userInfo?.id) {
      console.warn('用户信息未加载，尝试重新获取')
      await userStore.getUserInfo()
    }

    // 加载钱包信息和交易记录
    await Promise.all([
      loadWalletInfo(),
      loadTransactions()
    ])
  } catch (error) {
    console.error('初始化钱包页面失败:', error)
    ElMessage.error('初始化钱包页面失败，请重新登录')
  }
})
</script>

<style lang="scss" scoped>
.owner-wallet-container {
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

// 钱包概览卡片
.wallet-overview {
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

          &.success {
            color: #67C23A;
          }

          &.danger {
            color: #F56C6C;
          }
        }
      }
    }
  }
}

// 钱包状态和操作区域
.wallet-status {
  margin-bottom: 24px;

  .status-info {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .status-item {
      display: flex;
      align-items: center;

      .label {
        min-width: 80px;
        color: #606266;
        font-size: 14px;
      }

      .value {
        color: #303133;
        font-weight: 500;
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
  }
}

// 交易记录区域
.transaction-section {
  .transaction-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .amount-success {
    color: #67C23A;
    font-weight: 600;
  }

  .amount-danger {
    color: #F56C6C;
    font-weight: 600;
  }

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
}

// 表单提示
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

// 响应式设计
@media (max-width: 768px) {
  .wallet-overview {
    .el-col {
      margin-bottom: 16px;
    }
  }

  .wallet-status {
    .el-col {
      margin-bottom: 16px;
    }

    .action-buttons {
      flex-direction: column;
      align-items: stretch;

      .el-button {
        width: 100%;
      }
    }
  }

  .transaction-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch !important;

    .header-actions {
      flex-direction: column;
      align-items: stretch;

      .el-date-picker {
        width: 100% !important;
      }
    }
  }
}
</style>