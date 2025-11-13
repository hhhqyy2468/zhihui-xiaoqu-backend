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
                <el-option label="空闲" value="1" />
                <el-option label="已租" value="2" />
                <el-option label="维修中" value="3" />
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
            type="success"
            @click="handleImport"
          >
            <el-icon><Upload /></el-icon>
            批量导入
          </el-button>
          <el-button
            type="warning"
            @click="handleExport"
          >
            <el-icon><Download /></el-icon>
            导出数据
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
            <el-table-column prop="status" label="车位状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.status)">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="monthlyRent" label="月租金" width="120">
              <template #default="{ row }">
                ¥{{ row.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column prop="ownerName" label="使用人" width="120" />
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
        <el-form-item label="车位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">空闲</el-radio>
            <el-radio value="2">已租</el-radio>
            <el-radio value="3">维修中</el-radio>
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
  Upload,
  Download
} from '@element-plus/icons-vue'

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
  monthlyRent: 0,
  status: '1',
  remark: ''
})

// 搜索表单
const searchForm = reactive({
  spaceNo: '',
  location: '',
  status: ''
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

// 生成模拟数据
const generateMockData = () => {
  const spaces = []
  const locations = ['A区地下1层', 'A区地下2层', 'B区地下1层', 'B区地下2层', 'C区地面停车场']
  const statuses = ['1', '2', '3']
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']

  for (let i = 1; i <= 50; i++) {
    spaces.push({
      id: i,
      spaceNo: `P${String(i).padStart(3, '0')}`,
      location: locations[Math.floor(Math.random() * locations.length)] + `${Math.floor(Math.random() * 100) + 1}号`,
      status: statuses[Math.floor(Math.random() * statuses.length)],
      monthlyRent: Math.floor(Math.random() * 500) + 200,
      ownerName: Math.random() > 0.3 ? owners[Math.floor(Math.random() * owners.length)] : '',
      createTime: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000),
      remark: ''
    })
  }

  return spaces
}

// 加载车位数据
const loadParkingSpaces = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockData()
    parkingSpaceList.value = mockData.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    )
    total.value = mockData.length
    loading.value = false
  }, 500)
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
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增车位'
  Object.assign(form, {
    spaceNo: '',
    location: '',
    monthlyRent: 0,
    status: '1',
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
  ).then(() => {
    ElMessage.success('删除成功')
    loadParkingSpaces()
  })
}

// 查看租赁记录
const handleViewRentals = (row) => {
  ElMessage.info(`查看车位 ${row.spaceNo} 的租赁记录`)
}

// 批量导入
const handleImport = () => {
  ElMessage.info('批量导入功能开发中')
}

// 导出数据
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadParkingSpaces()
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