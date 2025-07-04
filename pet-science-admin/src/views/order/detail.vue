<template>
    <div class="app-container">
        <div class="page-header">
            <el-button @click="$router.back()">
                <el-icon><ArrowLeft /></el-icon> &ensp;
                返回
            </el-button>
            <h2>订单详情</h2>
        </div>
        <el-row :gutter="20" v-loading="loading">
            <!-- 订单基本信息 -->
            <el-col :span="24">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>订单信息</span>
                            <div class="order-status">
                                <el-tag :type="getOrderStatusType(orderDetail.status)" size="large">
                                    {{ getOrderStatusText(orderDetail.status) }}
                                </el-tag>
                                <!-- 添加倒计时显示 -->
                                <span v-if="orderDetail.status === 'pending'" class="countdown-timer">
                                    支付剩余时间：{{ formatCountdown(countdown) }}
                                </span>
                            </div>
                        </div>
                    </template>
                    <el-descriptions :column="3" border>
                        <el-descriptions-item label="订单编号">{{ orderDetail.orderNo }}</el-descriptions-item>
                        <el-descriptions-item label="下单时间">{{ $formatDate(orderDetail.createdAt)
                            }}</el-descriptions-item>
                        <el-descriptions-item label="支付时间">{{ $formatDate(orderDetail.payment?.paymentTime) || '未支付'
                            }}</el-descriptions-item>
                        <el-descriptions-item label="商品信息">
                            <span>{{ orderItems.length }}种商品，共{{ getTotalQuantity() }}件</span>
                            <el-tag v-if="orderItems.length > 1" type="success" size="small" style="margin-left: 8px;">多商品</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="支付方式">{{ getPaymentMethodText(orderDetail.payment?.paymentMethod)
                || '未支付'
                            }}</el-descriptions-item>
                        <el-descriptions-item label="订单金额">¥{{ orderDetail.totalAmount }}</el-descriptions-item>
                        <el-descriptions-item label="支付流水号">{{ orderDetail.payment?.transactionNo || '无'
                            }}</el-descriptions-item>
                    </el-descriptions>

                    <div class="action-buttons" v-if="orderDetail.status">
                        <el-button v-if="orderDetail.status === 'paid'" type="primary" @click="handleShip">
                            发货
                        </el-button>
                        <el-button v-if="orderDetail.status === 'pending'" type="danger" @click="handleCancel">
                            取消订单
                        </el-button>
                    </div>
                </el-card>
            </el-col>

            <!-- 收货信息 -->
            <el-col :span="12">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>收货信息</span>
                        </div>
                    </template>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="收货人">{{ orderDetail.shipping?.receiverName
                            }}</el-descriptions-item>
                        <el-descriptions-item label="联系电话">{{ orderDetail.shipping?.receiverMobile
                            }}</el-descriptions-item>
                        <el-descriptions-item label="收货地址">{{ orderDetail.shipping?.address }}</el-descriptions-item>
                        <el-descriptions-item label="备注">{{ orderDetail.remark || '无' }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>
            </el-col>

            <!-- 物流信息 -->
            <el-col :span="12">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>物流信息</span>
                        </div>
                    </template>
                    <div v-if="orderDetail.shipping?.trackingNumber && orderDetail.shipping?.shippingCompany">
                        <el-descriptions :column="1" border>
                            <el-descriptions-item label="物流公司">{{
                getLogisticsCompanyName(orderDetail.shipping.shippingCompany)
            }}</el-descriptions-item>
                            <el-descriptions-item label="物流单号">{{ orderDetail.shipping.trackingNumber
                                }}</el-descriptions-item>
                            <el-descriptions-item label="发货时间">{{ $formatDate(orderDetail.shipping.shippingTime)
                                }}</el-descriptions-item>
                        </el-descriptions>
                    </div>
                    <div v-else class="empty-data">
                        <el-empty description="暂无物流信息" />
                    </div>
                </el-card>
            </el-col>

            <!-- 订单商品 -->
            <el-col :span="24">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>商品信息 ({{ orderItems.length }}件商品)</span>
                            <el-tag v-if="orderItems.length > 1" type="success" size="small">多商品订单</el-tag>
                        </div>
                    </template>
                    <div v-if="orderItems.length > 0">
                        <el-table :data="orderItems" border style="width: 100%">
                            <el-table-column type="index" width="50" />
                            <el-table-column label="商品图片" width="150" align="center">
                                <template #default="scope">
                                    <el-image style="width: 60px; height: 60px" :src="getFirstImage(scope.row.productImage)"
                                        fit="cover" :preview-src-list="getImageList(scope.row.productImage)" />
                                </template>
                            </el-table-column>
                            <el-table-column prop="productName" label="商品名称" min-width="200" align="center" />
                            <el-table-column prop="productId" label="商品编码" width="120" align="center"/>
                            <el-table-column prop="price" label="单价" width="100" align="center">
                                <template #default="scope">
                                    ¥{{ scope.row.price }}
                                </template>
                            </el-table-column>
                            <el-table-column prop="quantity" label="数量" width="80" align="center" />
                            <el-table-column prop="subtotal" label="小计" width="100" align="center">
                                <template #default="scope">
                                    ¥{{ scope.row.subtotal }}
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                    <div v-else class="empty-data">
                        <el-empty description="暂无商品信息" />
                    </div>

                    <div class="order-summary">
                        <div class="summary-item">
                            <span>商品种类：</span>
                            <span>{{ orderItems.length }}种</span>
                        </div>
                        <div class="summary-item">
                            <span>商品总数：</span>
                            <span>{{ getTotalQuantity() }}件</span>
                        </div>
                        <div class="summary-item">
                            <span>商品总价：</span>
                            <span>¥{{ orderDetail.totalAmount }}</span>
                        </div>
                        <div class="summary-item">
                            <span>运费：</span>
                            <span>¥0</span>
                        </div>
                        <div class="summary-item">
                            <span>优惠金额：</span>
                            <span>-¥0</span>
                        </div>
                        <div class="summary-item total">
                            <span>实付金额：</span>
                            <span>¥{{ orderDetail.totalAmount }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 订单日志 -->
            <el-col :span="24">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>订单日志</span>
                        </div>
                    </template>
                    <el-timeline>
                        <el-timeline-item v-for="(activity, index) in orderDetail.logs || []" :key="index"
                            :timestamp="activity.time" :type="getTimelineItemType(activity.action)">
                            {{ activity.content }}
                        </el-timeline-item>
                    </el-timeline>
                </el-card>
            </el-col>
        </el-row>

        <!-- 发货对话框 -->
        <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
            <el-form :model="shipForm" label-width="100px">
                <el-form-item label="订单编号">
                    <span>{{ shipForm.orderNo }}</span>
                </el-form-item>
                <el-form-item label="物流公司" prop="logisticsCompany">
                    <el-select v-model="shipForm.logisticsCompany" placeholder="请选择物流公司" style="width: 100%">
                        <el-option label="顺丰速运" value="SF" />
                        <el-option label="中通快递" value="ZTO" />
                        <el-option label="圆通速递" value="YTO" />
                        <el-option label="韵达快递" value="YD" />
                        <el-option label="申通快递" value="STO" />
                        <el-option label="京东物流" value="JD" />
                    </el-select>
                </el-form-item>
                <el-form-item label="物流单号" prop="trackingNumber">
                    <div style="display: flex; align-items: center;width: 100%;">
                        <div class="tracking-prefix" v-if="shipForm.logisticsCompany">
                            {{ shipForm.logisticsCompany }}
                        </div>
                        <el-input v-model="shipForm.trackingNumber" placeholder="请输入物流单号" />
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="shipDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmShip" :loading="shipLoading">确认发货</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, onActivated, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, updateOrderStatus, shipOrder } from '@/api/order'
import { ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
let orderId = route.params.id as string

const loading = ref(false)
const orderDetail = ref<any>({})
let orderItems = ref([])
const countdown = ref(0)
let countdownTimer: any = null

// 获取第一张图片
const getFirstImage = (imageStr: string) => {
  if (!imageStr) return '';
  const images = imageStr.split(';');
  return images[0];
}

// 获取图片列表
const getImageList = (imageStr: string) => {
  if (!imageStr) return [];
  return imageStr.split(';').filter(img => img);
}
// 获取订单详情
const fetchOrderDetail = async () => {
    loading.value = true
    try {
        const res = await getOrderDetail(Number(orderId))
        if (res.code === 200 && res.data) {
            orderDetail.value = res.data

            // 处理订单项数据，支持多商品和单商品订单
            orderItems.value = getOrderItemsFromResponse(res.data)

            // 如果是待支付状态，获取剩余支付时间
            if (orderDetail.value.status === 'pending') {
                fetchRemainingTime()
            }
        } else {
            ElMessage.error('获取订单详情失败')
        }
    } catch (error) {
        console.error('获取订单详情失败:', error)
        ElMessage.error('获取订单详情失败')
    } finally {
        loading.value = false
    }
}

// 从响应数据中获取订单项列表
const getOrderItemsFromResponse = (orderData: any) => {
    // 优先使用 orderItems 数组（多商品订单）
    if (orderData.orderItems && Array.isArray(orderData.orderItems) && orderData.orderItems.length > 0) {
        return orderData.orderItems
    }
    // 如果没有 orderItems，使用单个 orderItem（向后兼容）
    else if (orderData.orderItem) {
        return [orderData.orderItem]
    }
    // 如果都没有，返回空数组
    return []
}

// 计算商品总数量
const getTotalQuantity = () => {
    return orderItems.value.reduce((total: number, item: any) => {
        return total + (item.quantity || 0)
    }, 0)
}

// 获取剩余支付时间
const fetchRemainingTime = async () => {
    try {
        const res = await axios.get(`/api/order/expiration/${orderId}`)
        if (res.data.code === 200 && res.data.data !== null) {
            countdown.value = res.data.data
            startCountdown()
        }
    } catch (error) {
        console.error('获取订单剩余时间失败:', error)
    }
}

// 开始倒计时
const startCountdown = () => {
    // 清除之前的定时器
    if (countdownTimer) {
        clearInterval(countdownTimer)
    }
    
    countdownTimer = setInterval(() => {
        if (countdown.value > 0) {
            countdown.value--
        } else {
            // 倒计时结束，刷新订单状态
            clearInterval(countdownTimer)
            fetchOrderDetail()
        }
    }, 1000)
}

// 格式化倒计时显示
const formatCountdown = (seconds: number) => {
    if (seconds <= 0) return '00:00'
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 组件卸载时清除定时器
onUnmounted(() => {
    if (countdownTimer) {
        clearInterval(countdownTimer)
    }
})

// 发货对话框
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipForm = reactive({
    orderId: '',
    orderNo: '',
    logisticsCompany: '',
    trackingNumber: ''
})

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
    const statusMap: Record<string, string> = {
        pending: 'warning',
        paid: 'success',
        shipped: 'primary',
        completed: 'success',
        cancelled: 'danger'
    }
    return statusMap[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        pending: '待付款',
        paid: '已付款',
        shipped: '已发货',
        completed: '已完成',
        cancelled: '已取消'
    }
    return statusMap[status] || status
}

// 获取支付方式文本
const getPaymentMethodText = (method: string) => {
    const methodMap: Record<string, string> = {
        'zfb': '支付宝',
        'wx': '微信支付',
        'card': '银行卡'
    }
    return methodMap[method] || method
}

// 获取物流公司名称
const getLogisticsCompanyName = (code: string) => {
    const companyMap: Record<string, string> = {
        'SF': '顺丰速运',
        'ZTO': '中通快递',
        'YTO': '圆通速递',
        'YD': '韵达快递',
        'STO': '申通快递',
        'JD': '京东物流'
    }
    return companyMap[code] || code
}

// 获取时间线项目类型
const getTimelineItemType = (action: string) => {
    const actionMap: Record<string, string> = {
        create: 'primary',
        pay: 'success',
        ship: 'warning',
        complete: 'success',
        cancel: 'danger'
    }
    return actionMap[action] || 'info'
}

// 发货
const handleShip = () => {
    shipForm.orderId = orderDetail.value.orderId
    shipForm.orderNo = orderDetail.value.orderNo
    shipForm.logisticsCompany = ''
    shipForm.trackingNumber = ''
    shipDialogVisible.value = true
}

// 确认发货
const confirmShip = async () => {
    if (!shipForm.logisticsCompany) {
        ElMessage.warning('请选择物流公司')
        return
    }
    if (!shipForm.trackingNumber) {
        ElMessage.warning('请输入物流单号')
        return
    }

    shipLoading.value = true
    try {
        // 合并物流公司代码和物流单号
        const fullTrackingNumber = `${shipForm.logisticsCompany}${shipForm.trackingNumber}`
        
        // 修改为调用shipOrder接口
        await shipOrder({
            orderId: shipForm.orderId,
            shippingCompany: shipForm.logisticsCompany,
            trackingNumber: fullTrackingNumber
        })
        ElMessage.success('发货成功')
        shipDialogVisible.value = false
        fetchOrderDetail()
    } catch (error) {
        console.error('发货失败:', error)
    } finally {
        shipLoading.value = false
    }
}

// 取消订单
const handleCancel = () => {
    ElMessageBox.confirm(
        `确认要取消订单 ${orderDetail.value.orderNo} 吗?`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await updateOrderStatus(orderDetail.value.orderId, 'cancelled')
            ElMessage.success('订单已取消')
            fetchOrderDetail()
        } catch (error) {
            console.error('取消订单失败:', error)
        }
    }).catch(() => {
        ElMessage.info('已取消操作')
    })
}

