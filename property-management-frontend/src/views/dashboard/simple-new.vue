<template>
  <div class="dashboard" style="padding: 20px;">
    <!-- 简化的欢迎区域 -->
    <el-card class="welcome-card" style="margin-bottom: 20px;">
      <h1 style="color: #409eff; margin-bottom: 10px;">欢迎使用物业管理系统</h1>
      <p style="font-size: 16px; color: #666;">您好，{{ userStore.realName || '用户' }}！</p>
      <p style="font-size: 14px; color: #999;">您的角色：{{ getUserTypeText(userStore.userType) }}</p>
    </el-card>

    <!-- 简化的统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card>
          <div style="text-align: center;">
            <div style="font-size: 32px; color: #409eff;">{{ stats.buildingCount }}</div>
            <div style="margin-top: 5px; color: #666;">楼栋数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center;">
            <div style="font-size: 32px; color: #67c23a;">{{ stats.houseCount }}</div>
            <div style="margin-top: 5px; color: #666;">房产数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center;">
            <div style="font-size: 32px; color: #e6a23c;">{{ stats.userCount }}</div>
            <div style="margin-top: 5px; color: #666;">用户数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center;">
            <div style="font-size: 32px; color: #f56c6c;">{{ stats.taskCount }}</div>
            <div style="margin-top: 5px; color: #666;">待办任务</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告列表 -->
    <el-card>
      <template #header>
        <span style="font-weight: bold;">最新公告</span>
      </template>
      <div v-for="notice in notices" :key="notice.id" style="padding: 10px 0; border-bottom: 1px solid #eee;">
        <el-tag :type="getNoticeTag(notice.type)" size="small" style="margin-right: 10px;">{{ notice.type }}</el-tag>
        <span style="margin-right: 20px;">{{ notice.title }}</span>
        <span style="color: #999; font-size: 12px;">{{ notice.time }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

// 获取用户类型文本
const getUserTypeText = (userType) => {
  const map = {
    1: '系统管理员',
    2: '物业管理员',
    3: '业主',
    4: '维修人员'
  }
  return map[userType] || '未知用户'
}

// 组件挂载
onMounted(() => {
  userStore.initUserState()
})
</script>