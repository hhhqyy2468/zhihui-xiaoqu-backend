<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">停车管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>停车管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
      >
        <el-form-item label="车位编号" prop="parkingNo">
          <el-input
            v-model="searchForm.parkingNo"
            placeholder="请输入车位编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="位置区域" prop="location">
          <el-input
            v-model="searchForm.location"
            placeholder="请输入位置区域"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="车位类型" prop="parkingType">
          <el-select
            v-model="searchForm.parkingType"
            placeholder="请选择车位类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in parkingTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="租赁状态" prop="rentalStatus">
          <el-select
            v-model="searchForm.rentalStatus"
            placeholder="请选择租赁状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in rentalStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增车位
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <!-- 车位表格 -->
    <div class="table-section">

      <Table
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @sort-change="handleSortChange"
      >
        <!-- 月租金列 -->
        <template #monthlyRent="{ row }">
          <span class="price-text">¥{{ row.monthlyRent.toFixed(2) }}</span>
        </template>

        <!-- 车赁状态列 -->
        <template #rentalStatus="{ row }">
          <el-tag :type="getRentalStatusTag(row.rentalStatus)">
            {{ getRentalStatusName(row.rentalStatus) }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'property:parking:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="success"
            v-permission="'property:parking:rent'"
            v-if="row.rentalStatus === 0"
            @click="handleRent(row)"
          >
            出租
          </el-button>
          <el-button
            link
            type="info"
            v-permission="'property:parking:renew'"
            v-if="row.rentalStatus === 1"
            @click="handleRenew(row)"
          >
            续租
          </el-button>
          <el-button
            link
            type="warning"
            v-permission="'property:parking:return'"
            v-if="row.rentalStatus === 1"
            @click="handleReturn(row)"
          >
            退租
          </el-button>
        </template>
      </Table>

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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <Form
        ref="formRef"
        :model="form"
        :rules="formRules"
        :items="formItems"
        label-width="100px"
      />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出租对话框 -->
    <el-dialog
      v-model="rentDialogVisible"
      title="车位出租"
      width="600px"
    >
      <el-form :model="rentForm" :rules="rentRules" ref="rentFormRef" label-width="100px">
        <el-form-item label="车位编号">
          <el-input v-model="rentForm.parkingNo" disabled />
        </el-form-item>
        <el-form-item label="车位位置">
          <el-input v-model="rentForm.location" disabled />
        </el-form-item>
        <el-form-item label="业主姓名" prop="ownerName">
          <el-input v-model="rentForm.ownerName" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="rentForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="车牌号" prop="plateNumber">
          <el-input v-model="rentForm.plateNumber" placeholder="请输入车牌号" />
        </el-form-item>
        <el-form-item label="月租金" prop="monthlyRent">
          <el-input-number
            v-model="rentForm.monthlyRent"
            :min="0"
            :precision="2"
            style="width: 200px"
          />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        <el-form-item label="租赁开始时间" prop="startTime">
          <el-date-picker
            v-model="rentForm.startTime"
            type="date"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="租赁结束时间" prop="endTime">
          <el-date-picker
            v-model="rentForm.endTime"
            type="date"
            placeholder="请选择结束时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="rentForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rentDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="rentLoading"
            @click="handleRentSubmit"
          >
            确认出租
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Download } from '@element-plus/icons-vue'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const rentFormRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const rentLoading = ref(false)
const dialogVisible = ref(false)
const rentDialogVisible = ref(false)
const isEdit = ref(false)

// 搜索表单
const searchForm = reactive({
  parkingNo: '',
  location: '',
  parkingType: '',
  rentalStatus: ''
})

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列配置
const tableColumns = [
  {
    prop: 'parkingNo',
    label: '车位编号',
    width: '120',
    sortable: true
  },
  {
    prop: 'location',
    label: '位置区域',
    width: '200'
  },
  {
    prop: 'parkingType',
    label: '车位类型',
    width: '120',
    formatter: (row) => getParkingTypeName(row.parkingType)
  },
  {
    prop: 'monthlyRent',
    label: '月租金',
    width: '120',
    slot: 'monthlyRent',
    sortable: true
  },
  {
    prop: 'rentalStatus',
    label: '租赁状态',
    width: '100',
    slot: 'rentalStatus'
  },
  {
    prop: 'ownerName',
    label: '业主姓名',
    width: '120'
  },
  {
    prop: 'plateNumber',
    label: '车牌号',
    width: '120'
  },
  {
    prop: 'rentalPeriod',
    label: '租赁期限',
    width: '200',
    formatter: (row) => {
      if (row.rentalStatus === 0) return '-'
      return `${row.startTime} 至 ${row.endTime}`
    }
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'operation',
    label: '操作',
    width: '280',
    slot: 'operation',
    fixed: 'right'
  }
]

// 选项数据
const parkingTypeOptions = ref([
  { label: '地下车位', value: 'underground' },
  { label: '地面车位', value: 'ground' },
  { label: '机械车位', value: 'mechanical' },
  { label: '充电车位', value: 'electric' }
])

const rentalStatusOptions = ref([
  { label: '空闲', value: 0 },
  { label: '已出租', value: 1 },
  { label: '维护中', value: 2 }
])

// 表单数据
const form = reactive({
  parkingId: null,
  parkingNo: '',
  location: '',
  parkingType: 'underground',
  monthlyRent: 0,
  description: '',
  status: 1
})

