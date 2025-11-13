<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>系统管理</el-breadcrumb-item>
        <el-breadcrumb-item>用户管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入真实姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="用户类型">
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
        <el-form-item label="状态">
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增用户
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

    <!-- 用户表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" width="120" sortable />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)">
              {{ getUserTypeName(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
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
              @click="handleResetPassword(row)"
            >
              重置密码
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option
              v-for="item in userTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
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
              :value="role.id"
              :label="role.id"
            >
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import * as systemApi from '@/api/system'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

// 响应式数据
const formRef = ref()
const loading = ref(false)
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

// 用户类型选项（根据数据库）
const userTypeOptions = [
  { label: '系统管理员', value: 1 },
  { label: '物业管理员', value: 2 },
  { label: '业主', value: 3 },
  { label: '维修人员', value: 4 }
]

// 表单数据
const form = reactive({
  userId: null,
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取用户类型名称
const getUserTypeName = (userType) => {
  const option = userTypeOptions.find(item => item.value === userType)
  return option ? option.label : '未知'
}

// 获取用户类型标签
const getUserTypeTag = (userType) => {
  const tagMap = {
    1: 'danger',
    2: 'warning',
    3: 'success',
    4: 'info'
  }
  return tagMap[userType] || 'info'
}

// 生成模拟数据
const generateMockData = () => {
  const users = []
  const userNames = ['admin', 'manager01', 'owner001', 'worker_a', 'owner002', 'manager02', 'owner003', 'worker_b']
  const realNames = ['系统管理员', '物业管理员A', '张三', '维修人员A', '李四', '物业管理员B', '王五', '维修人员B']

  for (let i = 0; i < userNames.length; i++) {
    const userType = i < 2 ? (i === 0 ? 1 : 2) : (i < 6 ? 3 : 4)
    users.push({
      id: i + 1,
      username: userNames[i],
      realName: realNames[i],
      phone: `138${String(Math.floor(Math.random() * 100000000)).padStart(8, '0')}`,
      email: `${userNames[i]}@example.com`,
      userType: userType,
      status: 1,
      createTime: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString()
    })
  }

  return users
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 加载用户数据
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      username: searchForm.username || undefined,
      realName: searchForm.realName || undefined,
      phone: searchForm.phone || undefined,
      userType: searchForm.userType || undefined
    }
    const response = await systemApi.listUsers(params)
    if (response.code === 200) {
      tableData.value = response.data.rows || []
      pagination.total = response.data.total || 0
    } else if (response.total !== undefined && response.rows !== undefined) {
      // 兼容Mock后端的响应格式
      tableData.value = response.rows || []
      pagination.total = response.total || 0
    } else {
      ElMessage.error(response.msg || '加载用户数据失败')
    }
  } catch (error) {
    console.error('加载用户数据失败:', error)
    ElMessage.error('网络错误，加载用户数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    realName: '',
    phone: '',
    userType: '',
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    userId: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    userType: '',
    status: 1
  })
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
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await systemApi.delUser(row.userId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个用户吗？`,
      '批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const userIds = selectedRows.value.map(row => row.userId).join(',')
    const response = await systemApi.delUser(userIds)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.msg || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除用户失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户"${row.realName}"的密码吗？`,
      '重置密码',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await systemApi.resetUserPwd(row.userId, '123456')
    if (response.code === 200) {
      ElMessage.success('密码重置成功，新密码为：123456')
    } else {
      ElMessage.error(response.msg || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }
}

// 分配角色
const handleAssignRole = async (row) => {
  // 确保roleForm.roleIds是数组
  roleForm.roleIds = []
  roleForm.id = row.userId
  roleForm.username = row.username

  try {
    // 加载用户已有的角色
    const response = await systemApi.getUserRoles(row.userId)
    console.log('加载用户角色响应:', response)
    if (response.code === 200 && response.data) {
      // 确保数据是数组格式
      if (Array.isArray(response.data)) {
        roleForm.roleIds = response.data
      } else if (typeof response.data === 'object') {
        roleForm.roleIds = Object.values(response.data)
      } else {
        roleForm.roleIds = []
      }
      console.log('设置角色ID:', roleForm.roleIds)
    } else {
      roleForm.roleIds = []
    }
  } catch (error) {
    console.error('加载用户角色失败:', error)
    roleForm.roleIds = []
  }

  // 延迟打开对话框，确保数据已设置
  await new Promise(resolve => setTimeout(resolve, 100))
  roleDialogVisible.value = true
}

// 导出Excel（纯前端方案）
const handleExport = async () => {
  try {
    // 显示加载状态
    const loadingInstance = ElMessage({
      message: '正在导出数据...',
      type: 'info',
      duration: 0 // 不自动关闭
    })

    // 获取当前表格中的所有数据（不分页）
    const allParams = {
      pageNum: 1,
      pageSize: 10000 // 获取大量数据用于导出
    }

    // 调用API获取所有用户数据
    const response = await systemApi.listUsers(allParams)

    // 关闭加载提示
    loadingInstance.close()

    if (response.code === 200 && response.data.rows) {
      const users = response.data.rows

      if (users.length === 0) {
        ElMessage.warning('没有数据可导出')
        return
      }

      // 转换数据格式
      const exportData = users.map(user => ({
        '用户ID': user.userId,
        '用户名': user.username,
        '真实姓名': user.realName,
        '手机号': user.phone || '',
        '邮箱': user.email || '',
        '用户类型': getUserTypeName(user.userType),
        '状态': user.status === 1 ? '启用' : '禁用',
        '创建时间': user.createTime || '',
        '更新时间': user.updateTime || ''
      }))

      // 创建工作簿
      const wb = XLSX.utils.book_new()
      const ws = XLSX.utils.json_to_sheet(exportData)

      // 设置列宽
      const colWidths = [
        { wch: 10 }, // 用户ID
        { wch: 15 }, // 用户名
        { wch: 15 }, // 真实姓名
        { wch: 15 }, // 手机号
        { wch: 25 }, // 邮箱
        { wch: 12 }, // 用户类型
        { wch: 8 },  // 状态
        { wch: 20 }, // 创建时间
        { wch: 20 }  // 更新时间
      ]
      ws['!cols'] = colWidths

      // 将工作表添加到工作簿
      XLSX.utils.book_append_sheet(wb, ws, '用户数据')

      // 生成Excel文件并下载
      const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
      const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })

      // 生成文件名（包含时间戳）
      const now = new Date()
      const timestamp = now.getFullYear() +
                      String(now.getMonth() + 1).padStart(2, '0') +
                      String(now.getDate()).padStart(2, '0') + '_' +
                      String(now.getHours()).padStart(2, '0') +
                      String(now.getMinutes()).padStart(2, '0')

      saveAs(blob, `用户数据_${timestamp}.xlsx`)
      ElMessage.success(`成功导出 ${users.length} 条用户数据`)
    } else {
      ElMessage.error('获取用户数据失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败：' + (error.message || '网络错误'))
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    let response
    if (isEdit.value) {
      // 编辑用户
      response = await systemApi.updateUser(form)
    } else {
      // 新增用户
      response = await systemApi.addUser(form)
    }

    if (response.code === 200) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(response.msg || dialogTitle.value + '失败')
    }
  } catch (error) {
    console.error(isEdit.value ? '更新用户失败:' : '新增用户失败:', error)
    ElMessage.error(isEdit.value ? '更新用户失败' : '新增用户失败')
  }
}

// 提交角色分配
const handleRoleSubmit = async () => {
  try {
    // 构建要提交的数据
    const data = {
      operation: 'assignRoles',
      data: {
        userId: roleForm.id
      },
      roleIds: roleForm.roleIds
    }

    console.log('分配角色数据:', data)

    // 调用统一操作接口
    const response = await systemApi.userOperation(data)

    if (response.code === 200) {
      ElMessage.success('角色分配成功')
      roleDialogVisible.value = false
      loadUsers() // 重新加载用户数据以更新显示
    } else {
      ElMessage.error(response.msg || '角色分配失败')
    }
  } catch (error) {
    console.error('角色分配失败:', error)
    ElMessage.error('角色分配失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUsers()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadUsers()
}

// 初始化
onMounted(() => {
  loadUsers()
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