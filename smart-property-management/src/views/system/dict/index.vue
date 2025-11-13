<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">字典管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>字典管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="字典名称">
          <el-input
            v-model="searchForm.dictName"
            placeholder="请输入字典名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input
            v-model="searchForm.dictType"
            placeholder="请输入字典类型"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
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
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
        v-permission="'system:dict:add'"
      >
        <el-icon><Plus /></el-icon>
        新增字典
      </el-button>
      <el-button
        type="success"
        @click="handleRefreshCache"
        v-permission="'system:dict:refresh'"
      >
        <el-icon><Refresh /></el-icon>
        刷新缓存
      </el-button>
      <el-button
        type="danger"
        @click="handleBatchDelete"
        :disabled="!selectedDicts.length"
        v-permission="'system:dict:delete'"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 字典列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="dictList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="dictId" label="字典编码" width="80" />
        <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip />
        <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewData(row)"
            >
              字典数据
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
              v-permission="'system:dict:edit'"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'system:dict:delete'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑字典对话框 -->
    <el-dialog
      v-model="dictDialogVisible"
      :title="dictForm.dictId ? '编辑字典' : '新增字典'"
      width="600px"
    >
      <el-form
        ref="dictFormRef"
        :model="dictForm"
        :rules="dictRules"
        label-width="100px"
      >
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="dictForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="dictForm.dictType"
            placeholder="请输入字典类型"
            :disabled="!!dictForm.dictId"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dictForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="dictForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dictDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDictSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog
      v-model="dataDialogVisible"
      title="字典数据"
      width="800px"
    >
      <div class="dict-data-section">
        <div class="dict-data-actions">
          <el-button
            type="primary"
            size="small"
            @click="handleAddData"
          >
            <el-icon><Plus /></el-icon>
            新增数据
          </el-button>
        </div>

        <el-table :data="dictDataList" v-loading="dataLoading">
          <el-table-column prop="dataSort" label="排序" width="80" />
          <el-table-column prop="dataLabel" label="字典标签" />
          <el-table-column prop="dataValue" label="字典值" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                size="small"
                @click="handleEditData(row)"
              >
                编辑
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                @click="handleDeleteData(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dataDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑字典数据对话框 -->
    <el-dialog
      v-model="dataFormDialogVisible"
      :title="dataForm.dataId ? '编辑字典数据' : '新增字典数据'"
      width="500px"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRules"
        label-width="100px"
      >
        <el-form-item label="数据标签" prop="dataLabel">
          <el-input v-model="dataForm.dataLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据值" prop="dataValue">
          <el-input v-model="dataForm.dataValue" placeholder="请输入数据值" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dataSort">
          <el-input-number v-model="dataForm.dataSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="dataForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dataFormDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDataSubmit" :loading="dataSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const dataLoading = ref(false)
const dataSubmitting = ref(false)
const dictDialogVisible = ref(false)
const dataDialogVisible = ref(false)
const dataFormDialogVisible = ref(false)

// 表单引用
const dictFormRef = ref()
const dataFormRef = ref()

// 搜索表单
const searchForm = reactive({
  dictName: '',
  dictType: '',
  status: ''
})

// 字典表单
const dictForm = reactive({
  dictId: null,
  dictName: '',
  dictType: '',
  status: 1,
  remark: ''
})

// 字典数据表单
const dataForm = reactive({
  dataId: null,
  dictType: '',
  dataLabel: '',
  dataValue: '',
  dataSort: 0,
  status: 1,
  remark: ''
})

