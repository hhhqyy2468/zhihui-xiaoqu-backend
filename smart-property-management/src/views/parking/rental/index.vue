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
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="车位编号">
                <el-input
                  v-model="searchForm.spaceNo"
                  placeholder="请输入车位编号"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="申请状态">
                <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                  style="width: 150px"
                >
                  <el-option label="待审核" value="1" />
                  <el-option label="已通过" value="2" />
                  <el-option label="已驳回" value="3" />
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
              <el-table-column prop="applicationNo" label="申请编号" width="150" />
              <el-table-column label="车位信息" width="180">
                <template #default="{ row }">
                  {{ row.vehicleNumber }} - {{ row.spaceNo }}
                </template>
              </el-table-column>
              <el-table-column prop="ownerName" label="申请人" width="120" />
              <el-table-column prop="contactPhone" label="联系电话" width="120" />
              <el-table-column prop="monthlyRent" label="月租金" width="120">
                <template #default="{ row }">
                  ¥{{ row.monthlyRent }}
                </template>
              </el-table-column>
              <el-table-column prop="rentalMonths" label="租期(月)" width="100" />
              <el-table-column prop="applicationStatus" label="申请状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusColor(row.applicationStatus)">
                    {{ getStatusName(row.applicationStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="申请时间" width="180">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
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
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
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
                  v-model="recordSearchForm.tenant"
                  placeholder="请输入承租人姓名"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="车位编号">
                <el-input
                  v-model="recordSearchForm.spaceNo"
                  placeholder="请输入车位编号"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="租赁状态">
                <el-select
                  v-model="recordSearchForm.leaseStatus"
                  placeholder="请选择状态"
                  clearable
                  style="width: 150px"
                >
                  <el-option label="进行中" value="1" />
                  <el-option label="已到期" value="2" />
                  <el-option label="已终止" value="3" />
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

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button
              type="warning"
              @click="handleExportRecords"
            >
              <el-icon><Download /></el-icon>
              导出记录
            </el-button>
          </div>

          <!-- 记录表格 -->
          <div class="table-section">
            <el-table
              v-loading="recordLoading"
              :data="recordList"
            >
              <el-table-column prop="contractNo" label="合同编号" width="150" />
              <el-table-column prop="tenant" label="承租人" width="120" />
              <el-table-column prop="spaceNo" label="车位编号" width="120" />
              <el-table-column prop="monthlyRent" label="月租金" width="120">
                <template #default="{ row }">
                  ¥{{ row.monthlyRent }}
                </template>
              </el-table-column>
              <el-table-column prop="leaseStatus" label="租赁状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getLeaseStatusColor(row.leaseStatus)">
                    {{ getLeaseStatusName(row.leaseStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="startDate" label="开始日期" width="120" />
              <el-table-column prop="endDate" label="结束日期" width="120" />
              <el-table-column prop="totalAmount" label="总金额" width="120">
                <template #default="{ row }">
                  ¥{{ row.totalAmount }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button
                    link
                    type="primary"
                    @click="handleViewContract(row)"
                  >
                    查看合同
                  </el-button>
                  <el-button
                    v-if="row.leaseStatus === '1'"
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
                v-model:current-page="recordCurrentPage"
                v-model:page-size="recordPageSize"
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
          />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
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
  addApplication,
  updateApplication,
  delApplication,
  reviewApplication
} from '@/api/parking/rentalApplication'

// 响应式数据
const activeTab = ref('applications')
const loading = ref(false)
const recordLoading = ref(false)
const detailVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  applicant: '',
  spaceNo: '',
  status: ''
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
  tenant: '',
  spaceNo: '',
  leaseStatus: ''
})

// 数据
const applicationList = ref([])
const recordList = ref([])
const selectedApplications = ref([])
const currentApplication = ref({})
const ids = ref([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)

// 表单相关
const open = ref(false)
const reviewOpen = ref(false)
const title = ref('')
const form = ref({})
const reviewForm = ref({})
const formRef = ref()
const reviewFormRef = ref()

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

// 获取租赁状态名称
const getLeaseStatusName = (status) => {
  const statusMap = {
    1: '进行中',
    2: '已到期',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

// 获取租赁状态颜色
const getLeaseStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
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

// 提交表单
const submitForm = () => {
  if (formRef.value) {
    formRef.value.validate(valid => {
      if (valid) {
        if (form.value.id != null) {
          updateApplication(form.value).then(response => {
            ElMessage.success("修改成功")
            open.value = false
            getList()
          })
        } else {
          addApplication(form.value).then(response => {
            ElMessage.success("新增成功")
            open.value = false
            getList()
          })
        }
      }
    })
  }
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

// 审核按钮操作
const handleReview = (row) => {
  const applicationIds = row.id || ids.value
  reviewForm.value = {
    status: 2,
    reviewRemark: ''
  }
  reviewOpen.value = true
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

// 搜索 (兼容旧的搜索接口)
const handleSearch = () => {
  queryParams.value.ownerName = searchForm.applicant
  queryParams.value.spaceNo = searchForm.spaceNo
  queryParams.value.applicationStatus = searchForm.status ? parseInt(searchForm.status) : null
  queryParams.value.pageNum = 1
  getList()
}

// 重置 (兼容旧的重置接口)
const handleReset = () => {
  Object.assign(searchForm, {
    applicant: '',
    spaceNo: '',
    status: ''
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

// 记录搜索 (保持模拟数据)
const handleRecordSearch = () => {
  recordCurrentPage.value = 1
  loadRecords()
}

// 记录重置 (保持模拟数据)
const handleRecordReset = () => {
  Object.assign(recordSearchForm, {
    tenant: '',
    spaceNo: '',
    leaseStatus: ''
  })
  handleRecordSearch()
}

// 批量通过
const handleBatchApprove = () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要审核的申请')
    return
  }

  ElMessageBox.confirm(
    `确定要通过选中的 ${selectedApplications.value.length} 个申请吗？`,
    '批量通过',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 批量审核逻辑
    selectedApplications.value.forEach(app => {
      if (app.applicationStatus === 1) {
        reviewApplication(app.id, 2, '批量审核通过').catch(() => {})
      }
    })
    ElMessage.success('批量审核成功')
    getList()
  })
}

// 批量拒绝
const handleBatchReject = () => {
  if (selectedApplications.value.length === 0) {
    ElMessage.warning('请选择要拒绝的申请')
    return
  }

  ElMessageBox.confirm(
    `确定要拒绝选中的 ${selectedApplications.value.length} 个申请吗？`,
    '批量拒绝',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 批量审核逻辑
    selectedApplications.value.forEach(app => {
      if (app.applicationStatus === 1) {
        reviewApplication(app.id, 3, '批量审核驳回').catch(() => {})
      }
    })
    ElMessage.success('批量审核成功')
    getList()
  })
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
const handleViewContract = (row) => {
  ElMessage.info(`查看合同 ${row.contractNo} 的详细信息`)
}

// 终止合同
const handleTerminate = (row) => {
  ElMessageBox.confirm(
    `确定要终止合同 ${row.contractNo} 吗？`,
    '终止合同',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('合同已终止')
    loadRecords()
  })
}

// 导出记录
const handleExportRecords = () => {
  ElMessage.success('租赁记录导出成功')
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
  recordPageSize.value = val
  loadRecords()
}

const handleRecordCurrentChange = (val) => {
  recordCurrentPage.value = val
  loadRecords()
}

// 保持模拟数据生成功能（用于租赁记录）
const generateMockRecords = () => {
  const records = []
  const tenants = ['张三', '李四', '王五', '赵六', '钱七', '孙八']
  const statuses = ['1', '2', '3']

  for (let i = 1; i <= 50; i++) {
    const leaseTerm = Math.floor(Math.random() * 12) + 6
    const startDate = new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000)
    const endDate = new Date(startDate.getTime() + leaseTerm * 30 * 24 * 60 * 60 * 1000)

    records.push({
      id: i,
      contractNo: `C${String(i).padStart(6, '0')}`,
      tenant: tenants[Math.floor(Math.random() * tenants.length)],
      spaceNo: `P${String(Math.floor(Math.random() * 500) + 1).padStart(3, '0')}`,
      monthlyRent: Math.floor(Math.random() * 500) + 200,
      leaseStatus: statuses[Math.floor(Math.random() * statuses.length)],
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      totalAmount: (Math.floor(Math.random() * 500) + 200) * leaseTerm
    })
  }

  return records
}

// 加载记录数据 (保持模拟数据)
const loadRecords = () => {
  recordLoading.value = true
  setTimeout(() => {
    const mockData = generateMockRecords()
    recordList.value = mockData.slice(
      (recordCurrentPage.value - 1) * recordPageSize.value,
      recordCurrentPage.value * recordPageSize.value
    )
    recordTotal.value = mockData.length
    recordLoading.value = false
  }, 500)
}

// 初始化
onMounted(() => {
  getList()
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
</style>