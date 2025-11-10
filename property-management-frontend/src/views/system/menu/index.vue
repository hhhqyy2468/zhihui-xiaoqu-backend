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
        <el-form-item label="菜单名称" prop="menuName">
          <el-input
            v-model="searchForm.menuName"
            placeholder="请输入菜单名称"
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
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>菜单列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'system:menu:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增菜单
            </el-button>
            <el-button @click="handleExpandAll">
              <el-icon><Sort /></el-icon>
              展开/折叠
            </el-button>
          </div>
        </div>
      </template>

      <Table
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :pagination="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
      >
        <!-- 菜单名称列 -->
        <template #menuName="{ row }">
          <div class="menu-name-cell">
            <el-icon v-if="row.icon" class="menu-icon">
              <component :is="row.icon" />
            </el-icon>
            <span>{{ row.menuName }}</span>
          </div>
        </template>

        <!-- 菜单类型列 -->
        <template #menuType="{ row }">
          <el-tag :type="getMenuTypeTag(row.menuType)">
            {{ getMenuTypeName(row.menuType) }}
          </el-tag>
        </template>

        <!-- 状态列 -->
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'system:menu:add'"
            @click="handleAdd(row)"
          >
            新增
          </el-button>
          <el-button
            link
            type="primary"
            v-permission="'system:menu:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'system:menu:delete'"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Sort } from '@element-plus/icons-vue'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isExpandAll = ref(false)
const isEdit = ref(false)
const parentMenu = ref(null)

// 搜索表单
const searchForm = reactive({
  menuName: '',
  status: ''
})

// 表格数据
const tableData = ref([])

// 表格列配置
const tableColumns = [
  {
    prop: 'menuName',
    label: '菜单名称',
    width: '250',
    slot: 'menuName',
    sortable: true
  },
  {
    prop: 'menuType',
    label: '菜单类型',
    width: '120',
    slot: 'menuType'
  },
  {
    prop: 'orderNum',
    label: '排序',
    width: '80',
    sortable: true
  },
  {
    prop: 'perms',
    label: '权限标识',
    showOverflowTooltip: true
  },
  {
    prop: 'component',
    label: '组件路径',
    showOverflowTooltip: true
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
    width: '200',
    slot: 'operation',
    fixed: 'right'
  }
]

// 表单数据
const form = reactive({
  menuId: null,
  parentId: 0,
  menuName: '',
  menuType: 'M',
  orderNum: 0,
  path: '',
  component: '',
  menuName: '',
  perms: '',
  icon: '',
  status: 1,
  visible: 1,
  isFresh: 1,
  remark: ''
})

// 表单规则
const formRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '菜单名称长度在2到50个字符', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入排序', trigger: 'blur' },
    { type: 'number', message: '排序必须为数字', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ]
}

// 菜单类型选项
const menuTypeOptions = [
  { label: '目录', value: 'M' },
  { label: '菜单', value: 'C' },
  { label: '按钮', value: 'F' }
]

// 显示状态选项
const visibleOptions = [
  { label: '显示', value: 1 },
  { label: '隐藏', value: 0 }
]

// 是否刷新选项
const freshOptions = [
  { label: '刷新', value: 1 },
  { label: '不刷新', value: 0 }
]

// 父级菜单选项
const parentMenuOptions = ref([
  { label: '主类目', value: 0 },
  { label: '系统管理', value: 1 },
  { label: '物业管理', value: 2 },
  { label: '业主门户', value: 3 }
])

// 图标选项
const iconOptions = ref([
  { label: 'House', value: 'House' },
  { label: 'Setting', value: 'Setting' },
  { label: 'User', value: 'User' },
  { label: 'OfficeBuilding', value: 'OfficeBuilding' },
  { label: 'Document', value: 'Document' },
  { label: 'Money', value: 'Money' },
  { label: 'Tools', value: 'Tools' },
  { label: 'Message', value: 'Message' },
  { label: 'Car', value: 'Car' },
  { label: 'Bell', value: 'Bell' }
])

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'parentId',
    label: '上级菜单',
    type: 'select',
    options: parentMenuOptions.value,
    placeholder: '请选择上级菜单'
  },
  {
    prop: 'menuType',
    label: '菜单类型',
    type: 'radio',
    options: menuTypeOptions,
    placeholder: '请选择菜单类型'
  },
  {
    prop: 'menuName',
    label: '菜单名称',
    type: 'input',
    placeholder: '请输入菜单名称'
  },
  {
    prop: 'orderNum',
    label: '显示顺序',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入显示顺序'
  },
  {
    prop: 'path',
    label: '路由地址',
    type: 'input',
    placeholder: '请输入路由地址',
    hidden: form.menuType === 'F'
  },
  {
    prop: 'component',
    label: '组件路径',
    type: 'input',
    placeholder: '请输入组件路径',
    hidden: form.menuType === 'M' || form.menuType === 'F'
  },
  {
    prop: 'perms',
    label: '权限标识',
    type: 'input',
    placeholder: '请输入权限标识',
    hidden: form.menuType === 'M'
  },
  {
    prop: 'icon',
    label: '菜单图标',
    type: 'select',
    options: iconOptions.value,
    placeholder: '请选择菜单图标',
    hidden: form.menuType === 'F'
  },
  {
    prop: 'status',
    label: '菜单状态',
    type: 'radio',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  },
  {
    prop: 'visible',
    label: '显示状态',
    type: 'radio',
    options: visibleOptions,
    hidden: form.menuType === 'F'
  },
  {
    prop: 'isFresh',
    label: '是否刷新',
    type: 'radio',
    options: freshOptions,
    hidden: form.menuType === 'M' || form.menuType === 'F'
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息'
  }
])

