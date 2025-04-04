<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="userList"
        border
        style="width: 100%"
        :cell-style="{textAlign: 'center'}"
        :header-cell-style="{textAlign: 'center', backgroundColor: '#f5f7fa'}"
      >
        <el-table-column type="index" width="60" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" align="center" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="180" align="center" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.email || '暂无' }}
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" min-width="120" align="center">
          <template #default="scope">
            {{ scope.row.mobile || '暂无' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" min-width="180" align="center" >
          <template #default="scope">
            {{ $formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" align="center">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
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
    
    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="用户详情"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ userDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userDetail.mobile }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-avatar :size="60" :src="userDetail.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
        </el-descriptions-item>
        <el-descriptions-item label="个人简介">{{ userDetail.bio || '暂无简介' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ $formatDate(userDetail.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
            {{ userDetail.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, getUserDetail, updateUserStatus } from '@/api/user'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  email: '',
  mobile: ''
})

const loading = ref(false)
const total = ref(0)
const userList = ref([])
const dialogVisible = ref(false)
const userDetail = ref({
  userId: '',
  username: '',
  email: '',
  mobile: '',
  avatarUrl: '',
  bio: '',
  createdAt: '',
  status: 1
})

// 获取用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
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
  queryParams.username = ''
  queryParams.email = ''
  queryParams.mobile = ''
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

// 查看用户详情
const handleView = async (row: any) => {
  try {
    const res = await getUserDetail(row.userId)
    userDetail.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
  }
}

// 修改用户状态
const handleStatusChange = (row: any) => {
  const statusText = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1
  
  ElMessageBox.confirm(
    `确认要${statusText}用户 ${row.username} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateUserStatus(row.userId, newStatus)
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error(`${statusText}失败:`, error)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
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
</style>