<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">楼栋管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>楼栋管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button type="primary" @click="handleAdd" v-permission="'property:building:add'">
        <el-icon><Plus /></el-icon>
        新增楼栋
      </el-button>
      <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0" v-permission="'property:building:delete'">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出Excel
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="queryParams"
        inline
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="楼栋编号" prop="buildingNo">
              <el-input
                v-model="queryParams.buildingNo"
                placeholder="请输入楼栋编号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="楼栋名称" prop="buildingName">
              <el-input
                v-model="queryParams.buildingName"
                placeholder="请输入楼栋名称"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="建筑年份" prop="buildYear">
              <el-date-picker
                v-model="queryParams.buildYear"
                type="year"
                placeholder="请选择建筑年份"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="buildingList"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />

        <el-table-column
          prop="buildingNo"
          label="楼栋编号"
          width="120"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary">{{ row.buildingNo }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="buildingName"
          label="楼栋名称"
          width="150"
          sortable="custom"
          show-overflow-tooltip
        />

        <el-table-column
          prop="floorCount"
          label="楼层数"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="info">{{ row.floorCount }}层</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="unitCount"
          label="单元数"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="success">{{ row.unitCount }}个</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="totalHouseholds"
          label="总户数"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <span class="number-cell">{{ row.totalHouseholds || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="address"
          label="详细地址"
          min-width="200"
          show-overflow-tooltip
        />

        <el-table-column
          prop="buildYear"
          label="建筑年份"
          width="120"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span>{{ row.buildYear }}年</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
              v-permission="'property:building:edit'"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewUnits(row)"
            >
              <el-icon><View /></el-icon>
              单元
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'property:building:delete'"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        class="building-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼栋编号" prop="buildingNo">
              <el-input
                v-model="form.buildingNo"
                placeholder="请输入楼栋编号（如：A01）"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼栋名称" prop="buildingName">
              <el-input
                v-model="form.buildingName"
                placeholder="请输入楼栋名称（如：1号楼）"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层数" prop="floorCount">
              <el-input-number
                v-model="form.floorCount"
                :min="1"
                :max="99"
                placeholder="请输入楼层数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单元数" prop="unitCount">
              <el-input-number
                v-model="form.unitCount"
                :min="1"
                :max="20"
                placeholder="请输入单元数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建筑年份" prop="buildYear">
              <el-date-picker
                v-model="form.buildYear"
                type="year"
                placeholder="请选择建筑年份"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结构类型" prop="structureType">
              <el-select v-model="form.structureType" placeholder="请选择结构类型" style="width: 100%">
                <el-option label="钢筋混凝土" value="钢筋混凝土" />
                <el-option label="钢结构" value="钢结构" />
                <el-option label="砖混结构" value="砖混结构" />
                <el-option label="木结构" value="木结构" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看单元对话框 -->
    <el-dialog
      v-model="unitDialogVisible"
      title="单元列表"
      width="900px"
      @close="unitDialogVisible = false"
    >
      <div class="unit-list-content">
        <div class="building-info">
          <el-tag type="primary">{{ currentBuilding.buildingName }}</el-tag>
          <span class="address">{{ currentBuilding.address }}</span>
        </div>

        <el-table :data="unitList" border stripe v-loading="unitLoading">
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column prop="unitCode" label="单元编号" width="120" align="center">
            <template #default="{ row }">
              <el-tag type="info">{{ row.unitCode }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="unitName" label="单元名称" width="120" />
          <el-table-column prop="floorCount" label="楼层数" width="100" align="center">
            <template #default="{ row }">
              {{ row.floorCount }}层
            </template>
          </el-table-column>
          <el-table-column prop="roomsPerFloor" label="每层户数" width="100" align="center" />
          <el-table-column prop="totalRooms" label="总户数" width="100" align="center">
            <template #default="{ row }">
              <span class="number-cell">{{ row.totalRooms }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                @click="handleViewHouses(row)"
              >
                查看房产
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="unitDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  Download,
  Edit,
  View
} from '@element-plus/icons-vue'
import {
  getBuildingList,
  getBuildingDetail,
  addBuilding,
  updateBuilding,
  deleteBuilding,
  batchDeleteBuilding
} from '@/api/building'
import { getUnitsByBuilding } from '@/api/unit'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const unitLoading = ref(false)
const dialogVisible = ref(false)
const unitDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const buildingList = ref([])
const unitList = ref([])
const total = ref(0)
const currentBuilding = ref({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingNo: '',
  buildingName: '',
  buildYear: null
})

// 表单数据
const form = reactive({
  buildingId: null,
  buildingNo: '',
  buildingName: '',
  floorCount: 1,
  unitCount: 1,
  address: '',
  buildYear: null,
  structureType: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  buildingNo: [
    { required: true, message: '请输入楼栋编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '楼栋编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  buildingName: [
    { required: true, message: '请输入楼栋名称', trigger: 'blur' },
    { min: 2, max: 50, message: '楼栋名称长度在2到50个字符', trigger: 'blur' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '楼层数必须在1-99之间', trigger: 'blur' }
  ],
  unitCount: [
    { required: true, message: '请输入单元数', trigger: 'blur' },
    { type: 'number', min: 1, max: 20, message: '单元数必须在1-20之间', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  buildYear: [
    { required: true, message: '请选择建筑年份', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑楼栋' : '新增楼栋')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取楼栋列表
const getBuildingListData = async () => {
  loading.value = true
  try {
    const response = await getBuildingList(queryParams)
    const data = handleResponse(response)
    buildingList.value = data.list || data.records || []
    total.value = data.total || 0
  } catch (error) {
    handleError(error, '获取楼栋列表失败')
    buildingList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getBuildingListData()
}

// 重置搜索
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.buildingNo = ''
  queryParams.buildingName = ''
  queryParams.buildYear = null
  getBuildingListData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getBuildingListData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getBuildingListData()
}

// 排序变化
const handleSortChange = ({ column, prop, order }) => {
  // 这里可以处理排序逻辑
  console.log('排序变化:', { column, prop, order })
  getBuildingListData()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增楼栋
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑楼栋
const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const response = await getBuildingDetail(row.buildingId)
    const data = handleResponse(response)
    Object.assign(form, data)
    dialogVisible.value = true
  } catch (error) {
    handleError(error, '获取楼栋详情失败')
  }
}

// 删除楼栋
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除楼栋"${row.buildingName}"吗？删除后不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteBuilding(row.buildingId)
    ElMessage.success('删除成功')
    getBuildingListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的楼栋')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个楼栋吗？删除后不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const buildingIds = selectedRows.value.map(row => row.buildingId)
    await batchDeleteBuilding(buildingIds)
    ElMessage.success('批量删除成功')
    getBuildingListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '批量删除失败')
    }
  }
}

// 查看单元
const handleViewUnits = async (row) => {
  currentBuilding.value = row
  unitLoading.value = true
  unitDialogVisible.value = true

  try {
    const response = await getUnitsByBuilding(row.buildingId)
    unitList.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取单元列表失败')
    unitList.value = []
  } finally {
    unitLoading.value = false
  }
}

// 查看房产
const handleViewHouses = (row) => {
  ElMessage.info(`查看单元 ${row.unitName} 的房产信息`)
  // 这里可以跳转到房产页面并传递筛选条件
}

// 导出Excel
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateBuilding(form)
      ElMessage.success('编辑成功')
    } else {
      await addBuilding(form)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    getBuildingListData()
  } catch (error) {
    handleError(error, isEdit.value ? '编辑失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    buildingId: null,
    buildingNo: '',
    buildingName: '',
    floorCount: 1,
    unitCount: 1,
    address: '',
    buildYear: null,
    structureType: '',
    remark: ''
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 组件挂载
onMounted(() => {
  getBuildingListData()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

// 页面头部
.page-header {
  margin-bottom: 20px;

  .page-title {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

// 操作按钮区域
.action-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

// 搜索区域
.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;

  .el-form-item {
    margin-bottom: 0;
  }
}



  // 表格区域
  .table-section {
    .el-table {
      margin-bottom: 16px;

      .number-cell {
        font-weight: 600;
        color: #409eff;
      }

      .time-cell {
        font-size: 13px;
        color: #909399;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding: 16px 0;
    }
  }

  // 批量操作
  .batch-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 20px;
    padding: 15px 20px;
    background-color: #f0f9ff;
    border: 1px solid #bfdbfe;
    border-radius: 6px;

    .batch-info {
      font-size: 14px;
      color: #1e40af;
    }

    .batch-buttons {
      display: flex;
      gap: 10px;
    }
  }

  .building-form {
    .el-form-item {
      margin-bottom: 18px;
    }
  }

  .unit-list-content {
    .building-info {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;

      .address {
        margin-left: 8px;
        color: #666;
      }
    }

    .number-cell {
      font-weight: 600;
      color: #409eff;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #fafafa;
      font-weight: 600;
      color: #303133;
    }
  }

  .el-table__body {
    tr:hover > td {
      background-color: #f5f7fa;
    }
  }
}

// 卡片样式优化
:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .el-card__header {
    border-bottom: 1px solid #ebeef5;
    padding: 16px 20px;
  }

  .el-card__body {
    padding: 20px;
  }
}

// 按钮样式优化
:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
}

// 输入框样式优化
:deep(.el-input__wrapper) {
  border-radius: 6px;
}

// 标签样式优化
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}
</style>