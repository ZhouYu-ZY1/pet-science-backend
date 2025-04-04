<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>编辑产品</span>
        </div>
      </template>
      
      <el-form
        v-loading="loading"
        ref="productFormRef"
        :model="productForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入产品名称" />
        </el-form-item>
        
        
        <el-form-item label="产品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择产品分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.categoryId" :label="item.categoryName" :value="item.categoryCode" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="产品价格" prop="price">
          <el-input-number v-model="productForm.price" :precision="2" :min="0" :step="0.1" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="产品库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="4" placeholder="请输入产品描述" />
        </el-form-item>
        
        <el-form-item label="产品图片">
          <el-upload
            class="product-uploader"
            action="/api/upload/image"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="productForm.mainImage" :src="productForm.mainImage" class="product-image" />
            <el-icon v-else class="product-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="el-upload__tip">
            建议上传尺寸为800x800像素的JPG/PNG图片，且不超过2MB
          </div>
        </el-form-item>
        
        <el-form-item label="产品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio label="1">上架</el-radio>
            <el-radio label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">保存修改</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted,onActivated } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getProductDetail, updateProduct } from '@/api/product'
import { getAllCategories } from '@/api/category'

const route = useRoute()
const router = useRouter()
let productId = route.params.id as string

const productFormRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const categoryList = ref<any[]>([])

// 表单数据
const productForm = reactive({
  productId: '',
  productName: '',
  productCode: '',
  category: '',
  price: 0,
  stock: 0,
  description: '',
  mainImage: '',
  status: '1'
})

// 表单验证规则
const rules = {
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2到100个字符之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择产品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入产品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入产品库存', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入产品描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择产品状态', trigger: 'change' }
  ]
}

// 上传相关
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 获取所有分类
const fetchCategories = async () => {
  try {
    const res = await getAllCategories()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 获取产品详情
const fetchProductDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(Number(productId))
    const productData = res.data
    // 确保status是字符串类型
    if (productData.status !== undefined) {
      productData.status = String(productData.status)
    }
    Object.assign(productForm, productData)
  } catch (error) {
    console.error('获取产品详情失败:', error)
    ElMessage.error('获取产品详情失败')
  } finally {
    loading.value = false
  }
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
  productForm.mainImage = response.data.url
}

// 提交表单
const submitForm = async () => {
  if (!productFormRef.value) return
  
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await updateProduct(productForm)
        ElMessage.success('产品更新成功')
        // 使用replace而不是push，确保返回列表页面时会重新加载数据
        router.replace('/product/list')
      } catch (error) {
        console.error('产品更新失败:', error)
        ElMessage.error('产品更新失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchCategories()
  fetchProductDetail()
})

// 添加onActivated钩子，确保重新获取数据
onActivated(() => {
  productId = route.params.id as string
  fetchCategories()
  fetchProductDetail()
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

.product-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 200px;
}

.product-uploader:hover {
  border-color: #409EFF;
}

.product-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  line-height: 200px;
  text-align: center;
}

.product-image {
  width: 200px;
  height: 200px;
  display: block;
  object-fit: cover;
}
</style>