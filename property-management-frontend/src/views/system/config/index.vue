<template>
  <div class="system-config">
    <!-- 配置面板 -->
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>系统配置</span>
          <div class="header-actions">
            <el-button @click="handleExportConfig">
              <el-icon><Download /></el-icon>
              导出配置
            </el-button>
            <el-button @click="handleImportConfig">
              <el-icon><Upload /></el-icon>
              导入配置
            </el-button>
            <el-button type="primary" @click="handleSaveConfig">
              <el-icon><Check /></el-icon>
              保存配置
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <div class="config-section">
            <h3>系统信息</h3>
            <el-form :model="config.basic" label-width="200px">
              <el-form-item label="系统名称">
                <el-input v-model="config.basic.systemName" style="width: 300px" />
              </el-form-item>

              <el-form-item label="系统版本">
                <el-input v-model="config.basic.version" style="width: 200px" readonly />
              </el-form-item>

              <el-form-item label="系统Logo">
                <el-upload
                  class="logo-uploader"
                  :show-file-list="false"
                  :on-success="handleLogoSuccess"
                  :before-upload="beforeLogoUpload"
                >
                  <img v-if="config.basic.logoUrl" :src="config.basic.logoUrl" class="logo" />
                  <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="form-help">建议尺寸：120x40px，支持PNG、JPG格式</div>
              </el-form-item>

              <el-form-item label="系统描述">
                <el-input
                  v-model="config.basic.description"
                  type="textarea"
                  :rows="3"
                  style="width: 500px"
                />
              </el-form-item>

              <el-form-item label="公司名称">
                <el-input v-model="config.basic.companyName" style="width: 300px" />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input v-model="config.basic.phone" style="width: 200px" />
              </el-form-item>

              <el-form-item label="邮箱地址">
                <el-input v-model="config.basic.email" style="width: 300px" />
              </el-form-item>

              <el-form-item label="系统地址">
                <el-input v-model="config.basic.address" style="width: 400px" />
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>界面设置</h3>
            <el-form :model="config.basic.ui" label-width="200px">
              <el-form-item label="默认主题">
                <el-select v-model="config.basic.ui.theme" style="width: 200px">
                  <el-option label="默认主题" value="default" />
                  <el-option label="暗黑主题" value="dark" />
                  <el-option label="蓝色主题" value="blue" />
                  <el-option label="绿色主题" value="green" />
                </el-select>
              </el-form-item>

              <el-form-item label="默认语言">
                <el-select v-model="config.basic.ui.language" style="width: 200px">
                  <el-option label="简体中文" value="zh-CN" />
                  <el-option label="繁体中文" value="zh-TW" />
                  <el-option label="English" value="en-US" />
                </el-select>
              </el-form-item>

              <el-form-item label="侧边栏默认展开">
                <el-switch
                  v-model="config.basic.ui.sidebarDefaultOpen"
                  active-text="展开"
                  inactive-text="收起"
                />
              </el-form-item>

              <el-form-item label="启用动画效果">
                <el-switch
                  v-model="config.basic.ui.enableAnimation"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="页面缓存数量">
                <el-input-number
                  v-model="config.basic.ui.cachePages"
                  :min="1"
                  :max="20"
                  :step="1"
                />
                <div class="form-help">设置最大缓存的页面数量</div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 安全配置 -->
        <el-tab-pane label="安全配置" name="security">
          <div class="config-section">
            <h3>登录设置</h3>
            <el-form :model="config.security.login" label-width="200px">
              <el-form-item label="密码复杂度要求">
                <el-checkbox-group v-model="config.security.login.passwordRules">
                  <el-checkbox value="length">长度至少8位</el-checkbox>
                  <el-checkbox value="uppercase">包含大写字母</el-checkbox>
                  <el-checkbox value="lowercase">包含小写字母</el-checkbox>
                  <el-checkbox value="number">包含数字</el-checkbox>
                  <el-checkbox value="special">包含特殊字符</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="密码有效期">
                <el-input-number
                  v-model="config.security.login.passwordExpire"
                  :min="0"
                  :max="365"
                  :step="1"
                />
                <span style="margin-left: 8px;">天（0表示永不过期）</span>
              </el-form-item>

              <el-form-item label="登录失败锁定">
                <el-switch
                  v-model="config.security.login.enableLock"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="最大失败次数" v-if="config.security.login.enableLock">
                <el-input-number
                  v-model="config.security.login.maxFailCount"
                  :min="3"
                  :max="10"
                  :step="1"
                />
              </el-form-item>

              <el-form-item label="锁定时间" v-if="config.security.login.enableLock">
                <el-input-number
                  v-model="config.security.login.lockTime"
                  :min="5"
                  :max="60"
                  :step="5"
                />
                <span style="margin-left: 8px;">分钟</span>
              </el-form-item>

              <el-form-item label="启用验证码">
                <el-switch
                  v-model="config.security.login.enableCaptcha"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="启用双因素认证">
                <el-switch
                  v-model="config.security.login.enable2FA"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="会话超时时间">
                <el-input-number
                  v-model="config.security.login.sessionTimeout"
                  :min="30"
                  :max="480"
                  :step="30"
                />
                <span style="margin-left: 8px;">分钟</span>
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>访问控制</h3>
            <el-form :model="config.security.access" label-width="200px">
              <el-form-item label="IP白名单">
                <el-switch
                  v-model="config.security.access.enableIpWhitelist"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="白名单IP" v-if="config.security.access.enableIpWhitelist">
                <el-input
                  v-model="config.security.access.ipWhitelist"
                  type="textarea"
                  :rows="4"
                  placeholder="每行一个IP地址或IP段，如：192.168.1.0/24"
                  style="width: 400px"
                />
              </el-form-item>

              <el-form-item label="访问频率限制">
                <el-switch
                  v-model="config.security.access.enableRateLimit"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="每分钟最大请求数" v-if="config.security.access.enableRateLimit">
                <el-input-number
                  v-model="config.security.access.maxRequestsPerMinute"
                  :min="10"
                  :max="1000"
                  :step="10"
                />
              </el-form-item>

              <el-form-item label="启用操作日志">
                <el-switch
                  v-model="config.security.access.enableOperationLog"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="日志保留天数" v-if="config.security.access.enableOperationLog">
                <el-input-number
                  v-model="config.security.access.logRetentionDays"
                  :min="7"
                  :max="365"
                  :step="1"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 邮件配置 -->
        <el-tab-pane label="邮件配置" name="email">
          <div class="config-section">
            <h3>SMTP设置</h3>
            <el-form :model="config.email.smtp" label-width="200px">
              <el-form-item label="启用邮件服务">
                <el-switch
                  v-model="config.email.smtp.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="SMTP服务器" v-if="config.email.smtp.enabled">
                <el-input v-model="config.email.smtp.host" style="width: 300px" placeholder="smtp.example.com" />
              </el-form-item>

              <el-form-item label="SMTP端口" v-if="config.email.smtp.enabled">
                <el-input-number v-model="config.email.smtp.port" :min="1" :max="65535" style="width: 120px" />
              </el-form-item>

              <el-form-item label="加密方式" v-if="config.email.smtp.enabled">
                <el-radio-group v-model="config.email.smtp.encryption">
                  <el-radio value="none">无加密</el-radio>
                  <el-radio value="ssl">SSL</el-radio>
                  <el-radio value="tls">TLS</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="发件人邮箱" v-if="config.email.smtp.enabled">
                <el-input v-model="config.email.smtp.fromEmail" style="width: 300px" placeholder="noreply@example.com" />
              </el-form-item>

              <el-form-item label="发件人名称" v-if="config.email.smtp.enabled">
                <el-input v-model="config.email.smtp.fromName" style="width: 200px" />
              </el-form-item>

              <el-form-item label="用户名" v-if="config.email.smtp.enabled">
                <el-input v-model="config.email.smtp.username" style="width: 300px" />
              </el-form-item>

              <el-form-item label="密码" v-if="config.email.smtp.enabled">
                <el-input
                  v-model="config.email.smtp.password"
                  type="password"
                  show-password
                  style="width: 200px"
                />
              </el-form-item>

              <el-form-item label="测试邮箱" v-if="config.email.smtp.enabled">
                <el-input v-model="testEmail" style="width: 300px" placeholder="输入测试邮箱地址" />
                <el-button type="primary" @click="handleTestEmail" style="margin-left: 12px">
                  发送测试
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>邮件模板</h3>
            <el-table :data="config.email.templates" border style="width: 100%">
              <el-table-column prop="name" label="模板名称" width="200" />
              <el-table-column prop="subject" label="主题" width="300" />
              <el-table-column prop="description" label="描述" />
              <el-table-column label="操作" width="150">
                <template #default="{ row, $index }">
                  <el-button link type="primary" size="small" @click="handleEditTemplate(row, $index)">
                    编辑
                  </el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteTemplate($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" @click="handleAddTemplate" style="margin-top: 12px">
              <el-icon><Plus /></el-icon>
              添加模板
            </el-button>
          </div>
        </el-tab-pane>

        <!-- 备份配置 -->
        <el-tab-pane label="备份配置" name="backup">
          <div class="config-section">
            <h3>自动备份设置</h3>
            <el-form :model="config.backup.auto" label-width="200px">
              <el-form-item label="启用自动备份">
                <el-switch
                  v-model="config.backup.auto.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="备份频率" v-if="config.backup.auto.enabled">
                <el-select v-model="config.backup.auto.frequency" style="width: 200px">
                  <el-option label="每天" value="daily" />
                  <el-option label="每周" value="weekly" />
                  <el-option label="每月" value="monthly" />
                </el-select>
              </el-form-item>

              <el-form-item label="备份时间" v-if="config.backup.auto.enabled">
                <el-time-picker
                  v-model="config.backup.auto.time"
                  format="HH:mm"
                  value-format="HH:mm"
                  placeholder="选择时间"
                />
              </el-form-item>

              <el-form-item label="备份保留数量" v-if="config.backup.auto.enabled">
                <el-input-number
                  v-model="config.backup.auto.retainCount"
                  :min="1"
                  :max="30"
                  :step="1"
                />
                <span style="margin-left: 8px;">个</span>
              </el-form-item>

              <el-form-item label="备份内容" v-if="config.backup.auto.enabled">
                <el-checkbox-group v-model="config.backup.auto.content">
                  <el-checkbox value="database">数据库</el-checkbox>
                  <el-checkbox value="files">上传文件</el-checkbox>
                  <el-checkbox value="config">系统配置</el-checkbox>
                  <el-checkbox value="logs">日志文件</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>备份存储</h3>
            <el-form :model="config.backup.storage" label-width="200px">
              <el-form-item label="存储方式">
                <el-radio-group v-model="config.backup.storage.type">
                  <el-radio value="local">本地存储</el-radio>
                  <el-radio value="ftp">FTP存储</el-radio>
                  <el-radio value="oss">对象存储</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="存储路径" v-if="config.backup.storage.type === 'local'">
                <el-input v-model="config.backup.storage.path" style="width: 400px" />
              </el-form-item>

              <template v-if="config.backup.storage.type === 'ftp'">
                <el-form-item label="FTP服务器">
                  <el-input v-model="config.backup.storage.ftp.host" style="width: 200px" />
                </el-form-item>
                <el-form-item label="FTP端口">
                  <el-input-number v-model="config.backup.storage.ftp.port" :min="1" :max="65535" />
                </el-form-item>
                <el-form-item label="用户名">
                  <el-input v-model="config.backup.storage.ftp.username" style="width: 200px" />
                </el-form-item>
                <el-form-item label="密码">
                  <el-input
                    v-model="config.backup.storage.ftp.password"
                    type="password"
                    show-password
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item label="存储路径">
                  <el-input v-model="config.backup.storage.ftp.path" style="width: 300px" />
                </el-form-item>
              </template>
            </el-form>
          </div>

          <div class="config-section">
            <h3>备份操作</h3>
            <el-space>
              <el-button type="primary" @click="handleBackupNow">
                <el-icon><Download /></el-icon>
                立即备份
              </el-button>
              <el-button @click="handleRestoreBackup">
                <el-icon><Upload /></el-icon>
                恢复备份
              </el-button>
              <el-button @click="handleCleanBackups">
                <el-icon><Delete /></el-icon>
                清理备份
              </el-button>
            </el-space>
          </div>
        </el-tab-pane>

        <!-- 性能配置 -->
        <el-tab-pane label="性能配置" name="performance">
          <div class="config-section">
            <h3>缓存设置</h3>
            <el-form :model="config.performance.cache" label-width="200px">
              <el-form-item label="启用缓存">
                <el-switch
                  v-model="config.performance.cache.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="缓存类型" v-if="config.performance.cache.enabled">
                <el-select v-model="config.performance.cache.type" style="width: 200px">
                  <el-option label="内存缓存" value="memory" />
                  <el-option label="Redis缓存" value="redis" />
                </el-select>
              </el-form-item>

              <template v-if="config.performance.cache.type === 'redis'">
                <el-form-item label="Redis主机">
                  <el-input v-model="config.performance.cache.redis.host" style="width: 200px" />
                </el-form-item>
                <el-form-item label="Redis端口">
                  <el-input-number v-model="config.performance.cache.redis.port" :min="1" :max="65535" />
                </el-form-item>
                <el-form-item label="Redis密码">
                  <el-input
                    v-model="config.performance.cache.redis.password"
                    type="password"
                    show-password
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item label="数据库">
                  <el-input v-model="config.performance.cache.redis.db" style="width: 100px" />
                </el-form-item>
              </template>

              <el-form-item label="缓存过期时间" v-if="config.performance.cache.enabled">
                <el-input-number
                  v-model="config.performance.cache.expireTime"
                  :min="60"
                  :max="86400"
                  :step="60"
                />
                <span style="margin-left: 8px;">秒</span>
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>文件上传</h3>
            <el-form :model="config.performance.upload" label-width="200px">
              <el-form-item label="最大文件大小">
                <el-input-number
                  v-model="config.performance.upload.maxSize"
                  :min="1"
                  :max="100"
                  :step="1"
                />
                <span style="margin-left: 8px;">MB</span>
              </el-form-item>

              <el-form-item label="允许的文件类型">
                <el-input
                  v-model="config.performance.upload.allowedTypes"
                  placeholder="jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx"
                  style="width: 400px"
                />
              </el-form-item>

              <el-form-item label="图片压缩">
                <el-switch
                  v-model="config.performance.upload.compressImage"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="压缩质量" v-if="config.performance.upload.compressImage">
                <el-slider
                  v-model="config.performance.upload.compressQuality"
                  :min="10"
                  :max="100"
                  :step="5"
                  style="width: 200px"
                />
              </el-form-item>
            </el-form>
          </div>

          <div class="config-section">
            <h3>系统监控</h3>
            <el-form :model="config.performance.monitor" label-width="200px">
              <el-form-item label="启用性能监控">
                <el-switch
                  v-model="config.performance.monitor.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="监控间隔" v-if="config.performance.monitor.enabled">
                <el-input-number
                  v-model="config.performance.monitor.interval"
                  :min="1"
                  :max="60"
                  :step="1"
                />
                <span style="margin-left: 8px;">分钟</span>
              </el-form-item>

              <el-form-item label="启用报警" v-if="config.performance.monitor.enabled">
                <el-switch
                  v-model="config.performance.monitor.enableAlert"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="CPU使用率阈值" v-if="config.performance.monitor.enableAlert">
                <el-input-number
                  v-model="config.performance.monitor.cpuThreshold"
                  :min="50"
                  :max="95"
                  :step="5"
                />
                <span style="margin-left: 8px;">%</span>
              </el-form-item>

              <el-form-item label="内存使用率阈值" v-if="config.performance.monitor.enableAlert">
                <el-input-number
                  v-model="config.performance.monitor.memoryThreshold"
                  :min="50"
                  :max="95"
                  :step="5"
                />
                <span style="margin-left: 8px;">%</span>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 邮件模板编辑对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="templateDialogTitle"
      width="600px"
    >
      <el-form :model="templateForm" label-width="100px">
        <el-form-item label="模板名称">
          <el-input v-model="templateForm.name" />
        </el-form-item>
        <el-form-item label="邮件主题">
          <el-input v-model="templateForm.subject" />
        </el-form-item>
        <el-form-item label="邮件内容">
          <el-input
            v-model="templateForm.content"
            type="textarea"
            :rows="6"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="templateForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTemplate">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 隐藏的文件输入 -->
    <input
      ref="fileInput"
      type="file"
      accept=".json"
      style="display: none"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Download,
  Upload,
  Plus,
  Delete
} from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('basic')
const templateDialogVisible = ref(false)
const templateDialogTitle = ref('')
const testEmail = ref('')
const fileInput = ref(null)
const templateEditIndex = ref(-1)

