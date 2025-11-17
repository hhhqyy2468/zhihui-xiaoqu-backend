<template>
  <div class="notice-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">社区公告</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>业主门户</el-breadcrumb-item>
        <el-breadcrumb-item>社区公告</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-form inline>
        <el-form-item label="公告类型">
          <el-select
            v-model="filterForm.noticeType"
            placeholder="全部类型"
            clearable
            style="width: 150px"
            @change="loadNotices"
          >
            <el-option label="全部" value="" />
            <el-option label="通知公告" value="notice" />
            <el-option label="停水停电" value="outage" />
            <el-option label="社区活动" value="activity" />
            <el-option label="政策通知" value="policy" />
            <el-option label="温馨提示" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="loadNotices">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告列表 -->
    <div class="notice-list">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="noticeList.length === 0" class="empty-container">
        <el-empty description="暂无公告" />
      </div>

      <div v-else>
        <div
          v-for="notice in noticeList"
          :key="notice.id"
          class="notice-item"
          :class="{ 'is-read': notice.isRead, 'is-top': notice.isTop === 1 }"
          @click="handleViewNotice(notice)"
        >
          <div class="notice-header">
            <div class="notice-title">
              <el-tag v-if="notice.isTop === 1" type="warning" size="small" class="top-tag">
                置顶
              </el-tag>
              <span class="title-text">{{ notice.noticeTitle }}</span>
              <el-tag
                v-if="!notice.isRead"
                type="danger"
                size="small"
                class="unread-tag"
              >
                未读
              </el-tag>
            </div>
            <div class="notice-meta">
              <span class="notice-type">
                <el-tag :type="getNoticeTypeColor(notice.noticeType)" size="small">
                  {{ getNoticeTypeName(notice.noticeType) }}
                </el-tag>
              </span>
              <span class="publish-time">{{ formatDateTime(notice.publishTime) }}</span>
            </div>
          </div>

          <div class="notice-content">
            {{ getContentPreview(notice.noticeContent) }}
          </div>

          <div class="notice-footer">
            <span class="publisher">{{ notice.publisherName }}</span>
            <span class="read-count">{{ notice.readCount }}次阅读</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice.noticeTitle"
      width="800px"
      :before-close="handleCloseDetail"
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
          <div v-if="currentNotice.isTop === 1" class="top-badge">
            <el-icon color="#E6A23C"><Star /></el-icon>
            <span>置顶公告</span>
          </div>
        </div>

        <div class="detail-content">
          <div class="content-text">{{ currentNotice.noticeContent }}</div>

          <div v-if="currentNotice.attachmentUrls" class="attachments">
            <h4>附件列表</h4>
            <div class="attachment-list">
              <div
                v-for="(attachment, index) in getAttachments(currentNotice.attachmentUrls)"
                :key="index"
                class="attachment-item"
              >
                <el-icon><Document /></el-icon>
                <span>{{ attachment }}</span>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="handleDownload(attachment)"
                >
                  下载
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-footer">
          <div class="stats">
            <span>阅读次数：{{ currentNotice.readCount }}</span>
            <span v-if="currentNotice.effectiveEndTime">
              有效期至：{{ formatDateTime(currentNotice.effectiveEndTime) }}
            </span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Star, Document } from '@element-plus/icons-vue'
import { getUserNotices, markNoticeAsRead } from '@/api/notice'

// 响应式数据
const loading = ref(false)
const detailVisible = ref(false)
const noticeList = ref([])
const currentNotice = ref({})

// 筛选表单
const filterForm = reactive({
  noticeType: ''
})

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
const getContentPreview = (content, maxLength = 100) => {
  if (!content) return ''
  const text = content.replace(/<[^>]*>/g, '') // 移除HTML标签
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 解析附件URL
const getAttachments = (attachmentUrls) => {
  if (!attachmentUrls) return []
  return attachmentUrls.split(',').map(url => url.trim()).filter(url => url)
}

// 加载公告列表
const loadNotices = async () => {
  loading.value = true
  try {
    const params = {
      ...filterForm
    }

    const response = await getUserNotices(params)
    if (response.code === 200 && response.data) {
      noticeList.value = response.data
    } else {
      ElMessage.error(response.msg || '获取公告列表失败')
      noticeList.value = []
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载公告列表失败')
    noticeList.value = []
  } finally {
    loading.value = false
  }
}

// 查看公告详情
const handleViewNotice = async (notice) => {
  currentNotice.value = { ...notice }
  detailVisible.value = true

  // 如果未读，标记为已读
  if (!notice.isRead) {
    try {
      await markNoticeAsRead(notice.id)
      // 更新本地状态
      notice.isRead = true
      notice.readTime = new Date().toISOString()
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

// 关闭详情对话框
const handleCloseDetail = () => {
  detailVisible.value = false
}

// 下载附件
const handleDownload = (attachment) => {
  ElMessage.info(`下载文件: ${attachment}`)
  // 这里可以实现实际的下载逻辑
}

// 初始化
onMounted(() => {
  loadNotices()
})
</script>

<style lang="scss" scoped>
.notice-container {
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

.filter-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}

.notice-list {
  .loading-container,
  .empty-container {
    padding: 40px;
    text-align: center;
    background: #fff;
    border-radius: 4px;
  }
}

.notice-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid transparent;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  &.is-read {
    opacity: 0.8;
    border-left-color: #ddd;
  }

  &.is-top {
    border-left-color: #E6A23C;
    background: linear-gradient(90deg, rgba(230, 162, 60, 0.05) 0%, #fff 100%);
  }

  .notice-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;

    .notice-title {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      color: #303133;

      .top-tag {
        flex-shrink: 0;
      }

      .title-text {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .unread-tag {
        flex-shrink: 0;
      }
    }

    .notice-meta {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;

      .publish-time {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .notice-content {
    color: #666;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 12px;
  }

  .notice-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #999;

    .read-count {
      display: flex;
      align-items: center;
      gap: 4px;
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

    .top-badge {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #E6A23C;
      font-weight: 600;
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

    .attachments {
      margin-top: 24px;

      h4 {
        margin: 0 0 12px 0;
        color: #333;
        font-size: 16px;
      }

      .attachment-list {
        .attachment-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 12px;
          background: #f5f7fa;
          border-radius: 4px;
          margin-bottom: 8px;

          .el-icon {
            color: #409eff;
          }

          span {
            flex: 1;
            color: #606266;
          }
        }
      }
    }
  }

  .detail-footer {
    padding-top: 16px;
    border-top: 1px solid #eee;

    .stats {
      display: flex;
      gap: 24px;
      font-size: 14px;
      color: #666;
    }
  }
}

@media (max-width: 768px) {
  .notice-container {
    padding: 10px;
  }

  .notice-item {
    padding: 16px;

    .notice-header {
      flex-direction: column;
      gap: 8px;

      .notice-meta {
        flex-direction: row;
        justify-content: space-between;
      }
    }
  }
}
</style>