<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon building-icon"><el-icon :size="32"><OfficeBuilding /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number">{{ statsData.buildingCount ?? '-' }}</div>
                <div class="stats-label">总楼栋数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon house-icon"><el-icon :size="32"><House /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number">{{ statsData.houseCount ?? '-' }}</div>
                <div class="stats-label">总房产数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon user-icon"><el-icon :size="32"><User /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number">{{ statsData.userCount ?? '-' }}</div>
                <div class="stats-label">总用户数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon login-icon"><el-icon :size="32"><Odometer /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number">{{ statsData.todayLoginCount ?? '-' }}</div>
                <div class="stats-label">今日登录数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 第二行补充卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card stats-card-sm" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon repair-icon"><el-icon :size="28"><Tools /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number-sm">{{ statsData.pendingRepairs ?? '-' }}</div>
                <div class="stats-label">待处理维修</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card stats-card-sm" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon log-icon"><el-icon :size="28"><Document /></el-icon></div>
              <div class="stats-info">
                <div class="stats-number-sm">{{ statsData.logCount ?? '-' }}</div>
                <div class="stats-label">操作日志总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="stats-card stats-card-sm" shadow="hover">
            <div class="stats-content" style="gap:16px;">
              <el-icon :size="18" color="#409EFF"><InfoFilled /></el-icon>
              <span style="font-size:13px;color:#606266;flex:1;">数据实时从后端API获取</span>
              <el-button size="small" type="primary" :loading="statsLoading" @click="loadAllData">
                <el-icon><Refresh /></el-icon> 刷新全部
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域第一行 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>各楼栋房产入住率</span>
                <el-button size="small" :loading="occupancyLoading" @click="loadOccupancyData"><el-icon><Refresh /></el-icon></el-button>
              </div>
            </template>
            <div v-if="occupancyLoading" class="chart-loading"><el-icon class="is-loading" :size="28"><Loading /></el-icon><span>加载中...</span></div>
            <div v-else ref="occupancyChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>近7天登录趋势</span>
                <el-button size="small" :loading="loginTrendLoading" @click="loadLoginTrend"><el-icon><Refresh /></el-icon></el-button>
              </div>
            </template>
            <div v-if="loginTrendLoading" class="chart-loading"><el-icon class="is-loading" :size="28"><Loading /></el-icon><span>加载中...</span></div>
            <div v-else ref="loginTrendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top:20px;">
        <el-col :span="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>维修工单状态分布</span>
                <el-button size="small" :loading="repairLoading" @click="loadRepairData"><el-icon><Refresh /></el-icon></el-button>
              </div>
            </template>
            <div v-if="repairLoading" class="chart-loading"><el-icon class="is-loading" :size="28"><Loading /></el-icon><span>加载中...</span></div>
            <div v-else ref="repairChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>账单缴费情况</span>
                <el-button size="small" :loading="billLoading" @click="loadBillData"><el-icon><Refresh /></el-icon></el-button>
              </div>
            </template>
            <div v-if="billLoading" class="chart-loading"><el-icon class="is-loading" :size="28"><Loading /></el-icon><span>加载中...</span></div>
            <div v-else ref="billChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>停车位使用情况</span>
                <el-button size="small" :loading="parkingLoading" @click="loadParkingData"><el-icon><Refresh /></el-icon></el-button>
              </div>
            </template>
            <div v-if="parkingLoading" class="chart-loading"><el-icon class="is-loading" :size="28"><Loading /></el-icon><span>加载中...</span></div>
            <div v-else ref="parkingChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  OfficeBuilding, House, User, Refresh, Odometer,
  Tools, Document, InfoFilled, Loading
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getAdminStats } from '@/api/dashboard'
import { listBuildings } from '@/api/building'
import { listHouses } from '@/api/house'
import { getRepairOrderPage } from '@/api/repair'
import { getAllBills } from '@/api/bill'
import { getParkingSpaceStats } from '@/api/parkingSpace'
import { listLoginLog } from '@/api/log'

