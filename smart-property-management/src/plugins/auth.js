import { useUserStore } from '@/stores/user'

/**
 * 认证初始化插件
 * 在应用启动时初始化用户认证状态
 */
export default {
  install(app) {
    // 在应用挂载后初始化用户状态
    app.mixin({
      created() {
        // 只在根组件中执行一次
        if (this.$parent === null) {
          const userStore = useUserStore()
          userStore.initializeAuth()
        }
      }
    })
  }
}