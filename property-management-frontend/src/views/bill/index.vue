<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">账单管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>财务管理</el-breadcrumb-item>
        <el-breadcrumb-item>账单管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card bill-total">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.totalAmount.toLocaleString() }}</div>
              <div class="stat-label">账单总金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card paid">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.paidAmount.toLocaleString() }}</div>
              <div class="stat-label">已收金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card unpaid">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.unpaidAmount.toLocaleString() }}</div>
              <div class="stat-label">待收金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card overdue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.overdueCount }}</div>
              <div class="stat-label">逾期账单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新建账单
      </el-button>
      <el-button type="success" @click="showBatchGenerateDialog">
        <el-icon><Operation /></el-icon>
        批量生成
      </el-button>
      <el-button @click="exportBills">
        <el-icon><Download /></el-icon>
        导出账单
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item label="账单编号">
          <el-input
            v-model="queryParams.billNo"
            placeholder="请输入账单编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="业主">
          <el-input
            v-model="queryParams.ownerName"
            placeholder="请输入业主姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select
            v-model="queryParams.feeType"
            placeholder="请选择费用类型"
            clearable
            style="width: 150px"
          >
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="燃气费" value="燃气费" />
            <el-option label="停车费" value="停车费" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择支付状态"
            clearable
            style="width: 120px"
          >
            <el-option label="待支付" value="0" />
            <el-option label="已支付" value="1" />
            <el-option label="已逾期" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="账单期间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
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
        :data="billList"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="billNo" label="账单编号" width="160" />
        <el-table-column prop="ownerName" label="业主姓名" width="100" />
        <el-table-column prop="houseInfo" label="房产信息" width="180" />
        <el-table-column prop="feeType" label="费用类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getFeeTypeTag(row.feeType)">{{ row.feeType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="账单金额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="billPeriod" label="账单期间" width="140" />
        <el-table-column prop="dueDate" label="到期日期" width="110" />
        <el-table-column prop="status" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handlePay(row)"
            >
              <el-icon><Money /></el-icon>
              收款
            </el-button>
            <el-button v-if="row.status === 0" type="warning" link @click="handleRemind(row)">
              <el-icon><Bell /></el-icon>
              催缴
            </el-button>
            <el-dropdown trigger="click" @command="(command) => handleCommand(command, row)">
              <el-button type="info" link>
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">
                    <el-icon><Edit /></el-icon>编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="reminder" divided>
                    <el-icon><Message /></el-icon>发送提醒
                  </el-dropdown-item>
                  <el-dropdown-item command="receipt">
                    <el-icon><Document /></el-icon>下载收据
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
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
    </div>

    <!-- 批量操作 -->
    <div v-if="selectedBills.length > 0" class="batch-actions">
      <div class="batch-info">
        已选择 {{ selectedBills.length }} 项
      </div>
      <div class="batch-buttons">
        <el-button type="success" @click="batchPay">批量收款</el-button>
        <el-button type="warning" @click="batchRemind">批量催缴</el-button>
        <el-button type="danger" @click="batchDelete">批量删除</el-button>
      </div>
    </div>

    <!-- 新增/编辑账单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :before-close="cancelDialog"
    >
      <el-form
        ref="billFormRef"
        :model="billForm"
        :rules="billRules"
        label-width="100px"
      >
        <el-form-item label="业主" prop="ownerId">
          <el-select
            v-model="billForm.ownerId"
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
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="billForm.feeType" placeholder="请选择费用类型">
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="燃气费" value="燃气费" />
            <el-option label="停车费" value="停车费" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="账单金额" prop="amount">
          <el-input-number
            v-model="billForm.amount"
            :precision="2"
            :min="0"
            :max="999999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="账单期间" prop="billPeriod">
          <el-date-picker
            v-model="billForm.billPeriod"
            type="month"
            placeholder="请选择账单期间"
            format="YYYY年MM月"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到期日期" prop="dueDate">
          <el-date-picker
            v-model="billForm.dueDate"
            type="date"
            placeholder="请选择到期日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注说明" prop="remark">
          <el-input
            v-model="billForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialog">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量生成对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量生成账单"
      width="800px"
    >
      <el-form :model="batchForm" label-width="120px">
        <el-form-item label="费用类型">
          <el-select v-model="batchForm.feeType" placeholder="请选择费用类型">
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="燃气费" value="燃气费" />
            <el-option label="停车费" value="停车费" />
          </el-select>
        </el-form-item>
        <el-form-item label="计费方式">
          <el-radio-group v-model="batchForm.billingMethod">
            <el-radio value="area">按面积计费</el-radio>
            <el-radio value="fixed">固定金额</el-radio>
            <el-radio value="custom">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="batchForm.billingMethod === 'area'" label="单价(元/㎡)">
          <el-input-number
            v-model="batchForm.unitPrice"
            :precision="2"
            :min="0"
          />
        </el-form-item>
        <el-form-item v-if="batchForm.billingMethod === 'fixed'" label="固定金额">
          <el-input-number
            v-model="batchForm.fixedAmount"
            :precision="2"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="账单期间">
          <el-date-picker
            v-model="batchForm.billPeriod"
            type="month"
            placeholder="请选择账单期间"
            format="YYYY年MM月"
            value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker
            v-model="batchForm.dueDate"
            type="date"
            placeholder="请选择到期日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="应用范围">
          <el-checkbox-group v-model="batchForm.buildingIds">
            <el-checkbox
              v-for="building in buildingOptions"
              :key="building.id"
              :label="building.id"
            >
              {{ building.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchGenerate" :loading="batchGenerating">
            生成账单
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 账单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="账单详情"
      width="800px"
    >
      <div v-if="currentBill" class="bill-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单编号">{{ currentBill.billNo }}</el-descriptions-item>
          <el-descriptions-item label="费用类型">
            <el-tag :type="getFeeTypeTag(currentBill.feeType)">{{ currentBill.feeType }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业主姓名">{{ currentBill.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentBill.ownerPhone }}</el-descriptions-item>
          <el-descriptions-item label="房产信息">{{ currentBill.houseInfo }}</el-descriptions-item>
          <el-descriptions-item label="账单金额">
            <span class="amount-highlight">¥{{ currentBill.amount.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="账单期间">{{ currentBill.billPeriod }}</el-descriptions-item>
          <el-descriptions-item label="到期日期">{{ currentBill.dueDate }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getStatusTag(currentBill.status)">
              {{ getStatusText(currentBill.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentBill.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentBill.payTime" label="支付时间">{{ currentBill.payTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentBill.payMethod" label="支付方式">{{ currentBill.payMethod }}</el-descriptions-item>
          <el-descriptions-item label="备注说明" :span="2">{{ currentBill.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-tabs">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="支付记录" name="payment">
              <el-table :data="paymentRecords" stripe>
                <el-table-column prop="payTime" label="支付时间" width="160" />
                <el-table-column prop="amount" label="支付金额" width="120" align="right">
                  <template #default="{ row }">
                    <span>¥{{ row.amount.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="payMethod" label="支付方式" width="100" />
                <el-table-column prop="operator" label="操作人" width="100" />
                <el-table-column prop="remark" label="备注" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="催缴记录" name="reminder">
              <el-table :data="reminderRecords" stripe>
                <el-table-column prop="remindTime" label="催缴时间" width="160" />
                <el-table-column prop="remindMethod" label="催缴方式" width="100">
                  <template #default="{ row }">
                    <el-tag size="small">{{ row.remindMethod }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="operator" label="操作人" width="100" />
                <el-table-column prop="remark" label="备注" />
                <el-table-column prop="result" label="结果" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.result === '成功' ? 'success' : 'warning'" size="small">
                      {{ row.result }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, Download, Edit, Delete, View,
  Money, Clock, Warning, CircleCheck, Bell, Message,
  Document, ArrowDown, Operation
} from '@element-plus/icons-vue'
import { billApi, ownerApi, buildingApi } from '@/api'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const batchGenerating = ref(false)
const ownerLoading = ref(false)
const billList = ref([])
const selectedBills = ref([])
const ownerOptions = ref([])
const buildingOptions = ref([])
const total = ref(0)
const dateRange = ref([])

// 统计数据
const statistics = ref({
  totalAmount: 0,
  paidAmount: 0,
  unpaidAmount: 0,
  overdueCount: 0
})

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 20,
  billNo: '',
  ownerName: '',
  feeType: '',
  status: '',
  startDate: '',
  endDate: ''
})

// 对话框状态
const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const activeTab = ref('payment')

// 表单数据
const billForm = reactive({
  id: null,
  ownerId: null,
  feeType: '',
  amount: 0,
  billPeriod: '',
  dueDate: '',
  remark: ''
})

const batchForm = reactive({
  feeType: '',
  billingMethod: 'area',
  unitPrice: 0,
  fixedAmount: 0,
  billPeriod: '',
  dueDate: '',
  buildingIds: []
})

// 当前查看的账单
const currentBill = ref(null)
const paymentRecords = ref([])
const reminderRecords = ref([])

// 表单引用
const billFormRef = ref(null)

// 表单验证规则
const billRules = {
  ownerId: [
    { required: true, message: '请选择业主', trigger: 'change' }
  ],
  feeType: [
    { required: true, message: '请选择费用类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入账单金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于0', trigger: 'blur' }
  ],
  billPeriod: [
    { required: true, message: '请选择账单期间', trigger: 'change' }
  ],
  dueDate: [
    { required: true, message: '请选择到期日期', trigger: 'change' }
  ]
}

// 计算属性
const getStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已逾期' }
  return map[status] || '未知'
}

const getFeeTypeTag = (feeType) => {
  const map = {
    '物业费': 'primary',
    '水费': 'cyan',
    '电费': 'warning',
    '燃气费': 'success',
    '停车费': 'info',
    '其他': ''
  }
  return map[feeType] || ''
}

// 获取账单列表
const getBillList = async () => {
  try {
    loading.value = true

    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = ''
      queryParams.endDate = ''
    }

    const response = await billApi.getList(queryParams)
    const result = handleResponse(response)
    billList.value = result.data.records
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
    const response = await billApi.getStatistics()
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
      phone: owner.phone
    }))
  } catch (error) {
    handleError(error)
  } finally {
    ownerLoading.value = false
  }
}

// 获取楼栋列表
const getBuildingOptions = async () => {
  try {
    const response = await buildingApi.getList({ page: 1, size: 100 })
    const result = handleResponse(response)
    buildingOptions.value = result.data.records
  } catch (error) {
    handleError(error)
  }
}

// 业主选择变化
const onOwnerChange = (ownerId) => {
  const owner = ownerOptions.value.find(o => o.id === ownerId)
  if (owner) {
    // 可以根据业主信息设置其他字段
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  getBillList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.billNo = ''
  queryParams.ownerName = ''
  queryParams.feeType = ''
  queryParams.status = ''
  dateRange.value = []
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedBills.value = selection
}

// 显示新增对话框
const showAddDialog = () => {
  dialogTitle.value = '新增账单'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑账单'
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(billForm, row)
}

// 重置表单
const resetForm = () => {
  Object.assign(billForm, {
    id: null,
    ownerId: null,
    feeType: '',
    amount: 0,
    billPeriod: '',
    dueDate: '',
    remark: ''
  })
  billFormRef.value?.resetFields()
}

// 取消对话框
const cancelDialog = () => {
  dialogVisible.value = false
  resetForm()
}

// 提交表单
const submitForm = async () => {
  try {
    await billFormRef.value.validate()
    submitting.value = true

    const response = isEdit.value
      ? await billApi.update(billForm)
      : await billApi.create(billForm)

    handleResponse(response)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    getBillList()
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
    const response = await billApi.getDetail(row.id)
    const result = handleResponse(response)
    currentBill.value = result.data.bill
    paymentRecords.value = result.data.paymentRecords || []
    reminderRecords.value = result.data.reminderRecords || []
    detailDialogVisible.value = true
    activeTab.value = 'payment'
  } catch (error) {
    handleError(error)
  }
}

// 收款操作
const handlePay = async (row) => {
  try {
    const { value: payMethod } = await ElMessageBox.prompt(
      `确认收取 ${row.ownerName} 的账单金额 ¥${row.amount.toFixed(2)}？`,
      '收款确认',
      {
        confirmButtonText: '确认收款',
        cancelButtonText: '取消',
        inputPlaceholder: '支付方式',
        inputValue: '现金'
      }
    )

    const response = await billApi.pay({
      id: row.id,
      payMethod: payMethod
    })

    handleResponse(response)
    ElMessage.success('收款成功')
    getBillList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 催缴操作
const handleRemind = async (row) => {
  try {
    const { value: method } = await ElMessageBox.prompt(
      '选择催缴方式',
      '催缴提醒',
      {
        confirmButtonText: '发送提醒',
        cancelButtonText: '取消',
        inputPlaceholder: '短信',
        inputValue: '短信'
      }
    )

    const response = await billApi.remind({
      id: row.id,
      remindMethod: method
    })

    handleResponse(response)
    ElMessage.success('催缴提醒已发送')
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 批量操作
const batchPay = async () => {
  const unpaidBills = selectedBills.value.filter(bill => bill.status === 0)
  if (unpaidBills.length === 0) {
    ElMessage.warning('请选择待支付的账单')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认批量收取 ${unpaidBills.length} 笔账单，总金额 ¥${unpaidBills.reduce((sum, bill) => sum + bill.amount, 0).toFixed(2)}？`,
      '批量收款确认',
      { type: 'warning' }
    )

    const ids = unpaidBills.map(bill => bill.id)
    const response = await billApi.batchPay({ ids })
    handleResponse(response)
    ElMessage.success('批量收款成功')
    getBillList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchRemind = async () => {
  const unpaidBills = selectedBills.value.filter(bill => bill.status === 0)
  if (unpaidBills.length === 0) {
    ElMessage.warning('请选择待支付的账单')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认向 ${unpaidBills.length} 位业主发送催缴提醒？`,
      '批量催缴确认',
      { type: 'warning' }
    )

    const ids = unpaidBills.map(bill => bill.id)
    const response = await billApi.batchRemind({ ids })
    handleResponse(response)
    ElMessage.success('批量催缴成功')
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchDelete = async () => {
  if (selectedBills.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedBills.value.length} 条账单记录？`,
      '批量删除确认',
      { type: 'warning' }
    )

    const ids = selectedBills.value.map(bill => bill.id)
    const response = await billApi.batchDelete(ids)
    handleResponse(response)
    ElMessage.success('删除成功')
    getBillList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 批量生成
const showBatchGenerateDialog = () => {
  batchDialogVisible.value = true
  resetBatchForm()
}

const resetBatchForm = () => {
  Object.assign(batchForm, {
    feeType: '',
    billingMethod: 'area',
    unitPrice: 0,
    fixedAmount: 0,
    billPeriod: '',
    dueDate: '',
    buildingIds: []
  })
}

const submitBatchGenerate = async () => {
  try {
    batchGenerating.value = true
    const response = await billApi.batchGenerate(batchForm)
    handleResponse(response)
    ElMessage.success('批量生成成功')
    batchDialogVisible.value = false
    getBillList()
    getStatistics()
  } catch (error) {
    handleError(error)
  } finally {
    batchGenerating.value = false
  }
}

// 导出账单
const exportBills = async () => {
  try {
    const response = await billApi.export(queryParams)
    // 处理文件下载
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `账单明细_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    handleError(error)
  }
}

// 命令处理
const handleCommand = (command, row) => {
  switch (command) {
    case 'edit':
      showEditDialog(row)
      break
    case 'reminder':
      handleRemind(row)
      break
    case 'receipt':
      // 下载收据逻辑
      ElMessage.info('收据下载功能开发中')
      break
    case 'delete':
      deleteBill(row)
      break
  }
}

// 删除账单
const deleteBill = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除账单编号为 ${row.billNo} 的记录？`,
      '删除确认',
      { type: 'warning' }
    )

    const response = await billApi.delete(row.id)
    handleResponse(response)
    ElMessage.success('删除成功')
    getBillList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 初始化
onMounted(() => {
  getBillList()
  getStatistics()
  getBuildingOptions()
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

.action-section {
  margin-bottom: 20px;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.table-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;

  .amount {
    font-weight: bold;
    color: #e6a23c;
  }

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
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

    &.bill-total {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.paid {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.unpaid {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.overdue {
      background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
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

// 账单详情
.bill-detail {
  .amount-highlight {
    font-size: 18px;
    font-weight: bold;
    color: #e6a23c;
  }

  .detail-tabs {
    margin-top: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .statistics-cards {
    .el-col {
      margin-bottom: 10px;
    }
  }

  .search-section {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>