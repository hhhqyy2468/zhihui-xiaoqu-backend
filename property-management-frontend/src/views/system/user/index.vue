<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
        class="search-form"
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

        <el-form-item label="状态" prop="status">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'system:user:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
            <el-button
              type="danger"
              v-permission="'system:user:delete'"
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
        </div>
      </template>

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
        <!-- 状态列 -->
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>

        <!-- 用户类型列 -->
        <template #userType="{ row }">
          <el-tag :type="getUserTypeTag(row.userType)">
            {{ getUserTypeName(row.userType) }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'system:user:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="info"
            v-permission="'system:user:resetPassword'"
            @click="handleResetPassword(row)"
          >
            重置密码
          </el-button>
          <el-button
            link
            type="warning"
            v-permission="'system:user:assignRole'"
            @click="handleAssignRole(row)"
          >
            分配角色
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'system:user:delete'"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
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

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      @close="handleRoleDialogClose"
    >
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色列表">
          <el-checkbox-group v-model="roleForm.roleIds">
            <el-checkbox
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.id"
            >
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="roleSubmitLoading"
            @click="handleRoleSubmit"
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
const roleSubmitLoading = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  phone: '',
  userType: '',
  status: ''
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
    width: '120'
  },
  {
    prop: 'phone',
    label: '手机号',
    width: '130'
  },
  {
    prop: 'email',
    label: '邮箱',
    width: '200',
    showOverflowTooltip: true
  },
  {
    prop: 'userType',
    label: '用户类型',
    width: '120',
    slot: 'userType'
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    slot: 'status'
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

// 用户类型选项
const userTypeOptions = [
  { label: '系统管理员', value: USER_TYPES.ADMIN },
  { label: '物业管理员', value: USER_TYPES.MANAGER },
  { label: '业主', value: USER_TYPES.OWNER },
  { label: '维修人员', value: USER_TYPES.WORKER }
]

// 表单数据
const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  userType: '',
  status: 1
})

// 表单规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名',
    disabled: isEdit.value
  },
  {
    prop: 'password',
    label: '密码',
    type: 'input',
    inputType: 'password',
    placeholder: isEdit.value ? '不修改请留空' : '请输入密码',
    hidden: isEdit.value
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
    prop: 'email',
    label: '邮箱',
    type: 'input',
    placeholder: '请输入邮箱'
  },
  {
    prop: 'userType',
    label: '用户类型',
    type: 'select',
    options: userTypeOptions,
    placeholder: '请选择用户类型'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'radio',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  }
])

// 角色表单
const roleForm = reactive({
  id: null,
  username: '',
  roleIds: []
})

// 角色选项
const roleOptions = ref([
  { id: 1, roleName: '系统管理员' },
  { id: 2, roleName: '物业管理员' },
  { id: 3, roleName: '业主' },
  { id: 4, roleName: '维修人员' }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

// 获取用户类型名称
const getUserTypeName = (userType) => {
  const option = userTypeOptions.find(item => item.value === userType)
  return option ? option.label : '未知'
}

// 获取用户类型标签
const getUserTypeTag = (userType) => {
  const tagMap = {
    [USER_TYPES.ADMIN]: 'danger',
    [USER_TYPES.MANAGER]: 'warning',
    [USER_TYPES.OWNER]: 'success',
    [USER_TYPES.WORKER]: 'info'
  }
  return tagMap[userType] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  const mockUsers = [
    {
      id: 1,
      username: 'admin',
      realName: '系统管理员',
      phone: '13800138000',
      email: 'admin@example.com',
      userType: USER_TYPES.ADMIN,
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      id: 2,
      username: 'manager',
      realName: '物业经理',
      phone: '13800138001',
      email: 'manager@example.com',
      userType: USER_TYPES.MANAGER,
      status: 1,
      createTime: '2024-01-02 10:00:00'
    },
    {
      id: 3,
      username: 'owner',
      realName: '张三',
      phone: '13800138002',
      email: 'owner@example.com',
      userType: USER_TYPES.OWNER,
      status: 1,
      createTime: '2024-01-03 10:00:00'
    },
    {
      id: 4,
      username: 'worker',
      realName: '维修师傅',
      phone: '13800138003',
      email: 'worker@example.com',
      userType: USER_TYPES.WORKER,
      status: 1,
      createTime: '2024-01-04 10:00:00'
    }
  ]

  // 模拟分页
  pagination.total = mockUsers.length
  return mockUsers
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
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${row.realName}"吗？`,
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
      `确定要删除选中的${selectedRows.value.length}个用户吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户"${row.realName}"的密码吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch (error) {
    // 用户取消操作
  }
}

// 分配角色
const handleAssignRole = (row) => {
  roleForm.id = row.id
  roleForm.username = row.username
  roleForm.roleIds = [1, 2] // 模拟已分配的角色
  roleDialogVisible.value = true
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

// 提交角色分配
const handleRoleSubmit = async () => {
  roleSubmitLoading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('角色分配成功')
      roleDialogVisible.value = false
      roleSubmitLoading.value = false
    }, 1000)
  } catch (error) {
    roleSubmitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    userType: '',
    status: 1
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 角色对话框关闭
const handleRoleDialogClose = () => {
  Object.assign(roleForm, {
    id: null,
    username: '',
    roleIds: []
  })
}

// 组件挂载
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
}
</style>