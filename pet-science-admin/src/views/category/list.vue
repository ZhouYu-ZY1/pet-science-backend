<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类列表</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="categoryList"
        border
        style="width: 100%"
      >
        <el-table-column prop="categoryId" label="ID" width="80" />
        <el-table-column prop="categoryCode" label="分类编码" min-width="120" />
        <el-table-column prop="categoryName" label="分类名称" min-width="150" />
        <el-table-column label="操作" width="180">
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增分类' : '编辑分类'"
      width="500px"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryName: ''
})

const loading = ref(false)
const total = ref(0)
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const categoryFormRef = ref<FormInstance>()

// 表单数据
const categoryForm = reactive({
  categoryId: '',
  categoryCode: '',
  categoryName: ''
})

// 表单验证规则
const categoryRules = reactive<FormRules>({
  categoryCode: [
    { required: true, message: '请输入分类编码', trigger: 'blur' },
  ],
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
  ]
})

// 获取分类列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCategoryList(queryParams)
    categoryList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取分类列表失败:', error)
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
  queryParams.categoryName = ''
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

// 新增分类
const handleAdd = () => {
  dialogType.value = 'add'
  categoryForm.categoryId = ''
  categoryForm.categoryCode = ''
  categoryForm.categoryName = ''
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  categoryForm.categoryId = row.categoryId
  categoryForm.categoryCode = row.categoryCode
  categoryForm.categoryName = row.categoryName
  dialogVisible.value = true
}

// 删除分类
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确认要删除分类 ${row.categoryName} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCategory(row.categoryId)
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
  if (!categoryFormRef.value) return
  
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await addCategory(categoryForm)
          ElMessage.success('添加成功')
        } else {
          await updateCategory(categoryForm)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('操作失败:', error)
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
</style>