// 配置数据
const config = reactive({
  basic: {
    systemName: '社区物业管理系统',
    version: '1.0.0',
    logoUrl: '',
    description: '专业的社区物业管理解决方案',
    companyName: '智慧物业科技有限公司',
    phone: '400-888-8888',
    email: 'support@example.com',
    address: '北京市朝阳区科技园区',
    ui: {
      theme: 'default',
      language: 'zh-CN',
      sidebarDefaultOpen: true,
      enableAnimation: true,
      cachePages: 10
    }
  },
  security: {
    login: {
      passwordRules: ['length', 'number', 'lowercase'],
      passwordExpire: 90,
      enableLock: true,
      maxFailCount: 5,
      lockTime: 30,
      enableCaptcha: true,
      enable2FA: false,
      sessionTimeout: 120
    },
    access: {
      enableIpWhitelist: false,
      ipWhitelist: '',
      enableRateLimit: true,
      maxRequestsPerMinute: 100,
      enableOperationLog: true,
      logRetentionDays: 90
    }
  },
  email: {
    smtp: {
      enabled: false,
      host: '',
      port: 587,
      encryption: 'tls',
      fromEmail: '',
      fromName: '',
      username: '',
      password: ''
    },
    templates: [
      {
        name: '用户注册',
        subject: '欢迎注册{系统名称}',
        content: '尊敬的{用户名}，欢迎您注册{系统名称}！',
        description: '用户注册成功后发送的欢迎邮件'
      },
      {
        name: '密码重置',
        subject: '密码重置通知',
        content: '您的密码已重置，新密码为：{新密码}',
        description: '密码重置后发送的通知邮件'
      }
    ]
  },
  backup: {
    auto: {
      enabled: false,
      frequency: 'daily',
      time: '02:00',
      retainCount: 7,
      content: ['database', 'config']
    },
    storage: {
      type: 'local',
      path: '/backup',
      ftp: {
        host: '',
        port: 21,
        username: '',
        password: '',
        path: '/backup'
      }
    }
  },
  performance: {
    cache: {
      enabled: true,
      type: 'memory',
      redis: {
        host: 'localhost',
        port: 6379,
        password: '',
        db: 0
      },
      expireTime: 3600
    },
    upload: {
      maxSize: 10,
      allowedTypes: 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx',
      compressImage: true,
      compressQuality: 80
    },
    monitor: {
      enabled: true,
      interval: 5,
      enableAlert: true,
      cpuThreshold: 80,
      memoryThreshold: 85
    }
  }
})

