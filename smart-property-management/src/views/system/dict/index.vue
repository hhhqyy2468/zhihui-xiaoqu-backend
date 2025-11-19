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
        <el-table-column prop="id" label="字典编码" width="80" />
        <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip />
        <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '启用' : '停用' }}
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
          :current-page="pagination.current"
          :page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑字典对话框 -->
    <el-dialog
      v-model="dictDialogVisible"
      :title="dictForm.id ? '编辑字典' : '新增字典'"
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
            :disabled="!!dictForm.id"
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
            v-permission="'system:dict:add'"
          >
            <el-icon><Plus /></el-icon>
            新增数据
          </el-button>
          <el-button
            type="success"
            size="small"
            @click="handleBatchEnableData"
            :disabled="!selectedDictData.length"
            v-permission="'system:dict:edit'"
          >
            <el-icon><Switch /></el-icon>
            批量启用
          </el-button>
          <el-button
            type="warning"
            size="small"
            @click="handleBatchDisableData"
            :disabled="!selectedDictData.length"
            v-permission="'system:dict:edit'"
          >
            <el-icon><Switch /></el-icon>
            批量停用
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleBatchDeleteData"
            :disabled="!selectedDictData.length"
            v-permission="'system:dict:delete'"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>

        <el-table
          :data="dictDataList"
          v-loading="dataLoading"
          @selection-change="handleDataSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="dictSort" label="排序" width="80" />
          <el-table-column prop="dictLabel" label="字典标签" />
          <el-table-column prop="dictValue" label="字典值" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                {{ row.status === '1' ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                size="small"
                @click="handleEditData(row)"
                v-permission="'system:dict:edit'"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                link
                :type="row.status === '1' ? 'warning' : 'success'"
                size="small"
                @click="handleToggleDataStatus(row)"
                v-permission="'system:dict:edit'"
              >
                <el-icon><Switch /></el-icon>
                {{ row.status === '1' ? '停用' : '启用' }}
              </el-button>
              <el-button
                link
                type="info"
                size="small"
                @click="handleCopyData(row)"
              >
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                @click="handleDeleteData(row)"
                v-permission="'system:dict:delete'"
              >
                <el-icon><Delete /></el-icon>
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
      :title="dataForm.id ? '编辑字典数据' : '新增字典数据'"
      width="500px"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRules"
        label-width="100px"
      >
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入数据值" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="dataForm.dictSort" :min="0" :max="999" />
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
import { Search, Refresh, Plus, Delete, Edit, Switch, CopyDocument } from '@element-plus/icons-vue'

// 导入API接口
import {
  getDictTypePage,
  addDictType,
  updateDictType,
  deleteDictType,
  getDictDataByType,
  addDictData,
  updateDictData,
  deleteDictData
} from '@/api/dict'

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
  id: null,
  dictName: '',
  dictType: '',
  status: '1',
  remark: ''
})

// 字典数据表单
const dataForm = reactive({
  id: null,
  dictType: '',
  dictLabel: '',
  dictValue: '',
  dictSort: 0,
  status: '1',
  remark: ''
})

// 数据列表
const dictList = ref([])
const dictDataList = ref([])
const selectedDicts = ref([])
const selectedDictData = ref([])

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

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
  dictLabel: [
    { required: true, message: '请输入数据标签', trigger: 'blur' }
  ],
  dictValue: [
    { required: true, message: '请输入数据值', trigger: 'blur' }
  ],
  dictSort: [
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

// 加载字典列表
const loadDictList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getDictTypePage(params)
    if (response.code === 200) {
      dictList.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取字典类型列表失败')
    }
  } catch (error) {
    console.error('加载字典类型列表失败:', error)
    ElMessage.error('加载字典类型列表失败')
  } finally {
    loading.value = false
  }
}

// 加载字典数据
const loadDictData = async (dictType) => {
  dataLoading.value = true
  try {
    const response = await getDictDataByType(dictType)
    if (response.code === 200) {
      dictDataList.value = response.data
    } else {
      ElMessage.error(response.message || '获取字典数据失败')
    }
  } catch (error) {
    console.error('加载字典数据失败:', error)
    ElMessage.error('加载字典数据失败')
  } finally {
    dataLoading.value = false
  }
}


