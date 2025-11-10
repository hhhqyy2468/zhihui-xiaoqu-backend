<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon :size="32" color="#409EFF"><OfficeBuilding /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ overviewData.totalBuildings }}</div>
                <div class="stats-label">总楼栋数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon :size="32" color="#67C23A"><House /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ overviewData.totalHouseholds }}</div>
                <div class="stats-label">总户数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon :size="32" color="#E6A23C"><User /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ overviewData.totalResidents }}</div>
                <div class="stats-label">总人数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon :size="32" color="#F56C6C"><Money /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">¥{{ overviewData.totalRevenue.toLocaleString() }}</div>
                <div class="stats-label">本月收入</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 收入趋势图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>收入趋势分析</span>
                <el-button-group>
                  <el-button
                    :type="revenuePeriod === 'week' ? 'primary' : ''"
                    size="small"
                    @click="changeRevenuePeriod('week')"
                  >
                    近一周
                  </el-button>
                  <el-button
                    :type="revenuePeriod === 'month' ? 'primary' : ''"
                    size="small"
                    @click="changeRevenuePeriod('month')"
                  >
                    近一月
                  </el-button>
                  <el-button
                    :type="revenuePeriod === 'year' ? 'primary' : ''"
                    size="small"
                    @click="changeRevenuePeriod('year')"
                  >
                    近一年
                  </el-button>
                </el-button-group>
              </div>
            </template>
            <div ref="revenueChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 楼栋入住率 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>楼栋入住率分析</span>
            </template>
            <div ref="occupancyChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 费用类型分布 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>费用类型分布</span>
            </template>
            <div ref="feeTypeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 投诉维修统计 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>投诉维修统计</span>
            </template>
            <div ref="complaintChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 停车位使用情况 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>停车位使用情况</span>
            </template>
            <div ref="parkingChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 实时数据区域 -->
    <div class="realtime-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>实时动态</span>
            <el-button @click="refreshRealtimeData" :loading="realtimeLoading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        <div class="realtime-content">
          <el-timeline>
            <el-timeline-item
              v-for="item in realtimeData"
              :key="item.id"
              :timestamp="item.timestamp"
              :type="item.type"
            >
              <div class="realtime-item">
                <span class="realtime-title">{{ item.title }}</span>
                <span class="realtime-desc">{{ item.description }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, House, User, Money, Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 响应式数据
const revenueChartRef = ref()
const occupancyChartRef = ref()
const feeTypeChartRef = ref()
const complaintChartRef = ref()
const parkingChartRef = ref()

const revenuePeriod = ref('month')
const realtimeLoading = ref(false)

// 图表实例
let revenueChart = null
let occupancyChart = null
let feeTypeChart = null
let complaintChart = null
let parkingChart = null

// 概览数据
const overviewData = ref({
  totalBuildings: 12,
  totalHouseholds: 856,
  totalResidents: 2145,
  totalRevenue: 1256800
})

// 实时数据
const realtimeData = ref([
  {
    id: 1,
    title: '新增投诉',
    description: '3号楼102业主反映电梯故障',
    timestamp: '2024-11-09 11:45',
    type: 'warning'
  },
  {
    id: 2,
    title: '费用缴纳',
    description: '张三成功缴纳本月物业费',
    timestamp: '2024-11-09 11:32',
    type: 'success'
  },
  {
    id: 3,
    title: '维修完成',
    description: '1号楼管道维修工作已完成',
    timestamp: '2024-11-09 11:15',
    type: 'info'
  },
  {
    id: 4,
    title: '新用户注册',
    description: '李四完成住户信息登记',
    timestamp: '2024-11-09 10:58',
    type: 'primary'
  }
])

// 获取收入趋势数据
const getRevenueData = () => {
  const mockData = {
    week: {
      dates: ['11-03', '11-04', '11-05', '11-06', '11-07', '11-08', '11-09'],
      revenue: [28000, 32000, 29000, 35000, 41000, 38000, 42000]
    },
    month: {
      dates: Array.from({length: 30}, (_, i) => `${11}-${String(i + 1).padStart(2, '0')}`),
      revenue: Array.from({length: 30}, () => Math.floor(Math.random() * 50000) + 20000)
    },
    year: {
      dates: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月'],
      revenue: [850000, 920000, 980000, 1050000, 1120000, 1180000, 1250000, 1320000, 1280000, 1350000, 1256800]
    }
  }
  return mockData[revenuePeriod.value]
}

// 初始化收入趋势图
const initRevenueChart = () => {
  const data = getRevenueData()
  const option = {
    title: {
      text: '收入趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#333'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>收入: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        },
        data: data.revenue
      }
    ]
  }

  revenueChart = echarts.init(revenueChartRef.value)
  revenueChart.setOption(option)
}

