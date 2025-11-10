<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'sidebar-collapsed': !sidebarOpened }">
      <div class="sidebar-logo">
        <img src="@/assets/images/logo.png" alt="Logo" class="logo-img" />
        <h1 v-show="sidebarOpened" class="logo-title">物业管理系统</h1>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="!sidebarOpened"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard" v-user-type="[1, 2, 4]">
          <el-icon><House /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <!-- 系统管理 - 仅系统管理员可见 -->
        <el-sub-menu index="/system" v-user-type="1">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            用户管理
          </el-menu-item>
          <el-menu-item index="/system/role">
            角色管理
          </el-menu-item>
          <el-menu-item index="/system/menu">
            菜单管理
          </el-menu-item>
          <el-menu-item index="/system/dict">
            字典管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 物业管理 - 系统管理员和物业管理员可见 -->
        <el-sub-menu index="/property" v-user-type="[1, 2]">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>物业管理</span>
          </template>
          <el-menu-item index="/property/building">
            楼栋管理
          </el-menu-item>
          <el-menu-item index="/property/unit">
            单元管理
          </el-menu-item>
          <el-menu-item index="/property/house">
            房产管理
          </el-menu-item>
          <el-menu-item index="/property/owner">
            业主管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 费用管理 - 系统管理员和物业管理员可见 -->
        <el-sub-menu index="/finance" v-user-type="[1, 2]">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>费用管理</span>
          </template>
          <el-menu-item index="/finance/feetype">
            费用类型管理
          </el-menu-item>
          <el-menu-item index="/finance/bill">
            账单管理
          </el-menu-item>
          <el-menu-item index="/finance/wallet">
            虚拟钱包管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 服务管理 - 系统管理员和物业管理员可见 -->
        <el-sub-menu index="/service" v-user-type="[1, 2]">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>服务管理</span>
          </template>
          <el-menu-item index="/service/complaint">
            投诉管理
          </el-menu-item>
          <el-menu-item index="/service/repair">
            维修管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 停车管理 - 系统管理员和物业管理员可见 -->
        <el-sub-menu index="/parking" v-user-type="[1, 2]">
          <template #title>
            <el-icon><Van /></el-icon>
            <span>停车管理</span>
          </template>
          <el-menu-item index="/parking/space">
            车位管理
          </el-menu-item>
          <el-menu-item index="/parking/rental">
            租赁管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 公告管理 - 系统管理员和物业管理员可见 -->
        <el-sub-menu index="/notice" v-user-type="[1, 2]">
          <template #title>
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </template>
          <el-menu-item index="/notice/publish">
            公告发布
          </el-menu-item>
        </el-sub-menu>

        <!-- 系统日志 - 仅系统管理员可见 -->
        <el-sub-menu index="/log" v-user-type="1">
          <template #title>
            <el-icon><View /></el-icon>
            <span>系统日志</span>
          </template>
          <el-menu-item index="/log/operation">
            操作日志
          </el-menu-item>
          <el-menu-item index="/log/login">
            登录日志
          </el-menu-item>
        </el-sub-menu>

        <!-- 业主门户菜单 - 仅业主可见 -->
        <el-menu-item index="/portal/dashboard" v-user-type="3">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/portal/bills" v-user-type="3">
          <el-icon><Money /></el-icon>
          <template #title>我的账单</template>
        </el-menu-item>

        <el-menu-item index="/portal/wallet" v-user-type="3">
          <el-icon><CreditCard /></el-icon>
          <template #title>我的钱包</template>
        </el-menu-item>

        <el-menu-item index="/portal/house" v-user-type="3">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>我的房产</template>
        </el-menu-item>

        <el-menu-item index="/portal/parking" v-user-type="3">
          <el-icon><Van /></el-icon>
          <template #title>我的车位</template>
        </el-menu-item>

        <el-menu-item index="/portal/complaint" v-user-type="3">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>我的投诉</template>
        </el-menu-item>

        <el-menu-item index="/portal/repair" v-user-type="3">
          <el-icon><Tools /></el-icon>
          <template #title>我的报修</template>
        </el-menu-item>

        <el-menu-item index="/portal/announcement" v-user-type="3">
          <el-icon><Bell /></el-icon>
          <template #title>社区公告</template>
        </el-menu-item>

        <!-- 我的工作 - 仅维修人员可见 -->
        <el-sub-menu index="/work" v-user-type="4">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>我的工作</span>
          </template>
          <el-menu-item index="/work/pending">
            待接单
          </el-menu-item>
          <el-menu-item index="/work/processing">
            进行中
          </el-menu-item>
          <el-menu-item index="/work/pending-accept">
            待验收
          </el-menu-item>
          <el-menu-item index="/work/completed">
            已完成
          </el-menu-item>
        </el-sub-menu>

        <!-- 社区公告 - 维修人员可见 -->
        <el-sub-menu index="/community-notice" v-user-type="4">
          <template #title>
            <el-icon><Bell /></el-icon>
            <span>社区公告</span>
          </template>
          <el-menu-item index="/community-notice/view">
            公告查看
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 主容器 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-icon class="toggle-sidebar" @click="toggleSidebar">
            <Fold v-if="sidebarOpened" />
            <Expand v-else />
          </el-icon>

          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="navbar-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.avatar">
                {{ userStore.realName.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  House,
  Setting,
  OfficeBuilding,
  Money,
  Tools,
  Van,
  Bell,
  User,
  View,
  Fold,
  Expand,
  ArrowDown,
  CreditCard,
  ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const sidebarOpened = computed(() => appStore.sidebarStatus)
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile/index')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
}

.sidebar-container {
  width: 210px;
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;

  &.sidebar-collapsed {
    width: 64px;
  }

  .sidebar-logo {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 16px;
    background: rgba(255, 255, 255, 0.1);

    .logo-img {
      width: 32px;
      height: 32px;
    }

    .logo-title {
      margin-left: 12px;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 50px;
    line-height: 50px;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;

  .navbar-left {
    display: flex;
    align-items: center;

    .toggle-sidebar {
      font-size: 20px;
      cursor: pointer;
      margin-right: 16px;
      padding: 10px;

      &:hover {
        background: #f0f0f0;
        border-radius: 4px;
      }
    }
  }

  .navbar-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 8px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background: #f0f0f0;
      }

      .username {
        margin: 0 8px;
      }
    }
  }
}

.app-main {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f0f2f5;
}
</style>