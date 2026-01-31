<template>
  <div class="parking-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的车位</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业服务</el-breadcrumb-item>
        <el-breadcrumb-item>我的车位</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="合同编号">
          <el-input v-model="searchForm.contractNo" placeholder="请输入合同编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="车位编号">
          <el-input v-model="searchForm.spaceNo" placeholder="请输入车位编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="合同状态">
          <el-select v-model="searchForm.contractStatus" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待付款" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已到期" :value="3" />
            <el-option label="已终止" :value="4" />
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
          <el-button type="success" @click="handleApply">
            <el-icon><Plus /></el-icon>
            申请新车位
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 车位列表 -->
    <div class="table-section">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的合同" name="contracts">
          <el-table v-loading="loading" :data="contractList">
            <el-table-column prop="contractNo" label="合同编号" width="150" />
            <el-table-column prop="spaceNo" label="车位编号" width="120" />
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="vehicleNumber" label="车牌号" width="120" />
            <el-table-column prop="vehicleBrand" label="车辆品牌" width="120" />
            <el-table-column prop="startDate" label="开始日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.startDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="endDate" label="结束日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.endDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="paidAmount" label="已付金额" width="120">
              <template #default="{ row }">
                ¥{{ row.paidAmount || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="contractStatus" label="合同状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.contractStatus)">
                  {{ getStatusName(row.contractStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleView(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="contractList.length === 0" description="暂无租赁合同，请先申请车位" />
        </el-tab-pane>

        <el-tab-pane label="我的申请" name="applications">
          <el-table v-loading="loading" :data="applicationList">
            <el-table-column prop="spaceNo" label="车位编号" width="120" />
            <el-table-column prop="vehicleNumber" label="车牌号" width="120" />
            <el-table-column prop="vehicleBrand" label="车辆品牌" width="120" />
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="rentalMonths" label="租赁月数" width="100" />
            <el-table-column prop="applicationStatus" label="审核状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getApplicationStatusColor(row.applicationStatus)">
                  {{ getApplicationStatusName(row.applicationStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="reviewRemark" label="审核意见" width="200" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="applicationList.length === 0" description="暂无申请记录" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 申请对话框 -->
    <el-dialog v-model="applyVisible" title="申请新车位" width="700px">
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="100px">
        <el-form-item label="选择车位" prop="parkingSpaceId">
          <el-select
            v-model="applyForm.parkingSpaceId"
            placeholder="请选择可用车位"
            style="width: 100%"
            @change="handleSpaceChange"
          >
            <el-option
              v-for="space in availableSpaces"
              :key="space.id"
              :label="`${space.spaceNo} - ${space.location} - ¥${space.monthlyRent}/月`"
              :value="space.id"
            />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">车辆信息</el-divider>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="applyForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="车牌号" prop="vehicleNumber">
          <el-input v-model="applyForm.vehicleNumber" placeholder="请输入车牌号" />
        </el-form-item>

        <el-form-item label="车辆品牌" prop="vehicleBrand">
          <el-input v-model="applyForm.vehicleBrand" placeholder="请输入车辆品牌" />
        </el-form-item>

        <el-form-item label="车辆颜色">
          <el-input v-model="applyForm.vehicleColor" placeholder="请输入车辆颜色" />
        </el-form-item>

        <el-divider content-position="left">租赁信息</el-divider>

        <el-form-item label="月租金">
          <el-input v-model="selectedSpaceRent" disabled />
        </el-form-item>

        <el-form-item label="租赁月数" prop="rentalMonths">
          <el-input-number
            v-model="applyForm.rentalMonths"
            :min="1"
            :max="60"
            @change="calculateTotal"
          />
          <span style="margin-left: 10px">个月</span>
        </el-form-item>

        <el-form-item label="租赁开始日期" prop="rentalStartDate">
          <el-date-picker
            v-model="applyForm.rentalStartDate"
            type="date"
            placeholder="请选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="优惠金额">
          <el-input-number
            v-model="applyForm.discount"
            :min="0"
            :precision="2"
            @change="calculateTotal"
          />
          <span style="margin-left: 10px">元</span>
        </el-form-item>

        <el-divider />

        <el-form-item label="费用明细">
          <div class="fee-summary">
            <div class="fee-row">
              <span>月租金：</span>
              <span class="fee-value">¥{{ selectedSpaceRent }}</span>
            </div>
            <div class="fee-row">
              <span>租赁月数：</span>
              <span class="fee-value">{{ applyForm.rentalMonths }} 个月</span>
            </div>
            <div class="fee-row">
              <span>小计：</span>
              <span class="fee-value">¥{{ subtotal }}</span>
            </div>
            <div class="fee-row">
              <span>优惠：</span>
              <span class="fee-value discount">-¥{{ applyForm.discount || 0 }}</span>
            </div>
            <div class="fee-row total">
              <span>总金额：</span>
              <span class="fee-value">¥{{ totalAmount }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="applyForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 合同详情对话框 -->
    <el-dialog v-model="detailVisible" title="合同详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ currentContract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <el-tag :type="getStatusColor(currentContract.contractStatus)">
            {{ getStatusName(currentContract.contractStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="车位编号">{{ currentContract.spaceNo }}</el-descriptions-item>
        <el-descriptions-item label="承租人">{{ currentContract.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentContract.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="车辆号码">{{ currentContract.vehicleNumber }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentContract.vehicleBrand }}</el-descriptions-item>
        <el-descriptions-item label="车辆颜色">{{ currentContract.vehicleColor }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentContract.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="租赁月数">{{ currentContract.rentalMonths }} 个月</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentContract.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentContract.paidAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ formatDate(currentContract.signDate) }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ formatDate(currentContract.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ formatDate(currentContract.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="待付金额">¥{{ ((currentContract.totalAmount || 0) - (currentContract.paidAmount || 0)).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getMyContracts } from '@/api/parking/rentalContract'
import { getAvailableSpaces } from '@/api/parkingSpace'
import { submitMyApplication, getMyApplications } from '@/api/parking/rentalApplication'

// 响应式数据
const loading = ref(false)
const detailVisible = ref(false)
const applyVisible = ref(false)
const submitLoading = ref(false)
const activeTab = ref('contracts')

// 搜索表单
const searchForm = reactive({
  contractNo: '',
  spaceNo: '',
  contractStatus: null
})

// 合同列表
const contractList = ref([])
const allContracts = ref([]) // 保存所有数据用于本地过滤
const currentContract = ref({})

// 申请列表
const applicationList = ref([])

// 可用车位列表
const availableSpaces = ref([])

// 申请表单
const applyFormRef = ref()
const applyForm = reactive({
  parkingSpaceId: null,
  spaceNo: '',
  contactPhone: '',
  vehicleNumber: '',
  vehicleBrand: '',
  vehicleColor: '',
  monthlyRent: 0,
  rentalMonths: 12,
  rentalStartDate: '',
  discount: 0,
  remark: ''
})

// 选中的车位租金
const selectedSpaceRent = ref('0')

// 表单验证规则
const applyRules = {
  parkingSpaceId: [{ required: true, message: '请选择车位', trigger: 'change' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  vehicleNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]$/, message: '请输入正确的车牌号', trigger: 'blur' }
  ],
  vehicleBrand: [{ required: true, message: '请输入车辆品牌', trigger: 'blur' }],
  rentalMonths: [{ required: true, message: '请选择租赁月数', trigger: 'change' }],
  rentalStartDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

// 计算属性
const subtotal = computed(() => {
  const rent = parseFloat(selectedSpaceRent.value) || 0
  const months = applyForm.rentalMonths || 1
  return (rent * months).toFixed(2)
})

const totalAmount = computed(() => {
  return Math.max(0, parseFloat(subtotal.value) - (applyForm.discount || 0)).toFixed(2)
})

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '待付款',
    2: '进行中',
    3: '已到期',
    4: '已终止'
  }
  return statusMap[status] || '未知'
}

// 获取申请状态名称
const getApplicationStatusName = (status) => {
  const statusMap = {
    1: '待审核',
    2: '已通过',
    3: '已驳回'
  }
  return statusMap[status] || '未知'
}

// 获取申请状态颜色
const getApplicationStatusColor = (status) => {
  const colorMap = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取状态颜色
const getStatusColor = (status) => {
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
  return new Date(date).toLocaleDateString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 加载合同列表
const loadContracts = async () => {
  loading.value = true
  try {
    const response = await getMyContracts()
    if (response.code === 200) {
      allContracts.value = response.data || []
      applyLocalFilter()
    } else {
      ElMessage.error(response.msg || '加载车位信息失败')
    }
  } catch (error) {
    console.error('加载车位信息错误:', error)
    ElMessage.error('加载车位信息失败')
  } finally {
    loading.value = false
  }
}

// 加载可用车位
const loadAvailableSpaces = async () => {
  try {
    const response = await getAvailableSpaces()
    if (response.code === 200) {
      availableSpaces.value = response.data || []
    }
  } catch (error) {
    console.error('加载可用车位错误:', error)
  }
}

// 加载申请列表
const loadApplications = async () => {
  loading.value = true
  try {
    const response = await getMyApplications()
    if (response.code === 200) {
      applicationList.value = response.data || []
    }
  } catch (error) {
    console.error('加载申请记录错误:', error)
    ElMessage.error('加载申请记录失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'contracts') {
    loadContracts()
  } else if (tabName === 'applications') {
    loadApplications()
  }
}

// 本地过滤
const applyLocalFilter = () => {
  let filtered = [...allContracts.value]

  if (searchForm.contractNo) {
    filtered = filtered.filter(item =>
      item.contractNo && item.contractNo.includes(searchForm.contractNo)
    )
  }

  if (searchForm.spaceNo) {
    filtered = filtered.filter(item =>
      item.spaceNo && item.spaceNo.includes(searchForm.spaceNo)
    )
  }

  if (searchForm.contractStatus !== null && searchForm.contractStatus !== undefined && searchForm.contractStatus !== '') {
    filtered = filtered.filter(item => item.contractStatus === searchForm.contractStatus)
  }

  contractList.value = filtered
}

// 搜索
const handleSearch = () => {
  applyLocalFilter()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    contractNo: '',
    spaceNo: '',
    contractStatus: null
  })
  applyLocalFilter()
}

// 查看详情
const handleView = (row) => {
  currentContract.value = row
  detailVisible.value = true
}

// 申请车位
const handleApply = async () => {
  applyVisible.value = true
  await loadAvailableSpaces()
}

// 车位选择变化
const handleSpaceChange = (spaceId) => {
  const space = availableSpaces.value.find(s => s.id === spaceId)
  if (space) {
    applyForm.spaceNo = space.spaceNo
    applyForm.monthlyRent = space.monthlyRent
    selectedSpaceRent.value = space.monthlyRent
  }
}

// 计算总金额
const calculateTotal = () => {
  // 触发计算属性重新计算
}

// 提交申请
const submitApply = async () => {
  if (!applyFormRef.value) return

  try {
    await applyFormRef.value.validate()
    submitLoading.value = true

    const response = await submitMyApplication(applyForm)
    if (response.code === 200) {
      ElMessage.success('申请提交成功，请等待管理员审核')
      applyVisible.value = false
      // 重置表单
      applyFormRef.value.resetFields()
      Object.assign(applyForm, {
        parkingSpaceId: null,
        spaceNo: '',
        contactPhone: '',
        vehicleNumber: '',
        vehicleBrand: '',
        vehicleColor: '',
        monthlyRent: 0,
        rentalMonths: 12,
        rentalStartDate: '',
        discount: 0,
        remark: ''
      })
      selectedSpaceRent.value = '0'
      // 切换到申请列表标签页并刷新
      activeTab.value = 'applications'
      await loadApplications()
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

// 初始化
onMounted(() => {
  if (activeTab.value === 'contracts') {
    loadContracts()
  } else {
    loadApplications()
  }
})
</script>

<style lang="scss" scoped>
.parking-container {
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
.table-section {
  margin-bottom: 20px;
}

.table-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.fee-summary {
  width: 100%;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;

  .fee-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;

    &:last-child {
      margin-bottom: 0;
    }

    &.total {
      margin-top: 10px;
      padding-top: 10px;
      border-top: 1px solid #dcdfe6;
      font-weight: bold;
      font-size: 16px;
    }

    .fee-value {
      font-weight: 500;

      &.discount {
        color: #67c23a;
      }
    }
  }
}
</style>
