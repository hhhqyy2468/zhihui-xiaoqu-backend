<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="4" v-for="card in statCards" :key="card.key">
        <div class="stat-card" :class="card.colorClass">
          <div class="stat-icon">
            <el-icon :size="26"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-num">{{ statsData[card.key] ?? '-' }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card stat-refresh">
          <el-button type="primary" :loading="statsLoading" @click="loadAllData" style="width:100%">
            <el-icon><Refresh /></el-icon> 刷新全部
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 图表第一行：入住率（宽）+ 维修工单（窄） -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="15">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">各楼栋房产入住率</span>
              <el-button size="small" text :loading="occupancyLoading" @click="loadOccupancyData">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="occupancyLoading" class="chart-placeholder"><el-icon class="is-loading" :size="32"><Loading /></el-icon><span>加载中...</span></div>
          <div v-else ref="occupancyChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">维修工单状态分布</span>
              <el-button size="small" text :loading="repairLoading" @click="loadRepairData">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="repairLoading" class="chart-placeholder"><el-icon class="is-loading" :size="32"><Loading /></el-icon><span>加载中...</span></div>
          <div v-else ref="repairChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第二行：账单 + 车位 + 登录趋势 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="10">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">账单缴费情况</span>
              <el-button size="small" text :loading="billLoading" @click="loadBillData">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="billLoading" class="chart-placeholder"><el-icon class="is-loading" :size="32"><Loading /></el-icon><span>加载中...</span></div>
          <div v-else ref="billChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="7">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">停车位使用情况</span>
              <el-button size="small" text :loading="parkingLoading" @click="loadParkingData">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="parkingLoading" class="chart-placeholder"><el-icon class="is-loading" :size="32"><Loading /></el-icon><span>加载中...</span></div>
          <div v-else ref="parkingChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="7">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7天登录趋势</span>
              <el-button size="small" text :loading="loginTrendLoading" @click="loadLoginTrend">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="loginTrendLoading" class="chart-placeholder"><el-icon class="is-loading" :size="32"><Loading /></el-icon><span>加载中...</span></div>
          <div v-else ref="loginTrendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, onActivated, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, House, User, Refresh, Odometer, Tools, Loading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getAdminStats } from '@/api/dashboard'
import { listBuildings } from '@/api/building'
import { listHouses } from '@/api/house'
import { getRepairOrderPage } from '@/api/repair'
import { getAllBills } from '@/api/bill'
import { getParkingSpaceStats } from '@/api/parkingSpace'
import { listOperLog } from '@/api/log'

const statCards = [
  { key: 'buildingCount',   label: '总楼栋数',   icon: 'OfficeBuilding', colorClass: 'blue' },
  { key: 'houseCount',      label: '总房产数',   icon: 'House',          colorClass: 'green' },
  { key: 'userCount',       label: '总用户数',   icon: 'User',           colorClass: 'orange' },
  { key: 'todayLoginCount', label: '今日登录',   icon: 'Odometer',       colorClass: 'red' },
  { key: 'pendingRepairs',  label: '待处理维修', icon: 'Tools',          colorClass: 'purple' },
]

const occupancyChartRef  = ref()
const loginTrendChartRef = ref()
const repairChartRef     = ref()
const billChartRef       = ref()
const parkingChartRef    = ref()
const statsLoading      = ref(false)
const occupancyLoading  = ref(false)
const loginTrendLoading = ref(false)
const repairLoading     = ref(false)
const billLoading       = ref(false)
const parkingLoading    = ref(false)
const statsData = ref({ buildingCount: null, houseCount: null, userCount: null, todayLoginCount: null, pendingRepairs: null })
let occupancyChart = null, loginTrendChart = null, repairChart = null, billChart = null, parkingChart = null

const loadStats = async () => {
  statsLoading.value = true
  try {
    const res = await getAdminStats()
    if (res?.data) statsData.value = { ...statsData.value, ...res.data }
    // 今日登录数从操作日志（title=用户登录, businessType=1）统计
    const today = new Date().toISOString().slice(0, 10)
    const loginRes = await listOperLog({ pageNum: 1, pageSize: 1, businessType: 1, beginTime: today + ' 00:00:00', endTime: today + ' 23:59:59' })
    statsData.value.todayLoginCount = loginRes?.data?.total ?? 0
  } catch (e) { ElMessage.warning('统计数据加载失败') }
  finally { statsLoading.value = false }
}

