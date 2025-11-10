<template>
  <div class="notification-settings">
    <!-- 设置面板 -->
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>通知设置</span>
          <el-button type="primary" @click="handleSaveSettings">
            保存设置
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 通用设置 -->
        <el-tab-pane label="通用设置" name="general">
          <div class="settings-section">
            <h3>基础设置</h3>
            <el-form :model="settings.general" label-width="200px">
              <el-form-item label="启用消息通知">
                <el-switch
                  v-model="settings.general.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">控制整个消息通知系统的开关</div>
              </el-form-item>

              <el-form-item label="默认消息保留天数">
                <el-input-number
                  v-model="settings.general.retentionDays"
                  :min="7"
                  :max="365"
                  :step="1"
                />
                <div class="form-help">系统将自动删除超过此天数的消息记录</div>
              </el-form-item>

              <el-form-item label="每页显示消息数">
                <el-select v-model="settings.general.pageSize" style="width: 120px">
                  <el-option label="10条" :value="10" />
                  <el-option label="20条" :value="20" />
                  <el-option label="50条" :value="50" />
                  <el-option label="100条" :value="100" />
                </el-select>
                <div class="form-help">设置消息列表每页默认显示的消息数量</div>
              </el-form-item>

              <el-form-item label="启用消息提醒">
                <el-switch
                  v-model="settings.general.enableReminder"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">有新消息时是否弹出提醒</div>
              </el-form-item>

              <el-form-item label="自动刷新间隔">
                <el-select v-model="settings.general.refreshInterval" style="width: 150px">
                  <el-option label="不自动刷新" :value="0" />
                  <el-option label="30秒" :value="30" />
                  <el-option label="1分钟" :value="60" />
                  <el-option label="5分钟" :value="300" />
                  <el-option label="10分钟" :value="600" />
                </el-select>
                <div class="form-help">消息列表自动刷新的时间间隔</div>
              </el-form-item>
            </el-form>
          </div>

          <div class="settings-section">
            <h3>消息处理设置</h3>
            <el-form :model="settings.general" label-width="200px">
              <el-form-item label="阅读后自动标记">
                <el-switch
                  v-model="settings.general.autoMarkRead"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">点击查看消息详情后自动标记为已读</div>
              </el-form-item>

              <el-form-item label="批量操作确认">
                <el-switch
                  v-model="settings.general.confirmBatch"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">批量删除或标记已读时是否弹出确认对话框</div>
              </el-form-item>

              <el-form-item label="消息预览字数">
                <el-input-number
                  v-model="settings.general.previewLength"
                  :min="50"
                  :max="500"
                  :step="10"
                />
                <div class="form-help">消息列表中显示的消息内容预览字数</div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 通知渠道设置 -->
        <el-tab-pane label="通知渠道" name="channels">
          <div class="settings-section">
            <h3>应用内通知</h3>
            <el-form :model="settings.channels.app" label-width="200px">
              <el-form-item label="启用应用内通知">
                <el-switch
                  v-model="settings.channels.app.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="显示位置">
                <el-radio-group v-model="settings.channels.app.position">
                  <el-radio value="top-right">右上角</el-radio>
                  <el-radio value="top-left">左上角</el-radio>
                  <el-radio value="bottom-right">右下角</el-radio>
                  <el-radio value="bottom-left">左下角</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="显示时长">
                <el-input-number
                  v-model="settings.channels.app.duration"
                  :min="1000"
                  :max="10000"
                  :step="1000"
                />
                <span style="margin-left: 8px;">毫秒</span>
              </el-form-item>

              <el-form-item label="显示类型">
                <el-checkbox-group v-model="settings.channels.app.types">
                  <el-checkbox value="system">系统通知</el-checkbox>
                  <el-checkbox value="property">物业通知</el-checkbox>
                  <el-checkbox value="bill">账单通知</el-checkbox>
                  <el-checkbox value="repair">维修通知</el-checkbox>
                  <el-checkbox value="activity">社区活动</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>

          <div class="settings-section">
            <h3>短信通知</h3>
            <el-form :model="settings.channels.sms" label-width="200px">
              <el-form-item label="启用短信通知">
                <el-switch
                  v-model="settings.channels.sms.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="短信服务商">
                <el-select v-model="settings.channels.sms.provider" style="width: 200px">
                  <el-option label="阿里云短信" value="aliyun" />
                  <el-option label="腾讯云短信" value="tencent" />
                  <el-option label="华为云短信" value="huawei" />
                </el-select>
              </el-form-item>

              <el-form-item label="API Key">
                <el-input
                  v-model="settings.channels.sms.apiKey"
                  type="password"
                  show-password
                  style="width: 300px"
                  placeholder="请输入API Key"
                />
              </el-form-item>

              <el-form-item label="API Secret">
                <el-input
                  v-model="settings.channels.sms.apiSecret"
                  type="password"
                  show-password
                  style="width: 300px"
                  placeholder="请输入API Secret"
                />
              </el-form-item>

              <el-form-item label="签名">
                <el-input
                  v-model="settings.channels.sms.signature"
                  style="width: 200px"
                  placeholder="请输入短信签名"
                />
              </el-form-item>

              <el-form-item label="每日发送限制">
                <el-input-number
                  v-model="settings.channels.sms.dailyLimit"
                  :min="100"
                  :max="10000"
                  :step="100"
                />
                <span style="margin-left: 8px;">条</span>
              </el-form-item>

              <el-form-item label="发送类型">
                <el-checkbox-group v-model="settings.channels.sms.types">
                  <el-checkbox value="urgent">紧急通知</el-checkbox>
                  <el-checkbox value="bill">账单通知</el-checkbox>
                  <el-checkbox value="repair">维修通知</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>

          <div class="settings-section">
            <h3>邮件通知</h3>
            <el-form :model="settings.channels.email" label-width="200px">
              <el-form-item label="启用邮件通知">
                <el-switch
                  v-model="settings.channels.email.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="SMTP服务器">
                <el-input
                  v-model="settings.channels.email.smtpHost"
                  style="width: 200px"
                  placeholder="smtp.example.com"
                />
              </el-form-item>

              <el-form-item label="SMTP端口">
                <el-input-number
                  v-model="settings.channels.email.smtpPort"
                  :min="1"
                  :max="65535"
                  style="width: 120px"
                />
              </el-form-item>

              <el-form-item label="发件人邮箱">
                <el-input
                  v-model="settings.channels.email.fromEmail"
                  style="width: 250px"
                  placeholder="noreply@example.com"
                />
              </el-form-item>

              <el-form-item label="邮箱密码">
                <el-input
                  v-model="settings.channels.email.password"
                  type="password"
                  show-password
                  style="width: 200px"
                  placeholder="请输入邮箱密码"
                />
              </el-form-item>

              <el-form-item label="启用SSL">
                <el-switch
                  v-model="settings.channels.email.enableSSL"
                  active-text="是"
                  inactive-text="否"
                />
              </el-form-item>

              <el-form-item label="发送类型">
                <el-checkbox-group v-model="settings.channels.email.types">
                  <el-checkbox value="system">系统通知</el-checkbox>
                  <el-checkbox value="property">物业通知</el-checkbox>
                  <el-checkbox value="activity">社区活动</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 权限设置 -->
        <el-tab-pane label="权限设置" name="permissions">
          <div class="settings-section">
            <h3>消息管理权限</h3>
            <el-table :data="permissionsData" border style="width: 100%">
              <el-table-column prop="role" label="角色" width="150" />
              <el-table-column label="查看消息" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.permissions.view" />
                </template>
              </el-table-column>
              <el-table-column label="发送通知" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.permissions.send" />
                </template>
              </el-table-column>
              <el-table-column label="模板管理" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.permissions.template" />
                </template>
              </el-table-column>
              <el-table-column label="批量操作" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.permissions.batch" />
                </template>
              </el-table-column>
              <el-table-column label="系统设置" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.permissions.settings" />
                </template>
              </el-table-column>
              <el-table-column prop="description" label="说明" />
            </el-table>
          </div>

          <div class="settings-section">
            <h3>消息类型权限</h3>
            <el-table :data="typePermissionsData" border style="width: 100%">
              <el-table-column prop="role" label="角色" width="150" />
              <el-table-column label="系统通知" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.types.system" />
                </template>
              </el-table-column>
              <el-table-column label="物业通知" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.types.property" />
                </template>
              </el-table-column>
              <el-table-column label="账单通知" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.types.bill" />
                </template>
              </el-table-column>
              <el-table-column label="维修通知" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.types.repair" />
                </template>
              </el-table-column>
              <el-table-column label="社区活动" width="120" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.types.activity" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 高级设置 -->
        <el-tab-pane label="高级设置" name="advanced">
          <div class="settings-section">
            <h3>性能设置</h3>
            <el-form :model="settings.advanced" label-width="200px">
              <el-form-item label="启用消息队列">
                <el-switch
                  v-model="settings.advanced.enableQueue"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">使用消息队列处理大量消息发送</div>
              </el-form-item>

              <el-form-item label="队列处理并发数">
                <el-input-number
                  v-model="settings.advanced.queueConcurrency"
                  :min="1"
                  :max="20"
                  :step="1"
                  :disabled="!settings.advanced.enableQueue"
                />
                <div class="form-help">同时处理的消息队列任务数量</div>
              </el-form-item>

              <el-form-item label="启用缓存">
                <el-switch
                  v-model="settings.advanced.enableCache"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">缓存消息列表和模板数据以提高性能</div>
              </el-form-item>

              <el-form-item label="缓存过期时间">
                <el-input-number
                  v-model="settings.advanced.cacheExpire"
                  :min="300"
                  :max="3600"
                  :step="60"
                  :disabled="!settings.advanced.enableCache"
                />
                <span style="margin-left: 8px;">秒</span>
              </el-form-item>
            </el-form>
          </div>

          <div class="settings-section">
            <h3>安全设置</h3>
            <el-form :model="settings.advanced" label-width="200px">
              <el-form-item label="启用消息加密">
                <el-switch
                  v-model="settings.advanced.enableEncryption"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">对敏感消息内容进行加密存储</div>
              </el-form-item>

              <el-form-item label="操作日志记录">
                <el-switch
                  v-model="settings.advanced.enableLog"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <div class="form-help">记录所有消息操作日志</div>
              </el-form-item>

              <el-form-item label="IP白名单">
                <el-input
                  v-model="settings.advanced.ipWhitelist"
                  type="textarea"
                  :rows="3"
                  placeholder="每行一个IP地址，留空表示不限制"
                />
                <div class="form-help">限制只有指定IP可以访问消息系统</div>
              </el-form-item>

              <el-form-item label="频率限制">
                <el-input-number
                  v-model="settings.advanced.rateLimit"
                  :min="10"
                  :max="1000"
                  :step="10"
                />
                <span style="margin-left: 8px;">条/分钟</span>
                <div class="form-help">每个用户每分钟最多发送的消息数量</div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 测试对话框 -->
    <el-dialog v-model="testDialogVisible" title="发送测试消息" width="500px">
      <el-form :model="testForm" label-width="100px">
        <el-form-item label="消息类型">
          <el-select v-model="testForm.type" style="width: 100%">
            <el-option label="系统通知" value="system" />
            <el-option label="物业通知" value="property" />
            <el-option label="账单通知" value="bill" />
            <el-option label="维修通知" value="repair" />
            <el-option label="社区活动" value="activity" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送渠道">
          <el-checkbox-group v-model="testForm.channels">
            <el-checkbox value="app">应用内</el-checkbox>
            <el-checkbox value="sms">短信</el-checkbox>
            <el-checkbox value="email">邮件</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="测试内容">
          <el-input
            v-model="testForm.content"
            type="textarea"
            :rows="3"
            placeholder="请输入测试消息内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSendTest">
            发送测试
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const activeTab = ref('general')
const testDialogVisible = ref(false)

