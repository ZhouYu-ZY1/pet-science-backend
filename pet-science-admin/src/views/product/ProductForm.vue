<template>
    <div class="app-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>{{ isEdit ? '编辑产品' : '发布产品' }}</span>
                </div>
            </template>

            <el-form v-loading="loading" ref="productFormRef" :model="productForm" :rules="rules" label-width="100px">
                <el-form-item label="产品名称" prop="productName">
                    <el-input v-model="productForm.productName" placeholder="请输入产品名称" />
                </el-form-item>

                <el-form-item label="产品分类" prop="category">
                    <el-select v-model="productForm.category" placeholder="请选择产品分类" style="width: 100%">
                        <el-option v-for="item in categoryList" :key="item.categoryId" :label="item.categoryName"
                            :value="item.categoryCode" />
                    </el-select>
                </el-form-item>

                <el-form-item label="产品价格" prop="price">
                    <el-input-number v-model="productForm.price" :precision="2" :min="0" :step="0.1"
                        style="width: 100%" />
                </el-form-item>

                <el-form-item label="产品库存" prop="stock">
                    <el-input-number v-model="productForm.stock" :min="0" :step="1" style="width: 100%" />
                </el-form-item>

                <el-form-item label="产品描述" prop="description">
                    <el-input v-model="productForm.description" type="textarea" :rows="4" placeholder="请输入产品描述" />
                </el-form-item>

                <el-form-item label="产品图片">
                    <el-upload class="product-uploader" action="/api/upload/image" :headers="uploadHeaders"
                        :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
                        <el-icon class="product-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>

                    <!-- 已上传图片预览区域 - 改为可拖拽排序 -->
                    <div v-if="productForm.mainImage" class="image-preview-container">
                        <draggable 
                            v-model="imageArray" 
                            item-key="url"
                            @end="onDragEnd"
                            :animation="200"
                            class="draggable-container">
                            <template #item="{element, index}">
                                <div class="image-preview-item">
                                    <img :src="element" class="preview-image" />
                                    <span class="delete-icon" @click.stop="removeImage(index)">×</span>
                                </div>
                            </template>
                        </draggable>
                    </div>
                </el-form-item>

                <el-form-item label="产品状态" prop="status">
                    <el-radio-group v-model="productForm.status">
                        <el-radio label="1">上架</el-radio>
                        <el-radio label="0">下架</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ isEdit ? '保存修改' : '发布产品'
                        }}</el-button>
                    <el-button @click="$router.back()">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed, onActivated, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createProduct, updateProduct, getProductDetail, cleanupImages } from '@/api/product'
import { getAllCategories } from '@/api/category'
import draggable from 'vuedraggable'

const route = useRoute()
const router = useRouter()
let productId = computed(() => route.params.id as string)
const isEdit = computed(() => !!productId.value)

const productFormRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const categoryList = ref<any[]>([])
const imageList = ref<string[]>([]) // 临时图片列表

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

// 获取产品详情（仅编辑模式）
const fetchProductDetail = async () => {
    if (!isEdit.value) return

    loading.value = true
    try {
        const res = await getProductDetail(Number(productId.value))
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
    const isLt5M = file.size / 1024 / 1024 < 5

    if (!isJPG && !isPNG) {
        ElMessage.error('上传图片只能是JPG或PNG格式!')
        return false
    }
    if (!isLt5M) {
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

// 图片数组，用于拖拽排序
const imageArray = ref<string[]>([])

// 监听mainImage变化，更新imageArray
watch(() => productForm.mainImage, (newValue) => {
    if (newValue) {
        imageArray.value = newValue.split(';')
    } else {
        imageArray.value = []
    }
}, { immediate: true })

// 监听imageArray变化，更新mainImage
watch(imageArray, (newValue) => {
    productForm.mainImage = newValue.join(';')
}, { deep: true })

// 拖拽结束后的处理
const onDragEnd = () => {
    // 更新产品表单中的mainImage字段
    productForm.mainImage = imageArray.value.join(';')
    ElMessage.success('图片顺序已更新')
}

// 获取所有图片URL列表
const getImageList = () => {
    return imageArray.value
}

// 删除图片
const removeImage = (index: number) => {
    // 从数组中移除指定索引的图片
    imageArray.value.splice(index, 1)
    // 如果临时图片列表中有这个图片，也需要移除
    if (imageList.value.length > index) {
        imageList.value.splice(index, 1)
    }
    ElMessage.success('图片已移除')
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

// 提交表单
const submitForm = async () => {
    if (!productFormRef.value) return

    await productFormRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                if (isEdit.value) {
                    await updateProduct(productForm)
                    ElMessage.success('产品更新成功')
                    // 使用replace而不是push，确保返回列表页面时会重新加载数据
                    router.replace('/product/list')
                } else {
                    await createProduct(productForm)
                    ElMessage.success('产品发布成功')
                    router.push('/product/list')
                }
                imageList.value = [] // 清空临时图片列表
            } catch (error) {
                console.error(isEdit.value ? '产品更新失败:' : '产品发布失败:', error)
                ElMessage.error(isEdit.value ? '产品更新失败' : '产品发布失败')
            } finally {
                submitLoading.value = false
            }
        }
    })
}

// 在组件挂载时获取分类数据和产品详情（如果是编辑模式）
onMounted(() => {
    window.addEventListener('beforeunload', cleanupTempImages)
    // fetchCategories()
    // fetchProductDetail()
})

// 添加onActivated钩子，确保重新获取数据
onActivated(() => {
    // 检查当前路由是否为新增产品页面
    const isCreateRoute = !route.params.id;
    
    // 重新计算productId和isEdit
    productId = computed(() => route.params.id as string);
    
    // 如果是新增页面，重置表单数据
    if (isCreateRoute) {
        // 重置表单数据为初始状态
        Object.assign(productForm, {
            productId: '',
            productName: '',
            productCode: '',
            category: '',
            price: 0,
            stock: 0,
            description: '',
            mainImage: '',
            status: '1'
        });
        // 清空临时图片列表
        imageList.value = [];
    }
    
    fetchCategories();
    fetchProductDetail();
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
    display: flex;
    justify-content: center;
    align-items: center;
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
    cursor: move;
    transition: transform 0.2s;
}

.image-preview-item:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.preview-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.delete-icon {
    position: absolute;
    top: 2px;
    right: 2px;
    width: 20px;
    height: 20px;
    background-color: rgba(0, 0, 0, 0.5);
    color: #fff;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    font-size: 16px;
    z-index: 10;
}

.delete-icon:hover {
    background-color: rgba(0, 0, 0, 0.8);
}

.draggable-container {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.image-preview-container {
    display: flex;
    margin-left: 10px;
}

.image-preview-item {
    width: 100px;
    height: 100px;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
    position: relative;
    cursor: move;
    transition: transform 0.2s;
}

.image-preview-item:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>