<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">投诉管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>服务管理</el-breadcrumb-item>
        <el-breadcrumb-item>投诉管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="投诉单号">
          <el-input
            v-model="searchForm.complaintNo"
            placeholder="请输入投诉单号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="投诉人">
          <el-input
            v-model="searchForm.complainant"
            placeholder="请输入投诉人"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="投诉类型">
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
        <el-form-item label="投诉状态">
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
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

    <!-- 投诉表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
      >
        <el-table-column prop="complaintNo" label="投诉单号" width="160" sortable />
        <el-table-column prop="complainant" label="投诉人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="houseCode" label="房间编号" width="140" />
        <el-table-column prop="complaintType" label="投诉类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.complaintType)">
              {{ getTypeName(row.complaintType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyTag(row.urgencyLevel)">
              {{ getUrgencyName(row.urgencyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="complaintStatus" label="投诉状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.complaintStatus)">
              {{ getStatusName(row.complaintStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="120" />
        <el-table-column prop="complaintTime" label="投诉时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.complaintTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              v-if="row.complaintStatus === 0"
              link
              type="warning"
              @click="handleAssign(row)"
            >
              分配
            </el-button>
            <el-button
              v-if="row.complaintStatus === 1"
              link
              type="success"
              @click="handleProcess(row)"
            >
              处理
            </el-button>
            <el-button
              v-if="row.complaintStatus === 2"
              link
              type="info"
              @click="handleRate(row)"
            >
              评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 新增/编辑投诉对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="投诉人" prop="complainant">
          <el-input v-model="form.complainant" placeholder="请输入投诉人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="房间编号" prop="houseCode">
          <el-input v-model="form.houseCode" placeholder="请输入房间编号" />
        </el-form-item>
        <el-form-item label="投诉类型" prop="complaintType">
          <el-select v-model="form.complaintType" placeholder="请选择投诉类型" style="width: 100%">
            <el-option
              v-for="item in complaintTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgencyLevel">
          <el-radio-group v-model="form.urgencyLevel">
            <el-radio
              v-for="item in urgencyLevelOptions"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="投诉内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="请详细描述投诉内容"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="相关图片">
          <el-upload
            v-model:file-list="form.images"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
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
          {{ formatDateTime(currentComplaint.complaintTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="currentComplaint.handlerName">
          {{ currentComplaint.handlerName }}
        </el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="currentComplaint.handleTime">
          {{ formatDateTime(currentComplaint.handleTime) }}
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
        <el-button @click="detailDialogVisible = false">关闭</el-button>
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
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="assignLoading"
          @click="handleAssignSubmit"
        >
          确定分配
        </el-button>
      </template>
    </el-dialog>

    <!-- 处理投诉对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      title="处理投诉"
      width="600px"
    >
      <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="100px">
        <el-form-item label="投诉单号">
          <el-input v-model="processForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉人">
          <el-input v-model="processForm.complainant" disabled />
        </el-form-item>
        <el-form-item label="处理措施" prop="handleContent">
          <el-input
            v-model="processForm.handleContent"
            type="textarea"
            placeholder="请详细描述处理措施和结果"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="处理照片">
          <el-upload
            v-model:file-list="processForm.handleImages"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="processLoading"
          @click="handleProcessSubmit"
        >
          确认处理
        </el-button>
      </template>
    </el-dialog>

    <!-- 评价投诉对话框 -->
    <el-dialog
      v-model="rateDialogVisible"
      title="投诉评价"
      width="500px"
    >
      <el-form :model="rateForm" :rules="rateRules" ref="rateFormRef" label-width="100px">
        <el-form-item label="投诉单号">
          <el-input v-model="rateForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="处理结果">
          <p>{{ rateForm.handleContent }}</p>
        </el-form-item>
        <el-form-item label="满意度评价" prop="rating">
          <el-rate v-model="rateForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价等级" prop="satisfaction">
          <el-radio-group v-model="rateForm.satisfaction">
            <el-radio :label="1">满意</el-radio>
            <el-radio :label="2">一般</el-radio>
            <el-radio :label="3">不满意</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评价意见">
          <el-input
            v-model="rateForm.comment"
            type="textarea"
            placeholder="请输入您的评价意见（可选）"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="rateLoading"
          @click="handleRateSubmit"
        >
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Download } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const processFormRef = ref()
const rateFormRef = ref()
const loading = ref(false)
const assignLoading = ref(false)
const processLoading = ref(false)
const rateLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const processDialogVisible = ref(false)
const rateDialogVisible = ref(false)
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
  { label: '已处理', value: 2 },
  { label: '已关闭', value: 3 }
])

const handlerOptions = ref([
  { label: '物业管家-张三', value: 1 },
  { label: '物业管家-李四', value: 2 },
  { label: '物业管理员-王五', value: 3 },
  { label: '物业经理-赵六', value: 4 }
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

// 处理表单
const processForm = reactive({
  complaintId: null,
  complaintNo: '',
  complainant: '',
  handleContent: '',
  handleImages: []
})

// 评价表单
const rateForm = reactive({
  complaintId: null,
  complaintNo: '',
  handleContent: '',
  rating: 5,
  satisfaction: 1,
  comment: ''
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

// 处理表单规则
const processRules = {
  handleContent: [
    { required: true, message: '请输入处理措施', trigger: 'blur' },
    { min: 10, max: 500, message: '处理措施长度在10到500个字符', trigger: 'blur' }
  ]
}

// 评价表单规则
const rateRules = {
  rating: [
    { required: true, message: '请选择满意度评分', trigger: 'change' }
  ],
  satisfaction: [
    { required: true, message: '请选择评价等级', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑投诉' : '新增投诉')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

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

// 生成模拟数据
const generateMockData = () => {
  const complaints = []
  const complainants = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const types = ['facility', 'environment', 'noise', 'safety', 'service', 'fee', 'other']
  const statuses = [0, 1, 2, 3]
  const urgencyLevels = [1, 2, 3]

  for (let i = 0; i < 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const urgency = urgencyLevels[Math.floor(Math.random() * urgencyLevels.length)]

    complaints.push({
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
      handleTime: status > 0 ? new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString() : '',
      handleContent: status === 2 ? '问题已处理完成，业主表示满意' : '',
      complaintTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
    })
  }

  return complaints
}

// 加载投诉数据
const loadComplaints = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockData()
    tableData.value = mockData.slice(
      (pagination.current - 1) * pagination.pageSize,
      pagination.current * pagination.pageSize
    )
    pagination.total = mockData.length
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadComplaints()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    complaintNo: '',
    complainant: '',
    complaintType: '',
    complaintStatus: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
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
const handleAssignSubmit = () => {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择处理人')
    return
  }

  assignLoading.value = true
  setTimeout(() => {
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadComplaints()
    assignLoading.value = false
  }, 1000)
}

// 处理投诉
const handleProcess = (row) => {
  Object.assign(processForm, {
    complaintId: row.complaintId,
    complaintNo: row.complaintNo,
    complainant: row.complainant,
    handleContent: '',
    handleImages: []
  })
  processDialogVisible.value = true
}

// 评价投诉
const handleRate = (row) => {
  Object.assign(rateForm, {
    complaintId: row.complaintId,
    complaintNo: row.complaintNo,
    handleContent: row.handleContent || '暂无处理结果',
    rating: 5,
    satisfaction: 1,
    comment: ''
  })
  rateDialogVisible.value = true
}

// 提交处理
const handleProcessSubmit = async () => {
  if (!processFormRef.value) return

  try {
    await processFormRef.value.validate()
    processLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('处理完成')
      processDialogVisible.value = false
      loadComplaints()
      processLoading.value = false
    }, 1000)
  } catch (error) {
    processLoading.value = false
  }
}

// 提交评价
const handleRateSubmit = async () => {
  if (!rateFormRef.value) return

  try {
    await rateFormRef.value.validate()
    rateLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('评价提交成功')
      rateDialogVisible.value = false
      loadComplaints()
      rateLoading.value = false
    }, 1000)
  } catch (error) {
    rateLoading.value = false
  }
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadComplaints()
    }
  })
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadComplaints()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadComplaints()
}

// 初始化
onMounted(() => {
  loadComplaints()
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