// 邮件模板表单
const templateForm = reactive({
  name: '',
  subject: '',
  content: '',
  description: ''
})

// Logo上传相关
const handleLogoSuccess = (response) => {
  config.basic.logoUrl = response.url
  ElMessage.success('Logo上传成功')
}

const beforeLogoUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('Logo只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('Logo大小不能超过 2MB!')
    return false
  }
  return true
}

// 保存配置
const handleSaveConfig = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('配置保存成功')
  } catch {
    ElMessage.error('配置保存失败')
  }
}

// 导出配置
const handleExportConfig = () => {
  const exportData = JSON.stringify(config, null, 2)
  const dataBlob = new Blob([exportData], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = `system-config-${new Date().toISOString().slice(0, 10)}.json`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('配置导出成功')
}

// 导入配置
const handleImportConfig = () => {
  fileInput.value?.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const importedConfig = JSON.parse(e.target.result)
        Object.assign(config, importedConfig)
        ElMessage.success('配置导入成功')
      } catch {
        ElMessage.error('配置文件格式错误')
      }
    }
    reader.readAsText(file)
  }
  // 清空文件输入
  event.target.value = ''
}

// 发送测试邮件
const handleTestEmail = async () => {
  if (!testEmail.value) {
    ElMessage.warning('请输入测试邮箱地址')
    return
  }

  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('测试邮件发送成功')
  } catch {
    ElMessage.error('测试邮件发送失败')
  }
}

