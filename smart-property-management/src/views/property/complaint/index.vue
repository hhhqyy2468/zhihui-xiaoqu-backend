<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ getPageTitle() }}</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ getBreadcrumbParent() }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ getPageTitle() }}</el-breadcrumb-item>
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
            v-model="searchForm.userName"
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
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
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
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入联系电话"
            clearable
            style="width: 150px"
          />
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
        v-if="currentUserRole !== 4"
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        {{ currentUserRole === 3 ? '发起投诉' : '新增投诉' }}
      </el-button>
    </div>

    <!-- 投诉表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
      >
        <el-table-column prop="complaintNo" label="投诉单号" width="160" sortable />
          <el-table-column
          v-if="currentUserRole !== 3"
          prop="userName"
          label="投诉人"
          width="120"
        />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="houseNo" label="房间编号" width="140" />
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
        <el-table-column prop="createTime" label="投诉时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
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
              v-if="row.complaintStatus === 1 && (currentUserRole === 1 || currentUserRole === 2)"
              link
              type="warning"
              @click="handleAssign(row)"
            >
              分配
            </el-button>
            <el-button
              v-if="row.complaintStatus === 2 && (currentUserRole === 1 || currentUserRole === 2)"
              link
              type="success"
              @click="handleProcess(row)"
            >
              处理
            </el-button>
            <el-button
              v-if="row.complaintStatus === 3 && currentUserRole === 3"
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
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
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
        <el-form-item label="投诉内容" prop="complaintContent">
          <el-input
            v-model="form.complaintContent"
            type="textarea"
            placeholder="请详细描述投诉内容"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="相关图片">
          <el-upload
            :file-list="fileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-change="handleFileChange"
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
          {{ formatDateTime(currentComplaint.createTime) }}
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
        <p>{{ currentComplaint.complaintContent }}</p>
      </div>

      <div style="margin-top: 20px;" v-if="currentComplaint.imageUrls && currentComplaint.imageUrls.length > 0">
        <h4>相关图片</h4>
        <el-image
          v-for="(image, index) in getImageList(currentComplaint.imageUrls)"
          :key="index"
          :src="getImageUrl(image)"
          style="width: 100px; height: 100px; margin-right: 10px;"
          fit="cover"
          :preview-src-list="getImageList(currentComplaint.imageUrls).map(url => getImageUrl(url))"
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

// 导入API接口
import {
  getComplaintPage,
  getComplaintDetail,
  createComplaint,
  updateComplaint,
  deleteComplaint,
  assignComplaint,
  handleComplaint,
  rateComplaint,
  getMyComplaints,
  uploadComplaintImage,
  getComplaintTypeDict,
  getComplaintStatusDict
} from '@/api/complaint'

// 响应式数据
const formRef = ref()
const processFormRef = ref()
const rateFormRef = ref()
const loading = ref(false)
const assignLoading = ref(false)
const processLoading = ref(false)
const rateLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const processDialogVisible = ref(false)
const rateDialogVisible = ref(false)
const isEdit = ref(false)

// 用户角色判断
const currentUserRole = computed(() => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const tokenParts = token.split('.')
      if (tokenParts.length >= 2) {
        let payload = tokenParts[1]
        payload = payload.replace(/-/g, '+').replace(/_/g, '/')
        while (payload.length % 4) {
          payload += '='
        }
        const decoded = JSON.parse(atob(payload))
        return decoded.userType || 1
      }
    } catch (error) {
      console.error('解析Token失败:', error)
    }
  }
  return 1
})

// 获取页面标题
const getPageTitle = () => {
  switch (currentUserRole.value) {
    case 1: return '投诉管理'
    case 2: return '投诉管理'
    case 3: return '我的投诉'
    default: return '投诉管理'
  }
}

// 获取面包屑父级标题
const getBreadcrumbParent = () => {
  switch (currentUserRole.value) {
    case 1: return '服务管理'
    case 2: return '服务管理'
    case 3: return '业主门户'
    default: return '服务管理'
  }
}

// 搜索表单
const searchForm = reactive({
  complaintNo: '',
  userName: '',
  houseNo: '',
  complaintType: '',
  urgencyLevel: null,
  complaintStatus: null,
  phone: ''
})

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 字典数据
const complaintTypeOptions = ref([])
const complaintStatusOptions = ref([])

// 文件上传
const fileList = ref([])
const uploadUrl = `${import.meta.env.VITE_APP_BASE_API}/property/complaint/upload`
const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

const handlerOptions = ref([
  { label: '物业管家-张三', value: 1 },
  { label: '物业管家-李四', value: 2 },
  { label: '物业管理员-王五', value: 3 },
  { label: '物业经理-赵六', value: 4 }
])

