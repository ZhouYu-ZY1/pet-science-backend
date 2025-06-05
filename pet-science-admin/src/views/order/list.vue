<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="收货人">
          <el-input v-model="queryParams.consignee" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="订单状态" >
          <el-select v-model="queryParams.status" placeholder="请选择状态" style="width: 150px">
            <el-option label="待付款" value="pending" />
            <el-option label="已付款" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item width="50">
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="orderList"
        border
        style="width: 100%"
      >
        <el-table-column label="ID" prop="orderId" width="50" align="center" />
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column label="收货人" width="100">
          <template #default="scope">
            {{ scope.row.shipping?.receiverName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="120">
          <template #default="scope">
            {{ scope.row.shipping?.receiverMobile || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="支付时间" width="180">
          <template #default="scope">
            {{ $formatDate(scope.row.payment?.paymentTime) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="180">
          <template #default="scope">
            {{ $formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.status === 'paid'"
              size="small"
              type="success"
              @click="handleShip(scope.row)"
            >
              发货
            </el-button>
            <el-button
              v-if="scope.row.status === 'pending'"
              size="small"
              type="danger"
              @click="handleCancel(scope.row)"
            >
              取消
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
    
    <!-- 发货对话框 -->
    <el-dialog
      v-model="shipDialogVisible"
      title="订单发货"
      width="500px"
    >
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, updateOrderStatus, shipOrder } from '@/api/order'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  consignee: '',
  mobile: '',
  status: '',
  startTime: '',
  endTime: ''
})

const dateRange = ref<string[]>([])

// 监听日期范围变化
watch(dateRange, (val) => {
  if (val && val.length === 2) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
})

const loading = ref(false)
const total = ref(0)
const orderList = ref([])

// 发货对话框
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipForm = reactive({
  orderId: '',
  orderNo: '',
  logisticsCompany: '',
  trackingNumber: ''
})

// 获取订单列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getOrderList(queryParams)
    // 适配新的返回格式
    if (res.code === 200 && res.data) {
      // 如果返回的是分页数据
      if (res.data.list && res.data.total) {
        orderList.value = res.data.list
        total.value = res.data.total
      } else {
        orderList.value = []
        total.value = 0
      }
    } else {
      orderList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orderList.value = []
    total.value = 0
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
  queryParams.orderNo = ''
  queryParams.consignee = ''
  queryParams.mobile = ''
  queryParams.status = ''
  dateRange.value = []
  queryParams.startTime = ''
  queryParams.endTime = ''
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

// 查看订单详情
const handleDetail = (row: any) => {
  router.push(`/order/detail/${row.orderId}`)
}

// 发货
const handleShip = (row: any) => {
  shipForm.orderId = row.orderId
  shipForm.orderNo = row.orderNo
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
    
    await shipOrder({
      orderId: shipForm.orderId,
      shippingCompany: shipForm.logisticsCompany,
      trackingNumber: fullTrackingNumber
    })
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('发货失败:', error)
  } finally {
    shipLoading.value = false
  }
}

// 取消订单
const handleCancel = (row: any) => {
  ElMessageBox.confirm(
    `确认要取消订单 ${row.orderNo} 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateOrderStatus(row.orderId, 'cancelled')
      ElMessage.success('订单已取消')
      getList()
    } catch (error) {
      console.error('取消订单失败:', error)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

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
</style>