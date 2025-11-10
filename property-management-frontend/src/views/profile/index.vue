<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">个人中心</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
            <el-button
              type="primary"
              size="small"
              @click="editMode = !editMode"
            >
              {{ editMode ? '取消编辑' : '编辑信息' }}
            </el-button>
          </div>
        </template>

        <div class="profile-info">
          <div class="avatar-section">
            <el-avatar :size="120" :src="profileForm.avatar">
              {{ profileForm.realName.charAt(0) }}
            </el-avatar>
            <div class="avatar-actions" v-if="editMode">
              <el-button size="small" type="primary">更换头像</el-button>
            </div>
          </div>

          <div class="info-section">
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              :disabled="!editMode"
            >
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="用户名" prop="username">
                    <el-input v-model="profileForm.username" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="profileForm.realName" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="手机号码" prop="phone">
                    <el-input v-model="profileForm.phone" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="邮箱地址" prop="email">
                    <el-input v-model="profileForm.email" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="用户类型">
                    <el-tag :type="getUserTypeColor(profileForm.userType)">
                      {{ getUserTypeName(profileForm.userType) }}
                    </el-tag>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="所属角色">
                    <el-tag
                      v-for="role in userStore.roles"
                      :key="role"
                      type="success"
                      class="mr-1"
                    >
                      {{ getRoleName(role) }}
                    </el-tag>
                  </el-form-item>
                </el-col>
              </el-row>

              <!-- 业主特有信息 -->
              <template v-if="profileForm.userType === 3">
                <el-divider content-position="left">房产信息</el-divider>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="房产地址">
                      <el-input v-model="profileForm.houseInfo" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="入住天数">
                      <el-input v-model="profileForm.checkInDays" disabled>
                        <template #append>天</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
            </el-form>
          </div>
        </div>

        <div class="form-actions" v-if="editMode">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存
          </el-button>
        </div>
      </el-card>

      <!-- 账户安全卡片 -->
      <el-card class="security-card">
        <template #header>
          <span>账户安全</span>
        </template>

        <div class="security-items">
          <div class="security-item">
            <div class="item-info">
              <el-icon class="item-icon"><Lock /></el-icon>
              <div class="item-content">
                <h4>登录密码</h4>
                <p>保护账户安全，定期更换密码</p>
              </div>
            </div>
            <el-button size="small" @click="showPasswordDialog = true">
              修改密码
            </el-button>
          </div>

          <div class="security-item">
            <div class="item-info">
              <el-icon class="item-icon"><Iphone /></el-icon>
              <div class="item-content">
                <h4>手机绑定</h4>
                <p>已绑定 {{ maskPhone(profileForm.phone) }}</p>
              </div>
            </div>
            <el-button size="small" type="primary" disabled>
              已绑定
            </el-button>
          </div>

          <div class="security-item">
            <div class="item-info">
              <el-icon class="item-icon"><Message /></el-icon>
              <div class="item-content">
                <h4>邮箱绑定</h4>
                <p>已绑定 {{ maskEmail(profileForm.email) }}</p>
              </div>
            </div>
            <el-button size="small" type="primary" disabled>
              已绑定
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 登录记录卡片 -->
      <el-card class="login-record-card">
        <template #header>
          <span>登录记录</span>
        </template>

        <el-table :data="loginRecords" style="width: 100%">
          <el-table-column prop="loginTime" label="登录时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.loginTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="IP地址" width="140" />
          <el-table-column prop="location" label="登录地点" />
          <el-table-column prop="device" label="设备信息" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
                {{ row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
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
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="500px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="handlePasswordChange" :loading="changingPassword">
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lock, Iphone, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { USER_TYPES, ROLES } from '@/utils/permission'

const userStore = useUserStore()

// 编辑模式
const editMode = ref(false)
const saving = ref(false)

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 个人信息表单
const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  userType: 1,
  avatar: '',
  houseInfo: '',
  checkInDays: 0
})

// 表单验证规则
const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 修改密码相关
const showPasswordDialog = ref(false)
const changingPassword = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 登录记录
const loginRecords = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取用户类型名称
const getUserTypeName = (userType) => {
  const typeMap = {
    [USER_TYPES.ADMIN]: '系统管理员',
    [USER_TYPES.MANAGER]: '物业管理员',
    [USER_TYPES.OWNER]: '业主',
    [USER_TYPES.WORKER]: '维修人员'
  }
  return typeMap[userType] || '未知'
}

