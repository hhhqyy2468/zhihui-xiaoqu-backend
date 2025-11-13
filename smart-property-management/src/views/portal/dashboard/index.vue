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
              <div class="stat-item">
                <span class="stat-label">信用积分</span>
                <span class="stat-value">{{ userInfo.creditPoints }}</span>
              </div>
            </div>
          </div>
          <div class="welcome-weather">
            <div class="weather-info">
              <el-icon :size="32" color="#F39C12"><Sunny /></el-icon>
              <div class="weather-details">
                <div class="temperature">28°C</div>
                <div class="weather-desc">晴朗</div>
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
          <div class="action-item" @click="navigateTo('/portal/services')">
            <div class="action-icon" style="background: rgba(103, 194, 58, 0.1);">
              <el-icon :size="24" color="#67C23A"><Tools /></el-icon>
            </div>
            <span class="action-label">便民服务</span>
            <span class="action-desc">报修预约</span>
          </div>
          <div class="action-item" @click="navigateTo('/portal/notice')">
            <div class="action-icon" style="background: rgba(230, 162, 60, 0.1);">
              <el-icon :size="24" color="#E6A23C"><Bell /></el-icon>
            </div>
            <span class="action-label">社区公告</span>
            <span class="action-desc" v-if="unreadNoticeCount > 0">{{ unreadNoticeCount }}条新公告</span>
          </div>
          <div class="action-item" @click="navigateTo('/portal/contacts')">
            <div class="action-icon" style="background: rgba(245, 108, 108, 0.1);">
              <el-icon :size="24" color="#F56C6C"><Phone /></el-icon>
            </div>
            <span class="action-label">紧急联系</span>
            <span class="action-desc">物业电话</span>
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
                <el-button type="primary" size="small" @click="handleRecharge">
                  充值
                </el-button>
              </div>
            </template>
            <div class="balance-info">
              <div class="balance-amount">¥{{ wallet.balance.toLocaleString() }}</div>
              <div class="balance-desc">可用余额</div>
              <div class="balance-actions">
                <el-button link type="primary" @click="navigateTo('/portal/wallet')">
                  查看明细
                </el-button>
                <el-button link type="info" @click="handleRecharge">
                  立即充值
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

    <!-- 服务状态 -->
    <div class="service-status">
      <el-card class="status-card">
        <template #header>
          <span>服务状态</span>
        </template>
        <div class="status-grid">
          <div class="status-item" v-for="service in serviceStatus" :key="service.id">
            <div class="status-header">
              <div class="service-name">{{ service.name }}</div>
              <el-tag :type="getStatusTag(service.status)" size="small">
                {{ getStatusName(service.status) }}
              </el-tag>
            </div>
            <div class="status-desc">{{ service.description }}</div>
            <div class="status-time" v-if="service.updateTime">
              更新时间：{{ service.updateTime }}
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 社区动态 -->
    <div class="community-updates">
      <el-card class="updates-card">
        <template #header>
          <div class="card-header">
            <span>社区动态</span>
            <el-button link type="primary" @click="navigateTo('/portal/community')">
              查看更多
            </el-button>
          </div>
        </template>
        <div class="updates-list">
          <div class="update-item" v-for="update in communityUpdates" :key="update.id">
            <div class="update-icon">
              <el-icon :size="16" :color="getUpdateTypeColor(update.type)">
                <component :is="getUpdateIcon(update.type)" />
              </el-icon>
            </div>
            <div class="update-content">
              <div class="update-title">{{ update.title }}</div>
              <div class="update-desc">{{ update.description }}</div>
              <div class="update-time">{{ update.publishTime }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="账户充值"
      width="400px"
    >
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="10"
            :step="10"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio value="wechat">微信支付</el-radio>
            <el-radio value="alipay">支付宝</el-radio>
            <el-radio value="bank">银行卡</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRechargeSubmit">
            确认充值
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Sunny,
  Money,
  Tools,
  Bell,
  Phone,
  Message,
  Warning,
  InfoFilled,
  Star
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const rechargeDialogVisible = ref(false)

// 用户信息
const userInfo = ref({
  realName: '张三',
  houseInfo: '3号楼2单元501',
  checkInDays: 365,
  creditPoints: 95
})

// 未缴账单数量
const unpaidCount = ref(2)

// 未读公告数量
const unreadNoticeCount = ref(3)

// 钱包信息
const wallet = ref({
  balance: 2568.50
})

// 本月账单
const monthlyBill = ref({
  totalAmount: 1250.00,
  status: 0, // 0-待缴费 1-已缴清
  propertyFee: 850.00,
  parkingFee: 300.00,
  otherFee: 100.00
})

// 服务状态
const serviceStatus = ref([
  {
    id: 1,
    name: '网络服务',
    description: '社区宽带网络正常运行',
    status: 1, // 0-异常 1-正常
    updateTime: '2024-11-09 10:00'
  },
  {
    id: 2,
    name: '电梯运行',
    description: '1号楼电梯维护中，预计2小时后恢复',
    status: 0,
    updateTime: '2024-11-09 08:30'
  },
  {
    id: 3,
    name: '供水服务',
    description: '二次供水系统运行正常',
    status: 1,
    updateTime: '2024-11-09 09:00'
  }
])

// 社区动态
const communityUpdates = ref([
  {
    id: 1,
    type: 'notice',
    title: '电梯维护通知',
    description: '1号楼电梯将于今日上午8:00-12:00进行维护保养',
    publishTime: '2024-11-09 07:30'
  },
  {
    id: 2,
    type: 'activity',
    title: '社区篮球赛报名',
    description: '本周六下午3点社区篮球场举办友谊赛，欢迎报名参加',
    publishTime: '2024-11-08 18:00'
  },
  {
    id: 3,
    type: 'maintenance',
    title: '消防设施检查',
    description: '物业将于下周一对各楼栋消防设施进行例行检查',
    publishTime: '2024-11-08 15:00'
  }
])

// 充值表单
const rechargeForm = reactive({
  amount: 100,
  paymentMethod: 'wechat'
})

// 获取状态标签
const getStatusTag = (status) => {
  return status === 1 ? 'success' : 'danger'
}

// 获取状态名称
const getStatusName = (status) => {
  return status === 1 ? '正常' : '异常'
}

// 获取动态类型图标
const getUpdateIcon = (type) => {
  const iconMap = {
    'notice': Bell,
    'activity': Star,
    'maintenance': Warning,
    'message': Message
  }
  return iconMap[type] || InfoFilled
}

// 获取动态类型颜色
const getUpdateTypeColor = (type) => {
  const colorMap = {
    'notice': '#E6A23C',
    'activity': '#67C23A',
    'maintenance': '#F56C6C',
    'message': '#409EFF'
  }
  return colorMap[type] || '#909399'
}

// 导航
const navigateTo = (path) => {
  router.push(path)
}

// 充值
const handleRecharge = () => {
  rechargeDialogVisible.value = true
}

// 提交充值
const handleRechargeSubmit = () => {
  ElMessage.success(`充值成功，充值金额：¥${rechargeForm.amount}`)
  wallet.value.balance += rechargeForm.amount
  rechargeDialogVisible.value = false
}

// 组件挂载
onMounted(() => {
  // 模拟获取用户信息
  userInfo.value = {
    realName: userStore.realName || '张三',
    houseInfo: '3号楼2单元501',
    checkInDays: Math.floor(Math.random() * 1000) + 100,
    creditPoints: Math.floor(Math.random() * 30) + 70
  }

  // 模拟获取未缴账单数量
  unpaidCount.value = Math.floor(Math.random() * 5)

  // 模拟获取未读公告数量
  unreadNoticeCount.value = Math.floor(Math.random() * 5) + 1
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
        justify-content: space-between;
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

        .welcome-weather {
          .weather-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .weather-details {
              .temperature {
                font-size: 20px;
                font-weight: bold;
                color: #333;
              }

              .weather-desc {
                font-size: 14px;
                color: #666;
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

        .balance-amount {
          font-size: 32px;
          font-weight: bold;
          color: #409EFF;
          margin-bottom: 8px;
        }

        .balance-desc {
          font-size: 14px;
          color: #666;
          margin-bottom: 16px;
        }

        .balance-actions {
          display: flex;
          justify-content: center;
          gap: 16px;
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
            font-size: 18px;
            font-weight: bold;
            color: #F56C6C;
          }
        }

        .bill-status {
          margin-bottom: 16px;
        }

        .bill-details {
          margin-bottom: 16px;

          .bill-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 14px;

            span:first-child {
              color: #666;
            }

            span:last-child {
              color: #333;
              font-weight: 500;
            }
          }
        }

        .bill-actions {
          display: flex;
          gap: 12px;
        }
      }
    }
  }

  .service-status {
    margin-bottom: 20px;

    .status-card {
      .status-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;

        .status-item {
          padding: 16px;
          border: 1px solid #eee;
          border-radius: 8px;

          .status-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .service-name {
              font-weight: bold;
              color: #333;
            }
          }

          .status-desc {
            font-size: 14px;
            color: #666;
            margin-bottom: 8px;
          }

          .status-time {
            font-size: 12px;
            color: #999;
          }
        }
      }
    }
  }

  .community-updates {
    .updates-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .updates-list {
        .update-item {
          display: flex;
          gap: 12px;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .update-icon {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: #f5f7fa;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
          }

          .update-content {
            flex: 1;

            .update-title {
              font-weight: bold;
              color: #333;
              margin-bottom: 4px;
            }

            .update-desc {
              font-size: 14px;
              color: #666;
              margin-bottom: 4px;
            }

            .update-time {
              font-size: 12px;
              color: #999;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .portal-dashboard {
    padding: 10px;

    .welcome-section .welcome-card .welcome-content {
      flex-direction: column;
      gap: 16px;
      text-align: center;
    }

    .quick-actions .actions-card .actions-grid {
      grid-template-columns: repeat(2, 1fr);
    }

    .account-overview .overview-card {
      margin-bottom: 16px;
    }

    .service-status .status-card .status-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>