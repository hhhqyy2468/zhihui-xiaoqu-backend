<template>
  <div class="notification-center">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="消息类型">
          <el-select v-model="filterForm.type" placeholder="选择消息类型" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="系统通知" value="system" />
            <el-option label="物业通知" value="property" />
            <el-option label="账单通知" value="bill" />
            <el-option label="维修通知" value="repair" />
            <el-option label="社区活动" value="activity" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="未读" value="0" />
            <el-option label="已读" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 220px"
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

    <!-- 操作区域 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="action-left">
          <el-button
            type="primary"
            @click="handleMarkAllRead"
            :disabled="selectedMessages.length === 0"
          >
            <el-icon><Check /></el-icon>
            标记已读
          </el-button>
          <el-button
            type="danger"
            @click="handleBatchDelete"
            :disabled="selectedMessages.length === 0"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="action-right">
          <el-button type="success" @click="handleCreateNotification">
            <el-icon><Plus /></el-icon>
            发送通知
          </el-button>
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 消息列表 -->
    <el-card class="message-card">
      <div class="message-list">
        <div
          v-for="message in messageList"
          :key="message.id"
          class="message-item"
          :class="{ 'message-unread': message.status === 0 }"
        >
          <div class="message-checkbox">
            <el-checkbox v-model="selectedMessages" :value="message.id" />
          </div>

          <div class="message-icon">
            <el-avatar
              :size="40"
              :style="{ backgroundColor: getMessageTypeColor(message.type) }"
            >
              <el-icon :size="20">
                <component :is="getMessageTypeIcon(message.type)" />
              </el-icon>
            </el-avatar>
          </div>

          <div class="message-content" @click="handleViewMessage(message)">
            <div class="message-header">
              <div class="message-title">{{ message.title }}</div>
              <div class="message-time">{{ message.createTime }}</div>
            </div>
            <div class="message-desc">{{ message.content }}</div>
            <div class="message-footer">
              <el-tag size="small" :type="getMessageTypeTag(message.type)">
                {{ getMessageTypeName(message.type) }}
              </el-tag>
              <div class="message-actions" @click.stop>
                <el-button
                  v-if="message.status === 0"
                  link
                  type="primary"
                  size="small"
                  @click="handleMarkRead(message)"
                >
                  标记已读
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  @click="handleDeleteMessage(message)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

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

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="messageDialogVisible"
      :title="currentMessage?.title"
      width="600px"
      @open="handleMessageDialogOpen"
    >
      <div class="message-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag :type="getMessageTypeTag(currentMessage?.type)">
              {{ getMessageTypeName(currentMessage?.type) }}
            </el-tag>
            <span class="detail-time">{{ currentMessage?.createTime }}</span>
          </div>
        </div>
        <div class="detail-content">
          {{ currentMessage?.content }}
        </div>
        <div v-if="currentMessage?.images" class="detail-images">
          <el-image
            v-for="(image, index) in currentMessage.images"
            :key="index"
            :src="image"
            :preview-src-list="currentMessage.images"
            fit="cover"
            class="detail-image"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="messageDialogVisible = false">关闭</el-button>
          <el-button
            v-if="currentMessage?.status === 0"
            type="primary"
            @click="handleMarkReadAndClose"
          >
            标记已读
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 发送通知对话框 -->
    <el-dialog
      v-model="composeDialogVisible"
      title="发送通知"
      width="600px"
    >
      <el-form
        ref="composeFormRef"
        :model="composeForm"
        :rules="composeRules"
        label-width="80px"
      >
        <el-form-item label="接收人" prop="recipients">
          <el-select
            v-model="composeForm.recipients"
            placeholder="选择接收人"
            multiple
            style="width: 100%"
          >
            <el-option label="全部业主" value="all_owners" />
            <el-option label="全部住户" value="all_residents" />
            <el-option label="物业管理员" value="property_managers" />
            <el-option label="维修人员" value="workers" />
            <el-option-group label="按楼栋选择">
              <el-option
                v-for="building in buildings"
                :key="building.id"
                :label="`${building.name}全体业主`"
                :value="`building_${building.id}`"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="消息类型" prop="type">
          <el-radio-group v-model="composeForm.type">
            <el-radio value="system">系统通知</el-radio>
            <el-radio value="property">物业通知</el-radio>
            <el-radio value="bill">账单通知</el-radio>
            <el-radio value="repair">维修通知</el-radio>
            <el-radio value="activity">社区活动</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="composeForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="composeForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="composeForm.priority">
            <el-radio value="low">低</el-radio>
            <el-radio value="normal">普通</el-radio>
            <el-radio value="high">高</el-radio>
            <el-radio value="urgent">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发送方式">
          <el-checkbox-group v-model="composeForm.sendMethods">
            <el-checkbox value="app">应用内通知</el-checkbox>
            <el-checkbox value="sms">短信通知</el-checkbox>
            <el-checkbox value="email">邮件通知</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="composeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSendNotification">
            发送
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Check,
  Delete,
  Plus,
  Bell,
  Tools,
  Money,
  Message,
  Star
} from '@element-plus/icons-vue'

