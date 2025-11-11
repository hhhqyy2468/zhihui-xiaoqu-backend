<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card total-notices">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalNotices }}</div>
              <div class="stat-label">公告总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card published">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.publishedNotices }}</div>
              <div class="stat-label">已发布</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card draft">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.draftNotices }}</div>
              <div class="stat-label">草稿</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card today-published">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayPublished }}</div>
              <div class="stat-label">今日发布</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新建公告
      </el-button>
      <el-button @click="exportNotices">
        <el-icon><Download /></el-icon>
        导出公告
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
        <el-form :model="queryParams" :inline="true" @submit.prevent>
          <el-form-item label="公告标题">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入公告标题"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="公告类型">
            <el-select
              v-model="queryParams.type"
              placeholder="请选择公告类型"
              clearable
              style="width: 150px"
            >
              <el-option label="小区公告" value="小区公告" />
              <el-option label="停水通知" value="停水通知" />
              <el-option label="停电通知" value="停电通知" />
              <el-option label="电梯维护" value="电梯维护" />
              <el-option label="活动通知" value="活动通知" />
              <el-option label="费用催缴" value="费用催缴" />
              <el-option label="安全提醒" value="安全提醒" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择发布状态"
              clearable
              style="width: 120px"
            >
              <el-option label="草稿" value="0" />
              <el-option label="已发布" value="1" />
              <el-option label="已撤回" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="重要级别">
            <el-select
              v-model="queryParams.priority"
              placeholder="请选择重要级别"
              clearable
              style="width: 120px"
            >
              <el-option label="普通" value="普通" />
              <el-option label="重要" value="重要" />
              <el-option label="紧急" value="紧急" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

    <!-- 数据表格 -->
    <div class="table-section">
        <el-table
          v-loading="loading"
          :data="noticeList"
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="title" label="公告标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="type" label="公告类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)">{{ row.type }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="重要级别" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityTag(row.priority)">
                <el-icon v-if="row.priority === '紧急'"><WarningFilled /></el-icon>
                {{ row.priority }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="publisherName" label="发布人" width="100" />
          <el-table-column prop="readCount" label="阅读量" width="80" align="center">
            <template #default="{ row }">
              <span class="read-count">{{ row.readCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="160" />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button v-if="row.status === 0" type="success" link @click="handlePublish(row)">
                <el-icon><Promotion /></el-icon>
                发布
              </el-button>
              <el-button v-if="row.status === 1" type="warning" link @click="handleRecall(row)">
                <el-icon><RefreshLeft /></el-icon>
                撤回
              </el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, row)">
                <el-button type="info" link>
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit" v-if="row.status === 0">
                      <el-icon><Edit /></el-icon>编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><DocumentCopy /></el-icon>复制
                    </el-dropdown-item>
                    <el-dropdown-item command="top" v-if="row.status === 1">
                      <el-icon><Top /></el-icon>置顶
                    </el-dropdown-item>
                    <el-dropdown-item command="statistics">
                      <el-icon><TrendCharts /></el-icon>统计
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>

      <!-- 批量操作 -->
      <div v-if="selectedNotices.length > 0" class="batch-actions">
        <div class="batch-info">
          已选择 {{ selectedNotices.length }} 项
        </div>
        <div class="batch-buttons">
          <el-button type="success" @click="batchPublish">批量发布</el-button>
          <el-button type="warning" @click="batchRecall">批量撤回</el-button>
          <el-button type="danger" @click="batchDelete">批量删除</el-button>
        </div>
      </div>

    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :before-close="cancelDialog"
      fullscreen
    >
      <el-form
        ref="noticeFormRef"
        :model="noticeForm"
        :rules="noticeRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="公告标题" prop="title">
              <el-input
                v-model="noticeForm.title"
                placeholder="请输入公告标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="noticeForm.type" placeholder="请选择公告类型" style="width: 100%">
                <el-option label="小区公告" value="小区公告" />
                <el-option label="停水通知" value="停水通知" />
                <el-option label="停电通知" value="停电通知" />
                <el-option label="电梯维护" value="电梯维护" />
                <el-option label="活动通知" value="活动通知" />
                <el-option label="费用催缴" value="费用催缴" />
                <el-option label="安全提醒" value="安全提醒" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="重要级别" prop="priority">
              <el-select v-model="noticeForm.priority" placeholder="请选择重要级别" style="width: 100%">
                <el-option label="普通" value="普通" />
                <el-option label="重要" value="重要" />
                <el-option label="紧急" value="紧急" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发布状态">
              <el-radio-group v-model="noticeForm.status">
                <el-radio :value="0">保存草稿</el-radio>
                <el-radio :value="1">立即发布</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="定时发布">
              <el-switch
                v-model="noticeForm.isScheduled"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="noticeForm.isScheduled">
          <el-col :span="12">
            <el-form-item label="发布时间" prop="scheduledTime">
              <el-date-picker
                v-model="noticeForm.scheduledTime"
                type="datetime"
                placeholder="请选择发布时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="撤回时间" prop="recallTime">
              <el-date-picker
                v-model="noticeForm.recallTime"
                type="datetime"
                placeholder="请选择撤回时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <!-- WangEditor temporarily disabled for production build
        <div style="border: 1px solid #dcdfe6; border-radius: 4px;">
            <Toolbar
              style="border-bottom: 1px solid #dcdfe6"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 400px; overflow-y: hidden;"
              v-model="noticeForm.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
        -->

        <el-form-item label="附件上传">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleRemove"
            multiple
            :limit="5"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传附件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持jpg/png/pdf/doc/docx格式，单个文件不超过10MB，最多5个文件
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="推送范围">
          <el-checkbox-group v-model="noticeForm.targetGroups">
            <el-checkbox label="all">全体业主</el-checkbox>
            <el-checkbox label="building1">1号楼</el-checkbox>
            <el-checkbox label="building2">2号楼</el-checkbox>
            <el-checkbox label="building3">3号楼</el-checkbox>
            <el-checkbox label="custom">自定义</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="短信通知">
          <el-switch
            v-model="noticeForm.sendSms"
            active-text="发送"
            inactive-text="不发送"
          />
          <span class="form-tip">重要公告建议开启短信通知</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialog">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ noticeForm.status === 0 ? '保存草稿' : '发布公告' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="公告详情"
      width="900px"
    >
      <div v-if="currentNotice" class="notice-detail">
        <!-- 公告头部信息 -->
        <div class="notice-header">
          <h2 class="notice-title">{{ currentNotice.title }}</h2>
          <div class="notice-meta">
            <el-tag :type="getTypeTag(currentNotice.type)">{{ currentNotice.type }}</el-tag>
            <el-tag :type="getPriorityTag(currentNotice.priority)" style="margin-left: 8px;">
              <el-icon v-if="currentNotice.priority === '紧急'"><WarningFilled /></el-icon>
              {{ currentNotice.priority }}
            </el-tag>
            <el-tag :type="getStatusTag(currentNotice.status)" style="margin-left: 8px;">
              {{ getStatusText(currentNotice.status) }}
            </el-tag>
          </div>
        </div>

        <!-- 公告基本信息 -->
        <el-descriptions :column="3" border style="margin: 20px 0;">
          <el-descriptions-item label="发布人">{{ currentNotice.publisherName }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentNotice.publishTime }}</el-descriptions-item>
          <el-descriptions-item label="阅读量">{{ currentNotice.readCount || 0 }}</el-descriptions-item>
        </el-descriptions>

        <!-- 公告内容 -->
        <div class="notice-content">
          <h4>公告内容</h4>
          <div class="content-html" v-html="currentNotice.content"></div>
        </div>

        <!-- 附件列表 -->
        <div v-if="currentNotice.attachments && currentNotice.attachments.length > 0" class="notice-attachments">
          <h4>相关附件</h4>
          <el-table :data="currentNotice.attachments" stripe>
            <el-table-column prop="fileName" label="文件名" />
            <el-table-column prop="fileSize" label="文件大小" width="120" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" link @click="downloadFile(row)">
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 统计信息 -->
        <div class="notice-statistics">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="阅读统计" name="read">
              <el-table :data="readRecords" stripe>
                <el-table-column prop="ownerName" label="业主姓名" width="100" />
                <el-table-column prop="houseInfo" label="房产信息" width="180" />
                <el-table-column prop="readTime" label="阅读时间" width="160" />
                <el-table-column prop="readDevice" label="阅读设备" width="120" />
                <el-table-column prop="ipAddress" label="IP地址" width="130" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="推送记录" name="push">
              <el-table :data="pushRecords" stripe>
                <el-table-column prop="pushTime" label="推送时间" width="160" />
                <el-table-column prop="pushType" label="推送类型" width="100">
                  <template #default="{ row }">
                    <el-tag size="small">{{ row.pushType }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="targetCount" label="目标数量" width="100" />
                <el-table-column prop="successCount" label="成功数量" width="100" />
                <el-table-column prop="status" label="推送状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">
                      {{ row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// Temporarily commented out for production build
// import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
// import '@wangeditor/editor/dist/css/style.css'
import {
  Plus, Search, Refresh, Download, View, Bell, CircleCheck,
  Document, Clock, WarningFilled, Edit, Delete, DocumentCopy,
  Top, TrendCharts, ArrowDown, Promotion, RefreshLeft, Upload
} from '@element-plus/icons-vue'
import { noticeApi } from '@/api'
import { handleResponse, handleError } from '@/utils/response'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 编辑器实例
const editorRef = shallowRef()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const noticeList = ref([])
const selectedNotices = ref([])
const fileList = ref([])
const readRecords = ref([])
const pushRecords = ref([])
const total = ref(0)
const dateRange = ref([])
const activeTab = ref('read')

// 统计数据
const statistics = ref({
  totalNotices: 0,
  publishedNotices: 0,
  draftNotices: 0,
  todayPublished: 0
})

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 20,
  title: '',
  type: '',
  status: '',
  priority: '',
  startDate: '',
  endDate: ''
})

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// 表单数据
const noticeForm = reactive({
  id: null,
  title: '',
  type: '',
  priority: '普通',
  status: 0,
  isScheduled: false,
  scheduledTime: '',
  recallTime: '',
  content: '',
  attachments: [],
  targetGroups: ['all'],
  sendSms: false
})

// 当前查看的公告
const currentNotice = ref(null)

// 表单引用
const noticeFormRef = ref(null)

// 上传配置
const uploadUrl = '/api/upload'
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

// 编辑器配置
const toolbarConfig = {
  excludeKeys: [
    'uploadVideo',
    'insertVideo',
    'uploadImage',
    'insertImage',
    'codeBlock'
  ]
}

const editorConfig = {
  placeholder: '请输入公告内容...',
  MENU_CONF: {}
}

// 表单验证规则
const noticeRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择重要级别', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, message: '公告内容至少10个字符', trigger: 'blur' }
  ],
  scheduledTime: [
    { required: true, message: '请选择发布时间', trigger: 'change' }
  ]
}

