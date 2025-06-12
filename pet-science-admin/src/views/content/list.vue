<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>内容列表</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="内容描述">
          <el-input v-model="queryParams.desc" placeholder="请输入内容描述" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="queryParams.uid" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" style="width: 200px">
            <el-option label="已审核" :value="1" />
            <el-option label="未审核" :value="0" />
            <el-option label="已下架" :value="-1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="contentList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" width="60" align="center" />
        <el-table-column prop="desc" label="内容描述" min-width="150" show-overflow-tooltip />
        <!-- 内容类型列 -->
        <el-table-column label="类型" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === 68 ? 'success' : 'primary'">
              {{ scope.row.type === 68 ? '图文' : '视频' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 封面列 -->
        <el-table-column label="封面" width="120" align="center">
          <template #default="scope">
            <el-image
              v-if="getCoverImage(scope.row)"
              :src="getCoverImage(scope.row)"
              style="width: 80px; max-height: 100px; object-fit: contain; cursor: pointer;"
              @click="handleCoverClick(scope.row)"
            />
            <el-icon v-else style="width: 80px; height: 80px; "><Picture /></el-icon>
          </template>
        </el-table-column>
        <!-- 预览列 -->
        <el-table-column label="预览" width="100" align="center">
          <template #default="scope">
            <el-button
              size="small"
              @click="previewContent(scope.row)"
              :disabled="!canPreview(scope.row)"
            >
              <el-icon v-if="scope.row.type === 68"><Picture /></el-icon>
              <el-icon v-else><VideoPlay /></el-icon>
              预览
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="作者" width="100" />

        <el-table-column prop="diggCount" label="点赞数" width="80" />
        <el-table-column prop="commentCount" label="评论数" width="80" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="success"
              @click="handleStatusChange(scope.row, 1)"
              :loading="scope.row.statusLoading"
            >
              通过审核
            </el-button>
            <el-button
              v-if="scope.row.status !== -1"
              size="small"
              type="danger"
              @click="handleStatusChange(scope.row, -1)"
              :loading="scope.row.statusLoading"
            >
              下架
            </el-button>
            <el-button
              v-if="scope.row.status === -1"
              size="small"
              type="primary"
              @click="handleStatusChange(scope.row, 1)"
              :loading="scope.row.statusLoading"
            >
              上架
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 视频预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="视频预览"
      width="60%"
      style="margin: 30px auto"
      destroy-on-close
    >
      <!-- 视频预览 -->
      <video
        v-if="currentContent"
        :src="currentContent.videoSrc"
        controls
        style="width: 100%;height: 500px"
      ></video>

      <!-- 视频信息 -->
      <div v-if="currentContent" style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 15px;">
        <p><strong>类型：</strong>视频</p>
        <p><strong>描述：</strong>{{ currentContent.desc }}</p>
        <p><strong>作者：</strong>{{ currentContent.nickname }}</p>
        <p><strong>点赞数：</strong>{{ currentContent.diggCount }}</p>
        <p><strong>评论数：</strong>{{ currentContent.commentCount }}</p>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="imagePreviewVisible"
      title="图片预览"
      width="80%"
      style="margin: 30px auto"
      destroy-on-close
      :show-close="false"
      class="image-preview-dialog"
    >
      <div class="image-preview-container" v-if="currentImages.length > 0">
        <!-- 图片显示区域 -->
        <div class="image-display">
          <div class="image-wrapper">
            <el-image
              :src="currentImages[currentImageIndex]"
              fit="contain"
              style="width: 100%; height: 500px; background-color: #000;"
              :preview-teleported="false"
              @load="onImageLoad"
              @error="onImageError"
              loading="lazy"
            >
              <template #placeholder>
                <div class="image-loading">
                  <div class="loading-spinner"></div>
                  <div class="loading-text">加载中...</div>
                </div>
              </template>
              <template #error>
                <div class="image-error">
                  <div class="error-text">图片加载失败</div>
                </div>
              </template>
            </el-image>
          </div>

          <!-- 页码指示器（左上角） -->
          <div class="image-indicator">
            {{ currentImageIndex + 1 }} / {{ currentImages.length }}
          </div>

          <!-- 关闭按钮（右上角） -->
          <el-button
            @click="closeImagePreview"
            circle
            size="large"
            class="control-btn close-btn-top"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>

        <!-- 控制按钮 -->
        <div class="image-controls">
          <el-button
            @click="prevImage"
            :disabled="currentImageIndex === 0"
            circle
            size="large"
            class="control-btn prev-btn"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>

          <el-button
            @click="nextImage"
            :disabled="currentImageIndex === currentImages.length - 1"
            circle
            size="large"
            class="control-btn next-btn"
          >
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <!-- 缩略图导航 -->
        <div class="thumbnail-nav" v-if="currentImages.length > 1">
          <div
            v-for="(image, index) in currentImages"
            :key="index"
            class="thumbnail-item"
            :class="{ active: index === currentImageIndex }"
            @click="currentImageIndex = index"
          >
            <el-image
              :src="image"
              fit="cover"
              style="width: 60px; height: 60px;"
            />
          </div>
        </div>

        <!-- 内容信息（底部） -->
        <div v-if="currentPreviewContent" class="content-info">
          <p><strong>类型：</strong>{{ currentPreviewContent.type === 68 ? '图文' : '视频' }}</p>
          <p><strong>描述：</strong>{{ currentPreviewContent.desc }}</p>
          <p><strong>作者：</strong>{{ currentPreviewContent.nickname }}</p>
          <p><strong>点赞数：</strong>{{ currentPreviewContent.diggCount }}</p>
          <p><strong>评论数：</strong>{{ currentPreviewContent.commentCount }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onActivated, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, VideoPlay, ArrowLeft, ArrowRight, Close } from '@element-plus/icons-vue'
import { getContentList, updateContentStatus } from '@/api/content'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  desc: '',
  uid: '',
  status: ''
})

