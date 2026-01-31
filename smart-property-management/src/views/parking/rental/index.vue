<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">车位租赁管理</h2>
      <el-tabs
        v-model="activeTab"
        class="demo-tabs"
        @tab-change="handleTabChange"
      >
        <el-tab-pane label="租赁申请" name="applications">
          <!-- 搜索区域 -->
          <div class="search-section">
            <el-form :model="searchForm" inline>
              <el-form-item label="申请人">
                <el-input
                  v-model="searchForm.applicant"
                  placeholder="请输入申请人姓名"
                  clearable
                  style="width: 180px"
                />
              </el-form-item>
              <el-form-item label="车位编号">
                <el-input
                  v-model="searchForm.spaceNo"
                  placeholder="请输入车位编号"
                  clearable
                  style="width: 180px"
                />
              </el-form-item>
              <el-form-item label="车牌号">
                <el-input
                  v-model="searchForm.vehicleNumber"
                  placeholder="请输入车牌号"
                  clearable
                  style="width: 150px"
                />
              </el-form-item>
              <el-form-item label="申请状态">
                <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                  style="width: 120px"
                >
                  <el-option label="待审核" :value="1" />
                  <el-option label="已通过" :value="2" />
                  <el-option label="已驳回" :value="3" />
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
                <el-button type="success" @click="handleAdd">
                  <el-icon><Plus /></el-icon>
                  新增申请
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button
              type="success"
              @click="handleBatchApprove"
              :disabled="selectedApplications.length === 0"
            >
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button
              type="danger"
              @click="handleBatchReject"
              :disabled="selectedApplications.length === 0"
            >
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
          </div>

          <!-- 申请表格 -->
          <div class="table-section">
            <el-table
              v-loading="loading"
              :data="applicationList"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="spaceNo" label="车位编号" width="100" />
              <el-table-column prop="ownerName" label="申请人" width="100" />
              <el-table-column prop="contactPhone" label="联系电话" width="120" />
              <el-table-column prop="vehicleNumber" label="车牌号" width="120" />
              <el-table-column prop="vehicleBrand" label="车辆品牌" width="120" />
              <el-table-column label="租赁日期" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.rentalStartDate) }} 至<br/>
                  {{ formatDate(row.rentalEndDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="rentalMonths" label="租期(月)" width="90" />
              <el-table-column prop="monthlyRent" label="月租金" width="100">
                <template #default="{ row }">
                  ¥{{ row.monthlyRent }}
                </template>
              </el-table-column>
              <el-table-column prop="applicationStatus" label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="getStatusColor(row.applicationStatus)">
                    {{ getStatusName(row.applicationStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="审核信息" width="150">
                <template #default="{ row }">
                  <div v-if="row.applicationStatus !== 1" class="review-info">
                    <div>{{ row.reviewUserName || '-' }}</div>
                    <div class="review-time">{{ formatDateTime(row.reviewTime) }}</div>
                  </div>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="申请时间" width="160">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button
                    v-if="row.applicationStatus === 1"
                    link
                    type="success"
                    @click="handleApprove(row)"
                  >
                    通过
                  </el-button>
                  <el-button
                    v-if="row.applicationStatus === 1"
                    link
                    type="danger"
                    @click="handleReject(row)"
                  >
                    拒绝
                  </el-button>
                  <el-button
                    link
                    type="primary"
                    @click="handleView(row)"
                  >
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-wrapper">
              <el-pagination
                :current-page="currentPage"
                :page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 租赁记录 -->
        <el-tab-pane label="租赁记录" name="records">
          <!-- 搜索区域 -->
          <div class="search-section">
            <el-form :model="recordSearchForm" inline>
              <el-form-item label="承租人">
                <el-input
                  v-model="recordSearchForm.ownerName"
                  placeholder="请输入承租人姓名"
                  clearable
                  style="width: 180px"
                />
              </el-form-item>
              <el-form-item label="车位编号">
                <el-input
                  v-model="recordSearchForm.spaceNo"
                  placeholder="请输入车位编号"
                  clearable
                  style="width: 180px"
                />
              </el-form-item>
              <el-form-item label="车牌号">
                <el-input
                  v-model="recordSearchForm.vehicleNumber"
                  placeholder="请输入车牌号"
                  clearable
                  style="width: 150px"
                />
              </el-form-item>
              <el-form-item label="合同状态">
                <el-select
                  v-model="recordSearchForm.contractStatus"
                  placeholder="请选择状态"
                  clearable
                  style="width: 120px"
                >
                  <el-option label="待付款" :value="1" />
                  <el-option label="进行中" :value="2" />
                  <el-option label="已到期" :value="3" />
                  <el-option label="已终止" :value="4" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleRecordSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="handleRecordReset">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 记录表格 -->
          <div class="table-section">
            <el-table
              v-loading="recordLoading"
              :data="recordList"
            >
              <el-table-column prop="contractNo" label="合同编号" width="150" />
              <el-table-column prop="spaceNo" label="车位编号" width="100" />
              <el-table-column prop="ownerName" label="承租人" width="100" />
              <el-table-column prop="vehicleNumber" label="车牌号" width="120" />
              <el-table-column prop="vehicleBrand" label="车辆品牌" width="120" />
              <el-table-column label="租赁期限" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.startDate) }} 至<br/>
                  {{ formatDate(row.endDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="rentalMonths" label="租期" width="80">
                <template #default="{ row }">
                  {{ row.rentalMonths }}个月
                </template>
              </el-table-column>
              <el-table-column prop="monthlyRent" label="月租金" width="100">
                <template #default="{ row }">
                  ¥{{ row.monthlyRent }}
                </template>
              </el-table-column>
              <el-table-column prop="totalAmount" label="总金额" width="110">
                <template #default="{ row }">
                  ¥{{ row.totalAmount }}
                </template>
              </el-table-column>
              <el-table-column prop="paidAmount" label="已付金额" width="110">
                <template #default="{ row }">
                  ¥{{ row.paidAmount || 0 }}
                </template>
              </el-table-column>
              <el-table-column prop="contractStatus" label="合同状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getContractStatusColor(row.contractStatus)">
                    {{ getContractStatusName(row.contractStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button
                    link
                    type="primary"
                    @click="handleViewContract(row)"
                  >
                    查看
                  </el-button>
                  <el-button
                    v-if="row.contractStatus === 2"
                    link
                    type="warning"
                    @click="handleTerminate(row)"
                  >
                    终止
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-wrapper">
              <el-pagination
                :current-page="recordCurrentPage"
                :page-size="recordPageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="recordTotal"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleRecordSizeChange"
                @current-change="handleRecordCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 添加或修改租赁申请对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="业主姓名" prop="ownerName">
          <el-input v-model="form.ownerName" placeholder="请输入业主姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="车辆号码" prop="vehicleNumber">
          <el-input v-model="form.vehicleNumber" placeholder="请输入车辆号码" />
        </el-form-item>

        <el-form-item label="车辆品牌" prop="vehicleBrand">
          <el-input v-model="form.vehicleBrand" placeholder="请输入车辆品牌" />
        </el-form-item>

        <el-form-item label="车辆颜色" prop="vehicleColor">
          <el-input v-model="form.vehicleColor" placeholder="请输入车辆颜色" />
        </el-form-item>

        <el-form-item label="选择车位" prop="parkingSpaceId">
          <el-select
            v-model="form.parkingSpaceId"
            placeholder="请选择车位"
            filterable
            style="width: 100%"
            @change="handleSpaceChange">
            <el-option
              v-for="space in availableSpaces"
              :key="space.id"
              :label="`${space.spaceNo} - ${space.location}`"
              :value="space.id">
              <span>{{ space.spaceNo }}</span>
              <span style="float: right; color: #8492a6;">
                {{ space.location }} - ¥{{ space.monthlyRent }}/月
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="租赁开始日期" prop="rentalStartDate">
          <el-date-picker
            v-model="form.rentalStartDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="租赁结束日期" prop="rentalEndDate">
          <el-date-picker
            v-model="form.rentalEndDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="租赁月数" prop="rentalMonths">
          <el-input-number
            v-model="form.rentalMonths"
            :min="1"
            :max="60"
            placeholder="请输入租赁月数"
            style="width: 100%"
            @change="calculateTotalAmount"
          />
        </el-form-item>

        <!-- 费用明细 -->
        <el-divider content-position="left">费用明细</el-divider>
        <el-form-item label="月租金">
          <el-input v-model="form.monthlyRent" disabled>
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="租赁月数">
          <el-input v-model="form.rentalMonths" disabled>
            <template #suffix>个月</template>
          </el-input>
        </el-form-item>
        <el-form-item label="小计">
          <el-input :value="subtotal" disabled>
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="折扣">
          <el-input-number
            v-model="form.discount"
            :min="0"
            :precision="2"
            placeholder="折扣金额"
            style="width: 100%"
            @change="calculateTotalAmount"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input :value="totalAmount" disabled>
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>

        <el-form-item label="申请原因" prop="applicationReason">
          <el-input
            v-model="form.applicationReason"
            type="textarea"
            placeholder="请输入申请原因"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog title="租赁申请审核" v-model="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="100px">
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="reviewForm.status">
            <el-radio :value="2">通过</el-radio>
            <el-radio :value="3">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="reviewRemark">
          <el-input
            v-model="reviewForm.reviewRemark"
            type="textarea"
            placeholder="请输入审核备注"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReview">确 定</el-button>
          <el-button @click="reviewOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="申请详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ currentApplication.id }}</el-descriptions-item>
        <el-descriptions-item label="车位编号">{{ currentApplication.spaceNo }}</el-descriptions-item>
        <el-descriptions-item label="车位位置">{{ currentApplication.location }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentApplication.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="业主姓名">{{ currentApplication.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="车辆号码">{{ currentApplication.vehicleNumber }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentApplication.vehicleBrand }}</el-descriptions-item>
        <el-descriptions-item label="车辆颜色">{{ currentApplication.vehicleColor }}</el-descriptions-item>
        <el-descriptions-item label="租赁开始日期">{{ parseTime(currentApplication.rentalStartDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="租赁结束日期">{{ parseTime(currentApplication.rentalEndDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="租赁月数">{{ currentApplication.rentalMonths }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusColor(currentApplication.applicationStatus)">
            {{ getStatusName(currentApplication.applicationStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请原因" :span="2">{{ currentApplication.applicationReason }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ currentApplication.reviewUserName }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ parseTime(currentApplication.reviewTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ currentApplication.reviewRemark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(currentApplication.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(currentApplication.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 合同详情对话框 -->
    <el-dialog
      v-model="contractDetailVisible"
      title="合同详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ currentContract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="车位编号">{{ currentContract.spaceNo }}</el-descriptions-item>
        <el-descriptions-item label="车位位置">{{ currentContract.location }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentContract.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="业主姓名">{{ currentContract.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentContract.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="车辆号码">{{ currentContract.vehicleNumber }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentContract.vehicleBrand }}</el-descriptions-item>
        <el-descriptions-item label="车辆颜色">{{ currentContract.vehicleColor }}</el-descriptions-item>
        <el-descriptions-item label="租赁月数">{{ currentContract.rentalMonths }} 个月</el-descriptions-item>
        <el-descriptions-item label="租赁开始日期">{{ parseTime(currentContract.startDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="租赁结束日期">{{ parseTime(currentContract.endDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentContract.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentContract.paidAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <el-tag :type="getContractStatusColor(currentContract.contractStatus)">
            {{ getContractStatusName(currentContract.contractStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ parseTime(currentContract.signDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="剩余天数" :span="2">
          <span v-if="currentContract.remainingDays !== null && currentContract.remainingDays !== undefined">
            {{ currentContract.remainingDays > 0 ? currentContract.remainingDays + ' 天' : '已到期' }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentContract.contractStatus === 4" label="终止日期" :span="2">
          {{ parseTime(currentContract.terminateDate, '{y}-{m}-{d}') }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentContract.contractStatus === 4" label="终止原因" :span="2">
          {{ currentContract.terminateReason }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentContract.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(currentContract.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(currentContract.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="contractDetailVisible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Check,
  Close,
  Download
} from '@element-plus/icons-vue'

// 导入API
import {
  listApplication,
  getApplication,
  delApplication,
  reviewApplication
} from '@/api/parking/rentalApplication'
import {
  listContract,
  getContract,
  terminateContract
} from '@/api/parking/rentalContract'

// 响应式数据
const activeTab = ref('applications')
const loading = ref(false)
const recordLoading = ref(false)
const detailVisible = ref(false)
const contractDetailVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  applicant: '',
  spaceNo: '',
  vehicleNumber: '',
  status: null
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  spaceNo: '',
  ownerName: '',
  vehicleNumber: '',
  applicationStatus: null
})

const recordSearchForm = reactive({
  ownerName: '',
  spaceNo: '',
  vehicleNumber: '',
  contractStatus: null
})

const recordSearchParams = ref({
  pageNum: 1,
  pageSize: 10,
  spaceNo: '',
  ownerName: '',
  vehicleNumber: '',
  contractStatus: null
})

// 数据
const applicationList = ref([])
const recordList = ref([])
const selectedApplications = ref([])
const currentApplication = ref({})
const currentContract = ref({})
const ids = ref([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)

// 表单相关
const open = ref(false)
const reviewOpen = ref(false)
const title = ref('')
const form = ref({
  monthlyRent: 0,
  discount: 0
})
const reviewForm = ref({})
const formRef = ref()
const reviewFormRef = ref()

// 费用计算
const subtotal = computed(() => {
  const rent = form.value.monthlyRent || 0
  const months = form.value.rentalMonths || 1
  return rent * months
})

const totalAmount = computed(() => {
  return Math.max(0, subtotal.value - (form.value.discount || 0))
})

// 表单验证规则
const rules = reactive({
  ownerName: [
    { required: true, message: '业主姓名不能为空', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' }
  ],
  vehicleNumber: [
    { required: true, message: '车辆号码不能为空', trigger: 'blur' }
  ],
  rentalStartDate: [
    { required: true, message: '租赁开始日期不能为空', trigger: 'change' }
  ],
  rentalMonths: [
    { required: true, message: '租赁月数不能为空', trigger: 'change' }
  ]
})

const reviewRules = reactive({
  status: [
    { required: true, message: '审核结果不能为空', trigger: 'change' }
  ]
})

const currentPage = ref(1)
const pageSize = ref(10)
const recordCurrentPage = ref(1)
const recordPageSize = ref(10)
const recordTotal = ref(0)

// 可用车位列表
const availableSpaces = ref([])

// 获取申请状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '待审核',
    2: '已通过',
    3: '已驳回'
  }
  return statusMap[status] || '未知'
}

// 获取申请状态颜色
const getStatusType = (status) => {
  const colorMap = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取申请状态颜色 (兼容旧的函数名)
const getStatusColor = (status) => {
  return getStatusType(status)
}

// 获取租赁状态名称（保留兼容）
const getLeaseStatusName = (status) => {
  return getContractStatusName(status)
}

// 获取租赁状态颜色（保留兼容）
const getLeaseStatusColor = (status) => {
  return getContractStatusColor(status)
}

// 获取合同状态名称
const getContractStatusName = (status) => {
  const statusMap = {
    1: '待付款',
    2: '进行中',
    3: '已到期',
    4: '已终止'
  }
  return statusMap[status] || '未知'
}

// 获取合同状态颜色
const getContractStatusColor = (status) => {
  const colorMap = {
    1: 'warning',
    2: 'success',
    3: 'info',
    4: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const d = new Date(dateTime)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 计算总金额
const calculateTotalAmount = () => {
  // 费用计算通过 computed 自动完成
}

// 加载可用车位
const loadAvailableSpaces = async () => {
  try {
    // 导入车位API
    const { listParkingSpaces } = await import('@/api/parkingSpace')
    const response = await listParkingSpaces({
      spaceStatus: 1, // 只加载空闲车位
      pageSize: 1000
    })
    availableSpaces.value = response.data.rows || []
  } catch (error) {
    console.error('加载可用车位失败:', error)
  }
}

// 车位变化时自动填充信息
const handleSpaceChange = (spaceId) => {
  const space = availableSpaces.value.find(s => s.id === spaceId)
  if (space) {
    form.value.spaceNo = space.spaceNo
    form.value.monthlyRent = space.monthlyRent || 0
  }
}

// 工具函数
const parseTime = (time, pattern) => {
  if (!time) return null
  const date = new Date(time)
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds()
  }
  return format.replace(/{(y|m|d|h|i|s)+}/g, (result, key) => {
    let value = formatObj[key]
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || '0'
  })
}

// 查询申请列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listApplication(queryParams.value)
    applicationList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('查询租赁申请列表失败:', error)
    ElMessage.error('查询数据失败')
  } finally {
    loading.value = false
  }
}

// 取消表单
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.value = {
    id: null,
    parkingSpaceId: null,
    spaceNo: '',
    ownerId: null,
    ownerName: '',
    contactPhone: '',
    vehicleNumber: '',
    vehicleBrand: '',
    vehicleColor: '',
    rentalStartDate: null,
    rentalEndDate: null,
    rentalMonths: 1,
    monthlyRent: 0,
    discount: 0,
    applicationReason: ''
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    spaceNo: '',
    ownerName: '',
    vehicleNumber: '',
    applicationStatus: null
  }
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = "添加租赁申请"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const _id = row.id || ids.value
  getApplication(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改租赁申请"
  })
}

// 提交表单（已移除新增和修改功能）
const submitForm = () => {
  // 新增和修改功能已禁用，业主只能通过"我的车位"页面提交申请
  ElMessage.info('请通过"我的车位"页面提交申请')
}

// 删除按钮操作
const handleDelete = (row) => {
  const _ids = row.id || ids.value
  ElMessageBox.confirm('是否确认删除租赁申请编号为"' + _ids + '"的数据项？').then(() => {
    return delApplication(_ids)
  }).then(() => {
    getList()
    ElMessage.success("删除成功")
  }).catch(() => {})
}

// 单个审核
const handleReviewSingle = (row) => {
  reviewForm.value = {
    id: row.id,
    status: 2,
    reviewRemark: ''
  }
  reviewOpen.value = true
}

// 提交审核
const submitReview = () => {
  if (reviewFormRef.value) {
    reviewFormRef.value.validate(valid => {
      if (valid) {
        const applicationId = Array.isArray(ids.value) ? ids.value[0] : reviewForm.value.id
        reviewApplication(applicationId, reviewForm.value.status, reviewForm.value.reviewRemark).then(response => {
          ElMessage.success("审核成功")
          reviewOpen.value = false
          getList()
        })
      }
    })
  }
}

// 详情查看
const handleDetail = (row) => {
  getApplication(row.id).then(response => {
    currentApplication.value = response.data
    detailVisible.value = true
  })
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  if (tabName === 'applications') {
    getList()
  } else if (tabName === 'records') {
    // 保持模拟数据的记录功能
    loadRecords()
  }
}

// 搜索
const handleSearch = () => {
  queryParams.value.ownerName = searchForm.applicant
  queryParams.value.spaceNo = searchForm.spaceNo
  queryParams.value.vehicleNumber = searchForm.vehicleNumber
  queryParams.value.applicationStatus = searchForm.status
  queryParams.value.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    applicant: '',
    spaceNo: '',
    vehicleNumber: '',
    status: null
  })
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    spaceNo: '',
    ownerName: '',
    vehicleNumber: '',
    applicationStatus: null
  }
  getList()
}

// 记录搜索
const handleRecordSearch = () => {
  recordSearchParams.value.ownerName = recordSearchForm.ownerName
  recordSearchParams.value.spaceNo = recordSearchForm.spaceNo
  recordSearchParams.value.vehicleNumber = recordSearchForm.vehicleNumber
  recordSearchParams.value.contractStatus = recordSearchForm.contractStatus
  recordSearchParams.value.pageNum = 1
  loadRecords()
}

// 记录重置
const handleRecordReset = () => {
  Object.assign(recordSearchForm, {
    ownerName: '',
    spaceNo: '',
    vehicleNumber: '',
    contractStatus: null
  })
  recordSearchParams.value = {
    pageNum: 1,
    pageSize: 10,
    spaceNo: '',
    ownerName: '',
    vehicleNumber: '',
    contractStatus: null
  }
  loadRecords()
}

// 批量通过
const handleBatchApprove = async () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要审核的申请')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要通过选中的 ${selectedApplications.value.length} 个申请吗？`,
      '批量通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 过滤出待审核的申请
    const pendingApps = selectedApplications.value.filter(app => app.applicationStatus === 1)

    if (pendingApps.length === 0) {
      ElMessage.warning('所选申请中没有待审核的申请')
      return
    }

    // 批量审核逻辑
    const promises = pendingApps.map(app =>
      reviewApplication(app.id, 2, '批量审核通过')
    )

    await Promise.all(promises)
    ElMessage.success('批量审核成功')
    getList()
  } catch (error) {
    // 用户取消操作
    if (error !== 'cancel') {
      console.error('批量审核失败:', error)
      ElMessage.error('批量审核失败')
    }
  }
}

// 批量拒绝
const handleBatchReject = async () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要拒绝的申请')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要拒绝选中的 ${selectedApplications.value.length} 个申请吗？`,
      '批量拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 过滤出待审核的申请
    const pendingApps = selectedApplications.value.filter(app => app.applicationStatus === 1)

    if (pendingApps.length === 0) {
      ElMessage.warning('所选申请中没有待审核的申请')
      return
    }

    // 批量审核逻辑
    const promises = pendingApps.map(app =>
      reviewApplication(app.id, 3, '批量审核驳回')
    )

    await Promise.all(promises)
    ElMessage.success('批量审核成功')
    getList()
  } catch (error) {
    // 用户取消操作
    if (error !== 'cancel') {
      console.error('批量审核失败:', error)
      ElMessage.error('批量审核失败')
    }
  }
}

// 通过申请
const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确定要通过申请吗？`,
    '通过申请',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    reviewApplication(row.id, 2, '审核通过').then(() => {
      ElMessage.success('申请已通过')
      getList()
    })
  })
}

// 拒绝申请
const handleReject = (row) => {
  ElMessageBox.confirm(
    `确定要拒绝申请吗？`,
    '拒绝申请',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    reviewApplication(row.id, 3, '审核驳回').then(() => {
      ElMessage.success('申请已拒绝')
      getList()
    })
  })
}

// 查看详情
const handleView = (row) => {
  handleDetail(row)
}

// 查看合同
const handleViewContract = async (row) => {
  try {
    const response = await getContract(row.id)
    currentContract.value = response.data
    contractDetailVisible.value = true
  } catch (error) {
    console.error('查看合同失败:', error)
    ElMessage.error('查看合同失败')
  }
}

// 终止合同
const handleTerminate = (row) => {
  ElMessageBox.prompt('请输入终止原因', '终止合同', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '终止原因不能为空'
  }).then(({ value }) => {
    terminateContract(row.id, value).then(() => {
      ElMessage.success('合同已终止')
      loadRecords()
    }).catch(() => {
      ElMessage.error('终止合同失败')
    })
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getList()
}

const handleRecordSizeChange = (val) => {
  recordSearchParams.value.pageSize = val
  recordCurrentPage.value = 1
  loadRecords()
}

const handleRecordCurrentChange = (val) => {
  recordSearchParams.value.pageNum = val
  loadRecords()
}

// 加载记录数据
const loadRecords = async () => {
  recordLoading.value = true
  try {
    const response = await listContract(recordSearchParams.value)
    recordList.value = response.data.rows || []
    recordTotal.value = response.data.total || 0
  } catch (error) {
    console.error('查询租赁记录失败:', error)
    ElMessage.error('查询数据失败')
  } finally {
    recordLoading.value = false
  }
}

// 初始化
onMounted(() => {
  getList()
  loadAvailableSpaces()
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

  .review-info {
    font-size: 12px;
    line-height: 1.5;

    .review-time {
      color: #909399;
      font-size: 11px;
    }
  }
}
</style>