// 计算属性
const getTypeTag = (type) => {
  const map = {
    '小区公告': 'primary',
    '停水通知': 'warning',
    '停电通知': 'warning',
    '电梯维护': 'info',
    '活动通知': 'success',
    '费用催缴': 'danger',
    '安全提醒': 'warning',
    '其他': ''
  }
  return map[type] || ''
}

const getPriorityTag = (priority) => {
  const map = { '普通': 'info', '重要': 'warning', '紧急': 'danger' }
  return map[priority] || ''
}

const getStatusTag = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { 0: '草稿', 1: '已发布', 2: '已撤回' }
  return map[status] || '未知'
}

// 编辑器创建回调
const handleCreated = (editor) => {
  editorRef.value = editor
}

// 获取公告列表
const getNoticeList = async () => {
  try {
    loading.value = true

    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = ''
      queryParams.endDate = ''
    }

    const response = await noticeApi.getList(queryParams)
    const result = handleResponse(response)
    noticeList.value = result.data.records
    total.value = result.data.total
  } catch (error) {
    handleError(error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const response = await noticeApi.getStatistics()
    const result = handleResponse(response)
    statistics.value = result.data
  } catch (error) {
    handleError(error)
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  getNoticeList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.title = ''
  queryParams.type = ''
  queryParams.status = ''
  queryParams.priority = ''
  dateRange.value = []
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedNotices.value = selection
}

// 显示新增对话框
const showAddDialog = () => {
  dialogTitle.value = '新建公告'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑公告'
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(noticeForm, row)
  if (row.attachments) {
    fileList.value = row.attachments.map(file => ({
      name: file.fileName,
      url: file.fileUrl
    }))
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(noticeForm, {
    id: null,
    title: '',
    type: '',
    priority: '普通',
    status: 0,
    isScheduled: false,
    scheduledTime: '',
    recallTime: '',
    content: '',
    attachments: [],
    targetGroups: ['all'],
    sendSms: false
  })
  fileList.value = []
  noticeFormRef.value?.resetFields()
}

// 取消对话框
const cancelDialog = () => {
  dialogVisible.value = false
  resetForm()
}

// 提交表单
const submitForm = async () => {
  try {
    await noticeFormRef.value.validate()

    // 处理定时发布的验证
    if (noticeForm.isScheduled && !noticeForm.scheduledTime) {
      ElMessage.error('请选择发布时间')
      return
    }

    submitting.value = true

    // 处理附件
    noticeForm.attachments = fileList.value.map(file => ({
      fileName: file.name,
      fileUrl: file.url || file.response?.data?.url
    }))

    const response = isEdit.value
      ? await noticeApi.update(noticeForm)
      : await noticeApi.create(noticeForm)

    handleResponse(response)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  } finally {
    submitting.value = false
  }
}

// 文件上传相关
const beforeUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'application/pdf',
    'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传jpg/png/pdf/doc/docx文件!')
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!')
  }
  return isValidType && isLt10M
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    file.url = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const handleRemove = (file, fileList) => {
  // 更新附件列表
}

