<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>系统管理</el-breadcrumb-item>
        <el-breadcrumb-item>角色管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input
            v-model="searchForm.roleKey"
            placeholder="请输入角色标识"
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
        新增角色
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <!-- 角色表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
      >
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
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right">
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
              type="warning"
              @click="handleAssignPermission(row)"
            >
              权限分配
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

    <!-- 新增/编辑角色对话框 -->
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限分配"
      width="800px"
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
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Download } from '@element-plus/icons-vue'
import * as systemApi from '@/api/system'

// 响应式数据
const formRef = ref()
const permissionTreeRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)

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
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 加载角色数据
const loadRoles = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }

    // 清理空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await systemApi.listRoles(params)
    if (response.code === 200) {
      tableData.value = response.data.rows || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取角色列表失败')
    }
  } catch (error) {
    console.error('加载角色数据失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadRoles()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    roleName: '',
    roleKey: '',
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    roleId: null,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  try {
    const response = await systemApi.getRole(row.roleId)
    if (response.code === 200) {
      isEdit.value = true
      Object.assign(form, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取角色信息失败')
    }
  } catch (error) {
    console.error('获取角色信息失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.roleName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await systemApi.delRole(row.roleId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadRoles()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 权限分配
const handleAssignPermission = async (row) => {
  try {
    permissionForm.roleId = row.roleId
    permissionForm.roleName = row.roleName
    permissionForm.permissionIds = [] // 初始化为空

    // 获取角色菜单列表
    const response = await systemApi.getRoleMenus(row.roleId)
    if (response.code === 200) {
      // 转换菜单数据为权限树格式
      permissionTreeData.value = convertMenusToPermissionTree(response.data || [])
      permissionDialogVisible.value = true
    } else {
      ElMessage.error('获取菜单数据失败')
    }
  } catch (error) {
    console.error('获取菜单数据失败:', error)
    ElMessage.error('获取菜单数据失败')
  }
}

// 转换菜单数据为权限树格式
const convertMenusToPermissionTree = (menus) => {
  return menus.map(menu => ({
    id: menu.menuId,
    label: menu.menuName,
    children: menu.children ? convertMenusToPermissionTree(menu.children) : []
  }))
}

// 导出Excel（纯前端方案）
const handleExport = async () => {
  try {
    const loadingInstance = ElMessage({
      message: '正在导出数据...',
      type: 'info',
      duration: 0
    })

    // 获取所有数据
    const allParams = {
      pageNum: 1,
      pageSize: 10000,
      ...searchForm
    }

    // 清理空值参数
    Object.keys(allParams).forEach(key => {
      if (allParams[key] === '' || allParams[key] === null || allParams[key] === undefined) {
        delete allParams[key]
      }
    })

    const response = await systemApi.listRoles(allParams)
    loadingInstance.close()

    if (response.code === 200 && response.data.rows) {
      const roles = response.data.rows
      if (roles.length === 0) {
        ElMessage.warning('没有数据可导出')
        return
      }

      // 导入xlsx库
      const XLSX = await import('xlsx')
      const { saveAs } = await import('file-saver')

      // 准备导出数据
      const exportData = roles.map(role => ({
        '角色ID': role.roleId,
        '角色名称': role.roleName,
        '角色标识': role.roleKey,
        '显示顺序': role.roleSort || 0,
        '状态': role.status === 1 ? '启用' : '禁用',
        '备注': role.remark || '',
        '创建时间': role.createTime || '',
        '更新时间': role.updateTime || ''
      }))

      // 创建工作簿
      const wb = XLSX.utils.book_new()
      const ws = XLSX.utils.json_to_sheet(exportData)

      // 设置列宽
      const colWidths = [
        { wch: 10 }, // 角色ID
        { wch: 15 }, // 角色名称
        { wch: 15 }, // 角色标识
        { wch: 12 }, // 显示顺序
        { wch: 10 }, // 状态
        { wch: 30 }, // 备注
        { wch: 20 }, // 创建时间
        { wch: 20 }  // 更新时间
      ]
      ws['!cols'] = colWidths

      // 添加工作表
      XLSX.utils.book_append_sheet(wb, ws, '角色数据')

      // 导出文件
      const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
      const blob = new Blob([excelBuffer], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      })

      // 生成文件名
      const now = new Date()
      const timestamp = now.getFullYear() +
        String(now.getMonth() + 1).padStart(2, '0') +
        String(now.getDate()).padStart(2, '0') + '_' +
        String(now.getHours()).padStart(2, '0') +
        String(now.getMinutes()).padStart(2, '0')

      saveAs(blob, `角色数据_${timestamp}.xlsx`)
      ElMessage.success(`成功导出 ${roles.length} 条角色数据`)
    } else {
      ElMessage.error('获取角色数据失败')
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
    await formRef.value.validate()

    let response
    if (isEdit.value) {
      response = await systemApi.updateRole(form)
    } else {
      response = await systemApi.addRole(form)
    }

    if (response.code === 200) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadRoles()
    } else {
      ElMessage.error(response.msg || dialogTitle.value + '失败')
    }
  } catch (error) {
    if (error !== false) { // 表单验证失败时error为false
      console.error('提交失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 提交权限分配
const handlePermissionSubmit = async () => {
  try {
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

    const response = await systemApi.assignRoleMenus(permissionForm.roleId, allCheckedKeys)
    if (response.code === 200) {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    } else {
      ElMessage.error(response.msg || '权限分配失败')
    }
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败，请稍后重试')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadRoles()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadRoles()
}

// 初始化
onMounted(() => {
  loadRoles()
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

.permission-tree {
  width: 100%;
  height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}
</style>