<template>
  <div class="owner-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">业主管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>业主管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="业主姓名">
          <el-input
            v-model="searchForm.ownerName"
            placeholder="请输入业主姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号码"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="房产地址">
          <el-input
            v-model="searchForm.houseAddress"
            placeholder="请输入房产地址"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="业主状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" value="1" />
            <el-option label="冻结" value="0" />
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

    <!-- 操作按钮区域 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
        v-permission="'property:owner:add'"
      >
        <el-icon><Plus /></el-icon>
        新增业主
      </el-button>
      <el-button
        type="success"
        @click="handleImport"
        v-permission="'property:owner:import'"
      >
        <el-icon><Upload /></el-icon>
        导入业主
      </el-button>
      <el-button
        type="warning"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出业主
      </el-button>
      <el-button
        type="danger"
        @click="handleBatchDelete"
        :disabled="!selectedOwners.length"
        v-permission="'property:owner:delete'"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 业主列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="ownerList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="ownerId" label="业主编号" width="100" />
        <el-table-column prop="ownerName" label="业主姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="houseAddress" label="房产地址" show-overflow-tooltip />
        <el-table-column prop="propertyArea" label="房产面积" width="120">
          <template #default="{ row }">
            {{ row.propertyArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="ownershipType" label="产权性质" width="100">
          <template #default="{ row }">
            {{ getOwnershipTypeName(row.ownershipType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="业主状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="登记时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
              v-permission="'property:owner:edit'"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleManageProperty(row)"
              v-permission="'property:house:view'"
            >
              房产
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewBills(row)"
              v-permission="'property:bill:view'"
            >
              账单
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
              v-permission="'property:owner:edit'"
            >
              {{ row.status === 1 ? '冻结' : '解冻' }}
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'property:owner:delete'"
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

    <!-- 新增/编辑业主对话框 -->
    <el-dialog
      v-model="ownerDialogVisible"
      :title="ownerForm.ownerId ? '编辑业主' : '新增业主'"
      width="800px"
    >
      <el-form
        ref="ownerFormRef"
        :model="ownerForm"
        :rules="ownerRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业主姓名" prop="ownerName">
              <el-input v-model="ownerForm.ownerName" placeholder="请输入业主姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="ownerForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="ownerForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="ownerForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="ownerForm.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产权性质" prop="ownershipType">
              <el-select v-model="ownerForm.ownershipType" placeholder="请选择产权性质" style="width: 100%">
                <el-option label="商品房" :value="1" />
                <el-option label="经济适用房" :value="2" />
                <el-option label="廉租房" :value="3" />
                <el-option label="共有产权房" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="房产地址" prop="houseAddress">
          <el-input v-model="ownerForm.houseAddress" placeholder="请输入房产地址" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房产面积" prop="propertyArea">
              <el-input-number
                v-model="ownerForm.propertyArea"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主状态" prop="status">
              <el-radio-group v-model="ownerForm.status">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">冻结</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="ownerForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="ownerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 业主详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="业主详情"
      width="800px"
    >
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="业主编号">{{ ownerDetail.ownerId }}</el-descriptions-item>
          <el-descriptions-item label="业主姓名">{{ ownerDetail.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ ownerDetail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ ownerDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ ownerDetail.idCard }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ ownerDetail.email }}</el-descriptions-item>
          <el-descriptions-item label="房产地址">{{ ownerDetail.houseAddress }}</el-descriptions-item>
          <el-descriptions-item label="房产面积">{{ ownerDetail.propertyArea }}m²</el-descriptions-item>
          <el-descriptions-item label="产权性质">{{ getOwnershipTypeName(ownerDetail.ownershipType) }}</el-descriptions-item>
          <el-descriptions-item label="业主状态">
            <el-tag :type="ownerDetail.status === 1 ? 'success' : 'danger'">
              {{ ownerDetail.status === 1 ? '正常' : '冻结' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="登记时间">{{ formatDateTime(ownerDetail.registerTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ ownerDetail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h4>相关房产</h4>
          <el-table :data="ownerDetail.properties || []" size="small">
            <el-table-column prop="buildingName" label="楼栋" />
            <el-table-column prop="unitName" label="单元" />
            <el-table-column prop="houseNumber" label="房号" />
            <el-table-column prop="area" label="面积">
              <template #default="{ row }">
                {{ row.area }}m²
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '已入住' : '空置' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="detail-section">
          <h4>费用统计</h4>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="应缴费用" :value="ownerDetail.totalAmount" prefix="￥" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="已缴费用" :value="ownerDetail.paidAmount" prefix="￥" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="待缴费用" :value="ownerDetail.unpaidAmount" prefix="￥" />
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Upload, Download } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const ownerDialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 表单引用
const ownerFormRef = ref()

// 搜索表单
const searchForm = reactive({
  ownerName: '',
  phone: '',
  houseAddress: '',
  status: ''
})

// 业主表单
const ownerForm = reactive({
  ownerId: null,
  ownerName: '',
  gender: 1,
  phone: '',
  idCard: '',
  email: '',
  houseAddress: '',
  propertyArea: 0,
  ownershipType: 1,
  status: 1,
  remark: ''
})

// 数据列表
const ownerList = ref([])
const ownerDetail = ref({})
const selectedOwners = ref([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单验证规则
const ownerRules = {
  ownerName: [
    { required: true, message: '请输入业主姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  houseAddress: [
    { required: true, message: '请输入房产地址', trigger: 'blur' }
  ],
  propertyArea: [
    { required: true, message: '请输入房产面积', trigger: 'blur' }
  ],
  ownershipType: [
    { required: true, message: '请选择产权性质', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择业主状态', trigger: 'change' }
  ]
}

// 获取产权性质名称
const getOwnershipTypeName = (type) => {
  const typeMap = {
    1: '商品房',
    2: '经济适用房',
    3: '廉租房',
    4: '共有产权房'
  }
  return typeMap[type] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生成模拟业主数据
const generateMockOwners = () => {
  const owners = []
  const surnames = ['张', '李', '王', '刘', '陈', '杨', '赵', '黄', '周', '吴']
  const names = ['伟', '芳', '娜', '敏', '静', '丽', '强', '磊', '洋', '艳']
  const buildings = ['1号楼', '2号楼', '3号楼', '5号楼', '6号楼', '8号楼']
  const units = ['1单元', '2单元', '3单元']

  for (let i = 1; i <= 50; i++) {
    const ownerName = surnames[Math.floor(Math.random() * surnames.length)] +
                      names[Math.floor(Math.random() * names.length)]
    const building = buildings[Math.floor(Math.random() * buildings.length)]
    const unit = units[Math.floor(Math.random() * units.length)]
    const room = Math.floor(Math.random() * 6) + 1 + '0' + (Math.floor(Math.random() * 8) + 1)

    owners.push({
      ownerId: i,
      ownerName,
      gender: Math.random() > 0.5 ? 1 : 2,
      phone: `138${Math.random().toString().substr(2, 8)}`,
      idCard: `${Math.floor(Math.random() * 900000000000000000) + 100000000000000000}`,
      email: `${ownerName.toLowerCase()}@example.com`,
      houseAddress: `${building}${unit}${room}室`,
      propertyArea: (Math.random() * 200 + 50).toFixed(2),
      ownershipType: Math.floor(Math.random() * 4) + 1,
      status: Math.random() > 0.1 ? 1 : 0,
      registerTime: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000),
      remark: Math.random() > 0.7 ? '备注信息' : ''
    })
  }

  // 模拟分页数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  total.value = owners.length
  return owners.slice(start, end)
}

// 生成业主详情数据
const generateOwnerDetail = (ownerId) => {
  const owner = ownerList.value.find(o => o.ownerId === ownerId)
  if (!owner) return {}

  return {
    ...owner,
    properties: [
      {
        buildingName: '3号楼',
        unitName: '2单元',
        houseNumber: '501',
        area: owner.propertyArea,
        status: 1
      }
    ],
    totalAmount: Math.floor(Math.random() * 10000) + 1000,
    paidAmount: Math.floor(Math.random() * 5000) + 500,
    unpaidAmount: 0
  }
}

// 加载业主列表
const loadOwnerList = () => {
  loading.value = true
  setTimeout(() => {
    ownerList.value = generateMockOwners()
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOwnerList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    ownerName: '',
    phone: '',
    houseAddress: '',
    status: ''
  })
  handleSearch()
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedOwners.value = selection
}

// 新增业主
const handleAdd = () => {
  Object.assign(ownerForm, {
    ownerId: null,
    ownerName: '',
    gender: 1,
    phone: '',
    idCard: '',
    email: '',
    houseAddress: '',
    propertyArea: 0,
    ownershipType: 1,
    status: 1,
    remark: ''
  })
  ownerDialogVisible.value = true
}

// 编辑业主
const handleEdit = (row) => {
  Object.assign(ownerForm, { ...row })
  ownerDialogVisible.value = true
}

// 查看业主详情
const handleView = (row) => {
  ownerDetail.value = generateOwnerDetail(row.ownerId)
  detailDialogVisible.value = true
}

// 删除业主
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除业主"${row.ownerName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadOwnerList()
  })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedOwners.value.length} 个业主吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量删除成功')
    loadOwnerList()
  })
}

// 切换业主状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '冻结' : '解冻'
  ElMessageBox.confirm(
    `确定要${action}业主"${row.ownerName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
  })
}

// 管理房产
const handleManageProperty = (row) => {
  ElMessage.info('跳转到房产管理页面')
}

// 查看账单
const handleViewBills = (row) => {
  ElMessage.info('跳转到账单管理页面')
}

// 导入业主
const handleImport = () => {
  ElMessage.success('导入功能开发中')
}

// 导出业主
const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

// 提交表单
const handleSubmit = async () => {
  try {
    await ownerFormRef.value.validate()
    submitting.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(ownerForm.ownerId ? '编辑成功' : '新增成功')
    ownerDialogVisible.value = false
    loadOwnerList()
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadOwnerList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadOwnerList()
}

onMounted(() => {
  loadOwnerList()
})
</script>

<style lang="scss" scoped>
.owner-container {
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

.detail-content {
  .detail-section {
    margin-top: 20px;

    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
}
</style>