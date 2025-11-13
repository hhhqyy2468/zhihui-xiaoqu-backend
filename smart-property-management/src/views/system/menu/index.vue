<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">菜单管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>系统管理</el-breadcrumb-item>
        <el-breadcrumb-item>菜单管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="菜单名称">
          <el-input
            v-model="searchForm.menuName"
            placeholder="请输入菜单名称"
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
        新增菜单
      </el-button>
      <el-button @click="handleExpandAll">
        <el-icon><Sort /></el-icon>
        展开/折叠
      </el-button>
      <el-button
        type="warning"
        :disabled="selectedRows.length === 0"
        @click="handleBatchStatus"
      >
        <el-icon><Switch /></el-icon>
        批量启用/禁用
      </el-button>
    </div>

    <!-- 菜单表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="menuName" label="菜单名称" width="250" sortable>
          <template #default="{ row }">
            <div class="menu-name-cell">
              <el-icon v-if="row.icon" class="menu-icon">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="菜单类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getMenuTypeTag(row.menuType)">
              {{ getMenuTypeName(row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" sortable />
        <el-table-column prop="path" label="路由地址" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" show-overflow-tooltip />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleAdd(row)"
              v-if="row.menuType !== 'F'"
            >
              新增
            </el-button>
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
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
    </div>

    <!-- 新增/编辑菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级菜单" style="width: 100%" @change="handleParentChange">
            <el-option
              v-for="item in parentMenuOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType" @change="handleMenuTypeChange">
            <el-radio
              v-for="item in menuTypeOptions"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" v-if="form.menuType !== 'F'">
          <el-input v-model="form.path" placeholder="请输入路由地址" @blur="generatePerms" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.menuType === 'C'">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms" v-if="form.menuType !== 'M'">
          <el-input v-model="form.perms" placeholder="请输入权限标识，或自动生成">
            <template #append>
              <el-button @click="generatePerms">自动生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon" v-if="form.menuType !== 'F'">
          <el-select v-model="form.icon" placeholder="请选择菜单图标" style="width: 100%" filterable>
            <el-option
              v-for="item in iconOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <div style="display: flex; align-items: center; gap: 8px;">
                <el-icon><component :is="item.value" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="显示状态" prop="visible" v-if="form.menuType !== 'F'">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Sort, Switch } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const isExpandAll = ref(false)
const isEdit = ref(false)
const parentMenu = ref(null)
const selectedRows = ref([])

// 搜索表单
const searchForm = reactive({
  menuName: '',
  status: ''
})

// 表格数据
const tableData = ref([])

// 菜单类型选项
const menuTypeOptions = [
  { label: '目录', value: 'M' },
  { label: '菜单', value: 'C' },
  { label: '按钮', value: 'F' }
]

// 父级菜单选项
const parentMenuOptions = ref([
  { label: '主类目', value: 0 },
  { label: '系统管理', value: 1 },
  { label: '物业管理', value: 2 },
  { label: '费用管理', value: 3 },
  { label: '服务管理', value: 4 },
  { label: '停车管理', value: 5 }
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

// 表单数据
const form = reactive({
  id: null,
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
  remark: ''
})

// 表单规则
const formRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '菜单名称长度在2到50个字符', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  if (parentMenu.value) {
    return `新增菜单 - ${parentMenu.value.menuName}`
  }
  return isEdit.value ? '编辑菜单' : '新增菜单'
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

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

// 生成模拟数据
const generateMockData = () => {
  return [
    {
      id: 1,
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
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: []
    },
    {
      id: 2,
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
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: [
        {
          id: 21,
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
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 22,
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
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 23,
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
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        }
      ]
    },
    {
      id: 3,
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
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: [
        {
          id: 31,
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
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 32,
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
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 33,
          menuName: '房产管理',
          parentId: 3,
          orderNum: 3,
          path: 'house',
          component: 'property/house/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'property:house:view',
          icon: 'OfficeBuilding',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 34,
          menuName: '公告管理',
          parentId: 3,
          orderNum: 4,
          path: 'notice',
          component: 'property/notice/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'property:notice:view',
          icon: 'Message',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        }
      ]
    },
    {
      id: 4,
      menuName: '费用管理',
      parentId: 0,
      orderNum: 4,
      path: '/fee',
      component: '',
      menuType: 'M',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'Money',
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: [
        {
          id: 41,
          menuName: '费用类型',
          parentId: 4,
          orderNum: 1,
          path: 'feetype',
          component: 'property/feetype/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'fee:type:view',
          icon: 'Money',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 42,
          menuName: '账单管理',
          parentId: 4,
          orderNum: 2,
          path: 'bill',
          component: 'property/bill/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'fee:bill:view',
          icon: 'Money',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        }
      ]
    },
    {
      id: 5,
      menuName: '服务管理',
      parentId: 0,
      orderNum: 5,
      path: '/service',
      component: '',
      menuType: 'M',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'Tools',
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: [
        {
          id: 51,
          menuName: '投诉管理',
          parentId: 5,
          orderNum: 1,
          path: 'complaint',
          component: 'property/complaint/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'service:complaint:view',
          icon: 'Message',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 52,
          menuName: '报修管理',
          parentId: 5,
          orderNum: 2,
          path: 'repair',
          component: 'property/repair/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'service:repair:view',
          icon: 'Tools',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        }
      ]
    },
    {
      id: 6,
      menuName: '停车管理',
      parentId: 0,
      orderNum: 6,
      path: '/parking',
      component: '',
      menuType: 'M',
      visible: 1,
      status: 1,
      perms: '',
      icon: 'Car',
      createTime: new Date('2024-01-01 10:00:00').toISOString(),
      children: [
        {
          id: 61,
          menuName: '车位管理',
          parentId: 6,
          orderNum: 1,
          path: 'space',
          component: 'parking/space/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'parking:space:view',
          icon: 'Car',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        },
        {
          id: 62,
          menuName: '租赁管理',
          parentId: 6,
          orderNum: 2,
          path: 'rental',
          component: 'parking/rental/index',
          menuType: 'C',
          visible: 1,
          status: 1,
          perms: 'parking:rental:view',
          icon: 'Car',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        }
      ]
    }
  ]
}

// 加载菜单数据
const loadMenus = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = generateMockData()
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  loadMenus()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    menuName: '',
    status: ''
  })
  handleSearch()
}

// 展开/折叠
const handleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
}

