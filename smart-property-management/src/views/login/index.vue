<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
        <h2>社区物业管理系统</h2>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form-content"
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

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            size="large"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="demo-accounts">
            <el-divider>
              <span class="demo-text">演示账号</span>
            </el-divider>
            <div class="account-list">
              <el-button
                size="small"
                type="info"
                plain
                @click="fillAccount('admin')"
              >
                系统管理员
              </el-button>
              <el-button
                size="small"
                type="info"
                plain
                @click="fillAccount('manager')"
              >
                物业管理员
              </el-button>
              <el-button
                size="small"
                type="info"
                plain
                @click="fillAccount('owner001')"
              >
                业主(张三)
              </el-button>
              <el-button
                size="small"
                type="info"
                plain
                @click="fillAccount('owner002')"
              >
                业主(李四)
              </el-button>
              <el-button
                size="small"
                type="warning"
                plain
                @click="fillAccount('worker_a')"
              >
                维修A
              </el-button>
              <el-button
                size="small"
                type="warning"
                plain
                @click="fillAccount('worker_b')"
              >
                维修B
              </el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
})

// 演示账号配置（与数据库真实账号一致）
const demoAccounts = {
  admin: { username: 'admin', password: '123456', name: '系统管理员' },
  manager: { username: 'manager', password: '123456', name: '物业管理员' },
  owner001: { username: 'owner001', password: '123456', name: '张三(业主)' },
  owner002: { username: 'owner002', password: '123456', name: '李四(业主)' },
  worker_a: { username: 'worker_a', password: '123456', name: '维修人员A' },
  worker_b: { username: 'worker_b', password: '123456', name: '维修人员B' }
}

// 填充演示账号
const fillAccount = (type) => {
  const account = demoAccounts[type]
  if (account) {
    loginForm.username = account.username
    loginForm.password = account.password
    ElMessage.success(`已填充${account.name}账号信息`)
  }
}

// 保存登录信息到本地存储
const saveLoginInfo = () => {
  if (loginForm.rememberMe) {
    localStorage.setItem('rememberedUsername', loginForm.username)
    localStorage.setItem('rememberedPassword', loginForm.password)
  } else {
    localStorage.removeItem('rememberedUsername')
    localStorage.removeItem('rememberedPassword')
  }
}

// 从本地存储加载登录信息
const loadLoginInfo = () => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  const rememberedPassword = localStorage.getItem('rememberedPassword')

  if (rememberedUsername && rememberedPassword) {
    loginForm.username = rememberedUsername
    loginForm.password = rememberedPassword
    loginForm.rememberMe = true
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)

        // 保存登录信息
        saveLoginInfo()

        ElMessage.success('登录成功')

        // 根据用户类型重定向到不同的首页
        if (userStore.userType === 3) {
          // 业主重定向到业主首页
          router.push('/portal/dashboard')
        } else if (userStore.userType === 4) {
          // 维修人员重定向到工作台
          router.push('/work/pending')
        } else {
          // 系统管理员和物业管理员重定向到工作台
          router.push('/dashboard')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}

// 组件挂载时加载保存的登录信息
onMounted(() => {
  loadLoginInfo()
})
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;

  .login-form {
    width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);

    .login-header {
      text-align: center;
      margin-bottom: 40px;

      .logo {
        width: 80px;
        height: 80px;
        margin-bottom: 20px;
      }

      h2 {
        color: #333;
        font-weight: 600;
        margin: 0;
      }
    }

    .login-form-content {
      .login-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
      }

      .login-button {
        width: 100%;
        height: 50px;
        font-size: 16px;
        font-weight: 500;
      }

      .demo-accounts {
        width: 100%;

        .demo-text {
          color: #999;
          font-size: 12px;
        }

        .account-list {
          display: flex;
          gap: 8px;
          justify-content: center;
          flex-wrap: wrap;

          .el-button {
            flex: 1;
            min-width: 80px;
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style>