<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">维修工单</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>服务管理</el-breadcrumb-item>
        <el-breadcrumb-item>维修工单</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
        v-permission="'repair:add'"
      >
        <el-icon><Plus /></el-icon>
        新增工单
      </el-button>
      <el-button
        type="success"
        @click="handleBatchAssign"
        :disabled="selectedRows.length === 0"
        v-permission="'repair:assign'"
      >
        <el-icon><User /></el-icon>
        批量派单
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出Excel
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="queryParams"
        inline
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :span="5">
            <el-form-item label="工单编号" prop="orderNo">
              <el-input
                v-model="queryParams.orderNo"
                placeholder="请输入工单编号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="工单标题" prop="title">
              <el-input
                v-model="queryParams.title"
                placeholder="请输入工单标题"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="工单状态" prop="status">
              <el-select
                v-model="queryParams.status"
                placeholder="请选择状态"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="优先级" prop="priority">
              <el-select
                v-model="queryParams.priority"
                placeholder="请选择优先级"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in priorityOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="维修类型" prop="repairType">
              <el-select
                v-model="queryParams.repairType"
                placeholder="请选择维修类型"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in repairTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="报修人" prop="reporterName">
              <el-input
                v-model="queryParams.reporterName"
                placeholder="请输入报修人姓名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="联系电话" prop="reporterPhone">
              <el-input
                v-model="queryParams.reporterPhone"
                placeholder="请输入联系电话"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="创建时间" prop="dateRange">
              <el-date-picker
                v-model="queryParams.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-header">
        <div class="header-left">
          <span class="table-title">维修工单列表</span>
          <el-tag type="info" size="small">共 {{ total }} 条记录</el-tag>
          <div class="status-summary">
            <el-tag type="warning" size="small">待处理: {{ statusStats.pending || 0 }}</el-tag>
            <el-tag type="primary" size="small">进行中: {{ statusStats.processing || 0 }}</el-tag>
            <el-tag type="info" size="small">待验收: {{ statusStats.pendingAccept || 0 }}</el-tag>
            <el-tag type="success" size="small">已完成: {{ statusStats.completed || 0 }}</el-tag>
          </div>
        </div>
      </div>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="repairList"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />

        <el-table-column
          prop="orderNo"
          label="工单编号"
          width="150"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary" size="small">{{ row.orderNo }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="title"
          label="工单标题"
          min-width="200"
          show-overflow-tooltip
        />

        <el-table-column
          prop="priority"
          label="优先级"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getPriorityTag(row.priority)" size="small">
              {{ getPriorityName(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="status"
          label="状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="repairType"
          label="维修类型"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ getRepairTypeName(row.repairType) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="reporterName"
          label="报修人"
          width="100"
          align="center"
        />

        <el-table-column
          prop="reporterPhone"
          label="联系电话"
          width="130"
          align="center"
        />

        <el-table-column
          prop="location"
          label="维修地点"
          width="200"
          show-overflow-tooltip
        />

        <el-table-column
          prop="assignedTo"
          label="维修人员"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <span v-if="row.assignedTo">{{ row.assignedTo }}</span>
            <el-tag type="warning" size="small" v-else>未分配</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleView(row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleAssign(row)"
              v-if="row.status === 0"
              v-permission="'repair:assign'"
            >
              <el-icon><User /></el-icon>
              派单
            </el-button>
            <el-button
              link
              type="success"
              @click="handleAccept(row)"
              v-if="row.status === 1 && userStore.userType === 4"
            >
              <el-icon><Check /></el-icon>
              接单
            </el-button>
            <el-button
              link
              type="info"
              @click="handleProcess(row)"
              v-if="row.status === 2 && userStore.userType === 4"
            >
              <el-icon><Setting /></el-icon>
              处理
            </el-button>
            <el-button
              link
              type="success"
              @click="handleComplete(row)"
              v-if="row.status === 3 && userStore.userType === 4"
            >
              <el-icon><CircleCheck /></el-icon>
              完成
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleAcceptCheck(row)"
              v-if="row.status === 4 && userStore.userType === 2"
            >
              <el-icon><Select /></el-icon>
              验收
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleCancel(row)"
              v-if="row.status < 5"
            >
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        class="repair-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入工单标题"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option
                  v-for="item in priorityOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修类型" prop="repairType">
              <el-select v-model="form.repairType" placeholder="请选择维修类型" style="width: 100%">
                <el-option
                  v-for="item in repairTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计费用" prop="estimatedCost">
              <el-input-number
                v-model="form.estimatedCost"
                :min="0"
                :precision="2"
                placeholder="请输入预计费用"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="报修信息">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="报修人" prop="reporterName">
                <el-input
                  v-model="form.reporterName"
                  placeholder="请输入报修人姓名"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系电话" prop="reporterPhone">
                <el-input
                  v-model="form.reporterPhone"
                  placeholder="请输入联系电话"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="业主类型" prop="reporterType">
                <el-select v-model="form.reporterType" placeholder="请选择业主类型" style="width: 100%">
                  <el-option label="产权人" :value="1" />
                  <el-option label="租户" :value="2" />
                  <el-option label="家庭成员" :value="3" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="楼栋" prop="buildingId">
              <el-select
                v-model="form.buildingId"
                placeholder="请选择楼栋"
                @change="handleBuildingChange"
                style="width: 100%"
              >
                <el-option
                  v-for="item in buildingOptions"
                  :key="item.buildingId"
                  :label="item.buildingName"
                  :value="item.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单元" prop="unitId">
              <el-select
                v-model="form.unitId"
                placeholder="请选择单元"
                style="width: 100%"
              >
                <el-option
                  v-for="item in unitOptions"
                  :key="item.unitId"
                  :label="item.unitName"
                  :value="item.unitId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房产" prop="houseId">
              <el-select
                v-model="form.houseId"
                placeholder="请选择房产"
                style="width: 100%"
              >
                <el-option
                  v-for="item in houseOptions"
                  :key="item.houseId"
                  :label="item.houseCode"
                  :value="item.houseId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="location">
          <el-input
            v-model="form.location"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item label="问题描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述维修问题"
          />
        </el-form-item>

        <el-form-item label="附件上传" prop="attachments">
          <el-upload
            v-model:file-list="form.attachments"
            action="#"
            :auto-upload="false"
            multiple
            :limit="5"
            accept="image/*,.pdf,.doc,.docx"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持jpg/png/pdf/doc/docx格式，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="备注信息" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="工单详情"
      width="1000px"
      @close="detailDialogVisible = false"
    >
      <div class="order-detail" v-loading="detailLoading">
        <div class="order-header">
          <div class="order-info">
            <h3>{{ currentOrder.title }}</h3>
            <div class="order-meta">
              <el-tag type="primary">{{ currentOrder.orderNo }}</el-tag>
              <el-tag :type="getPriorityTag(currentOrder.priority)">{{ getPriorityName(currentOrder.priority) }}</el-tag>
              <el-tag :type="getStatusTag(currentOrder.status)">{{ getStatusName(currentOrder.status) }}</el-tag>
              <span class="create-time">创建时间: {{ formatDateTime(currentOrder.createTime) }}</span>
            </div>
          </div>
        </div>

        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="报修人">{{ currentOrder.reporterName }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ currentOrder.reporterPhone }}</el-descriptions-item>
              <el-descriptions-item label="业主类型">{{ getReporterTypeName(currentOrder.reporterType) }}</el-descriptions-item>
              <el-descriptions-item label="维修类型">{{ getRepairTypeName(currentOrder.repairType) }}</el-descriptions-item>
              <el-descriptions-item label="维修地点" span="2">{{ currentOrder.location }}</el-descriptions-item>
              <el-descriptions-item label="预计费用">¥{{ currentOrder.estimatedCost || 0 }}</el-descriptions-item>
              <el-descriptions-item label="实际费用">¥{{ currentOrder.actualCost || 0 }}</el-descriptions-item>
              <el-descriptions-item label="维修人员">{{ currentOrder.assignedTo || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="处理时间">{{ formatDateTime(currentOrder.processTime) || '-' }}</el-descriptions-item>
              <el-descriptions-item label="完成时间">{{ formatDateTime(currentOrder.completeTime) || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <el-tab-pane label="问题描述" name="description">
            <div class="description-content">
              <h4>问题描述</h4>
              <p>{{ currentOrder.description }}</p>
            </div>
          </el-tab-pane>

          <el-tab-pane label="处理记录" name="process">
            <el-timeline>
              <el-timeline-item
                v-for="(record, index) in processRecords"
                :key="index"
                :timestamp="formatDateTime(record.createTime)"
                :type="getTimelineType(record.action)"
              >
                <div class="process-record">
                  <strong>{{ record.operatorName }}</strong>
                  <span class="action-text">{{ getProcessActionText(record.action) }}</span>
                  <p v-if="record.remark">{{ record.remark }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
          </el-tab-pane>

          <el-tab-pane label="附件" name="attachments">
            <div class="attachment-list">
              <div
                v-for="(file, index) in currentOrder.attachments || []"
                :key="index"
                class="attachment-item"
              >
                <el-link :href="file.url" target="_blank">
                  <el-icon><Document /></el-icon>
                  {{ file.name }}
                </el-link>
                <span class="file-size">({{ formatFileSize(file.size) }})</span>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            type="warning"
            @click="handlePrintOrder"
            v-permission="'repair:print'"
          >
            <el-icon><Printer /></el-icon>
            打印
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Tools,
  Search,
  Refresh,
  Plus,
  Download,
  Delete,
  View,
  User,
  Check,
  Setting,
  CircleCheck,
  Select,
  Close,
  Document,
  Printer
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getRepairOrderList,
  getRepairOrderDetail,
  addRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
  batchDeleteRepairOrders,
  assignRepairOrder,
  acceptRepairOrder,
  processRepairOrder,
  completeRepairOrder,
  acceptCheckRepairOrder,
  cancelRepairOrder,
  batchAssignRepairOrder
} from '@/api/repair'
import { getBuildingList } from '@/api/building'
import { getUnitsByBuilding } from '@/api/unit'
import { getHousesByUnit } from '@/api/house'
import { handleResponse, handleError } from '@/utils/response'

const userStore = useUserStore()

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const repairList = ref([])
const total = ref(0)
const currentOrder = ref({})
const processRecords = ref([])
const statusStats = ref({})

// 对话框相关
const activeTab = ref('basic')
const buildingOptions = ref([])
const unitOptions = ref([])
const houseOptions = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  title: '',
  status: '',
  priority: '',
  repairType: '',
  reporterName: '',
  reporterPhone: '',
  dateRange: []
})

// 表单数据
const form = reactive({
  orderId: null,
  title: '',
  priority: 1,
  repairType: '',
  estimatedCost: 0,
  reporterName: '',
  reporterPhone: '',
  reporterType: 1,
  buildingId: '',
  unitId: '',
  houseId: '',
  location: '',
  description: '',
  attachments: [],
  remark: ''
})

// 选项数据
const statusOptions = [
  { label: '待处理', value: 0 },
  { label: '已派单', value: 1 },
  { label: '进行中', value: 2 },
  { label: '待验收', value: 3 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: 5 }
]

const priorityOptions = [
  { label: '紧急', value: 1 },
  { label: '高', value: 2 },
  { label: '中', value: 3 },
  { label: '低', value: 4 }
]

const repairTypeOptions = [
  { label: '水电维修', value: 'water_electric' },
  { label: '门窗维修', value: 'door_window' },
  { label: '电梯维修', value: 'elevator' },
  { label: '公共设施', value: 'public_facility' },
  { label: '家电维修', value: 'appliance' },
  { label: '其他', value: 'other' }
]

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入工单标题', trigger: 'blur' },
    { min: 2, max: 100, message: '工单标题长度在2到100个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  repairType: [
    { required: true, message: '请选择维修类型', trigger: 'change' }
  ],
  reporterName: [
    { required: true, message: '请输入报修人姓名', trigger: 'blur' }
  ],
  reporterPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  buildingId: [
    { required: true, message: '请选择楼栋', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 5, message: '问题描述至少5个字符', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑工单' : '新增工单')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  while (size >= 1024 && index < units.length - 1) {
    size /= 1024
    index++
  }
  return `${size.toFixed(1)} ${units[index]}`
}

// 获取状态名称
const getStatusName = (status) => {
  const option = statusOptions.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 待处理
    1: 'primary', // 已派单
    2: 'info',    // 进行中
    3: '',        // 待验收
    4: 'success', // 已完成
    5: 'danger'   // 已取消
  }
  return tagMap[status] || 'info'
}

// 获取优先级名称
const getPriorityName = (priority) => {
  const option = priorityOptions.find(item => item.value === priority)
  return option ? option.label : '未知'
}

// 获取优先级标签
const getPriorityTag = (priority) => {
  const tagMap = {
    1: 'danger', // 紧急
    2: 'warning', // 高
    3: 'info',    // 中
    4: 'success'  // 低
  }
  return tagMap[priority] || 'info'
}

// 获取维修类型名称
const getRepairTypeName = (type) => {
  const option = repairTypeOptions.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 获取报修人类型名称
const getReporterTypeName = (type) => {
  const typeMap = {
    1: '产权人',
    2: '租户',
    3: '家庭成员'
  }
  return typeMap[type] || '未知'
}

// 获取时间轴类型
const getTimelineType = (action) => {
  const typeMap = {
    'create': 'primary',
    'assign': 'warning',
    'accept': 'info',
    'process': 'info',
    'complete': 'success',
    'accept_check': 'success',
    'cancel': 'danger'
  }
  return typeMap[action] || 'info'
}

// 获取处理动作文本
const getProcessActionText = (action) => {
  const actionMap = {
    'create': '创建工单',
    'assign': '派单给维修人员',
    'accept': '接受工单',
    'process': '开始处理',
    'complete': '完成维修',
    'accept_check': '验收通过',
    'cancel': '取消工单'
  }
  return actionMap[action] || action
}

// 获取楼栋列表
const getBuildingOptions = async () => {
  try {
    const response = await getBuildingList({ pageNum: 1, pageSize: 1000 })
    const data = handleResponse(response)
    buildingOptions.value = data.list || data.records || []
  } catch (error) {
    handleError(error, '获取楼栋列表失败')
    buildingOptions.value = []
  }
}

// 获取单元列表
const getUnitOptions = async (buildingId) => {
  if (!buildingId) {
    unitOptions.value = []
    houseOptions.value = []
    return
  }

  try {
    const response = await getUnitsByBuilding(buildingId)
    unitOptions.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取单元列表失败')
    unitOptions.value = []
  }
}

// 获取房产列表
const getHouseOptions = async (unitId) => {
  if (!unitId) {
    houseOptions.value = []
    return
  }

  try {
    const response = await getHousesByUnit(unitId)
    houseOptions.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取房产列表失败')
    houseOptions.value = []
  }
}

// 楼栋变化处理
const handleBuildingChange = (buildingId) => {
  form.unitId = ''
  form.houseId = ''
  if (buildingId) {
    getUnitOptions(buildingId)
  } else {
    unitOptions.value = []
    houseOptions.value = []
  }
}

// 监听单元变化
watch(() => form.unitId, (unitId) => {
  form.houseId = ''
  if (unitId) {
    getHouseOptions(unitId)
  }
})

// 获取工单列表
const getRepairOrderListData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (queryParams.dateRange && queryParams.dateRange.length === 2) {
      params.startDate = queryParams.dateRange[0]
      params.endDate = queryParams.dateRange[1]
    }
    delete params.dateRange

    const response = await getRepairOrderList(params)
    const data = handleResponse(response)
    repairList.value = data.list || data.records || []
    total.value = data.total || 0

    // 统计各状态数量
    statusStats.value = repairList.value.reduce((stats, order) => {
      if (order.status === 0) stats.pending++
      if (order.status === 2) stats.processing++
      if (order.status === 3) stats.pendingAccept++
      if (order.status === 4) stats.completed++
      return stats
    }, { pending: 0, processing: 0, pendingAccept: 0, completed: 0 })
  } catch (error) {
    handleError(error, '获取工单列表失败')
    repairList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取工单详情
const getRepairOrderDetailData = async (orderId) => {
  detailLoading.value = true
  try {
    const response = await getRepairOrderDetail(orderId)
    const data = handleResponse(response)
    currentOrder.value = data
    processRecords.value = data.processRecords || []
  } catch (error) {
    handleError(error, '获取工单详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getRepairOrderListData()
}

// 重置搜索
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.orderNo = ''
  queryParams.title = ''
  queryParams.status = ''
  queryParams.priority = ''
  queryParams.repairType = ''
  queryParams.reporterName = ''
  queryParams.reporterPhone = ''
  queryParams.dateRange = []
  getRepairOrderListData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getRepairOrderListData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getRepairOrderListData()
}

// 排序变化
const handleSortChange = ({ column, prop, order }) => {
  console.log('排序变化:', { column, prop, order })
  getRepairOrderListData()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增工单
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑工单
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    ...row,
    attachments: row.attachments || []
  })
  dialogVisible.value = true
}

// 查看工单详情
const handleView = async (row) => {
  await getRepairOrderDetailData(row.orderId)
  detailDialogVisible.value = true
}

// 派单
const handleAssign = async (row) => {
  try {
    const { value: technicianId } = await ElMessageBox.prompt(
      '请选择维修人员',
      '派单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\d+/,
        inputErrorMessage: '请输入维修人员ID'
      }
    )

    await assignRepairOrder({ orderId: row.orderId, technicianId: Number(technicianId) })
    ElMessage.success('派单成功')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '派单失败')
    }
  }
}

// 批量派单
const handleBatchAssign = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要派单的工单')
    return
  }

  try {
    const { value: technicianId } = await ElMessageBox.prompt(
      '请选择维修人员',
      '批量派单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\d+/,
        inputErrorMessage: '请输入维修人员ID'
      }
    )

    const orderIds = selectedRows.value.map(row => row.orderId)
    await batchAssignRepairOrder({ orderIds, technicianId: Number(technicianId) })
    ElMessage.success('批量派单成功')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '批量派单失败')
    }
  }
}