// refs
const occupancyChartRef = ref()
const loginTrendChartRef = ref()
const repairChartRef = ref()
const billChartRef = ref()
const parkingChartRef = ref()

// loading状态
const statsLoading = ref(false)
const occupancyLoading = ref(false)
const loginTrendLoading = ref(false)
const repairLoading = ref(false)
const billLoading = ref(false)
const parkingLoading = ref(false)

// 统计数据
const statsData = ref({
  buildingCount: null,
  houseCount: null,
  userCount: null,
  todayLoginCount: null,
  pendingRepairs: null,
  logCount: null
})

// 图表实例
let occupancyChart = null
let loginTrendChart = null
let repairChart = null
let billChart = null
let parkingChart = null

// 加载顶部统计卡片
const loadStats = async () => {
  statsLoading.value = true
  try {
    const res = await getAdminStats()
    if (res && res.data) {
      statsData.value = { ...statsData.value, ...res.data }
    }
  } catch (e) {
    ElMessage.warning('统计数据加载失败')
  } finally {
    statsLoading.value = false
  }
}

// 各楼栋入住率（柱状图）
const loadOccupancyData = async () => {
  occupancyLoading.value = true
  try {
    const buildRes = await listBuildings({ pageNum: 1, pageSize: 100 })
    const buildings = buildRes?.data?.records || buildRes?.data?.list || buildRes?.data || []
    if (!Array.isArray(buildings) || !buildings.length) {
      renderOccupancyChart([], [], [], [])
      return
    }
    const names = [], rates = [], totalArr = [], occupiedArr = []
    for (const b of buildings) {
      const houseRes = await listHouses({ buildingId: b.id, pageNum: 1, pageSize: 1000 })
      const houses = houseRes?.data?.records || houseRes?.data?.list || houseRes?.data || []
      const total = houses.length
      const occupied = houses.filter(h => h.houseStatus === 2).length
      const rate = total > 0 ? Math.round(occupied / total * 100) : 0
      names.push(b.buildingName || b.name || `楼栋${b.id}`)
      rates.push(rate)
      totalArr.push(total)
      occupiedArr.push(occupied)
    }
    renderOccupancyChart(names, rates, totalArr, occupiedArr)
  } catch (e) {
    ElMessage.warning('楼栋入住率加载失败')
  } finally {
    occupancyLoading.value = false
  }
}

const renderOccupancyChart = (names, rates, totalArr, occupiedArr) => {
  nextTick(() => {
    if (!occupancyChartRef.value) return
    if (occupancyChart) occupancyChart.dispose()
    occupancyChart = echarts.init(occupancyChartRef.value)
    occupancyChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params) => {
          const i = params[0].dataIndex
          return `${names[i]}<br/>入住率: ${params[0].value}%<br/>已入住: ${occupiedArr[i]} / ${totalArr[i]} 套`
        }
      },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: names },
      yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
      series: [{
        name: '入住率', type: 'bar', barMaxWidth: 60,
        itemStyle: {
          color: (params) => ['#5470C6','#91CC75','#FAC858','#EE6666','#73C0DE','#3BA272'][params.dataIndex % 6],
          borderRadius: [4, 4, 0, 0]
        },
        label: { show: true, position: 'top', formatter: '{c}%' },
        data: rates
      }]
    })
  })
}

// 近7天登录趋势（折线图）
const loadLoginTrend = async () => {
  loginTrendLoading.value = true
  try {
    const days = [], counts = [], successCounts = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      const dateStr = d.toISOString().slice(0, 10)
      days.push(dateStr.slice(5))
      const beginTime = dateStr + ' 00:00:00'
      const endTime = dateStr + ' 23:59:59'
      try {
        const res = await listLoginLog({ pageNum: 1, pageSize: 1, beginTime, endTime })
        const total = res?.data?.total ?? (Array.isArray(res?.data) ? res.data.length : 0)
        const successRes = await listLoginLog({ pageNum: 1, pageSize: 1, beginTime, endTime, status: 0 })
        const success = successRes?.data?.total ?? (Array.isArray(successRes?.data) ? successRes.data.length : 0)
        counts.push(total)
        successCounts.push(success)
      } catch {
        counts.push(0)
        successCounts.push(0)
      }
    }
    renderLoginTrendChart(days, counts, successCounts)
  } catch (e) {
    ElMessage.warning('登录趋势加载失败')
  } finally {
    loginTrendLoading.value = false
  }
}

