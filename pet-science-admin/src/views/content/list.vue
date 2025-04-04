<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>内容列表</span>
          <el-button type="primary" @click="handleCreate">发布内容</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
            <el-option label="文章" value="article" />
            <el-option label="视频" value="video" />
            <el-option label="活动" value="event" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
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
        <el-table-column type="index" width="50" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getContentTypeTag(scope.row.type)">
              {{ getContentTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'published' ? 'success' : 'info'">
              {{ scope.row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              :type="scope.row.status === 'published' ? 'warning' : 'success'"
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 'published' ? '下架' : '发布' }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
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
    
    <!-- 内容编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="contentFormRef"
        :model="contentForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="contentForm.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-select v-model="contentForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="文章" value="article" />
            <el-option label="视频" value="video" />
            <el-option label="活动" value="event" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="封面图片">
          <el-upload
            class="content-uploader"
            action="/api/upload/image"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="contentForm.coverImage" :src="contentForm.coverImage" class="content-image" />
            <el-icon v-else class="content-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="el-upload__tip">
            建议上传尺寸为16:9的JPG/PNG图片，且不超过2MB
          </div>
        </el-form-item>
        
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="contentForm.summary" type="textarea" :rows="3" placeholder="请输入内容摘要" />
        </el-form-item>
        
        <el-form-item label="内容" prop="content" v-if="contentForm.type === 'article'">
          <el-input v-model="contentForm.content" type="textarea" :rows="10" placeholder="请输入文章内容" />
        </el-form-item>
        
        <el-form-item label="视频链接" prop="videoUrl" v-if="contentForm.type === 'video'">
          <el-input v-model="contentForm.videoUrl" placeholder="请输入视频链接" />
        </el-form-item>
        
        <el-form-item label="活动时间" prop="eventTime" v-if="contentForm.type === 'event'">
          <el-date-picker
            v-model="contentForm.eventTime"
            type="datetime"
            placeholder="选择活动时间"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="活动地点" prop="eventLocation" v-if="contentForm.type === 'event'">
          <el-input v-model="contentForm.eventLocation" placeholder="请输入活动地点" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="contentForm.status">
            <el-radio label="published">发布</el-radio>
            <el-radio label="draft">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getContentList, createContent, updateContent, deleteContent } from '@/api/content'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: '',
  status: ''
})

const loading = ref(false)
const total = ref(0)
const contentList = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('create')
const dialogTitle = computed(() => dialogType.value === 'create' ? '发布内容' : '编辑内容')
const contentFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const contentForm = reactive({
  contentId: '',
  title: '',
  type: 'article',
  coverImage: '',
  summary: '',
  content: '',
  videoUrl: '',
  eventTime: '',
  eventLocation: '',
  status: 'draft'
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2到100个字符之间', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  summary: [
    { required: true, message: '请输入摘要', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ],
  videoUrl: [
    { required: true, message: '请输入视频链接', trigger: 'blur' }
  ],
  eventTime: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ],
  eventLocation: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 上传相关
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

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
  queryParams.title = ''
  queryParams.type = ''
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

// 获取内容类型名称
const getContentTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    'article': '文章',
    'video': '视频',
    'event': '活动'
  }
  return typeMap[type] || type
}

// 获取内容类型标签样式
const getContentTypeTag = (type: string) => {
  const typeMap: Record<string, string> = {
    'article': '',
    'video': 'success',
    'event': 'warning'
  }
  return typeMap[type] || ''
}

// 上传前校验
const beforeUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是JPG或PNG格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过2MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response: any) => {
  contentForm.coverImage = response.data.url
}

// 重置表单
const resetForm = () => {
  contentForm.contentId = ''
  contentForm.title = ''
  contentForm.type = 'article'
  contentForm.coverImage = ''
  contentForm.summary = ''
  contentForm.content = ''
  contentForm.videoUrl = ''
  contentForm.eventTime = ''
  contentForm.eventLocation = ''
  contentForm.status = 'draft'
  
  if (contentFormRef.value) {
    contentFormRef.value.resetFields()
  }
}

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 创建内容
const handleCreate = () => {
  dialogType.value = 'create'
  resetForm()
  dialogVisible.value = true
}

// 编辑内容
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  resetForm()
  
  // 填充表单数据
  Object.assign(contentForm, row)
  
  dialogVisible.value = true
}

// 修改内容状态
const handleStatusChange = (row: any) => {
  const statusText = row.status === 'published' ? '下架' : '发布'
  const newStatus = row.status === 'published' ? 'draft' : 'published'
  
  ElMessageBox.confirm(
    `确认要${statusText}内容 "${row.title}" 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateContent({
        contentId: row.contentId,
        status: newStatus
      })
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error(`${statusText}失败:`, error)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 删除内容
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确认要删除内容 "${row.title}" 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteContent(row.contentId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 提交表单
const submitForm = async () => {
  if (!contentFormRef.value) return
  
  await contentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'create') {
          await createContent(contentForm)
          ElMessage.success('内容发布成功')
        } else {
          await updateContent(contentForm)
          ElMessage.success('内容更新成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  getList()
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

.content-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 300px;
  height: 169px;
}

.content-uploader:hover {
  border-color: #409EFF;
}

.content-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 300px;
  height: 169px;
  line-height: 169px;
  text-align: center;
}

.content-image {
  width: 300px;
  height: 169px;
  display: block;
  object-fit: cover;
}
</style>