<template>
  <div class="upload-container">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="headers"
      :data="uploadData"
      :name="name"
      :with-credentials="withCredentials"
      :multiple="multiple"
      :accept="accept"
      :list-type="listType"
      :auto-upload="autoUpload"
      :disabled="disabled"
      :limit="limit"
      :file-list="fileList"
      :drag="drag"
      :show-file-list="showFileList"
      :before-upload="beforeUpload"
      :before-remove="beforeRemove"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      :on-change="handleChange"
      :on-exceed="handleExceed"
      :http-request="httpRequest"
    >
      <!-- 拖拽上传 -->
      <template v-if="drag">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip" v-if="tip">
            {{ tip }}
          </div>
        </template>
      </template>

      <!-- 按钮上传 -->
      <template v-else-if="listType !== 'picture-card'">
        <el-button type="primary">
          <el-icon><upload /></el-icon>
          {{ buttonText }}
        </el-button>
        <template #tip>
          <div class="el-upload__tip" v-if="tip">
            {{ tip }}
          </div>
        </template>
      </template>

      <!-- 图片卡片上传 -->
      <template v-else>
        <el-icon><plus /></el-icon>
        <template #tip>
          <div class="el-upload__tip" v-if="tip">
            {{ tip }}
          </div>
        </template>
      </template>
    </el-upload>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="50%"
      append-to-body
    >
      <img
        v-if="previewUrl"
        :src="previewUrl"
        alt="预览图片"
        style="width: 100%; max-height: 500px; object-fit: contain;"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Upload, Plus } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 上传配置
  action: {
    type: String,
    default: '/api/upload'
  },
  headers: {
    type: Object,
    default: () => ({})
  },
  data: {
    type: Object,
    default: () => ({})
  },
  name: {
    type: String,
    default: 'file'
  },
  withCredentials: {
    type: Boolean,
    default: false
  },

  // 文件配置
  multiple: {
    type: Boolean,
    default: false
  },
  accept: {
    type: String,
    default: ''
  },
  limit: {
    type: Number,
    default: 1
  },
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 10MB
  },

  // 显示配置
  listType: {
    type: String,
    default: 'text' // text, picture, picture-card
  },
  showFileList: {
    type: Boolean,
    default: true
  },
  drag: {
    type: Boolean,
    default: false
  },
  buttonText: {
    type: String,
    default: '点击上传'
  },
  tip: {
    type: String,
    default: ''
  },

  // 功能配置
  autoUpload: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  preview: {
    type: Boolean,
    default: true
  },

  // 数据
  modelValue: {
    type: [Array, String],
    default: () => []
  }
})

// Emits
const emit = defineEmits([
  'update:modelValue',
  'success',
  'error',
  'progress',
  'change',
  'remove',
  'preview'
])

// Refs
const uploadRef = ref()
const previewVisible = ref(false)
const previewUrl = ref('')

// Data
const fileList = ref([])

// Computed
const uploadUrl = computed(() => props.action)
const uploadData = computed(() => props.data)

// Watch
watch(
  () => props.modelValue,
  (newVal) => {
    if (typeof newVal === 'string') {
      fileList.value = newVal ? [{ name: newVal, url: newVal }] : []
    } else {
      fileList.value = newVal || []
    }
  },
  { immediate: true }
)

// Methods
const beforeUpload = (file) => {
  // 检查文件类型
  if (props.accept) {
    const acceptTypes = props.accept.split(',').map(type => type.trim())
    const fileType = '.' + file.name.split('.').pop().toLowerCase()
    const isValidType = acceptTypes.some(type => {
      if (type.startsWith('.')) {
        return fileType === type.toLowerCase()
      }
      return file.type.includes(type)
    })

    if (!isValidType) {
      ElMessage.error(`文件类型不符合要求，请上传 ${props.accept} 格式的文件`)
      return false
    }
  }

  // 检查文件大小
  if (file.size > props.maxSize) {
    const sizeText = props.maxSize > 1024 * 1024
      ? `${props.maxSize / (1024 * 1024)}MB`
      : `${props.maxSize / 1024}KB`
    ElMessage.error(`文件大小不能超过 ${sizeText}`)
    return false
  }

  return true
}

