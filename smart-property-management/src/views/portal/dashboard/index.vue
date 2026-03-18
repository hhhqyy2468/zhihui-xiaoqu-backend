<template>
  <div class="portal-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-info">
            <h2>欢迎回家，{{ userInfo.realName }}</h2>
            <p class="welcome-desc">{{ userInfo.houseInfo }} 的业主</p>
            <div class="welcome-stats">
              <div class="stat-item">
                <span class="stat-label">入住天数</span>
                <span class="stat-value">{{ userInfo.checkInDays }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <el-card class="actions-card">
        <template #header>
          <span>快捷服务</span>
        </template>
        <div class="actions-grid">
          <div class="action-item" @click="navigateTo('/portal/bills')">
            <div class="action-icon" style="background: rgba(64, 158, 255, 0.1);">
              <el-icon :size="24" color="#409EFF"><Money /></el-icon>
            </div>
            <span class="action-label">缴费中心</span>
            <span class="action-desc" v-if="unpaidCount > 0">{{ unpaidCount }}笔待缴</span>
          </div>
          <div class="action-item" @click="navigateTo('/portal/repair')">
            <div class="action-icon" style="background: rgba(103, 194, 58, 0.1);">
              <el-icon :size="24" color="#67C23A"><Tools /></el-icon>
            </div>
            <span class="action-label">便民服务</span>
            <span class="action-desc">报修预约</span>
          </div>
          <div class="action-item" @click="navigateTo('/portal/announcement')">
            <div class="action-icon" style="background: rgba(230, 162, 60, 0.1);">
              <el-icon :size="24" color="#E6A23C"><Bell /></el-icon>
            </div>
            <span class="action-label">社区公告</span>
            <span class="action-desc" v-if="unreadNoticeCount > 0">{{ unreadNoticeCount }}条新公告</span>
          </div>
          <div class="action-item" @click="navigateTo('/portal/complaint')">
            <div class="action-icon" style="background: rgba(245, 108, 108, 0.1);">
              <el-icon :size="24" color="#F56C6C"><ChatDotRound /></el-icon>
            </div>
            <span class="action-label">我的投诉</span>
            <span class="action-desc">提交投诉</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 账户概览 -->
    <div class="account-overview">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="overview-card">
            <template #header>
              <div class="card-header">
                <span>账户余额</span>
              </div>
            </template>
            <div class="balance-info">
              <div class="balance-amount">¥{{ wallet.balance.toLocaleString() }}</div>
              <div class="balance-desc">可用余额</div>
              <div class="balance-actions">
                <el-button link type="primary" @click="navigateTo('/portal/wallet')">
                  查看明细
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="overview-card">
            <template #header>
              <span>本月账单</span>
            </template>
            <div class="bill-summary">
              <div class="bill-amount">
                <span class="amount-label">应缴金额</span>
                <span class="amount-value">¥{{ monthlyBill.totalAmount.toLocaleString() }}</span>
              </div>
              <div class="bill-status">
                <el-tag :type="monthlyBill.status === 1 ? 'success' : 'warning'">
                  {{ monthlyBill.status === 1 ? '已缴清' : '待缴费' }}
                </el-tag>
              </div>
              <div class="bill-details">
                <div class="bill-item">
                  <span>物业费</span>
                  <span>¥{{ monthlyBill.propertyFee.toLocaleString() }}</span>
                </div>
                <div class="bill-item">
                  <span>停车费</span>
                  <span>¥{{ monthlyBill.parkingFee.toLocaleString() }}</span>
                </div>
                <div class="bill-item">
                  <span>其他费用</span>
                  <span>¥{{ monthlyBill.otherFee.toLocaleString() }}</span>
                </div>
              </div>
              <div class="bill-actions">
                <el-button
                  v-if="monthlyBill.status === 0"
                  type="primary"
                  size="small"
                  @click="navigateTo('/portal/bills')"
                >
                  立即缴费
                </el-button>
                <el-button
                  link
                  type="info"
                  size="small"
                  @click="navigateTo('/portal/bills')"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Money,
  Tools,
  Bell,
  ChatDotRound
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getWalletByUserId } from '@/api/wallet'
import { getMyBillList } from '@/api/bill'
import { getUserNotices } from '@/api/notice'
import { getHousesByUserId } from '@/api/userHouse'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = ref({
  realName: '',
  houseInfo: '暂无房产信息',
  checkInDays: 0
})

const unpaidCount = ref(0)
const unreadNoticeCount = ref(0)

const wallet = ref({ balance: 0 })

const monthlyBill = ref({
  totalAmount: 0,
  status: 1,
  propertyFee: 0,
  parkingFee: 0,
  otherFee: 0
})

const navigateTo = (path) => {
  router.push(path)
}