// 接单
const handleAccept = async (row) => {
  try {
    await ElMessageBox.confirm('确定要接受这个工单吗？', '确认接单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await acceptRepairOrder({ orderId: row.orderId })
    ElMessage.success('接单成功')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '接单失败')
    }
  }
}

// 处理工单
const handleProcess = async (row) => {
  try {
    const { value: remark } = await ElMessageBox.prompt(
      '请输入处理备注',
      '处理工单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea'
      }
    )

    await processRepairOrder({ orderId: row.orderId, remark })
    ElMessage.success('处理成功')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '处理失败')
    }
  }
}

// 完成工单
const handleComplete = async (row) => {
  try {
    const { value: actualCost } = await ElMessageBox.prompt(
      '请输入实际费用',
      '完成工单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+(\.\d{1,2})?$/,
        inputErrorMessage: '请输入正确的费用金额'
      }
    )

    await completeRepairOrder({ orderId: row.orderId, actualCost: Number(actualCost) })
    ElMessage.success('工单完成')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '完成工单失败')
    }
  }
}

// 验收
const handleAcceptCheck = async (row) => {
  try {
    const { value: remark } = await ElMessageBox.prompt(
      '请输入验收备注',
      '验收工单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea'
      }
    )

    await acceptCheckRepairOrder({ orderId: row.orderId, remark })
    ElMessage.success('验收通过')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '验收失败')
    }
  }
}