const urgencyLevelOptions = ref([
  { label: '普通', value: 1 },
  { label: '紧急', value: 2 }
])

// 表单数据
const form = reactive({
  id: null,
  complaintNo: '',
  houseNo: '',
  complaintType: '',
  urgencyLevel: 1,
  phone: '',
  complaintContent: '',
  imageUrls: ''
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

// 表单验证规则
const formRules = {
  houseNo: [
    { required: true, message: '房屋编号不能为空', trigger: 'blur' }
  ],
  complaintType: [
    { required: true, message: '投诉类型不能为空', trigger: 'change' }
  ],
  complaintContent: [
    { required: true, message: '投诉内容不能为空', trigger: 'blur' },
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




// 加载字典数据
const loadDictData = async () => {
  try {
    const [typeRes, statusRes] = await Promise.all([
      getComplaintTypeDict(),
      getComplaintStatusDict()
    ])

    if (typeRes.code === 200) {
      complaintTypeOptions.value = typeRes.data || []
    }
    if (statusRes.code === 200) {
      complaintStatusOptions.value = statusRes.data || []
    }
  } catch (error) {
    console.error('加载字典数据失败:', error)
  }
}

// 加载投诉数据
const loadComplaints = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }

    let response
    if (currentUserRole.value === 3) {
      // 业主使用专用API - 查看自己的投诉
      response = await getMyComplaints(params)
    } else {
      // 管理员和物业经理使用通用API - 查看所有投诉
      response = await getComplaintPage(params)
    }

    if (response.code === 200) {
      tableData.value = response.data.rows || response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '加载投诉失败')
    }
  } catch (error) {
    console.error('加载投诉失败:', error)
    ElMessage.error('加载投诉失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadComplaints()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (typeof searchForm[key] === 'string') {
      searchForm[key] = ''
    } else {
      searchForm[key] = null
    }
  })
  handleSearch()
}

// 新增投诉
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    const response = await getComplaintDetail(row.id)
    if (response.code === 200) {
      currentComplaint.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取投诉详情失败')
    }
  } catch (error) {
    console.error('获取投诉详情失败:', error)
    ElMessage.error('获取投诉详情失败')
  }
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
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 处理图片URL
    const imageUrls = fileList.value.map(file => {
      if (file.response) {
        return file.response
      }
      return file.url
    }).join(',')

    const formData = { ...form, imageUrls }
    const response = await createComplaint(formData)

    if (response.code === 200) {
      ElMessage.success('投诉提交成功')
      dialogVisible.value = false
      loadComplaints()
    } else {
      ElMessage.error(response.msg || '投诉提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 删除投诉
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除投诉"${row.complaintNo}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteComplaint(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadComplaints()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    complaintNo: '',
    houseNo: '',
    complaintType: '',
    urgencyLevel: 1,
    phone: '',
    complaintContent: '',
    imageUrls: ''
  })
  fileList.value = []
  formRef.value?.resetFields()
}

// 文件上传相关
const beforeUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isValidType) {
    ElMessage.error('只能上传JPG/PNG/GIF格式的图片!')
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
  }
  return isValidType && isLt5M
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.msg || '图片上传失败')
  }
}

const handleRemove = (file, fileList) => {
  console.log('移除文件:', file, fileList)
}

const handleFileChange = (file, fileList) => {
  console.log('文件变化:', file, fileList)
}

// 工具函数
const getTypeTag = (type) => {
  const tagMap = {
    'sanitation': 'warning',
    'noise': 'danger',
    'facility_damage': 'primary',
    'service_attitude': 'info',
    'safety_hazard': 'danger',
    'other': 'info'
  }
  return tagMap[type] || 'info'
}

const getTypeName = (type) => {
  return getComplaintTypeName(type)
}

const getUrgencyTag = (urgency) => {
  return urgency === 2 ? 'danger' : 'info'
}

const getUrgencyName = (urgency) => {
  return urgency === 2 ? '紧急' : '普通'
}

const getStatusTag = (status) => {
  const tagMap = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return tagMap[status] || 'info'
}

const getStatusName = (status) => {
  const statusMap = {
    1: '待处理',
    2: '处理中',
    3: '已处理',
    4: '已关闭'
  }
  return statusMap[status] || '未知'
}

const getComplaintTypeName = (type) => {
  const option = complaintTypeOptions.value.find(item => item.dictValue === type)
  return option ? option.dictLabel : type
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

const getImageList = (imageUrls) => {
  if (!imageUrls) return []
  if (Array.isArray(imageUrls)) return imageUrls
  return imageUrls.split(',').filter(url => url.trim())
}

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_APP_BASE_API}${url}`
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
  loadDictData()
  loadComplaints()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

.breadcrumb-section {
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #e4e7ed;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
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