// 下载文件
const downloadFile = (file) => {
  window.open(file.fileUrl)
}

// 查看详情
const handleView = async (row) => {
  try {
    const response = await noticeApi.getDetail(row.id)
    const result = handleResponse(response)
    currentNotice.value = result.data.notice
    readRecords.value = result.data.readRecords || []
    pushRecords.value = result.data.pushRecords || []
    detailDialogVisible.value = true
    activeTab.value = 'read'
  } catch (error) {
    handleError(error)
  }
}

// 发布公告
const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认发布公告"${row.title}"？`,
      '发布确认',
      { type: 'warning' }
    )

    const response = await noticeApi.publish({ id: row.id })
    handleResponse(response)
    ElMessage.success('发布成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 撤回公告
const handleRecall = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认撤回公告"${row.title}"？撤回后业主将无法查看此公告。`,
      '撤回确认',
      { type: 'warning' }
    )

    const response = await noticeApi.recall({ id: row.id })
    handleResponse(response)
    ElMessage.success('撤回成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 命令处理
const handleCommand = (command, row) => {
  switch (command) {
    case 'edit':
      showEditDialog(row)
      break
    case 'copy':
      copyNotice(row)
      break
    case 'top':
      topNotice(row)
      break
    case 'statistics':
      showStatistics(row)
      break
    case 'delete':
      deleteNotice(row)
      break
  }
}

// 复制公告
const copyNotice = (row) => {
  showAddDialog()
  Object.assign(noticeForm, {
    ...row,
    id: null,
    title: `${row.title} (复制)`,
    status: 0
  })
}

// 置顶公告
const topNotice = async (row) => {
  try {
    const response = await noticeApi.top({ id: row.id })
    handleResponse(response)
    ElMessage.success('置顶成功')
    getNoticeList()
  } catch (error) {
    handleError(error)
  }
}

// 显示统计
const showStatistics = (row) => {
  ElMessage.info('统计功能开发中')
}

// 删除公告
const deleteNotice = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除公告"${row.title}"？此操作不可恢复！`,
      '删除确认',
      { type: 'warning' }
    )

    const response = await noticeApi.delete(row.id)
    handleResponse(response)
    ElMessage.success('删除成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 批量操作
const batchPublish = async () => {
  try {
    const draftNotices = selectedNotices.value.filter(notice => notice.status === 0)
    if (draftNotices.length === 0) {
      ElMessage.warning('请选择草稿状态的公告')
      return
    }

    await ElMessageBox.confirm(
      `确认发布选中的 ${draftNotices.length} 个公告？`,
      '批量发布确认',
      { type: 'warning' }
    )

    const ids = draftNotices.map(notice => notice.id)
    const response = await noticeApi.batchPublish({ ids })
    handleResponse(response)
    ElMessage.success('批量发布成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchRecall = async () => {
  try {
    const publishedNotices = selectedNotices.value.filter(notice => notice.status === 1)
    if (publishedNotices.length === 0) {
      ElMessage.warning('请选择已发布的公告')
      return
    }

    await ElMessageBox.confirm(
      `确认撤回选中的 ${publishedNotices.length} 个公告？`,
      '批量撤回确认',
      { type: 'warning' }
    )

    const ids = publishedNotices.map(notice => notice.id)
    const response = await noticeApi.batchRecall({ ids })
    handleResponse(response)
    ElMessage.success('批量撤回成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

const batchDelete = async () => {
  try {
    if (selectedNotices.value.length === 0) return

    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedNotices.value.length} 个公告？此操作不可恢复！`,
      '批量删除确认',
      { type: 'warning' }
    )

    const ids = selectedNotices.value.map(notice => notice.id)
    const response = await noticeApi.batchDelete(ids)
    handleResponse(response)
    ElMessage.success('批量删除成功')
    getNoticeList()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error)
    }
  }
}

