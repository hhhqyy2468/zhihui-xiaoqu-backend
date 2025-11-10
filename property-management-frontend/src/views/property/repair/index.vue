<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
        class="search-form"
      >
        <el-form-item label="工单编号" prop="orderNo">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入工单编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="报修人" prop="reporter">
          <el-input
            v-model="searchForm.reporter"
            placeholder="请输入报修人"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="维修类型" prop="repairType">
          <el-select
            v-model="searchForm.repairType"
            placeholder="请选择维修类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in repairTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="工单状态" prop="orderStatus">
          <el-select
            v-model="searchForm.orderStatus"
            placeholder="请选择工单状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in orderStatusOptions"
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
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>维修工单列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:repair:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增工单
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>

      <Table
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @sort-change="handleSortChange"
      >
        <!-- 紧急程度列 -->
        <template #urgencyLevel="{ row }">
          <el-tag :type="getUrgencyTag(row.urgencyLevel)">
            {{ getUrgencyName(row.urgencyLevel) }}
          </el-tag>
        </template>

        <!-- 工单状态列 -->
        <template #orderStatus="{ row }">
          <el-tag :type="getStatusTag(row.orderStatus)">
            {{ getStatusName(row.orderStatus) }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            @click="handleViewDetail(row)"
          >
            详情
          </el-button>
          <el-button
            link
            type="warning"
            v-permission="'property:repair:assign'"
            v-if="row.orderStatus === 0"
            @click="handleAssign(row)"
          >
            派工
          </el-button>
          <el-button
            link
            type="success"
            v-permission="'property:repair:handle'"
            v-if="row.orderStatus === 1"
            @click="handleProcess(row)"
          >
            处理
          </el-button>
          <el-button
            link
            type="info"
            v-permission="'property:repair:accept'"
            v-if="row.orderStatus === 2"
            @click="handleAccept(row)"
          >
            验收
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 新增/编辑工单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
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

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="工单详情"
      width="900px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">
          {{ currentOrder.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="报修人">
          {{ currentOrder.reporter }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentOrder.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="房间编号">
          {{ currentOrder.houseCode }}
        </el-descriptions-item>
        <el-descriptions-item label="维修类型">
          <el-tag :type="getTypeTag(currentOrder.repairType)">
            {{ getTypeName(currentOrder.repairType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyTag(currentOrder.urgencyLevel)">
            {{ getUrgencyName(currentOrder.urgencyLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工单状态">
          <el-tag :type="getStatusTag(currentOrder.orderStatus)">
            {{ getStatusName(currentOrder.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">
          {{ currentOrder.reportTime }}
        </el-descriptions-item>
        <el-descriptions-item label="维修人员" v-if="currentOrder.repairerName">
          {{ currentOrder.repairerName }}
        </el-descriptions-item>
        <el-descriptions-item label="派工时间" v-if="currentOrder.assignTime">
          {{ currentOrder.assignTime }}
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>故障描述</h4>
        <p>{{ currentOrder.description }}</p>
      </div>

      <div style="margin-top: 20px;" v-if="currentOrder.images && currentOrder.images.length > 0">
        <h4>故障图片</h4>
        <el-image
          v-for="(image, index) in currentOrder.images"
          :key="index"
          :src="image"
          style="width: 100px; height: 100px; margin-right: 10px;"
          fit="cover"
          :preview-src-list="currentOrder.images"
        />
      </div>

      <div style="margin-top: 20px;" v-if="currentOrder.repairContent">
        <h4>维修记录</h4>
        <p>{{ currentOrder.repairContent }}</p>
      </div>

      <div style="margin-top: 20px;" v-if="currentOrder.acceptanceResult">
        <h4>验收结果</h4>
        <el-rate
          v-model="currentOrder.rating"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value} 分"
        />
        <p style="margin-top: 10px;">{{ currentOrder.acceptanceResult }}</p>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 派工对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="派工"
      width="500px"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="工单编号">
          <el-input v-model="assignForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="报修人">
          <el-input v-model="assignForm.reporter" disabled />
        </el-form-item>
        <el-form-item label="维修人员" required>
          <el-select v-model="assignForm.repairerId" placeholder="请选择维修人员" style="width: 200px">
            <el-option
              v-for="item in repairerOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预计完成时间" required>
          <el-date-picker
            v-model="assignForm.expectedCompleteTime"
            type="datetime"
            placeholder="请选择预计完成时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="assignForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="assignLoading"
            @click="handleAssignSubmit"
          >
            确定派工
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
const loading = ref(false)
const submitLoading = ref(false)
const assignLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const isEdit = ref(false)

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  reporter: '',
  repairType: '',
  orderStatus: ''
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
    prop: 'orderNo',
    label: '工单编号',
    width: '160',
    sortable: true
  },
  {
    prop: 'reporter',
    label: '报修人',
    width: '120'
  },
  {
    prop: 'phone',
    label: '联系电话',
    width: '130'
  },
  {
    prop: 'houseCode',
    label: '房间编号',
    width: '140'
  },
  {
    prop: 'repairType',
    label: '维修类型',
    width: '120',
    formatter: (row) => getTypeName(row.repairType)
  },
  {
    prop: 'urgencyLevel',
    label: '紧急程度',
    width: '100',
    slot: 'urgencyLevel'
  },
  {
    prop: 'orderStatus',
    label: '工单状态',
    width: '100',
    slot: 'orderStatus'
  },
  {
    prop: 'repairerName',
    label: '维修人员',
    width: '120'
  },
  {
    prop: 'reportTime',
    label: '报修时间',
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
const repairTypeOptions = ref([
  { label: '水电维修', value: 'water_electric' },
  { label: '门窗维修', value: 'door_window' },
  { label: '家电维修', value: 'appliance' },
  { label: '管道维修', value: 'plumbing' },
  { label: '油漆粉刷', value: 'painting' },
  { label: '其他维修', value: 'other' }
])

const urgencyLevelOptions = ref([
  { label: '一般', value: 1 },
  { label: '紧急', value: 2 },
  { label: '特急', value: 3 }
])

const orderStatusOptions = ref([
  { label: '待派工', value: 0 },
  { label: '待处理', value: 1 },
  { label: '处理中', value: 2 },
  { label: '待验收', value: 3 },
  { label: '已完成', value: 4 },
  { label: '已关闭', value: 5 }
])

const repairerOptions = ref([
  { label: '维修工张师傅', value: 1 },
  { label: '维修工李师傅', value: 2 },
  { label: '维修工王师傅', value: 3 },
  { label: '维修工赵师傅', value: 4 }
])

// 表单数据
const form = reactive({
  orderId: null,
  reporter: '',
  phone: '',
  houseCode: '',
  repairType: '',
  urgencyLevel: 1,
  description: '',
  images: []
})

// 派工表单
const assignForm = reactive({
  orderId: null,
  orderNo: '',
  reporter: '',
  repairerId: '',
  expectedCompleteTime: '',
  remark: ''
})

// 当前工单
const currentOrder = ref({})

// 表单规则
const formRules = {
  reporter: [
    { required: true, message: '请输入报修人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  houseCode: [
    { required: true, message: '请输入房间编号', trigger: 'blur' }
  ],
  repairType: [
    { required: true, message: '请选择维修类型', trigger: 'change' }
  ],
  urgencyLevel: [
    { required: true, message: '请选择紧急程度', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入故障描述', trigger: 'blur' },
    { min: 10, max: 500, message: '故障描述长度在10到500个字符', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'reporter',
    label: '报修人',
    type: 'input',
    placeholder: '请输入报修人姓名'
  },
  {
    prop: 'phone',
    label: '联系电话',
    type: 'input',
    placeholder: '请输入联系电话'
  },
  {
    prop: 'houseCode',
    label: '房间编号',
    type: 'input',
    placeholder: '请输入房间编号'
  },
  {
    prop: 'repairType',
    label: '维修类型',
    type: 'select',
    options: repairTypeOptions.value,
    placeholder: '请选择维修类型'
  },
  {
    prop: 'urgencyLevel',
    label: '紧急程度',
    type: 'radio',
    options: urgencyLevelOptions.value
  },
  {
    prop: 'description',
    label: '故障描述',
    type: 'textarea',
    placeholder: '请详细描述故障情况',
    rows: 4
  },
  {
    prop: 'images',
    label: '故障图片',
    type: 'upload',
    uploadType: 'image',
    multiple: true,
    limit: 5,
    placeholder: '请上传故障图片（最多5张）'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑工单' : '新增工单')

// 获取维修类型名称
const getTypeName = (type) => {
  const option = repairTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 获取维修类型标签
const getTypeTag = (type) => {
  const tagMap = {
    'water_electric': 'primary',
    'door_window': 'success',
    'appliance': 'warning',
    'plumbing': 'danger',
    'painting': 'info',
    'other': 'info'
  }
  return tagMap[type] || 'info'
}

// 获取紧急程度名称
const getUrgencyName = (level) => {
  const option = urgencyLevelOptions.value.find(item => item.value === level)
  return option ? option.label : '未知'
}

// 获取紧急程度标签
const getUrgencyTag = (level) => {
  const tagMap = {
    1: 'info',    // 一般
    2: 'warning', // 紧急
    3: 'danger'   // 特急
  }
  return tagMap[level] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const option = orderStatusOptions.value.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 待派工
    1: 'primary', // 待处理
    2: 'success', // 处理中
    3: 'info',    // 待验收
    4: 'success', // 已完成
    5: 'info'     // 已关闭
  }
  return tagMap[status] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockOrders = []
  const reporters = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const types = ['water_electric', 'door_window', 'appliance', 'plumbing', 'painting', 'other']
  const statuses = [0, 1, 2, 3, 4, 5]
  const urgencyLevels = [1, 2, 3]

  for (let i = 0; i < 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const urgency = urgencyLevels[Math.floor(Math.random() * urgencyLevels.length)]

    mockOrders.push({
      orderId: i + 1,
      orderNo: `REP${(i + 1).toString().padStart(6, '0')}`,
      reporter: reporters[i % reporters.length],
      phone: '138****' + Math.floor(Math.random() * 10000).toString().padStart(4, '0'),
      houseCode: `H${Math.floor(Math.random() * 4 + 1)}${Math.floor(Math.random() * 18 + 1)}${Math.floor(Math.random() * 3 + 1)}`,
      repairType: type,
      urgencyLevel: urgency,
      orderStatus: status,
      description: '详细描述故障情况和需要维修的内容',
      images: Math.random() > 0.6 ? [`https://picsum.photos/200/200?random=${i}`] : [],
      repairerName: status > 0 ? repairerOptions.value[Math.floor(Math.random() * repairerOptions.value.length)].label : '',
      assignTime: status > 0 ? '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' 10:00:00' : '',
      repairContent: status >= 3 ? '维修已完成，问题解决' : '',
      acceptanceResult: status === 4 ? '维修质量很好，服务态度满意' : '',
      rating: status === 4 ? 4 + Math.floor(Math.random() * 2) : 0,
      reportTime: '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' ' +
                  Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' +
                  Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':00'
    })
  }

  // 模拟分页
  pagination.total = mockOrders.length
  return mockOrders
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

// 查看详情
const handleViewDetail = (row) => {
  currentOrder.value = { ...row }
  detailDialogVisible.value = true
}

// 派工
const handleAssign = (row) => {
  Object.assign(assignForm, {
    orderId: row.orderId,
    orderNo: row.orderNo,
    reporter: row.reporter,
    repairerId: '',
    expectedCompleteTime: '',
    remark: ''
  })
  assignDialogVisible.value = true
}

// 提交派工
const handleAssignSubmit = async () => {
  if (!assignForm.repairerId || !assignForm.expectedCompleteTime) {
    ElMessage.warning('请填写完整的派工信息')
    return
  }

  assignLoading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('派工成功')
      assignDialogVisible.value = false
      fetchData()
      assignLoading.value = false
    }, 1000)
  } catch (error) {
    assignLoading.value = false
  }
}

// 处理工单
const handleProcess = (row) => {
  ElMessage.info(`处理工单 ${row.orderNo}`)
}

// 验收工单
const handleAccept = (row) => {
  ElMessage.info(`验收工单 ${row.orderNo}`)
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
    orderId: null,
    reporter: '',
    phone: '',
    houseCode: '',
    repairType: '',
    urgencyLevel: 1,
    description: '',
    images: []
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
.app-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
}
</style>