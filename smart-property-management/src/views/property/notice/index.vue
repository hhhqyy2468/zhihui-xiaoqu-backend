<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">公告发布</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item>公告发布</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="公告标题">
          <el-input
            v-model="searchForm.noticeTitle"
            placeholder="请输入公告标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select
            v-model="searchForm.noticeType"
            placeholder="请选择公告类型"
            clearable
            style="width: 150px"
          >
            <el-option label="通知公告" value="notice" />
            <el-option label="停水停电" value="outage" />
            <el-option label="社区活动" value="activity" />
            <el-option label="政策通知" value="policy" />
            <el-option label="温馨提示" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告状态">
          <el-select
            v-model="searchForm.noticeStatus"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已发布" value="1" />
            <el-option label="已过期" value="2" />
            <el-option label="已撤回" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        发布公告
      </el-button>
      <el-button
        type="success"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出公告
      </el-button>
    </div>

    <!-- 公告表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="noticeList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="noticeTitle" label="公告标题" width="200" show-overflow-tooltip />
        <el-table-column prop="noticeType" label="公告类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getNoticeTypeColor(row.noticeType)">
              {{ getNoticeTypeName(row.noticeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="noticeStatus" label="公告状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getNoticeStatusColor(row.noticeStatus)">
              {{ getNoticeStatusName(row.noticeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="是否置顶" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="warning">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="readCount" label="阅读次数" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="effectiveEndTime" label="有效期至" width="180">
          <template #default="{ row }">
            {{ row.effectiveEndTime ? formatDateTime(row.effectiveEndTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.noticeStatus === '1'"
              link
              type="info"
              @click="handleWithdraw(row)"
            >
              撤回
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

    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择公告类型">
            <el-option label="通知公告" value="notice" />
            <el-option label="停水停电" value="outage" />
            <el-option label="社区活动" value="activity" />
            <el-option label="政策通知" value="policy" />
            <el-option label="温馨提示" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <el-input
            v-model="form.noticeContent"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="form.isTop" />
        </el-form-item>
        <el-form-item label="发布范围" prop="publishScope">
          <el-radio-group v-model="form.publishScope">
            <el-radio value="1">全部</el-radio>
            <el-radio value="2">指定楼栋</el-radio>
            <el-radio value="3">指定单元</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.publishScope === '2'" label="目标楼栋" prop="targetBuildingIds">
          <el-select
            v-model="form.targetBuildingIds"
            placeholder="请选择楼栋"
            multiple
            style="width: 100%"
          >
            <el-option label="1号楼" value="1" />
            <el-option label="2号楼" value="2" />
            <el-option label="3号楼" value="3" />
            <el-option label="4号楼" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.publishScope === '3'" label="目标单元" prop="targetUnitIds">
          <el-select
            v-model="form.targetUnitIds"
            placeholder="请选择单元"
            multiple
            style="width: 100%"
          >
            <el-option label="1栋1单元" value="1-1" />
            <el-option label="1栋2单元" value="1-2" />
            <el-option label="2栋1单元" value="2-1" />
            <el-option label="2栋2单元" value="2-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="生效时间" prop="effectiveStartTime">
          <el-date-picker
            v-model="form.effectiveStartTime"
            type="datetime"
            placeholder="选择生效开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="失效时间" prop="effectiveEndTime">
          <el-date-picker
            v-model="form.effectiveEndTime"
            type="datetime"
            placeholder="选择失效时间（可选）"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ dialogTitle.includes('编辑') ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="公告详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题">{{ currentNotice.noticeTitle }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="getNoticeTypeColor(currentNotice.noticeType)">
            {{ getNoticeTypeName(currentNotice.noticeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ currentNotice.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="发布状态">
          <el-tag :type="getNoticeStatusColor(currentNotice.noticeStatus)">
            {{ getNoticeStatusName(currentNotice.noticeStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否置顶">
          {{ currentNotice.isTop ? '置顶' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="阅读次数">{{ currentNotice.readCount }}次</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDateTime(currentNotice.publishTime) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">
          {{ currentNotice.effectiveEndTime ? formatDateTime(currentNotice.effectiveEndTime) : '永久有效' }}
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>公告内容</h4>
        <div class="notice-content">{{ currentNotice.noticeContent }}</div>
      </div>

      <div v-if="currentNotice.attachmentUrls" style="margin-top: 20px;">
        <h4>附件列表</h4>
        <div class="attachment-list">
          <div
            v-for="(attachment, index) in getAttachments(currentNotice.attachmentUrls)"
            :key="index"
            class="attachment-item"
          >
            <el-icon><Document /></el-icon>
            <span>{{ attachment }}</span>
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
  Download,
  Document
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('发布公告')

// 表单数据
const formRef = ref()
const form = reactive({
  id: null,
  noticeTitle: '',
  noticeType: 'notice',
  noticeContent: '',
  isTop: false,
  publishScope: '1',
  targetBuildingIds: [],
  targetUnitIds: [],
  effectiveStartTime: '',
  effectiveEndTime: '',
  attachmentUrls: ''
})

// 搜索表单
const searchForm = reactive({
  noticeTitle: '',
  noticeType: '',
  noticeStatus: '',
  dateRange: []
})

// 公告数据
const noticeList = ref([])
const selectedNotices = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentNotice = ref({})

// 表单验证规则
const rules = {
  noticeTitle: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, max: 200, message: '公告标题长度在5到200个字符', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  noticeContent: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, message: '公告内容不能少于10个字符', trigger: 'blur' }
  ]
}

// 获取公告类型名称
const getNoticeTypeName = (type) => {
  const typeMap = {
    'notice': '通知公告',
    'outage': '停水停电',
    'activity': '社区活动',
    'policy': '政策通知',
    'reminder': '温馨提示'
  }
  return typeMap[type] || '未知'
}

// 获取公告类型颜色
const getNoticeTypeColor = (type) => {
  const colorMap = {
    'notice': 'primary',
    'outage': 'warning',
    'activity': 'success',
    'policy': 'info',
    'reminder': 'info'
  }
  return colorMap[type] || 'info'
}

// 获取公告状态名称
const getNoticeStatusName = (status) => {
  const statusMap = {
    1: '已发布',
    2: '已过期',
    3: '已撤回'
  }
  return statusMap[status] || '未知'
}

// 获取公告状态颜色
const getNoticeStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return colorMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 解析附件URL
const getAttachments = (attachmentUrls) => {
  if (!attachmentUrls) return []
  return attachmentUrls.split(',').map(url => url.trim()).filter(url => url)
}

// 生成模拟数据
const generateMockData = () => {
  const notices = []
  const titles = [
    '关于春节期间物业服务安排的通知',
    '小区电梯维护保养通知',
    '物业费缴纳温馨提示',
    '停车位调整公告',
    '社区春节活动通知',
    '安全防范提醒',
    '设施维修通知',
    '费用调整说明'
  ]
  const types = ['notice', 'outage', 'activity', 'policy', 'reminder']
  const statuses = ['1', '2', '3']
  const publishers = ['张三', '李四', '王五', '赵六']

  for (let i = 1; i <= 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const isTop = Math.random() > 0.8
    const publishDate = new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000)
    const endDate = new Date(publishDate.getTime() + (30 + Math.random() * 60) * 24 * 60 * 60 * 1000)

    notices.push({
      id: i,
      noticeTitle: titles[i % titles.length] + (i > 8 ? ` (${i})` : ''),
      noticeType: type,
      noticeContent: '尊敬的各位业主：为了提供更好的物业服务，现就相关事宜通知如下：请各位业主积极配合物业管理工作。如有任何问题，请及时联系物业服务中心。谢谢大家的理解与配合！',
      isTop: isTop,
      publishScope: '1',
      targetBuildingIds: '',
      targetUnitIds: '',
      effectiveStartTime: publishDate.toISOString(),
      effectiveEndTime: endDate.toISOString(),
      noticeStatus: status,
      publisherId: 1,
      publisherName: publishers[Math.floor(Math.random() * publishers.length)],
      readCount: Math.floor(Math.random() * 200) + 10,
      publishTime: publishDate.toISOString(),
      attachmentUrls: Math.random() > 0.7 ? 'https://example.com/file1.pdf,https://example.com/file2.pdf' : ''
    })
  }

  return notices
}

// 加载公告数据
const loadNotices = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockData()
    noticeList.value = mockData.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    )
    total.value = mockData.length
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadNotices()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    noticeTitle: '',
    noticeType: '',
    noticeStatus: '',
    dateRange: []
  })
  handleSearch()
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedNotices.value = selection
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '发布公告'
  Object.assign(form, {
    id: null,
    noticeTitle: '',
    noticeType: 'notice',
    noticeContent: '',
    isTop: false,
    publishScope: '1',
    targetBuildingIds: [],
    targetUnitIds: [],
    effectiveStartTime: '',
    effectiveEndTime: '',
    attachmentUrls: ''
  })
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  currentNotice.value = { ...row }
  detailVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 撤回
const handleWithdraw = (row) => {
  ElMessageBox.confirm(
    `确定要撤回公告"${row.noticeTitle}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('公告撤回成功')
    loadNotices()
  }).catch(() => {
    // 用户取消操作
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除公告"${row.noticeTitle}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadNotices()
  }).catch(() => {
    // 用户取消操作
  })
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 下载附件
const handleDownload = (attachment) => {
  ElMessage.info(`下载文件: ${attachment}`)
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadNotices()
    }
  })
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadNotices()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadNotices()
}

// 初始化
onMounted(() => {
  loadNotices()
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

.notice-content {
  line-height: 1.6;
  color: #333;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
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
</style>