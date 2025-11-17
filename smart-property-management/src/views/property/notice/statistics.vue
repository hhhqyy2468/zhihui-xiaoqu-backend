<template>
  <div class="statistics-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">公告统计</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item>公告统计</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计概览 -->
    <div class="overview-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(64, 158, 255, 0.1);">
                <el-icon :size="32" color="#409EFF"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.totalCount || 0 }}</div>
                <div class="stat-label">总公告数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(103, 194, 58, 0.1);">
                <el-icon :size="32" color="#67C23A"><Check /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.publishedCount || 0 }}</div>
                <div class="stat-label">已发布</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(230, 162, 60, 0.1);">
                <el-icon :size="32" color="#E6A23C"><Star /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.topCount || 0 }}</div>
                <div class="stat-label">置顶公告</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(245, 108, 108, 0.1);">
                <el-icon :size="32" color="#F56C6C"><Clock /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.expiredCount || 0 }}</div>
                <div class="stat-label">已过期</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 统计详情 -->
    <div class="details-section">
      <el-row :gutter="20">
        <!-- 最近公告 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最近公告</span>
                <el-button link type="primary" @click="navigateToList">查看全部</el-button>
              </div>
            </template>
            <div v-loading="loading" class="recent-notices">
              <div v-if="recentNotices.length === 0" class="empty-container">
                <el-empty description="暂无公告" />
              </div>
              <div v-else>
                <div
                  v-for="notice in recentNotices"
                  :key="notice.id"
                  class="notice-item"
                  @click="handleViewNotice(notice)"
                >
                  <div class="notice-header">
                    <div class="notice-title">
                      <el-tag v-if="notice.isTop === 1" type="warning" size="small">置顶</el-tag>
                      <span class="title-text">{{ notice.noticeTitle }}</span>
                    </div>
                    <div class="notice-stats">
                      <span class="read-count">{{ notice.readCount || 0 }}次阅读</span>
                      <span class="publish-time">{{ formatDateTime(notice.publishTime) }}</span>
                    </div>
                  </div>
                  <div class="notice-content">{{ getContentPreview(notice.noticeContent) }}</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 热门公告 -->
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>热门公告</span>
            </template>
            <div v-loading="loading" class="hot-notices">
              <div v-if="hotNotices.length === 0" class="empty-container">
                <el-empty description="暂无数据" />
              </div>
              <div v-else>
                <div
                  v-for="(notice, index) in hotNotices"
                  :key="notice.id"
                  class="hot-notice-item"
                  @click="handleViewNotice(notice)"
                >
                  <div class="rank-number">{{ index + 1 }}</div>
                  <div class="notice-info">
                    <div class="notice-title">{{ notice.noticeTitle }}</div>
                    <div class="notice-meta">
                      <span class="read-count">{{ notice.readCount || 0 }}次阅读</span>
                      <span class="publisher">{{ notice.publisherName }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice.noticeTitle"
      width="800px"
    >
      <div v-if="currentNotice.id" class="notice-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag :type="getNoticeTypeColor(currentNotice.noticeType)">
              {{ getNoticeTypeName(currentNotice.noticeType) }}
            </el-tag>
            <span class="publisher">发布人：{{ currentNotice.publisherName }}</span>
            <span class="publish-time">{{ formatDateTime(currentNotice.publishTime) }}</span>
          </div>
        </div>

        <div class="detail-content">
          <div class="content-text">{{ currentNotice.noticeContent }}</div>
        </div>

        <div class="detail-footer">
          <div class="stats">
            <span>阅读次数：{{ currentNotice.readCount || 0 }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, Check, Star, Clock } from '@element-plus/icons-vue'
import { getNoticeStatistics, getNoticeDetail } from '@/api/notice'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const detailVisible = ref(false)
const overview = ref({})
const recentNotices = ref([])
const hotNotices = ref([])
const currentNotice = ref({})

// 获取公告类型名称
const getNoticeTypeName = (type) => {
  const typeMap = {
    'notice': '通知公告',
    'outage': '停水停电',
    'activity': '社区活动',
    'policy': '政策通知',
    'reminder': '温馨提示'
  }
  return typeMap[type] || '未知'
}

// 获取公告类型颜色
const getNoticeTypeColor = (type) => {
  const colorMap = {
    'notice': 'primary',
    'outage': 'warning',
    'activity': 'success',
    'policy': 'info',
    'reminder': 'info'
  }
  return colorMap[type] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取内容预览
const getContentPreview = (content, maxLength = 80) => {
  if (!content) return ''
  const text = content.replace(/<[^>]*>/g, '') // 移除HTML标签
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    const response = await getNoticeStatistics()
    if (response.code === 200 && response.data) {
      overview.value = response.data.overview || {}
      recentNotices.value = response.data.recentNotices || []
      hotNotices.value = response.data.hotNotices || []
    } else {
      ElMessage.error(response.msg || '获取统计数据失败')
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 查看公告详情
const handleViewNotice = async (notice) => {
  try {
    const response = await getNoticeDetail(notice.id)
    if (response.code === 200 && response.data) {
      currentNotice.value = response.data
      detailVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取公告详情失败')
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}

// 导航到公告列表
const navigateToList = () => {
  router.push('/property/notice')
}

// 初始化
onMounted(() => {
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.statistics-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
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

.overview-section {
  margin-bottom: 20px;

  .stat-card {
    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .stat-content {
        .stat-value {
          font-size: 32px;
          font-weight: bold;
          color: #333;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

.details-section {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .recent-notices,
  .hot-notices {
    .empty-container {
      padding: 40px;
      text-align: center;
    }
  }
}

.notice-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #f9f9f9;
    margin: 0 -16px;
    padding: 16px;
  }

  &:last-child {
    border-bottom: none;
  }

  .notice-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;

    .notice-title {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 500;
      color: #303133;

      .title-text {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .notice-stats {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;
      font-size: 12px;
      color: #999;

      .read-count {
        font-weight: 500;
      }
    }
  }

  .notice-content {
    color: #666;
    font-size: 14px;
    line-height: 1.6;
  }
}

.hot-notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #f9f9f9;
    margin: 0 -16px;
    padding: 12px 16px;
  }

  &:last-child {
    border-bottom: none;
  }

  .rank-number {
    width: 24px;
    height: 24px;
    background: #409eff;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;
    flex-shrink: 0;
  }

  .notice-info {
    flex: 1;

    .notice-title {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .notice-meta {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #999;

      .read-count {
        font-weight: 500;
      }
    }
  }
}

.notice-detail {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #eee;

    .detail-meta {
      display: flex;
      align-items: center;
      gap: 16px;

      .publisher,
      .publish-time {
        color: #666;
        font-size: 14px;
      }
    }
  }

  .detail-content {
    margin-bottom: 20px;

    .content-text {
      line-height: 1.8;
      color: #333;
      font-size: 16px;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }

  .detail-footer {
    padding-top: 16px;
    border-top: 1px solid #eee;

    .stats {
      font-size: 14px;
      color: #666;
    }
  }
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }

  .details-section {
    .el-col {
      margin-bottom: 16px;
    }
  }

  .notice-item {
    .notice-header {
      flex-direction: column;
      gap: 8px;

      .notice-stats {
        flex-direction: row;
        justify-content: space-between;
      }
    }
  }
}
</style>