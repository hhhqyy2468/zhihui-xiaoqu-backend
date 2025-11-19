<template>
  <div class="my-house-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的房产</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>业主门户</el-breadcrumb-item>
        <el-breadcrumb-item>我的房产</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 房产列表 -->
    <div class="house-list">
      <el-row :gutter="20">
        <el-col
          v-for="house in houseList"
          :key="house.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="house-card" shadow="hover">
            <div class="house-info">
              <div class="house-header">
                <h3 class="house-title">{{ house.houseNo || '房产编号' }}</h3>
                <el-tag
                  v-if="house.houseStatus"
                  :type="getStatusType(house.houseStatus)"
                  size="small"
                >
                  {{ getStatusText(house.houseStatus) }}
                </el-tag>
              </div>

              <div class="house-details">
                <div class="detail-item">
                  <el-icon><House /></el-icon>
                  <span>房产编号：{{ house.houseNo || '--' }}</span>
                </div>
                <div class="detail-item">
                  <el-icon><HomeFilled /></el-icon>
                  <span>门牌号：{{ house.roomNumber || '--' }}</span>
                </div>
                <div class="detail-item">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>楼栋：{{ house.buildingName || '--' }}</span>
                </div>
                <div class="detail-item">
                  <el-icon><Grid /></el-icon>
                  <span>单元：{{ house.unitName || '--' }}</span>
                </div>
                <div class="detail-item" v-if="house.usableArea">
                  <el-icon><Expand /></el-icon>
                  <span>房产面积：{{ house.usableArea }}㎡</span>
                </div>
                <div class="detail-item" v-if="house.floor">
                  <el-icon><Sort /></el-icon>
                  <span>楼层：{{ house.floor }}层</span>
                </div>
                <div class="detail-item" v-if="house.houseType">
                  <el-icon><HomeFilled /></el-icon>
                  <span>户型：{{ house.houseType }}</span>
                </div>
                <div class="detail-item">
                  <el-icon><User /></el-icon>
                  <span>关系：{{ getRelationTypeText(house.relationType) }}</span>
                </div>
                <div class="detail-item" v-if="house.startDate">
                  <el-icon><Calendar /></el-icon>
                  <span>起始：{{ formatDate(house.startDate) }}</span>
                </div>
                <div class="detail-item" v-if="house.isCurrent">
                  <el-icon><Star /></el-icon>
                  <span class="current-tag">当前居住</span>
                </div>
                <div class="detail-item" v-if="house.remark">
                  <el-icon><Document /></el-icon>
                  <span>备注：{{ house.remark }}</span>
                </div>
              </div>

              <div class="house-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleViewDetails(house)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty
        v-if="!loading && houseList.length === 0"
        description="暂无房产信息"
        :image-size="200"
      >
        <template #description>
          <p>您目前没有关联的房产信息</p>
          <p class="empty-hint">如需添加房产，请联系物业管理员</p>
        </template>
      </el-empty>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
      </div>
    </div>

    <!-- 房产详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="房产详情"
      width="600px"
      :before-close="handleCloseDetail"
    >
      <div v-if="selectedHouse" class="house-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房产编号">
            {{ selectedHouse.houseNo || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="门牌号">
            {{ selectedHouse.roomNumber || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="楼栋名称">
            {{ selectedHouse.buildingName || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="单元名称">
            {{ selectedHouse.unitName || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="楼层">
            {{ selectedHouse.floor ? selectedHouse.floor + '层' : '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="户型">
            {{ selectedHouse.houseType || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="房产面积">
            {{ selectedHouse.usableArea || '--' }}㎡
          </el-descriptions-item>
          <el-descriptions-item label="房产状态">
            <el-tag v-if="selectedHouse.houseStatus" :type="getStatusType(selectedHouse.houseStatus)">
              {{ getStatusText(selectedHouse.houseStatus) }}
            </el-tag>
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="关系类型">
            {{ getRelationTypeText(selectedHouse.relationType) }}
          </el-descriptions-item>
          <el-descriptions-item label="是否当前居住">
            <el-tag v-if="selectedHouse.isCurrent" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="起始日期">
            {{ formatDate(selectedHouse.startDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">
            {{ formatDate(selectedHouse.endDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="用户姓名">
            {{ selectedHouse.userName || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" span="2">
            {{ selectedHouse.remark || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(selectedHouse.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDateTime(selectedHouse.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  House,
  OfficeBuilding,
  Grid,
  Expand,
  User,
  Star,
  Calendar,
  Document,
  HomeFilled,
  Sort
} from '@element-plus/icons-vue'
import { getHousesByUserId } from '@/api/userHouse'
import { useUserStore } from '@/stores/user'

// 用户存储
const userStore = useUserStore()

// 响应式数据
const houseList = ref([])
const loading = ref(true)
const detailDialogVisible = ref(false)
const selectedHouse = ref(null)

// 获取当前用户ID
const getCurrentUserId = () => {
  const userId = userStore.userInfo?.id
  if (!userId) {
    console.error('无法获取用户ID，用户信息:', userStore.userInfo)
    ElMessage.error('用户信息不完整，请重新登录')
    throw new Error('用户未登录或用户信息不完整')
  }
  return userId
}

// 加载房产列表
const loadHouseList = async () => {
  try {
    loading.value = true
    const userId = getCurrentUserId()
    console.log('正在加载用户房产，用户ID:', userId)

    const response = await getHousesByUserId(userId)
    console.log('房产数据响应:', response)

    if (response.code === 200 && response.data) {
      houseList.value = response.data
      console.log('房产列表加载成功:', houseList.value.length, '条记录')
    } else {
      console.warn('房产数据响应异常:', response)
      houseList.value = []
    }
  } catch (error) {
    console.error('加载房产列表失败:', error)
    ElMessage.error('加载房产信息失败')
    houseList.value = []
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    1: 'success',  // 已购
    2: 'warning',  // 在租
    3: 'primary',  // 自用
    4: 'danger'    // 其他
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '已购',
    2: '在租',
    3: '自用',
    4: '其他'
  }
  return statusMap[status] || '未知'
}

// 获取关系类型文本
const getRelationTypeText = (relationType) => {
  const relationMap = {
    1: '业主',
    2: '家属',
    3: '租客',
    4: '其他'
  }
  return relationMap[relationType] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '--'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 查看详情
const handleViewDetails = (house) => {
  selectedHouse.value = house
  detailDialogVisible.value = true
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailDialogVisible.value = false
  selectedHouse.value = null
}

// 组件挂载时加载数据
onMounted(() => {
  loadHouseList()
})
</script>

<style scoped>
.my-house-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.house-list {
  margin-top: 20px;
}

.house-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.house-card:hover {
  transform: translateY(-2px);
}

.house-info {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.house-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.house-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.house-details {
  flex: 1;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.detail-item .el-icon {
  margin-right: 8px;
  color: #909399;
}

.current-tag {
  color: #67c23a;
  font-weight: 500;
}

.house-actions {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.house-actions .el-button {
  width: 100%;
}

.loading-container {
  padding: 20px;
}

.empty-hint {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.house-detail {
  padding: 10px 0;
}

.dialog-footer {
  text-align: right;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .my-house-container {
    padding: 10px;
  }

  .page-title {
    font-size: 20px;
  }

  .house-card {
    margin-bottom: 15px;
  }
}
</style>