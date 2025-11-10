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
        <el-form-item label="公告标题" prop="title">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入公告标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="公告类型" prop="noticeType">
          <el-select
            v-model="searchForm.noticeType"
            placeholder="请选择公告类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in noticeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="公告状态" prop="status">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择公告状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
            <el-option label="已撤回" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="发布时间范围" prop="dateRange">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
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
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>公告列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:notice:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增公告
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
        <!-- 公告类型列 -->
        <template #noticeType="{ row }">
          <el-tag :type="getNoticeTypeTag(row.noticeType)">
            {{ getNoticeTypeName(row.noticeType) }}
          </el-tag>
        </template>

        <!-- 状态列 -->
        <template #status="{ row }">
          <el-tag :type="getStatusTag(row.status)">
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>

        <!-- 是否置顶列 -->
        <template #isTop="{ row }">
          <el-tag v-if="row.isTop" type="warning">置顶</el-tag>
          <span v-else>-</span>
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
            v-permission="'property:notice:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="success"
            v-permission="'property:notice:publish'"
            v-if="row.status === 0"
            @click="handlePublish(row)"
          >
            发布
          </el-button>
          <el-button
            link
            type="info"
            v-permission="'property:notice:withdraw'"
            v-if="row.status === 1"
            @click="handleWithdraw(row)"
          >
            撤回
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'property:notice:delete'"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
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
            {{ isEdit.value ? '更新' : '发布' }}
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
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题">
          {{ currentNotice.title }}
        </el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="getNoticeTypeTag(currentNotice.noticeType)">
            {{ getNoticeTypeName(currentNotice.noticeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">
          {{ currentNotice.publisher }}
        </el-descriptions-item>
        <el-descriptions-item label="发布状态">
          <el-tag :type="getStatusTag(currentNotice.status)">
            {{ getStatusName(currentNotice.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否置顶">
          <el-tag v-if="currentNotice.isTop" type="warning">置顶</el-tag>
          <span v-else>否</span>
        </el-descriptions-item>
        <el-descriptions-item label="阅读次数">
          {{ currentNotice.readCount || 0 }} 次
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">
          {{ currentNotice.publishTime }}
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>公告内容</h4>
        <div class="notice-content" v-html="currentNotice.content"></div>
      </div>

      <div style="margin-top: 20px;" v-if="currentNotice.attachments && currentNotice.attachments.length > 0">
        <h4>附件列表</h4>
        <div class="attachment-list">
          <div
            v-for="(attachment, index) in currentNotice.attachments"
            :key="index"
            class="attachment-item"
          >
            <el-icon><Document /></el-icon>
            <span>{{ attachment.name }}</span>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleDownload(attachment)"
            >
              下载
            </el-button>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Download, Document } from '@element-plus/icons-vue'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)

// 搜索表单
const searchForm = reactive({
  title: '',
  noticeType: '',
  status: '',
  dateRange: []
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
    prop: 'title',
    label: '公告标题',
    width: '200',
    sortable: true
  },
  {
    prop: 'noticeType',
    label: '公告类型',
    width: '120',
    slot: 'noticeType'
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    slot: 'status'
  },
  {
    prop: 'isTop',
    label: '置顶',
    width: '80',
    slot: 'isTop',
    sortable: true
  },
  {
    prop: 'publisher',
    label: '发布人',
    width: '120'
  },
  {
    prop: 'publishTime',
    label: '发布时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'readCount',
    label: '阅读次数',
    width: '100',
    sortable: true
  },
  {
    prop: 'operation',
    label: '操作',
    width: '320',
    slot: 'operation',
    fixed: 'right'
  }
]

// 选项数据
const noticeTypeOptions = ref([
  { label: '社区通知', value: 'community' },
  { label: '物业公告', value: 'property' },
  { label: '安全提示', value: 'safety' },
  { label: '活动通知', value: 'activity' },
  { label: '维修通知', value: 'maintenance' },
  { label: '费用通知', value: 'fee' },
  { label: '其他公告', value: 'other' }
])

// 表单数据
const form = reactive({
  noticeId: null,
  title: '',
  noticeType: 'community',
  content: '',
  attachments: [],
  isTop: false,
  status: 0,
  publishScope: 'all'
})

// 当前公告
const currentNotice = ref({})

// 表单规则
const formRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, max: 100, message: '公告标题长度在5到100个字符', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 20, message: '公告内容不能少于20个字符', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'title',
    label: '公告标题',
    type: 'input',
    placeholder: '请输入公告标题'
  },
  {
    prop: 'noticeType',
    label: '公告类型',
    type: 'select',
    options: noticeTypeOptions.value,
    placeholder: '请选择公告类型'
  },
  {
    prop: 'content',
    label: '公告内容',
    type: 'editor',
    placeholder: '请输入公告内容',
    height: 300
  },
  {
    prop: 'attachments',
    label: '附件',
    type: 'upload',
    uploadType: 'file',
    multiple: true,
    limit: 5,
    placeholder: '请上传附件（最多5个文件）'
  },
  {
    prop: 'isTop',
    label: '是否置顶',
    type: 'switch'
  },
  {
    prop: 'publishScope',
    label: '发布范围',
    type: 'radio',
    options: [
      { label: '全部用户', value: 'all' },
      { label: '业主用户', value: 'owner' },
      { label: '物业管理员', value: 'manager' }
    ]
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '发布公告')

// 获取公告类型名称
const getNoticeTypeName = (type) => {
  const option = noticeTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '未知'
}

// 获取公告类型标签
const getNoticeTypeTag = (type) => {
  const tagMap = {
    'community': 'primary',
    'property': 'success',
    'safety': 'danger',
    'activity': 'warning',
    'maintenance': 'info',
    'fee': 'info',
    'other': 'info'
  }
  return tagMap[type] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '草稿',
    1: '已发布',
    2: '已撤回'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'info',    // 草稿
    1: 'success', // 已发布
    2: 'warning'  // 已撤回
  }
  return tagMap[status] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockNotices = []
  const titles = [
    '关于春节期间物业服务安排的通知',
    '小区电梯维护保养通知',
    '物业费缴纳温馨提示',
    '停车位调整公告',
    '社区活动通知',
    '安全防范提醒',
    '设施维修通知',
    '费用调整说明'
  ]
  const types = ['community', 'property', 'safety', 'activity', 'maintenance', 'fee', 'other']
  const statuses = [0, 1, 2]
  const publishers = ['物业服务中心', '安保部', '维修部', '财务部']

  for (let i = 0; i < 30; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const isTop = Math.random() > 0.8
    const publishDate = new Date()
    publishDate.setDate(publishDate.getDate() - Math.floor(Math.random() * 30))

    mockNotices.push({
      noticeId: i + 1,
      title: titles[i % titles.length] + (i > 7 ? ` (${i})` : ''),
      noticeType: type,
      content: `<p>尊敬的各位业主：</p>
               <p>为了提供更好的物业服务，现就相关事宜通知如下：</p>
               <p>1. 请各位业主积极配合物业管理工作。</p>
               <p>2. 如有任何问题，请及时联系物业服务中心。</p>
               <p>谢谢大家的理解与配合！</p>
               <p>物业管理服务中心</p>
               <p>${publishDate.getFullYear()}年${publishDate.getMonth() + 1}月${publishDate.getDate()}日</p>`,
      attachments: Math.random() > 0.7 ? [
        { name: '附件1.pdf', url: '/files/attachment1.pdf' },
        { name: '附件2.doc', url: '/files/attachment2.doc' }
      ] : [],
      isTop: isTop,
      status: status,
      publisher: publishers[i % publishers.length],
      publishTime: publishDate.toISOString().split('T')[0] + ' ' + publishDate.toTimeString().substring(0, 5),
      readCount: Math.floor(Math.random() * 100) + 10,
      createTime: publishDate.toISOString()
    })
  }

  // 按置顶和发布时间排序
  mockNotices.sort((a, b) => {
    if (a.isTop !== b.isTop) {
      return b.isTop - a.isTop
    }
    return new Date(b.publishTime) - new Date(a.publishTime)
  })

  // 模拟分页
  pagination.total = mockNotices.length
  return mockNotices
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
  currentNotice.value = { ...row }
  detailDialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 发布公告
const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布公告"${row.title}"吗？`,
      '提示',
      { type: 'info' }
    )
    ElMessage.success('公告发布成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 撤回公告
const handleWithdraw = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要撤回公告"${row.title}"吗？撤回后公告将不再显示给用户。`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('公告撤回成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告"${row.title}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 下载附件
const handleDownload = (attachment) => {
  ElMessage.info(`下载文件: ${attachment.name}`)
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
      ElMessage.success(isEdit.value ? '公告更新成功' : '公告发布成功')
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
    noticeId: null,
    title: '',
    noticeType: 'community',
    content: '',
    attachments: [],
    isTop: false,
    status: 0,
    publishScope: 'all'
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

  .notice-content {
    line-height: 1.6;
    color: #333;
    font-size: 14px;
  }

  .attachment-list {
    margin-top: 10px;

    .attachment-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 12px;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 8px;

      .el-icon {
        color: #409eff;
      }

      span {
        flex: 1;
        color: #606266;
      }
    }
  }
}
</style>