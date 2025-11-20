<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ getPageTitle() }}</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ getBreadcrumbParent() }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ getPageTitle() }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

  
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="工单编号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入工单编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="维修类型">
          <el-select
            v-model="searchForm.repairType"
            placeholder="请选择维修类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in repairTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select
            v-model="searchForm.orderStatus"
            placeholder="请选择工单状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in orderStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入联系电话"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      
      <el-button
        v-if="currentUserRole !== 4"
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        {{ currentUserRole === 3 ? '新增报修' : '新增工单' }}
      </el-button>
      <el-button
        v-if="currentUserRole !== 3"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出
      </el-button>
      <el-button
        v-if="currentUserRole === 1"
        type="info"
        @click="handleViewStats"
      >
        <el-icon><DataAnalysis /></el-icon>
        维修统计
      </el-button>
      <el-button
        v-if="currentUserRole === 1"
        type="warning"
        @click="handleViewArchives"
      >
        <el-icon><FolderOpened /></el-icon>
        归档记录
      </el-button>
    </div>

    <!-- 维修工单表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
      >
        <el-table-column prop="orderNo" label="工单编号" width="160" sortable />
        <el-table-column
          v-if="currentUserRole !== 3"
          prop="realName"
          label="报修人"
          width="120"
        />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column
          v-if="currentUserRole !== 4"
          label="维修人员"
          width="180"
        >
          <template #default="{ row }">
            <span v-if="row.workerName">{{ row.workerName }}{{ row.workerPhone ? '-' + row.workerPhone : '' }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="houseNo" label="房间编号" width="140" />
        <el-table-column prop="repairType" label="维修类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.repairType)">
              {{ getTypeName(row.repairType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyTag(row.urgencyLevel)">
              {{ getUrgencyName(row.urgencyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="工单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.orderStatus)">
              {{ getStatusName(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="报修时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.reportTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="faultDescription" label="故障描述" width="200" show-overflow-tooltip />
        <el-table-column label="故障图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrls && row.imageUrls.length > 0 && isValidImageUrl(row.imageUrls[0])"
              :src="getImageUrl(row.imageUrls[0])"
              :preview-src-list="row.imageUrls.filter(url => isValidImageUrl(url)).map(url => getImageUrl(url))"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              :preview-teleported="true"
              @error="handleImageError"
            />
            <span v-else class="no-image">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="repairCost" label="维修费用" width="120" sortable>
          <template #default="{ row }">
            <span class="price-text">¥{{ row.repairCost ? row.repairCost.toFixed(2) : '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>

            <!-- 物业经理操作 -->
            <template v-if="currentUserRole === 1">
              <el-button
                v-if="row.orderStatus === 0"
                link
                type="warning"
                @click="handleAssign(row)"
              >
                派工
              </el-button>
              <el-button
                v-if="row.orderStatus === 4 && row.isRated"
                link
                type="success"
                @click="handleFinalProcess(row)"
              >
                完成处理
              </el-button>
              <el-button
                v-if="row.orderStatus === 4 && row.isRated && row.inspectResult === 0"
                link
                type="warning"
                @click="handleReassign(row)"
              >
                重新派工
              </el-button>
            </template>

            <!-- 维修师傅操作 -->
            <template v-if="currentUserRole === 4">
              <el-button
                v-if="row.orderStatus === 1"
                link
                type="success"
                @click="handleAcceptTask(row)"
              >
                接单
              </el-button>
              <el-button
                v-if="row.orderStatus === 2"
                link
                type="primary"
                @click="handleRepair(row)"
              >
                提交
              </el-button>
            </template>

            <!-- 业主操作 -->
            <template v-if="currentUserRole === 3">
              <el-button
                v-if="row.orderStatus === 3"
                link
                type="info"
                @click="handleRate(row)"
              >
                评价
              </el-button>
              <el-button
                v-if="row.orderStatus === 0"
                link
                type="danger"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
              <el-button
                v-else
                link
                type="info"
                disabled
                class="disabled-button"
              >
                删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑工单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item v-if="currentUserRole !== 3" label="报修人" prop="reporter">
          <el-input v-model="form.reporter" placeholder="请输入报修人姓名" />
        </el-form-item>
        <el-form-item v-if="currentUserRole !== 3" label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item v-if="currentUserRole !== 3" label="房间编号" prop="houseCode">
          <el-input v-model="form.houseCode" placeholder="请输入房间编号" />
        </el-form-item>
        <el-form-item label="维修类型" prop="repairType">
          <el-select v-model="form.repairType" placeholder="请选择维修类型" style="width: 100%">
            <el-option
              v-for="item in repairTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgencyLevel">
          <el-radio-group v-model="form.urgencyLevel">
            <el-radio
              v-for="item in urgencyLevelOptions"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="故障描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请详细描述故障情况"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="故障图片">
          <el-upload
            v-model:file-list="form.images"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="工单详情"
      width="900px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">
          {{ currentOrder.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="报修人">
          {{ currentOrder.realName || currentOrder.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentOrder.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="房间编号">
          {{ currentOrder.houseNo }}
        </el-descriptions-item>
        <el-descriptions-item label="维修类型">
          <el-tag :type="getTypeTag(currentOrder.repairType)">
            {{ getTypeName(currentOrder.repairType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyTag(currentOrder.urgencyLevel)">
            {{ getUrgencyName(currentOrder.urgencyLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工单状态">
          <el-tag :type="getStatusTag(currentOrder.orderStatus)">
            {{ getStatusName(currentOrder.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">
          {{ formatDateTime(currentOrder.reportTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="故障图片" v-if="currentOrder.imageUrls">
          <div v-if="currentOrder.imageUrls && currentOrder.imageUrls.length > 0" style="display: flex; gap: 10px; flex-wrap: wrap;">
            <el-image
              v-for="(imageUrl, index) in currentOrder.imageUrls.filter(url => isValidImageUrl(url))"
              :key="index"
              :src="getImageUrl(imageUrl)"
              :preview-src-list="currentOrder.imageUrls.filter(url => isValidImageUrl(url)).map(url => getImageUrl(url))"
              :initial-preview-index="index"
              fit="cover"
              style="width: 100px; height: 100px; border-radius: 4px;"
              :preview-teleported="true"
              @error="handleImageError"
            />
          </div>
          <span v-else>无图片</span>
        </el-descriptions-item>
        <el-descriptions-item label="维修人员" v-if="currentOrder.workerName">
          {{ currentOrder.workerName }}{{ currentOrder.workerPhone ? '-' + currentOrder.workerPhone : '' }}
        </el-descriptions-item>
        <el-descriptions-item label="派工时间" v-if="currentOrder.assignTime">
          {{ formatDateTime(currentOrder.assignTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="维修金额" v-if="currentOrder.repairCost">
          ¥{{ currentOrder.repairCost }}
        </el-descriptions-item>
        <el-descriptions-item label="预计完成时间" v-if="currentOrder.requiredFinishTime">
          {{ formatDateTime(currentOrder.requiredFinishTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>故障描述</h4>
        <p style="line-height: 1.6; white-space: pre-wrap;">{{ currentOrder.faultDescription }}</p>
      </div>

  
      <div style="margin-top: 20px;" v-if="currentOrder.repairContent">
        <h4>维修记录</h4>
        <p>{{ currentOrder.repairContent }}</p>

        <!-- 维修后照片 -->
        <div style="margin-top: 15px;" v-if="currentOrder.repairImageUrls">
          <h5>维修后照片</h5>
          <div v-if="getRepairAfterImages(currentOrder.repairImageUrls).length > 0" style="display: flex; gap: 10px; flex-wrap: wrap;">
            <el-image
              v-for="(imageUrl, index) in getRepairAfterImages(currentOrder.repairImageUrls)"
              :key="index"
              :src="getImageUrl(imageUrl)"
              :preview-src-list="getRepairAfterImages(currentOrder.repairImageUrls).map(url => getImageUrl(url))"
              :initial-preview-index="index"
              fit="cover"
              style="width: 100px; height: 100px; border-radius: 4px;"
              :preview-teleported="true"
              @error="handleImageError"
            />
          </div>
          <span v-else>无维修后照片</span>
        </div>
      </div>

      <div style="margin-top: 20px;" v-if="currentOrder.acceptanceResult">
        <h4>验收结果</h4>
        <el-rate
          v-model="currentOrder.rating"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value} 分"
        />
        <p style="margin-top: 10px;">{{ currentOrder.acceptanceResult }}</p>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 派工对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="派工"
      width="600px"
    >
      <el-form :model="assignForm" label-width="120px" :rules="assignRules" ref="assignFormRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单编号">
              <el-input v-model="assignForm.orderNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修人">
              <el-input v-model="assignForm.reporter" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修人员" prop="repairerId">
              <el-select
                v-model="assignForm.repairerId"
                placeholder="请选择维修人员"
                style="width: 100%"
                filterable
              >
                <el-option
                  v-for="item in repairerOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计完成时间" prop="expectedCompleteTime">
              <el-date-picker
                v-model="assignForm.expectedCompleteTime"
                type="datetime"
                placeholder="请选择预计完成时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="维修金额" prop="repairCost">
          <el-input-number
            v-model="assignForm.repairCost"
            placeholder="请输入维修金额"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #666; font-size: 14px;">元</span>
        </el-form-item>
        </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="assignLoading"
          @click="handleAssignSubmit"
        >
          确定派工
        </el-button>
      </template>
    </el-dialog>

    <!-- 接单对话框 -->
    <el-dialog
      v-model="acceptDialogVisible"
      title="确认接单"
      width="400px"
    >
      <el-form :model="acceptForm" label-width="100px">
        <el-form-item label="工单编号">
          <el-input v-model="acceptForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="报修人">
          <el-input v-model="acceptForm.reporter" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="acceptForm.phone" disabled />
        </el-form-item>
        <el-form-item label="维修地址">
          <el-input v-model="acceptForm.address" disabled />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="acceptDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="acceptLoading"
          @click="handleAcceptSubmit"
        >
          确认接单
        </el-button>
      </template>
    </el-dialog>

    <!-- 维修对话框 -->
    <el-dialog
      v-model="repairDialogVisible"
      title="维修记录"
      width="600px"
    >
      <el-form :model="repairForm" :rules="repairRules" ref="repairFormRef" label-width="100px">
        <el-form-item label="工单编号">
          <el-input v-model="repairForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="故障原因" prop="faultReason">
          <el-input
            v-model="repairForm.faultReason"
            type="textarea"
            placeholder="请描述实际故障原因"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="更换配件">
          <el-input
            v-model="repairForm.partsUsed"
            placeholder="请输入更换的配件（可选）"
          />
        </el-form-item>
        <el-form-item label="现场照片">
          <el-upload
            v-model:file-list="repairForm.beforeImages"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="margin-top: 10px; color: #909399; font-size: 12px;">
            维修前照片（可选）
          </div>
        </el-form-item>
        <el-form-item label="维修后照片">
          <el-upload
            v-model:file-list="repairForm.afterImages"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="margin-top: 10px; color: #909399; font-size: 12px;">
            维修后照片（可选）
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="repairLoading"
          @click="handleRepairSubmit"
        >
          完成维修
        </el-button>
      </template>
    </el-dialog>

    <!-- 验收对话框 -->
    <el-dialog
      v-model="inspectDialogVisible"
      title="维修验收"
      width="500px"
    >
      <el-form :model="inspectForm" :rules="inspectRules" ref="inspectFormRef" label-width="100px">
        <el-form-item label="工单编号">
          <el-input v-model="inspectForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="维修内容">
          <p>{{ inspectForm.repairContent }}</p>
        </el-form-item>
        <el-form-item label="验收结果" prop="result">
          <el-radio-group v-model="inspectForm.result">
            <el-radio :label="1">合格</el-radio>
            <el-radio :label="0">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="inspectForm.rating" show-text />
        </el-form-item>
        <el-form-item label="验收意见" prop="comment">
          <el-input
            v-model="inspectForm.comment"
            type="textarea"
            placeholder="请输入验收意见"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="inspectDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="inspectLoading"
          @click="handleInspectSubmit"
        >
          确认验收
        </el-button>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog
      v-model="rateDialogVisible"
      title="服务评价"
      width="500px"
    >
      <el-form :model="rateForm" :rules="rateRules" ref="rateFormRef" label-width="100px">
        <el-form-item label="工单编号">
          <el-input v-model="rateForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="服务质量" prop="serviceRating">
          <el-rate v-model="rateForm.serviceRating" show-text />
        </el-form-item>
        <el-form-item label="响应速度" prop="responseRating">
          <el-rate v-model="rateForm.responseRating" show-text />
        </el-form-item>
        <el-form-item label="专业水平" prop="professionalRating">
          <el-rate v-model="rateForm.professionalRating" show-text />
        </el-form-item>
        <el-form-item label="总体评价" prop="overallRating">
          <el-rate v-model="rateForm.overallRating" show-text />
        </el-form-item>
        <el-form-item label="评价意见">
          <el-input
            v-model="rateForm.comment"
            type="textarea"
            placeholder="请输入您的评价意见（可选）"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="rateLoading"
          @click="handleRateSubmit"
        >
          提交评价
        </el-button>
      </template>
    </el-dialog>

    <!-- 维修统计对话框 -->
    <el-dialog
      v-model="statsDialogVisible"
      title="维修人员工作量统计"
      width="800px"
    >
      <el-table :data="Object.entries(repairerStats).map(([name, stats]) => ({ name, ...stats }))" style="width: 100%">
        <el-table-column prop="name" label="维修人员" width="150" />
        <el-table-column prop="totalOrders" label="总工单数" width="100" sortable />
        <el-table-column prop="completedOrders" label="完成工单数" width="120" sortable />
        <el-table-column prop="archiveCount" label="归档数量" width="100" sortable />
        <el-table-column prop="avgRating" label="平均评分" width="100" sortable>
          <template #default="{ row }">
            <el-rate v-model="row.avgRating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="完成率" width="100">
          <template #default="{ row }">
            {{ ((row.completedOrders / row.totalOrders) * 100).toFixed(1) }}%
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="statsDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleExportStats">
          <el-icon><Download /></el-icon>
          导出统计
        </el-button>
      </template>
    </el-dialog>

    <!-- 归档记录对话框 -->
    <el-dialog
      v-model="archiveDialogVisible"
      title="维修工单归档记录"
      width="1000px"
    >
      <div class="archive-info" style="margin-bottom: 20px; padding: 15px; background: #f0f9ff; border-radius: 4px;">
        <p><strong>归档总数：</strong>{{ archivedOrders.length }} 条记录</p>
        <p><strong>存储说明：</strong>归档数据已保存到数据库归档表中，可随时查询和导出</p>
      </div>

      <el-table :data="archivedOrders" style="width: 100%" max-height="400">
        <el-table-column prop="orderNo" label="工单编号" width="140" />
        <el-table-column prop="reporter" label="报修人" width="100" />
        <el-table-column prop="repairerName" label="维修人员" width="120" />
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="archiveTime" label="归档时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.archiveTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="archiveBy" label="归档人" width="100" />
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score v-if="row.rating > 0" />
            <span v-else>未评分</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link @click="handleViewArchiveDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="archiveDialogVisible = false">关闭</el-button>
        <el-button type="warning" @click="handleExportArchives">
          <el-icon><Download /></el-icon>
          导出归档
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Download, DataAnalysis, FolderOpened, ArrowUp, ArrowDown } from '@element-plus/icons-vue'

// 导入API接口
import {
  getRepairOrderPage,
  getRepairOrderDetail,
  createRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
  assignRepairOrder,
  acceptRepairOrder,
  completeRepairOrder as completeOrder,
  handleRepairOrder,
  inspectRepairOrder,
  rateRepairOrder,
  reassignRepairOrder,
  cancelRepairOrder,
  getRepairerList,
  getRepairTypeList,
  getOrderStats,
  getWorkerStats,
  exportRepairOrders,
  getMyRepairOrders,
  ownerCreateRepairOrder,
  ownerDeleteRepairOrder,
  uploadRepairImages,
  getRepairers,
  getMyWorkerOrders
} from '@/api/repair'

// 导入字典API
import { getDictDataByType } from '@/api/dict'

// 路由实例
const route = useRoute()

// 响应式数据
const formRef = ref()
const assignFormRef = ref()
const loading = ref(false)
const assignLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const isEdit = ref(false)

// 用户角色 - 从用户登录信息中获取
// 在实际项目中，这里应该从用户store或路由中获取
const currentUserRole = computed(() => {
  // 首先尝试从Token中解析用户类型
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 解析JWT Token的payload部分
      const tokenParts = token.split('.')
      if (tokenParts.length >= 2) {
        // 对于URL安全的base64编码，需要处理替换
        let payload = tokenParts[1]
        payload = payload.replace(/-/g, '+').replace(/_/g, '/')
        // 补全padding
        while (payload.length % 4) {
          payload += '='
        }
        const decoded = JSON.parse(atob(payload))
        const userType = decoded.userType
        if (userType) {
          console.log('从Token解析的用户类型:', userType)
          return userType
        }
      }
    } catch (error) {
      console.error('解析Token失败:', error)
      console.error('Token前100字符:', token.substring(0, 100))
    }
  }

  // 备用方案：从localStorage或vuex获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userRole = userInfo.role || 1 // 默认为物业经理

  // 根据用户角色名称转换为数字
  const roleMap = {
    'admin': 1,             // 系统管理员
    'manager': 2,           // 物业经理
    'owner': 3,             // 业主
    'repairer': 4,          // 维修人员
    '系统管理员': 1,
    '物业管理员': 2,
    '业主': 3,
    '维修人员': 4
  }

  return roleMap[userInfo.roleName] || roleMap[userInfo.role] || userRole
})

// 获取页面标题
const getPageTitle = () => {
  switch (currentUserRole.value) {
    case 1: return '维修管理'
    case 2: return '维修管理'
    case 3: return '我的报修'
    case 4: return '我的工单'
    default: return '维修管理'
  }
}

// 获取面包屑父级标题
const getBreadcrumbParent = () => {
  switch (currentUserRole.value) {
    case 1: return '服务管理'
    case 2: return '服务管理'
    case 3: return '业主门户'
    case 4: return '维修工作台'
    default: return '服务管理'
  }
}

// 归档数据存储 (模拟数据库表)
const archivedOrders = ref([])

// 维修人员工作量统计 (模拟数据库表)
const repairerStats = ref({})

// 对话框状态
const statsDialogVisible = ref(false)
const archiveDialogVisible = ref(false)
const acceptDialogVisible = ref(false)
const acceptLoading = ref(false)
const repairDialogVisible = ref(false)
const repairLoading = ref(false)
const inspectDialogVisible = ref(false)
const inspectLoading = ref(false)
const rateDialogVisible = ref(false)
const rateLoading = ref(false)

// 表单引用
const repairFormRef = ref()
const inspectFormRef = ref()
const rateFormRef = ref()

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  repairType: '',
  orderStatus: null,
  phone: ''
})


// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 选项数据
const repairTypeOptions = ref([])

// 加载维修类型字典数据
const loadRepairTypeOptions = async () => {
  try {
    const response = await getDictDataByType('repair_type')
    if (response.code === 200) {
      // 只显示状态为启用的字典项 (status = '1' 或者 true)
      repairTypeOptions.value = response.data
        .filter(item => item.status === 1 || item.status === '1' || item.status === true)
        .map(item => ({
          label: item.dictLabel,
          value: item.dictValue
        }))
      console.log('加载到的维修类型字典:', repairTypeOptions.value)
    }
  } catch (error) {
    console.error('加载维修类型字典失败:', error)
    // 备用数据
    repairTypeOptions.value = [
      { label: '水电维修', value: 'water_electric' },
      { label: '门窗维修', value: 'door_window' },
      { label: '家电维修', value: 'appliance' },
      { label: '管道维修', value: 'plumbing' },
      { label: '其他维修', value: 'other' }
    ]
  }
}

const urgencyLevelOptions = ref([
  { label: '一般', value: 1 },
  { label: '紧急', value: 2 },
  { label: '特急', value: 3 }
])

const orderStatusOptions = ref([
  { label: '待派工', value: 0 },
  { label: '已派工', value: 1 },
  { label: '进行中', value: 2 },
  { label: '待验收', value: 3 },
  { label: '已完成', value: 4 }
])

const repairerOptions = ref([])

// 加载维修人员列表
const loadRepairers = async () => {
  // 只有系统管理员和物业经理才需要加载维修人员列表
  if (currentUserRole.value === 3 || currentUserRole.value === 4) {
    return // 业主和维修人员不需要加载维修人员列表
  }

  try {
    const response = await getRepairers()
    if (response.code === 200) {
      repairerOptions.value = response.data.map(item => ({
        value: item.value,
        label: item.label
      }))
    }
  } catch (error) {
    console.error('加载维修人员列表失败:', error)
    ElMessage.error('加载维修人员列表失败')
  }
}

// 表单数据
const form = reactive({
  orderId: null,
  reporter: '',
  phone: '',
  houseCode: '',
  repairType: '',
  urgencyLevel: 1,
  description: '',
  images: []
})

// 派工表单
const assignForm = reactive({
  orderId: null,
  orderNo: '',
  reporter: '',
  repairerId: '',
  expectedCompleteTime: '',
  repairCost: null
})

// 派工表单验证规则
const assignRules = {
  repairerId: [
    { required: true, message: '请选择维修人员', trigger: 'change' }
  ],
  expectedCompleteTime: [
    { required: true, message: '请选择预计完成时间', trigger: 'change' }
  ],
  repairCost: [
    { required: true, message: '请输入维修金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '维修金额不能小于0', trigger: 'blur' }
  ]
}

// 接单表单
const acceptForm = reactive({
  orderId: null,
  orderNo: '',
  reporter: '',
  phone: '',
  address: ''
})

// 维修表单
const repairForm = reactive({
  orderId: null,
  orderNo: '',
  faultReason: '',
  partsUsed: '',
  beforeImages: [],
  afterImages: []
})

// 验收表单
const inspectForm = reactive({
  orderId: null,
  orderNo: '',
  repairContent: '',
  result: 1,
  rating: 5,
  comment: ''
})

// 评价表单
const rateForm = reactive({
  orderId: null,
  orderNo: '',
  serviceRating: 5,
  responseRating: 5,
  professionalRating: 5,
  overallRating: 5,
  comment: ''
})

// 当前工单
const currentOrder = ref({})

// 表单规则
const formRules = {
  reporter: [
    { required: true, message: '请输入报修人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  houseCode: [
    { required: true, message: '请输入房间编号', trigger: 'blur' }
  ],
  repairType: [
    { required: true, message: '请选择维修类型', trigger: 'change' }
  ],
  urgencyLevel: [
    { required: true, message: '请选择紧急程度', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入故障描述', trigger: 'blur' }
  ]
}

// 维修表单规则
const repairRules = {
  faultReason: [
    { required: true, message: '请描述实际故障原因', trigger: 'blur' }
  ]
}

// 验收表单规则
const inspectRules = {
  result: [
    { required: true, message: '请选择验收结果', trigger: 'change' }
  ],
  rating: [
    { required: true, message: '请进行评分', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入验收意见', trigger: 'blur' }
  ]
}

// 评价表单规则
const rateRules = {
  serviceRating: [
    { required: true, message: '请评价服务质量', trigger: 'change' }
  ],
  responseRating: [
    { required: true, message: '请评价响应速度', trigger: 'change' }
  ],
  professionalRating: [
    { required: true, message: '请评价专业水平', trigger: 'change' }
  ],
  overallRating: [
    { required: true, message: '请进行总体评价', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑工单' : '新增工单')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取维修类型名称
const getTypeName = (type) => {
  const option = repairTypeOptions.value.find(item => item.value === type)
  return option ? option.label : '其他维修'
}

// 获取维修类型标签
const getTypeTag = (type) => {
  const tagMap = {
    'water_electric': 'primary',
    'door_window': 'success',
    'appliance': 'warning',
    'plumbing': 'danger',
    'painting': 'info',
    'other': 'info'
  }
  return tagMap[type] || 'info'
}

// 获取紧急程度名称
const getUrgencyName = (level) => {
  const option = urgencyLevelOptions.value.find(item => item.value === level)
  return option ? option.label : '一般'
}

// 获取紧急程度标签
const getUrgencyTag = (level) => {
  const tagMap = {
    1: 'info',    // 一般
    2: 'warning', // 紧急
    3: 'danger'   // 特急
  }
  return tagMap[level] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '待派工',
    1: '待接单',  // 已派工，等待维修员接单
    2: '进行中',  // 维修员正在处理
    4: '待验收',  // 维修完成，等待业主评价
    5: '已完成'   // 评价完成，工单结束
  }
  return statusMap[status] || '未知状态'
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning', // 待派工 - 橙色
    1: 'primary', // 待接单 - 蓝色
    2: 'success', // 进行中 - 绿色
    4: 'info',    // 待验收 - 灰色（等待业主操作）
    5: 'success'  // 已完成 - 绿色
  }
  return tagMap[status] || 'warning'
}

// 生成模拟数据
const generateMockData = () => {
  const orders = []
  const reporters = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const types = ['water_electric', 'door_window', 'appliance', 'plumbing', 'painting', 'other']
  const statuses = [0, 1, 2, 3, 4] // 只包含5个标准状态
  const urgencyLevels = [1, 2, 3]

  for (let i = 0; i < 50; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const urgency = urgencyLevels[Math.floor(Math.random() * urgencyLevels.length)]
    const isRated = status === 4 && Math.random() > 0.3 // 已完成状态中有70%已评价
    const inspectResult = status === 4 ? (Math.random() > 0.2 ? 1 : 0) : 0 // 80%合格，20%不合格

    orders.push({
      orderId: i + 1,
      orderNo: `REP${(i + 1).toString().padStart(6, '0')}`,
      reporter: reporters[i % reporters.length],
      phone: '138****' + Math.floor(Math.random() * 10000).toString().padStart(4, '0'),
      houseCode: `H${Math.floor(Math.random() * 4 + 1)}${Math.floor(Math.random() * 18 + 1)}${Math.floor(Math.random() * 3 + 1)}`,
      repairType: type,
      urgencyLevel: urgency,
      orderStatus: status,
      description: '详细描述故障情况和需要维修的内容',
      images: Math.random() > 0.6 ? [`https://picsum.photos/200/200?random=${i}`] : [],
      repairerName: status > 0 ? repairerOptions.value[Math.floor(Math.random() * repairerOptions.value.length)].label : '',
      assignTime: status > 0 ? new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString() : '',
      repairContent: status >= 3 ? '维修已完成，问题解决' : '',
      acceptanceResult: status === 4 && isRated ? (inspectResult === 1 ? '维修质量很好，服务态度满意' : '维修不到位，需要重新处理') : '',
      rating: status === 4 && isRated ? (inspectResult === 1 ? 4 + Math.floor(Math.random() * 2) : 2 + Math.floor(Math.random() * 2)) : 0,
      comment: status === 4 && isRated ? (inspectResult === 1 ? '满意' : '需要改进') : '',
      inspectResult: inspectResult, // 验收结果：1=合格，0=不合格
      isRated: isRated, // 是否已评价
      reportTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
    })
  }

  return orders
}

// 加载维修工单数据
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      orderNo: searchForm.orderNo,
      repairType: searchForm.repairType,
      orderStatus: searchForm.orderStatus,
      phone: searchForm.phone
    }
    console.log('发送维修工单分页查询请求:', params)
    console.log('当前用户角色:', currentUserRole.value)
    console.log('当前Token:', localStorage.getItem('token'))
    // 根据用户角色选择不同的API
    let response
    if (currentUserRole.value === 3) {
      // 业主使用专用API - 查看自己的报修
      response = await getMyRepairOrders(params)
    } else if (currentUserRole.value === 4) {
      // 维修人员使用专用API - 查看分配给自己的工单
      response = await getMyWorkerOrders(params)
    } else {
      // 系统管理员和物业经理使用通用API - 查看所有工单
      response = await getRepairOrderPage(params)
    }
    console.log('收到维修工单响应:', response)
    if (response.code === 200) {
      // 处理不同的数据结构：管理员API返回rows，维修员API返回records
      const dataRows = response.data.rows || response.data.records || []
      tableData.value = dataRows.map(item => {
        // 处理图片URL，将字符串转换为数组
        if (item.imageUrls && typeof item.imageUrls === 'string') {
          item.imageUrls = item.imageUrls.split(',').filter(url => url.trim())
        }
        return item
      })
      pagination.total = response.data.total || 0
      console.log('设置维修工单数据成功，记录数:', tableData.value.length, '总数:', pagination.total)
    } else {
      ElMessage.error(response.msg || '加载维修工单失败')
    }
  } catch (error) {
    console.error('加载维修工单失败:', error)
    ElMessage.error('加载维修工单失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadOrders()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    repairType: '',
    orderStatus: null,
    phone: ''
  })
  handleSearch()
}


// 处理维修后照片数组
const getRepairAfterImages = (imageUrls) => {
  if (!imageUrls) return []

  // 如果是字符串，按逗号分割
  if (typeof imageUrls === 'string') {
    return imageUrls.split(',').filter(url => isValidImageUrl(url.trim()))
  }

  // 如果是数组，直接返回
  if (Array.isArray(imageUrls)) {
    return imageUrls.filter(url => isValidImageUrl(url))
  }

  return []
}

// 验证图片URL是否有效
const isValidImageUrl = (url) => {
  if (!url || typeof url !== 'string') return false

  // 检查是否是blob URL（这些通常不能跨会话访问）
  if (url.startsWith('blob:')) return false

  // 检查是否是服务器图片路径（以 /images/ 开头）
  if (url.startsWith('/images/')) {
    return true // 是服务器图片路径
  }

  // 检查是否是本地文件名（不带路径）
  if (url && !url.includes('/') && !url.includes('\\') && url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
    return true // 是本地文件名
  }

  // 检查是否是完整的HTTP/HTTPS URL
  try {
    const urlObj = new URL(url)
    return urlObj.protocol === 'http:' || urlObj.protocol === 'https:'
  } catch {
    return false
  }
}

// 转换图片URL为访问地址
const getImageUrl = (url) => {
  if (!url || typeof url !== 'string') return null

  // 如果已经是以 /images/ 开头的完整路径，需要拼接后端地址
  if (url.startsWith('/images/')) {
    return `http://localhost:8080${url}`
  }

  // 如果是本地文件名，转换为完整的静态资源访问路径
  if (!url.includes('/') && !url.includes('\\') && url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
    return `http://localhost:8080/images/${url}`
  }

  // 如果已经是完整的HTTP/HTTPS URL，直接返回
  try {
    const urlObj = new URL(url)
    if (urlObj.protocol === 'http:' || urlObj.protocol === 'https:') {
      return url
    }
  } catch {
    // URL解析失败，继续处理
  }

  return url
}

// 处理图片加载错误
const handleImageError = (e) => {
  console.warn('图片加载失败:', e.target.src)
  // 可以在这里设置一个默认图片或者隐藏图片
  e.target.style.display = 'none'
}

// 上传图片到服务器
const uploadImages = async (imageFiles) => {
  if (!imageFiles || imageFiles.length === 0) {
    return []
  }

  const formData = new FormData()
  imageFiles.forEach(file => {
    if (file.raw) {
      formData.append('files', file.raw)
    } else if (file instanceof File) {
      formData.append('files', file)
    }
  })

  try {
    const response = await uploadRepairImages(formData)
    if (response.code === 200) {
      return response.data || []
    } else {
      throw new Error(response.msg || '上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    throw error
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    orderId: null,
    reporter: '',
    phone: '',
    houseCode: '',
    repairType: '',
    urgencyLevel: 1,
    description: '',
    images: []
  })
  dialogVisible.value = true
}

// 查看详情
const handleViewDetail = (row) => {
  currentOrder.value = { ...row }
  detailDialogVisible.value = true
}

// 删除维修工单
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除工单"${row.orderNo}"吗？此操作不可恢复。`,
    '删除确认',
    { type: 'warning' }
  ).then(async () => {
    try {
      const response = await ownerDeleteRepairOrder(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadOrders()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 派工
const handleAssign = (row) => {
  Object.assign(assignForm, {
    orderId: row.id,
    orderNo: row.orderNo,
    reporter: row.realName || row.userName,
    repairerId: '',
    expectedCompleteTime: '',
    repairCost: null
  })
  assignDialogVisible.value = true
}

// 提交派工
const handleAssignSubmit = async () => {
  if (!assignFormRef.value) return

  try {
    await assignFormRef.value.validate()

    assignLoading.value = true
    const assignData = {
      repairerId: assignForm.repairerId,
      expectedCompleteTime: assignForm.expectedCompleteTime,
      repairCost: assignForm.repairCost
    }
    console.log('发送派工请求:', assignForm.orderId, assignData)
    const response = await assignRepairOrder(assignForm.orderId, assignData)
    console.log('收到派工响应:', response)
    if (response.code === 200) {
      ElMessage.success('派工成功')
      assignDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.msg || '派工失败')
    }
  } catch (error) {
    if (error.constructor.name !== 'Error') {
      // 表单验证错误，不需要提示
      return
    }
    console.error('派工失败:', error)
    ElMessage.error('派工失败')
  } finally {
    assignLoading.value = false
  }
}

// 处理工单
const handleProcess = (row) => {
  ElMessage.info(`处理工单 ${row.orderNo}`)
}

// 验收工单
const handleAccept = (row) => {
  ElMessage.info(`验收工单 ${row.orderNo}`)
}

// 导出
const handleExport = async () => {
  try {
    const params = {
      orderNo: searchForm.orderNo,
      reporter: searchForm.reporter,
      repairType: searchForm.repairType,
      orderStatus: searchForm.orderStatus
    }
    console.log('发送导出维修工单请求:', params)
    const response = await exportRepairOrders(params)
    console.log('收到导出响应:', response)

    // 创建下载链接
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `维修工单_${new Date().toLocaleDateString('zh-CN')}.xlsx`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 根据用户角色构建不同的提交数据
    let submitData
    if (currentUserRole.value === 3) {
      // 业主报修 - 使用简化字段，先上传图片
      let imageUrls = []
      if (form.images && form.images.length > 0) {
        try {
          imageUrls = await uploadImages(form.images)
        } catch (error) {
          console.error('图片上传失败:', error)
          ElMessage.error('图片上传失败，请重试')
          return
        }
      }

      submitData = {
        repairType: form.repairType,
        urgencyLevel: form.urgencyLevel,
        faultDescription: form.description,
        imageUrls: imageUrls.join(',') || ''
      }
    } else {
      // 管理员 - 使用完整字段
      submitData = {
        reporter: form.reporter,
        phone: form.phone,
        houseCode: form.houseCode,
        repairType: form.repairType,
        urgencyLevel: form.urgencyLevel,
        description: form.description,
        images: form.images || []
      }
    }

    let response
    if (isEdit.value) {
      submitData.id = form.orderId
      response = await updateRepairOrder(submitData)
    } else {
      // 根据用户角色选择不同的创建API
      response = currentUserRole.value === 3
        ? await ownerCreateRepairOrder(submitData)  // 业主专用API
        : await createRepairOrder(submitData)        // 管理员API
    }

    console.log('收到提交工单响应:', response)
    if (response.code === 200) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.msg || (dialogTitle.value + '失败'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交工单失败:', error)
      ElMessage.error(dialogTitle.value + '失败')
    }
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadOrders()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadOrders()
}


// 查看验收结果
const handleViewInspectResult = (row) => {
  Object.assign(currentOrder, row)
  ElMessageBox.alert(
    `验收结果：${row.inspectResult === 1 ? '合格' : '不合格'}\n评分：${row.rating || '未评分'}\n评价意见：${row.comment || '无'}`,
    '验收结果',
    {
      confirmButtonText: '确定'
    }
  )
}

// 归档维修记录
const handleArchive = (row) => {
  ElMessageBox.confirm(
    `确定要归档工单"${row.orderNo}"吗？\n归档后将从当前列表中移除并保存到归档库。`,
    '归档确认',
    { type: 'warning' }
  ).then(() => {
    // 1. 创建归档记录
    const archiveRecord = {
      ...row,
      archiveId: Date.now(),
      archiveTime: new Date().toISOString(),
      archiveBy: '物业经理', // 实际应该从用户信息获取
      archiveReason: '正常归档'
    }

    // 2. 添加到归档数据表
    archivedOrders.value.push(archiveRecord)

    // 3. 更新维修人员工作量统计
    if (row.repairerName) {
      if (!repairerStats.value[row.repairerName]) {
        repairerStats.value[row.repairerName] = {
          totalOrders: 0,
          completedOrders: 0,
          totalRating: 0,
          avgRating: 0,
          archiveCount: 0
        }
      }

      const stats = repairerStats.value[row.repairerName]
      stats.totalOrders += 1
      stats.completedOrders += 1
      stats.archiveCount += 1

      if (row.rating > 0) {
        stats.totalRating += row.rating
        stats.avgRating = (stats.totalRating / stats.completedOrders).toFixed(1)
      }
    }

    // 4. 从当前表格数据中移除
    const index = tableData.value.findIndex(item => item.orderId === row.orderId)
    if (index > -1) {
      tableData.value.splice(index, 1)
      pagination.total--
    }

    // 5. 更新分页
    if (tableData.value.length === 0 && pagination.current > 1) {
      pagination.current--
      loadOrders()
    }

    ElMessage.success(`工单"${row.orderNo}"归档成功`)
    console.log('归档记录:', archiveRecord)
    console.log('维修人员统计:', repairerStats.value)
  }).catch(() => {
    // 用户取消
  })
}

// 重新派工
const handleReassign = (row) => {
  ElMessageBox.confirm(
    `验收不合格，确定要重新派工吗？\n不合格原因：${row.comment || '无'}`,
    '重新派工',
    { type: 'warning' }
  ).then(() => {
    // 状态回退到"进行中"
    ElMessage.success('已重新派工，状态回退到进行中')
    loadOrders()
  }).catch(() => {
    // 用户取消
  })
}

// 查看维修统计
const handleViewStats = () => {
  if (Object.keys(repairerStats.value).length === 0) {
    ElMessage.warning('暂无统计数据，请先归档一些维修工单')
    return
  }
  statsDialogVisible.value = true
}

// 查看归档记录
const handleViewArchives = () => {
  if (archivedOrders.value.length === 0) {
    ElMessage.warning('暂无归档记录')
    return
  }
  archiveDialogVisible.value = true
}

// 查看归档详情
const handleViewArchiveDetail = (row) => {
  ElMessageBox.alert(
    `工单编号：${row.orderNo}\n报修人：${row.reporter}\n维修人员：${row.repairerName}\n归档时间：${formatDateTime(row.archiveTime)}\n归档人：${row.archiveBy}\n评分：${row.rating || '未评分'}\n归档原因：${row.archiveReason}`,
    '归档详情',
    {
      confirmButtonText: '确定'
    }
  )
}

// 物业经理最终处理（完成处理）
const handleFinalProcess = (row) => {
  const statusText = row.inspectResult === 1 ? '合格' : '不合格'

  ElMessageBox.confirm(
    `验收结果：${statusText}\n\n` +
    `如合格：\n` +
    `- 更新维修单状态为"已完成"\n` +
    `- 归档维修记录\n` +
    `- 维修人员工作量统计+1\n\n` +
    `如不合格：\n` +
    `- 查看不合格原因\n` +
    `- 重新派工或指派同一维修人员整改\n` +
    `- 维修单状态回退到"进行中"\n\n` +
    `确定要进行处理吗？`,
    '验收处理',
    { type: 'warning' }
  ).then(() => {
    if (row.inspectResult === 1) {
      // 合格：更新状态、归档、统计工作量
      ElMessage.success('验收合格，已更新状态为"已完成"并归档')

      // 模拟更新状态和归档
      row.orderStatus = 4 // 已完成
      handleArchive(row) // 执行归档

    } else {
      // 不合格：重新派工，状态回退到"进行中"
      ElMessageBox.confirm(
        `验收不合格，原因：${row.comment || '无'}\n\n选择处理方式：`,
        '重新派工',
        {
          distinguishCancelAndClose: true,
          confirmButtonText: '重新派工（其他人员）',
          cancelButtonText: '返工（同一人员）',
          type: 'warning'
        }
      ).then(() => {
        // 重新派工给其他人员
        ElMessage.success('已重新派工，状态回退到"进行中"')
        row.orderStatus = 2 // 进行中
      }).catch((action) => {
        if (action === 'cancel') {
          // 返工给同一人员
          ElMessage.success('已指派同一人员返工，状态回退到"进行中"')
          row.orderStatus = 2 // 进行中
        }
      })
    }
  }).catch(() => {
    // 用户取消
  })
}

// 导出统计数据
const handleExportStats = () => {
  const data = Object.entries(repairerStats.value).map(([name, stats]) => ({
    维修人员: name,
    总工单数: stats.totalOrders,
    完成工单数: stats.completedOrders,
    归档数量: stats.archiveCount,
    平均评分: stats.avgRating,
    完成率: `${((stats.completedOrders / stats.totalOrders) * 100).toFixed(1)}%`
  }))

  console.log('导出统计数据:', data)
  ElMessage.success('统计数据导出成功（查看控制台）')
}

// 导出归档数据
const handleExportArchives = () => {
  const data = archivedOrders.value.map(archive => ({
    工单编号: archive.orderNo,
    报修人: archive.reporter,
    维修人员: archive.repairerName,
    评分: archive.rating || '未评分',
    归档时间: formatDateTime(archive.archiveTime),
    归档人: archive.archiveBy
  }))

  console.log('导出归档数据:', data)
  ElMessage.success('归档数据导出成功（查看控制台）')
}

// 接单
const handleAcceptTask = (row) => {
  console.log('接单数据:', row)
  Object.assign(acceptForm, {
    orderId: row.id,           // 修复：后端返回的是 id
    orderNo: row.orderNo,
    reporter: row.realName,    // 修复：后端返回的是 realName
    phone: row.phone,
    address: row.houseNo       // 修复：后端返回的是 houseNo
  })
  console.log('接单表单数据:', acceptForm)
  acceptDialogVisible.value = true
}

// 提交接单
const handleAcceptSubmit = async () => {
  acceptLoading.value = true
  try {
    console.log('发送接单请求:', acceptForm.orderId)
    const response = await acceptRepairOrder(acceptForm.orderId)
    console.log('收到接单响应:', response)
    if (response.code === 200) {
      ElMessage.success('接单成功')
      acceptDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.msg || '接单失败')
    }
  } catch (error) {
    console.error('接单失败:', error)
    ElMessage.error('接单失败')
  } finally {
    acceptLoading.value = false
  }
}

// 维修
const handleRepair = (row) => {
  Object.assign(repairForm, {
    orderId: row.id,
    orderNo: row.orderNo,
    faultReason: '',
    partsUsed: '',
    beforeImages: [],
    afterImages: []
  })
  repairDialogVisible.value = true
}

// 提交维修
const handleRepairSubmit = async () => {
  if (!repairFormRef.value) return

  try {
    await repairFormRef.value.validate()
    repairLoading.value = true

    // 上传维修前照片
    let beforeImageUrls = []
    if (repairForm.beforeImages && repairForm.beforeImages.length > 0) {
      try {
        beforeImageUrls = await uploadImages(repairForm.beforeImages)
      } catch (error) {
        console.error('维修前照片上传失败:', error)
        ElMessage.error('维修前照片上传失败')
        repairLoading.value = false
        return
      }
    }

    // 上传维修后照片
    let afterImageUrls = []
    if (repairForm.afterImages && repairForm.afterImages.length > 0) {
      try {
        afterImageUrls = await uploadImages(repairForm.afterImages)
      } catch (error) {
        console.error('维修后照片上传失败:', error)
        ElMessage.error('维修后照片上传失败')
        repairLoading.value = false
        return
      }
    }

    // 构建提交数据
    const submitData = {
      orderId: repairForm.orderId,
      faultReason: repairForm.faultReason,
      partsUsed: repairForm.partsUsed,
      beforeImages: beforeImageUrls.join(','),
      afterImages: afterImageUrls.join(',')
    }

    console.log('提交维修数据:', submitData)

    // 调用维修完成API - 维修师傅使用handleRepairOrder
    const response = await handleRepairOrder(repairForm.orderId, submitData)
    console.log('维修完成响应:', response)

    if (response.code === 200) {
      ElMessage.success('维修记录提交成功')
      repairDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.msg || '维修记录提交失败')
    }
  } catch (error) {
    console.error('维修记录提交失败:', error)
    ElMessage.error('维修记录提交失败')
  } finally {
    repairLoading.value = false
  }
}

// 评价
const handleRate = (row) => {
  Object.assign(rateForm, {
    orderId: row.id,
    orderNo: row.orderNo,
    serviceRating: 5,
    responseRating: 5,
    professionalRating: 5,
    overallRating: 5,
    comment: ''
  })
  rateDialogVisible.value = true
}

// 提交评价
const handleRateSubmit = async () => {
  if (!rateFormRef.value) return

  try {
    await rateFormRef.value.validate()
    rateLoading.value = true

    const submitData = {
      serviceRating: rateForm.serviceRating,
      responseRating: rateForm.responseRating,
      professionalRating: rateForm.professionalRating,
      overallRating: rateForm.overallRating,
      comment: rateForm.comment
    }

    console.log('提交评价数据:', submitData)

    // 调用评价API
    const response = await rateRepairOrder(rateForm.orderId, submitData)
    console.log('评价响应:', response)

    if (response.code === 200) {
      ElMessage.success('评价提交成功')
      rateDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.msg || '评价提交失败')
    }
  } catch (error) {
    console.error('评价提交失败:', error)
    ElMessage.error('评价提交失败')
  } finally {
    rateLoading.value = false
  }
}

// 提交验收
const handleInspectSubmit = async () => {
  if (!inspectFormRef.value) return

  try {
    await inspectFormRef.value.validate()
    inspectLoading.value = true

    setTimeout(() => {
      ElMessage.success('验收完成')
      inspectDialogVisible.value = false
      loadOrders()
      inspectLoading.value = false
    }, 1000)
  } catch (error) {
    inspectLoading.value = false
  }
}

// 根据路由路径设置状态筛选
const setStatusFromRoute = () => {
  if (currentUserRole.value === 4) { // 只对维修人员生效
    const path = route.path
    console.log('当前路径:', path)

    // 精确匹配路径，避免 includes() 造成的冲突
    if (path === '/work/pending' || path.endsWith('/work/pending')) {
      searchForm.orderStatus = 1 // 待接单
      console.log('设置筛选: 待接单 (status=1)')
    } else if (path === '/work/processing' || path.endsWith('/work/processing')) {
      searchForm.orderStatus = 2 // 进行中
      console.log('设置筛选: 进行中 (status=2)')
    } else if (path === '/work/pending-accept' || path.endsWith('/work/pending-accept')) {
      searchForm.orderStatus = 3 // 待验收
      console.log('设置筛选: 待验收 (status=3)')
    } else if (path === '/work/completed' || path.endsWith('/work/completed')) {
      searchForm.orderStatus = 4 // 已完成
      console.log('设置筛选: 已完成 (status=4)')
    } else {
      searchForm.orderStatus = null // 其他情况显示全部
      console.log('设置筛选: 显示全部')
    }

    console.log('最终状态筛选:', searchForm.orderStatus)
  }
}

// 初始化
onMounted(() => {
  setStatusFromRoute() // 根据当前路径设置状态
  loadRepairTypeOptions()  // 先加载字典数据
  loadRepairers()          // 加载维修人员列表
  loadOrders()
})

// 监听路由变化
watch(
  () => route.path,
  () => {
    console.log('路由路径变化:', route.path)
    setStatusFromRoute() // 重新设置状态筛选
    pagination.current = 1 // 重置页码
    loadOrders() // 重新加载数据
  }
)
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    margin: 0 0 16px 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.search-section,
.action-section {
  margin-bottom: 20px;
}

.table-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }

  .disabled-button {
    color: #c0c4cc !important;
    cursor: not-allowed !important;

    &:hover {
      color: #c0c4cc !important;
      background-color: transparent !important;
    }
  }
}
</style>