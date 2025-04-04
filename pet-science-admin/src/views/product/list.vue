<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产品列表</span>
          <el-button type="primary" @click="$router.push('/product/create')">新增产品</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="分类" min-width="200">
          <el-select v-model="queryParams.category" placeholder="请选择分类" clearable style="width: 200px">
            <el-option v-for="item in categoryList" :key="item.categoryId" :label="item.categoryName" :value="item.categoryCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="上架" value="1" />
            <el-option label="下架" value="0" />
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
        :data="productList"
        border
        style="width: 100%"
      >
        <el-table-column prop="productId" width="50" label="ID" />
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="scope">
            <el-tag>{{ getCategoryName(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" >
          <template #default="scope">
            {{ $formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
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
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 1 ? '下架' : '上架' }}
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
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, updateProduct, deleteProduct } from '@/api/product'
import { getAllCategories } from '@/api/category'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: '',
  productCode: '',
  category: '',
  status: ''
})

const loading = ref(false)
const total = ref(0)
const productList = ref([])
const categoryList = ref([])

// 获取产品列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getProductList(queryParams)
    productList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取产品列表失败:', error)
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
  queryParams.productName = ''
  queryParams.productCode = ''
  queryParams.category = ''
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

// 编辑产品
const handleEdit = (row: any) => {
  router.push(`/product/edit/${row.productId}`)
}

// 修改产品状态
const handleStatusChange = (row: any) => {
  const statusText = row.status === 1 ? '下架' : '上架'
  const newStatus = row.status === 1 ? '0' : '1'
  
  ElMessageBox.confirm(
    `确认要${statusText}产品 ${row.productName} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateProduct({
        productId: row.productId,
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

// 删除产品
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确认要删除产品 ${row.productName} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteProduct(row.productId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 获取分类名称
const getCategoryName = (categoryCode: string) => {
  const category = categoryList.value.find(item => item.categoryCode === categoryCode)
  return category ? category.categoryName : categoryCode
}

// 获取所有分类
const getCategoryList = async () => {
  try {
    const res = await getAllCategories()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

onMounted(() => {
  getCategoryList()
  getList()
})

// 添加onActivated钩子，确保从编辑页面返回时重新获取数据
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