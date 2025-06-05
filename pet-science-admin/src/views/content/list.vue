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
        <!-- 添加视频封面列 -->
        <el-table-column label="视频封面" width="120" align="center">
          <template #default="scope">
            <el-image 
              v-if="scope.row.coverSrc"
              :src="scope.row.coverSrc"
              style="width: 80px; max-height: 100px; object-fit: contain;"
              :preview-src-list="[scope.row.coverSrc]"
              :preview-teleported="true"
            />
            <el-icon v-else style="width: 80px; height: 80px; "><Picture /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="视频预览" width="100" align="center">
          <template #default="scope">
            <el-button size="small" @click="previewVideo(scope.row)" :disabled="!scope.row.videoSrc">
              <el-icon><VideoPlay /></el-icon>
              预览
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="作者" width="100" />
<!--        <el-table-column prop="type" label="类型" width="80" align="center">-->
<!--          <template #default="scope">-->
<!--            <el-tag :type="scope.row.type === 0 ? 'info' : 'success'">-->
<!--              {{ scope.row.type === 0 ? '抖音' : '用户' }}-->
<!--            </el-tag>-->
<!--          </template>-->
<!--        </el-table-column>-->
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
      v-model="videoDialogVisible"
      title="视频预览"
      width="50%"
      style="margin: 30px auto"
      destroy-on-close
    >
      <video
        v-if="currentVideo"
        :src="currentVideo.videoSrc"
        controls
        style="width: 100%;height: 500px"
      ></video>
      <div v-if="currentVideo" style="margin-top: 15px">
        <p><strong>描述：</strong>{{ currentVideo.desc }}</p>
        <p><strong>作者：</strong>{{ currentVideo.nickname }}</p>
        <p><strong>点赞数：</strong>{{ currentVideo.diggCount }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, VideoPlay } from '@element-plus/icons-vue'
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

// 视频预览相关
const videoDialogVisible = ref(false)
const currentVideo = ref(null)

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

// 预览视频
const previewVideo = (row: any) => {
  currentVideo.value = row
  videoDialogVisible.value = true
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
</style>