const loading = ref(false)
const total = ref(0)
const contentList = ref([])

// 内容预览相关
const previewDialogVisible = ref(false)
const currentContent = ref(null)

// 图片预览相关
const imagePreviewVisible = ref(false)
const currentImages = ref([])
const currentImageIndex = ref(0)
const currentPreviewContent = ref(null)

// 获取内容列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getContentList(queryParams)
    contentList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取内容列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮
const resetQuery = () => {
  queryParams.desc = ''
  queryParams.uid = ''
  queryParams.status = ''
  handleQuery()
}

// 每页条数改变
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// 预览内容（视频或图文）
const previewContent = (row: any) => {
  if (row.type === 68) {
    // 图文类型：打开自定义图片预览对话框
    openImagePreview(row)
  } else {
    // 视频类型：打开视频预览对话框
    currentContent.value = row
    previewDialogVisible.value = true
  }
}

// 打开图片预览
const openImagePreview = (row: any) => {
  const images = getImageList(row.coverSrc)
  if (images.length > 0) {
    currentImages.value = images
    currentImageIndex.value = 0
    currentPreviewContent.value = row
    imagePreviewVisible.value = true
    // 添加键盘事件监听
    document.addEventListener('keydown', handleKeydown)
  }
}

// 键盘事件处理
const handleKeydown = (event: KeyboardEvent) => {
  if (!imagePreviewVisible.value) return

  switch (event.key) {
    case 'ArrowLeft':
      event.preventDefault()
      prevImage()
      break
    case 'ArrowRight':
      event.preventDefault()
      nextImage()
      break
    case 'Escape':
      event.preventDefault()
      closeImagePreview()
      break
  }
}

// 关闭图片预览
const closeImagePreview = () => {
  imagePreviewVisible.value = false
  currentPreviewContent.value = null
  document.removeEventListener('keydown', handleKeydown)
}

// 上一张图片
const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

// 下一张图片
const nextImage = () => {
  if (currentImageIndex.value < currentImages.value.length - 1) {
    currentImageIndex.value++
  }
}

// 图片加载完成
const onImageLoad = () => {
  // 图片加载完成后的处理
  console.log('图片加载完成')
}

// 图片加载错误
const onImageError = () => {
  // 图片加载错误后的处理
  console.log('图片加载失败')
}

// 触发图片预览（保留原有的封面点击功能）
const triggerImagePreview = (row: any) => {
  openImagePreview(row)
}

// 处理封面点击
const handleCoverClick = (row: any) => {
  if (row.type === 68) {
    // 图文类型：打开自定义图片预览
    openImagePreview(row)
  } else {
    // 视频类型：显示视频封面大图
    if (row.coverSrc) {
      currentImages.value = [row.coverSrc]
      currentImageIndex.value = 0
      currentPreviewContent.value = row
      imagePreviewVisible.value = true
      // 添加键盘事件监听
      document.addEventListener('keydown', handleKeydown)
    }
  }
}

// 获取封面图片
const getCoverImage = (row: any) => {
  if (row.type === 68) {
    // 图文类型：从coverSrc中获取第一张图片
    const images = getImageList(row.coverSrc)
    return images.length > 0 ? images[0] : null
  } else {
    // 视频类型：直接使用coverSrc
    return row.coverSrc
  }
}

// 获取预览图片列表
const getPreviewImages = (row: any) => {
  if (row.type === 68) {
    // 图文类型：返回所有图片
    return getImageList(row.coverSrc)
  } else {
    // 视频类型：返回封面图片
    return row.coverSrc ? [row.coverSrc] : []
  }
}

// 解析图片列表（用分号分割）
const getImageList = (imageStr: string) => {
  if (!imageStr) return []
  return imageStr.split(';').filter(img => img.trim())
}

