<template>
  <div class="test-page">
    <h1>测试页面</h1>
    <p>如果你能看到这个页面，说明路由正常工作</p>
    <el-button type="primary" @click="testClick">测试按钮</el-button>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const testClick = () => {
  ElMessage.success('按钮点击正常！')
}
</script>

<style scoped>
.test-page {
  padding: 20px;
  text-align: center;
}

h1 {
  color: #409eff;
  margin-bottom: 20px;
}

p {
  font-size: 16px;
  margin-bottom: 20px;
}
</style>