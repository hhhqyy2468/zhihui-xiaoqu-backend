<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">公告发布</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item>公告发布</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="公告标题">
          <el-input
            v-model="searchForm.noticeTitle"
            placeholder="请输入公告标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select
            v-model="searchForm.noticeType"
            placeholder="请选择公告类型"
            clearable
            style="width: 150px"
          >
            <el-option label="通知公告" value="notice" />
            <el-option label="停水停电" value="outage" />
            <el-option label="社区活动" value="activity" />
            <el-option label="政策通知" value="policy" />
            <el-option label="温馨提示" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告状态">
          <el-select
            v-model="searchForm.noticeStatus"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已发布" value="1" />
            <el-option label="已过期" value="2" />
            <el-option label="已撤回" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 240px"
          />
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
        发布公告
      </el-button>
      <el-button
        type="success"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出公告
      </el-button>
    </div>

    <!-- 公告表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="noticeList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="noticeTitle" label="公告标题" width="200" show-overflow-tooltip />
        <el-table-column prop="noticeType" label="公告类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getNoticeTypeColor(row.noticeType)">
              {{ getNoticeTypeName(row.noticeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="noticeStatus" label="公告状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getNoticeStatusColor(row.noticeStatus)">
              {{ getNoticeStatusName(row.noticeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="是否置顶" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="warning">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="publishScope" label="发布范围" width="150">
          <template #default="{ row }">
            <el-tooltip
              :content="getPublishScopeTooltip(row)"
              placement="top"
              :disabled="row.publishScope === 1"
            >
              <div class="publish-scope-cell">
                <el-tag :type="getPublishScopeColor(row.publishScope)" size="small">
                  {{ getPublishScopeFullName(row.publishScope) }}
                </el-tag>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="readCount" label="阅读次数" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="effectiveEndTime" label="有效期至" width="180">
          <template #default="{ row }">
            {{ row.effectiveEndTime ? formatDateTime(row.effectiveEndTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="currentUser.userType === 1 || currentUser.userType === 2"
              link
              type="warning"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="(currentUser.userType === 1 || currentUser.userType === 2) && row.noticeStatus === 1"
              link
              type="info"
              @click="handleWithdraw(row)"
            >
              撤回
            </el-button>
            <el-button
              v-if="currentUser.userType === 1 || currentUser.userType === 2"
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

    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择公告类型">
            <el-option label="通知公告" value="notice" />
            <el-option label="停水停电" value="outage" />
            <el-option label="社区活动" value="activity" />
            <el-option label="政策通知" value="policy" />
            <el-option label="温馨提示" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <el-input
            v-model="form.noticeContent"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="form.isTop" />
        </el-form-item>
        <el-form-item label="发布范围" prop="publishScope">
          <el-radio-group v-model="form.publishScope" @change="handlePublishScopeChange">
            <el-radio :value="1">全部</el-radio>
            <el-radio :value="2">指定范围</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 指定范围选择区域 -->
        <template v-if="form.publishScope === 2">
          <el-form-item label="目标楼栋" prop="targetBuildingIds">
            <el-select
              v-model="form.targetBuildingIds"
              placeholder="请选择楼栋（可选择多个）"
              multiple
              style="width: 100%"
              @change="handleBuildingChange"
            >
              <el-option
                v-for="building in buildingList"
                :key="building.id"
                :label="building.buildingName"
                :value="building.id.toString()"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="目标单元" prop="targetUnitIds">
            <el-select
              v-model="form.targetUnitIds"
              placeholder="请选择单元（可选择多个，需先选择对应楼栋）"
              multiple
              style="width: 100%"
              :disabled="form.targetBuildingIds.length === 0"
            >
              <el-option
                v-for="unit in availableUnits"
                :key="unit.id"
                :label="getUnitDisplayName(unit)"
                :value="unit.id.toString()"
              />
            </el-select>
            <div v-if="form.targetBuildingIds.length === 0" style="color: #999; font-size: 12px; margin-top: 5px;">
              请先选择楼栋，然后可以选择对应楼栋的单元
            </div>
            <div v-else-if="availableUnits.length === 0" style="color: #999; font-size: 12px; margin-top: 5px;">
              所选楼栋暂无单元数据
            </div>
          </el-form-item>
        </template>
        <el-form-item label="生效时间" prop="effectiveStartTime">
          <el-date-picker
            v-model="form.effectiveStartTime"
            type="datetime"
            placeholder="选择生效开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="失效时间" prop="effectiveEndTime">
          <el-date-picker
            v-model="form.effectiveEndTime"
            type="datetime"
            placeholder="选择失效时间（可选）"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ dialogTitle.includes('编辑') ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="公告详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题">{{ currentNotice.noticeTitle }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="getNoticeTypeColor(currentNotice.noticeType)">
            {{ getNoticeTypeName(currentNotice.noticeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ currentNotice.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="发布状态">
          <el-tag :type="getNoticeStatusColor(currentNotice.noticeStatus)">
            {{ getNoticeStatusName(currentNotice.noticeStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布范围">
          <div class="scope-detail-info">
            <el-tag :type="getPublishScopeColor(currentNotice.publishScope)" size="default">
              {{ getPublishScopeFullName(currentNotice.publishScope) }}
            </el-tag>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="范围详情" v-if="currentNotice.publishScope === 2">
          <div class="scope-compact-info">
            <!-- 指定楼栋 -->
            <div v-if="currentNotice.targetBuildingIds" class="scope-compact-section">
              <div class="scope-compact-header">
                <el-icon><House /></el-icon>
                <span>楼栋 {{ currentNotice.targetBuildingIds.split(',').length }}个</span>
              </div>
              <div class="scope-compact-items">
                <span v-for="buildingId in currentNotice.targetBuildingIds.split(',')" :key="buildingId" class="scope-compact-tag">
                  {{ getBuildingName(buildingId.trim()) }}
                </span>
              </div>
            </div>

            <!-- 指定单元 -->
            <div v-if="currentNotice.targetUnitIds" class="scope-compact-section">
              <div class="scope-compact-header">
                <el-icon><OfficeBuilding /></el-icon>
                <span>单元 {{ currentNotice.targetUnitIds.split(',').length }}个</span>
              </div>
              <div class="scope-compact-items">
                <span v-for="unitId in currentNotice.targetUnitIds.split(',')" :key="unitId" class="scope-compact-tag">
                  {{ getUnitDisplayNameForNotice(unitId.trim()) }}
                </span>
              </div>
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="是否置顶">
          {{ currentNotice.isTop ? '置顶' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="阅读次数">{{ currentNotice.readCount }}次</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDateTime(currentNotice.publishTime) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">
          {{ currentNotice.effectiveEndTime ? formatDateTime(currentNotice.effectiveEndTime) : '永久有效' }}
        </el-descriptions-item>
        </el-descriptions>

      <div class="content-section">
        <h4 class="section-title">
          <el-icon class="section-icon"><Document /></el-icon>
          公告内容
        </h4>
        <div class="notice-content-card">
          <div class="notice-content">{{ currentNotice.noticeContent }}</div>
        </div>
      </div>

      <div v-if="currentNotice.attachmentUrls" class="content-section">
        <h4 class="section-title">
          <el-icon class="section-icon"><Paperclip /></el-icon>
          附件列表
        </h4>
        <div class="attachment-container">
          <div
            v-for="(attachment, index) in getAttachments(currentNotice.attachmentUrls)"
            :key="index"
            class="attachment-item"
          >
            <div class="attachment-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="attachment-info">
              <span class="attachment-name">{{ attachment }}</span>
              <span class="attachment-size">未知大小</span>
            </div>
            <el-button
              class="attachment-download"
              type="primary"
              size="small"
              @click="handleDownload(attachment)"
            >
              <el-icon><Download /></el-icon>
              下载
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Download,
  Document,
  House,
  OfficeBuilding,
  Paperclip
} from '@element-plus/icons-vue'
import {
  getNoticeList,
  getNoticeDetail,
  addNotice,
  updateNotice,
  deleteNotice,
  publishNotice,
  setNoticeTop,
  withdrawNotice,
  getNoticeStats,
  getBuildingList,
  getUnitList
} from '@/api/notice'
import { getUserInfo } from '@/api/auth'
import { getHousesByUserId } from '@/api/userHouse'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('发布公告')

// 当前用户信息
const currentUser = ref({
  userType: null,
  userId: null,
  username: '',
  realName: ''
})

// 用户房产信息（用于业主过滤公告）
const userHouses = ref([])

// 表单数据
const formRef = ref()
const form = reactive({
  id: null,
  noticeTitle: '',
  noticeType: 'notice',
  noticeContent: '',
  isTop: false,
  publishScope: 1,
  targetBuildingIds: [],
  targetUnitIds: [],
  effectiveStartTime: null,
  effectiveEndTime: null,
  attachmentUrls: ''
})

// 搜索表单
const searchForm = reactive({
  noticeTitle: '',
  noticeType: '',
  noticeStatus: '',
  dateRange: []
})

// 公告数据
const noticeList = ref([])
const selectedNotices = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentNotice = ref({})

// 楼栋和单元数据
const buildingList = ref([])
const unitList = ref([])
const availableUnits = ref([]) // 当前可选的单元列表

// 表单验证规则
const rules = {
  noticeTitle: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, max: 200, message: '公告标题长度在5到200个字符', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  noticeContent: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, message: '公告内容不能少于10个字符', trigger: 'blur' }
  ]
}

// 获取公告类型名称
const getNoticeTypeName = (type) => {
  const typeMap = {
    'notice': '通知公告',
    'outage': '停水停电',
    'activity': '社区活动',
    'policy': '政策通知',
    'reminder': '温馨提示'
  }
  return typeMap[type] || '未知'
}

// 获取公告类型颜色
const getNoticeTypeColor = (type) => {
  const colorMap = {
    'notice': 'primary',
    'outage': 'warning',
    'activity': 'success',
    'policy': 'info',
    'reminder': 'info'
  }
  return colorMap[type] || 'info'
}

// 获取公告状态名称
const getNoticeStatusName = (status) => {
  const statusMap = {
    1: '已发布',
    2: '已过期',
    3: '已撤回'
  }
  return statusMap[status] || '未知'
}

// 获取公告状态颜色
const getNoticeStatusColor = (status) => {
  const colorMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return colorMap[status] || 'info'
}

// 获取发布范围名称
const getPublishScopeName = (scope) => {
  const scopeMap = {
    1: '全部',
    2: '指定范围'
  }
  return scopeMap[scope] || '未知'
}

// 获取发布范围全名称
const getPublishScopeFullName = (scope) => {
  const scopeMap = {
    1: '面向全部用户',
    2: '指定发布范围'
  }
  return scopeMap[scope] || '未知范围'
}

// 获取发布范围颜色
const getPublishScopeColor = (scope) => {
  const colorMap = {
    1: 'primary',
    2: 'warning',
    3: 'success'
  }
  return colorMap[scope] || 'info'
}

// 获取发布范围详情
const getPublishScopeDetails = (notice) => {
  if (notice.publishScope === 2) {
    let details = []
    if (notice.targetBuildingIds && notice.targetBuildingIds.trim()) {
      const buildingIds = notice.targetBuildingIds.split(',').filter(id => id.trim())
      details.push(`${buildingIds.length}个楼栋`)
    }
    if (notice.targetUnitIds && notice.targetUnitIds.trim()) {
      const unitIds = notice.targetUnitIds.split(',').filter(id => id.trim())
      details.push(`${unitIds.length}个单元`)
    }
    return details.length > 0 ? `指定范围 (${details.join('、')})` : '指定范围'
  }
  return '-'
}

// 获取单元显示名称
const getUnitDisplayName = (unit) => {
  if (unit.buildingName) {
    let unitIdentifier = unit.unitName || unit.unitNo || '位置'
    if (unitIdentifier === '位置') {
      unitIdentifier = `${unit.id}单元`
    }
    return `${unit.buildingName} - ${unitIdentifier}`
  }
  return `${unit.id}单元`
}


// 获取发布范围详细信息（用于tooltip）
const getPublishScopeTooltip = (notice) => {
  if (notice.publishScope === 1) {
    return '面向所有用户发布'
  } else if (notice.publishScope === 2) {
    const details = []

    // 获取楼栋详细信息
    if (notice.targetBuildingIds && notice.targetBuildingIds.trim()) {
      const buildingIds = notice.targetBuildingIds.split(',').filter(id => id.trim())
      if (buildingIds.length > 0) {
        const buildingNames = getBuildingNamesSync(buildingIds)
        details.push(`指定楼栋：${buildingNames.join('、')}`)
      }
    }

    // 获取单元详细信息
    if (notice.targetUnitIds && notice.targetUnitIds.trim()) {
      const unitIds = notice.targetUnitIds.split(',').filter(id => id.trim())
      if (unitIds.length > 0) {
        const unitNames = getUnitNamesSync(unitIds)
        details.push(`指定单元：${unitNames.join('、')}`)
      }
    }

    return details.length > 0 ? details.join('\n') : '指定范围'
  }
  return '未知范围'
}

// 获取楼栋名称列表（同步版本）
const getBuildingNamesSync = (buildingIds) => {
  try {
    // 从已加载的楼栋列表中查找
    const names = buildingIds.map(id => {
      const building = buildingList.value.find(b => b.id.toString() === id.toString())
      return building ? building.buildingName : `楼栋${id}`
    })
    return names
  } catch (error) {
    console.error('获取楼栋名称失败:', error)
    return buildingIds.map(id => `楼栋${id}`)
  }
}

// 获取单元名称列表（同步版本）
const getUnitNamesSync = (unitIds) => {
  try {
    // 从已加载的单元列表中查找
    const names = unitIds.map(id => {
      const unit = availableUnits.value.find(u => u.id.toString() === id.toString())
      if (unit) {
        // 优先使用单元自带的buildingName属性
        const buildingName = unit.buildingName || (
          buildingList.value.find(b => b.id.toString() === unit.buildingId?.toString())?.buildingName
        )

        if (buildingName) {
          // 从单元名称中提取单元编号（例如："1单元" -> "1"，或者"1" -> "1"）
          let unitIdentifier = unit.unitName || unit.unitNo || unit.id.toString()
          // 提取单元中的数字部分
          const unitNumberMatch = unitIdentifier.match(/(\d+)/);
          const unitNumber = unitNumberMatch ? unitNumberMatch[1] : unitIdentifier;
          // 构建标准的单元标识符："X单元"
          unitIdentifier = `${unitNumber}单元`

          // 从楼栋名称中提取楼栋编号部分（例如："幸福苑1号楼" -> "1号楼"）
          let finalBuildingName = buildingName
          if (buildingName.includes('号楼')) {
            // 提取最后的楼栋编号（包括"号楼"）
            const buildingNumberMatch = buildingName.match(/(\d+号楼)$/);
            if (buildingNumberMatch) {
              finalBuildingName = buildingNumberMatch[1];
            } else {
              // 如果匹配不到，直接使用原名称
              finalBuildingName = buildingName;
            }
          }

          return `${finalBuildingName}${unitIdentifier}`
        }

        // 没有找到对应楼栋的情况，只显示单元信息
        let unitIdentifier = unit.unitName || unit.unitNo || unit.id.toString()
        // 提取单元中的数字部分
        const unitNumberMatch = unitIdentifier.match(/(\d+)/);
        const unitNumber = unitNumberMatch ? unitNumberMatch[1] : unitIdentifier;
        // 构建标准的单元标识符："X单元"
        unitIdentifier = `${unitNumber}单元`
        return unitIdentifier
      }
      // 找不到单元信息时的默认显示
      return `${id}单元`
    })
    return names
  } catch (error) {
    console.error('获取单元名称失败:', error)
    return unitIds.map(id => `${id}单元`)
  }
}

// 获取单个楼栋名称（用于详情显示）
const getBuildingName = (buildingId) => {
  const building = buildingList.value.find(b => b.id.toString() === buildingId.toString())
  return building ? building.buildingName : `楼栋${buildingId}`
}

// 获取单个单元显示名称（用于详情显示）
const getUnitDisplayNameForNotice = (unitId) => {
  const unit = availableUnits.value.find(u => u.id.toString() === unitId.toString())
  if (unit) {
    // 优先使用单元自带的buildingName属性
    const buildingName = unit.buildingName || (
      buildingList.value.find(b => b.id.toString() === unit.buildingId?.toString())?.buildingName
    )

    if (buildingName) {
      // 从单元名称中提取单元编号（例如："1单元" -> "1"，或者"1" -> "1"）
      let unitIdentifier = unit.unitName || unit.unitNo || unit.id.toString()
      // 提取单元中的数字部分
      const unitNumberMatch = unitIdentifier.match(/(\d+)/);
      const unitNumber = unitNumberMatch ? unitNumberMatch[1] : unitIdentifier;
      // 构建标准的单元标识符："X单元"
      unitIdentifier = `${unitNumber}单元`

      // 从楼栋名称中提取楼栋编号部分（例如："幸福苑1号楼" -> "1号楼"）
      let finalBuildingName = buildingName
      if (buildingName.includes('号楼')) {
        // 提取最后的楼栋编号（包括"号楼"）
        const buildingNumberMatch = buildingName.match(/(\d+号楼)$/);
        if (buildingNumberMatch) {
          finalBuildingName = buildingNumberMatch[1];
        } else {
          // 如果匹配不到，直接使用原名称
          finalBuildingName = buildingName;
        }
      }

      return `${finalBuildingName}${unitIdentifier}`
    }

    // 没有找到对应楼栋的情况，只显示单元信息
    let unitIdentifier = unit.unitName || unit.unitNo || unit.id.toString()
    // 提取单元中的数字部分
    const unitNumberMatch = unitIdentifier.match(/(\d+)/);
    const unitNumber = unitNumberMatch ? unitNumberMatch[1] : unitIdentifier;
    // 构建标准的单元标识符："X单元"
    unitIdentifier = `${unitNumber}单元`
    return unitIdentifier
  }
  // 找不到单元信息时的默认显示
  return `${unitId}单元`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 解析附件URL
const getAttachments = (attachmentUrls) => {
  if (!attachmentUrls) return []
  return attachmentUrls.split(',').map(url => url.trim()).filter(url => url)
}

// 加载公告数据
const loadNotices = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 只添加有值的搜索条件
    if (searchForm.noticeTitle && searchForm.noticeTitle.trim()) {
      params.noticeTitle = searchForm.noticeTitle.trim()
    }
    if (searchForm.noticeType && searchForm.noticeType.trim()) {
      params.noticeType = searchForm.noticeType.trim()
    }
    if (searchForm.noticeStatus !== '' && searchForm.noticeStatus !== null && searchForm.noticeStatus !== undefined) {
      params.noticeStatus = searchForm.noticeStatus
    }

    // 处理日期范围
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.beginTime = searchForm.dateRange[0]
      params.endTime = searchForm.dateRange[1]
    }

    console.log('发送的查询参数:', params)
    const response = await getNoticeList(params)
    console.log('公告列表响应:', response)
    if (response.code === 200 && response.data) {
      console.log('原始数据记录:', response.data.rows)
      console.log('数据总数:', response.data.total)

      let allNotices = response.data.rows || []

      // 如果是业主，需要根据发布范围过滤公告
      if (currentUser.value.userType === 3 && userHouses.value.length > 0) {
        allNotices = filterNoticesByUserHouses(allNotices)
        console.log('过滤后的公告数量:', allNotices.length)
      }

      noticeList.value = allNotices
      total.value = response.data.total || 0
      console.log('设置后的noticeList:', noticeList.value)
      console.log('noticeList长度:', noticeList.value.length)
    } else {
      console.log('API响应错误:', response)
      ElMessage.error(response.msg || '获取公告列表失败')
      noticeList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载公告数据失败:', error)
    ElMessage.error('加载公告数据失败')
    noticeList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载楼栋数据
const loadBuildings = async () => {
  try {
    const response = await getBuildingList()
    console.log('楼栋数据响应:', response)
    if (response.code === 200 && response.data) {
      // 检查数据结构，可能是直接返回数组还是有records属性
      if (Array.isArray(response.data)) {
        buildingList.value = response.data
      } else if (response.data.records && Array.isArray(response.data.records)) {
        buildingList.value = response.data.records
      } else if (response.data.rows && Array.isArray(response.data.rows)) {
        buildingList.value = response.data.rows
      } else {
        buildingList.value = []
      }
      console.log('处理后的楼栋列表:', buildingList.value)
      console.log('楼栋列表长度:', buildingList.value.length)
    } else {
      console.log('响应数据格式不正确:', response)
    }
  } catch (error) {
    console.error('加载楼栋数据失败:', error)
    buildingList.value = []
  }
}

// 加载单元数据
const loadUnits = async (buildingId) => {
  try {
    console.log(`=== 加载单元数据 buildingId: ${buildingId} ===`)
    console.log(`当前buildingList:`, buildingList.value.map(b => ({id: b.id, name: b.buildingName})))

    const response = await getUnitList(buildingId)
    console.log('单元数据响应:', response)
    if (response.code === 200 && response.data) {
      // 新API直接返回数组数据
      if (Array.isArray(response.data)) {
        // 为每个单元添加楼栋信息，便于显示
        const unitsWithBuildingInfo = response.data.map(unit => {
          console.log(`处理单元:`, unit)
          const building = buildingList.value.find(b => b.id.toString() === unit.buildingId?.toString())
          console.log(`找到的楼栋:`, building)
          return {
            ...unit,
            buildingName: building ? building.buildingName : '未知楼栋'
          }
        })

        // 将新加载的单元添加到availableUnits中，避免重复
        const existingIds = availableUnits.value.map(u => u.id)
        const newUnits = unitsWithBuildingInfo.filter(u => !existingIds.includes(u.id))
        availableUnits.value.push(...newUnits)

        // 如果某个楼栋被取消选择，移除对应的单元
        const selectedBuildingIds = form.targetBuildingIds
        availableUnits.value = availableUnits.value.filter(unit => {
          return selectedBuildingIds.some(buildingId => {
            const building = buildingList.value.find(b => b.id.toString() === buildingId.toString())
            return building && building.buildingName === unit.buildingName
          })
        })
      }
      console.log('处理后的可用单元列表:', availableUnits.value)
    }
  } catch (error) {
    console.error('加载单元数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadNotices()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    noticeTitle: '',
    noticeType: '',
    noticeStatus: '',
    dateRange: []
  })
  handleSearch()
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedNotices.value = selection
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '发布公告'
  Object.assign(form, {
    id: null,
    noticeTitle: '',
    noticeType: 'notice',
    noticeContent: '',
    isTop: false,
    publishScope: 1,
    targetBuildingIds: [],
    targetUnitIds: [],
    effectiveStartTime: null,
    effectiveEndTime: null,
    attachmentUrls: ''
  })
  dialogVisible.value = true
}

// 查看
const handleView = async (row) => {
  try {
    const response = await getNoticeDetail(row.id)
    if (response.code === 200 && response.data) {
      currentNotice.value = response.data

      // 确保楼栋数据已加载
      if (buildingList.value.length === 0) {
        await loadBuildings()
      }

      // 如果公告有指定单元，确保加载对应的单元数据
      if (response.data.targetUnitIds) {
        const unitIds = response.data.targetUnitIds.split(',').filter(id => id.trim())
        if (unitIds.length > 0) {
          // 检查是否需要加载单元数据
          const needToLoadUnits = unitIds.some(unitId =>
            !availableUnits.value.find(u => u.id.toString() === unitId.toString())
          )

          if (needToLoadUnits) {
            // 从公告的targetBuildingIds中获取涉及到的楼栋ID
            let buildingIds = []
            if (response.data.targetBuildingIds) {
              buildingIds = response.data.targetBuildingIds.split(',').filter(id => id.trim())
            }

            if (buildingIds.length > 0) {
              await loadUnitsForBuildings(buildingIds)
            } else {
              // 如果没有指定楼栋，则加载所有楼栋的单元数据
              const allBuildingIds = buildingList.value.map(b => b.id.toString())
              await loadUnitsForBuildings(allBuildingIds)
            }
          }
        }
      }

      detailVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取公告详情失败')
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}

// 编辑
const handleEdit = async (row) => {
  try {
    const response = await getNoticeDetail(row.id)
    if (response.code === 200 && response.data) {
      dialogTitle.value = '编辑公告'
      Object.assign(form, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取公告详情失败')
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}

// 撤回
const handleWithdraw = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要撤回公告"${row.noticeTitle}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await withdrawNotice(row.id)
    if (response.code === 200) {
      ElMessage.success('公告撤回成功')
      loadNotices()
    } else {
      ElMessage.error(response.msg || '公告撤回失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('公告撤回失败:', error)
      ElMessage.error('公告撤回失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告"${row.noticeTitle}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteNotice(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadNotices()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 下载附件
const handleDownload = (attachment) => {
  ElMessage.info(`下载文件: ${attachment}`)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 处理数据格式
    const formData = { ...form }

    // 设置公告状态为已发布（1）
    formData.noticeStatus = 1

    // 处理布尔值转换为数字
    formData.isTop = formData.isTop ? 1 : 0

    // 将数组转换为逗号分隔的字符串
    if (Array.isArray(formData.targetBuildingIds) && formData.targetBuildingIds.length > 0) {
      formData.targetBuildingIds = formData.targetBuildingIds.join(',')
    } else {
      formData.targetBuildingIds = ''
    }

    if (Array.isArray(formData.targetUnitIds) && formData.targetUnitIds.length > 0) {
      formData.targetUnitIds = formData.targetUnitIds.join(',')
    } else {
      formData.targetUnitIds = ''
    }

    console.log('提交的表单数据:', formData)

    let response
    if (formData.id) {
      // 编辑
      response = await updateNotice(formData)
    } else {
      // 新增
      response = await addNotice(formData)
    }

    if (response.code === 200) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadNotices()
    } else {
      ElMessage.error(response.msg || (formData.id ? '更新公告失败' : '发布公告失败'))
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('提交失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadNotices()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadNotices()
}

// 监听发布范围变化
const handlePublishScopeChange = (value) => {
  console.log('发布范围变化:', value)
  if (value === 2) {
    // 选择指定范围时，确保楼栋数据已加载
    if (buildingList.value.length === 0) {
      loadBuildings()
    }
  }

  // 清空之前的选择
  form.targetBuildingIds = []
  form.targetUnitIds = []
  availableUnits.value = []
}

// 处理楼栋选择变化
const handleBuildingChange = async (buildingIds) => {
  console.log('楼栋选择变化:', buildingIds)

  // 清空当前选择的单元（因为楼栋变化了）
  form.targetUnitIds = []

  if (buildingIds && buildingIds.length > 0) {
    // 加载所有选中楼栋的单元数据
    await loadUnitsForBuildings(buildingIds)
  } else {
    // 清空楼栋选择时，清空单元数据
    availableUnits.value = []
  }
}


// 加载指定楼栋的单元数据
const loadUnitsForBuildings = async (buildingIds) => {
  try {
    if (buildingIds && buildingIds.length > 0) {
      console.log('开始加载选中楼栋的单元数据:', buildingIds)

      // 并行加载所有选中楼栋的单元数据
      const unitPromises = buildingIds.map(buildingId => loadUnits(buildingId))
      await Promise.all(unitPromises)

      console.log('所有楼栋的单元数据加载完成')
    } else {
      availableUnits.value = []
    }
  } catch (error) {
    console.error('加载指定楼栋单元数据失败:', error)
    availableUnits.value = []
  }
}

// 获取当前用户信息
const getCurrentUser = () => {
  // 从存储中获取用户信息，优先尝试从多个可能的key中获取
  const userInfoStr = localStorage.getItem('userInfo') ||
                      sessionStorage.getItem('userInfo') ||
                      localStorage.getItem('user')

  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      currentUser.value = {
        userType: userInfo.userType || userInfo.user_type || 1,
        userId: userInfo.userId || userInfo.user_id || userInfo.id,
        username: userInfo.username || userInfo.userName,
        realName: userInfo.realName || userInfo.real_name || userInfo.name
      }
      console.log('从storage获取用户信息成功:', currentUser.value)
      return
    } catch (error) {
      console.error('解析storage用户信息失败:', error)
    }
  }

  // 尝试从JWT token解析
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 尝试解析JWT payload
      const parts = token.split('.')
      if (parts.length === 3) {
        // 处理可能缺少padding的情况
        let payload = parts[1]
        while (payload.length % 4) {
          payload += '='
        }
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
        const payloadObj = JSON.parse(decoded)
        currentUser.value = {
          userType: payloadObj.userType || payloadObj.user_type || 1,
          userId: payloadObj.userId || payloadObj.user_id || payloadObj.sub,
          username: payloadObj.sub || payloadObj.username,
          realName: payloadObj.realName || payloadObj.real_name || payloadObj.name
        }
        console.log('从JWT获取用户信息成功:', currentUser.value)
        return
      }
    } catch (error) {
      console.error('解析JWT用户信息失败:', error)
    }
  }

  // 默认设置为系统管理员（最安全的默认值）
  currentUser.value = { userType: 1 }
  console.log('使用默认用户信息:', currentUser.value)
}

// 获取用户房产信息（主要用于业主）
const loadUserHouses = async () => {
  if (currentUser.value.userType === 3 && currentUser.value.userId) {
    try {
      const response = await getHousesByUserId(currentUser.value.userId)
      console.log('用户房产信息响应:', response)
      if (response.code === 200 && response.data) {
        userHouses.value = Array.isArray(response.data) ? response.data : response.data.records || []
        console.log('用户房产列表:', userHouses.value)

        // 输出每个房产的详细信息，用于调试
        userHouses.value.forEach((house, index) => {
          console.log(`房产 ${index + 1}:`, {
            id: house.id,
            buildingId: house.buildingId,
            buildingIdStr: house.building_id,
            unitId: house.unitId,
            unitIdStr: house.unit_id,
            houseNo: house.houseNo,
            houseNoStr: house.house_no
          })
        })
      }
    } catch (error) {
      console.error('获取用户房产信息失败:', error)
    }
  }
}

// 根据用户房产过滤公告
const filterNoticesByUserHouses = (notices) => {
  console.log('=== 开始过滤公告 ===')
  console.log('当前用户类型:', currentUser.value.userType)
  console.log('用户房产数量:', userHouses.value.length)
  console.log('原始公告数量:', notices.length)

  return notices.filter(notice => {
    console.log('\n--- 过滤公告 ---')
    console.log('公告标题:', notice.noticeTitle)
    console.log('公告发布范围(publishScope):', notice.publishScope)
    console.log('目标楼栋IDs:', notice.targetBuildingIds)
    console.log('目标单元IDs:', notice.targetUnitIds)

    // 如果是全局公告，所有人都可见
    if (notice.publishScope === 1) {
      console.log('✓ 全局公告，显示')
      return true
    }

    // 如果是指定范围，检查是否与用户房产匹配
    if (notice.publishScope === 2) {
      const isMatch = checkNoticeMatchesUserHouses(notice)
      console.log('指定范围公告匹配结果:', isMatch ? '✓ 显示' : '✗ 隐藏')
      return isMatch
    }

    console.log('未知发布范围，默认显示')
    return true // 默认显示
  })
}

// 检查公告是否匹配用户的房产
const checkNoticeMatchesUserHouses = (notice) => {
  console.log('=== 检查公告匹配用户房产 ===')
  console.log('公告信息:', {
    title: notice.noticeTitle,
    publishScope: notice.publishScope,
    targetBuildingIds: notice.targetBuildingIds,
    targetUnitIds: notice.targetUnitIds
  })

  console.log('用户房产详细信息:', userHouses.value.map(house => ({
    id: house.id,
    buildingId: house.buildingId,
    buildingIdStr: house.buildingIdStr,
    buildingName: house.buildingName,
    unitId: house.unitId,
    unitIdStr: house.unitIdStr,
    unitName: house.unitName,
    houseNo: house.houseNo,
    houseCode: house.houseCode,
    fullObject: house
  })))

  // 获取用户的楼栋ID和单元ID列表
  const userBuildingIds = userHouses.value.map(house => {
    const buildingId = house.buildingId
    console.log('提取楼栋ID:', buildingId, '房屋:', house.houseNo, '楼栋名称:', house.buildingName)
    return parseInt(buildingId)
  }).filter(id => !isNaN(id))

  const userUnitIds = userHouses.value.map(house => {
    const unitId = house.unitId
    console.log('提取单元ID:', unitId, '房屋:', house.houseNo, '单元名称:', house.unitName)
    return parseInt(unitId)
  }).filter(id => !isNaN(id))

  console.log('用户楼栋ID列表:', userBuildingIds)
  console.log('用户单元ID列表:', userUnitIds)

  // 如果公告有目标楼栋限制，检查是否匹配
  if (notice.targetBuildingIds && notice.targetBuildingIds.toString().trim()) {
    const noticeBuildingIds = Array.isArray(notice.targetBuildingIds)
      ? notice.targetBuildingIds.map(id => parseInt(id)).filter(id => !isNaN(id))
      : notice.targetBuildingIds.toString().split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))

    console.log('公告目标楼栋ID列表:', noticeBuildingIds)

    const hasMatchingBuilding = noticeBuildingIds.some(buildingId =>
      userBuildingIds.includes(buildingId)
    )

    console.log('楼栋匹配结果:', hasMatchingBuilding)

    if (!hasMatchingBuilding) {
      console.log('楼栋不匹配，返回false')
      return false
    }

    // 如果公告还有目标单元限制，进一步检查
    if (notice.targetUnitIds && notice.targetUnitIds.toString().trim()) {
      const noticeUnitIds = Array.isArray(notice.targetUnitIds)
        ? notice.targetUnitIds.map(id => parseInt(id)).filter(id => !isNaN(id))
        : notice.targetUnitIds.toString().split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))

      console.log('公告目标单元ID列表:', noticeUnitIds)

      const hasMatchingUnit = noticeUnitIds.some(unitId =>
        userUnitIds.includes(unitId)
      )

      console.log('单元匹配结果:', hasMatchingUnit)

      if (!hasMatchingUnit) {
        console.log('单元不匹配，返回false')
        return false
      }
    }

    console.log('匹配成功，返回true')
    return true
  }

  console.log('公告没有目标限制，返回true')
  return true
}

// 初始化
onMounted(() => {
  getCurrentUser()
  loadBuildings()
  loadNotices()
  loadUserHouses() // 加载用户房产信息
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

.notice-content {
  line-height: 1.8;
  color: #303133;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
  padding: 16px;
  background: #fafbfc;
  border-radius: 6px;
  border: 1px solid #e8eaed;
}

.content-section {
  margin-top: 24px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    padding-bottom: 8px;
    border-bottom: 2px solid #409eff;

    .section-icon {
      color: #409eff;
      font-size: 18px;
    }
  }

  .notice-content-card {
    background: #ffffff;
    border: 1px solid #e8eaed;
    border-radius: 8px;
    padding: 0;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-1px);
    }
  }
}

.attachment-container {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .attachment-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #ffffff;
    border: 1px solid #e8eaed;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      background: #f0f9ff;
      border-color: #bfdbfe;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }

    .attachment-icon {
      width: 40px;
      height: 40px;
      background: #f0f9ff;
      border: 1px solid #bfdbfe;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;

      .el-icon {
        color: #409eff;
        font-size: 20px;
      }
    }

    .attachment-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .attachment-name {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
      }

      .attachment-size {
        font-size: 12px;
        color: #909399;
      }
    }

    .attachment-download {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      background: #409eff;
      border: none;
      border-radius: 6px;
      color: white;
      font-size: 13px;
      transition: all 0.3s ease;

      &:hover {
        background: #337ecc;
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }

      .el-icon {
        font-size: 14px;
      }
    }
  }
}

// 发布范围显示样式
.publish-scope-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;

  .scope-detail {
    font-size: 12px;
    color: #909399;
    font-weight: 500;
  }

  &:hover {
    .scope-detail {
      color: #409eff;
    }
  }
}

// 详情弹窗中的范围显示样式
.scope-detail-info {
  width: 100%;
}

// 紧凑版范围显示样式
.scope-compact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .scope-compact-section {
    .scope-compact-header {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 6px;
      font-size: 13px;
      font-weight: 600;
      color: #303133;

      .el-icon {
        color: #409eff;
        font-size: 14px;
      }
    }

    .scope-compact-items {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;

      .scope-compact-tag {
        display: inline-block;
        padding: 2px 6px;
        background: #f0f9ff;
        border: 1px solid #bfdbfe;
        border-radius: 4px;
        font-size: 11px;
        color: #1e40af;
        font-weight: 500;
        transition: all 0.2s ease;
        max-width: 120px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        &:hover {
          background: #dbeafe;
          border-color: #93c5fd;
          transform: translateY(-1px);
        }
      }
    }
  }
}
</style>