// 加载钱包信息
const loadWallet = async () => {
  try {
    const uid = userStore.id
    if (!uid) return
    const res = await getWalletByUserId(uid)
    if (res && res.code === 200 && res.data) {
      wallet.value.balance = res.data.balance || 0
    }
  } catch (e) {
    console.error('加载钱包失败:', e)
  }
}

// 加载未缴账单数量和本月账单统计
const loadUnpaidCount = async () => {
  try {
    const res = await getMyBillList({ pageNum: 1, pageSize: 100, billStatus: 1 })
    if (res && res.code === 200 && res.data) {
      const bills = res.data.records || res.data.rows || []
      unpaidCount.value = res.data.total || bills.length
      let total = 0, propertyFee = 0, parkingFee = 0, otherFee = 0
      bills.forEach(b => {
        const amt = parseFloat(b.amount) || 0
        total += amt
        const typeName = (b.feeTypeName || '').toLowerCase()
        if (typeName.includes('物业')) propertyFee += amt
        else if (typeName.includes('停车')) parkingFee += amt
        else otherFee += amt
      })
      monthlyBill.value = { totalAmount: total, status: unpaidCount.value === 0 ? 1 : 0, propertyFee, parkingFee, otherFee }
    }
  } catch (e) {
    console.error('加载账单失败:', e)
  }
}

// 加载未读公告数量
const loadUnreadNoticeCount = async () => {
  try {
    const res = await getUserNotices({ pageNum: 1, pageSize: 1, isRead: 0 })
    if (res && res.code === 200 && res.data) {
      unreadNoticeCount.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载公告失败:', e)
  }
}

// 加载房产信息
const loadHouseInfo = async () => {
  try {
    const uid = userStore.id
    if (!uid) return
    const res = await getHousesByUserId(uid)
    if (res && res.code === 200 && res.data) {
      const houses = Array.isArray(res.data) ? res.data : (res.data.records || [])
      if (houses.length > 0) {
        const h = houses[0]
        const parts = [h.buildingName, h.unitName, h.roomNumber].filter(Boolean)
        userInfo.value.houseInfo = parts.length > 0 ? parts.join('') : (h.houseNo || '已关联房产')
        if (h.startDate) {
          const days = Math.floor((Date.now() - new Date(h.startDate)) / 86400000)
          userInfo.value.checkInDays = days > 0 ? days : 0
        }
      }
    }
  } catch (e) {
    console.error('加载房产失败:', e)
  }
}

onMounted(() => {
  userInfo.value.realName = userStore.realName || userStore.username || '用户'
  loadWallet()
  loadUnpaidCount()
  loadUnreadNoticeCount()
  loadHouseInfo()
})
</script>

<style lang="scss" scoped>
.portal-dashboard {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  .welcome-section {
    margin-bottom: 20px;

    .welcome-card {
      .welcome-content {
        display: flex;
        align-items: center;

        .welcome-info {
          h2 {
            margin: 0 0 8px 0;
            color: #333;
            font-size: 24px;
          }

          .welcome-desc {
            margin: 0 0 16px 0;
            color: #666;
            font-size: 16px;
          }

          .welcome-stats {
            display: flex;
            gap: 32px;

            .stat-item {
              display: flex;
              flex-direction: column;
              gap: 4px;

              .stat-label {
                font-size: 14px;
                color: #999;
              }

              .stat-value {
                font-size: 20px;
                font-weight: bold;
                color: #333;
              }
            }
          }
        }
      }
    }
  }

  .quick-actions {
    margin-bottom: 20px;

    .actions-card {
      .actions-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 20px;

        .action-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 12px;
          padding: 20px;
          border-radius: 8px;
          background: #fff;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .action-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
          }

          .action-label {
            font-size: 16px;
            font-weight: bold;
            color: #333;
          }

          .action-desc {
            font-size: 14px;
            color: #666;
            text-align: center;
          }
        }
      }
    }
  }

  .account-overview {
    margin-bottom: 20px;

    .overview-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .balance-info {
        text-align: center;
        padding: 20px 0;

        .balance-amount {
          font-size: 32px;
          font-weight: bold;
          color: #409eff;
          margin-bottom: 8px;
        }

        .balance-desc {
          font-size: 14px;
          color: #999;
          margin-bottom: 16px;
        }
      }

      .bill-summary {
        .bill-amount {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          .amount-label {
            font-size: 14px;
            color: #666;
          }

          .amount-value {
            font-size: 20px;
            font-weight: bold;
            color: #f56c6c;
          }
        }

        .bill-status {
          margin-bottom: 12px;
        }

        .bill-details {
          .bill-item {
            display: flex;
            justify-content: space-between;
            padding: 6px 0;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
            color: #666;

            &:last-child {
              border-bottom: none;
            }
          }
        }

        .bill-actions {
          margin-top: 12px;
          display: flex;
          gap: 8px;
        }
      }
    }
  }
}
</style>