// 数据列表
const dictList = ref([])
const dictDataList = ref([])
const selectedDicts = ref([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单验证规则
const dictRules = {
  dictName: [
    { required: true, message: '请输入字典名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  dictType: [
    { required: true, message: '请输入字典类型', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '字典类型只能包含字母、数字和下划线，且以字母开头', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const dataRules = {
  dataLabel: [
    { required: true, message: '请输入数据标签', trigger: 'blur' }
  ],
  dataValue: [
    { required: true, message: '请输入数据值', trigger: 'blur' }
  ],
  dataSort: [
    { required: true, message: '请输入显示排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生成模拟字典数据
const generateMockDicts = () => {
  const dicts = [
    {
      dictId: 1,
      dictName: '用户性别',
      dictType: 'sys_user_sex',
      status: 1,
      remark: '用户性别字典',
      createTime: new Date()
    },
    {
      dictId: 2,
      dictName: '用户状态',
      dictType: 'sys_user_status',
      status: 1,
      remark: '用户状态字典',
      createTime: new Date()
    },
    {
      dictId: 3,
      dictName: '费用类型',
      dictType: 'fee_type',
      status: 1,
      remark: '物业费用类型',
      createTime: new Date()
    },
    {
      dictId: 4,
      dictName: '投诉类型',
      dictType: 'complaint_type',
      status: 1,
      remark: '投诉分类字典',
      createTime: new Date()
    },
    {
      dictId: 5,
      dictName: '维修类型',
      dictType: 'repair_type',
      status: 1,
      remark: '维修分类字典',
      createTime: new Date()
    }
  ]

  // 模拟分页数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  total.value = dicts.length
  return dicts.slice(start, end)
}

// 生成模拟字典数据
const generateMockDictData = (dictType) => {
  const dataMap = {
    'sys_user_sex': [
      { dataId: 1, dictType, dataLabel: '男', dataValue: '1', dataSort: 1, status: 1, remark: '男性' },
      { dataId: 2, dictType, dataLabel: '女', dataValue: '2', dataSort: 2, status: 1, remark: '女性' }
    ],
    'sys_user_status': [
      { dataId: 3, dictType, dataLabel: '正常', dataValue: '1', dataSort: 1, status: 1, remark: '正常状态' },
      { dataId: 4, dictType, dataLabel: '停用', dataValue: '0', dataSort: 2, status: 1, remark: '停用状态' }
    ],
    'fee_type': [
      { dataId: 5, dictType, dataLabel: '物业费', dataValue: '1', dataSort: 1, status: 1, remark: '物业管理费' },
      { dataId: 6, dictType, dataLabel: '停车费', dataValue: '2', dataSort: 2, status: 1, remark: '停车管理费' },
      { dataId: 7, dictType, dataLabel: '水电费', dataValue: '3', dataSort: 3, status: 1, remark: '水电费用' }
    ],
    'complaint_type': [
      { dataId: 8, dictType, dataLabel: '噪音投诉', dataValue: '1', dataSort: 1, status: 1, remark: '噪音扰民' },
      { dataId: 9, dictType, dataLabel: '设施损坏', dataValue: '2', dataSort: 2, status: 1, remark: '公共设施损坏' }
    ],
    'repair_type': [
      { dataId: 10, dictType, dataLabel: '水电维修', dataValue: '1', dataSort: 1, status: 1, remark: '水电设施维修' },
      { dataId: 11, dictType, dataLabel: '门窗维修', dataValue: '2', dataSort: 2, status: 1, remark: '门窗维修' }
    ]
  }

  return dataMap[dictType] || []
}

// 加载字典列表
const loadDictList = () => {
  loading.value = true
  setTimeout(() => {
    dictList.value = generateMockDicts()
    loading.value = false
  }, 500)
}

// 加载字典数据
const loadDictData = (dictType) => {
  dataLoading.value = true
  setTimeout(() => {
    dictDataList.value = generateMockDictData(dictType)
    dataLoading.value = false
  }, 300)
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadDictList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    dictName: '',
    dictType: '',
    status: ''
  })
  handleSearch()
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedDicts.value = selection
}

// 新增字典
const handleAdd = () => {
  Object.assign(dictForm, {
    dictId: null,
    dictName: '',
    dictType: '',
    status: 1,
    remark: ''
  })
  dictDialogVisible.value = true
}

// 编辑字典
const handleEdit = (row) => {
  Object.assign(dictForm, { ...row })
  dictDialogVisible.value = true
}

// 删除字典
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除字典"${row.dictName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadDictList()
  })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedDicts.value.length} 个字典吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量删除成功')
    loadDictList()
  })
}

// 提交字典表单
const handleDictSubmit = async () => {
  try {
    await dictFormRef.value.validate()
    submitting.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(dictForm.dictId ? '编辑成功' : '新增成功')
    dictDialogVisible.value = false
    loadDictList()
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 查看字典数据
const handleViewData = (row) => {
  dataForm.dictType = row.dictType
  loadDictData(row.dictType)
  dataDialogVisible.value = true
}

// 新增字典数据
const handleAddData = () => {
  Object.assign(dataForm, {
    dataId: null,
    dictType: dataForm.dictType,
    dataLabel: '',
    dataValue: '',
    dataSort: 0,
    status: 1,
    remark: ''
  })
  dataFormDialogVisible.value = true
}

// 编辑字典数据
const handleEditData = (row) => {
  Object.assign(dataForm, { ...row })
  dataFormDialogVisible.value = true
}

// 删除字典数据
const handleDeleteData = (row) => {
  ElMessageBox.confirm(
    `确定要删除字典数据"${row.dataLabel}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadDictData(dataForm.dictType)
  })
}

// 提交字典数据表单
const handleDataSubmit = async () => {
  try {
    await dataFormRef.value.validate()
    dataSubmitting.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(dataForm.dataId ? '编辑成功' : '新增成功')
    dataFormDialogVisible.value = false
    loadDictData(dataForm.dictType)
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    dataSubmitting.value = false
  }
}

// 刷新缓存
const handleRefreshCache = () => {
  ElMessage.success('缓存刷新成功')
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadDictList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadDictList()
}

onMounted(() => {
  loadDictList()
})
</script>

<style lang="scss" scoped>
.dict-container {
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

.dict-data-section {
  .dict-data-actions {
    margin-bottom: 16px;
  }
}
</style>