// 添加邮件模板
const handleAddTemplate = () => {
  templateDialogTitle.value = '添加邮件模板'
  Object.assign(templateForm, {
    name: '',
    subject: '',
    content: '',
    description: ''
  })
  templateEditIndex.value = -1
  templateDialogVisible.value = true
}

// 编辑邮件模板
const handleEditTemplate = (template, index) => {
  templateDialogTitle.value = '编辑邮件模板'
  Object.assign(templateForm, { ...template })
  templateEditIndex.value = index
  templateDialogVisible.value = true
}

// 保存邮件模板
const handleSaveTemplate = () => {
  if (templateEditIndex.value === -1) {
    // 添加新模板
    config.email.templates.push({ ...templateForm })
    ElMessage.success('模板添加成功')
  } else {
    // 编辑现有模板
    Object.assign(config.email.templates[templateEditIndex.value], templateForm)
    ElMessage.success('模板修改成功')
  }
  templateDialogVisible.value = false
}

// 删除邮件模板
const handleDeleteTemplate = async (index) => {
  try {
    await ElMessageBox.confirm('确定要删除这个邮件模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    config.email.templates.splice(index, 1)
    ElMessage.success('模板删除成功')
  } catch {
    // 用户取消
  }
}

// 立即备份
const handleBackupNow = async () => {
  try {
    await ElMessageBox.confirm('确定要立即执行系统备份吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('系统备份成功')
  } catch {
    // 用户取消
  }
}

// 恢复备份
const handleRestoreBackup = () => {
  ElMessage.info('请选择备份文件进行恢复')
}

// 清理备份
const handleCleanBackups = async () => {
  try {
    await ElMessageBox.confirm('确定要清理过期的备份文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('备份清理完成')
  } catch {
    // 用户取消
  }
}

// 组件挂载
onMounted(() => {
  // 模拟加载配置数据
})
</script>

<style lang="scss" scoped>
.system-config {
  .config-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }

  .config-section {
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
  }

  :deep(.el-tabs__content) {
    padding: 20px;
  }

  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .logo {
      width: 120px;
      height: 40px;
      display: block;
      object-fit: contain;
    }

    .logo-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 40px;
      line-height: 40px;
      text-align: center;
    }
  }
}

@media (max-width: 768px) {
  .system-config {
    .config-card .card-header {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;

      .header-actions {
        width: 100%;
        justify-content: flex-start;
      }
    }

    .config-section {
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