// 初始化入住率图表
const initOccupancyChart = () => {
  const data = [
    { name: '1号楼', rate: 85 },
    { name: '2号楼', rate: 92 },
    { name: '3号楼', rate: 78 },
    { name: '4号楼', rate: 88 },
    { name: '5号楼', rate: 95 },
    { name: '6号楼', rate: 82 }
  ]

  const option = {
    title: {
      text: '楼栋入住率',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#333'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}<br/>入住率: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '入住率',
        type: 'bar',
        itemStyle: {
          color: function(params) {
            const colors = ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE', '#3BA272']
            return colors[params.dataIndex % colors.length]
          }
        },
        data: data.map(item => item.rate)
      }
    ]
  }

  occupancyChart = echarts.init(occupancyChartRef.value)
  occupancyChart.setOption(option)
}

// 初始化费用类型分布图
const initFeeTypeChart = () => {
  const data = [
    { name: '物业费', value: 45 },
    { name: '停车费', value: 20 },
    { name: '水电费', value: 18 },
    { name: '维修费', value: 10 },
    { name: '其他', value: 7 }
  ]

  const option = {
    title: {
      text: '费用类型分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#333'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a}<br/>{b}: {c}% ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '费用类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }

  feeTypeChart = echarts.init(feeTypeChartRef.value)
  feeTypeChart.setOption(option)
}

// 初始化投诉维修统计图
const initComplaintChart = () => {
  const option = {
    title: {
      text: '投诉维修统计',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#333'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['投诉', '维修'],
      top: '10%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['本周', '上周', '上上周', '三周前', '四周前']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '投诉',
        type: 'bar',
        data: [12, 8, 15, 6, 10],
        itemStyle: {
          color: '#F56C6C'
        }
      },
      {
        name: '维修',
        type: 'bar',
        data: [28, 22, 35, 18, 25],
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }

  complaintChart = echarts.init(complaintChartRef.value)
  complaintChart.setOption(option)
}

// 初始化停车位使用情况图
const initParkingChart = () => {
  const data = [
    { name: '已占用', value: 320 },
    { name: '空闲', value: 180 }
  ]

  const option = {
    title: {
      text: '停车位使用情况',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#333'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a}<br/>{b}: {c}个 ({d}%)'
    },
    series: [
      {
        name: '停车位',
        type: 'pie',
        radius: '70%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        itemStyle: {
          color: function(params) {
            const colors = ['#F56C6C', '#67C23A']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }

  parkingChart = echarts.init(parkingChartRef.value)
  parkingChart.setOption(option)
}

// 切换收入统计周期
const changeRevenuePeriod = (period) => {
  revenuePeriod.value = period
  const data = getRevenueData()
  revenueChart.setOption({
    xAxis: {
      data: data.dates
    },
    series: [
      {
        data: data.revenue
      }
    ]
  })
}

// 刷新实时数据
const refreshRealtimeData = () => {
  realtimeLoading.value = true

  setTimeout(() => {
    // 模拟新数据
    const newEvents = [
      {
        id: Date.now(),
        title: '系统通知',
        description: '月度财务报表已生成',
        timestamp: new Date().toLocaleString(),
        type: 'success'
      }
    ]

    realtimeData.value.unshift(...newEvents)
    realtimeData.value = realtimeData.value.slice(0, 10)
    realtimeLoading.value = false
    ElMessage.success('实时数据已更新')
  }, 1000)
}

// 自适应图表大小
const resizeCharts = () => {
  if (revenueChart) revenueChart.resize()
  if (occupancyChart) occupancyChart.resize()
  if (feeTypeChart) feeTypeChart.resize()
  if (complaintChart) complaintChart.resize()
  if (parkingChart) parkingChart.resize()
}

// 组件挂载
onMounted(() => {
  nextTick(() => {
    initRevenueChart()
    initOccupancyChart()
    initFeeTypeChart()
    initComplaintChart()
    initParkingChart()

    window.addEventListener('resize', resizeCharts)
  })
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)

  if (revenueChart) revenueChart.dispose()
  if (occupancyChart) occupancyChart.dispose()
  if (feeTypeChart) feeTypeChart.dispose()
  if (complaintChart) complaintChart.dispose()
  if (parkingChart) parkingChart.dispose()
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
        padding: 10px;

        .stats-icon {
          margin-right: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 60px;
          border-radius: 50%;
          background: rgba(64, 158, 255, 0.1);
        }

        .stats-info {
          flex: 1;

          .stats-number {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stats-label {
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }

  .charts-section {
    .chart-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: bold;
        color: #333;
      }

      .chart-container {
        height: 300px;
        width: 100%;
      }
    }
  }

  .realtime-section {
    margin-top: 20px;

    .realtime-content {
      max-height: 300px;
      overflow-y: auto;

      .realtime-item {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .realtime-title {
          font-weight: bold;
          color: #333;
        }

        .realtime-desc {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}
</style>