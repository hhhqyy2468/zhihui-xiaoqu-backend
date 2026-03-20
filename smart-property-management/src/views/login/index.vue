<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 登录/注册 Tab -->
      <el-tabs v-model="activeTab" class="login-tabs" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="form-content"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                size="large"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                size="large"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
            <div class="forgot-pwd">
              <el-link type="primary" :underline="false" @click="handleForgotPwd">忘记密码？</el-link>
            </div>
            <el-form-item>
              <el-button
                :loading="loginLoading"
                type="primary"
                size="large"
                class="submit-button"
                @click="handleLogin"
              >
                {{ loginLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
            <el-form-item>
              <div class="demo-accounts">
                <el-divider><span class="demo-text">演示账号</span></el-divider>
                <div class="account-list">
                  <el-button size="small" type="info" plain @click="fillAccount('admin')">系统管理员</el-button>
                  <el-button size="small" type="info" plain @click="fillAccount('manager')">物业管理员</el-button>
                  <el-button size="small" type="info" plain @click="fillAccount('owner002')">业主(李四)</el-button>
                  <el-button size="small" type="warning" plain @click="fillAccount('worker_a')">维修员A</el-button>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="form-content"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="用户名（6-20位字母数字）"
                size="large"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input
                v-model="registerForm.realName"
                placeholder="真实姓名"
                size="large"
                prefix-icon="UserFilled"
                clearable
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="手机号"
                size="large"
                prefix-icon="Phone"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码（至少6位）"
                size="large"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                size="large"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                :loading="registerLoading"
                type="primary"
                size="large"
                class="submit-button"
                @click="handleRegister"
              >
                {{ registerLoading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-header">
        <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
        <h2>社区物业管理系统</h2>
        <p class="subtitle">河南科技职业大学·信息工程学院</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { register } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loginFormRef = ref()
const registerFormRef = ref()
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', realName: '', phone: '', password: '', confirmPassword: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }]
}

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字、下划线', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const demoAccounts = {
  admin:    { username: 'admin',    password: '123456', name: '系统管理员' },
  manager:  { username: 'manager',  password: '123456', name: '物业管理员' },
  owner002: { username: 'owner002', password: '123456', name: '李四(业主)' },
  worker_a: { username: 'worker_a', password: '123456', name: '维修人员A' }
}

const fillAccount = (type) => {
  const account = demoAccounts[type]
  if (account) {
    loginForm.username = account.username
    loginForm.password = account.password
    ElMessage.success(`已填充${account.name}账号`)
  }
}

const handleForgotPwd = () => {
  ElMessage.info('请联系管理员重置密码')
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loginLoading.value = true
    try {
      await userStore.login(loginForm)
      ElMessage.success('登录成功')
      if (userStore.userType === 3) {
        router.push('/portal/dashboard')
      } else if (userStore.userType === 4) {
        router.push('/work/pending')
      } else {
        router.push('/dashboard')
      }
    } catch (error) {
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
    } finally {
      loginLoading.value = false
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    registerLoading.value = true
    try {
      await register({
        username: registerForm.username,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword,
        realName: registerForm.realName,
        phone: registerForm.phone
      })
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      loginForm.username = registerForm.username
      loginForm.password = registerForm.password
      Object.assign(registerForm, { username: '', realName: '', phone: '', password: '', confirmPassword: '' })
    } catch (error) {
      ElMessage.error(error.message || '注册失败，请稍后重试')
    } finally {
      registerLoading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  background-image: url('@/assets/images/beijing.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.35);
  }
}

.login-box {
  position: relative;
  z-index: 1;
  width: 420px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 32px 40px 24px;
  display: flex;
  flex-direction: column-reverse;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
  .logo {
    width: 72px;
    height: 72px;
    margin-bottom: 12px;
  }
  h2 {
    margin: 0 0 4px;
    font-size: 22px;
    font-weight: 700;
    color: #303133;
  }
  .subtitle {
    margin: 0;
    font-size: 13px;
    color: #909399;
  }
}

.login-tabs {
  :deep(.el-tabs__header) { margin-bottom: 20px; }
}

.form-content {
  .forgot-pwd {
    text-align: right;
    margin: -8px 0 8px;
  }
  .submit-button {
    width: 100%;
    height: 44px;
    font-size: 16px;
  }
  .demo-accounts {
    width: 100%;
    .demo-text { color: #999; font-size: 12px; }
    .account-list {
      display: flex;
      gap: 8px;
      justify-content: center;
      flex-wrap: wrap;
      .el-button { flex: 1; min-width: 80px; font-size: 12px; }
    }
  }
}
</style>