// 出租表单
const rentForm = reactive({
  parkingId: null,
  parkingNo: '',
  location: '',
  ownerName: '',
  phone: '',
  plateNumber: '',
  monthlyRent: 0,
  startTime: '',
  endTime: '',
  remark: ''
})

// 表单规则
const formRules = {
  parkingNo: [
    { required: true, message: '请输入车位编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '车位编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置区域', trigger: 'blur' }
  ],
  parkingType: [
    { required: true, message: '请选择车位类型', trigger: 'change' }
  ],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { type: 'number', min: 0, message: '月租金必须大于等于0', trigger: 'blur' }
  ]
}

const rentRules = {
  ownerName: [
    { required: true, message: '请输入业主姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  plateNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]$/, message: '请输入正确的车牌号', trigger: 'blur' }
  ],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { type: 'number', min: 0, message: '月租金必须大于等于0', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择租赁开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择租赁结束时间', trigger: 'change' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'parkingNo',
    label: '车位编号',
    type: 'input',
    placeholder: '请输入车位编号（如：B1-001）',
    disabled: isEdit.value
  },
  {
    prop: 'location',
    label: '位置区域',
    type: 'input',
    placeholder: '请输入位置区域（如：B1层东侧）'
  },
  {
    prop: 'parkingType',
    label: '车位类型',
    type: 'select',
    options: parkingTypeOptions.value,
    placeholder: '请选择车位类型'
  },
  {
    prop: 'monthlyRent',
    label: '月租金',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入月租金',
    prepend: '¥'
  },
  {
    prop: 'description',
    label: '车位描述',
    type: 'textarea',
    placeholder: '请输入车位描述'
  },
  {
    prop: 'status',
    label: '车位状态',
    type: 'radio',
    options: [
      { label: '正常', value: 1 },
      { label: '维护中', value: 0 }
    ]
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑车位' : '新增车位')

// 获取车位类型名称
const getParkingTypeName = (type) => {
  const option = parkingTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 获取租赁状态名称
const getRentalStatusName = (status) => {
  const option = rentalStatusOptions.value.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取租赁状态标签
const getRentalStatusTag = (status) => {
  const tagMap = {
    0: 'success', // 空闲
    1: 'warning', // 已出租
    2: 'danger'   // 维护中
  }
  return tagMap[status] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockParkings = []
  const locations = ['B1层东侧', 'B1层西侧', 'B1层中央', 'B2层东侧', 'B2层西侧', '地面A区', '地面B区']
  const types = ['underground', 'ground', 'mechanical', 'electric']
  const statuses = [0, 1, 2]
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const plates = ['京A88888', '京B66666', '沪C55555', '粤D44444', '苏E33333', '浙F22222']

  for (let i = 0; i < 100; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const monthlyRent = 200 + Math.floor(Math.random() * 300)

    mockParkings.push({
      parkingId: i + 1,
      parkingNo: `${type === 'underground' ? 'B' : 'G'}${Math.floor(Math.random() * 3 + 1)}-${(i + 1).toString().padStart(3, '0')}`,
      location: locations[i % locations.length],
      parkingType: type,
      monthlyRent: monthlyRent,
      rentalStatus: status,
      ownerName: status === 1 ? owners[Math.floor(Math.random() * owners.length)] : '',
      phone: status === 1 ? '138****' + Math.floor(Math.random() * 10000).toString().padStart(4, '0') : '',
      plateNumber: status === 1 ? plates[Math.floor(Math.random() * plates.length)] : '',
      startTime: status === 1 ? '2024-01-01' : '',
      endTime: status === 1 ? '2024-12-31' : '',
      rentalPeriod: status === 1 ? '2024-01-01 至 2024-12-31' : '',
      description: type === 'electric' ? '配备充电桩' : '标准车位',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    })
  }

  // 模拟分页
  pagination.total = mockParkings.length
  return mockParkings
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  handleSearch()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      tableData.value = getMockData()
      loading.value = false
    }, 500)
  } catch (error) {
    loading.value = false
    ElMessage.error('获取数据失败')
  }
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page
  fetchData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  pagination.current = val
  fetchData()
}

// 排序变化
const handleSortChange = (sort) => {
  console.log('排序变化:', sort)
  fetchData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 出租
const handleRent = (row) => {
  Object.assign(rentForm, {
    parkingId: row.parkingId,
    parkingNo: row.parkingNo,
    location: row.location,
    ownerName: '',
    phone: '',
    plateNumber: '',
    monthlyRent: row.monthlyRent,
    startTime: '',
    endTime: '',
    remark: ''
  })
  rentDialogVisible.value = true
}

// 提交出租
const handleRentSubmit = async () => {
  if (!rentFormRef.value) return

  try {
    await rentFormRef.value.validate()
    rentLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('出租成功')
      rentDialogVisible.value = false
      fetchData()
      rentLoading.value = false
    }, 1000)
  } catch (error) {
    rentLoading.value = false
  }
}

// 续租
const handleRenew = (row) => {
  ElMessage.info(`续租车位 ${row.parkingNo}`)
}

// 退租
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要退还车位"${row.parkingNo}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('退租成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
      submitLoading.value = false
    }, 1000)
  } catch (error) {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    parkingId: null,
    parkingNo: '',
    location: '',
    parkingType: 'underground',
    monthlyRent: 0,
    description: '',
    status: 1
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 组件挂载
onMounted(() => {
  fetchData()
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

.price-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>