const loadOccupancyData = async () => {
  occupancyLoading.value = true
  let names = [], rates = [], totalArr = [], occupiedArr = []
  try {
    const buildRes = await listBuildings({ pageNum: 1, pageSize: 100 })
    const buildings = buildRes?.data?.records || buildRes?.data?.rows || buildRes?.data?.list || (Array.isArray(buildRes?.data) ? buildRes.data : [])
    for (const b of buildings) {
      const hr = await listHouses({ buildingId: b.id, pageNum: 1, pageSize: 1000 })
      const houses = hr?.data?.records || hr?.data?.rows || hr?.data?.list || (Array.isArray(hr?.data) ? hr.data : [])
      const total = houses.length
      const occupied = houses.filter(h => h.houseStatus === 2).length
      names.push(b.buildingName || b.name || ('楼栋' + b.id))
      rates.push(total > 0 ? Math.round(occupied / total * 100) : 0)
      totalArr.push(total); occupiedArr.push(occupied)
    }
  } catch (e) { ElMessage.warning('楼栋入住率加载失败') }
  finally { occupancyLoading.value = false }
  await nextTick()
  renderOccupancyChart(names, rates, totalArr, occupiedArr)
}

const renderOccupancyChart = (names, rates, totalArr, occupiedArr) => {
  if (!occupancyChartRef.value) return
  if (occupancyChart) occupancyChart.dispose()
  occupancyChart = echarts.init(occupancyChartRef.value)
  occupancyChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' },
      formatter: p => {
        const i = p[0]?.dataIndex ?? 0
        return (names[i] ?? '') + '<br/>入住率: ' + (p[0]?.value ?? 0) + '%<br/>已入住: ' + (occupiedArr[i] ?? 0) + ' / ' + (totalArr[i] ?? 0) + ' 套'
      }
    },
    grid: { left: '2%', right: '2%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{ name: '入住率', type: 'bar', barMaxWidth: 50,
      itemStyle: { color: p => ['#5470C6','#91CC75','#FAC858','#EE6666','#73C0DE','#3BA272'][p.dataIndex % 6], borderRadius: [4,4,0,0] },
      label: { show: true, position: 'top', formatter: '{c}%' },
      data: rates.length ? rates : [{ value: 0, name: '暂无数据' }] }]
  })
}

const loadLoginTrend = async () => {
  loginTrendLoading.value = true
  let days = [], counts = [], successCounts = []
  try {
    for (let i = 6; i >= 0; i--) {
      const d = new Date(); d.setDate(d.getDate() - i)
      const ds = d.toISOString().slice(0, 10)
      days.push(ds.slice(5))
      try {
        const r1 = await listLoginLog({ pageNum: 1, pageSize: 1, beginTime: ds + ' 00:00:00', endTime: ds + ' 23:59:59' })
        const r2 = await listLoginLog({ pageNum: 1, pageSize: 1, beginTime: ds + ' 00:00:00', endTime: ds + ' 23:59:59', status: 0 })
        counts.push(r1?.data?.total ?? 0); successCounts.push(r2?.data?.total ?? 0)
      } catch { counts.push(0); successCounts.push(0) }
    }
  } catch (e) { ElMessage.warning('登录趋势加载失败') }
  finally { loginTrendLoading.value = false }
  await nextTick()
  renderLoginTrendChart(days, counts, successCounts)
}

const renderLoginTrendChart = (days, counts, successCounts) => {
  if (!loginTrendChartRef.value) return
  if (loginTrendChart) loginTrendChart.dispose()
  loginTrendChart = echarts.init(loginTrendChartRef.value)
  loginTrendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['总登录', '成功'], top: 4 },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: days },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '总登录', type: 'line', smooth: true, data: counts, itemStyle: { color: '#409EFF' }, areaStyle: { opacity: 0.1 } },
      { name: '成功', type: 'line', smooth: true, data: successCounts, itemStyle: { color: '#67C23A' }, areaStyle: { opacity: 0.1 } }
    ]
  })
}

const loadRepairData = async () => {
  repairLoading.value = true
  let pieData = []
  try {
    const statusList = [
      { value: 1, name: '待派工', color: '#F56C6C' },
      { value: 2, name: '待接单', color: '#E6A23C' },
      { value: 3, name: '进行中', color: '#409EFF' },
      { value: 4, name: '已完成', color: '#67C23A' },
      { value: 5, name: '已关闭', color: '#909399' }
    ]
    for (const s of statusList) {
      try {
        const res = await getRepairOrderPage({ pageNum: 1, pageSize: 1, orderStatus: s.value })
        const cnt = res?.data?.total ?? 0
        if (cnt > 0) pieData.push({ value: cnt, name: s.name, itemStyle: { color: s.color } })
      } catch {}
    }
  } catch (e) { ElMessage.warning('维修工单数据加载失败') }
  finally { repairLoading.value = false }
  await nextTick()
  renderRepairChart(pieData)
}

const renderRepairChart = (pieData) => {
  if (!repairChartRef.value) return
  if (repairChart) repairChart.dispose()
  repairChart = echarts.init(repairChartRef.value)
  repairChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle', textStyle: { fontSize: 12 } },
    series: [{ name: '工单状态', type: 'pie', radius: ['40%', '70%'], center: ['62%', '50%'],
      itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 },
      label: { show: false }, emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
      data: pieData.length ? pieData : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }] }]
  })
}

