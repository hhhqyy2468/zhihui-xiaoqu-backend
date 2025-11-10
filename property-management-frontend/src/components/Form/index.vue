<template>
  <div class="form-container">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
      :label-suffix="labelSuffix"
      :hide-required-asterisk="hideRequiredAsterisk"
      :show-message="showMessage"
      :inline-message="inlineMessage"
      :status-icon="statusIcon"
      :disabled="disabled"
      :validate-on-rule-change="validateOnRuleChange"
      :size="size"
      @validate="handleValidate"
    >
      <template v-for="item in formItems" :key="item.prop">
        <!-- 动态表单项 -->
        <el-form-item
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
          :error="item.error"
          :show-message="item.showMessage"
          :inline="item.inline"
          :label-width="item.labelWidth"
        >
          <!-- 输入框 -->
          <el-input
            v-if="item.type === 'input'"
            v-model="formData[item.prop]"
            :placeholder="item.placeholder || `请输入${item.label}`"
            :type="item.inputType || 'text'"
            :maxlength="item.maxlength"
            :show-word-limit="item.showWordLimit"
            :clearable="item.clearable !== false"
            :show-password="item.showPassword"
            :disabled="item.disabled"
            :size="item.size || size"
            :prefix-icon="item.prefixIcon"
            :suffix-icon="item.suffixIcon"
            :rows="item.rows"
            :autosize="item.autosize"
            :resize="item.resize"
            :readonly="item.readonly"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
            @change="handleChange(item, $event)"
            @input="handleInput(item, $event)"
          />

          <!-- 数字输入框 -->
          <el-input-number
            v-else-if="item.type === 'number'"
            v-model="formData[item.prop]"
            :min="item.min"
            :max="item.max"
            :step="item.step"
            :step-strictly="item.stepStrictly"
            :precision="item.precision"
            :controls-position="item.controlsPosition"
            :disabled="item.disabled"
            :size="item.size || size"
            @change="handleChange(item, $event)"
          />

          <!-- 选择器 -->
          <el-select
            v-else-if="item.type === 'select'"
            v-model="formData[item.prop]"
            :placeholder="item.placeholder || `请选择${item.label}`"
            :multiple="item.multiple"
            :clearable="item.clearable !== false"
            :collapse-tags="item.collapseTags"
            :collapse-tags-tooltip="item.collapseTagsTooltip"
            :multiple-limit="item.multipleLimit"
            :filterable="item.filterable"
            :allow-create="item.allowCreate"
            :default-first-option="item.defaultFirstOption"
            :reserve-keyword="item.reserveKeyword"
            :no-match-text="item.noMatchText"
            :no-data-text="item.noDataText"
            :popper-class="item.popperClass"
            :remote="item.remote"
            :remote-method="item.remoteMethod"
            :loading="item.loading"
            :loading-text="item.loadingText"
            :disabled="item.disabled"
            :size="item.size || size"
            @change="handleChange(item, $event)"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
            @clear="handleClear(item)"
          >
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
              :disabled="option.disabled"
            />
          </el-select>

          <!-- 级联选择器 -->
          <el-cascader
            v-else-if="item.type === 'cascader'"
            v-model="formData[item.prop]"
            :options="item.options"
            :props="item.props"
            :size="item.size || size"
            :placeholder="item.placeholder || `请选择${item.label}`"
            :clearable="item.clearable !== false"
            :disabled="item.disabled"
            :filterable="item.filterable"
            :separator="item.separator"
            @change="handleChange(item, $event)"
            @expand-change="handleExpandChange(item, $event)"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
            @visible-change="handleVisibleChange(item, $event)"
          />

          <!-- 日期选择器 -->
          <el-date-picker
            v-else-if="item.type === 'date'"
            v-model="formData[item.prop]"
            :type="item.dateType || 'date'"
            :placeholder="item.placeholder || `请选择${item.label}`"
            :format="item.format"
            :value-format="item.valueFormat"
            :readonly="item.readonly"
            :disabled="item.disabled"
            :editable="item.editable"
            :clearable="item.clearable !== false"
            :size="item.size || size"
            :start-placeholder="item.startPlaceholder"
            :end-placeholder="item.endPlaceholder"
            :range-separator="item.rangeSeparator"
            :default-value="item.defaultValue"
            :default-time="item.defaultTime"
            :name="item.name"
            :unlink-panels="item.unlinkPanels"
            :prefix-icon="item.prefixIcon"
            :clear-icon="item.clearIcon"
            @change="handleChange(item, $event)"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
            @calendar-change="handleCalendarChange(item, $event)"
          />

          <!-- 时间选择器 -->
          <el-time-picker
            v-else-if="item.type === 'time'"
            v-model="formData[item.prop]"
            :is-range="item.isRange"
            :placeholder="item.placeholder || `请选择${item.label}`"
            :format="item.format"
            :value-format="item.valueFormat"
            :readonly="item.readonly"
            :disabled="item.disabled"
            :editable="item.editable"
            :clearable="item.clearable !== false"
            :size="item.size || size"
            :start-placeholder="item.startPlaceholder"
            :end-placeholder="item.endPlaceholder"
            :range-separator="item.rangeSeparator"
            :arrow-control="item.arrowControl"
            :name="item.name"
            @change="handleChange(item, $event)"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
          />

          <!-- 单选框组 -->
          <el-radio-group
            v-else-if="item.type === 'radio'"
            v-model="formData[item.prop]"
            :size="item.size || size"
            :disabled="item.disabled"
            :text-color="item.textColor"
            :fill="item.fill"
            @change="handleChange(item, $event)"
          >
            <el-radio
              v-for="option in item.options"
              :key="option.value"
              :label="option.value"
              :disabled="option.disabled"
              :border="option.border"
              :size="option.size"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>

          <!-- 复选框组 -->
          <el-checkbox-group
            v-else-if="item.type === 'checkbox'"
            v-model="formData[item.prop]"
            :size="item.size || size"
            :disabled="item.disabled"
            :text-color="item.textColor"
            :fill="item.fill"
            @change="handleChange(item, $event)"
          >
            <el-checkbox
              v-for="option in item.options"
              :key="option.value"
              :label="option.value"
              :disabled="option.disabled"
              :border="option.border"
              :size="option.size"
              :true-label="option.trueLabel"
              :false-label="option.falseLabel"
              :indeterminate="option.indeterminate"
            >
              {{ option.label }}
            </el-checkbox>
          </el-checkbox-group>

          <!-- 开关 -->
          <el-switch
            v-else-if="item.type === 'switch'"
            v-model="formData[item.prop]"
            :disabled="item.disabled"
            :loading="item.loading"
            :size="item.size"
            :width="item.width"
            :inline-prompt="item.inlinePrompt"
            :active-icon="item.activeIcon"
            :inactive-icon="item.inactiveIcon"
            :active-text="item.activeText"
            :inactive-text="item.inactiveText"
            :active-value="item.activeValue"
            :inactive-value="item.inactiveValue"
            :active-color="item.activeColor"
            :inactive-color="item.inactiveColor"
            :border-color="item.borderColor"
            :validate-event="item.validateEvent"
            :before-change="item.beforeChange"
            :loading-icon="item.loadingIcon"
            @change="handleChange(item, $event)"
          />

          <!-- 滑块 -->
          <el-slider
            v-else-if="item.type === 'slider'"
            v-model="formData[item.prop]"
            :min="item.min"
            :max="item.max"
            :disabled="item.disabled"
            :step="item.step"
            :show-input="item.showInput"
            :show-input-controls="item.showInputControls"
            :input-size="item.inputSize"
            :show-stops="item.showStops"
            :show-tooltip="item.showTooltip"
            :format-tooltip="item.formatTooltip"
            :range="item.range"
            :vertical="item.vertical"
            :height="item.height"
            :label="item.label"
            :marks="item.marks"
            :range-start-label="item.rangeStartLabel"
            :range-end-label="item.rangeEndLabel"
            @change="handleChange(item, $event)"
          />

          <!-- 评分 -->
          <el-rate
            v-else-if="item.type === 'rate'"
            v-model="formData[item.prop]"
            :max="item.max"
            :disabled="item.disabled"
            :allow-half="item.allowHalf"
            :low-threshold="item.lowThreshold"
            :high-threshold="item.highThreshold"
            :colors="item.colors"
            :void-color="item.voidColor"
            :disabled-void-color="item.disabledVoidColor"
            :icon-classes="item.iconClasses"
            :void-icon-class="item.voidIconClass"
            :disabled-void-icon-class="item.disabledVoidIconClass"
            :show-text="item.showText"
            :show-score="item.showScore"
            :texts="item.texts"
            :score-template="item.scoreTemplate"
            :size="item.size"
            @change="handleChange(item, $event)"
          />

          <!-- 颜色选择器 -->
          <el-color-picker
            v-else-if="item.type === 'color'"
            v-model="formData[item.prop]"
            :disabled="item.disabled"
            :size="item.size"
            :show-alpha="item.showAlpha"
            :color-format="item.colorFormat"
            :popper-class="item.popperClass"
            :predefine="item.predefine"
            @change="handleChange(item, $event)"
            @active-change="handleActiveChange(item, $event)"
          />

          <!-- 上传组件 -->
          <el-upload
            v-else-if="item.type === 'upload'"
            :action="item.action"
            :headers="item.headers"
            :method="item.method"
            :multiple="item.multiple"
            :data="item.data"
            :name="item.name"
            :with-credentials="item.withCredentials"
            :show-file-list="item.showFileList !== false"
            :drag="item.drag"
            :accept="item.accept"
            :on-preview="item.onPreview"
            :on-remove="item.onRemove"
            :on-success="item.onSuccess"
            :on-error="item.onError"
            :on-progress="item.onProgress"
            :on-change="item.onChange"
            :before-upload="item.beforeUpload"
            :before-remove="item.beforeRemove"
            :http-request="item.httpRequest"
            :filter="item.filter"
            :limit="item.limit"
            :on-exceed="item.onExceed"
            :disabled="item.disabled"
            :list-type="item.listType"
            :auto-upload="item.autoUpload"
            :file-list="item.fileList"
            :cross-origin="item.crossOrigin"
          >
            <template v-if="item.drag">
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
            </template>
            <template v-else>
              <el-button type="primary">点击上传</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip" v-if="item.tip">
                {{ item.tip }}
              </div>
            </template>
          </el-upload>

          <!-- 文本域 -->
          <el-input
            v-else-if="item.type === 'textarea'"
            v-model="formData[item.prop]"
            type="textarea"
            :placeholder="item.placeholder || `请输入${item.label}`"
            :maxlength="item.maxlength"
            :show-word-limit="item.showWordLimit"
            :rows="item.rows || 4"
            :autosize="item.autosize"
            :resize="item.resize"
            :readonly="item.readonly"
            :disabled="item.disabled"
            @blur="handleBlur(item, $event)"
            @focus="handleFocus(item, $event)"
            @change="handleChange(item, $event)"
            @input="handleInput(item, $event)"
          />

          <!-- 自定义插槽 -->
          <slot
            v-else-if="item.type === 'slot'"
            :name="item.prop"
            :row="formData"
            :item="item"
          />

          <!-- 纯文本显示 -->
          <span v-else-if="item.type === 'text'">{{ formData[item.prop] }}</span>
        </el-form-item>
      </template>

      <!-- 表单按钮 -->
      <el-form-item v-if="showButtons">
        <el-button
          v-if="showSubmit"
          type="primary"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          {{ submitText }}
        </el-button>
        <el-button
          v-if="showReset"
          @click="handleReset"
        >
          {{ resetText }}
        </el-button>
        <el-button
          v-if="showCancel"
          @click="handleCancel"
        >
          {{ cancelText }}
        </el-button>
        <slot name="buttons"></slot>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 表单数据
  modelValue: {
    type: Object,
    default: () => ({})
  },

  // 表单配置
  formItems: {
    type: Array,
    default: () => []
  },
  rules: {
    type: Object,
    default: () => ({})
  },

  // 表单配置
  labelWidth: {
    type: [String, Number],
    default: '120px'
  },
  labelPosition: {
    type: String,
    default: 'right'
  },
  labelSuffix: {
    type: String,
    default: ':'
  },
  hideRequiredAsterisk: {
    type: Boolean,
    default: false
  },
  showMessage: {
    type: Boolean,
    default: true
  },
  inlineMessage: {
    type: Boolean,
    default: false
  },
  statusIcon: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  validateOnRuleChange: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  },

  // 按钮配置
  showButtons: {
    type: Boolean,
    default: true
  },
  showSubmit: {
    type: Boolean,
    default: true
  },
  showReset: {
    type: Boolean,
    default: true
  },
  showCancel: {
    type: Boolean,
    default: false
  },
  submitText: {
    type: String,
    default: '提交'
  },
  resetText: {
    type: String,
    default: '重置'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  submitLoading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits([
  'update:modelValue',
  'submit',
  'reset',
  'cancel',
  'validate',
  'field-change',
  'field-blur',
  'field-focus'
])

// Refs
const formRef = ref()

// Data
const formData = reactive({})

// Watch
watch(
  () => props.modelValue,
  (newVal) => {
    Object.assign(formData, newVal)
  },
  { deep: true, immediate: true }
)

watch(
  formData,
  (newVal) => {
    emit('update:modelValue', { ...newVal })
  },
  { deep: true }
)

// Methods
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    emit('submit', { ...formData })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleReset = () => {
  formRef.value?.resetFields()
  emit('reset')
}

const handleCancel = () => {
  emit('cancel')
}

const handleValidate = (prop, isValid, message) => {
  emit('validate', prop, isValid, message)
}

// 字段事件处理
const handleChange = (item, value) => {
  emit('field-change', item.prop, value, item)
}

const handleBlur = (item, event) => {
  emit('field-blur', item.prop, event, item)
}

const handleFocus = (item, event) => {
  emit('field-focus', item.prop, event, item)
}

const handleInput = (item, event) => {
  emit('field-input', item.prop, event, item)
}

const handleClear = (item) => {
  emit('field-clear', item.prop, item)
}

const handleExpandChange = (item, value) => {
  emit('field-expand-change', item.prop, value, item)
}

const handleVisibleChange = (item, visible) => {
  emit('field-visible-change', item.prop, visible, item)
}

const handleCalendarChange = (item, value) => {
  emit('field-calendar-change', item.prop, value, item)
}

const handleActiveChange = (item, value) => {
  emit('field-active-change', item.prop, value, item)
}

// 暴露方法
defineExpose({
  formRef,
  validate: (callback) => formRef.value?.validate(callback),
  validateField: (props, callback) => formRef.value?.validateField(props, callback),
  resetFields: () => formRef.value?.resetFields(),
  scrollToField: (prop) => formRef.value?.scrollToField(prop),
  clearValidate: (props) => formRef.value?.clearValidate(props)
})
</script>

<style lang="scss" scoped>
.form-container {
  .el-form {
    .el-form-item {
      margin-bottom: 22px;
    }
  }
}
</style>