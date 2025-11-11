<template>
  <div class="log-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">单元管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>单元管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card total-units">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUnits }}</div>
              <div class="stat-label">单元总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
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
      <el-button type="primary" @click="handleAdd" v-permission="'property:unit:add'">
        <el-icon><Plus /></el-icon>
        新增单元
      </el-button>
      <el-button type="warning" @click="handleBatchDelete" :disabled="selectedRows.length === 0" v-permission="'property:unit:delete'">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出Excel
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="queryParams"
        inline
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="单元编号" prop="unitCode">
              <el-input
                v-model="queryParams.unitCode"
                placeholder="请输入单元编号"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单元名称" prop="unitName">
              <el-input
                v-model="queryParams.unitName"
                placeholder="请输入单元名称"
                clearable
                prefix-icon="Search"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select
                v-model="queryParams.buildingId"
                placeholder="请选择楼栋"
                clearable
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
          <el-col :span="6">
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
    </div>

    <!-- 数据表格 -->
    <div class="table-section">

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="unitList"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />

        <el-table-column
          prop="unitCode"
          label="单元编号"
          width="140"
          sortable="custom"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary">{{ row.unitCode }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="unitName"
          label="单元名称"
          width="120"
          sortable="custom"
          align="center"
        />

        <el-table-column
          prop="buildingName"
          label="所属楼栋"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="info">{{ row.buildingName }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="floorCount"
          label="楼层数"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <span class="floor-cell">{{ row.floorCount }}层</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="roomsPerFloor"
          label="每层户数"
          width="120"
          align="center"
        />

        <el-table-column
          prop="totalRooms"
          label="总户数"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <span class="number-cell">{{ row.totalRooms || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="occupiedRooms"
          label="已入住"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <span class="occupied-cell">{{ row.occupiedRooms || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="occupancyRate"
          label="入住率"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <div class="occupancy-rate">
              <el-progress
                :percentage="row.occupancyRate || 0"
                :color="getOccupancyRateColor(row.occupancyRate || 0)"
                :stroke-width="8"
                text-inside
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="remark"
          label="备注"
          min-width="150"
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
              v-permission="'property:unit:edit'"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewHouses(row)"
            >
              <el-icon><View /></el-icon>
              房产
            </el-button>
            <el-button
              link
              type="success"
              @click="handleViewStats(row)"
            >
              <el-icon><DataLine /></el-icon>
              统计
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
              v-permission="'property:unit:delete'"
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
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        class="unit-form"
      >
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select
            v-model="form.buildingId"
            placeholder="请选择所属楼栋"
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

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单元编号" prop="unitCode">
              <el-input
                v-model="form.unitCode"
                placeholder="请输入单元编号（如：A01-1）"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单元名称" prop="unitName">
              <el-input
                v-model="form.unitName"
                placeholder="请输入单元名称（如：1单元）"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层数" prop="floorCount">
              <el-input-number
                v-model="form.floorCount"
                :min="1"
                :max="99"
                placeholder="请输入楼层数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每层户数" prop="roomsPerFloor">
              <el-input-number
                v-model="form.roomsPerFloor"
                :min="1"
                :max="10"
                placeholder="请输入每层户数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="单元类型" prop="unitType">
          <el-select v-model="form.unitType" placeholder="请选择单元类型" style="width: 100%">
            <el-option label="住宅单元" value="住宅" />
            <el-option label="商业单元" value="商业" />
            <el-option label="办公单元" value="办公" />
            <el-option label="综合单元" value="综合" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息，如：一梯三户，南北通透"
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
      title="房产列表"
      width="1000px"
      @close="houseDialogVisible = false"
    >
      <div class="house-list-content">
        <div class="unit-info">
          <el-tag type="primary">{{ currentUnit.unitName }}</el-tag>
          <span class="unit-code">{{ currentUnit.unitCode }}</span>
          <span class="building-name">{{ currentUnit.buildingName }}</span>
          <el-tag type="info">总户数: {{ currentUnit.totalRooms }}</el-tag>
          <el-tag type="success">已入住: {{ currentUnit.occupiedRooms }}</el-tag>
        </div>

        <el-table :data="houseList" border stripe v-loading="houseLoading">
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column prop="houseCode" label="房产编号" width="140" align="center">
            <template #default="{ row }">
              <el-tag type="primary" size="small">{{ row.houseCode }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="roomNum" label="门牌号" width="100" align="center" />
          <el-table-column prop="floorNum" label="楼层" width="80" align="center" />
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
          <el-table-column prop="ownerName" label="产权人" width="120" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              <span class="time-cell">{{ formatDateTime(row.createTime) }}</span>
            </template>
          </el-table-column>
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

    <!-- 统计信息对话框 -->
    <el-dialog
      v-model="statsDialogVisible"
      title="单元统计信息"
      width="600px"
      @close="statsDialogVisible = false"
    >
      <div class="stats-content">
        <div class="stats-header">
          <h3>{{ currentUnit.unitName }} ({{ currentUnit.unitCode }})</h3>
          <p>{{ currentUnit.buildingName }}</p>
        </div>

        <el-row :gutter="20" class="stats-cards">
          <el-col :span="8">
            <el-card class="stats-card">
              <div class="stats-item">
                <div class="stats-value">{{ currentUnit.totalRooms || 0 }}</div>
                <div class="stats-label">总户数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stats-card">
              <div class="stats-item">
                <div class="stats-value occupied">{{ currentUnit.occupiedRooms || 0 }}</div>
                <div class="stats-label">已入住</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stats-card">
              <div class="stats-item">
                <div class="stats-value vacant">{{ (currentUnit.totalRooms || 0) - (currentUnit.occupiedRooms || 0) }}</div>
                <div class="stats-label">空置</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="occupancy-chart">
          <h4>入住率分析</h4>
          <el-progress
            type="dashboard"
            :percentage="currentUnit.occupancyRate || 0"
            :color="getOccupancyRateColor(currentUnit.occupancyRate || 0)"
            :width="200"
          >
            <template #default="{ percentage }">
              <span class="percentage-value">{{ percentage }}%</span>
              <span class="percentage-label">入住率</span>
            </template>
          </el-progress>
        </div>

        <div class="house-status-chart">
          <h4>房产状态分布</h4>
          <el-row :gutter="20">
            <el-col :span="6" v-for="status in houseStatusStats" :key="status.value">
              <div class="status-item">
                <el-tag :type="getHouseStatusTag(status.value)" size="large">
                  {{ status.count }}
                </el-tag>
                <span class="status-label">{{ status.label }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statsDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  OfficeBuilding,
  Search,
  Refresh,
  Plus,
  Delete,
  Download,
  Edit,
  View,
  DataLine,
  Grid,
  HomeFilled,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import {
  getUnitList,
  getUnitDetail,
  addUnit,
  updateUnit,
  deleteUnit,
  batchDeleteUnit,
  getUnitStats
} from '@/api/unit'
import { getBuildingList } from '@/api/building'
import { getHousesByUnit } from '@/api/house'
import { handleResponse, handleError } from '@/utils/response'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const houseLoading = ref(false)
const dialogVisible = ref(false)
const houseDialogVisible = ref(false)
const statsDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const unitList = ref([])
const buildingOptions = ref([])
const houseList = ref([])
const total = ref(0)
const currentUnit = ref({})
const houseStatusStats = ref([])

// 统计数据
const statistics = ref({
  totalUnits: 0,
  totalHouses: 0,
  occupiedHouses: 0,
  occupancyRate: 0
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  unitCode: '',
  unitName: '',
  buildingId: ''
})

// 表单数据
const form = reactive({
  unitId: null,
  buildingId: '',
  unitCode: '',
  unitName: '',
  floorCount: 1,
  roomsPerFloor: 3,
  unitType: '住宅',
  remark: ''
})

// 房产状态选项
const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

// 表单验证规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitCode: [
    { required: true, message: '请输入单元编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '单元编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  unitName: [
    { required: true, message: '请输入单元名称', trigger: 'blur' },
    { min: 2, max: 20, message: '单元名称长度在2到20个字符', trigger: 'blur' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '楼层数必须在1-99之间', trigger: 'blur' }
  ],
  roomsPerFloor: [
    { required: true, message: '请输入每层户数', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '每层户数必须在1-10之间', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑单元' : '新增单元')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取入住率颜色
const getOccupancyRateColor = (rate) => {
  if (rate >= 90) return '#67c23a'
  if (rate >= 70) return '#e6a23c'
  return '#f56c6c'
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
const getUnitListData = async () => {
  loading.value = true
  try {
    const response = await getUnitList(queryParams)
    const data = handleResponse(response)
    unitList.value = data.list || data.records || []
    total.value = data.total || 0
  } catch (error) {
    handleError(error, '获取单元列表失败')
    unitList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取单元的房产列表
const getHouseListByUnit = async (unitId) => {
  houseLoading.value = true
  try {
    const response = await getHousesByUnit(unitId)
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
  getUnitListData()
}

// 重置搜索
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.unitCode = ''
  queryParams.unitName = ''
  queryParams.buildingId = ''
  getUnitListData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getUnitListData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getUnitListData()
}

// 排序变化
const handleSortChange = ({ column, prop, order }) => {
  // 这里可以处理排序逻辑
  console.log('排序变化:', { column, prop, order })
  getUnitListData()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增单元
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑单元
const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const response = await getUnitDetail(row.unitId)
    const data = handleResponse(response)
    Object.assign(form, data)
    dialogVisible.value = true
  } catch (error) {
    handleError(error, '获取单元详情失败')
  }
}

// 删除单元
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除单元"${row.unitName}"吗？删除后不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteUnit(row.unitId)
    ElMessage.success('删除成功')
    getUnitListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的单元')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个单元吗？删除后不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const unitIds = selectedRows.value.map(row => row.unitId)
    await batchDeleteUnit(unitIds)
    ElMessage.success('批量删除成功')
    getUnitListData()
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '批量删除失败')
    }
  }
}

// 查看房产
const handleViewHouses = async (row) => {
  currentUnit.value = row
  houseDialogVisible.value = true
  await getHouseListByUnit(row.unitId)
}

// 查看统计
const handleViewStats = async (row) => {
  currentUnit.value = row
  try {
    const response = await getUnitStats(row.unitId)
    const data = handleResponse(response)

    // 设置房产状态统计
    houseStatusStats.value = [
      { label: '空置', value: 1, count: data.vacantCount || 0 },
      { label: '已售', value: 2, count: data.soldCount || 0 },
      { label: '已租', value: 3, count: data.rentedCount || 0 },
      { label: '自住', value: 4, count: data.ownerOccupiedCount || 0 }
    ]
  } catch (error) {
    handleError(error, '获取统计信息失败')
    // 设置默认统计数据
    houseStatusStats.value = [
      { label: '空置', value: 1, count: 0 },
      { label: '已售', value: 2, count: 0 },
      { label: '已租', value: 3, count: 0 },
      { label: '自住', value: 4, count: 0 }
    ]
  }
  statsDialogVisible.value = true
}

// 查看房产详情
const handleViewHouseDetail = (row) => {
  ElMessage.info(`查看房产${row.houseCode}的详细信息`)
  // 这里可以跳转到房产详情页面
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
      await updateUnit(form)
      ElMessage.success('编辑成功')
    } else {
      await addUnit(form)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    getUnitListData()
  } catch (error) {
    handleError(error, isEdit.value ? '编辑失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    unitId: null,
    buildingId: '',
    unitCode: '',
    unitName: '',
    floorCount: 1,
    roomsPerFloor: 3,
    unitType: '住宅',
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
  getBuildingOptions()
  getUnitListData()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

// 页面头部
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .page-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

// 操作按钮区域
.action-section {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

// 搜索区域
.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .el-form-item {
    margin-bottom: 0;
  }
}

// 表格区域
.table-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
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

    &.total-units {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .stat-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &.total-houses {
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


// 表格内部样式
.table-section {
  .el-table {
    margin-bottom: 16px;

    .number-cell {
      font-weight: 600;
      color: #409eff;
    }

    .time-cell {
      font-size: 13px;
      color: #909399;
    }

    .floor-cell {
      font-weight: 600;
      color: #409eff;
    }

    .occupied-cell {
      font-weight: 600;
      color: #e6a23c;
    }

    .occupancy-rate {
      .el-progress {
        .el-progress__text {
          font-size: 12px;
          font-weight: 600;
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 16px 0;
  }
}

  .unit-form {
  .el-form-item {
    margin-bottom: 18px;
  }
}

.house-list-content {
  .unit-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding: 12px;
    background: #f5f7fa;
    border-radius: 4px;

    .unit-code {
      font-weight: 600;
      color: #303133;
    }

    .building-name {
      color: #666;
    }
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

.stats-content {
  .stats-header {
    text-align: center;
    margin-bottom: 24px;

    h3 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 20px;
    }

    p {
      margin: 0;
      color: #666;
    }
  }

  .stats-cards {
    margin-bottom: 32px;

    .stats-card {
      text-align: center;

      .stats-item {
        padding: 16px;

        .stats-value {
          font-size: 32px;
          font-weight: bold;
          color: #409eff;
          margin-bottom: 8px;

          &.occupied {
            color: #67c23a;
          }

          &.vacant {
            color: #f56c6c;
          }
        }

        .stats-label {
          color: #666;
          font-size: 14px;
        }
      }
    }
  }

  .occupancy-chart {
    text-align: center;
    margin-bottom: 32px;

    h4 {
      margin-bottom: 20px;
      color: #303133;
    }

    .percentage-value {
      display: block;
      font-size: 28px;
      font-weight: bold;
      color: #409eff;
    }

    .percentage-label {
      display: block;
      font-size: 14px;
      color: #666;
      margin-top: 4px;
    }
  }

  .house-status-chart {
    h4 {
      margin-bottom: 16px;
      color: #303133;
    }

    .status-item {
      text-align: center;
      margin-bottom: 12px;

      .el-tag {
        margin-bottom: 8px;
        font-size: 20px;
        font-weight: bold;
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .status-label {
        display: block;
        color: #666;
        font-size: 14px;
      }
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

// 进度条样式优化
:deep(.el-progress) {
  .el-progress-bar__outer {
    border-radius: 4px;
  }

  .el-progress-bar__inner {
    border-radius: 4px;
  }
}
</style>