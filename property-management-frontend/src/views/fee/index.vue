<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card total-types">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalTypes }}</div>
              <div class="stat-label">费用类型总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card active-types">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.activeTypes }}</div>
              <div class="stat-label">启用中类型</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card recurring-types">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Refresh /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.recurringTypes }}</div>
              <div class="stat-label">循环收费类型</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card custom-types">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.customTypes }}</div>
              <div class="stat-label">自定义计费类型</div>
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
            <h3>费用类型管理</h3>
            <span class="subtitle">管理各类收费项目和计费规则</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showAddDialog">
              <el-icon><Plus /></el-icon>
              新建费用类型
            </el-button>
            <el-button @click="exportFeeTypes">
              <el-icon><Download /></el-icon>
              导出配置
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="filter-section">
        <el-form :model="queryParams" :inline="true" @submit.prevent>
          <el-form-item label="费用名称">
            <el-input
              v-model="queryParams.feeName"
              placeholder="请输入费用名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="计费方式">
            <el-select
              v-model="queryParams.billingMethod"
              placeholder="请选择计费方式"
              clearable
              style="width: 150px"
            >
              <el-option label="按面积" value="按面积" />
              <el-option label="固定金额" value="固定金额" />
              <el-option label="按用量" value="按用量" />
              <el-option label="按户数" value="按户数" />
              <el-option label="自定义" value="自定义" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 100px"
            >
              <el-option label="启用" :value="true" />
              <el-option label="禁用" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item label="循环周期">
            <el-select
              v-model="queryParams.billingCycle"
              placeholder="请选择循环周期"
              clearable
              style="width: 120px"
            >
              <el-option label="每月" value="每月" />
              <el-option label="每季度" value="每季度" />
              <el-option label="每半年" value="每半年" />
              <el-option label="每年" value="每年" />
              <el-option label="一次性" value="一次性" />
            </el-select>
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
          :data="feeTypeList"
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="feeName" label="费用名称" width="140" />
          <el-table-column prop="feeCode" label="费用编码" width="120" />
          <el-table-column prop="billingMethod" label="计费方式" width="100">
            <template #default="{ row }">
              <el-tag :type="getBillingMethodTag(row.billingMethod)">
                {{ row.billingMethod }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="100" align="right">
            <template #default="{ row }">
              <span v-if="row.billingMethod === '固定金额'">
                ¥{{ row.unitPrice.toFixed(2) }}
              </span>
              <span v-else-if="row.billingMethod === '按面积'">
                ¥{{ row.unitPrice.toFixed(2) }}/㎡
              </span>
              <span v-else-if="row.billingMethod === '按用量'">
                ¥{{ row.unitPrice.toFixed(2) }}/{{ row.unit }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="billingCycle" label="收费周期" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ row.billingCycle }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="defaultAmount" label="默认金额" width="110" align="right">
            <template #default="{ row }">
              <span v-if="row.defaultAmount">
                ¥{{ row.defaultAmount.toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'">
                {{ row.status ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button type="warning" link @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, row)">
                <el-button type="info" link>
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="copy">
                      <el-icon><DocumentCopy /></el-icon>复制
                    </el-dropdown-item>
                    <el-dropdown-item :command="row.status ? 'disable' : 'enable'">
                      <el-icon><Switch /></el-icon>{{ row.status ? '禁用' : '启用' }}
                    </el-dropdown-item>
                    <el-dropdown-item command="preview">
                      <el-icon><DataAnalysis /></el-icon>收费预览
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
      <div v-if="selectedFeeTypes.length > 0" class="batch-actions">
        <div class="batch-info">
          已选择 {{ selectedFeeTypes.length }} 项
        </div>
        <div class="batch-buttons">
          <el-button type="success" @click="batchEnable">批量启用</el-button>
          <el-button type="warning" @click="batchDisable">批量禁用</el-button>
          <el-button type="danger" @click="batchDelete">批量删除</el-button>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑费用类型对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :before-close="cancelDialog"
    >
      <el-form
        ref="feeFormRef"
        :model="feeForm"
        :rules="feeRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="费用名称" prop="feeName">
              <el-input
                v-model="feeForm.feeName"
                placeholder="请输入费用名称"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用编码" prop="feeCode">
              <el-input
                v-model="feeForm.feeCode"
                placeholder="请输入费用编码"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计费方式" prop="billingMethod">
              <el-select
                v-model="feeForm.billingMethod"
                placeholder="请选择计费方式"
                style="width: 100%"
                @change="onBillingMethodChange"
              >
                <el-option label="按面积" value="按面积" />
                <el-option label="固定金额" value="固定金额" />
                <el-option label="按用量" value="按用量" />
                <el-option label="按户数" value="按户数" />
                <el-option label="自定义" value="自定义" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费周期" prop="billingCycle">
              <el-select
                v-model="feeForm.billingCycle"
                placeholder="请选择收费周期"
                style="width: 100%"
              >
                <el-option label="每月" value="每月" />
                <el-option label="每季度" value="每季度" />
                <el-option label="每半年" value="每半年" />
                <el-option label="每年" value="每年" />
                <el-option label="一次性" value="一次性" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12" v-if="feeForm.billingMethod !== '自定义'">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number
                v-model="feeForm.unitPrice"
                :precision="2"
                :min="0"
                :max="999999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="feeForm.billingMethod === '按用量'">
            <el-form-item label="计量单位" prop="unit">
              <el-input
                v-model="feeForm.unit"
                placeholder="如：度、吨、立方米"
                maxlength="10"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认金额" prop="defaultAmount">
              <el-input-number
                v-model="feeForm.defaultAmount"
                :precision="2"
                :min="0"
                :max="999999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序号" prop="sortOrder">
              <el-input-number
                v-model="feeForm.sortOrder"
                :min="0"
                :max="999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="feeForm.status"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="费用说明" prop="description">
          <el-input
            v-model="feeForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入费用说明"
            maxlength="500"
          />
        </el-form-item>

        <!-- 高级设置 -->
        <el-divider content-position="left">高级设置</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否必需">
              <el-switch
                v-model="feeForm.isRequired"
                active-text="必需"
                inactive-text="可选"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="自动生成账单">
              <el-switch
                v-model="feeForm.autoGenerate"
                active-text="自动"
                inactive-text="手动"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="feeForm.autoGenerate">
          <el-col :span="12">
            <el-form-item label="生成日期">
              <el-select
                v-model="feeForm.generateDay"
                placeholder="请选择生成日期"
                style="width: 100%"
              >
                <el-option
                  v-for="day in 31"
                  :key="day"
                  :label="`${day}日`"
                  :value="day"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宽限期(天)">
              <el-input-number
                v-model="feeForm.gracePeriod"
                :min="0"
                :max="365"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- 费用类型详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="费用类型详情"
      width="800px"
    >
      <div v-if="currentFeeType" class="fee-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="费用名称">{{ currentFeeType.feeName }}</el-descriptions-item>
          <el-descriptions-item label="费用编码">{{ currentFeeType.feeCode }}</el-descriptions-item>
          <el-descriptions-item label="计费方式">
            <el-tag :type="getBillingMethodTag(currentFeeType.billingMethod)">
              {{ currentFeeType.billingMethod }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收费周期">
            <el-tag size="small">{{ currentFeeType.billingCycle }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="单价">
            <span v-if="currentFeeType.billingMethod === '固定金额'">
              ¥{{ currentFeeType.unitPrice?.toFixed(2) }}
            </span>
            <span v-else-if="currentFeeType.billingMethod === '按面积'">
              ¥{{ currentFeeType.unitPrice?.toFixed(2) }}/㎡
            </span>
            <span v-else-if="currentFeeType.billingMethod === '按用量'">
              ¥{{ currentFeeType.unitPrice?.toFixed(2) }}/{{ currentFeeType.unit }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="默认金额">
            <span v-if="currentFeeType.defaultAmount">
              ¥{{ currentFeeType.defaultAmount.toFixed(2) }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentFeeType.status ? 'success' : 'danger'">
              {{ currentFeeType.status ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排序号">{{ currentFeeType.sortOrder }}</el-descriptions-item>
          <el-descriptions-item label="是否必需">
            <el-tag :type="currentFeeType.isRequired ? 'success' : 'info'">
              {{ currentFeeType.isRequired ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="自动生成">
            <el-tag :type="currentFeeType.autoGenerate ? 'success' : 'warning'">
              {{ currentFeeType.autoGenerate ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentFeeType.autoGenerate" label="生成日期">
            每月{{ currentFeeType.generateDay }}日
          </el-descriptions-item>
          <el-descriptions-item v-if="currentFeeType.autoGenerate" label="宽限期">
            {{ currentFeeType.gracePeriod }}天
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentFeeType.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentFeeType.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="费用说明" :span="2">
            {{ currentFeeType.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-tabs">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="使用统计" name="statistics">
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-statistic title="本月账单数" :value="currentFeeType.currentMonthBills || 0" />
                </el-col>
                <el-col :span="8">
                  <el-statistic title="本月收入" :value="currentFeeType.currentMonthRevenue || 0" prefix="¥" />
                </el-col>
                <el-col :span="8">
                  <el-statistic title="使用次数" :value="currentFeeType.usageCount || 0" />
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="收费预览" name="preview">
              <div class="preview-content">
                <h4>收费标准示例</h4>
                <p v-if="currentFeeType.billingMethod === '按面积'">
                  按房产面积计算：单价 ¥{{ currentFeeType.unitPrice }}/㎡
                </p>
                <p v-else-if="currentFeeType.billingMethod === '固定金额'">
                  固定收费：¥{{ currentFeeType.unitPrice }}/{{ currentFeeType.billingCycle }}
                </p>
                <p v-else-if="currentFeeType.billingMethod === '按用量'">
                  按实际用量计算：单价 ¥{{ currentFeeType.unitPrice }}/{{ currentFeeType.unit }}
                </p>
                <p v-else-if="currentFeeType.billingMethod === '按户数'">
                  按户数计算：每户 ¥{{ currentFeeType.unitPrice }}/{{ currentFeeType.billingCycle }}
                </p>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, Download, Edit, Delete, View,
  CircleCheck, Collection, Refresh as RefreshIcon, Setting,
  DocumentCopy, Switch, DataAnalysis, ArrowDown
} from '@element-plus/icons-vue'
import { feeApi } from '@/api'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const feeTypeList = ref([])
const selectedFeeTypes = ref([])
const total = ref(0)

// 统计数据
const statistics = ref({
  totalTypes: 0,
  activeTypes: 0,
  recurringTypes: 0,
  customTypes: 0
})

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 20,
  feeName: '',
  billingMethod: '',
  status: null,
  billingCycle: ''
})

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const activeTab = ref('statistics')

// 表单数据
const feeForm = reactive({
  id: null,
  feeName: '',
  feeCode: '',
  billingMethod: '',
  billingCycle: '',
  unitPrice: 0,
  unit: '',
  defaultAmount: 0,
  sortOrder: 0,
  status: true,
  description: '',
  isRequired: false,
  autoGenerate: false,
  generateDay: 1,
  gracePeriod: 0
})

// 当前查看的费用类型
const currentFeeType = ref(null)

// 表单引用
const feeFormRef = ref(null)

// 表单验证规则
const feeRules = {
  feeName: [
    { required: true, message: '请输入费用名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  feeCode: [
    { required: true, message: '请输入费用编码', trigger: 'blur' },
    { pattern: /^[A-Z0-9_]+$/, message: '只能包含大写字母、数字和下划线', trigger: 'blur' }
  ],
  billingMethod: [
    { required: true, message: '请选择计费方式', trigger: 'change' }
  ],
  billingCycle: [
    { required: true, message: '请选择收费周期', trigger: 'change' }
  ],
  unitPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价必须大于0', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入计量单位', trigger: 'blur' }
  ]
}

// 计算属性
const getBillingMethodTag = (method) => {
  const map = {
    '按面积': 'primary',
    '固定金额': 'success',
    '按用量': 'warning',
    '按户数': 'info',
    '自定义': ''
  }
  return map[method] || ''
}

// 获取费用类型列表
const getFeeTypeList = async () => {
  try {
    loading.value = true
    const response = await feeApi.getList(queryParams)
    const result = handleResponse(response)
    feeTypeList.value = result.data.records
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
    const response = await feeApi.getStatistics()
    const result = handleResponse(response)
    statistics.value = result.data
  } catch (error) {
    handleError(error)
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  getFeeTypeList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.feeName = ''
  queryParams.billingMethod = ''
  queryParams.status = null
  queryParams.billingCycle = ''
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedFeeTypes.value = selection
}

// 计费方式变化
const onBillingMethodChange = (method) => {
  // 根据计费方式重置相关字段
  if (method === '固定金额') {
    feeForm.defaultAmount = feeForm.unitPrice
  } else {
    feeForm.defaultAmount = 0
  }
}

// 显示新增对话框
const showAddDialog = () => {
  dialogTitle.value = '新增费用类型'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑费用类型'
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(feeForm, row)
}

// 重置表单
const resetForm = () => {
  Object.assign(feeForm, {
    id: null,
    feeName: '',
    feeCode: '',
    billingMethod: '',
    billingCycle: '',
    unitPrice: 0,
    unit: '',
    defaultAmount: 0,
    sortOrder: 0,
    status: true,
    description: '',
    isRequired: false,
    autoGenerate: false,
    generateDay: 1,
    gracePeriod: 0
  })
  feeFormRef.value?.resetFields()
}

// 取消对话框
const cancelDialog = () => {
  dialogVisible.value = false
  resetForm()
}

// 提交表单
const submitForm = async () => {
  try {
    await feeFormRef.value.validate()
    submitting.value = true

    const response = isEdit.value
      ? await feeApi.update(feeForm)
      : await feeApi.create(feeForm)

    handleResponse(response)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    getFeeTypeList()
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
    const response = await feeApi.getDetail(row.id)
    const result = handleResponse(response)
    currentFeeType.value = result.data
    detailDialogVisible.value = true
    activeTab.value = 'statistics'
  } catch (error) {
    handleError(error)
  }
}

// 编辑
const handleEdit = (row) => {
  showEditDialog(row)
}

// 命令处理
const handleCommand = (command, row) => {
  switch (command) {
    case 'copy':
      copyFeeType(row)
      break
    case 'enable':
      toggleStatus(row, true)
      break
    case 'disable':
      toggleStatus(row, false)
      break
    case 'preview':
      showPreview(row)
      break
    case 'delete':
      deleteFeeType(row)
      break
  }
}

// 复制费用类型
const copyFeeType = (row) => {
  showAddDialog()
  Object.assign(feeForm, {
    ...row,
    id: null,
    feeName: `${row.feeName} (复制)`,
    feeCode: `${row.feeCode}_COPY`
  })
}

// 切换状态
const toggleStatus = async (row, status) => {
  try {
    const response = await feeApi.updateStatus({
      id: row.id,
      status: status
    })
    handleResponse(response)
    ElMessage.success(`${status ? '启用' : '禁用'}成功`)
    getFeeTypeList()
    getStatistics()
  } catch (error) {
    handleError(error)
  }
}

// 显示预览
const showPreview = (row) => {
  ElMessage.info('收费预览功能开发中')
}

// 删除费用类型
const deleteFeeType = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除费用类型"${row.feeName}"？此操作不可恢复！`,
      '删除确认',
      { type: 'warning' }
    )

    const response = await feeApi.delete(row.id)
    handleResponse(response)
    ElMessage.success('删除成功')
    getFeeTypeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 批量操作
const batchEnable = async () => {
  try {
    const disabledTypes = selectedFeeTypes.value.filter(type => !type.status)
    if (disabledTypes.length === 0) {
      ElMessage.warning('请选择已禁用的费用类型')
      return
    }

    await ElMessageBox.confirm(
      `确认启用选中的 ${disabledTypes.length} 个费用类型？`,
      '批量启用确认',
      { type: 'warning' }
    )

    const ids = disabledTypes.map(type => type.id)
    const response = await feeApi.batchUpdateStatus({ ids, status: true })
    handleResponse(response)
    ElMessage.success('批量启用成功')
    getFeeTypeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchDisable = async () => {
  try {
    const enabledTypes = selectedFeeTypes.value.filter(type => type.status)
    if (enabledTypes.length === 0) {
      ElMessage.warning('请选择已启用的费用类型')
      return
    }

    await ElMessageBox.confirm(
      `确认禁用选中的 ${enabledTypes.length} 个费用类型？`,
      '批量禁用确认',
      { type: 'warning' }
    )

    const ids = enabledTypes.map(type => type.id)
    const response = await feeApi.batchUpdateStatus({ ids, status: false })
    handleResponse(response)
    ElMessage.success('批量禁用成功')
    getFeeTypeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchDelete = async () => {
  try {
    if (selectedFeeTypes.value.length === 0) return

    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedFeeTypes.value.length} 个费用类型？此操作不可恢复！`,
      '批量删除确认',
      { type: 'warning' }
    )

    const ids = selectedFeeTypes.value.map(type => type.id)
    const response = await feeApi.batchDelete(ids)
    handleResponse(response)
    ElMessage.success('批量删除成功')
    getFeeTypeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 导出配置
const exportFeeTypes = async () => {
  try {
    const response = await feeApi.export(queryParams)
    // 处理文件下载
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `费用类型配置_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    handleError(error)
  }
}

// 初始化
onMounted(() => {
  getFeeTypeList()
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

    &.total-types {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.active-types {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.recurring-types {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.custom-types {
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
  // 表格样式已在全局定义
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

// 费用详情
.fee-detail {
  .detail-tabs {
    margin-top: 20px;
  }

  .preview-content {
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 6px;

    h4 {
      margin-top: 0;
      color: #303133;
    }

    p {
      margin: 10px 0;
      color: #606266;
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