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
        <el-form-item label="投诉单号" prop="complaintNo">
          <el-input
            v-model="searchForm.complaintNo"
            placeholder="请输入投诉单号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="投诉人" prop="complainant">
          <el-input
            v-model="searchForm.complainant"
            placeholder="请输入投诉人"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="投诉类型" prop="complaintType">
          <el-select
            v-model="searchForm.complaintType"
            placeholder="请选择投诉类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in complaintTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="投诉状态" prop="complaintStatus">
          <el-select
            v-model="searchForm.complaintStatus"
            placeholder="请选择投诉状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in complaintStatusOptions"
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
          <span>投诉列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:complaint:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增投诉
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

        <!-- 投诉状态列 -->
        <template #complaintStatus="{ row }">
          <el-tag :type="getStatusTag(row.complaintStatus)">
            {{ getStatusName(row.complaintStatus) }}
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
            v-permission="'property:complaint:assign'"
            v-if="row.complaintStatus === 0"
            @click="handleAssign(row)"
          >
            分配
          </el-button>
          <el-button
            link
            type="success"
            v-permission="'property:complaint:handle'"
            v-if="row.complaintStatus === 1"
            @click="handleProcess(row)"
          >
            处理
          </el-button>
          <el-button
            link
            type="info"
            v-permission="'property:complaint:rate'"
            v-if="row.complaintStatus === 2"
            @click="handleRate(row)"
          >
            评价
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 新增/编辑投诉对话框 -->
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

    <!-- 投诉详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="投诉详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="投诉单号">
          {{ currentComplaint.complaintNo }}
        </el-descriptions-item>
        <el-descriptions-item label="投诉人">
          {{ currentComplaint.complainant }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentComplaint.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="房间编号">
          {{ currentComplaint.houseCode }}
        </el-descriptions-item>
        <el-descriptions-item label="投诉类型">
          <el-tag :type="getTypeTag(currentComplaint.complaintType)">
            {{ getTypeName(currentComplaint.complaintType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyTag(currentComplaint.urgencyLevel)">
            {{ getUrgencyName(currentComplaint.urgencyLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投诉状态">
          <el-tag :type="getStatusTag(currentComplaint.complaintStatus)">
            {{ getStatusName(currentComplaint.complaintStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投诉时间">
          {{ currentComplaint.complaintTime }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="currentComplaint.handlerName">
          {{ currentComplaint.handlerName }}
        </el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="currentComplaint.handleTime">
          {{ currentComplaint.handleTime }}
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>投诉内容</h4>
        <p>{{ currentComplaint.content }}</p>
      </div>

      <div style="margin-top: 20px;" v-if="currentComplaint.images && currentComplaint.images.length > 0">
        <h4>相关图片</h4>
        <el-image
          v-for="(image, index) in currentComplaint.images"
          :key="index"
          :src="image"
          style="width: 100px; height: 100px; margin-right: 10px;"
          fit="cover"
          :preview-src-list="currentComplaint.images"
        />
      </div>

      <div style="margin-top: 20px;" v-if="currentComplaint.handleContent">
        <h4>处理结果</h4>
        <p>{{ currentComplaint.handleContent }}</p>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配处理人对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配处理人"
      width="500px"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="投诉单号">
          <el-input v-model="assignForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉人">
          <el-input v-model="assignForm.complainant" disabled />
        </el-form-item>
        <el-form-item label="处理人" required>
          <el-select v-model="assignForm.handlerId" placeholder="请选择处理人" style="width: 200px">
            <el-option
              v-for="item in handlerOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
            确定分配
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
  complaintNo: '',
  complainant: '',
  complaintType: '',
  complaintStatus: ''
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
    prop: 'complaintNo',
    label: '投诉单号',
    width: '160',
    sortable: true
  },
  {
    prop: 'complainant',
    label: '投诉人',
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
    prop: 'complaintType',
    label: '投诉类型',
    width: '120',
    formatter: (row) => getTypeName(row.complaintType)
  },
  {
    prop: 'urgencyLevel',
    label: '紧急程度',
    width: '100',
    slot: 'urgencyLevel'
  },
  {
    prop: 'complaintStatus',
    label: '投诉状态',
    width: '100',
    slot: 'complaintStatus'
  },
  {
    prop: 'handlerName',
    label: '处理人',
    width: '120'
  },
  {
    prop: 'complaintTime',
    label: '投诉时间',
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
const complaintTypeOptions = ref([
  { label: '设施维修', value: 'facility' },
  { label: '环境卫生', value: 'environment' },
  { label: '噪音扰民', value: 'noise' },
  { label: '安全隐患', value: 'safety' },
  { label: '服务态度', value: 'service' },
  { label: '费用争议', value: 'fee' },
  { label: '其他问题', value: 'other' }
])

const urgencyLevelOptions = ref([
  { label: '一般', value: 1 },
  { label: '紧急', value: 2 },
  { label: '特急', value: 3 }
])

const complaintStatusOptions = ref([
  { label: '待处理', value: 0 },
  { label: '处理中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已关闭', value: 3 }
])

const handlerOptions = ref([
  { label: '维修工张师傅', value: 1 },
  { label: '维修工李师傅', value: 2 },
  { label: '维修工王师傅', value: 3 },
  { label: '物业经理', value: 4 }
])

// 表单数据
const form = reactive({
  complaintId: null,
  complainant: '',
  phone: '',
  houseCode: '',
  complaintType: '',
  urgencyLevel: 1,
  content: '',
  images: []
})

// 分配表单
const assignForm = reactive({
  complaintId: null,
  complaintNo: '',
  complainant: '',
  handlerId: '',
  remark: ''
})

// 当前投诉
const currentComplaint = ref({})

// 表单规则
const formRules = {
  complainant: [
    { required: true, message: '请输入投诉人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  houseCode: [
    { required: true, message: '请输入房间编号', trigger: 'blur' }
  ],
  complaintType: [
    { required: true, message: '请选择投诉类型', trigger: 'change' }
  ],
  urgencyLevel: [
    { required: true, message: '请选择紧急程度', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入投诉内容', trigger: 'blur' },
    { min: 10, max: 500, message: '投诉内容长度在10到500个字符', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'complainant',
    label: '投诉人',
    type: 'input',
    placeholder: '请输入投诉人姓名'
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
    prop: 'complaintType',
    label: '投诉类型',
    type: 'select',
    options: complaintTypeOptions.value,
    placeholder: '请选择投诉类型'
  },
  {
    prop: 'urgencyLevel',
    label: '紧急程度',
    type: 'radio',
    options: urgencyLevelOptions.value
  },
  {
    prop: 'content',
    label: '投诉内容',
    type: 'textarea',
    placeholder: '请详细描述投诉内容',
    rows: 4
  },
  {
    prop: 'images',
    label: '相关图片',
    type: 'upload',
    uploadType: 'image',
    multiple: true,
    limit: 5,
    placeholder: '请上传相关图片（最多5张）'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑投诉' : '新增投诉')

// 获取投诉类型名称
const getTypeName = (type) => {
  const option = complaintTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 获取投诉类型标签
const getTypeTag = (type) => {
  const tagMap = {
    'facility': 'primary',
    'environment': 'success',
    'noise': 'warning',
    'safety': 'danger',
    'service': 'info',
    'fee': 'warning',
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
  const option = complaintStatusOptions.value.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 待处理
    1: 'primary', // 处理中
    2: 'success', // 已完成
    3: 'info'     // 已关闭
  }
  return tagMap[status] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockComplaints = []
  const complainants = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const types = ['facility', 'environment', 'noise', 'safety', 'service', 'fee', 'other']
  const statuses = [0, 1, 2, 3]
  const urgencyLevels = [1, 2, 3]

  for (let i = 0; i < 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const urgency = urgencyLevels[Math.floor(Math.random() * urgencyLevels.length)]

    mockComplaints.push({
      complaintId: i + 1,
      complaintNo: `COMP${(i + 1).toString().padStart(6, '0')}`,
      complainant: complainants[i % complainants.length],
      phone: '138****' + Math.floor(Math.random() * 10000).toString().padStart(4, '0'),
      houseCode: `H${Math.floor(Math.random() * 4 + 1)}${Math.floor(Math.random() * 18 + 1)}${Math.floor(Math.random() * 3 + 1)}`,
      complaintType: type,
      urgencyLevel: urgency,
      complaintStatus: status,
      content: '详细描述投诉内容和相关问题',
      images: Math.random() > 0.5 ? [`https://picsum.photos/200/200?random=${i}`] : [],
      handlerName: status > 0 ? handlerOptions.value[Math.floor(Math.random() * handlerOptions.value.length)].label : '',
      handleTime: status > 0 ? '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' 10:00:00' : '',
      handleContent: status === 2 ? '问题已处理完成，业主表示满意' : '',
      complaintTime: '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' ' +
                     Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' +
                     Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':00'
    })
  }

  // 模拟分页
  pagination.total = mockComplaints.length
  return mockComplaints
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
  currentComplaint.value = { ...row }
  detailDialogVisible.value = true
}

// 分配处理人
const handleAssign = (row) => {
  Object.assign(assignForm, {
    complaintId: row.complaintId,
    complaintNo: row.complaintNo,
    complainant: row.complainant,
    handlerId: '',
    remark: ''
  })
  assignDialogVisible.value = true
}

// 提交分配
const handleAssignSubmit = async () => {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择处理人')
    return
  }

  assignLoading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('分配成功')
      assignDialogVisible.value = false
      fetchData()
      assignLoading.value = false
    }, 1000)
  } catch (error) {
    assignLoading.value = false
  }
}

// 处理投诉
const handleProcess = (row) => {
  ElMessage.info(`处理投诉单 ${row.complaintNo}`)
}

// 评价投诉
const handleRate = (row) => {
  ElMessage.info(`评价投诉单 ${row.complaintNo}`)
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
    complaintId: null,
    complainant: '',
    phone: '',
    houseCode: '',
    complaintType: '',
    urgencyLevel: 1,
    content: '',
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