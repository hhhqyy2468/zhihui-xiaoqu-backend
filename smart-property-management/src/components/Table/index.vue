<template>
  <div class="table-container">
    <!-- 工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <el-button
          v-if="showAdd"
          type="primary"
          :icon="Plus"
          @click="handleAdd"
        >
          新增
        </el-button>

        <el-button
          v-if="showDelete"
          type="danger"
          :icon="Delete"
          :disabled="!hasSelection"
          @click="handleBatchDelete"
        >
          删除
        </el-button>

        <el-button
          v-if="showExport"
          type="success"
          :icon="Download"
          @click="handleExport"
        >
          导出
        </el-button>
      </div>

      <div class="toolbar-right">
        <slot name="toolbar-right"></slot>
        <el-button
          v-if="showRefresh"
          :icon="Refresh"
          circle
          @click="handleRefresh"
        />
      </div>
    </div>

    <!-- 表格 -->
    <el-table
      ref="tableRef"
      :data="data"
      :loading="loading"
      :height="height"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="size"
      :fit="fit"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :row-key="rowKey"
      :empty-text="emptyText"
      :default-expand-all="defaultExpandAll"
      :expand-row-keys="expandRowKeys"
      :default-sort="defaultSort"
      :tooltip-effect="tooltipEffect"
      :show-summary="showSummary"
      :sum-text="sumText"
      :summary-method="summaryMethod"
      :span-method="spanMethod"
      :select-on-indeterminate="selectOnIndeterminate"
      :indent="indent"
      :lazy="lazy"
      :load="load"
      :tree-props="treeProps"
      @selection-change="handleSelectionChange"
      @select="handleSelect"
      @select-all="handleSelectAll"
      @cell-mouse-enter="handleCellMouseEnter"
      @cell-mouse-leave="handleCellMouseLeave"
      @cell-click="handleCellClick"
      @cell-dblclick="handleCellDblclick"
      @row-click="handleRowClick"
      @row-contextmenu="handleRowContextmenu"
      @row-dblclick="handleRowDblclick"
      @header-click="handleHeaderClick"
      @header-contextmenu="handleHeaderContextmenu"
      @sort-change="handleSortChange"
      @filter-change="handleFilterChange"
      @current-change="handleCurrentChange"
      @header-dragend="handleHeaderDragend"
      @expand-change="handleExpandChange"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
        align="center"
        :reserve-selection="reserveSelection"
        :selectable="selectable"
      />

      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
        :index="indexMethod"
      />

      <!-- 数据列 -->
      <slot></slot>

      <!-- 操作列 -->
      <el-table-column
        v-if="showActions"
        label="操作"
        :width="actionWidth"
        :fixed="actionFixed"
        align="center"
      >
        <template #default="scope">
          <slot name="actions" :row="scope.row" :index="scope.$index">
            <el-button
              v-if="showEdit"
              type="primary"
              :icon="Edit"
              size="small"
              link
              @click="handleEdit(scope.row, scope.$index)"
            >
              编辑
            </el-button>
            <el-button
              v-if="showDelete"
              type="danger"
              :icon="Delete"
              size="small"
              link
              @click="handleDelete(scope.row, scope.$index)"
            >
              删除
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="paginationLayout"
        :background="background"
        :small="small"
        @size-change="handleSizeChange"
        @current-change="handleCurrentPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Plus, Delete, Download, Refresh, Edit } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 数据
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },

  // 表格配置
  height: [String, Number],
  maxHeight: [String, Number],
  stripe: {
    type: Boolean,
    default: true
  },
  border: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  },
  fit: {
    type: Boolean,
    default: true
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  highlightCurrentRow: {
    type: Boolean,
    default: true
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  },

  // 选择配置
  showSelection: {
    type: Boolean,
    default: true
  },
  showIndex: {
    type: Boolean,
    default: true
  },
  reserveSelection: {
    type: Boolean,
    default: false
  },
  selectable: Function,
  rowKey: String,

  // 展开行配置
  defaultExpandAll: {
    type: Boolean,
    default: false
  },
  expandRowKeys: Array,

  // 排序配置
  defaultSort: Object,

  // 树形表格配置
  treeProps: Object,
  lazy: Boolean,
  load: Function,
  indent: {
    type: Number,
    default: 16
  },

  // 合计行配置
  showSummary: {
    type: Boolean,
    default: false
  },
  sumText: {
    type: String,
    default: '合计'
  },
  summaryMethod: Function,

  // 合并单元格
  spanMethod: Function,
  selectOnIndeterminate: {
    type: Boolean,
    default: true
  },

  // 工具栏配置
  showToolbar: {
    type: Boolean,
    default: true
  },
  showAdd: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  showEdit: {
    type: Boolean,
    default: true
  },
  showExport: {
    type: Boolean,
    default: false
  },
  showRefresh: {
    type: Boolean,
    default: true
  },

  // 操作列配置
  showActions: {
    type: Boolean,
    default: true
  },
  actionWidth: {
    type: [String, Number],
    default: 150
  },
  actionFixed: {
    type: [Boolean, String],
    default: 'right'
  },

  // 分页配置
  showPagination: {
    type: Boolean,
    default: true
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  small: {
    type: Boolean,
    default: false
  },

  // 其他
  tooltipEffect: {
    type: String,
    default: 'dark'
  }
})

