<template>
  <div class="log-container">
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
            v-model="searchForm.realName"
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
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="住户状态">
          <el-select
            v-model="searchForm.residentStatus"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="在住" :value="1" />
            <el-option label="已搬离" :value="2" />
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
        <el-table-column prop="userId" label="业主编号" width="80" />
        <el-table-column prop="realName" label="业主姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">
            {{ row.gender === 0 ? '女' : row.gender === 1 ? '男' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号码" width="120" />
        <el-table-column prop="residentStatus" label="住户状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.residentStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.residentStatus === 1 ? '在住' : '已搬离' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="房产信息" width="200">
          <template #default="{ row }">
            <div v-if="row.houseList && row.houseList.length > 0">
              <div v-for="(house, index) in row.houseList" :key="index" class="house-info">
                <div class="house-location">
                  <span class="building-name">{{ house.buildingName || '-' }}</span>
                  <span class="unit-name">{{ house.unitName || '-' }}</span>
                  <span class="house-no">{{ house.houseNo || '-' }}</span>
                </div>
                <div class="house-detail">
                  <span class="house-type">{{ house.houseType || '-' }}</span>
                  <span class="house-area">{{ house.buildingAreaNum || '-' }}㎡</span>
                </div>
              </div>
            </div>
            <span v-else class="no-house">暂无房产</span>
          </template>
        </el-table-column>
          <el-table-column prop="createTime" label="登记时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
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
              @click="handleViewProperties(row)"
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
      :title="ownerForm.userId ? '编辑业主' : '新增业主'"
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
            <el-form-item label="登录账号" prop="username">
              <el-input v-model="ownerForm.username" placeholder="请输入登录账号" :disabled="!!ownerForm.userId" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主姓名" prop="realName">
              <el-input v-model="ownerForm.realName" placeholder="请输入业主姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="ownerForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录密码" prop="password" v-if="!ownerForm.userId">
              <el-input v-model="ownerForm.password" type="password" placeholder="请输入登录密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="ownerForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="ownerForm.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住户状态" prop="residentStatus">
              <el-select v-model="ownerForm.residentStatus" placeholder="请选择住户状态" style="width: 100%">
                <el-option label="在住" :value="1" />
                <el-option label="搬离" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主状态" prop="status">
              <el-select v-model="ownerForm.status" placeholder="请选择业主状态" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="冻结" :value="0" />
              </el-select>
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
          <el-descriptions-item label="业主编号">{{ ownerDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="业主姓名">{{ ownerDetail.realName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ ownerDetail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ ownerDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ ownerDetail.idCard }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ ownerDetail.email }}</el-descriptions-item>
          <el-descriptions-item label="住户状态">
            <el-tag :type="ownerDetail.residentStatus === 1 ? 'success' : 'info'">
              {{ ownerDetail.residentStatus === 1 ? '在住' : '已搬离' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业主状态">
            <el-tag :type="ownerDetail.status === 1 ? 'success' : 'danger'">
              {{ ownerDetail.status === 1 ? '正常' : '冻结' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(ownerDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ ownerDetail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section" v-if="ownerDetail.houseList && ownerDetail.houseList.length > 0">
          <h4>相关房产</h4>
          <el-table :data="ownerDetail.houseList" size="small">
            <el-table-column prop="buildingName" label="楼栋" width="100" />
            <el-table-column prop="unitName" label="单元" width="100" />
            <el-table-column prop="houseNo" label="房号" width="120" />
            <el-table-column prop="houseType" label="户型" width="100" />
            <el-table-column prop="buildingArea" label="建筑面积" width="120">
              <template #default="{ row }">
                {{ row.buildingArea }}m²
              </template>
            </el-table-column>
            <el-table-column prop="relationType" label="关系" width="100">
              <template #default="{ row }">
                {{ row.relationType === 1 ? '业主' : '租户' }}
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

    <!-- 房产详情对话框 -->
    <el-dialog
      v-model="propertyDialogVisible"
      title="业主房产信息"
      width="900px"
    >
      <template #header="{ titleId, titleClass }">
        <div class="dialog-header">
          <span :id="titleId" :class="titleClass">业主房产信息</span>
          <el-button
            type="primary"
            size="small"
            @click="handleAssignProperty(propertyOwner)"
          >
            <el-icon><Plus /></el-icon>
            分配房产
          </el-button>
        </div>
      </template>
      <div class="property-content">
        <!-- 业主基本信息 -->
        <el-descriptions :column="3" border class="owner-basic-info">
          <el-descriptions-item label="业主姓名">{{ propertyOwner.realName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ propertyOwner.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ propertyOwner.phone }}</el-descriptions-item>
        </el-descriptions>

        <!-- 房产列表 -->
        <div class="property-list-section" v-if="propertyOwner.houseList && propertyOwner.houseList.length > 0">
          <h4 style="margin: 20px 0 15px 0; color: #409EFF;">房产列表 ({{ propertyOwner.houseList.length }}套)</h4>
          <el-table :data="propertyOwner.houseList" stripe>
            <el-table-column prop="buildingName" label="楼栋" width="120" />
            <el-table-column prop="unitName" label="单元" width="100" />
            <el-table-column prop="houseNo" label="房号" width="120" />
            <el-table-column prop="houseType" label="户型" width="150" />
            <el-table-column prop="buildingAreaNum" label="建筑面积(m²)" width="120">
              <template #default="{ row }">
                {{ row.buildingAreaNum || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="usableArea" label="使用面积(m²)" width="120">
              <template #default="{ row }">
                {{ row.usableArea || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="relationType" label="房产关系" width="100">
              <template #default="{ row }">
                <el-tag :type="row.relationTypeNum === 1 ? 'success' : 'warning'" size="small">
                  {{ row.relationTypeNum === 1 ? '业主' : '租户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isCurrent" label="当前居住" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isCurrent ? 'success' : 'info'" size="small">
                  {{ row.isCurrent ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <!-- 房产统计信息 -->
          <div class="property-summary" style="margin-top: 20px;">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-statistic title="房产总数" :value="propertyOwner.houseList.length" suffix="套" />
              </el-col>
              <el-col :span="8">
                <el-statistic
                  title="总建筑面积"
                  :value="propertyOwner.houseList.reduce((sum, house) => sum + (house.buildingAreaNum || 0), 0)"
                  suffix="m²"
                />
              </el-col>
              <el-col :span="8">
                <el-statistic
                  title="当前居住"
                  :value="propertyOwner.houseList.filter(house => house.isCurrent).length"
                  suffix="套"
                />
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 无房产提示 -->
        <el-empty v-else description="该业主暂无房产信息" style="margin: 30px 0;" />
      </div>
    </el-dialog>

    <!-- 分配房产对话框 -->
    <el-dialog
      v-model="assignPropertyDialogVisible"
      title="分配房产"
      width="800px"
    >
      <div class="assign-property-content">
        <!-- 业主信息 -->
        <el-alert
          :title="`为业主「${propertyOwner.realName}」分配房产`"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        />

        <!-- 可选房产列表 -->
        <div class="available-houses">
          <h4>可选房产</h4>
          <el-table
            :data="availableHouses"
            style="width: 100%"
            @selection-change="handleHouseSelectionChange"
            max-height="400"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="houseNo" label="房产编号" width="150" />
            <el-table-column prop="buildingName" label="楼栋" width="120" />
            <el-table-column prop="unitName" label="单元" width="120" />
            <el-table-column prop="roomNumber" label="房号" width="100" />
            <el-table-column prop="houseType" label="户型" width="120" />
            <el-table-column prop="buildingAreaNum" label="面积(m²)" width="100">
              <template #default="{ row }">
                {{ row.buildingAreaNum || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="houseStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.houseStatus === 1 ? 'success' : 'warning'" size="small">
                  {{ row.houseStatus === 1 ? '空置' : '已售' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <!-- 关系类型设置 -->
          <div class="relation-type-section" v-if="selectedHouses.length > 0">
            <h4>设置房产关系</h4>
            <el-form :model="assignForm" label-width="120px">
              <el-form-item label="关系类型">
                <el-radio-group v-model="assignForm.relationType">
                  <el-radio :label="1">业主</el-radio>
                  <el-radio :label="2">租户</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="开始日期">
                <el-date-picker
                  v-model="assignForm.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="结束日期" v-if="assignForm.relationType === 2">
                <el-date-picker
                  v-model="assignForm.endDate"
                  type="date"
                  placeholder="选择结束日期（租户必填）"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="设为当前居住">
                <el-switch v-model="assignForm.isCurrent" />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignPropertyDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleConfirmAssign"
            :loading="assigning"
            :disabled="selectedHouses.length === 0"
          >
            确认分配 ({{ selectedHouses.length }})
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import { listOwners, getOwner, addOwner, updateOwner, deleteOwners, resetPassword, changeStatus } from '@/api/owner'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const assigning = ref(false)
const ownerDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const propertyDialogVisible = ref(false)
const assignPropertyDialogVisible = ref(false)

// 表单引用
const ownerFormRef = ref()

// 搜索表单
const searchForm = reactive({
  realName: '',
  phone: '',
  username: '',
  residentStatus: ''
})

// 业主表单
const ownerForm = reactive({
  userId: null,
  username: '',
  password: '',
  realName: '',
  gender: 0,
  phone: '',
  email: '',
  residentType: 1,
  residentStatus: 1, // 住户状态：1-在住，2-搬离
  status: 1, // 用户状态：1-正常，0-禁用
  remark: ''
})

// 数据列表
const ownerList = ref([])
const ownerDetail = ref({})
const propertyOwner = ref({})
const selectedOwners = ref([])

// 分配房产相关数据
const availableHouses = ref([])
const selectedHouses = ref([])
const assignForm = reactive({
  relationType: 1, // 1-业主 2-租户
  startDate: '',
  endDate: '',
  isCurrent: true
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单验证规则
const ownerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  residentType: [
    { required: true, message: '请选择住户类型', trigger: 'change' }
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

// 生成业主详情数据（使用真实数据）
const generateOwnerDetail = (ownerId) => {
  const owner = ownerList.value.find(o => o.userId === ownerId)
  if (!owner) return {}

  // 使用后端返回的真实房产数据
  const properties = owner.houseList && owner.houseList.length > 0
    ? owner.houseList.map(house => ({
        buildingName: house.buildingName || '-',
        unitName: house.unitName || '-',
        houseNumber: house.houseNo || '-',
        area: house.buildingAreaNum || 0,
        houseType: house.houseType || '-',
        status: house.status || 1
      }))
    : []

  // 计算总面积
  const totalArea = properties.reduce((sum, prop) => sum + (prop.area || 0), 0)

  return {
    ...owner,
    properties: properties,
    totalArea: totalArea,
    totalProperties: properties.length,
    // 财务信息使用真实字段或提供默认值
    totalAmount: owner.totalAmount || 0,
    paidAmount: owner.paidAmount || 0,
    unpaidAmount: owner.unpaidAmount || 0
  }
}

// 加载业主列表
const loadOwnerList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      realName: searchForm.realName,
      phone: searchForm.phone,
      username: searchForm.username,
      residentStatus: searchForm.residentStatus ? Number(searchForm.residentStatus) : null
    }
    const response = await listOwners(params)
    if (response.code === 200) {
      ownerList.value = response.data.rows
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载业主数据失败')
    }
  } catch (error) {
    console.error('加载业主数据错误:', error)
    ElMessage.error('加载业主数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOwnerList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    realName: '',
    phone: '',
    username: '',
    residentStatus: ''
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
    userId: null,
    username: '',
    password: '123456',
    realName: '',
    gender: 0,
    phone: '',
    email: '',
    residentType: 1,
    status: 1, // 新增业主默认状态为启用
    remark: ''
  })
  ownerDialogVisible.value = true
}

// 编辑业主
const handleEdit = (row) => {
  // 清空表单
  Object.assign(ownerForm, {
    userId: null,
    username: '',
    password: '',
    realName: '',
    gender: 0,
    phone: '',
    email: '',
    residentType: 1,
    residentStatus: 1, // 住户状态：1-在住，2-搬离
    status: 1, // 保持默认状态
    remark: ''
  })

  // 填充数据，保持原有状态值
  Object.assign(ownerForm, {
    userId: row.userId,
    username: row.username || '',
    realName: row.realName || '',
    gender: row.gender || 0,
    phone: row.phone || '',
    email: row.email || '',
    residentType: row.residentType || 1,
    residentStatus: row.residentStatus || 1, // 保持原有住户状态
    status: row.status || 1, // 保持原有状态
    remark: row.remark || ''
  })

  ownerDialogVisible.value = true
}

// 查看业主详情
const handleView = (row) => {
  ownerDetail.value = generateOwnerDetail(row.userId)
  detailDialogVisible.value = true
}

// 删除业主
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除业主"${row.realName}"吗？删除后将无法恢复，且会清除该业主的所有关联数据。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    const response = await deleteOwners([row.userId])
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadOwnerList()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除错误:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedOwners.value.length === 0) {
    ElMessage.warning('请选择要删除的业主')
    return
  }

  const ownerNames = selectedOwners.value.map(item => item.realName).join('、')
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedOwners.value.length} 个业主吗？\n业主：${ownerNames}\n删除后将无法恢复，且会清除这些业主的所有关联数据。`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    const ids = selectedOwners.value.map(item => item.userId)
    const response = await deleteOwners(ids)
    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedOwners.value.length} 个业主`)
      selectedOwners.value = []
      loadOwnerList()
    } else {
      ElMessage.error(response.msg || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除错误:', error)
      ElMessage.error('批量删除失败')
    }
  }
}


// 查看房产详情
const handleViewProperties = async (row) => {
  try {
    const res = await getOwner(row.userId)
    propertyOwner.value = res.data
    propertyDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取业主房产信息失败')
  }
}

// 分配房产
const handleAssignProperty = async (row) => {
  try {
    // 设置当前选中的业主
    propertyOwner.value = row

    // 获取可分配的房产列表（空置状态的房产）
    const response = await fetch(`/api/v1/property/house/available?userId=${row.userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    if (response.ok) {
      const result = await response.json()
      availableHouses.value = result.data || []
      selectedHouses.value = []

      // 重置表单
      Object.assign(assignForm, {
        relationType: 1,
        startDate: new Date().toISOString().split('T')[0],
        endDate: '',
        isCurrent: true
      })

      assignPropertyDialogVisible.value = true
    } else {
      ElMessage.error('获取可选房产列表失败')
    }
  } catch (error) {
    ElMessage.error('获取可选房产列表失败')
  }
}

// 房产选择变化
const handleHouseSelectionChange = (selection) => {
  selectedHouses.value = selection
}

// 确认分配房产
const handleConfirmAssign = async () => {
  // 表单验证
  if (selectedHouses.value.length === 0) {
    ElMessage.warning('请选择要分配的房产')
    return
  }

  if (assignForm.relationType === 2 && !assignForm.endDate) {
    ElMessage.warning('租户关系必须设置结束日期')
    return
  }

  assigning.value = true

  try {
    const houseIds = selectedHouses.value.map(house => house.id)
    const assignData = {
      userId: propertyOwner.value.userId,
      houseIds: houseIds,
      relationType: assignForm.relationType,
      startDate: assignForm.startDate,
      endDate: assignForm.endDate,
      isCurrent: assignForm.isCurrent
    }

    const response = await fetch('/api/v1/property/house/assign', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(assignData)
    })

    if (response.ok) {
      ElMessage.success(`成功分配 ${houseIds.length} 套房产`)
      assignPropertyDialogVisible.value = false

      // 刷新业主房产信息
      const res = await getOwner(propertyOwner.value.userId)
      propertyOwner.value = res.data

    } else {
      ElMessage.error('房产分配失败')
    }
  } catch (error) {
    ElMessage.error('房产分配失败')
  } finally {
    assigning.value = false
  }
}

// 查看账单
const handleViewBills = (row) => {
  ElMessage.info('跳转到账单管理页面')
}

// 导出业主
const handleExport = async () => {
  try {
    const response = await fetch('/api/v1/property/owner/export?' + new URLSearchParams(searchForm), {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })

    if (!response.ok) {
      throw new Error('导出失败')
    }

    // 创建下载链接
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)

    // 创建a标签并点击下载
    const link = document.createElement('a')
    link.href = url
    link.download = `业主数据_${new Date().toISOString().slice(0, 19).replace(/[:-]/g, '')}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await ownerFormRef.value.validate()
    submitting.value = true

    // 调试：打印提交的数据
    console.log('=== 提交前的表单数据 ===')
    console.log('ownerForm:', ownerForm)
    console.log('realName:', ownerForm.realName)
    console.log('userId:', ownerForm.userId)

    let response
    if (ownerForm.userId) {
      response = await updateOwner(ownerForm)
      console.log('=== 编辑接口响应 ===')
      console.log('response:', response)
    } else {
      response = await addOwner(ownerForm)
    }

    if (response.code === 200) {
      ElMessage.success(ownerForm.userId ? '编辑成功' : '新增成功')
      ownerDialogVisible.value = false
      // 延迟刷新列表，确保数据库更新完成
      setTimeout(() => {
        loadOwnerList()
      }, 500)
    } else {
      ElMessage.error(response.msg || (ownerForm.userId ? '编辑失败' : '新增失败'))
      console.error('接口返回错误:', response)
    }
  } catch (error) {
    console.error('提交表单错误:', error)
    ElMessage.error('提交失败')
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

// 房产信息样式
.house-info {
  margin-bottom: 8px;
  font-size: 13px;
  border-left: 3px solid #409eff;
  padding-left: 8px;

  &:last-child {
    margin-bottom: 0;
  }

  .house-location {
    margin-bottom: 2px;

    .building-name {
      color: #409eff;
      font-weight: 500;
      margin-right: 4px;
    }

    .unit-name {
      color: #67c23a;
      margin-right: 4px;
    }

    .house-no {
      color: #e6a23c;
    }
  }

  .house-detail {
    display: flex;
    gap: 8px;
    margin-top: 2px;

    .house-type {
      color: #606266;
      font-size: 12px;
    }

    .house-area {
      color: #909399;
      font-size: 12px;
      font-weight: 500;
    }
  }
}

.no-house {
  color: #909399;
  font-size: 13px;
  font-style: italic;
}

// 分配房产对话框样式
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.assign-property-content {
  .available-houses {
    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .relation-type-section {
    margin-top: 20px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 4px;

    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
}
</style>