// 计算属性
const dialogTitle = computed(() => {
  if (parentMenu.value) {
    return `新增菜单 - ${parentMenu.value.menuName}`
  }
  return isEdit.value ? '编辑菜单' : '新增菜单'
})

// 获取菜单类型名称
const getMenuTypeName = (menuType) => {
  const option = menuTypeOptions.find(item => item.value === menuType)
  return option ? option.label : '未知'
}

// 获取菜单类型标签
const getMenuTypeTag = (menuType) => {
  const tagMap = {
    'M': 'primary',
    'C': 'success',
    'F': 'warning'
  }
  return tagMap[menuType] || 'info'
}

// 获取模拟数据
const getMockData = () => {
  return [
    {
      menuId: 1,
      menuName: '首页',
      parentId: 0,
      orderNum: 1,
      path: '/dashboard',
      component: 'dashboard',
      menuType: 'C',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'House',
      createTime: '2024-01-01 10:00:00',
      children: []
    },
    {
      menuId: 2,
      menuName: '系统管理',
      parentId: 0,
      orderNum: 2,
      path: '/system',
      component: '',
      menuType: 'M',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'Setting',
      createTime: '2024-01-01 10:00:00',
      children: [
        {
          menuId: 21,
          menuName: '用户管理',
          parentId: 2,
          orderNum: 1,
          path: 'user',
          component: 'system/user/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'system:user:view',
          icon: 'User',
          createTime: '2024-01-01 10:00:00'
        },
        {
          menuId: 22,
          menuName: '角色管理',
          parentId: 2,
          orderNum: 2,
          path: 'role',
          component: 'system/role/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'system:role:view',
          icon: 'Tools',
          createTime: '2024-01-01 10:00:00'
        },
        {
          menuId: 23,
          menuName: '菜单管理',
          parentId: 2,
          orderNum: 3,
          path: 'menu',
          component: 'system/menu/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'system:menu:view',
          icon: 'Document',
          createTime: '2024-01-01 10:00:00'
        }
      ]
    },
    {
      menuId: 3,
      menuName: '物业管理',
      parentId: 0,
      orderNum: 3,
      path: '/property',
      component: '',
      menuType: 'M',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'OfficeBuilding',
      createTime: '2024-01-01 10:00:00',
      children: [
        {
          menuId: 31,
          menuName: '楼栋管理',
          parentId: 3,
          orderNum: 1,
          path: 'building',
          component: 'property/building/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'property:building:view',
          icon: 'OfficeBuilding',
          createTime: '2024-01-01 10:00:00'
        },
        {
          menuId: 32,
          menuName: '单元管理',
          parentId: 3,
          orderNum: 2,
          path: 'unit',
          component: 'property/unit/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'property:unit:view',
          icon: 'OfficeBuilding',
          createTime: '2024-01-01 10:00:00'
        }
      ]
    }
  ]
}

// 搜索
const handleSearch = () => {
  fetchData()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  handleSearch()
}

// 展开/折叠
const handleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  tableRef.value?.expandAllRows(isExpandAll.value)
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

// 新增
const handleAdd = (row) => {
  isEdit.value = false
  parentMenu.value = row || null
  resetForm()
  if (row) {
    form.parentId = row.menuId
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  parentMenu.value = null
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单"${row.menuName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
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
    menuId: null,
    parentId: 0,
    menuName: '',
    menuType: 'M',
    orderNum: 0,
    path: '',
    component: '',
    perms: '',
    icon: '',
    status: 1,
    visible: 1,
    isFresh: 1,
    remark: ''
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
  parentMenu.value = null
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

  .menu-name-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .menu-icon {
      font-size: 16px;
      color: #409eff;
    }
  }
}
</style>