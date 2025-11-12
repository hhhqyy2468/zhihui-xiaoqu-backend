<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <h1>欢迎使用物业管理系统</h1>
          <p>您好，{{ userStore.realName }}！</p>
        </div>
      </el-card>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-section">
      <el-col :span="6">
        <el-card class="stat-card building">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.buildingCount }}</div>
              <div class="stat-label">楼栋数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card house">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.houseCount }}</div>
              <div class="stat-label">房产数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card user">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card task">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.taskCount }}</div>
              <div class="stat-label">待处理任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>快捷操作</h3>
          </template>
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="$router.push('/property/building')">
              <el-icon><OfficeBuilding /></el-icon>
              楼栋管理
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/property/owner')">
              <el-icon><User /></el-icon>
              业主管理
            </el-button>
            <el-button type="warning" size="large" @click="$router.push('/service/repair')">
              <el-icon><Tools /></el-icon>
              维修管理
            </el-button>
            <el-button type="info" size="large" @click="$router.push('/finance/bill')">
              <el-icon><Money /></el-icon>
              账单管理
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>最新通知</h3>
          </template>
          <div class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <el-tag :type="getNoticeTag(notice.type)" size="small">{{ notice.type }}</el-tag>
              <span class="notice-title">{{ notice.title }}</span>
              <span class="notice-time">{{ notice.time }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { OfficeBuilding, House, User, List, Tools, Money } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 响应式数据
const stats = ref({
  buildingCount: 12,
  houseCount: 156,
  userCount: 89,
  taskCount: 8
})

const notices = ref([
  {
    id: 1,
    type: '公告',
    title: '小区停水通知',
    time: '2025-11-11 09:00'
  },
  {
    id: 2,
    type: '维修',
    title: '3号楼电梯维护完成',
    time: '2025-11-11 08:30'
  },
  {
    id: 3,
    type: '费用',
    title: '本月物业费催缴提醒',
    time: '2025-11-10 16:00'
  }
])

// 获取通知标签样式
const getNoticeTag = (type) => {
  const map = {
    '公告': 'primary',
    '维修': 'success',
    '费用': 'warning',
    '安全': 'danger'
  }
  return map[type] || 'info'
}

// 组件挂载
onMounted(() => {
  userStore.initUserState()
  console.log('Dashboard mounted')
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;

  .welcome-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;

    .welcome-content {
      text-align: center;

      h1 {
        margin: 0 0 10px 0;
        font-size: 32px;
        font-weight: 300;
      }

      p {
        margin: 0;
        font-size: 16px;
        opacity: 0.9;
      }
    }
  }
}

.statistics-section {
  margin-bottom: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px;
    }

    .stat-icon {
      margin-right: 16px;
      color: white;
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        opacity: 0.8;
      }
    }

    &.building {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.house {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
      color: white;
      border: none;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.user {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      color: white;
      border: none;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.task {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: white;
      border: none;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

.quick-actions {
  .action-buttons {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .el-button {
      height: 60px;
      font-size: 16px;
    }
  }

  .notice-list {
    .notice-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .notice-title {
        flex: 1;
        margin: 0 12px;
        font-size: 14px;
        color: #333;
      }

      .notice-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>