// Emits
const emit = defineEmits([
  'add',
  'edit',
  'delete',
  'batch-delete',
  'export',
  'refresh',
  'selection-change',
  'select',
  'select-all',
  'cell-click',
  'cell-dblclick',
  'row-click',
  'row-dblclick',
  'sort-change',
  'filter-change',
  'size-change',
  'current-change',
  'expand-change'
])

// Refs
const tableRef = ref()
const selectedRows = ref([])

// Computed
const hasSelection = computed(() => selectedRows.value.length > 0)

// Methods
const indexMethod = (index) => {
  return (props.currentPage - 1) * props.pageSize + index + 1
}

// 事件处理
const handleAdd = () => {
  emit('add')
}

const handleEdit = (row, index) => {
  emit('edit', row, index)
}

const handleDelete = (row, index) => {
  emit('delete', row, index)
}

const handleBatchDelete = () => {
  emit('batch-delete', selectedRows.value)
}

const handleExport = () => {
  emit('export')
}

const handleRefresh = () => {
  emit('refresh')
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
  emit('selection-change', selection)
}

const handleSelect = (selection, row) => {
  emit('select', selection, row)
}

const handleSelectAll = (selection) => {
  emit('select-all', selection)
}

const handleCellClick = (row, column, cell, event) => {
  emit('cell-click', row, column, cell, event)
}

const handleCellDblclick = (row, column, cell, event) => {
  emit('cell-dblclick', row, column, cell, event)
}

const handleRowClick = (row, column, event) => {
  emit('row-click', row, column, event)
}

const handleRowDblclick = (row, column, event) => {
  emit('row-dblclick', row, column, event)
}

const handleRowContextmenu = (row, column, event) => {
  emit('row-contextmenu', row, column, event)
}

const handleHeaderClick = (column, event) => {
  emit('header-click', column, event)
}

const handleHeaderContextmenu = (column, event) => {
  emit('header-contextmenu', column, event)
}

const handleSortChange = (sort) => {
  emit('sort-change', sort)
}

const handleFilterChange = (filters) => {
  emit('filter-change', filters)
}

const handleSizeChange = (size) => {
  emit('size-change', size)
}

const handleCurrentPageChange = (page) => {
  emit('current-change', page)
}

const handleExpandChange = (row, expandedRows) => {
  emit('expand-change', row, expandedRows)
}

const handleCellMouseEnter = (row, column, cell, event) => {
  emit('cell-mouse-enter', row, column, cell, event)
}

const handleCellMouseLeave = (row, column, cell, event) => {
  emit('cell-mouse-leave', row, column, cell, event)
}

const handleHeaderDragend = (newWidth, oldWidth, column, event) => {
  emit('header-dragend', newWidth, oldWidth, column, event)
}

const handleCurrentChange = (currentRow, oldCurrentRow) => {
  emit('current-change', currentRow, oldCurrentRow)
}

// 暴露方法
defineExpose({
  tableRef,
  clearSelection: () => tableRef.value?.clearSelection(),
  toggleRowSelection: (row, selected) => tableRef.value?.toggleRowSelection(row, selected),
  toggleAllSelection: () => tableRef.value?.toggleAllSelection(),
  toggleRowExpansion: (row, expanded) => tableRef.value?.toggleRowExpansion(row, expanded),
  setCurrentRow: (row) => tableRef.value?.setCurrentRow(row),
  clearSort: () => tableRef.value?.clearSort(),
  clearFilter: (columnKeys) => tableRef.value?.clearFilter(columnKeys),
  doLayout: () => tableRef.value?.doLayout(),
  sort: (prop, order) => tableRef.value?.sort(prop, order)
})
</script>

<style lang="scss" scoped>
.table-container {
  background: #fff;
  border-radius: 4px;
  padding: 16px;

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .toolbar-left {
      display: flex;
      gap: 8px;
    }

    .toolbar-right {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }

  .table-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>