const renderLoginTrendChart = (days, counts, successCounts) => {
  nextTick(() => {
    if (!loginTrendChartRef.value) return
    if (loginTrendChart) loginTrendChart.dispose()
    loginTrendChart = echarts.init(loginTrendChartRef.value)
    loginTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['总登录', '成功登录'], top: 8 },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: days },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '总登录', type: 'line', smooth: true,
          itemStyle: { color: '#409EFF' },
          areaStyle: { color: 'rgba(64,158,255,0.1)' },
          data: counts
        },
        {
          name: '成功登录', type: 'line', smooth: true,
          itemStyle: { color: '#67C23A' },
          areaStyle: { color: 'rgba(103,194,58,0.1)' },
          data: successCounts
        }
      ]
    })
  })
}

// 维修工单状态分布（饼图）
// 状态: 1=待派工 2=待接单 3=进行中 4=已完成 5=已关闭
const loadRepairData = async () => {
  repairLoading.value = true
  try {
    const statusList = [
      { value: 1, name: '待派工', color: '#F56C6C' },
      { value: 2, name: '待接单', color: '#E6A23C' },
      { value: 3, name: '进行中', color: '#409EFF' },
      { value: 4, name: '已完成', color: '#67C23A' },
      { value: 5, name: '已关闭', color: '#909399' }
    ]
    const pieData = []
    for (const s of statusList) {
      const res = await getRepairOrderPage({ pageNum: 1, pageSize: 1, orderStatus: s.value })
      const cnt = res?.data?.total ?? (Array.isArray(res?.data) ? res.data.length : 0)
      if (cnt > 0) pieData.push({ value: cnt, name: s.name, itemStyle: { color: s.color } })
    }
    renderRepairChart(pieData)
  } catch (e) {
    ElMessage.warning('维修工单数据加载失败')
  } finally {
    repairLoading.value = false
  }
}

const renderRepairChart = (pieData) => {
  nextTick(() => {
    if (!repairChartRef.value) return
    if (repairChart) repairChart.dispose()
    repairChart = echarts.init(repairChartRef.value)
    repairChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left', top: 'center' },
      series: [{
        name: '工单状态', type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: pieData.length ? pieData : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }]
      }]
    })
  })
}

// 账单缴费情况（柱状图）
// billStatus: 1=待缴费 2=已缴费 3=逾期
const loadBillData = async () => {
  billLoading.value = true
  try {
    const statusList = [
      { value: 1, name: '待缴费', color: '#E6A23C' },
      { value: 2, name: '已缴费', color: '#67C23A' },
      { value: 3, name: '逾期', color: '#F56C6C' }
    ]
    const names = statusList.map(s => s.name)
    const values = []
    const colors = statusList.map(s => s.color)
    for (const s of statusList) {
      const res = await getAllBills({ billStatus: s.value })
      const cnt = res?.data?.total ?? (Array.isArray(res?.data) ? res.data.length : 0)
      values.push(cnt)
    }
    renderBillChart(names, values, colors)
  } catch (e) {
    ElMessage.warning('账单数据加载失败')
  } finally {
    billLoading.value = false
  }
}

const renderBillChart = (names, values, colors) => {
  nextTick(() => {
    if (!billChartRef.value) return
    if (billChart) billChart.dispose()
    billChart = echarts.init(billChartRef.value)
    billChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: names },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '账单数', type: 'bar', barMaxWidth: 60,
        itemStyle: {
          color: (params) => colors[params.dataIndex],
          borderRadius: [4, 4, 0, 0]
        },
        label: { show: true, position: 'top' },
        data: values
      }]
    })
  })
}

