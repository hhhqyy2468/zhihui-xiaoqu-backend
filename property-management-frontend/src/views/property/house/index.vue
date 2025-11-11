<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">房产管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>房产管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card total-houses">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalHouses }}</div>
              <div class="stat-label">房产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card sold-houses">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.soldHouses }}</div>
              <div class="stat-label">已售房产</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card occupied-houses">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.occupiedHouses }}</div>
              <div class="stat-label">已入住</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card occupancy-rate">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.occupancyRate }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button type="primary" @click="handleAdd" v-permission="'property:house:add'">
        <el-icon><Plus /></el-icon>
        新增房产
      </el-button>
      <el-button type="warning" @click="handleBatchDelete" :disabled="selectedRows.length === 0" v-permission="'property:house:delete'">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出Excel
      </el-button>
    </div>

    <!-- 搜索筛选 -->
      <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="queryParams"
        inline
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :span="5">
            <el-form-item label="房产编号" prop="houseCode">
              <el-input
                v-model="queryParams.houseCode"
                placeholder="请输入房产编号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="门牌号" prop="roomNum">
              <el-input
                v-model="queryParams.roomNum"
                placeholder="请输入门牌号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select
                v-model="queryParams.buildingId"
                placeholder="请选择楼栋"
                clearable
                @change="handleBuildingChange"
                style="width: 100%"
              >
                <el-option
                  v-for="item in buildingOptions"
                  :key="item.buildingId"
                  :label="item.buildingName"
                  :value="item.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="所属单元" prop="unitId">
              <el-select
                v-model="queryParams.unitId"
                placeholder="请选择单元"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in unitOptions"
                  :key="item.unitId"
                  :label="item.unitName"
                  :value="item.unitId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="房产状态" prop="houseStatus">
              <el-select
                v-model="queryParams.houseStatus"
                placeholder="请选择状态"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in houseStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24" class="search-buttons">
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
            <el-button type="success" @click="handleBatchAdd">
              <el-icon><Plus /></el-icon>
              批量生成
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="houseList"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />

        <el-table-column
          prop="houseCode"
          label="房产编号"
          width="140"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary">{{ row.houseCode }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="buildingName"
          label="楼栋名称"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="info">{{ row.buildingName }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="unitName"
          label="单元名称"
          width="100"
          align="center"
        />

        <el-table-column
          prop="roomNum"
          label="门牌号"
          width="100"
          sortable="custom"
          align="center"
        />

        <el-table-column
          prop="floorNum"
          label="楼层"
          width="80"
          align="center"
        >
          <template #default="{ row }">
            <span class="floor-cell">{{ row.floorNum }}层</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="houseType"
          label="户型"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="warning" size="small">{{ row.houseType }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="buildArea"
          label="建筑面积"
          width="120"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span class="area-cell">{{ row.buildArea }}m²</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="useArea"
          label="使用面积"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <span class="area-cell">{{ row.useArea }}m²</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="houseStatus"
          label="房产状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getHouseStatusTag(row.houseStatus)">
              {{ getHouseStatusName(row.houseStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="ownerName"
          label="产权人"
          width="120"
          show-overflow-tooltip
        />

        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
              v-permission="'property:house:edit'"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewResident(row)"
            >
              <el-icon><View /></el-icon>
              住户
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'property:house:delete'"
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
    </div>

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
        class="house-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select
                v-model="form.buildingId"
                placeholder="请选择所属楼栋"
                @change="handleFormBuildingChange"
                style="width: 100%"
              >
                <el-option
                  v-for="item in buildingOptions"
                  :key="item.buildingId"
                  :label="item.buildingName"
                  :value="item.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属单元" prop="unitId">
              <el-select
                v-model="form.unitId"
                placeholder="请选择所属单元"
                style="width: 100%"
              >
                <el-option
                  v-for="item in formUnitOptions"
                  :key="item.unitId"
                  :label="item.unitName"
                  :value="item.unitId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房产编号" prop="houseCode">
              <el-input
                v-model="form.houseCode"
                placeholder="请输入房产编号"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门牌号" prop="roomNum">
              <el-input
                v-model="form.roomNum"
                placeholder="请输入门牌号"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="楼层" prop="floorNum">
              <el-input-number
                v-model="form.floorNum"
                :min="1"
                :max="99"
                placeholder="请输入楼层"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="户型" prop="houseType">
              <el-select v-model="form.houseType" placeholder="请选择户型" style="width: 100%">
                <el-option
                  v-for="item in houseTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房产状态" prop="houseStatus">
              <el-select v-model="form.houseStatus" placeholder="请选择状态" style="width: 100%">
                <el-option
                  v-for="item in houseStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="buildArea">
              <el-input-number
                v-model="form.buildArea"
                :min="1"
                :precision="2"
                placeholder="请输入建筑面积"
                style="width: 100%"
                @change="handleBuildAreaChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用面积" prop="useArea">
              <el-input-number
                v-model="form.useArea"
                :min="1"
                :precision="2"
                placeholder="请输入使用面积"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产权人" prop="ownerName">
          <el-input
            v-model="form.ownerName"
            placeholder="请输入产权人姓名"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
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

    <!-- 查看住户对话框 -->
    <el-dialog
      v-model="residentDialogVisible"
      title="住户信息"
      width="600px"
      @close="residentDialogVisible = false"
    >
      <div class="resident-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产权人">
            <span class="info-value">{{ currentResident.ownerName || '暂无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            <span class="info-value">{{ currentResident.ownerPhone || '暂无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="身份证号">
            <span class="info-value">{{ currentResident.ownerIdCard || '暂无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="入住时间">
            <span class="info-value">{{ currentResident.checkInTime || '暂无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="住户类型">
            <el-tag :type="getResidentTypeTag(currentResident.residentType)">
              {{ getResidentTypeName(currentResident.residentType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="住户状态">
            <el-tag :type="currentResident.residentStatus === 1 ? 'success' : 'danger'">
              {{ currentResident.residentStatus === 1 ? '在住' : '已搬离' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="currentResident.familyMembers && currentResident.familyMembers.length > 0" class="family-section">
          <h4>家庭成员</h4>
          <el-table :data="currentResident.familyMembers" border size="small">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="relation" label="关系" width="100" />
            <el-table-column prop="phone" label="联系电话" />
            <el-table-column prop="idCard" label="身份证号" show-overflow-tooltip />
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="residentDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量生成对话框 -->
    <el-dialog
      v-model="batchAddDialogVisible"
      title="批量生成房产"
      width="500px"
      @close="batchAddDialogVisible = false"
    >
      <el-form
        ref="batchFormRef"
        :model="batchForm"
        :rules="batchFormRules"
        label-width="120px"
      >
        <el-form-item label="选择楼栋" prop="buildingId">
          <el-select
            v-model="batchForm.buildingId"
            placeholder="请选择楼栋"
            @change="handleBatchBuildingChange"
            style="width: 100%"
          >
            <el-option
              v-for="item in buildingOptions"
              :key="item.buildingId"
              :label="item.buildingName"
              :value="item.buildingId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择单元" prop="unitId">
          <el-select
            v-model="batchForm.unitId"
            placeholder="请选择单元"
            style="width: 100%"
          >
            <el-option
              v-for="item in batchUnitOptions"
              :key="item.unitId"
              :label="item.unitName"
              :value="item.unitId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number
            v-model="batchForm.floorCount"
            :min="1"
            :max="99"
            placeholder="请输入楼层数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="每层户数" prop="roomsPerFloor">
          <el-input-number
            v-model="batchForm.roomsPerFloor"
            :min="1"
            :max="10"
            placeholder="请输入每层户数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="默认户型" prop="defaultHouseType">
          <el-select v-model="batchForm.defaultHouseType" placeholder="请选择默认户型" style="width: 100%">
            <el-option
              v-for="item in houseTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchAddDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchAddSubmit" :loading="batchSubmitLoading">
            确定生成
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  Search,
  Refresh,
  Plus,
  Delete,
  Download,
  Edit,
  View,
  HomeFilled,
  CircleCheck,
  User,
  TrendCharts
} from '@element-plus/icons-vue'
import {
  getHouseList,
  getHouseDetail,
  addHouse,
  updateHouse,
  deleteHouse,
  batchDeleteHouse
} from '@/api/house'
import { getBuildingList } from '@/api/building'
import { getUnitsByBuilding } from '@/api/unit'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const batchFormRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const batchSubmitLoading = ref(false)
const dialogVisible = ref(false)
const residentDialogVisible = ref(false)
const batchAddDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const houseList = ref([])
const buildingOptions = ref([])
const unitOptions = ref([])
const formUnitOptions = ref([])
const batchUnitOptions = ref([])
const total = ref(0)
const currentResident = ref({})

// 统计数据
const statistics = ref({
  totalHouses: 0,
  soldHouses: 0,
  occupiedHouses: 0,
  occupancyRate: 0
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  houseCode: '',
  roomNum: '',
  buildingId: '',
  unitId: '',
  houseStatus: ''
})

// 表单数据
const form = reactive({
  houseId: null,
  buildingId: '',
  unitId: '',
  houseCode: '',
  roomNum: '',
  floorNum: 1,
  houseType: '',
  buildArea: 0,
  useArea: 0,
  houseStatus: 1,
  ownerName: '',
  remark: ''
})

// 批量生成表单数据
const batchForm = reactive({
  buildingId: '',
  unitId: '',
  floorCount: 18,
  roomsPerFloor: 3,
  defaultHouseType: '两室一厅'
})

// 选项数据
const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

const houseTypeOptions = [
  { label: '一室一厅', value: '一室一厅' },
  { label: '两室一厅', value: '两室一厅' },
  { label: '三室两厅', value: '三室两厅' },
  { label: '四室两厅', value: '四室两厅' },
  { label: '复式', value: '复式' },
  { label: '别墅', value: '别墅' }
]

// 表单验证规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitId: [
    { required: true, message: '请选择所属单元', trigger: 'change' }
  ],
  houseCode: [
    { required: true, message: '请输入房产编号', trigger: 'blur' }
  ],
  roomNum: [
    { required: true, message: '请输入门牌号', trigger: 'blur' }
  ],
  floorNum: [
    { required: true, message: '请输入楼层', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层必须大于0', trigger: 'blur' }
  ],
  houseType: [
    { required: true, message: '请选择户型', trigger: 'change' }
  ],
  buildArea: [
    { required: true, message: '请输入建筑面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '建筑面积必须大于0', trigger: 'blur' }
  ],
  useArea: [
    { required: true, message: '请输入使用面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '使用面积必须大于0', trigger: 'blur' }
  ]
}

const batchFormRules = {
  buildingId: [
    { required: true, message: '请选择楼栋', trigger: 'change' }
  ],
  unitId: [
    { required: true, message: '请选择单元', trigger: 'change' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '楼层数必须在1-99之间', trigger: 'blur' }
  ],
  roomsPerFloor: [
    { required: true, message: '请输入每层户数', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '每层户数必须在1-10之间', trigger: 'blur' }
  ],
  defaultHouseType: [
    { required: true, message: '请选择默认户型', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑房产' : '新增房产')

// 监听建筑面积变化，自动计算使用面积
watch(() => form.buildArea, (newValue) => {
  if (newValue && !form.useArea) {
    form.useArea = Math.floor(newValue * 0.8) // 默认使用面积为建筑面积的80%
  }
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取楼栋列表
const getBuildingOptions = async () => {
  try {
    const response = await getBuildingList({ pageNum: 1, pageSize: 1000 })
    const data = handleResponse(response)
    buildingOptions.value = data.list || data.records || []
  } catch (error) {
    handleError(error, '获取楼栋列表失败')
    buildingOptions.value = []
  }
}

// 获取单元列表
const getUnitOptions = async (buildingId) => {
  if (!buildingId) {
    unitOptions.value = []
    return
  }

  try {
    const response = await getUnitsByBuilding(buildingId)
    unitOptions.value = handleResponse(response)
  } catch (error) {
    handleError(error, '获取单元列表失败')
    unitOptions.value = []
  }
}

// 获取房产列表
const getHouseListData = async () => {
  loading.value = true
  try {
    const response = await getHouseList(queryParams)
    const data = handleResponse(response)
    houseList.value = data.list || data.records || []
    total.value = data.total || 0
  } catch (error) {
    handleError(error, '获取房产列表失败')
    houseList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getHouseListData()
}

// 重置搜索
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.houseCode = ''
  queryParams.roomNum = ''
  queryParams.buildingId = ''
  queryParams.unitId = ''
  queryParams.houseStatus = ''
  getHouseListData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getHouseListData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getHouseListData()
}

// 排序变化
const handleSortChange = ({ column, prop, order }) => {
  // 这里可以处理排序逻辑
  console.log('排序变化:', { column, prop, order })
  getHouseListData()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 楼栋变化处理
const handleBuildingChange = (buildingId) => {
  queryParams.unitId = ''
  getUnitOptions(buildingId)
}

// 表单中楼栋变化处理
const handleFormBuildingChange = (buildingId) => {
  form.unitId = ''
  if (buildingId) {
    getUnitOptions(buildingId).then(() => {
      formUnitOptions.value = unitOptions.value
    })
  } else {
    formUnitOptions.value = []
  }
}

// 批量生成中楼栋变化处理
const handleBatchBuildingChange = (buildingId) => {
  batchForm.unitId = ''
  if (buildingId) {
    getUnitOptions(buildingId).then(() => {
      batchUnitOptions.value = unitOptions.value
    })
  } else {
    batchUnitOptions.value = []
  }
}

// 建筑面积变化处理
const handleBuildAreaChange = (value) => {
  if (value && !form.useArea) {
    form.useArea = Math.floor(value * 0.8)
  }
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

// 获取住户类型名称
const getResidentTypeName = (type) => {
  const typeMap = {
    1: '产权人',
    2: '租户',
    3: '家庭成员'
  }
  return typeMap[type] || '未知'
}

// 获取住户类型标签
const getResidentTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return tagMap[type] || 'info'
}

// 新增房产
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑房产
const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const response = await getHouseDetail(row.houseId)
    const data = handleResponse(response)
    Object.assign(form, data)
    // 设置单元选项
    if (data.buildingId) {
      await getUnitOptions(data.buildingId)
      formUnitOptions.value = unitOptions.value
    }
    dialogVisible.value = true
  } catch (error) {
    handleError(error, '获取房产详情失败')
  }
}

// 删除房产
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除房产"${row.houseCode}"吗？删除后不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteHouse(row.houseId)
    ElMessage.success('删除成功')
    getHouseListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的房产')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个房产吗？删除后不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const houseIds = selectedRows.value.map(row => row.houseId)
    await batchDeleteHouse(houseIds)
    ElMessage.success('批量删除成功')
    getHouseListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '批量删除失败')
    }
  }
}

// 批量生成
const handleBatchAdd = () => {
  batchAddDialogVisible.value = true
}

// 批量生成提交
const handleBatchAddSubmit = async () => {
  if (!batchFormRef.value) return

  try {
    await batchFormRef.value.validate()
    batchSubmitLoading.value = true

    // 这里应该调用批量生成的API
    // await batchGenerateHouses(batchForm)

    // 模拟API调用
    setTimeout(() => {
      ElMessage.success(`成功生成 ${batchForm.floorCount * batchForm.roomsPerFloor} 个房产`)
      batchAddDialogVisible.value = false
      getHouseListData()
      batchSubmitLoading.value = false
    }, 2000)
  } catch (error) {
    handleError(error, '批量生成失败')
    batchSubmitLoading.value = false
  }
}

// 查看住户
const handleViewResident = (row) => {
  // 模拟住户数据
  currentResident.value = {
    ownerName: row.ownerName || '暂无',
    ownerPhone: row.ownerName ? '138****' + Math.floor(Math.random() * 10000) : '暂无',
    ownerIdCard: row.ownerName ? '110****' + Math.floor(Math.random() * 1000000000000) : '暂无',
    checkInTime: row.houseStatus > 1 ? '2024-01-15' : '暂无',
    residentType: row.houseStatus === 4 ? 1 : 2, // 自住为产权人，其他为租户
    residentStatus: row.houseStatus > 1 ? 1 : 0,
    familyMembers: row.houseStatus > 1 ? [
      {
        name: row.ownerName || '张三',
        relation: '户主',
        phone: '138****' + Math.floor(Math.random() * 10000),
        idCard: '110****' + Math.floor(Math.random() * 1000000000000)
      },
      {
        name: '李四',
        relation: '配偶',
        phone: '139****' + Math.floor(Math.random() * 10000),
        idCard: '110****' + Math.floor(Math.random() * 1000000000000)
      }
    ] : []
  }
  residentDialogVisible.value = true
}

// 导出Excel
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateHouse(form)
      ElMessage.success('编辑成功')
    } else {
      await addHouse(form)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    getHouseListData()
  } catch (error) {
    handleError(error, isEdit.value ? '编辑失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    houseId: null,
    buildingId: '',
    unitId: '',
    houseCode: '',
    roomNum: '',
    floorNum: 1,
    houseType: '',
    buildArea: 0,
    useArea: 0,
    houseStatus: 1,
    ownerName: '',
    remark: ''
  })
  formUnitOptions.value = []
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 组件挂载
onMounted(() => {
  getBuildingOptions()
  getHouseListData()
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
    text-align: center;
  }
}

// 统计卡片
.statistics-cards {
  margin-bottom: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px;
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      font-size: 24px;
      color: white;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }

    &.total-houses {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.sold-houses {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.occupied-houses {
      background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.occupancy-rate {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 搜索区域样式
.search-section {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;

  .el-form-item {
    margin-bottom: 0;
  }
}

.data-table {
  margin-bottom: 16px;

  .floor-cell {
    font-weight: 600;
    color: #409eff;
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

.house-form {
  .el-form-item {
    margin-bottom: 18px;
  }
}

.resident-info {
  .info-value {
    font-weight: 500;
    color: #303133;
  }

  .family-section {
    margin-top: 20px;

    h4 {
      margin-bottom: 12px;
      color: #303133;
      font-size: 16px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
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

// 卡片样式优化（保留统计卡片）
.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

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

// 描述列表样式优化
:deep(.el-descriptions) {
  .el-descriptions__label {
    font-weight: 600;
    color: #606266;
  }
}
</style>