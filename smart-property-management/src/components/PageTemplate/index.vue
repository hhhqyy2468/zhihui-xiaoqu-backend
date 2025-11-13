<template>
  <div class="page-template">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ pageTitle }}</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="parentTitle">{{ parentTitle }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 动态标签页内容 -->
      <slot name="tabs"></slot>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

// Props
const props = defineProps({
  pageTitle: {
    type: String,
    required: true
  },
  parentTitle: {
    type: String,
    default: ''
  },
  defaultTab: {
    type: String,
    default: 'list'
  }
})

// Router
const router = useRouter()
const route = useRoute()

// 响应式数据
const activeTab = ref(props.defaultTab)

// 标签页切换处理
const handleTabChange = (tabName) => {
  // 子组件可以覆盖此方法
  emit('tab-change', tabName)
}

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    // 可以根据路径设置活动标签页
    console.log('Route changed:', newPath)
  }
)

// 定义事件
const emit = defineEmits(['tab-change'])
</script>

<style lang="scss" scoped>
.page-template {
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

:deep(.el-tabs__content) {
  .search-section,
  .action-section {
    margin-bottom: 20px;
  }

  .table-section {
    background: #fff;
    border-radius: 4px;
    padding: 20px;

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }

  .statistics-section {
    .stats-card {
      .stats-content {
        display: flex;
        align-items: center;
        padding: 10px;

        .stats-icon {
          margin-right: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 60px;
          border-radius: 50%;
          background: rgba(64, 158, 255, 0.1);
        }

        .stats-info {
          flex: 1;

          .stats-number {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stats-label {
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }

  .expand-content {
    padding: 20px;
    background: #f8f9fa;
    border-radius: 4px;

    .expand-item {
      margin-bottom: 16px;

      .label {
        font-weight: 600;
        color: #606266;
        margin-right: 8px;
      }

      .value {
        color: #303133;
      }
    }
  }

  .detail-content {
    .detail-section {
      margin-top: 20px;

      h4 {
        margin: 0 0 10px 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .json-content,
    .error-content {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      line-height: 1.5;
      color: #303133;
      margin: 0;
      overflow-x: auto;
      white-space: pre-wrap;
      word-break: break-all;
    }

    .error-content {
      color: #f56c6c;
      background: #fef0f0;
    }
  }
}
</style>