// onMounted(() => {
//     fetchOrderDetail()
// })
// 添加onActivated钩子，确保重新获取数据
onActivated(() => {
    orderId = route.params.id as string
    fetchOrderDetail()
})
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.page-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.page-header h2 {
    margin: 0 0 0 10px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.box-card {
    margin-bottom: 20px;
}

.action-buttons {
    margin-top: 20px;
    text-align: right;
}

.empty-data {
    padding: 30px 0;
}

.order-summary {
    margin-top: 20px;
    text-align: right;
    padding-right: 50px;
}

.summary-item {
    margin-bottom: 10px;
}

.summary-item.total {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #ebeef5;
}

.tracking-prefix {
    background-color: #f5f7fa;
    padding: 0 10px;
    height: 30px;
    line-height: 30px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    margin-right: 5px;
    color: #606266;
    font-weight: bold;
}

/* 多商品订单样式 */
.order-status {
    display: flex;
    align-items: center;
    gap: 10px;
}

.countdown-timer {
    color: #f56c6c;
    font-weight: bold;
    font-size: 14px;
}

/* 商品表格样式优化 */
.el-table .el-table__row:hover > td {
    background-color: #f5f7fa !important;
}

/* 商品信息卡片头部样式 */
.card-header .el-tag {
    margin-left: 8px;
}
</style>