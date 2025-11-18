<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">车位管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>停车管理</el-breadcrumb-item>
        <el-breadcrumb-item>车位管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 车位管理 -->
      <el-tab-pane label="车位管理" name="list">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form :model="searchForm" inline>
            <el-form-item label="车位编号">
              <el-input
                v-model="searchForm.spaceNo"
                placeholder="请输入车位编号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位位置">
              <el-input
                v-model="searchForm.location"
                placeholder="请输入车位位置"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="车位状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 150px"
              >
                <el-option label="空闲" :value="1" />
                <el-option label="已租" :value="2" />
                <el-option label="维修中" :value="3" />
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

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            type="primary"
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新增车位
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedSpaces.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          </div>

        <!-- 车位表格 -->
        <div class="table-section">
          <el-table
            v-loading="loading"
            :data="parkingSpaceList"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="spaceNo" label="车位编号" width="120" sortable />
            <el-table-column prop="location" label="车位位置" show-overflow-tooltip />
            <el-table-column prop="spaceStatus" label="车位状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.spaceStatus)">
                  {{ getStatusName(row.spaceStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="currentTenant" label="使用人" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  link
                  type="info"
                  @click="handleViewRentals(row)"
                >
                  租赁记录
                </el-button>
                <el-button
                  link
                  type="danger"
                  @click="handleDelete(row)"
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
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑车位对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="车位编号" prop="spaceNo">
          <el-input v-model="form.spaceNo" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="车位位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入车位位置" />
        </el-form-item>
        <el-form-item label="月租金" prop="monthlyRent">
          <el-input-number
            v-model="form.monthlyRent"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="车位状态" prop="spaceStatus">
          <el-radio-group v-model="form.spaceStatus">
            <el-radio :value="1">空闲</el-radio>
            <el-radio :value="2">已租</el-radio>
            <el-radio :value="3">维修中</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  Upload,
  Download
} from '@element-plus/icons-vue'
import {
  listParkingSpaces,
  getParkingSpace,
  addParkingSpace,
  updateParkingSpace,
  deleteParkingSpaces
} from '@/api/parkingSpace'

// 响应式数据
const activeTab = ref('list')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增车位')

// 表单数据
const formRef = ref()
const form = reactive({
  spaceNo: '',
  location: '',
  spaceStatus: 1,
  monthlyRent: 0,
  remark: ''
})

// 搜索表单
const searchForm = reactive({
  spaceNo: '',
  location: '',
  status: null
})

// 车位数据
const parkingSpaceList = ref([])
const selectedSpaces = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)


// 表单验证规则
const rules = {
  spaceNo: [
    { required: true, message: '请输入车位编号', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入车位位置', trigger: 'blur' }
  ],
  spaceStatus: [
    { required: true, message: '请选择车位状态', trigger: 'change' }
  ],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' }
  ]
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '空闲',
    2: '已租',
    3: '维修中'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}


// 加载车位数据
const loadParkingSpaces = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      spaceNo: searchForm.spaceNo,
      location: searchForm.location,
      status: searchForm.status
    }
    const response = await listParkingSpaces(params)
    if (response.code === 200) {
      parkingSpaceList.value = response.data.rows
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载车位数据失败')
    }
  } catch (error) {
    console.error('加载车位数据错误:', error)
    ElMessage.error('加载车位数据失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  loadParkingSpaces()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadParkingSpaces()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    spaceNo: '',
    location: '',
    status: null
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增车位'
  Object.assign(form, {
    spaceNo: '',
    location: '',
    spaceStatus: 1,
    monthlyRent: 0,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑车位'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除车位 ${row.spaceNo} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteParkingSpaces([row.id])
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadParkingSpaces()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除车位错误:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedSpaces.value.length === 0) {
    ElMessage.warning('请选择要删除的车位')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedSpaces.value.length} 个车位吗？`,
    '批量删除警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedSpaces.value.map(space => space.id)
      const response = await deleteParkingSpaces(ids)
      if (response.code === 200) {
        ElMessage.success('批量删除成功')
        loadParkingSpaces()
      } else {
        ElMessage.error(response.msg || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除车位错误:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

// 查看租赁记录
const handleViewRentals = (row) => {
  ElMessage.info(`查看车位 ${row.spaceNo} 的租赁记录`)
}


// 提交表单
const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (dialogTitle.value === '新增车位') {
          response = await addParkingSpace(form)
        } else {
          response = await updateParkingSpace(form)
        }

        if (response.code === 200) {
          ElMessage.success(dialogTitle.value + '成功')
          dialogVisible.value = false
          loadParkingSpaces()
        } else {
          ElMessage.error(response.msg || (dialogTitle.value + '失败'))
        }
      } catch (error) {
        console.error(dialogTitle.value + '错误:', error)
        ElMessage.error(dialogTitle.value + '失败')
      }
    }
  })
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedSpaces.value = selection
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadParkingSpaces()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadParkingSpaces()
}

// 初始化
onMounted(() => {
  loadParkingSpaces()
})
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

</style>