<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">{{ title }}</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
          {{ item }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页导航（可选） -->
    <el-tabs v-if="tabs && tabs.length > 0" v-model="currentActiveTab" @tab-change="handleTabChange">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.name"
        :label="tab.label"
        :name="tab.name"
      >
        <div class="tab-content">
          <!-- 搜索区域 -->
          <div v-if="showSearch" class="search-section">
            <slot name="search">
              <!-- 默认搜索表单 -->
              <el-form :model="searchForm" inline @submit.prevent>
                <el-form-item label="搜索">
                  <el-input
                    v-model="searchForm.keyword"
                    placeholder="请输入关键词"
                    clearable
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSearch">
                    <el-icon><Search /></el-icon>
                    搜索
                  </el-button>
                  <el-button @click="handleReset">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </el-form-item>
              </el-form>
            </slot>
          </div>

          <!-- 操作按钮 -->
          <div v-if="showActions" class="action-section">
            <slot name="actions">
              <!-- 默认操作按钮 -->
              <el-button
                v-if="showAddButton"
                type="primary"
                @click="handleAdd"
                v-permission="addPermission"
              >
                <el-icon><Plus /></el-icon>
                {{ addButtonText }}
              </el-button>
              <el-button
                v-if="showBatchDeleteButton"
                type="warning"
                @click="handleBatchDelete"
                :disabled="selectedCount === 0"
                v-permission="deletePermission"
              >
                <el-icon><Delete /></el-icon>
                批量删除
              </el-button>
              <el-button
                v-if="showExportButton"
                @click="handleExport"
              >
                <el-icon><Download /></el-icon>
                导出Excel
              </el-button>
            </slot>
          </div>

          <!-- 统计卡片（可选） -->
          <div v-if="showStatistics" class="statistics-cards">
            <slot name="statistics"></slot>
          </div>

          <!-- 数据表格 -->
          <div class="table-section">
            <slot name="table"></slot>
          </div>

          <!-- 分页（可选） -->
          <div v-if="showPagination" class="pagination-wrapper">
            <slot name="pagination"></slot>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 无标签页时的内容 -->
    <div v-else class="no-tab-content">
      <!-- 搜索区域 -->
      <div v-if="showSearch" class="search-section">
        <slot name="search">
          <!-- 默认搜索表单 -->
          <el-form :model="searchForm" inline @submit.prevent>
            <el-form-item label="搜索">
              <el-input
                v-model="searchForm.keyword"
                placeholder="请输入关键词"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </slot>
      </div>

      <!-- 操作按钮 -->
      <div v-if="showActions" class="action-section">
        <slot name="actions">
          <!-- 默认操作按钮 -->
          <el-button
            v-if="showAddButton"
            type="primary"
            @click="handleAdd"
            v-permission="addPermission"
          >
            <el-icon><Plus /></el-icon>
            {{ addButtonText }}
          </el-button>
          <el-button
            v-if="showBatchDeleteButton"
            type="warning"
            @click="handleBatchDelete"
            :disabled="selectedCount === 0"
            v-permission="deletePermission"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          <el-button
            v-if="showExportButton"
            @click="handleExport"
          >
            <el-icon><Download /></el-icon>
            导出Excel
          </el-button>
        </slot>
      </div>

      <!-- 统计卡片（可选） -->
      <div v-if="showStatistics" class="statistics-cards">
        <slot name="statistics"></slot>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <slot name="table"></slot>
      </div>

      <!-- 分页（可选） -->
      <div v-if="showPagination" class="pagination-wrapper">
        <slot name="pagination"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

// Props
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  breadcrumbs: {
    type: Array,
    default: () => []
  },
  tabs: {
    type: Array,
    default: null // { name: string, label: string }[]
  },
  activeTab: {
    type: String,
    default: ''
  },
  showActions: {
    type: Boolean,
    default: true
  },
  showSearch: {
    type: Boolean,
    default: true
  },
  showStatistics: {
    type: Boolean,
    default: false
  },
  showPagination: {
    type: Boolean,
    default: false
  },
  showAddButton: {
    type: Boolean,
    default: false
  },
  showBatchDeleteButton: {
    type: Boolean,
    default: false
  },
  showExportButton: {
    type: Boolean,
    default: false
  },
  addButtonText: {
    type: String,
    default: '新增'
  },
  addPermission: {
    type: String,
    default: ''
  },
  deletePermission: {
    type: String,
    default: ''
  },
  selectedCount: {
    type: Number,
    default: 0
  }
})

// Emits
const emit = defineEmits([
  'tab-change',
  'add',
  'batch-delete',
  'export',
  'search',
  'reset',
  'update:activeTab'
])

// Router
const router = useRouter()

// Local state
const internalActiveTab = ref(props.activeTab)

// Computed
const currentActiveTab = computed({
  get: () => internalActiveTab.value,
  set: (value) => {
    internalActiveTab.value = value
    emit('update:activeTab', value)
  }
})

// Watch props.activeTab
watch(() => props.activeTab, (newVal) => {
  internalActiveTab.value = newVal
})

// Methods
const handleTabChange = (tabName) => {
  currentActiveTab.value = tabName
  emit('tab-change', tabName)

  // 如果tabs有路由配置，可以进行路由导航
  if (props.tabs) {
    const tab = props.tabs.find(t => t.name === tabName)
    if (tab && tab.route) {
      router.push(tab.route)
    }
  }
}

const handleAdd = () => {
  emit('add')
}

const handleBatchDelete = () => {
  emit('batch-delete')
}

const handleExport = () => {
  emit('export')
}

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style lang="scss" scoped>
.log-container {
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

.tab-content,
.no-tab-content {
  // 标签页内容和无标签页内容的样式
}

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

.statistics-cards {
  margin-bottom: 20px;
}
</style>