// 判断是否可以预览
const canPreview = (row: any) => {
  if (row.type === 68) {
    // 图文类型：检查是否有图片
    return getImageList(row.coverSrc).length > 0
  } else {
    // 视频类型：检查是否有视频源
    return !!row.videoSrc
  }
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'success'
    case 0:
      return 'warning'
    case -1:
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '已审核'
    case 0:
      return '未审核'
    case -1:
      return '已下架'
    default:
      return '未知'
  }
}

// 修改内容状态
const handleStatusChange = async (row: any, newStatus: number) => {
  // 状态文本
  let statusText = ''
  if (newStatus === 1) {
    statusText = row.status === -1 ? '上架' : '审核通过'
  } else if (newStatus === -1) {
    statusText = '下架'
  }
  
  ElMessageBox.confirm(
    `确认要${statusText}内容 ${row.desc || row.videoId} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    // 设置当前行的状态加载中
    row.statusLoading = true
    try {
      await updateContentStatus({
        videoId: row.videoId,
        status: newStatus
      })
      ElMessage.success(`${statusText}成功`)
      // 更新状态
      row.status = newStatus
    } catch (error) {
      console.error(`${statusText}失败:`, error)
      ElMessage.error(`${statusText}失败`)
    } finally {
      row.statusLoading = false
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

onMounted(() => {
  getList()
})

// 添加onActivated钩子，确保从其他页面返回时重新获取数据
onActivated(() => {
  getList()
})

// 组件卸载时清理事件监听
onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 图片预览指示器样式 */
:deep(.el-image-viewer__wrapper) {
  z-index: 3000;
}

:deep(.el-image-viewer__actions) {
  background-color: rgba(0, 0, 0, 0.8);
  border-radius: 8px;
  padding: 8px 16px;
}

:deep(.el-image-viewer__actions .el-image-viewer__actions__inner) {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 图片预览对话框样式 */
.image-preview-dialog :deep(.el-dialog) {
  background-color: #000;
  border: 1px solid #333;
  border-radius: 8px;
}

.image-preview-dialog :deep(.el-dialog__header) {
  background-color: #000;
  border-bottom: 1px solid #333;
  padding: 15px 20px;
}

.image-preview-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}

.image-preview-dialog :deep(.el-dialog__body) {
  padding: 0;
  background-color: #000;
}

.image-preview-container {
  position: relative;
  background-color: #000;
  min-height: 500px;
}

.image-display {
  position: relative;
  background-color: #000;
}

.image-wrapper {
  background-color: #000;
  width: 100%;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 修复 Element Plus 图片组件的白屏问题 */
.image-wrapper :deep(.el-image) {
  background-color: #000 !important;
}

.image-wrapper :deep(.el-image__inner) {
  background-color: transparent !important;
}

.image-wrapper :deep(.el-image__placeholder) {
  background-color: #000 !important;
}

.image-wrapper :deep(.el-image__error) {
  background-color: #000 !important;
}

.image-indicator {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: rgba(20, 20, 20, 0.9);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
  font-weight: 500;
  z-index: 1000;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.image-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  pointer-events: none;
}

.control-btn {
  pointer-events: auto;
  background-color: rgba(40, 40, 40, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease;
}

.control-btn:hover {
  background-color: rgba(60, 60, 60, 1) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
  transform: scale(1.1);
}

.prev-btn {
  margin-right: auto;
}

.next-btn {
  margin-left: auto;
  margin-right: 20px;
}

.close-btn-top {
  position: absolute;
  top: 20px;
  right: 20px;
  background-color: rgba(40, 40, 40, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
  z-index: 1000;
}

.close-btn-top:hover {
  background-color: rgba(60, 60, 60, 1) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
  transform: scale(1.1);
}

.thumbnail-nav {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  background-color: rgba(20, 20, 20, 0.9);
  overflow-x: auto;
  border-top: 1px solid #333;
}

.thumbnail-item {
  cursor: pointer;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  transition: all 0.3s ease;
  flex-shrink: 0;
  overflow: hidden;
}

.thumbnail-item:hover {
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.2);
}

.thumbnail-item.active {
  border-color: #409eff;
  box-shadow: 0 0 12px rgba(64, 158, 255, 0.6);
}

/* 内容信息样式 */
.content-info {
  background-color: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-top: 1px solid #eee;
  margin-top: 0;
}

.content-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.content-info p:first-child {
  margin-top: 0;
}

.content-info p:last-child {
  margin-bottom: 0;
}

.content-info strong {
  color: #666;
  font-weight: 500;
  min-width: 60px;
  display: inline-block;
}

/* 图片加载状态样式 */
.image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 500px;
  background-color: #000;
  color: #fff;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 图片错误状态样式 */
.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 500px;
  background-color: #000;
  color: #fff;
}

.error-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}
</style>