// 设置数据
const settings = reactive({
  general: {
    enabled: true,
    retentionDays: 90,
    pageSize: 20,
    enableReminder: true,
    refreshInterval: 60,
    autoMarkRead: true,
    confirmBatch: true,
    previewLength: 100
  },
  channels: {
    app: {
      enabled: true,
      position: 'top-right',
      duration: 3000,
      types: ['system', 'property', 'bill', 'repair', 'activity']
    },
    sms: {
      enabled: false,
      provider: 'aliyun',
      apiKey: '',
      apiSecret: '',
      signature: '',
      dailyLimit: 1000,
      types: ['urgent', 'bill', 'repair']
    },
    email: {
      enabled: false,
      smtpHost: '',
      smtpPort: 587,
      fromEmail: '',
      password: '',
      enableSSL: true,
      types: ['system', 'property', 'activity']
    }
  },
  advanced: {
    enableQueue: true,
    queueConcurrency: 5,
    enableCache: true,
    cacheExpire: 1800,
    enableEncryption: false,
    enableLog: true,
    ipWhitelist: '',
    rateLimit: 100
  }
})

// 权限数据
const permissionsData = ref([
  {
    role: '系统管理员',
    permissions: {
      view: true,
      send: true,
      template: true,
      batch: true,
      settings: true
    },
    description: '拥有所有权限'
  },
  {
    role: '物业管理员',
    permissions: {
      view: true,
      send: true,
      template: true,
      batch: true,
      settings: false
    },
    description: '可管理除系统设置外的所有功能'
  },
  {
    role: '维修人员',
    permissions: {
      view: true,
      send: true,
      template: false,
      batch: false,
      settings: false
    },
    description: '可查看和发送维修相关消息'
  },
  {
    role: '业主',
    permissions: {
      view: true,
      send: false,
      template: false,
      batch: false,
      settings: false
    },
    description: '只能查看接收到的消息'
  }
])

