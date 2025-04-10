<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发布产品</span>
        </div>
      </template>
      
      <el-form
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
            <el-option 
              v-for="item in categoryList" 
              :key="item.categoryId" 
              :label="item.categoryName" 
              :value="item.categoryCode" 
            />
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
            :before-upload="beforeUpload">
            <el-icon class="product-uploader-icon"><Plus /></el-icon>
          </el-upload>
          
          <!-- 已上传图片预览区域 -->
          <div v-if="productForm.mainImage" class="image-preview-container">
            <div v-for="(url, index) in getImageList()" :key="index" class="image-preview-item">
              <img :src="url" class="preview-image" />
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="产品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio label="1">上架</el-radio>
            <el-radio label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">发布产品</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted ,onBeforeUnmount} from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createProduct,cleanupImages } from '@/api/product'
import { getAllCategories } from '@/api/category'

const router = useRouter()
const productFormRef = ref<FormInstance>()
const loading = ref(false)
const categoryList = ref<any[]>([])

// 获取所有分类
const fetchCategories = async () => {
  try {
    const res = await getAllCategories()
    if (res.code === 200) {
      categoryList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取分类失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}


// 表单数据
const productForm = reactive({
  productName: '',
  productCode: '',
  category: '',
  price: 0,
  stock: 0,
  description: '',
  mainImage: '',
  status: '1'
})

const imageList = ref<string[]>([])

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

// 上传前校验
const beforeUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 5

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是JPG或PNG格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过5MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response: any) => {
  ElMessage.success('上传成功')
  setTimeout(() => {
     // 如果已经有图片URL，则添加新的URL并用分号分隔
      if (productForm.mainImage) {
        productForm.mainImage += ';' + response.data.url
      } else {
        productForm.mainImage = response.data.url
      }
      imageList.value.push(response.data.url)
  }, 1000)
}

// 提交表单
const submitForm = async () => {
  if (!productFormRef.value) return
  
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createProduct(productForm)
        imageList.value = [] // 清空临时图片列表
        ElMessage.success('产品发布成功')
        router.push('/product/list')
      } catch (error) {
        console.error('产品发布失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 获取所有图片URL列表
const getImageList = () => {
  if (!productForm.mainImage) return []
  return productForm.mainImage.split(';')
}

// 添加页面卸载前的清理函数
const cleanupTempImages = async () => {
  console.log('页面卸载前清理临时图片')
  if (imageList.value.length > 0) {
    try {
      // 调用后端清理接口
      await cleanupImages(imageList.value)
    } catch (error) {
      console.error('清理临时图片失败:', error)
    }
  }
}

// 在组件挂载时获取分类数据
onMounted(() => {
  window.addEventListener('beforeunload', cleanupTempImages)
  fetchCategories()
})
// 添加页面卸载钩子
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', cleanupTempImages)
  cleanupTempImages()
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
  width: 100px;
  height: 100px;
}

.product-uploader:hover {
  border-color: #409EFF;
}

.product-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}


.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  margin-left: 10px;
  gap: 10px;
}

.image-preview-item {
  width: 100px;
  height: 100px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