const loadBillData = async () => {
  billLoading.value = true
  let names = [], values = [], colors = []
  try {
    const statusList = [
      { value: 1, name: '待缴费', color: '#E6A23C' },
      { value: 2, name: '已缴费', color: '#67C23A' },
      { value: 3, name: '逾期',   color: '#F56C6C' }
    ]
    names = statusList.map(s => s.name)
    colors = statusList.map(s => s.color)
    for (const s of statusList) {
      try {
        const res = await getAllBills({ billStatus: s.value })
        const cnt = res?.data?.total ?? (Array.isArray(res?.data) ? res.data.length : 0)
        values.push(cnt)
      } catch { values.push(0) }
    }
  } catch (e) { ElMessage.warning('账单数据加载失败') }
  finally { billLoading.value = false }
  await nextTick()
  renderBillChart(names, values, colors)
}

const renderBillChart = (names, values, colors) => {
  if (!billChartRef.value) return
  if (billChart) billChart.dispose()
  billChart = echarts.init(billChartRef.value)
  billChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '账单数', type: 'bar', barMaxWidth: 60,
      itemStyle: { color: p => colors[p.dataIndex], borderRadius: [4,4,0,0] },
      label: { show: true, position: 'top' }, data: values }]
  })
}

const loadParkingData = async () => {
  parkingLoading.value = true
  let pieData = [], total = 0
  try {
    const res = await getParkingSpaceStats()
    const d = res?.data || {}
    const availableCount = d.availableCount || 0
    const rentedCount = d.rentedCount || 0
    const maintenanceCount = d.maintenanceCount || 0
    total = d.totalCount || (availableCount + rentedCount + maintenanceCount)
    pieData = [
      { value: availableCount,   name: '空闲',   itemStyle: { color: '#67C23A' } },
      { value: rentedCount,      name: '已租',   itemStyle: { color: '#409EFF' } },
      { value: maintenanceCount, name: '维修中', itemStyle: { color: '#E6A23C' } }
    ].filter(i => i.value > 0)
  } catch (e) { ElMessage.warning('停车位数据加载失败') }
  finally { parkingLoading.value = false }
  await nextTick()
  renderParkingChart(pieData, total)
}

const renderParkingChart = (pieData, totalCount) => {
  if (!parkingChartRef.value) return
  if (parkingChart) parkingChart.dispose()
  parkingChart = echarts.init(parkingChartRef.value)
  parkingChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle' },
    graphic: [{ type: 'text', left: 'center', top: 'center',
      style: { text: '总计\n' + totalCount + '个', fill: '#666', fontSize: 12, textAlign: 'center' } }],
    series: [{ name: '车位', type: 'pie', radius: ['45%', '70%'], center: ['62%', '50%'],
      itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 },
      label: { show: false }, emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
      data: pieData.length ? pieData : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }] }]
  })
}

const loadAllData = async () => {
  await loadStats()
  loadOccupancyData()
  loadLoginTrend()
  loadRepairData()
  loadBillData()
  loadParkingData()
}

const resizeCharts = () => {
  occupancyChart?.resize(); loginTrendChart?.resize()
  repairChart?.resize(); billChart?.resize(); parkingChart?.resize()
}

onMounted(() => { loadAllData(); window.addEventListener('resize', resizeCharts) })
onActivated(() => { loadAllData() })
onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  occupancyChart?.dispose(); loginTrendChart?.dispose()
  repairChart?.dispose(); billChart?.dispose(); parkingChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}
.stats-row { margin-bottom: 16px; }
.chart-row { margin-bottom: 16px; }
.stat-card {
  display: flex; align-items: center; gap: 14px;
  padding: 16px; border-radius: 10px; background: #fff;
  box-shadow: 0 1px 6px rgba(0,0,0,0.08); height: 80px;
  .stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
  .stat-body { .stat-num { font-size: 24px; font-weight: 700; color: #303133; line-height: 1.2; } .stat-label { font-size: 12px; color: #909399; margin-top: 2px; } }
  &.blue   { .stat-icon { background: rgba(64,158,255,0.12); color: #409EFF; } }
  &.green  { .stat-icon { background: rgba(103,194,58,0.12); color: #67C23A; } }
  &.orange { .stat-icon { background: rgba(230,162,60,0.12); color: #E6A23C; } }
  &.red    { .stat-icon { background: rgba(245,108,108,0.12); color: #F56C6C; } }
  &.purple { .stat-icon { background: rgba(144,100,255,0.12); color: #9064FF; } }
  &.stat-refresh { background: transparent; box-shadow: none; justify-content: center; }
}
.chart-card {
  border-radius: 10px;
  :deep(.el-card__header) { padding: 12px 16px; border-bottom: 1px solid #f0f0f0; }
  :deep(.el-card__body) { padding: 12px 16px; }
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .card-title { font-size: 14px; font-weight: 600; color: #303133; }
  .chart-box { height: 280px; width: 100%; }
  .chart-placeholder { height: 280px; display: flex; align-items: center; justify-content: center; gap: 8px; color: #909399; font-size: 14px; }
}
</style>