// 筛选表单
const filterForm = reactive({
  type: '',
  status: '',
  dateRange: []
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 消息列表
const messageList = ref([])
const selectedMessages = ref([])

// 对话框状态
const messageDialogVisible = ref(false)
const composeDialogVisible = ref(false)
const currentMessage = ref(null)
const composeFormRef = ref(null)

// 发送通知表单
const composeForm = reactive({
  recipients: [],
  type: 'property',
  title: '',
  content: '',
  priority: 'normal',
  sendMethods: ['app']
})

// 楼栋列表
const buildings = ref([
  { id: 1, name: '1号楼' },
  { id: 2, name: '2号楼' },
  { id: 3, name: '3号楼' }
])

// 表单验证规则
const composeRules = {
  recipients: [
    { required: true, message: '请选择接收人', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择消息类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ]
}

// 生成模拟消息数据
const generateMessages = () => {
  const types = ['system', 'property', 'bill', 'repair', 'activity']
  const typeNames = {
    system: '系统通知',
    property: '物业通知',
    bill: '账单通知',
    repair: '维修通知',
    activity: '社区活动'
  }

  const messages = []
  for (let i = 1; i <= 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = Math.random() > 0.7 ? 0 : 1

    messages.push({
      id: i,
      type,
      title: `${typeNames[type]} - 测试消息${i}`,
      content: `这是一条${typeNames[type]}的详细内容，包含了相关的通知信息。Lorem ipsum dolor sit amet, consectetur adipiscing elit.`,
      status,
      createTime: `2024-11-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')} ${String(Math.floor(Math.random() * 24)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
      sender: '系统管理员',
      priority: ['low', 'normal', 'high', 'urgent'][Math.floor(Math.random() * 4)]
    })
  }

  return messages
}

// 获取消息类型颜色
const getMessageTypeColor = (type) => {
  const colorMap = {
    system: '#F56C6C',
    property: '#409EFF',
    bill: '#E6A23C',
    repair: '#67C23A',
    activity: '#909399'
  }
  return colorMap[type] || '#909399'
}

// 获取消息类型图标
const getMessageTypeIcon = (type) => {
  const iconMap = {
    system: Bell,
    property: Message,
    bill: Money,
    repair: Tools,
    activity: Star
  }
  return iconMap[type] || Message
}

// 获取消息类型标签
const getMessageTypeTag = (type) => {
  const tagMap = {
    system: 'danger',
    property: 'primary',
    bill: 'warning',
    repair: 'success',
    activity: 'info'
  }
  return tagMap[type] || 'info'
}

// 获取消息类型名称
const getMessageTypeName = (type) => {
  const nameMap = {
    system: '系统通知',
    property: '物业通知',
    bill: '账单通知',
    repair: '维修通知',
    activity: '社区活动'
  }
  return nameMap[type] || '未知'
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadMessages()
}

// 重置筛选
const handleReset = () => {
  Object.assign(filterForm, {
    type: '',
    status: '',
    dateRange: []
  })
  handleSearch()
}

// 刷新
const handleRefresh = () => {
  loadMessages()
  ElMessage.success('刷新成功')
}

// 标记全部已读
const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要标记所有选中消息为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    messageList.value.forEach(msg => {
      if (selectedMessages.value.includes(msg.id)) {
        msg.status = 1
      }
    })

    selectedMessages.value = []
    ElMessage.success('标记成功')
  } catch {
    // 用户取消
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除所有选中消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    messageList.value = messageList.value.filter(msg => !selectedMessages.value.includes(msg.id))
    pagination.total -= selectedMessages.value.length
    selectedMessages.value = []

    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

// 查看消息详情
const handleViewMessage = (message) => {
  currentMessage.value = message
  messageDialogVisible.value = true
}

// 标记单条消息已读
const handleMarkRead = (message) => {
  message.status = 1
  ElMessage.success('标记成功')
}

// 删除消息
const handleDeleteMessage = async (message) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = messageList.value.findIndex(msg => msg.id === message.id)
    if (index > -1) {
      messageList.value.splice(index, 1)
      pagination.total--
    }

    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

// 打开发送通知对话框
const handleCreateNotification = () => {
  Object.assign(composeForm, {
    recipients: [],
    type: 'property',
    title: '',
    content: '',
    priority: 'normal',
    sendMethods: ['app']
  })
  composeDialogVisible.value = true
}

// 发送通知
const handleSendNotification = async () => {
  try {
    await composeFormRef.value.validate()

    // 模拟发送通知
    ElMessage.success('通知发送成功')
    composeDialogVisible.value = false

    // 刷新列表
    loadMessages()
  } catch {
    // 验证失败
  }
}

// 消息对话框打开时标记已读
const handleMessageDialogOpen = () => {
  if (currentMessage.value?.status === 0) {
    currentMessage.value.status = 1
  }
}

// 标记已读并关闭
const handleMarkReadAndClose = () => {
  if (currentMessage.value) {
    currentMessage.value.status = 1
  }
  messageDialogVisible.value = false
  ElMessage.success('标记成功')
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadMessages()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadMessages()
}

// 加载消息列表
const loadMessages = () => {
  const allMessages = generateMessages()

  // 筛选处理
  let filteredMessages = allMessages.filter(msg => {
    if (filterForm.type && msg.type !== filterForm.type) return false
    if (filterForm.status !== '' && msg.status !== Number(filterForm.status)) return false
    return true
  })

  pagination.total = filteredMessages.length

  // 分页处理
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  messageList.value = filteredMessages.slice(start, end)
}

// 组件挂载
onMounted(() => {
  loadMessages()
})
</script>

<style lang="scss" scoped>
.notification-center {
  .filter-card, .action-card, .message-card {
    margin-bottom: 16px;
  }

  .action-card {
    .action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .message-list {
    .message-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 16px;
      border: 1px solid #eee;
      border-radius: 8px;
      margin-bottom: 12px;
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        background: #f8f9fa;
        border-color: #e0e0e0;
      }

      &.message-unread {
        background: #f0f9ff;
        border-color: #bfdbfe;

        .message-title {
          font-weight: 600;
        }
      }

      .message-checkbox {
        padding-top: 8px;
      }

      .message-icon {
        flex-shrink: 0;
      }

      .message-content {
        flex: 1;
        min-width: 0;

        .message-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .message-title {
            font-size: 16px;
            color: #333;
            font-weight: 500;
          }

          .message-time {
            font-size: 14px;
            color: #999;
          }
        }

        .message-desc {
          font-size: 14px;
          color: #666;
          line-height: 1.5;
          margin-bottom: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .message-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .message-actions {
            display: flex;
            gap: 8px;
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .message-detail {
    .detail-header {
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;

      .detail-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .detail-time {
          font-size: 14px;
          color: #999;
        }
      }
    }

    .detail-content {
      font-size: 16px;
      line-height: 1.6;
      color: #333;
      margin-bottom: 16px;
    }

    .detail-images {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 12px;

      .detail-image {
        width: 100%;
        height: 150px;
        border-radius: 8px;
        cursor: pointer;
      }
    }
  }
}

@media (max-width: 768px) {
  .notification-center {
    .filter-card :deep(.el-form--inline) .el-form-item {
      display: block;
      margin-right: 0;
      margin-bottom: 12px;
    }

    .action-card .action-bar {
      flex-direction: column;
      gap: 12px;
    }

    .message-list .message-item {
      flex-direction: column;
      align-items: flex-start;
    }

    .message-list .message-item .message-content .message-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;
    }

    .message-list .message-item .message-content .message-footer {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
  }
}
</style>