<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>系统管理</el-breadcrumb-item>
        <el-breadcrumb-item>角色管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
      <el-button type="warning" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出Excel
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
        class="search-form"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="角色标识" prop="roleKey">
          <el-input
            v-model="searchForm.roleKey"
            placeholder="请输入角色标识"
            clearable
            style="width: 200px"
          />
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
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleId" label="角色编号" width="100" />
        <el-table-column prop="roleName" label="角色名称" width="150" sortable />
        <el-table-column prop="roleKey" label="角色标识" width="150" />
        <el-table-column prop="roleSort" label="显示顺序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              v-permission="'system:role:edit'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              v-permission="'system:role:assignPermission'"
              @click="handleAssignPermission(row)"
            >
              权限分配
            </el-button>
            <el-button
              link
              type="danger"
              v-permission="'system:role:delete'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="form.roleName"
            placeholder="请输入角色名称"
          />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input
            v-model="form.roleKey"
            placeholder="请输入角色标识"
          />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number
            v-model="form.roleSort"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>

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

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限分配"
      width="800px"
      @close="handlePermissionDialogClose"
    >
      <el-form :model="permissionForm" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="permissionForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限列表">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTreeData"
            :props="treeProps"
            show-checkbox
            node-key="id"
            :default-checked-keys="permissionForm.permissionIds"
            :default-expanded-keys="[1, 2, 3]"
            class="permission-tree"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="permissionSubmitLoading"
            @click="handlePermissionSubmit"
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
import { Search, Refresh, Plus, Download } from '@element-plus/icons-vue'

// 响应式数据
const searchFormRef = ref()
const formRef = ref()
const permissionTreeRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const permissionSubmitLoading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const selectedRows = ref([])

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleKey: '',
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


// 表单数据
const form = reactive({
  roleId: null,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: 1,
  remark: ''
})

// 表单规则
const formRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在2到20个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '角色标识只能包含字母、数字和下划线，且以字母或下划线开头', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' },
    { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }
  ]
}


// 权限表单
const permissionForm = reactive({
  roleId: null,
  roleName: '',
  permissionIds: []
})

// 权限树数据
const permissionTreeData = ref([
  {
    id: 1,
    label: '系统管理',
    children: [
      {
        id: 11,
        label: '用户管理',
        children: [
          { id: 111, label: '用户查看' },
          { id: 112, label: '用户新增' },
          { id: 113, label: '用户编辑' },
          { id: 114, label: '用户删除' }
        ]
      },
      {
        id: 12,
        label: '角色管理',
        children: [
          { id: 121, label: '角色查看' },
          { id: 122, label: '角色新增' },
          { id: 123, label: '角色编辑' },
          { id: 124, label: '角色删除' }
        ]
      }
    ]
  },
  {
    id: 2,
    label: '物业管理',
    children: [
      {
        id: 21,
        label: '楼栋管理',
        children: [
          { id: 211, label: '楼栋查看' },
          { id: 212, label: '楼栋新增' },
          { id: 213, label: '楼栋编辑' },
          { id: 214, label: '楼栋删除' }
        ]
      },
      {
        id: 22,
        label: '房产管理',
        children: [
          { id: 221, label: '房产查看' },
          { id: 222, label: '房产新增' },
          { id: 223, label: '房产编辑' },
          { id: 224, label: '房产删除' }
        ]
      }
    ]
  },
  {
    id: 3,
    label: '业主门户',
    children: [
      { id: 31, label: '我的账单' },
      { id: 32, label: '我的钱包' },
      { id: 33, label: '我的投诉' }
    ]
  }
])

// 树形控件配置
const treeProps = {
  children: 'children',
  label: 'label'
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

// 获取模拟数据
const getMockData = () => {
  const mockRoles = [
    {
      roleId: 1,
      roleName: '系统管理员',
      roleKey: 'admin',
      roleSort: 1,
      status: 1,
      createTime: '2024-01-01 10:00:00',
      remark: '系统管理员，拥有所有权限'
    },
    {
      roleId: 2,
      roleName: '物业管理员',
      roleKey: 'property_manager',
      roleSort: 2,
      status: 1,
      createTime: '2024-01-02 10:00:00',
      remark: '物业管理员，负责物业管理相关工作'
    },
    {
      roleId: 3,
      roleName: '业主',
      roleKey: 'owner',
      roleSort: 3,
      status: 1,
      createTime: '2024-01-03 10:00:00',
      remark: '业主，可查看和操作自己的相关信息'
    },
    {
      roleId: 4,
      roleName: '维修人员',
      roleKey: 'worker',
      roleSort: 4,
      status: 1,
      createTime: '2024-01-04 10:00:00',
      remark: '维修人员，负责处理维修工单'
    }
  ]

  // 模拟分页
  pagination.total = mockRoles.length
  return mockRoles
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

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.roleName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 权限分配
const handleAssignPermission = (row) => {
  permissionForm.roleId = row.roleId
  permissionForm.roleName = row.roleName
  permissionForm.permissionIds = [111, 112, 113, 114, 211, 212] // 模拟已分配的权限
  permissionDialogVisible.value = true
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个角色吗？`,
      '批量删除',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchData()
  } catch (error) {
    // 用户取消操作
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

// 提交权限分配
const handlePermissionSubmit = async () => {
  const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

  permissionSubmitLoading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
      permissionSubmitLoading.value = false
    }, 1000)
  } catch (error) {
    permissionSubmitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    roleId: null,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: 1,
    remark: ''
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  Object.assign(permissionForm, {
    roleId: null,
    roleName: '',
    permissionIds: []
  })
}

// 组件挂载
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    .page-title {
      margin: 0 0 16px 0;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }

  .action-section {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }

  .search-section {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
  }

  .table-section {
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
    padding: 20px;
  }

  .permission-tree {
    width: 100%;
    height: 400px;
    overflow-y: auto;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
  }
}
</style>