// 取消工单
const handleCancel = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消原因',
      '取消工单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入取消原因'
          }
          return true
        }
      }
    )

    await cancelRepairOrder({ orderId: row.orderId, reason })
    ElMessage.success('工单已取消')
    getRepairOrderListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '取消工单失败')
    }
  }
}

// 导出Excel
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 打印工单
const handlePrintOrder = () => {
  ElMessage.info('打印功能开发中...')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateRepairOrder(form)
      ElMessage.success('编辑成功')
    } else {
      await addRepairOrder(form)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    getRepairListData()
  } catch (error) {
    handleError(error, isEdit.value ? '编辑失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    orderId: null,
    title: '',
    priority: 1,
    repairType: '',
    estimatedCost: 0,
    reporterName: '',
    reporterPhone: '',
    reporterType: 1,
    buildingId: '',
    unitId: '',
    houseId: '',
    location: '',
    description: '',
    attachments: [],
    remark: ''
  })
  unitOptions.value = []
  houseOptions.value = []
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 组件挂载
onMounted(() => {
  getBuildingOptions()
  getRepairOrderListData()
})
</script>

<style lang="scss" scoped>
.log-container {
  .page-header {
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .page-title {
      margin: 0 0 12px 0;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    :deep(.el-breadcrumb) {
      font-size: 14px;
    }
  }

  .action-section {
    margin-bottom: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    display: flex;
    gap: 10px;
  }

  .search-section {
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .search-form {
      .el-form-item {
        margin-bottom: 16px;
      }
    }
  }

  .table-section {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;

    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .table-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .status-summary {
          display: flex;
          gap: 8px;
        }
      }
    }

    .data-table {
      margin-bottom: 16px;

      .time-cell {
        font-size: 13px;
        color: #909399;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding: 16px 0;
    }
  }

  .repair-form {
    .el-form-item {
      margin-bottom: 18px;
    }
  }

  .order-detail {
    .order-header {
      margin-bottom: 20px;

      .order-info {
        h3 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 20px;
        }

        .order-meta {
          display: flex;
          align-items: center;
          gap: 12px;
          flex-wrap: wrap;

          .create-time {
            color: #666;
            font-size: 14px;
          }
        }
      }
    }

    .description-content {
      h4 {
        margin-bottom: 8px;
        color: #303133;
      }

      p {
        line-height: 1.6;
        color: #606266;
      }
    }

    .process-record {
      .action-text {
        margin: 0 8px;
        color: #409eff;
      }

      p {
        margin: 4px 0 0 0;
        color: #666;
        font-size: 14px;
      }
    }

    .attachment-list {
      .attachment-item {
        display: flex;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .el-link {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .file-size {
          color: #999;
          font-size: 12px;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #fafafa;
      font-weight: 600;
      color: #303133;
    }
  }

  .el-table__body {
    tr:hover > td {
      background-color: #f5f7fa;
    }
  }
}

// 卡片样式优化
:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .el-card__header {
    border-bottom: 1px solid #ebeef5;
    padding: 16px 20px;
  }

  .el-card__body {
    padding: 20px;
  }
}

// 按钮样式优化
:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
}

// 输入框样式优化
:deep(.el-input__wrapper) {
  border-radius: 6px;
}

// 标签样式优化
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

// 时间轴样式优化
:deep(.el-timeline) {
  .el-timeline-item__timestamp {
    font-size: 12px;
    color: #909399;
  }
}

// 选项卡样式优化
:deep(.el-tabs) {
  .el-tabs__header {
    margin-bottom: 16px;
  }

  .el-tabs__content {
    padding-top: 0;
  }
}

// 上传组件样式优化
:deep(.el-upload) {
  .el-upload-dragger {
    border-radius: 8px;
  }
}

// 描述列表样式优化
:deep(.el-descriptions) {
  .el-descriptions__label {
    font-weight: 600;
    color: #606266;
  }
}
</style>