<template>
  <div class="dashboard">
    <!-- 物业管理员工作台 -->
    <div v-if="userStore.userType === 2" class="manager-dashboard">
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
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>待处理投诉</span>
                <el-button type="text" @click="$router.push('/service/complaint')">查看更多</el-button>
              </div>
            </template>
            <div class="task-list">
              <el-empty v-if="pendingComplaints.length === 0" description="暂无待处理投诉" :image-size="60" />
              <div v-for="task in pendingComplaints" :key="task.id" class="task-item">
                <el-tag :type="task.priority === '紧急' ? 'danger' : 'warning'" size="small">{{ task.priority }}</el-tag>
                <span class="task-title">{{ task.title }}</span>
                <span class="task-time">{{ task.time }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>待派工维修</span>
                <el-button type="text" @click="$router.push('/service/repair')">查看更多</el-button>
              </div>
            </template>
            <div class="task-list">
              <el-empty v-if="pendingRepairs.length === 0" description="暂无待派工维修" :image-size="60" />
              <div v-for="task in pendingRepairs" :key="task.id" class="task-item">
                <el-tag type="warning" size="small">{{ task.type }}</el-tag>
                <span class="task-title">{{ task.title }}</span>
                <span class="task-time">{{ task.time }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>待审核租赁申请</span>
                <el-button type="text" @click="$router.push('/parking/rental')">查看更多</el-button>
              </div>
            </template>
            <div class="task-list">
              <el-empty v-if="pendingRentals.length === 0" description="暂无待审核申请" :image-size="60" />
              <div v-for="task in pendingRentals" :key="task.id" class="task-item">
                <el-tag type="primary" size="small">租赁</el-tag>
                <span class="task-title">{{ task.title }}</span>
                <span class="task-time">{{ task.time }}</span>
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

import { getManagerStats } from '@/api/dashboard'
import { getRepairOrderPage } from '@/api/repair'
import { getComplaintPage } from '@/api/complaint'
import { listApplication } from '@/api/parking/rentalApplication'

const userStore = useUserStore()

// 物业管理员统计数据
const managerStats = reactive({
  houseCount: 0,
  ownerCount: 0,
  unpaidBills: 0,
  pendingTasks: 0
})

const pendingComplaints = ref([])
const pendingRepairs = ref([])
const pendingRentals = ref([])

// 加载真实的维修员统计数据和最新工单
// 格式化相对时间
const formatRelativeTime = (timeStr) => {
  const now = new Date()
  const time = new Date(timeStr)
  const diff = now - time

  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    return `${days}天前`
  }
}

const loadPendingComplaints = async () => {
  try {
    const res = await getComplaintPage({ pageNum: 1, pageSize: 5, complaintStatus: 1 })
    if (res && res.data) {
      const records = res.data.records || res.data.rows || []
      pendingComplaints.value = records.map(c => ({
        id: c.id,
        priority: c.urgencyLevel === 2 ? '紧急' : '普通',
        title: c.complaintContent ? c.complaintContent.substring(0, 20) : '-',
        time: formatRelativeTime(c.createTime)
      }))
    }
  } catch (e) {
    console.error('获取投诉数据失败', e)
  }
}

const loadPendingRepairs = async () => {
  try {
    const res = await getRepairOrderPage({ pageNum: 1, pageSize: 5, orderStatus: 1 })
    if (res && res.data) {
      const records = res.data.records || res.data.rows || []
      pendingRepairs.value = records.map(r => ({
        id: r.id,
        type: ({ water_electric:'水电维修', door_window:'门窗维修', elevator:'电梯维修', public_facility:'公共设施', appliance:'家电维修', plumbing:'管道维修', painting:'油漆粉刷', other:'其他维修' })[r.repairType] || r.repairType || '其他维修',
        title: r.faultDescription ? r.faultDescription.substring(0, 20) : '-',
        time: formatRelativeTime(r.createTime)
      }))
    }
  } catch (e) {
    console.error('获取维修工单失败', e)
  }
}

const loadPendingRentals = async () => {
  try {
    const res = await listApplication({ pageNum: 1, pageSize: 5, applicationStatus: 1 })
    if (res && res.data) {
      const records = res.data.records || res.data.rows || []
      pendingRentals.value = records.map(r => ({
        id: r.id,
        title: (r.applicantName || '-') + ' 申请 ' + (r.spaceNo || '车位'),
        time: formatRelativeTime(r.createTime)
      }))
    }
  } catch (e) {
    console.error('获取租赁申请失败', e)
  }
}

const loadManagerStats = async () => {
  try {
    const res = await getManagerStats()
    if (res.code === 200 && res.data) {
      Object.assign(managerStats, {
        houseCount: res.data.houseCount || 0,
        ownerCount: res.data.ownerCount || 0,
        unpaidBills: res.data.unpaidBills || 0,
        pendingTasks: res.data.pendingRepairs || 0
      })
    }
  } catch (e) {
    console.error('获取物业管理员统计数据失败', e)
  }
}

onMounted(() => {
  if (userStore.userType === 2) {
    loadManagerStats()
    loadPendingComplaints()
    loadPendingRepairs()
    loadPendingRentals()
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