// 停车位使用情况（饼图）
const loadParkingData = async () => {
  parkingLoading.value = true
  try {
    const res = await getParkingSpaceStats()
    const d = res?.data || {}
    const availableCount = d.availableCount || 0
    const rentedCount = d.rentedCount || 0
    const maintenanceCount = d.maintenanceCount || 0
    const totalCount = d.totalCount || (availableCount + rentedCount + maintenanceCount)
    const pieData = [
      { value: availableCount, name: '空闲', itemStyle: { color: '#67C23A' } },
      { value: rentedCount, name: '已租', itemStyle: { color: '#409EFF' } },
      { value: maintenanceCount, name: '维修中', itemStyle: { color: '#E6A23C' } }
    ].filter(item => item.value > 0)
    renderParkingChart(pieData, totalCount)
  } catch (e) {
    ElMessage.warning('停车位数据加载失败')
  } finally {
    parkingLoading.value = false
  }
}

const renderParkingChart = (pieData, totalCount) => {
  nextTick(() => {
    if (!parkingChartRef.value) return
    if (parkingChart) parkingChart.dispose()
    parkingChart = echarts.init(parkingChartRef.value)
    parkingChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
      legend: { orient: 'vertical', left: 'left', top: 'center' },
      graphic: [{
        type: 'text', left: 'center', top: 'center',
        style: { text: `总计\n${totalCount}个`, fill: '#666', fontSize: 12, textAlign: 'center' }
      }],
      series: [{
        name: '车位', type: 'pie',
        radius: ['45%', '70%'],
        center: ['60%', '50%'],
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: pieData.length ? pieData : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }]
      }]
    })
  })
}

// 加载全部数据
const loadAllData = async () => {
  await loadStats()
  loadOccupancyData()
  loadLoginTrend()
  loadRepairData()
  loadBillData()
  loadParkingData()
}

const resizeCharts = () => {
  occupancyChart?.resize()
  loginTrendChart?.resize()
  repairChart?.resize()
  billChart?.resize()
  parkingChart?.resize()
}

onMounted(() => {
  loadAllData()
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  occupancyChart?.dispose()
  loginTrendChart?.dispose()
  repairChart?.dispose()
  billChart?.dispose()
  parkingChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  .stats-cards {
    margin-bottom: 20px;
    .stats-card {
      .stats-content {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 8px 4px;
        .stats-icon {
          width: 60px; height: 60px;
          border-radius: 12px;
          display: flex; align-items: center; justify-content: center;
          flex-shrink: 0;
        }
        .building-icon { background: rgba(64,158,255,0.12); color: #409EFF; }
        .house-icon    { background: rgba(103,194,58,0.12);  color: #67C23A; }
        .user-icon     { background: rgba(230,162,60,0.12);  color: #E6A23C; }
        .login-icon    { background: rgba(245,108,108,0.12); color: #F56C6C; }
        .repair-icon   { background: rgba(144,147,153,0.12); color: #909399; }
        .log-icon      { background: rgba(64,158,255,0.12);  color: #409EFF; }
        .stats-info {
          flex: 1;
          .stats-number {
            font-size: 28px; font-weight: 700; color: #303133; line-height: 1.2;
          }
          .stats-number-sm {
            font-size: 22px; font-weight: 700; color: #303133; line-height: 1.2;
          }
          .stats-label {
            font-size: 13px; color: #909399; margin-top: 4px;
          }
        }
      }
    }
  }

  .charts-section {
    .chart-card {
      .card-header {
        display: flex; justify-content: space-between; align-items: center;
        font-weight: 600; color: #303133;
      }
      .chart-container { height: 280px; width: 100%; }
      .chart-loading {
        height: 280px;
        display: flex; align-items: center; justify-content: center;
        gap: 8px; color: #909399; font-size: 14px;
      }
    }
  }
}
</style>
