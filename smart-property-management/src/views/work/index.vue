<template>
  <div class="work-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的工作台</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card stat-pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon size="36"><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.pendingCount ?? 0 }}</div>
              <div class="stat-label">待接单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-processing" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon size="36"><Tools /></el-icon></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.processingCount ?? 0 }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-pending-accept" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon size="36"><Checked /></el-icon></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.pendingAcceptCount ?? 0 }}</div>
              <div class="stat-label">待验收/进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-completed" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon size="36"><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.completedCount ?? 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工单列表 -->
    <el-card class="list-card" shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待接单" name="2" />
        <el-tab-pane label="进行中" name="3" />
        <el-tab-pane label="待验收" name="4" />
        <el-tab-pane label="已完成" name="5" />
      </el-tabs>

      <el-table
        v-loading="loading"
        :data="orderList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="工单号" width="160" />
        <el-table-column prop="houseNo" label="房间号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="报修人" width="100" />
        <el-table-column prop="createTime" label="报修时间" width="160" />
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)" size="small">
              {{ getStatusLabel(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="openDetailDrawer(row)">详情</el-button>
            <el-button
              v-if="row.orderStatus === 2"
              type="primary"
              size="small"
              @click="handleAccept(row)"
            >接单</el-button>
            <el-button
              v-if="row.orderStatus === 3"
              type="success"
              size="small"
              @click="openCompleteDialog(row)"
            >完成处理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </el-card>

    <!-- 完成处理 Dialog -->
    <el-dialog
      v-model="completeDialogVisible"
      title="完成处理"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form :model="completeForm" :rules="completeRules" ref="completeFormRef" label-width="90px">
        <el-form-item label="处理结果" prop="repairResult">
          <el-input
            v-model="completeForm.repairResult"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleComplete">确认提交</el-button>
      </template>
    </el-dialog>
  <!-- 工单详情抽屉 -->
  <el-drawer v-model="detailDrawerVisible" title="工单详情" size="520px" direction="rtl">
    <template v-if="detailOrder">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="工单号">{{ detailOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ detailOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailOrder.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailOrder.houseNo }}</el-descriptions-item>
        <el-descriptions-item label="维修类型">{{ getRepairTypeName(detailOrder.repairType) }}</el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="detailOrder.urgencyLevel === 3 ? 'danger' : detailOrder.urgencyLevel === 2 ? 'warning' : 'info'" size="small">
            {{ detailOrder.urgencyLevel === 3 ? '紧急' : detailOrder.urgencyLevel === 2 ? '较急' : '一般' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障描述">{{ detailOrder.faultDescription }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">
          <span style="color:#e74c3c;font-weight:bold">¥{{ detailOrder.repairCost ? Number(detailOrder.repairCost).toFixed(2) : '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailOrder.orderStatus)" size="small">{{ getStatusLabel(detailOrder.orderStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ detailOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="派工时间" v-if="detailOrder.assignTime">{{ detailOrder.assignTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" v-if="detailOrder.finishTime">{{ detailOrder.finishTime }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="detailOrder.imageUrls" style="margin-top:16px">
        <div style="font-weight:600;margin-bottom:8px">报修图片</div>
        <div style="display:flex;flex-wrap:wrap;gap:8px">
          <el-image
            v-for="(url, i) in parseImages(detailOrder.imageUrls)"
            :key="i"
            :src="getImageUrl(url)"
            :preview-src-list="parseImages(detailOrder.imageUrls).map(getImageUrl)"
            style="width:100px;height:100px;object-fit:cover;border-radius:4px"
            fit="cover"
          />
        </div>
      </div>

      <div v-if="detailOrder.repairImageUrls" style="margin-top:16px">
        <div style="font-weight:600;margin-bottom:8px">维修后图片</div>
        <div style="display:flex;flex-wrap:wrap;gap:8px">
          <el-image
            v-for="(url, i) in parseImages(detailOrder.repairImageUrls)"
            :key="i"
            :src="getImageUrl(url)"
            :preview-src-list="parseImages(detailOrder.repairImageUrls).map(getImageUrl)"
            style="width:100px;height:100px;object-fit:cover;border-radius:4px"
            fit="cover"
          />
        </div>
      </div>

      <div v-if="detailOrder.repairContent" style="margin-top:16px">
        <div style="font-weight:600;margin-bottom:8px">维修内容</div>
        <div style="color:#606266">{{ detailOrder.repairContent }}</div>
      </div>
    </template>
  </el-drawer>
</div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Clock, Tools, Checked, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

// 路由路径 -> tab name 映射
// status: 1=待派工 2=已派工(待接单) 3=进行中 4=待验收 5=已完成
const routeTabMap = {
  'pending': '2',
  'processing': '3',
  'pending-accept': '4',
  'completed': '5'
}
// tab name -> 路由路径 映射
const tabRouteMap = {
  'all': '/work/pending',
  '2': '/work/pending',
  '3': '/work/processing',
  '4': '/work/pending-accept',
  '5': '/work/completed'
}

// 统计数据
const stats = reactive({
  pendingCount: 0,
  processingCount: 0,
  pendingAcceptCount: 0,
  completedCount: 0
})

// 标签页 - 根据当前路由初始化
const getTabFromRoute = () => routeTabMap[route.path.split('/').pop()] ?? 'all'
const activeTab = ref(getTabFromRoute())

// 列表数据
const loading = ref(false)
const orderList = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 详情抽屉
const detailDrawerVisible = ref(false)
const detailOrder = ref(null)

const BASE_URL = (import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api/v1').replace(/\/api\/v1$/, '')

function openDetailDrawer(row) {
  detailOrder.value = row
  detailDrawerVisible.value = true
}

function parseImages(str) {
  if (!str) return []
  try {
    const arr = JSON.parse(str)
    return Array.isArray(arr) ? arr : [str]
  } catch {
    return str.split(',').filter(Boolean)
  }
}

function getImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_URL + url
}

const repairTypeMap = {
  water_electric: '水电维修',
  door_window: '门窗维修',
  elevator: '电梯维修',
  public_facility: '公共设施',
  other: '其他维修',
  appliance: '家电维修',
  plumbing: '管道维修',
  painting: '油漆粉刷'
}
function getRepairTypeName(type) {
  return repairTypeMap[type] || type || '未知'
}

// 完成处理弹窗
const completeDialogVisible = ref(false)
const submitting = ref(false)
const completeFormRef = ref(null)
const currentOrder = ref(null)
const completeForm = reactive({
  repairResult: ''
})
const completeRules = {
  repairResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
}

// 状态映射（后端：1=待派工 2=已派工 3=进行中 4=待验收 5=已完成）
const statusMap = {
  1: { label: '待派工', type: 'warning' },
  2: { label: '已派工', type: 'primary' },
  3: { label: '进行中', type: 'primary' },
  4: { label: '待验收', type: 'info' },
  5: { label: '已完成', type: 'success' }
}

function getStatusLabel(status) {
  return statusMap[status]?.label ?? '未知'
}
function getStatusType(status) {
  return statusMap[status]?.type ?? 'info'
}

// 加载统计
async function loadStats() {
  try {
    const res = await request({ url: '/workbench/stats', method: 'get' })
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载工单列表
async function loadOrders() {
  loading.value = true
  try {
    const repairStatus = activeTab.value === 'all' ? undefined : Number(activeTab.value)
    const res = await request({
      url: '/workbench/my-orders',
      method: 'get',
      params: {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        repairStatus
      }
    })
    const data = res.data
    if (data) {
      // 后端返回直接列表（非分页对象）
      if (Array.isArray(data)) {
        console.log('[Work] 第一条工单数据:', JSON.stringify(data[0]))
        orderList.value = data
        pagination.total = data.length
      } else {
        orderList.value = data.records ?? data.list ?? []
        pagination.total = data.total ?? 0
      }
    }
  } catch (e) {
    console.error('加载工单失败', e)
  } finally {
    loading.value = false
  }
}

// 监听 activeTab 变化 -> 同步路由 + 加载数据
watch(activeTab, (tab) => {
  pagination.pageNum = 1
  const targetPath = tabRouteMap[tab] || '/work/pending'
  if (route.path !== targetPath) {
    router.replace(targetPath)
  }
  loadOrders()
})

// 监听路由变化 -> 同步 tab（侧边栏点击触发）
watch(() => route.path, (newPath) => {
  const seg = newPath.split('/').pop()
  const tab = routeTabMap[seg] ?? 'all'
  if (activeTab.value !== tab) {
    activeTab.value = tab
  }
})

function handleTabChange() {
  // 由 watch(activeTab) 处理，此处留空
}

// 接单
async function handleAccept(row) {
  try {
    await ElMessageBox.confirm(`确认接单「${row.orderNo}」？`, '接单确认', { type: 'warning' })
    await request({ url: `/workbench/order/${row.id}/accept`, method: 'post' })
    ElMessage.success('接单成功')
    loadStats()
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('接单失败')
  }
}

// 打开完成处理弹窗
function openCompleteDialog(row) {
  currentOrder.value = row
  completeForm.repairResult = ''
  completeDialogVisible.value = true
}

// 提交完成处理
async function handleComplete() {
  if (!completeFormRef.value) return
  await completeFormRef.value.validate()
  submitting.value = true
  try {
    await request({
      url: `/workbench/order/${currentOrder.value.id}/complete`,
      method: 'post',
      data: { faultReason: completeForm.repairResult }
    })
    ElMessage.success('处理完成')
    completeDialogVisible.value = false
    loadStats()
    loadOrders()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadStats()
  loadOrders()
})
</script>

<style scoped>
.work-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  border-radius: 8px;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 4px 0;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.stat-pending .stat-icon { background: #e6a23c; }
.stat-processing .stat-icon { background: #409eff; }
.stat-pending-accept .stat-icon { background: #909399; }
.stat-completed .stat-icon { background: #67c23a; }
.stat-num {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}
.list-card {
  border-radius: 8px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