// 导出公告
const exportNotices = async () => {
  try {
    const response = await noticeApi.export(queryParams)
    // 处理文件下载
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `通知公告_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    handleError(error)
  }
}

// 组件销毁时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 初始化
onMounted(() => {
  getNoticeList()
  getStatistics()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

// 页面头部
.page-header {
  margin-bottom: 20px;

  .page-title {
    margin: 0 0 10px 0;
    font-size: 24px;
    color: #303133;
    font-weight: 600;
  }
}

// 操作按钮区域
.action-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

// 统计卡片
.statistics-cards {
  margin-bottom: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px;
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      font-size: 24px;
      color: white;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }

    &.total-notices {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.published {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.draft {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.today-published {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 搜索区域
.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

// 表格区域
.table-section {
  .read-count {
    font-weight: bold;
    color: #409eff;
  }
}

// 批量操作
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 15px 20px;
  background-color: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;

  .batch-info {
    font-size: 14px;
    color: #1e40af;
  }

  .batch-buttons {
    display: flex;
    gap: 10px;
  }
}

// 公告详情
.notice-detail {
  .notice-header {
    text-align: center;
    margin-bottom: 20px;

    .notice-title {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: #303133;
    }

    .notice-meta {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
    }
  }

  .notice-content {
    margin: 20px 0;

    h4 {
      margin-bottom: 10px;
      color: #303133;
    }

    .content-html {
      padding: 20px;
      background-color: #f8f9fa;
      border-radius: 6px;
      line-height: 1.6;

      :deep(p) {
        margin: 10px 0;
      }

      :deep(img) {
        max-width: 100%;
        height: auto;
      }
    }
  }

  .notice-attachments {
    margin: 20px 0;

    h4 {
      margin-bottom: 10px;
      color: #303133;
    }
  }

  .notice-statistics {
    margin-top: 20px;
  }
}

// 表单提示
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

// 响应式设计
@media (max-width: 768px) {
  .statistics-cards {
    .el-col {
      margin-bottom: 10px;
    }
  }

  .action-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-section {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>