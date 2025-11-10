import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: true,
      withoutAnimation: false
    },
    device: 'desktop',
    size: 'default',
    theme: 'light',
    language: 'zh-cn'
  }),

  getters: {
    sidebarStatus: (state) => state.sidebar.opened,
    deviceType: (state) => state.device,
    currentSize: (state) => state.size,
    currentTheme: (state) => state.theme,
    currentLanguage: (state) => state.language
  },

  actions: {
    // 切换侧边栏
    toggleSidebar(withoutAnimation = false) {
      this.sidebar.opened = !this.sidebar.opened
      this.sidebar.withoutAnimation = withoutAnimation
    },

    // 关闭侧边栏
    closeSidebar(withoutAnimation = false) {
      this.sidebar.opened = false
      this.sidebar.withoutAnimation = withoutAnimation
    },

    // 设置设备类型
    setDevice(device) {
      this.device = device
    },

    // 设置组件尺寸
    setSize(size) {
      this.size = size
    },

    // 设置主题
    setTheme(theme) {
      this.theme = theme
      // 可以在这里添加主题切换的逻辑
    },

    // 设置语言
    setLanguage(language) {
      this.language = language
    }
  },

  persist: {
    key: 'app-store',
    storage: localStorage,
    paths: ['sidebar', 'size', 'theme', 'language']
  }
})