const beforeRemove = (file, fileList) => {
  if (!props.multiple || fileList.length <= 1) {
    return true
  }

  return ElMessageBox.confirm(`确定要移除 ${file.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return true
  }).catch(() => {
    return false
  })
}

const handlePreview = (file) => {
  if (!props.preview) return

  // 如果是图片文件，显示预览
  const isImage = file.raw && file.raw.type.startsWith('image/')
  if (isImage || file.url) {
    previewUrl.value = file.url || URL.createObjectURL(file.raw)
    previewVisible.value = true
  }

  emit('preview', file)
}

const handleRemove = (file, fileList) => {
  const updatedFiles = fileList.map(item => {
    if (item.response && item.response.data) {
      return item.response.data.url || item.response.data
    }
    return item.url
  })

  if (props.multiple) {
    emit('update:modelValue', updatedFiles)
  } else {
    emit('update:modelValue', '')
  }

  emit('remove', file, fileList)
}

const handleSuccess = (response, file, fileList) => {
  ElMessage.success('上传成功')

  // 处理响应数据
  const fileUrl = response.data?.url || response.url || file.url

  if (props.multiple) {
    const updatedFiles = fileList.map(item => {
      if (item.response && item.response.data) {
        return item.response.data.url || item.response.data
      }
      return item.url
    })
    emit('update:modelValue', updatedFiles)
  } else {
    emit('update:modelValue', fileUrl)
  }

  emit('success', response, file, fileList)
}

const handleError = (error, file, fileList) => {
  ElMessage.error('上传失败')
  emit('error', error, file, fileList)
}

const handleProgress = (event, file, fileList) => {
  emit('progress', event, file, fileList)
}

const handleChange = (file, fileList) => {
  emit('change', file, fileList)
}

const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

const httpRequest = (options) => {
  // 可以在这里自定义上传逻辑
  // 例如使用 FormData 进行上传
  const formData = new FormData()
  formData.append(props.name, options.file)

  // 添加额外的数据
  Object.keys(uploadData.value).forEach(key => {
    formData.append(key, uploadData.value[key])
  })

  // 使用 XMLHttpRequest 进行上传
  const xhr = new XMLHttpRequest()

  // 监听上传进度
  xhr.upload.addEventListener('progress', (event) => {
    if (event.lengthComputable) {
      const progress = Math.round((event.loaded / event.total) * 100)
      options.onProgress({ percent: progress })
    }
  })

  // 监听完成
  xhr.addEventListener('load', () => {
    if (xhr.status >= 200 && xhr.status < 300) {
      try {
        const response = JSON.parse(xhr.responseText)
        options.onSuccess(response)
      } catch (error) {
        options.onError(new Error('解析响应失败'))
      }
    } else {
      options.onError(new Error(`上传失败，状态码：${xhr.status}`))
    }
  })

  // 监听错误
  xhr.addEventListener('error', () => {
    options.onError(new Error('网络错误'))
  })

  // 发送请求
  xhr.open('POST', uploadUrl.value, true)

  // 设置请求头
  Object.keys(props.headers).forEach(key => {
    xhr.setRequestHeader(key, props.headers[key])
  })

  xhr.send(formData)
}

// 暴露方法
defineExpose({
  uploadRef,
  clearFiles: () => uploadRef.value?.clearFiles(),
  abort: (file) => uploadRef.value?.abort(file),
  submit: () => uploadRef.value?.submit()
})
</script>

<style lang="scss" scoped>
.upload-container {
  :deep(.el-upload) {
    .el-upload-dragger {
      width: 100%;
      height: 180px;
    }
  }

  .el-upload__tip {
    color: #999;
    font-size: 12px;
    margin-top: 8px;
  }
}
</style>