<template>
  <div class="owner-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon><User /></el-icon>
          业主管理
        </h1>
        <p class="page-description">管理小区内所有业主信息，包括业主基本信息、房产信息、联系方式等</p>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="search-section" shadow="never">
      <el-form
        ref="searchFormRef"
        :model="queryParams"
        inline
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :span="5">
            <el-form-item label="业主姓名" prop="ownerName">
              <el-input
                v-model="queryParams.ownerName"
                placeholder="请输入业主姓名"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="手机号码" prop="phone">
              <el-input
                v-model="queryParams.phone"
                placeholder="请输入手机号码"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="身份证号" prop="idCard">
              <el-input
                v-model="queryParams.idCard"
                placeholder="请输入身份证号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="业主状态" prop="ownerStatus">
              <el-select
                v-model="queryParams.ownerStatus"
                placeholder="请选择状态"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in ownerStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-section" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="table-title">业主列表</span>
            <el-tag type="info" size="small">共 {{ total }} 条记录</el-tag>
          </div>
          <div class="header-right">
            <el-button
              type="primary"
              @click="handleAdd"
              v-permission="'owner:add'"
            >
              <el-icon><Plus /></el-icon>
              新增业主
            </el-button>
            <el-button
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedRows.length === 0"
              v-permission="'owner:delete'"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button @click="handleImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="ownerList"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />

        <el-table-column
          prop="ownerName"
          label="业主姓名"
          width="120"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <div class="owner-info">
              <el-avatar :size="32" :src="row.avatar">
                {{ row.ownerName?.charAt(0) }}
              </el-avatar>
              <span class="owner-name">{{ row.ownerName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="gender"
          label="性别"
          width="80"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? 'primary' : 'danger'" size="small">
              {{ row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="phone"
          label="手机号码"
          width="130"
          align="center"
        >
          <template #default="{ row }">
            <span class="phone-cell">{{ maskPhone(row.phone) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="idCard"
          label="身份证号"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            <span class="id-card-cell">{{ maskIdCard(row.idCard) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="houseCount"
          label="房产数量"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.houseCount || 0 }}套</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="totalArea"
          label="总面积"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <span class="area-cell">{{ row.totalArea || 0 }}m²</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="ownerStatus"
          label="业主状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getOwnerStatusTag(row.ownerStatus)">
              {{ getOwnerStatusName(row.ownerStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="registerTime"
          label="注册时间"
          width="180"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.registerTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="lastLoginTime"
          label="最后登录"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.lastLoginTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
              v-permission="'owner:edit'"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewHouses(row)"
            >
              <el-icon><House /></el-icon>
              房产
            </el-button>
            <el-button
              link
              type="success"
              @click="handleViewWallet(row)"
            >
              <el-icon><Wallet /></el-icon>
              钱包
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleResetPassword(row)"
              v-permission="'owner:resetPassword'"
            >
              <el-icon><Key /></el-icon>
              重置密码
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'owner:delete'"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        class="owner-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业主姓名" prop="ownerName">
              <el-input
                v-model="form.ownerName"
                placeholder="请输入业主姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号码"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input
                v-model="form.idCard"
                placeholder="请输入身份证号"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="请选择出生日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主状态" prop="ownerStatus">
              <el-select v-model="form.ownerStatus" placeholder="请选择状态" style="width: 100%">
                <el-option
                  v-for="item in ownerStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="联系地址" prop="address">
          <el-input
            v-model="form.address"
            placeholder="请输入联系地址"
          />
        </el-form-item>

        <el-form-item label="邮箱地址" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址"
          />
        </el-form-item>

        <el-form-item label="备注信息" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看房产对话框 -->
    <el-dialog
      v-model="houseDialogVisible"
      title="业主房产"
      width="1000px"
      @close="houseDialogVisible = false"
    >
      <div class="house-list-content">
        <div class="owner-summary">
          <el-avatar :size="64" :src="currentOwner.avatar">
            {{ currentOwner.ownerName?.charAt(0) }}
          </el-avatar>
          <div class="owner-details">
            <h3>{{ currentOwner.ownerName }}</h3>
            <p>{{ currentOwner.phone }}</p>
            <p>房产总数: {{ currentOwner.houseCount || 0 }}套 | 总面积: {{ currentOwner.totalArea || 0 }}m²</p>
          </div>
        </div>

        <el-table :data="houseList" border stripe v-loading="houseLoading">
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column prop="houseCode" label="房产编号" width="140" align="center">
            <template #default="{ row }">
              <el-tag type="primary" size="small">{{ row.houseCode }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="buildingName" label="楼栋" width="100" align="center" />
          <el-table-column prop="unitName" label="单元" width="100" align="center" />
          <el-table-column prop="roomNum" label="门牌号" width="100" align="center" />
          <el-table-column prop="houseType" label="户型" width="120" align="center">
            <template #default="{ row }">
              <el-tag type="warning" size="small">{{ row.houseType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="buildArea" label="建筑面积" width="120" align="center">
            <template #default="{ row }">
              <span class="area-cell">{{ row.buildArea }}m²</span>
            </template>
          </el-table-column>
          <el-table-column prop="houseStatus" label="房产状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getHouseStatusTag(row.houseStatus)" size="small">
                {{ getHouseStatusName(row.houseStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="purchaseDate" label="购买日期" width="120" align="center" />
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleViewHouseDetail(row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="houseDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 钱包信息对话框 -->
    <el-dialog
      v-model="walletDialogVisible"
      title="钱包信息"
      width="600px"
      @close="walletDialogVisible = false"
    >
      <div class="wallet-content">
        <div class="wallet-header">
          <el-avatar :size="48" :src="currentOwner.avatar">
            {{ currentOwner.ownerName?.charAt(0) }}
          </el-avatar>
          <div class="wallet-user">
            <h3>{{ currentOwner.ownerName }}</h3>
            <p>手机号: {{ currentOwner.phone }}</p>
          </div>
        </div>

        <el-row :gutter="20" class="wallet-cards">
          <el-col :span="12">
            <el-card class="wallet-card">
              <div class="wallet-item">
                <div class="wallet-label">账户余额</div>
                <div class="wallet-value balance">¥{{ walletInfo.balance || 0 }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="wallet-card">
              <div class="wallet-item">
                <div class="wallet-label">冻结金额</div>
                <div class="wallet-value frozen">¥{{ walletInfo.frozenAmount || 0 }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="wallet-actions">
          <el-button type="primary" @click="handleRecharge" v-permission="'wallet:recharge'">
            <el-icon><Plus /></el-icon>
            充值
          </el-button>
          <el-button type="warning" @click="handleFreeze" v-permission="'wallet:freeze'">
            <el-icon><Lock /></el-icon>
            冻结
          </el-button>
          <el-button type="success" @click="handleViewTransactions">
            <el-icon><DataLine /></el-icon>
            交易记录
          </el-button>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="walletDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入业主"
      width="500px"
      @close="importDialogVisible = false"
    >
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        :before-upload="beforeUpload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :file-list="fileList"
        accept=".xlsx,.xls"
        :auto-upload="false"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            只能上传xlsx/xls文件，且不超过10MB
            <el-link type="primary" @click="downloadTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">
            开始导入
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Search,
  Refresh,
  Plus,
  Delete,
  Download,
  Edit,
  House,
  Wallet,
  Key,
  Upload,
  Lock,
  DataLine
} from '@element-plus/icons-vue'
import {
  getOwnerList,
  getOwnerDetail,
  addOwner,
  updateOwner,
  deleteOwner,
  batchDeleteOwner,
  resetOwnerPassword
} from '@/api/owner'
import { getHousesByOwner } from '@/api/house'
import { getOwnerWallet } from '@/api/wallet'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const uploadRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const houseLoading = ref(false)
const importLoading = ref(false)
const dialogVisible = ref(false)
const houseDialogVisible = ref(false)
const walletDialogVisible = ref(false)
const importDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const ownerList = ref([])
const houseList = ref([])
const total = ref(0)
const currentOwner = ref({})
const walletInfo = ref({})
const fileList = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ownerName: '',
  phone: '',
  idCard: '',
  ownerStatus: ''
})

// 表单数据
const form = reactive({
  ownerId: null,
  ownerName: '',
  gender: 1,
  phone: '',
  idCard: '',
  birthday: '',
  address: '',
  email: '',
  ownerStatus: 1,
  remark: ''
})

// 选项数据
const ownerStatusOptions = [
  { label: '正常', value: 1 },
  { label: '冻结', value: 2 },
  { label: '注销', value: 3 }
]

const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

// 表单验证规则
const formRules = {
  ownerName: [
    { required: true, message: '请输入业主姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '业主姓名长度在2到20个字符', trigger: 'blur' }
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
    { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑业主' : '新增业主')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 脱敏手机号
const maskPhone = (phone) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 脱敏身份证号
const maskIdCard = (idCard) => {
  if (!idCard) return '-'
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

// 获取业主状态名称
const getOwnerStatusName = (status) => {
  const option = ownerStatusOptions.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取业主状态标签
const getOwnerStatusTag = (status) => {
  const tagMap = {
    1: 'success', // 正常
    2: 'danger',  // 冻结
    3: 'info'     // 注销
  }
  return tagMap[status] || 'info'
}

// 获取房产状态名称
const getHouseStatusName = (status) => {
  const option = houseStatusOptions.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取房产状态标签
const getHouseStatusTag = (status) => {
  const tagMap = {
    1: 'info',    // 空置
    2: 'success', // 已售
    3: 'warning', // 已租
    4: 'primary'  // 自住
  }
  return tagMap[status] || 'info'
}

// 获取业主列表
const getOwnerListData = async () => {
  loading.value = true
  try {
    const response = await getOwnerList(queryParams)
    const data = handleResponse(response)
    ownerList.value = data.list || data.records || []
    total.value = data.total || 0
  } catch (error) {
    handleError(error, '获取业主列表失败')
    ownerList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取业主的房产列表
const getHouseListByOwner = async (ownerId) => {
  houseLoading.value = true
  try {
    const response = await getHousesByOwner(ownerId)
    houseList.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取房产列表失败')
    houseList.value = []
  } finally {
    houseLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getOwnerListData()
}

// 重置搜索
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.ownerName = ''
  queryParams.phone = ''
  queryParams.idCard = ''
  queryParams.ownerStatus = ''
  getOwnerListData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getOwnerListData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getOwnerListData()
}

// 排序变化
const handleSortChange = ({ column, prop, order }) => {
  console.log('排序变化:', { column, prop, order })
  getOwnerListData()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增业主
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑业主
const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const response = await getOwnerDetail(row.ownerId)
    const data = handleResponse(response)
    Object.assign(form, data)
    dialogVisible.value = true
  } catch (error) {
    handleError(error, '获取业主详情失败')
  }
}

// 删除业主
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除业主"${row.ownerName}"吗？删除后不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteOwner(row.ownerId)
    ElMessage.success('删除成功')
    getOwnerListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的业主')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个业主吗？删除后不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ownerIds = selectedRows.value.map(row => row.ownerId)
    await batchDeleteOwner(ownerIds)
    ElMessage.success('批量删除成功')
    getOwnerListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '批量删除失败')
    }
  }
}

// 查看房产
const handleViewHouses = async (row) => {
  currentOwner.value = row
  houseDialogVisible.value = true
  await getHouseListByOwner(row.ownerId)
}

// 查看钱包
const handleViewWallet = async (row) => {
  currentOwner.value = row
  try {
    const response = await getOwnerWallet(row.ownerId)
    walletInfo.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取钱包信息失败')
    walletInfo.value = { balance: 0, frozenAmount: 0 }
  }
  walletDialogVisible.value = true
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置业主"${row.ownerName}"的密码吗？新密码将发送到手机${maskPhone(row.phone)}`,
      '重置密码确认',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await resetOwnerPassword(row.ownerId)
    ElMessage.success('密码重置成功，新密码已发送到业主手机')
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '重置密码失败')
    }
  }
}

// 批量导入
const handleImport = () => {
  fileList.value = []
  importDialogVisible.value = true
}

// 下载模板
const downloadTemplate = () => {
  ElMessage.info('模板下载功能开发中...')
}

// 文件上传前检查
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                  file.type === 'application/vnd.ms-excel'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传Excel文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!')
    return false
  }
  return false // 阻止自动上传
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  ElMessage.success('文件上传成功')
  importDialogVisible.value = false
  getOwnerListData()
}

// 上传失败
const handleUploadError = (error, file) => {
  ElMessage.error('文件上传失败')
}

// 提交导入
const handleImportSubmit = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    // 这里应该调用导入API
    // await importOwners(fileList.value[0].raw)

    // 模拟导入
    setTimeout(() => {
      ElMessage.success('导入成功')
      importDialogVisible.value = false
      getOwnerListData()
      importLoading.value = false
    }, 2000)
  } catch (error) {
    handleError(error, '导入失败')
    importLoading.value = false
  }
}

// 导出Excel
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 充值
const handleRecharge = () => {
  ElMessage.info('充值功能开发中...')
}

// 冻结
const handleFreeze = () => {
  ElMessage.info('冻结功能开发中...')
}

// 查看交易记录
const handleViewTransactions = () => {
  ElMessage.info('交易记录功能开发中...')
}

// 查看房产详情
const handleViewHouseDetail = (row) => {
  ElMessage.info(`查看房产${row.houseCode}的详细信息`)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateOwner(form)
      ElMessage.success('编辑成功')
    } else {
      await addOwner(form)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    getOwnerListData()
  } catch (error) {
    handleError(error, isEdit.value ? '编辑失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    ownerId: null,
    ownerName: '',
    gender: 1,
    phone: '',
    idCard: '',
    birthday: '',
    address: '',
    email: '',
    ownerStatus: 1,
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
  getOwnerListData()
})
</script>

<style lang="scss" scoped>
.owner-management {
  .page-header {
    margin-bottom: 20px;
    padding: 24px;
    background: linear-gradient(135deg, #56CCF2 0%, #2F80ED 100%);
    border-radius: 8px;
    color: white;

    .header-content {
      .page-title {
        display: flex;
        align-items: center;
        font-size: 28px;
        font-weight: 600;
        margin: 0 0 8px 0;

        .el-icon {
          margin-right: 12px;
          font-size: 32px;
        }
      }

      .page-description {
        font-size: 16px;
        opacity: 0.9;
        margin: 0;
      }
    }
  }

  .search-section {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-section {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .table-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }

      .header-right {
        display: flex;
        gap: 10px;
      }
    }

    .data-table {
      margin-bottom: 16px;

      .owner-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .owner-name {
          font-weight: 500;
          color: #303133;
        }
      }

      .phone-cell {
        font-family: 'Courier New', monospace;
        color: #606266;
      }

      .id-card-cell {
        font-family: 'Courier New', monospace;
        color: #909399;
        font-size: 13px;
      }

      .area-cell {
        font-weight: 600;
        color: #67c23a;
      }

      .time-cell {
        font-size: 13px;
        color: #909399;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding: 16px 0;
    }
  }

  .owner-form {
    .el-form-item {
      margin-bottom: 18px;
    }
  }

  .house-list-content {
    .owner-summary {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 24px;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;

      .owner-details {
        h3 {
          margin: 0 0 8px 0;
          color: #303133;
          font-size: 18px;
        }

        p {
          margin: 4px 0;
          color: #666;
        }
      }
    }

    .area-cell {
      font-weight: 600;
      color: #67c23a;
    }
  }

  .wallet-content {
    .wallet-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 24px;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;

      .wallet-user {
        h3 {
          margin: 0 0 8px 0;
          color: #303133;
          font-size: 18px;
        }

        p {
          margin: 4px 0;
          color: #666;
        }
      }
    }

    .wallet-cards {
      margin-bottom: 24px;

      .wallet-card {
        text-align: center;

        .wallet-item {
          padding: 16px;

          .wallet-label {
            font-size: 14px;
            color: #666;
            margin-bottom: 8px;
          }

          .wallet-value {
            font-size: 24px;
            font-weight: bold;

            &.balance {
              color: #67c23a;
            }

            &.frozen {
              color: #f56c6c;
            }
          }
        }
      }
    }

    .wallet-actions {
      text-align: center;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
  }

  .upload-demo {
    text-align: center;
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #fafafa;
      font-weight: 600;
      color: #303133;
    }
  }

  .el-table__body {
    tr:hover > td {
      background-color: #f5f7fa;
    }
  }
}

// 卡片样式优化
:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .el-card__header {
    border-bottom: 1px solid #ebeef5;
    padding: 16px 20px;
  }

  .el-card__body {
    padding: 20px;
  }
}

// 按钮样式优化
:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
}

// 输入框样式优化
:deep(.el-input__wrapper) {
  border-radius: 6px;
}

// 标签样式优化
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

// 上传组件样式优化
:deep(.el-upload) {
  .el-upload-dragger {
    border-radius: 8px;
  }
}
</style>