// 类型权限数据
const typePermissionsData = ref([
  {
    role: '系统管理员',
    types: {
      system: true,
      property: true,
      bill: true,
      repair: true,
      activity: true
    }
  },
  {
    role: '物业管理员',
    types: {
      system: false,
      property: true,
      bill: true,
      repair: true,
      activity: true
    }
  },
  {
    role: '维修人员',
    types: {
      system: false,
      property: false,
      bill: false,
      repair: true,
      activity: false
    }
  },
  {
    role: '业主',
    types: {
      system: true,
      property: true,
      bill: true,
      repair: true,
      activity: true
    }
  }
])

// 测试表单
const testForm = reactive({
  type: 'system',
  channels: ['app'],
  content: '这是一条测试消息'
})

// 保存设置
const handleSaveSettings = async () => {
  try {
    // 模拟保存设置
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('设置保存成功')
  } catch {
    ElMessage.error('设置保存失败')
  }
}

// 发送测试消息
const handleSendTest = async () => {
  try {
    // 模拟发送测试消息
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('测试消息发送成功')
    testDialogVisible.value = false
  } catch {
    ElMessage.error('测试消息发送失败')
  }
}

// 重置设置
const handleResetSettings = async () => {
  try {
    await ElMessageBox.confirm('确定要重置所有设置为默认值吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 重置为默认值
    ElMessage.success('设置已重置为默认值')
  } catch {
    // 用户取消
  }
}

// 导出设置
const handleExportSettings = () => {
  const exportData = JSON.stringify(settings, null, 2)
  const dataBlob = new Blob([exportData], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'notification-settings.json'
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('设置导出成功')
}

// 导入设置
const handleImportSettings = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => {
        try {
          const importedSettings = JSON.parse(event.target.result)
          Object.assign(settings, importedSettings)
          ElMessage.success('设置导入成功')
        } catch {
          ElMessage.error('设置文件格式错误')
        }
      }
      reader.readAsText(file)
    }
  }
  input.click()
}

// 组件挂载
onMounted(() => {
  // 模拟加载设置
})
</script>

<style lang="scss" scoped>
.notification-settings {
  .settings-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .settings-section {
    margin-bottom: 32px;

    h3 {
      margin: 0 0 16px 0;
      padding-bottom: 8px;
      border-bottom: 1px solid #eee;
      color: #333;
      font-size: 16px;
      font-weight: 600;
    }

    .form-help {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }

    :deep(.el-form-item__label) {
      font-weight: 500;
      color: #333;
    }

    :deep(.el-switch__label) {
      font-weight: normal;
    }
  }

  :deep(.el-tabs__content) {
    padding: 20px;
  }

  :deep(.el-table) {
    .el-switch {
      --el-switch-on-color: #13ce66;
      --el-switch-off-color: #ff4949;
    }
  }
}

@media (max-width: 768px) {
  .notification-settings {
    .settings-section {
      :deep(.el-form-item__label) {
        width: 100% !important;
        text-align: left;
        margin-bottom: 8px;
      }

      :deep(.el-form-item__content) {
        margin-left: 0 !important;
      }
    }

    :deep(.el-table) {
      font-size: 12px;
    }
  }
}
</style>