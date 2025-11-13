<template>
  <div class="notification-template">
    <!-- 操作区域 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="action-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索模板名称或类型"
            style="width: 250px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="action-right">
          <el-button type="primary" @click="handleCreateTemplate">
            <el-icon><Plus /></el-icon>
            新建模板
          </el-button>
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 模板列表 -->
    <el-card class="template-card">
      <el-table
        :data="templateList"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="name" label="模板名称" width="200">
          <template #default="{ row }">
            <div class="template-name">
              <el-icon :color="getTemplateTypeColor(row.type)">
                <component :is="getTemplateTypeIcon(row.type)" />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTemplateTypeTag(row.type)">
              {{ getTemplateTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.title" placement="top">
              <div class="template-title">{{ row.title }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="content" label="内容预览" min-width="300">
          <template #default="{ row }">
            <div class="template-content">
              {{ row.content.substring(0, 100) }}...
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="usageCount" label="使用次数" width="100" align="center" />

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="updateTime" label="更新时间" width="160" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewTemplate(row)"
            >
              查看
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEditTemplate(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              size="small"
              @click="handleCopyTemplate(row)"
            >
              复制
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)">
              <el-button link type="info" size="small">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="toggle">
                    {{ row.status === 1 ? '禁用' : '启用' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="preview">预览</el-dropdown-item>
                  <el-dropdown-item command="export">导出</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 模板编辑对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="templateFormRef"
        :model="templateForm"
        :rules="templateRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="消息类型" prop="type">
              <el-select v-model="templateForm.type" placeholder="选择消息类型" style="width: 100%">
                <el-option label="系统通知" value="system" />
                <el-option label="物业通知" value="property" />
                <el-option label="账单通知" value="bill" />
                <el-option label="维修通知" value="repair" />
                <el-option label="社区活动" value="activity" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标题模板" prop="title">
          <el-input v-model="templateForm.title" placeholder="请输入标题模板">
            <template #append>
              <el-button @click="showVariableHelp = !showVariableHelp">
                变量
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="内容模板" prop="content">
          <el-input
            v-model="templateForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入内容模板，可使用变量如：{业主姓名}、{费用金额}、{缴费日期}等"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发送方式">
              <el-checkbox-group v-model="templateForm.sendMethods">
                <el-checkbox value="app">应用内通知</el-checkbox>
                <el-checkbox value="sms">短信通知</el-checkbox>
                <el-checkbox value="email">邮件通知</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-radio-group v-model="templateForm.priority">
                <el-radio value="low">低</el-radio>
                <el-radio value="normal">普通</el-radio>
                <el-radio value="high">高</el-radio>
                <el-radio value="urgent">紧急</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态">
          <el-switch
            v-model="templateForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="templateForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <!-- 变量帮助 -->
      <el-collapse v-model="showVariableHelp" v-if="showVariableHelp">
        <el-collapse-item title="可用变量" name="variables">
          <div class="variable-help">
            <p>以下变量可在模板中使用：</p>
            <el-tag
              v-for="variable in availableVariables"
              :key="variable.name"
              class="variable-tag"
            >
              {{ variable.name }} - {{ variable.description }}
            </el-tag>
          </div>
        </el-collapse-item>
      </el-collapse>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTemplate">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 模板预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="模板预览"
      width="600px"
    >
      <div class="template-preview">
        <div class="preview-header">
          <div class="preview-meta">
            <el-tag :type="getTemplateTypeTag(currentTemplate?.type)">
              {{ getTemplateTypeName(currentTemplate?.type) }}
            </el-tag>
            <el-tag :type="currentTemplate?.status === 1 ? 'success' : 'info'">
              {{ currentTemplate?.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </div>
        </div>
        <div class="preview-content">
          <h3>{{ renderTemplate(currentTemplate?.title) }}</h3>
          <div class="preview-text">{{ renderTemplate(currentTemplate?.content) }}</div>
        </div>
        <div class="preview-footer">
          <div class="preview-info">
            <p>发送方式：{{ getSendMethodsText(currentTemplate?.sendMethods) }}</p>
            <p>优先级：{{ getPriorityText(currentTemplate?.priority) }}</p>
            <p>使用次数：{{ currentTemplate?.usageCount || 0 }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleUseTemplate">
            使用此模板
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  ArrowDown,
  Bell,
  Tools,
  Money,
  Message,
  Star
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const templateDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const showVariableHelp = ref(false)
const isEdit = ref(false)
const currentTemplate = ref(null)
const templateFormRef = ref(null)

// 模板列表
const templateList = ref([])

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 模板表单
const templateForm = reactive({
  id: null,
  name: '',
  type: 'property',
  title: '',
  content: '',
  sendMethods: ['app'],
  priority: 'normal',
  status: 1,
  remark: ''
})

// 可用变量
const availableVariables = [
  { name: '{业主姓名}', description: '业主姓名' },
  { name: '{房产地址}', description: '房产地址' },
  { name: '{费用金额}', description: '费用金额' },
  { name: '{缴费日期}', description: '缴费日期' },
  { name: '{账单编号}', description: '账单编号' },
  { name: '{维修内容}', description: '维修内容' },
  { name: '{预计时间}', description: '预计完成时间' },
  { name: '{社区名称}', description: '社区名称' },
  { name: '{当前日期}', description: '当前日期' },
  { name: '{管理员姓名}', description: '管理员姓名' }
]

// 表单验证规则
const templateRules = {
  name: [
    { required: true, message: '请输入模板名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择消息类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题模板', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容模板', trigger: 'blur' }
  ]
}

// 对话框标题
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑模板' : '新建模板'
})

// 生成模拟模板数据
const generateTemplates = () => {
  const types = ['system', 'property', 'bill', 'repair', 'activity']
  const typeNames = {
    system: '系统通知',
    property: '物业通知',
    bill: '账单通知',
    repair: '维修通知',
    activity: '社区活动'
  }

  const templates = [
    {
      id: 1,
      name: '月度物业费催缴',
      type: 'bill',
      title: '物业费催缴通知 - {业主姓名}',
      content: '尊敬的{业主姓名}业主，您位于{房产地址}的房产，本年度{月份}物业费{费用金额}元已到期，请于{缴费日期}前完成缴费。感谢您的配合！',
      status: 1,
      usageCount: 156,
      updateTime: '2024-11-08 14:30',
      sendMethods: ['app', 'sms'],
      priority: 'high',
      remark: '月度物业费催缴模板'
    },
    {
      id: 2,
      name: '电梯维护通知',
      type: 'property',
      title: '电梯维护通知 - {楼栋号}',
      content: '各位业主，{楼栋号}电梯将于{开始时间}至{结束时间}进行维护保养，维护期间电梯将暂停使用。请提前安排好出行时间，给您带来的不便敬请谅解。',
      status: 1,
      usageCount: 45,
      updateTime: '2024-11-07 09:15',
      sendMethods: ['app', 'sms'],
      priority: 'normal',
      remark: '电梯维护通知模板'
    },
    {
      id: 3,
      name: '维修完成通知',
      type: 'repair',
      title: '维修完成通知 - {维修内容}',
      content: '尊敬的{业主姓名}业主，您报修的{维修内容}已完成，维修人员：{维修人员}，完成时间：{完成时间}。如有问题请及时联系物业。',
      status: 1,
      usageCount: 89,
      updateTime: '2024-11-06 16:45',
      sendMethods: ['app'],
      priority: 'normal',
      remark: '维修完成通知模板'
    },
    {
      id: 4,
      name: '社区活动通知',
      type: 'activity',
      title: '{活动名称}活动通知',
      content: '亲爱的业主们，{社区名称}将于{活动时间}举办{活动名称}活动，活动地点：{活动地点}。欢迎大家踊跃参加，共同营造和谐社区氛围。',
      status: 0,
      usageCount: 23,
      updateTime: '2024-11-05 11:20',
      sendMethods: ['app', 'email'],
      priority: 'low',
      remark: '社区活动通知模板'
    },
    {
      id: 5,
      name: '系统维护通知',
      type: 'system',
      title: '系统维护通知',
      content: '尊敬的用户，{系统名称}将于{维护时间}进行系统维护，期间系统可能暂时无法使用。维护预计持续{预计时长}，给您带来的不便敬请谅解。',
      status: 1,
      usageCount: 12,
      updateTime: '2024-11-04 13:00',
      sendMethods: ['app', 'sms'],
      priority: 'urgent',
      remark: '系统维护通知模板'
    }
  ]

  return templates
}

// 获取模板类型颜色
const getTemplateTypeColor = (type) => {
  const colorMap = {
    system: '#F56C6C',
    property: '#409EFF',
    bill: '#E6A23C',
    repair: '#67C23A',
    activity: '#909399'
  }
  return colorMap[type] || '#909399'
}

// 获取模板类型图标
const getTemplateTypeIcon = (type) => {
  const iconMap = {
    system: Bell,
    property: Message,
    bill: Money,
    repair: Tools,
    activity: Star
  }
  return iconMap[type] || Message
}

// 获取模板类型标签
const getTemplateTypeTag = (type) => {
  const tagMap = {
    system: 'danger',
    property: 'primary',
    bill: 'warning',
    repair: 'success',
    activity: 'info'
  }
  return tagMap[type] || 'info'
}

// 获取模板类型名称
const getTemplateTypeName = (type) => {
  const nameMap = {
    system: '系统通知',
    property: '物业通知',
    bill: '账单通知',
    repair: '维修通知',
    activity: '社区活动'
  }
  return nameMap[type] || '未知'
}

// 获取发送方式文本
const getSendMethodsText = (methods) => {
  const methodMap = {
    app: '应用内',
    sms: '短信',
    email: '邮件'
  }
  return methods.map(method => methodMap[method] || method).join('、')
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const priorityMap = {
    low: '低',
    normal: '普通',
    high: '高',
    urgent: '紧急'
  }
  return priorityMap[priority] || '普通'
}

// 渲染模板
const renderTemplate = (template) => {
  if (!template) return ''

  let rendered = template
  const sampleData = {
    '{业主姓名}': '张三',
    '{房产地址}': '3号楼2单元501',
    '{费用金额}': '1250.00',
    '{缴费日期}': '2024-11-15',
    '{账单编号}': 'BILL20241115001',
    '{维修内容}': '水管漏水',
    '{预计时间}': '2024-11-10 14:00',
    '{社区名称}': '阳光花园',
    '{当前日期}': '2024-11-09',
    '{管理员姓名}': '李四',
    '{楼栋号}': '1号楼',
    '{开始时间}': '2024-11-10 08:00',
    '{结束时间}': '2024-11-10 12:00',
    '{维修人员}': '王师傅',
    '{完成时间}': '2024-11-09 15:30',
    '{活动名称}': '社区篮球赛',
    '{活动时间}': '2024-11-12 15:00',
    '{活动地点}': '社区篮球场',
    '{系统名称}': '物业管理系统',
    '{维护时间}': '2024-11-15 02:00-04:00',
    '{预计时长}': '2小时',
    '{月份}': '11月'
  }

  Object.keys(sampleData).forEach(key => {
    rendered = rendered.replace(new RegExp(key, 'g'), sampleData[key])
  })

  return rendered
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadTemplates()
}

// 刷新
const handleRefresh = () => {
  loadTemplates()
  ElMessage.success('刷新成功')
}

// 新建模板
const handleCreateTemplate = () => {
  isEdit.value = false
  Object.assign(templateForm, {
    id: null,
    name: '',
    type: 'property',
    title: '',
    content: '',
    sendMethods: ['app'],
    priority: 'normal',
    status: 1,
    remark: ''
  })
  templateDialogVisible.value = true
}

// 编辑模板
const handleEditTemplate = (template) => {
  isEdit.value = true
  Object.assign(templateForm, { ...template })
  templateDialogVisible.value = true
}

// 复制模板
const handleCopyTemplate = (template) => {
  isEdit.value = false
  Object.assign(templateForm, {
    ...template,
    id: null,
    name: `${template.name} - 副本`,
    usageCount: 0
  })
  templateDialogVisible.value = true
  ElMessage.success('模板已复制')
}

// 查看模板
const handleViewTemplate = (template) => {
  currentTemplate.value = template
  previewDialogVisible.value = true
}

// 处理下拉菜单命令
const handleCommand = async (command, template) => {
  switch (command) {
    case 'toggle':
      await handleToggleStatus(template)
      break
    case 'preview':
      handleViewTemplate(template)
      break
    case 'export':
      handleExportTemplate(template)
      break
    case 'delete':
      await handleDeleteTemplate(template)
      break
  }
}

// 切换状态
const handleToggleStatus = async (template) => {
  try {
    const newStatus = template.status === 1 ? 0 : 1
    template.status = newStatus
    ElMessage.success(`${newStatus === 1 ? '启用' : '禁用'}成功`)
  } catch {
    // 处理错误
  }
}

// 导出模板
const handleExportTemplate = (template) => {
  // 模拟导出功能
  const exportData = {
    name: template.name,
    type: template.type,
    title: template.title,
    content: template.content,
    sendMethods: template.sendMethods,
    priority: template.priority,
    remark: template.remark
  }

  const dataStr = JSON.stringify(exportData, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${template.name}.json`
  link.click()
  URL.revokeObjectURL(url)

  ElMessage.success('导出成功')
}

// 删除模板
const handleDeleteTemplate = async (template) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = templateList.value.findIndex(t => t.id === template.id)
    if (index > -1) {
      templateList.value.splice(index, 1)
      pagination.total--
    }

    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

// 保存模板
const handleSaveTemplate = async () => {
  try {
    await templateFormRef.value.validate()

    if (isEdit.value) {
      // 编辑
      const index = templateList.value.findIndex(t => t.id === templateForm.id)
      if (index > -1) {
        templateList.value[index] = {
          ...templateForm,
          updateTime: new Date().toLocaleString('zh-CN')
        }
      }
      ElMessage.success('修改成功')
    } else {
      // 新建
      const newTemplate = {
        ...templateForm,
        id: Date.now(),
        usageCount: 0,
        updateTime: new Date().toLocaleString('zh-CN')
      }
      templateList.value.unshift(newTemplate)
      pagination.total++
      ElMessage.success('创建成功')
    }

    templateDialogVisible.value = false
  } catch {
    // 验证失败
  }
}

// 使用模板
const handleUseTemplate = () => {
  // 跳转到发送通知页面，并传递模板数据
  ElMessage.success('跳转到发送通知页面')
  previewDialogVisible.value = false
}

// 对话框关闭处理
const handleDialogClose = () => {
  templateFormRef.value?.resetFields()
  showVariableHelp.value = false
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadTemplates()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadTemplates()
}

// 加载模板列表
const loadTemplates = () => {
  loading.value = true

  setTimeout(() => {
    const allTemplates = generateTemplates()

    // 搜索过滤
    let filteredTemplates = allTemplates.filter(template => {
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        return template.name.toLowerCase().includes(keyword) ||
               template.type.toLowerCase().includes(keyword) ||
               template.title.toLowerCase().includes(keyword)
      }
      return true
    })

    pagination.total = filteredTemplates.length

    // 分页处理
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    templateList.value = filteredTemplates.slice(start, end)

    loading.value = false
  }, 500)
}

// 组件挂载
onMounted(() => {
  loadTemplates()
})
</script>

<style lang="scss" scoped>
.notification-template {
  .action-card, .template-card {
    margin-bottom: 16px;
  }

  .action-card {
    .action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .template-name {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .template-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .template-content {
    color: #666;
    line-height: 1.4;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .variable-help {
    p {
      margin-bottom: 12px;
      color: #666;
    }

    .variable-tag {
      margin: 4px 8px 4px 0;
    }
  }

  .template-preview {
    .preview-header {
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;

      .preview-meta {
        display: flex;
        gap: 8px;
      }
    }

    .preview-content {
      h3 {
        margin: 0 0 12px 0;
        color: #333;
      }

      .preview-text {
        color: #666;
        line-height: 1.6;
        margin-bottom: 16px;
      }
    }

    .preview-footer {
      .preview-info {
        p {
          margin: 4px 0;
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .notification-template {
    .action-card .action-bar {
      flex-direction: column;
      gap: 12px;
    }

    :deep(.el-table) {
      font-size: 12px;
    }

    .template-preview {
      .preview-header .preview-meta {
        flex-direction: column;
        gap: 4px;
      }
    }
  }
}
</style>