// 获取用户类型颜色
const getUserTypeColor = (userType) => {
  const colorMap = {
    [USER_TYPES.ADMIN]: 'danger',
    [USER_TYPES.MANAGER]: 'warning',
    [USER_TYPES.OWNER]: 'primary',
    [USER_TYPES.WORKER]: 'info'
  }
  return colorMap[userType] || 'info'
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    [ROLES.ADMIN]: '管理员',
    [ROLES.PROPERTY_MANAGER]: '物业经理',
    [ROLES.OWNER]: '业主',
    [ROLES.WORKER]: '维修工'
  }
  return roleMap[role] || role
}

// 手机号脱敏
const maskPhone = (phone) => {
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 邮箱脱敏
const maskEmail = (email) => {
  const [username, domain] = email.split('@')
  const maskedUsername = username.length > 2
    ? username.substring(0, 2) + '***' + username.substring(username.length - 1)
    : username
  return `${maskedUsername}@${domain}`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 初始化个人信息
const initProfileInfo = () => {
  Object.assign(profileForm, {
    ...userStore.userInfo,
    houseInfo: '3号楼2单元501',
    checkInDays: Math.floor(Math.random() * 1000) + 100
  })
}

// 加载登录记录
const loadLoginRecords = () => {
  // 模拟登录记录数据
  const mockRecords = [
    {
      id: 1,
      loginTime: new Date(),
      ip: '192.168.1.100',
      location: '北京市朝阳区',
      device: 'Chrome 118.0 / Windows 10',
      status: 'success'
    },
    {
      id: 2,
      loginTime: new Date(Date.now() - 86400000),
      ip: '192.168.1.101',
      location: '北京市朝阳区',
      device: 'Chrome 118.0 / Windows 10',
      status: 'success'
    },
    {
      id: 3,
      loginTime: new Date(Date.now() - 172800000),
      ip: '192.168.1.102',
      location: '北京市朝阳区',
      device: 'Chrome 117.0 / Windows 10',
      status: 'success'
    },
    {
      id: 4,
      loginTime: new Date(Date.now() - 259200000),
      ip: '192.168.1.103',
      location: '北京市朝阳区',
      device: 'Chrome 117.0 / Windows 10',
      status: 'failed'
    }
  ]

  loginRecords.value = mockRecords
  total.value = mockRecords.length
}

// 保存个人信息
const handleSave = async () => {
  try {
    await profileFormRef.value.validate()
    saving.value = true

    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 更新用户信息
    userStore.userInfo = { ...profileForm }
    localStorage.setItem('userInfo', JSON.stringify(profileForm))

    ElMessage.success('个人信息保存成功')
    editMode.value = false
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  initProfileInfo()
  editMode.value = false
}

// 修改密码
const handlePasswordChange = async () => {
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true

    // 模拟密码修改
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('密码修改成功，请重新登录')
    showPasswordDialog.value = false

    // 重置表单
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 2秒后自动退出登录
    setTimeout(async () => {
      await userStore.logout()
      window.location.href = '/login'
    }, 2000)
  } catch (error) {
    console.error('密码修改失败:', error)
  } finally {
    changingPassword.value = false
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadLoginRecords()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadLoginRecords()
}

onMounted(() => {
  initProfileInfo()
  loadLoginRecords()
})
</script>

<style lang="scss" scoped>
.profile-container {
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

.profile-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.profile-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .profile-info {
    display: flex;
    gap: 40px;

    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;

      .avatar-actions {
        display: flex;
        gap: 8px;
      }
    }

    .info-section {
      flex: 1;
    }
  }

  .form-actions {
    margin-top: 20px;
    text-align: right;
  }
}

.security-card {
  .security-items {
    .security-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .item-info {
        display: flex;
        align-items: center;
        gap: 16px;

        .item-icon {
          font-size: 24px;
          color: #409eff;
        }

        .item-content {
          h4 {
            margin: 0 0 4px 0;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
          }

          p {
            margin: 0;
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }
}

.login-record-card {
  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
}

.mr-1 {
  margin-right: 4px;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    gap: 20px;
  }

  .security-item {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 12px;
  }
}
</style>