// 搜索
const handleSearch = () => {
  pagination.current = 1
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

// 字典数据选择变更
const handleDataSelectionChange = (selection) => {
  selectedDictData.value = selection
}

// 新增字典
const handleAdd = () => {
  Object.assign(dictForm, {
    id: null,
    dictName: '',
    dictType: '',
    status: '1',
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
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字典"${row.dictName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteDictType(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadDictList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedDicts.value.length} 个字典吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const dictIds = selectedDicts.value.map(item => item.id).join(',')
    const response = await deleteDictType(dictIds)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      loadDictList()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 提交字典表单
const handleDictSubmit = async () => {
  try {
    await dictFormRef.value.validate()
    submitting.value = true

    const response = dictForm.id
      ? await updateDictType(dictForm)
      : await addDictType(dictForm)

    if (response.code === 200) {
      ElMessage.success(dictForm.id ? '编辑成功' : '新增成功')
      dictDialogVisible.value = false
      loadDictList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '操作失败')
    } else {
      console.error('表单验证失败:', error)
    }
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
    id: null,
    dictType: dataForm.dictType,
    dictLabel: '',
    dictValue: '',
    dictSort: 0,
    status: '1',
    remark: ''
  })
  dataFormDialogVisible.value = true
}

// 编辑字典数据
const handleEditData = (row) => {
  Object.assign(dataForm, { ...row })
  dataFormDialogVisible.value = true
}

// 切换字典数据状态
const handleToggleDataStatus = async (row) => {
  try {
    const newStatus = row.status === '1' ? '0' : '1'
    const action = newStatus === '1' ? '启用' : '停用'

    await ElMessageBox.confirm(
      `确定要${action}字典数据"${row.dictLabel}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const updateData = { ...row, status: newStatus }
    const response = await updateDictData(updateData)

    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      loadDictData(dataForm.dictType)
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 复制字典数据
const handleCopyData = (row) => {
  Object.assign(dataForm, {
    ...row,
    id: null,
    dictLabel: row.dictLabel + '_副本',
    dictValue: row.dictValue + '_copy'
  })
  dataFormDialogVisible.value = true
  ElMessage.info('已复制数据，请修改后保存')
}

// 批量启用字典数据
const handleBatchEnableData = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedDictData.value.length} 条字典数据吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const promises = selectedDictData.value.map(item =>
      updateDictData({ ...item, status: '1' })
    )

    await Promise.all(promises)
    ElMessage.success('批量启用成功')
    loadDictData(dataForm.dictType)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
      ElMessage.error('批量启用失败')
    }
  }
}

// 批量停用字典数据
const handleBatchDisableData = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要停用选中的 ${selectedDictData.value.length} 条字典数据吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const promises = selectedDictData.value.map(item =>
      updateDictData({ ...item, status: '0' })
    )

    await Promise.all(promises)
    ElMessage.success('批量停用成功')
    loadDictData(dataForm.dictType)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量停用失败:', error)
      ElMessage.error('批量停用失败')
    }
  }
}

// 批量删除字典数据
const handleBatchDeleteData = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedDictData.value.length} 条字典数据吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const promises = selectedDictData.value.map(item =>
      deleteDictData(item.id)
    )

    await Promise.all(promises)
    ElMessage.success('批量删除成功')
    loadDictData(dataForm.dictType)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 删除字典数据
const handleDeleteData = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字典数据"${row.dictLabel}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteDictData(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadDictData(dataForm.dictType)
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典数据失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交字典数据表单
const handleDataSubmit = async () => {
  try {
    await dataFormRef.value.validate()
    dataSubmitting.value = true

    const response = dataForm.id
      ? await updateDictData(dataForm)
      : await addDictData(dataForm)

    if (response.code === 200) {
      ElMessage.success(dataForm.id ? '编辑成功' : '新增成功')
      dataFormDialogVisible.value = false
      loadDictData(dataForm.dictType)
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '操作失败')
    } else {
      console.error('表单验证失败:', error)
    }
  } finally {
    dataSubmitting.value = false
  }
}


// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadDictList()
}

const handleCurrentChange = (val) => {
  pagination.current = val
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
    display: flex;
    gap: 8px;
    flex-wrap: wrap;

    .el-button {
      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .el-table {
    .el-button {
      padding: 4px 8px;

      .el-icon {
        margin-right: 2px;
      }
    }
  }
}
</style>