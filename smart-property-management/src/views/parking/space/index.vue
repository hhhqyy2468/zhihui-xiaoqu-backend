<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">车位管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>停车管理</el-breadcrumb-item>
        <el-breadcrumb-item>车位管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 车位管理 -->
      <el-tab-pane label="车位管理" name="list">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="searchForm" inline>
            <el-form-item label="车位编号">
              <el-input
                v-model="searchForm.spaceNo"
                placeholder="请输入车位编号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位位置">
              <el-input
                v-model="searchForm.location"
                placeholder="请输入车位位置"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 150px"
              >
                <el-option label="空闲" :value="1" />
                <el-option label="已租" :value="2" />
                <el-option label="维修中" :value="3" />
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
            @click="handleApply"
          >
            <el-icon><Plus /></el-icon>
            申请新车位
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedSpaces.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          </div>

        <!-- 车位表格 -->
        <div class="table-section">
          <el-table
            v-loading="loading"
            :data="parkingSpaceList"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="spaceNo" label="车位编号" width="120" sortable />
            <el-table-column prop="location" label="车位位置" show-overflow-tooltip />
            <el-table-column prop="spaceStatus" label="车位状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.spaceStatus)">
                  {{ getStatusName(row.spaceStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="currentTenant" label="使用人" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  link
                  type="info"
                  @click="handleViewRentals(row)"
                >
                  租赁记录
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
    </el-tabs>

    <!-- 新增/编辑车位对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="车位编号" prop="spaceNo">
          <el-input v-model="form.spaceNo" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="车位位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入车位位置" />
        </el-form-item>
        <el-form-item label="月租金" prop="monthlyRent">
          <el-input-number
            v-model="form.monthlyRent"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="车位状态" prop="spaceStatus">
          <el-radio-group v-model="form.spaceStatus">
            <el-radio :value="1">空闲</el-radio>
            <el-radio :value="2">已租</el-radio>
            <el-radio :value="3">维修中</el-radio>
          </el-radio-group>
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
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 申请车位对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="申请新车位"
      width="700px"
    >
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="applyRules"
        label-width="120px"
      >
        <el-form-item label="选择车位" prop="parkingSpaceId">
          <el-select
            v-model="applyForm.parkingSpaceId"
            placeholder="请选择车位"
            filterable
            @change="handleSpaceChange"
            style="width: 100%"
          >
            <el-option
              v-for="space in availableSpaces"
              :key="space.id"
              :label="`${space.spaceNo} - ${space.location} (¥${space.monthlyRent}/月)`"
              :value="space.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车位编号">
          <el-input v-model="applyForm.spaceNo" disabled />
        </el-form-item>
        <el-form-item label="月租金">
          <el-input v-model="applyForm.monthlyRent" disabled />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="applyForm.contactPhone"
            placeholder="请输入联系电话"
          />
        </el-form-item>
        <el-form-item label="车辆号码" prop="vehicleNumber">
          <el-input
            v-model="applyForm.vehicleNumber"
            placeholder="请输入车辆号码"
          />
        </el-form-item>
        <el-form-item label="车辆品牌" prop="vehicleBrand">
          <el-input
            v-model="applyForm.vehicleBrand"
            placeholder="请输入车辆品牌"
          />
        </el-form-item>
        <el-form-item label="车辆颜色" prop="vehicleColor">
          <el-input
            v-model="applyForm.vehicleColor"
            placeholder="请输入车辆颜色"
          />
        </el-form-item>
        <el-form-item label="租赁月数" prop="rentalMonths">
          <el-input-number
            v-model="applyForm.rentalMonths"
            :min="1"
            :max="60"
            @change="calculateTotal"
          />
        </el-form-item>
        <el-form-item label="租赁开始日期" prop="rentalStartDate">
          <el-date-picker
            v-model="applyForm.rentalStartDate"
            type="date"
            placeholder="选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="calculateTotal"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input v-model="totalAmountDisplay" disabled />
        </el-form-item>
        <el-form-item label="申请原因" prop="applicationReason">
          <el-input
            v-model="applyForm.applicationReason"
            type="textarea"
            :rows="3"
            placeholder="请输入申请原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApply">提交申请</el-button>
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
  Delete,
  Upload,
  Download
} from '@element-plus/icons-vue'
import {
  listParkingSpaces,
  getParkingSpace,
  addParkingSpace,
  updateParkingSpace,
  deleteParkingSpaces,
  getAvailableSpaces
} from '@/api/parkingSpace'
import {
  submitMyApplication as submitApplication
} from '@/api/parking/rentalApplication'

// 响应式数据
const activeTab = ref('list')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增车位')
const applyDialogVisible = ref(false)
const submitLoading = ref(false)
const availableSpaces = ref([])

// 表单数据
const formRef = ref()
const form = reactive({
  spaceNo: '',
  location: '',
  spaceStatus: 1,
  monthlyRent: 0,
  remark: ''
})

// 搜索表单
const searchForm = reactive({
  spaceNo: '',
  location: '',
  status: null
})

// 车位数据
const parkingSpaceList = ref([])
const selectedSpaces = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)


// 表单验证规则
const rules = {
  spaceNo: [
    { required: true, message: '请输入车位编号', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入车位位置', trigger: 'blur' }
  ],
  spaceStatus: [
    { required: true, message: '请选择车位状态', trigger: 'change' }
  ],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' }
  ]
}

// 申请表单
const applyFormRef = ref()
const applyForm = reactive({
  parkingSpaceId: null,
  spaceNo: '',
  monthlyRent: 0,
  contactPhone: '',
  vehicleNumber: '',
  vehicleBrand: '',
  vehicleColor: '',
  rentalMonths: 12,
  rentalStartDate: '',
  applicationReason: ''
})

// 申请表单验证规则
const applyRules = {
  parkingSpaceId: [
    { required: true, message: '请选择车位', trigger: 'change' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  vehicleNumber: [
    { required: true, message: '请输入车辆号码', trigger: 'blur' }
  ],
  vehicleBrand: [
    { required: true, message: '请输入车辆品牌', trigger: 'blur' }
  ],
  vehicleColor: [
    { required: true, message: '请输入车辆颜色', trigger: 'blur' }
  ],
  rentalMonths: [
    { required: true, message: '请选择租赁月数', trigger: 'change' }
  ],
  rentalStartDate: [
    { required: true, message: '请选择租赁开始日期', trigger: 'change' }
  ],
  applicationReason: [
    { required: true, message: '请输入申请原因', trigger: 'blur' }
  ]
}

// 总金额显示
const totalAmountDisplay = ref('¥0')

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '空闲',
    2: '已租',
    3: '维修中'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
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


// 加载车位数据
const loadParkingSpaces = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      spaceNo: searchForm.spaceNo,
      location: searchForm.location,
      status: searchForm.status
    }
    const response = await listParkingSpaces(params)
    if (response.code === 200) {
      parkingSpaceList.value = response.data.rows
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载车位数据失败')
    }
  } catch (error) {
    console.error('加载车位数据错误:', error)
    ElMessage.error('加载车位数据失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  loadParkingSpaces()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadParkingSpaces()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    spaceNo: '',
    location: '',
    status: null
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  handleApply()
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑车位'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除车位 ${row.spaceNo} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteParkingSpaces([row.id])
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadParkingSpaces()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除车位错误:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedSpaces.value.length === 0) {
    ElMessage.warning('请选择要删除的车位')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedSpaces.value.length} 个车位吗？`,
    '批量删除警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedSpaces.value.map(space => space.id)
      const response = await deleteParkingSpaces(ids)
      if (response.code === 200) {
        ElMessage.success('批量删除成功')
        loadParkingSpaces()
      } else {
        ElMessage.error(response.msg || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除车位错误:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

// 查看租赁记录
const handleViewRentals = (row) => {
  ElMessage.info(`查看车位 ${row.spaceNo} 的租赁记录`)
}


// 提交表单
const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (dialogTitle.value === '新增车位') {
          response = await addParkingSpace(form)
        } else {
          response = await updateParkingSpace(form)
        }

        if (response.code === 200) {
          ElMessage.success(dialogTitle.value + '成功')
          dialogVisible.value = false
          loadParkingSpaces()
        } else {
          ElMessage.error(response.msg || (dialogTitle.value + '失败'))
        }
      } catch (error) {
        console.error(dialogTitle.value + '错误:', error)
        ElMessage.error(dialogTitle.value + '失败')
      }
    }
  })
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedSpaces.value = selection
}

// 申请车位
const handleApply = async () => {
  try {
    const response = await getAvailableSpaces()
    if (response.code === 200) {
      availableSpaces.value = response.data || []
      if (availableSpaces.value.length === 0) {
        ElMessage.warning('暂无可用的空闲车位')
        return
      }
      applyDialogVisible.value = true
    } else {
      ElMessage.error('加载可用车位失败')
    }
  } catch (error) {
    console.error('加载可用车位错误:', error)
    ElMessage.error('加载可用车位失败')
  }
}

// 车位选择变化
const handleSpaceChange = (spaceId) => {
  const space = availableSpaces.value.find(s => s.id === spaceId)
  if (space) {
    applyForm.spaceNo = space.spaceNo
    applyForm.monthlyRent = space.monthlyRent
    calculateTotal()
  }
}

// 计算总金额
const calculateTotal = () => {
  if (applyForm.monthlyRent && applyForm.rentalMonths) {
    const total = applyForm.monthlyRent * applyForm.rentalMonths
    totalAmountDisplay.value = `¥${total.toFixed(2)}`
  } else {
    totalAmountDisplay.value = '¥0'
  }
}

// 提交申请
const submitApply = async () => {
  if (!applyFormRef.value) return

  try {
    await applyFormRef.value.validate()
    submitLoading.value = true

    const response = await submitApplication(applyForm)
    if (response.code === 200) {
      ElMessage.success('申请提交成功，请等待管理员审核')
      applyDialogVisible.value = false
      // 重置表单
      applyFormRef.value.resetFields()
      Object.assign(applyForm, {
        parkingSpaceId: null,
        spaceNo: '',
        monthlyRent: 0,
        contactPhone: '',
        vehicleNumber: '',
        vehicleBrand: '',
        vehicleColor: '',
        rentalMonths: 12,
        rentalStartDate: '',
        applicationReason: ''
      })
      totalAmountDisplay.value = '¥0'
    } else {
      ElMessage.error(response.msg || '申请提交失败')
    }
  } catch (error) {
    if (error !== false) { // 排除表单验证失败
      console.error('提交申请错误:', error)
      ElMessage.error('申请提交失败')
    }
  } finally {
    submitLoading.value = false
  }
}


// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadParkingSpaces()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadParkingSpaces()
}

// 初始化
onMounted(() => {
  loadParkingSpaces()
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