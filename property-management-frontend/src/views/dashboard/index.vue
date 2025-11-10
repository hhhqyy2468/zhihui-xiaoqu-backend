<template>
  <div class="dashboard">
    <!-- 系统管理员工作台 -->
    <div v-if="userStore.userType === 1" class="admin-dashboard">
      <div class="welcome-section">
        <h1>欢迎回来，{{ userStore.realName }}！</h1>
        <p>系统管理员工作台 - 全局管理视角</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#409eff"><OfficeBuilding /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">楼栋总数</div>
                <div class="card-value">{{ adminStats.buildingCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#67c23a"><House /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">房产总数</div>
                <div class="card-value">{{ adminStats.houseCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#e6a23c"><User /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">用户总数</div>
                <div class="card-value">{{ adminStats.userCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#f56c6c"><Document /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">系统日志</div>
                <div class="card-value">{{ adminStats.logCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最新系统日志</span>
                <el-button type="text" @click="$router.push('/log/operation')">查看更多</el-button>
              </div>
            </template>
            <div class="log-list">
              <div v-for="log in recentLogs" :key="log.id" class="log-item">
                <span class="log-time">{{ log.time }}</span>
                <span class="log-user">{{ log.user }}</span>
                <span class="log-action">{{ log.action }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>系统状态</span>
              </div>
            </template>
            <div class="system-status">
              <div class="status-item">
                <el-tag type="success">系统正常</el-tag>
                <span class="status-desc">所有服务运行正常</span>
              </div>
              <div class="status-item">
                <el-tag type="warning">待处理</el-tag>
                <span class="status-desc">{{ adminStats.pendingTasks }} 个待处理任务</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 物业管理员工作台 -->
    <div v-else-if="userStore.userType === 2" class="manager-dashboard">
      <div class="welcome-section">
        <h1>欢迎回来，{{ userStore.realName }}！</h1>
        <p>物业管理员工作台 - 业务管理视角</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#409eff"><House /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">房产总数</div>
                <div class="card-value">{{ managerStats.houseCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#67c23a"><User /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">业主总数</div>
                <div class="card-value">{{ managerStats.ownerCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#e6a23c"><Money /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待缴费账单</div>
                <div class="card-value">{{ managerStats.unpaidBills }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#f56c6c"><Tools /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待处理工单</div>
                <div class="card-value">{{ managerStats.pendingTasks }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>待处理投诉</span>
                <el-button type="text" @click="$router.push('/service/complaint')">查看更多</el-button>
              </div>
            </template>
            <div class="task-list">
              <div v-for="task in pendingComplaints" :key="task.id" class="task-item">
                <el-tag :type="task.priority === '紧急' ? 'danger' : 'warning'" size="small">{{ task.priority }}</el-tag>
                <span class="task-title">{{ task.title }}</span>
                <span class="task-time">{{ task.time }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>待派工维修</span>
                <el-button type="text" @click="$router.push('/service/repair')">查看更多</el-button>
              </div>
            </template>
            <div class="task-list">
              <div v-for="task in pendingRepairs" :key="task.id" class="task-item">
                <el-tag type="warning" size="small">{{ task.type }}</el-tag>
                <span class="task-title">{{ task.title }}</span>
                <span class="task-time">{{ task.time }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 维修人员工作台 -->
    <div v-else-if="userStore.userType === 4" class="worker-dashboard">
      <div class="welcome-section">
        <h1>欢迎回来，{{ userStore.realName }}！</h1>
        <p>维修人员工作台 - 工单管理视角</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#409eff"><Tools /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待接单</div>
                <div class="card-value">{{ workerStats.pendingOrders }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#67c23a"><Setting /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">进行中</div>
                <div class="card-value">{{ workerStats.processingOrders }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon">
                <el-icon size="40" color="#e6a23c"><View /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-title">待验收</div>
                <div class="card-value">{{ workerStats.pendingAcceptOrders }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最新工单</span>
                <el-button type="text" @click="$router.push('/work/pending')">查看更多</el-button>
              </div>
            </template>
            <div class="order-list">
              <div v-for="order in recentOrders" :key="order.id" class="order-item">
                <el-tag :type="getOrderStatusType(order.status)" size="small">{{ order.status }}</el-tag>
                <span class="order-title">{{ order.title }}</span>
                <span class="order-location">{{ order.location }}</span>
                <span class="order-time">{{ order.time }}</span>
                <el-button v-if="order.status === '待接单'" type="primary" size="small" @click="acceptOrder(order.id)">接单</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

// 系统管理员统计数据
const adminStats = reactive({
  buildingCount: 0,
  houseCount: 0,
  userCount: 0,
  logCount: 0,
  pendingTasks: 0
})

// 物业管理员统计数据
const managerStats = reactive({
  houseCount: 0,
  ownerCount: 0,
  unpaidBills: 0,
  pendingTasks: 0
})

// 维修人员统计数据
const workerStats = reactive({
  pendingOrders: 0,
  processingOrders: 0,
  pendingAcceptOrders: 0
})

// 模拟数据
const recentLogs = ref([
  { id: 1, time: '2025-01-09 14:30', user: '张三', action: '新增用户' },
  { id: 2, time: '2025-01-09 14:15', user: '李四', action: '修改楼栋信息' },
  { id: 3, time: '2025-01-09 14:00', user: '王五', action: '删除公告' },
  { id: 4, time: '2025-01-09 13:45', user: '赵六', action: '生成账单' }
])

const pendingComplaints = ref([
  { id: 1, priority: '紧急', title: '电梯故障', time: '2小时前' },
  { id: 2, priority: '普通', title: '水管漏水', time: '4小时前' },
  { id: 3, priority: '紧急', title: '门禁系统故障', time: '6小时前' }
])

const pendingRepairs = ref([
  { id: 1, type: '水电', title: '业主家水龙头漏水', time: '1小时前' },
  { id: 2, type: '门窗', title: '单元门无法关闭', time: '3小时前' },
  { id: 3, type: '电梯', title: '电梯按钮失灵', time: '5小时前' }
])

const recentOrders = ref([
  { id: 1, status: '待接单', title: '业主家水龙头漏水', location: '1号楼1单元101', time: '30分钟前' },
  { id: 2, status: '进行中', title: '楼道灯泡更换', location: '2号楼3单元', time: '2小时前' },
  { id: 3, status: '待验收', title: '门锁维修', location: '3号楼2单元201', time: '1天前' },
  { id: 4, status: '待接单', title: '窗户玻璃更换', location: '1号楼2单元302', time: '2天前' }
])

const getOrderStatusType = (status) => {
  const statusMap = {
    '待接单': 'warning',
    '进行中': 'primary',
    '待验收': 'info',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

const acceptOrder = (orderId) => {
  ElMessage.success(`工单 ${orderId} 接单成功`)
  // 这里应该调用接单API
  recentOrders.value = recentOrders.value.map(order => {
    if (order.id === orderId) {
      order.status = '进行中'
    }
    return order
  })
  updateWorkerStats()
}

const updateWorkerStats = () => {
  workerStats.pendingOrders = recentOrders.value.filter(order => order.status === '待接单').length
  workerStats.processingOrders = recentOrders.value.filter(order => order.status === '进行中').length
  workerStats.pendingAcceptOrders = recentOrders.value.filter(order => order.status === '待验收').length
}

onMounted(() => {
  // 根据不同角色加载不同的统计数据
  if (userStore.userType === 1) {
    // 系统管理员数据
    adminStats.buildingCount = 12
    adminStats.houseCount = 486
    adminStats.userCount = 520
    adminStats.logCount = 1250
    adminStats.pendingTasks = 8
  } else if (userStore.userType === 2) {
    // 物业管理员数据
    managerStats.houseCount = 486
    managerStats.ownerCount = 352
    managerStats.unpaidBills = 28
    managerStats.pendingTasks = 15
  } else if (userStore.userType === 4) {
    // 维修人员数据
    updateWorkerStats()
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;

  .welcome-section {
    margin-bottom: 30px;

    h1 {
      color: #303133;
      font-size: 28px;
      margin-bottom: 8px;
      font-weight: 600;
    }

    p {
      color: #909399;
      font-size: 16px;
      margin: 0;
    }
  }

  .dashboard-card {
    .card-content {
      display: flex;
      align-items: center;
      padding: 10px 0;

      .card-icon {
        margin-right: 20px;
      }

      .card-info {
        .card-title {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
        }

        .card-value {
          font-size: 24px;
          font-weight: bold;
          color: #333;
        }
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  // 系统日志列表
  .log-list {
    .log-item {
      display: flex;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .log-time {
        color: #909399;
        font-size: 12px;
        margin-right: 12px;
        min-width: 100px;
      }

      .log-user {
        color: #606266;
        font-size: 14px;
        margin-right: 12px;
        min-width: 60px;
      }

      .log-action {
        color: #303133;
        font-size: 14px;
        flex: 1;
      }
    }
  }

  // 系统状态
  .system-status {
    .status-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .el-tag {
        margin-right: 12px;
        min-width: 80px;
      }

      .status-desc {
        color: #606266;
        font-size: 14px;
        flex: 1;
      }
    }
  }

  // 任务列表
  .task-list {
    .task-item {
      display: flex;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .el-tag {
        margin-right: 12px;
        min-width: 60px;
      }

      .task-title {
        color: #303133;
        font-size: 14px;
        flex: 1;
        margin-right: 12px;
      }

      .task-time {
        color: #909399;
        font-size: 12px;
        min-width: 60px;
      }
    }
  }

  // 工单列表
  .order-list {
    .order-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .el-tag {
        margin-right: 12px;
        min-width: 70px;
      }

      .order-title {
        color: #303133;
        font-size: 14px;
        flex: 1;
        margin-right: 12px;
      }

      .order-location {
        color: #606266;
        font-size: 13px;
        margin-right: 12px;
        min-width: 120px;
      }

      .order-time {
        color: #909399;
        font-size: 12px;
        margin-right: 12px;
        min-width: 70px;
      }

      .el-button {
        min-width: 60px;
      }
    }
  }
}
</style>