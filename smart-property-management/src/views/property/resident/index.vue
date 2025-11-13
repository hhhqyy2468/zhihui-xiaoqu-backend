<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">住户管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>住户管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入真实姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="用户类型" prop="userType">
          <el-select
            v-model="searchForm.userType"
            placeholder="请选择用户类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in userTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="住户类型" prop="residentType">
          <el-select
            v-model="searchForm.residentType"
            placeholder="请选择住户类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in residentTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="住户状态" prop="residentStatus">
          <el-select
            v-model="searchForm.residentStatus"
            placeholder="请选择住户状态"
            clearable
            style="width: 120px"
          >
            <el-option label="在住" :value="1" />
            <el-option label="已搬离" :value="0" />
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
        新增住户
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <!-- 住户表格 -->
    <div class="table-section">

      <Table
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @page-change="handlePageChange"
        @sort-change="handleSortChange"
      >
        <!-- 用户类型列 -->
        <template #userType="{ row }">
          <el-tag :type="getUserTypeTag(row.userType)">
            {{ getUserTypeName(row.userType) }}
          </el-tag>
        </template>

        <!-- 住户类型列 -->
        <template #residentType="{ row }">
          <el-tag :type="getResidentTypeTag(row.residentType)">
            {{ getResidentTypeName(row.residentType) }}
          </el-tag>
        </template>

        <!-- 住户状态列 -->
        <template #residentStatus="{ row }">
          <el-tag :type="row.residentStatus === 1 ? 'success' : 'danger'">
            {{ row.residentStatus === 1 ? '在住' : '已搬离' }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'property:resident:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="info"
            @click="handleViewHouses(row)"
          >
            查看房产
          </el-button>
          <el-button
            link
            type="warning"
            @click="handleChangeStatus(row)"
          >
            变更状态
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'property:resident:delete'"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </Table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
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
      width="700px"
      @close="handleDialogClose"
    >
      <Form
        ref="formRef"
        :model="form"
        :rules="formRules"
        :items="formItems"
        label-width="100px"
      />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看房产对话框 -->
    <el-dialog
      v-model="houseDialogVisible"
      title="房产信息"
      width="800px"
    >
      <el-table :data="houseData" border max-height="400">
        <el-table-column prop="houseCode" label="房产编号" width="140" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="unitName" label="单元" width="100" />
        <el-table-column prop="roomNum" label="门牌号" width="100" />
        <el-table-column prop="houseType" label="户型" width="100" />
        <el-table-column prop="buildArea" label="建筑面积" width="120">
          <template #default="{ row }">
            {{ row.buildArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="houseStatus" label="房产状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHouseStatusTag(row.houseStatus)">
              {{ getHouseStatusName(row.houseStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="入住时间" width="180" />
        <el-table-column prop="relationType" label="关系" width="100">
          <template #default="{ row }">
            <el-tag :type="row.relationType === 1 ? 'success' : 'info'">
              {{ row.relationType === 1 ? '产权人' : '家庭成员' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="houseDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 变更状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="变更住户状态"
      width="400px"
    >
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="当前住户">
          <span>{{ statusForm.realName }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="statusForm.residentStatus === 1 ? 'success' : 'danger'">
            {{ statusForm.residentStatus === 1 ? '在住' : '已搬离' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-radio-group v-model="statusForm.newStatus">
            <el-radio :label="1">在住</el-radio>
            <el-radio :label="0">已搬离</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="变更原因" v-if="statusForm.newStatus === 0">
          <el-input
            v-model="statusForm.reason"
            type="textarea"
            placeholder="请输入搬离原因"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="statusSubmitLoading"
            @click="handleStatusSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'
import { USER_TYPES } from '@/utils/permission'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const statusSubmitLoading = ref(false)
const dialogVisible = ref(false)
const houseDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const houseData = ref([])

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  phone: '',
  userType: '',
  residentType: '',
  residentStatus: ''
})

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列配置
const tableColumns = [
  {
    type: 'selection',
    width: '55'
  },
  {
    prop: 'username',
    label: '用户名',
    width: '120',
    sortable: true
  },
  {
    prop: 'realName',
    label: '真实姓名',
    width: '120',
    sortable: true
  },
  {
    prop: 'phone',
    label: '手机号',
    width: '130'
  },
  {
    prop: 'idCard',
    label: '身份证号',
    width: '180',
    formatter: (row) => row.idCard ? row.idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2') : ''
  },
  {
    prop: 'userType',
    label: '用户类型',
    width: '120',
    slot: 'userType'
  },
  {
    prop: 'residentType',
    label: '住户类型',
    width: '120',
    slot: 'residentType'
  },
  {
    prop: 'houseCount',
    label: '房产数量',
    width: '100'
  },
  {
    prop: 'residentStatus',
    label: '住户状态',
    width: '100',
    slot: 'residentStatus'
  },
  {
    prop: 'checkInTime',
    label: '入住时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'operation',
    label: '操作',
    width: '280',
    slot: 'operation',
    fixed: 'right'
  }
]

// 选项数据
const userTypeOptions = [
  { label: '业主', value: USER_TYPES.OWNER },
  { label: '租户', value: 5 },
  { label: '家庭成员', value: 6 }
]

const residentTypeOptions = [
  { label: '产权人', value: 1 },
  { label: '租户', value: 2 },
  { label: '家庭成员', value: 3 }
]

// 表单数据
const form = reactive({
  residentId: null,
  userType: '',
  residentType: '',
  realName: '',
  phone: '',
  idCard: '',
  houseIds: [],
  checkInTime: '',
  remark: ''
})

// 表单规则
const formRules = {
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  residentType: [
    { required: true, message: '请选择住户类型', trigger: 'change' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  checkInTime: [
    { required: true, message: '请选择入住时间', trigger: 'change' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'userType',
    label: '用户类型',
    type: 'select',
    options: userTypeOptions,
    placeholder: '请选择用户类型'
  },
  {
    prop: 'residentType',
    label: '住户类型',
    type: 'select',
    options: residentTypeOptions,
    placeholder: '请选择住户类型'
  },
  {
    prop: 'realName',
    label: '真实姓名',
    type: 'input',
    placeholder: '请输入真实姓名'
  },
  {
    prop: 'phone',
    label: '手机号',
    type: 'input',
    placeholder: '请输入手机号'
  },
  {
    prop: 'idCard',
    label: '身份证号',
    type: 'input',
    placeholder: '请输入身份证号'
  },
  {
    prop: 'checkInTime',
    label: '入住时间',
    type: 'date',
    placeholder: '请选择入住时间'
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息'
  }
])

// 状态变更表单
const statusForm = reactive({
  residentId: null,
  realName: '',
  residentStatus: 1,
  newStatus: 1,
  reason: ''
})

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑住户' : '新增住户')

// 获取用户类型名称
const getUserTypeName = (userType) => {
  const option = userTypeOptions.find(item => item.value === userType)
  return option ? option.label : '未知'
}

// 获取用户类型标签
const getUserTypeTag = (userType) => {
  const tagMap = {
    [USER_TYPES.OWNER]: 'success',
    5: 'warning',  // 租户
    6: 'info'     // 家庭成员
  }
  return tagMap[userType] || 'info'
}

// 获取住户类型名称
const getResidentTypeName = (residentType) => {
  const option = residentTypeOptions.find(item => item.value === residentType)
  return option ? option.label : '未知'
}

// 获取住户类型标签
const getResidentTypeTag = (residentType) => {
  const tagMap = {
    1: 'success', // 产权人
    2: 'warning', // 租户
    3: 'info'     // 家庭成员
  }
  return tagMap[residentType] || 'info'
}

// 获取房产状态名称
const getHouseStatusName = (status) => {
  const statusMap = {
    1: '空置',
    2: '已售',
    3: '已租',
    4: '自住'
  }
  return statusMap[status] || '未知'
}

// 获取房产状态标签
const getHouseStatusTag = (status) => {
  const tagMap = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'primary'
  }
  return tagMap[status] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockResidents = []
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑一', '冯二']
  const phonePrefixes = ['138', '139', '150', '151', '152', '185', '186', '188']

  for (let i = 0; i < 50; i++) {
    const userType = [USER_TYPES.OWNER, 5, 6][Math.floor(Math.random() * 3)]
    const residentType = [1, 2, 3][Math.floor(Math.random() * 3)]
    const residentStatus = Math.random() > 0.1 ? 1 : 0 // 90%在住

    mockResidents.push({
      residentId: i + 1,
      username: `user${i + 1}`,
      realName: names[i % names.length] + (i > 9 ? i : ''),
      phone: phonePrefixes[Math.floor(Math.random() * phonePrefixes.length)] +
             Math.floor(Math.random() * 100000000).toString().padStart(8, '0'),
      idCard: '11010119' + Math.floor(Math.random() * 100000000).toString().padStart(8, '0'),
      userType: userType,
      residentType: residentType,
      houseCount: Math.floor(Math.random() * 3) + 1,
      residentStatus: residentStatus,
      checkInTime: '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' 10:00:00',
      createTime: '2024-01-01 10:00:00'
    })
  }

  // 模拟分页
  pagination.total = mockResidents.length
  return mockResidents
}

// 获取模拟房产数据
const getMockHouseData = (residentId) => {
  const houses = []
  const houseStatuses = [2, 3, 4] // 已售、已租、自住
  const layouts = ['一室一厅', '两室一厅', '三室两厅']

  for (let i = 0; i < Math.floor(Math.random() * 3) + 1; i++) {
    houses.push({
      houseId: `house_${residentId}_${i}`,
      residentId: residentId,
      houseCode: `H${Math.floor(Math.random() * 4 + 1)}${Math.floor(Math.random() * 18 + 1)}${Math.floor(Math.random() * 3 + 1)}`,
      buildingName: `${Math.floor(Math.random() * 4 + 1)}号楼`,
      unitName: `${Math.floor(Math.random() * 4 + 1)}单元`,
      roomNum: `${Math.floor(Math.random() * 18 + 1)}0${Math.floor(Math.random() * 3 + 1)}`,
      houseType: layouts[Math.floor(Math.random() * layouts.length)],
      buildArea: 80 + Math.floor(Math.random() * 100),
      houseStatus: houseStatuses[Math.floor(Math.random() * houseStatuses.length)],
      checkInTime: '2024-01-' + Math.floor(Math.random() * 28 + 1).toString().padStart(2, '0') + ' 10:00:00',
      relationType: i === 0 ? 1 : 2 // 第一个为产权人，其他为家庭成员
    })
  }

  return houses
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  handleSearch()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      tableData.value = getMockData()
      loading.value = false
    }, 500)
  } catch (error) {
    loading.value = false
    ElMessage.error('获取数据失败')
  }
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page
  fetchData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  pagination.current = val
  fetchData()
}

// 排序变化
const handleSortChange = (sort) => {
  console.log('排序变化:', sort)
  fetchData()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除住户"${row.realName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个住户吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看房产
const handleViewHouses = (row) => {
  houseData.value = getMockHouseData(row.residentId)
  houseDialogVisible.value = true
}

// 变更状态
const handleChangeStatus = (row) => {
  Object.assign(statusForm, {
    residentId: row.residentId,
    realName: row.realName,
    residentStatus: row.residentStatus,
    newStatus: row.residentStatus,
    reason: ''
  })
  statusDialogVisible.value = true
}

// 提交状态变更
const handleStatusSubmit = async () => {
  if (statusForm.newStatus === 0 && !statusForm.reason) {
    ElMessage.warning('搬离时必须填写原因')
    return
  }

  statusSubmitLoading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('状态变更成功')
      statusDialogVisible.value = false
      fetchData()
      statusSubmitLoading.value = false
    }, 1000)
  } catch (error) {
    statusSubmitLoading.value = false
  }
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
      submitLoading.value = false
    }, 1000)
  } catch (error) {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    residentId: null,
    userType: '',
    residentType: '',
    realName: '',
    phone: '',
    idCard: '',
    houseIds: [],
    checkInTime: '',
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
  fetchData()
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