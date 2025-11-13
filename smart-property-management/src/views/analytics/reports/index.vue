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
        <el-form-item label="报表类型" prop="reportType">
          <el-select
            v-model="searchForm.reportType"
            placeholder="请选择报表类型"
            clearable
            style="width: 150px"
          >
            <el-option label="财务报表" value="finance" />
            <el-option label="运营报表" value="operation" />
            <el-option label="住户报表" value="resident" />
            <el-option label="设备报表" value="equipment" />
          </el-select>
        </el-form-item>

        <el-form-item label="报表周期" prop="period">
          <el-select
            v-model="searchForm.period"
            placeholder="请选择报表周期"
            clearable
            style="width: 120px"
          >
            <el-option label="日报" value="daily" />
            <el-option label="周报" value="weekly" />
            <el-option label="月报" value="monthly" />
            <el-option label="季报" value="quarterly" />
            <el-option label="年报" value="yearly" />
          </el-select>
        </el-form-item>

        <el-form-item label="创建时间范围" prop="dateRange">
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
          <span>报表列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'analytics:report:generate'"
              @click="handleGenerate"
            >
              <el-icon><DocumentAdd /></el-icon>
              生成报表
            </el-button>
            <el-button
              type="success"
              v-permission="'analytics:report:template'"
              @click="handleTemplate"
            >
              <el-icon><Setting /></el-icon>
              报表模板
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
        <!-- 报表类型列 -->
        <template #reportType="{ row }">
          <el-tag :type="getReportTypeTag(row.reportType)">
            {{ getReportTypeName(row.reportType) }}
          </el-tag>
        </template>

        <!-- 周期列 -->
        <template #period="{ row }">
          <el-tag :type="getPeriodTag(row.period)">
            {{ getPeriodName(row.period) }}
          </el-tag>
        </template>

        <!-- 状态列 -->
        <template #status="{ row }">
          <el-tag :type="getStatusTag(row.status)">
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            @click="handleView(row)"
          >
            查看
          </el-button>
          <el-button
            link
            type="success"
            @click="handleDownload(row)"
            :disabled="row.status !== 2"
          >
            下载
          </el-button>
          <el-button
            link
            type="warning"
            @click="handleShare(row)"
            v-permission="'analytics:report:share'"
          >
            分享
          </el-button>
          <el-button
            link
            type="info"
            @click="handleSchedule(row)"
            v-permission="'analytics:report:schedule'"
          >
            定时
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row)"
            v-permission="'analytics:report:delete'"
          >
            删除
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 生成报表对话框 -->
    <el-dialog
      v-model="generateDialogVisible"
      title="生成报表"
      width="600px"
      @close="handleGenerateDialogClose"
    >
      <Form
        ref="generateFormRef"
        :model="generateForm"
        :rules="generateFormRules"
        :items="generateFormItems"
        label-width="100px"
      />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="generateDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="generateLoading"
            @click="handleGenerateSubmit"
          >
            生成
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 报表预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="报表预览"
      width="80%"
      @close="handlePreviewDialogClose"
    >
      <div class="report-preview">
        <div class="preview-header">
          <h3>{{ currentReport.reportName }}</h3>
          <div class="preview-meta">
            <span>生成时间：{{ currentReport.generateTime }}</span>
            <span>报表周期：{{ getPeriodName(currentReport.period) }}</span>
          </div>
        </div>

        <div class="preview-content">
          <!-- 报表内容预览 -->
          <div v-if="currentReport.reportType === 'finance'">
            <el-descriptions title="财务报表概览" :column="2" border>
              <el-descriptions-item label="总收入">
                ¥{{ formatNumber(currentReport.data?.totalRevenue || 0) }}
              </el-descriptions-item>
              <el-descriptions-item label="总支出">
                ¥{{ formatNumber(currentReport.data?.totalExpense || 0) }}
              </el-descriptions-item>
              <el-descriptions-item label="净利润">
                ¥{{ formatNumber(currentReport.data?.netProfit || 0) }}
              </el-descriptions-item>
              <el-descriptions-item label="缴费率">
                {{ currentReport.data?.paymentRate || 0 }}%
              </el-descriptions-item>
            </el-descriptions>

            <div class="chart-container" style="height: 300px; margin-top: 20px;">
              <div ref="financeChartRef"></div>
            </div>
          </div>

          <div v-else-if="currentReport.reportType === 'operation'">
            <el-descriptions title="运营报表概览" :column="2" border>
              <el-descriptions-item label="总楼栋数">
                {{ currentReport.data?.totalBuildings || 0 }}
              </el-descriptions-item>
              <el-descriptions-item label="入住率">
                {{ currentReport.data?.occupancyRate || 0 }}%
              </el-descriptions-item>
              <el-descriptions-item label="投诉处理率">
                {{ currentReport.data?.complaintResolutionRate || 0 }}%
              </el-descriptions-item>
              <el-descriptions-item label="维修完成率">
                {{ currentReport.data?.repairCompletionRate || 0 }}%
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-else>
            <p>报表内容加载中...</p>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            @click="handleDownload(currentReport)"
            :disabled="currentReport.status !== 2"
          >
            下载报表
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分享报表对话框 -->
    <el-dialog
      v-model="shareDialogVisible"
      title="分享报表"
      width="500px"
    >
      <div class="share-content">
        <el-form :model="shareForm" label-width="80px">
          <el-form-item label="分享方式">
            <el-radio-group v-model="shareForm.shareType">
              <el-radio value="link">链接分享</el-radio>
              <el-radio value="email">邮件分享</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="有效期" v-if="shareForm.shareType === 'link'">
            <el-select v-model="shareForm.expireTime" style="width: 100%">
              <el-option label="1天" value="1" />
              <el-option label="7天" value="7" />
              <el-option label="30天" value="30" />
              <el-option label="永久" value="-1" />
            </el-select>
          </el-form-item>

          <el-form-item label="收件人" v-if="shareForm.shareType === 'email'">
            <el-input
              v-model="shareForm.emails"
              type="textarea"
              :rows="3"
              placeholder="请输入邮箱地址，多个邮箱用逗号分隔"
            />
          </el-form-item>

          <el-form-item label="备注">
            <el-input
              v-model="shareForm.comment"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息（可选）"
            />
          </el-form-item>
        </el-form>

        <div class="share-link" v-if="shareForm.shareType === 'link' && shareLink">
          <el-input
            v-model="shareLink"
            readonly
          >
            <template #append>
              <el-button @click="copyShareLink">复制</el-button>
            </template>
          </el-input>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shareDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleShareSubmit"
            :loading="shareLoading"
          >
            分享
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, DocumentAdd, Setting } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const generateFormRef = ref()
const loading = ref(false)
const generateLoading = ref(false)
const shareLoading = ref(false)

const generateDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const shareDialogVisible = ref(false)

const financeChartRef = ref()
let financeChart = null

// 搜索表单
const searchForm = reactive({
  reportType: '',
  period: '',
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
    prop: 'reportName',
    label: '报表名称',
    width: '200',
    sortable: true
  },
  {
    prop: 'reportType',
    label: '报表类型',
    width: '120',
    slot: 'reportType'
  },
  {
    prop: 'period',
    label: '报表周期',
    width: '100',
    slot: 'period'
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    slot: 'status'
  },
  {
    prop: 'generateTime',
    label: '生成时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'fileSize',
    label: '文件大小',
    width: '120',
    formatter: (row) => `${(row.fileSize / 1024).toFixed(2)} KB`
  },
  {
    prop: 'creator',
    label: '创建人',
    width: '120'
  },
  {
    prop: 'operation',
    label: '操作',
    width: '320',
    slot: 'operation',
    fixed: 'right'
  }
]

// 生成报表表单
const generateForm = reactive({
  reportName: '',
  reportType: '',
  period: '',
  startDate: '',
  endDate: '',
  format: 'excel',
  email: ''
})

// 生成报表表单规则
const generateFormRules = {
  reportName: [
    { required: true, message: '请输入报表名称', trigger: 'blur' }
  ],
  reportType: [
    { required: true, message: '请选择报表类型', trigger: 'change' }
  ],
  period: [
    { required: true, message: '请选择报表周期', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ]
}

// 生成报表表单项配置
const generateFormItems = computed(() => [
  {
    prop: 'reportName',
    label: '报表名称',
    type: 'input',
    placeholder: '请输入报表名称'
  },
  {
    prop: 'reportType',
    label: '报表类型',
    type: 'select',
    options: [
      { label: '财务报表', value: 'finance' },
      { label: '运营报表', value: 'operation' },
      { label: '住户报表', value: 'resident' },
      { label: '设备报表', value: 'equipment' }
    ],
    placeholder: '请选择报表类型'
  },
  {
    prop: 'period',
    label: '报表周期',
    type: 'select',
    options: [
      { label: '日报', value: 'daily' },
      { label: '周报', value: 'weekly' },
      { label: '月报', value: 'monthly' },
      { label: '季报', value: 'quarterly' },
      { label: '年报', value: 'yearly' }
    ],
    placeholder: '请选择报表周期'
  },
  {
    prop: 'startDate',
    label: '开始日期',
    type: 'date',
    placeholder: '请选择开始日期'
  },
  {
    prop: 'endDate',
    label: '结束日期',
    type: 'date',
    placeholder: '请选择结束日期'
  },
  {
    prop: 'format',
    label: '导出格式',
    type: 'radio',
    options: [
      { label: 'Excel', value: 'excel' },
      { label: 'PDF', value: 'pdf' },
      { label: 'Word', value: 'word' }
    ]
  },
  {
    prop: 'email',
    label: '邮箱地址',
    type: 'input',
    placeholder: '生成完成后发送到此邮箱（可选）'
  }
])

// 当前报表
const currentReport = ref({})

// 分享表单
const shareForm = reactive({
  shareType: 'link',
  expireTime: '7',
  emails: '',
  comment: ''
})

const shareLink = ref('')

// 获取模拟数据
const getMockData = () => {
  const mockReports = []
  const reportTypes = ['finance', 'operation', 'resident', 'equipment']
  const periods = ['daily', 'weekly', 'monthly', 'quarterly', 'yearly']
  const statuses = [0, 1, 2, 3] // 0-生成中 1-生成失败 2-已完成 3-已删除
  const creators = ['张三', '李四', '王五', '赵六']

  for (let i = 1; i <= 30; i++) {
    const reportType = reportTypes[Math.floor(Math.random() * reportTypes.length)]
    const period = periods[Math.floor(Math.random() * periods.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const generateDate = new Date()
    generateDate.setDate(generateDate.getDate() - Math.floor(Math.random() * 30))

    mockReports.push({
      reportId: i,
      reportName: `${getReportTypeName(reportType)}_${getPeriodName(period)}_${generateDate.toISOString().split('T')[0]}`,
      reportType: reportType,
      period: period,
      status: status,
      generateTime: generateDate.toISOString().split('T')[0] + ' ' + generateDate.toTimeString().substring(0, 5),
      fileSize: Math.floor(Math.random() * 5000) + 100,
      creator: creators[Math.floor(Math.random() * creators.length)],
      data: status === 2 ? generateMockReportData(reportType) : null
    })
  }

  // 按生成时间排序
  mockReports.sort((a, b) => new Date(b.generateTime) - new Date(a.generateTime))
  pagination.total = mockReports.length
  return mockReports
}

// 生成模拟报表数据
const generateMockReportData = (reportType) => {
  const baseData = {
    totalRevenue: Math.floor(Math.random() * 1000000) + 500000,
    totalExpense: Math.floor(Math.random() * 800000) + 300000,
    netProfit: 0,
    paymentRate: Math.floor(Math.random() * 20) + 80,
    totalBuildings: 12,
    occupancyRate: Math.floor(Math.random() * 15) + 80,
    complaintResolutionRate: Math.floor(Math.random() * 10) + 90,
    repairCompletionRate: Math.floor(Math.random() * 10) + 85
  }

  baseData.netProfit = baseData.totalRevenue - baseData.totalExpense
  return baseData
}

// 获取报表类型名称
const getReportTypeName = (type) => {
  const typeMap = {
    'finance': '财务报表',
    'operation': '运营报表',
    'resident': '住户报表',
    'equipment': '设备报表'
  }
  return typeMap[type] || '未知'
}

// 获取报表类型标签
const getReportTypeTag = (type) => {
  const tagMap = {
    'finance': 'primary',
    'operation': 'success',
    'resident': 'warning',
    'equipment': 'info'
  }
  return tagMap[type] || 'info'
}

// 获取周期名称
const getPeriodName = (period) => {
  const periodMap = {
    'daily': '日报',
    'weekly': '周报',
    'monthly': '月报',
    'quarterly': '季报',
    'yearly': '年报'
  }
  return periodMap[period] || '未知'
}

// 获取周期标签
const getPeriodTag = (period) => {
  const tagMap = {
    'daily': 'primary',
    'weekly': 'success',
    'monthly': 'warning',
    'quarterly': 'info',
    'yearly': 'danger'
  }
  return tagMap[period] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '生成中',
    1: '生成失败',
    2: '已完成',
    3: '已删除'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 生成中
    1: 'danger',  // 生成失败
    2: 'success', // 已完成
    3: 'info'     // 已删除
  }
  return tagMap[status] || 'info'
}

// 格式化数字
const formatNumber = (num) => {
  return num.toLocaleString()
}

// 初始化财务图表
const initFinanceChart = () => {
  if (!currentReport.value.data) return

  const option = {
    title: {
      text: '收支趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收入', '支出'],
      top: '10%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: [120000, 132000, 101000, 134000, 90000, 230000],
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '支出',
        type: 'line',
        data: [220000, 182000, 191000, 234000, 290000, 330000],
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }

  financeChart = echarts.init(financeChartRef.value)
  financeChart.setOption(option)
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

// 生成报表
const handleGenerate = () => {
  generateDialogVisible.value = true
}

// 报表模板
const handleTemplate = () => {
  ElMessage.info('报表模板功能开发中...')
}

// 查看报表
const handleView = (row) => {
  currentReport.value = { ...row }
  previewDialogVisible.value = true

  nextTick(() => {
    if (row.reportType === 'finance' && row.data) {
      initFinanceChart()
    }
  })
}

// 下载报表
const handleDownload = (row) => {
  if (row.status !== 2) {
    ElMessage.warning('报表未生成完成，无法下载')
    return
  }
  ElMessage.success(`正在下载报表: ${row.reportName}`)
}

// 分享报表
const handleShare = (row) => {
  currentReport.value = { ...row }
  shareDialogVisible.value = true
}

// 定时报表
const handleSchedule = (row) => {
  ElMessage.info('定时报表功能开发中...')
}

// 删除报表
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除报表"${row.reportName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 提交生成报表
const handleGenerateSubmit = async () => {
  if (!generateFormRef.value) return

  try {
    await generateFormRef.value.validate()
    generateLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('报表生成任务已提交，请稍后查看生成结果')
      generateDialogVisible.value = false
      generateLoading.value = false
      fetchData()
    }, 2000)
  } catch (error) {
    generateLoading.value = false
  }
}

// 关闭生成报表对话框
const handleGenerateDialogClose = () => {
  generateFormRef.value?.resetFields()
  generateLoading.value = false
}

// 关闭预览对话框
const handlePreviewDialogClose = () => {
  if (financeChart) {
    financeChart.dispose()
    financeChart = null
  }
  previewDialogVisible.value = false
}

// 提交分享
const handleShareSubmit = async () => {
  shareLoading.value = true

  try {
    // 模拟API请求
    setTimeout(() => {
      if (shareForm.shareType === 'link') {
        // 生成分享链接
        shareLink.value = `https://property.example.com/share/report/${currentReport.value.reportId}?token=${Date.now()}`
      } else {
        // 发送邮件
        ElMessage.success('报表已通过邮件分享')
        shareDialogVisible.value = false
      }
      shareLoading.value = false
    }, 1000)
  } catch (error) {
    shareLoading.value = false
    ElMessage.error('分享失败')
  }
}

// 复制分享链接
const copyShareLink = () => {
  navigator.clipboard.writeText(shareLink.value)
    .then(() => {
      ElMessage.success('链接已复制到剪贴板')
    })
    .catch(() => {
      ElMessage.error('复制失败')
    })
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

  .report-preview {
    .preview-header {
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #eee;

      h3 {
        margin: 0 0 10px 0;
        color: #333;
      }

      .preview-meta {
        display: flex;
        gap: 20px;
        color: #666;
        font-size: 14px;
      }
    }

    .preview-content {
      .chart-container {
        background: #fff;
        border-radius: 4px;
        padding: 20px;
      }
    }
  }

  .share-content {
    .share-link {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }
  }
}
</style>