// 新增
const handleAdd = (row) => {
  isEdit.value = false
  parentMenu.value = row || null

  // 重置表单
  Object.assign(form, {
    id: null,
    parentId: row ? row.id : 0,
    menuName: '',
    menuType: row ? 'C' : 'M',
    orderNum: 0,
    path: '',
    component: '',
    perms: '',
    icon: '',
    status: 1,
    visible: 1,
    remark: ''
  })

  // 根据父菜单类型设置默认值
  if (row) {
    if (row.menuType === 'M') {
      form.menuType = 'C'
      form.path = '/'
    } else if (row.menuType === 'C') {
      form.menuType = 'F'
    }
  }

  // 生成权限标识
  generatePerms()

  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  parentMenu.value = null
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 状态切换
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1

  ElMessageBox.confirm(
    `确定要${action}菜单"${row.menuName}"吗？`,
    `${action}菜单`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    row.status = newStatus
    ElMessage.success(`${action}成功`)
    loadMenus()
  }).catch(() => {
    // 用户取消操作
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除菜单"${row.menuName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadMenus()
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadMenus()
    }
  })
}

// 查找菜单
const findMenuById = (id) => {
  const allMenus = []
  const traverse = (menus) => {
    menus.forEach(menu => {
      allMenus.push(menu)
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children)
      }
    })
  }
  traverse(tableData.value)
  return allMenus.find(menu => menu.id === id)
}

// 权限标识自动生成
const generatePerms = () => {
  if (form.menuType === 'M') {
    form.perms = ''
  } else if (form.menuType === 'C') {
    const pathName = form.path?.replace('/', '') || 'view'
    if (form.parentId && form.parentId !== 0) {
      const parentMenu = findMenuById(form.parentId)
      if (parentMenu) {
        form.perms = `${parentMenu.perms}:${pathName}`
      } else {
        form.perms = `system:${pathName}`
      }
    } else {
      form.perms = `system:${pathName}`
    }
  } else if (form.menuType === 'F') {
    if (form.parentId && form.parentId !== 0) {
      const parentMenu = findMenuById(form.parentId)
      if (parentMenu) {
        form.perms = `${parentMenu.perms}:${form.menuName.replace(/\s+/g, '').toLowerCase()}`
      } else {
        form.perms = `system:${form.menuName.replace(/\s+/g, '').toLowerCase()}`
      }
    } else {
      form.perms = `system:${form.menuName.replace(/\s+/g, '').toLowerCase()}`
    }
  }
}

// 监听菜单类型变化
const handleMenuTypeChange = () => {
  generatePerms()
  if (form.menuType === 'M') {
    form.component = ''
    form.perms = ''
  } else if (form.menuType === 'F') {
    form.component = ''
    form.path = ''
    generatePerms()
  }
}

// 监听上级菜单变化
const handleParentChange = () => {
  generatePerms()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量状态切换
const handleBatchStatus = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要操作的菜单')
    return
  }

  const disabledCount = selectedRows.value.filter(row => row.status === 0).length
  const enabledCount = selectedRows.value.length - disabledCount

  const action = disabledCount > enabledCount ? '启用' : '禁用'
  const newStatus = disabledCount > enabledCount ? 1 : 0

  ElMessageBox.confirm(
    `确定要${action}选中的${selectedRows.value.length}个菜单吗？`,
    `批量${action}`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 更新状态
    selectedRows.value.forEach(row => {
      row.status = newStatus
    })
    ElMessage.success(`批量${action}成功`)
    loadMenus()
  }).catch(() => {
    // 用户取消操作
  })
}

// 初始化
onMounted(() => {
  loadMenus()
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