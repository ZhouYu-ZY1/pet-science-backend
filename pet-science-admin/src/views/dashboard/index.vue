<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-card-content">
            <div class="data-card-value">{{ statistics.userCount }}</div>
            <div class="data-card-title">用户总数</div>
          </div>
          <div class="data-card-icon user-icon">
            <el-icon><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-card-content">
            <div class="data-card-value">{{ statistics.productCount }}</div>
            <div class="data-card-title">产品总数</div>
          </div>
          <div class="data-card-icon product-icon">
            <el-icon><Goods /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-card-content">
            <div class="data-card-value">{{ statistics.orderCount }}</div>
            <div class="data-card-title">订单总数</div>
          </div>
          <div class="data-card-icon order-icon">
            <el-icon><List /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-card-content">
            <div class="data-card-value">{{ statistics.contentCount }}</div>
            <div class="data-card-title">内容总数</div>
          </div>
          <div class="data-card-icon content-icon">
            <el-icon><Picture /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近7天订单统计</span>
            </div>
          </template>
          <div class="chart-container" ref="orderChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
            </div>
          </template>
          <div class="chart-container" ref="userChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
              <el-button class="button" text @click="$router.push('/order/list')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="latestOrders" style="width: 100%" stripe>
            <el-table-column prop="orderNo" label="订单编号" width="180" />
            <el-table-column prop="consignee" label="收货人" width="120" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新用户</span>
              <el-button class="button" text @click="$router.push('/user/list')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="latestUsers" style="width: 100%" stripe>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="email" label="邮箱">
              <template #default="scope">
                {{ scope.row.email || '暂无' }}
              </template>
            </el-table-column>
            <el-table-column prop="mobile" label="手机号" width="120">
              <template #default="scope">
                {{ scope.row.mobile || '暂无' }}
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180">
              <template #default="scope">
                {{ $formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { User, Goods, List, Picture } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getUserList } from '@/api/user'  // 导入用户API

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  BarChart,
  LineChart,
  CanvasRenderer
])

// 模拟数据
const statistics = reactive({
  userCount: 0,  // 初始化为0，将通过API获取
  productCount: 86,
  orderCount: 324,
  contentCount: 156
})

const latestOrders = ref([
  {
    orderNo: 'ORD20250315001',
    consignee: '张三',
    totalAmount: 299.00,
    status: 'pending',
    createdAt: '2025-03-15 10:23:45'
  },
  {
    orderNo: 'ORD20250315002',
    consignee: '李四',
    totalAmount: 158.50,
    status: 'paid',
    createdAt: '2025-03-15 11:45:12'
  },
  {
    orderNo: 'ORD20250314001',
    consignee: '王五',
    totalAmount: 499.00,
    status: 'shipped',
    createdAt: '2025-03-14 16:32:08'
  },
  {
    orderNo: 'ORD20250314002',
    consignee: '赵六',
    totalAmount: 89.90,
    status: 'completed',
    createdAt: '2025-03-14 09:15:30'
  }
])

const latestUsers = ref([])  // 初始化为空数组，将通过API获取

const orderChartRef = ref<HTMLElement>()
const userChartRef = ref<HTMLElement>()

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

// 获取用户数据
const fetchUserData = async () => {
  try {
    const res = await getUserList({
      pageNum: 1,
      pageSize: 4  // 只获取4条最新用户数据
    })
    
    if (res.data && res.data.list) {
      latestUsers.value = res.data.list
      statistics.userCount = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户数据失败:', error)
  }
}

onMounted(() => {
  // 获取用户数据
  fetchUserData()
  
  // 初始化订单图表
  if (orderChartRef.value) {
    const orderChart = echarts.init(orderChartRef.value)
    orderChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['订单数', '销售额']
      },
      xAxis: {
        type: 'category',
        data: ['3/9', '3/10', '3/11', '3/12', '3/13', '3/14', '3/15']
      },
      yAxis: [
        {
          type: 'value',
          name: '订单数',
          position: 'left'
        },
        {
          type: 'value',
          name: '销售额',
          position: 'right',
          axisLabel: {
            formatter: '{value} 元'
          }
        }
      ],
      series: [
        {
          name: '订单数',
          type: 'bar',
          data: [10, 15, 12, 18, 20, 25, 22]
        },
        {
          name: '销售额',
          type: 'line',
          yAxisIndex: 1,
          data: [1500, 2300, 1800, 2600, 3000, 3500, 3200]
        }
      ]
    })
    
    // 窗口大小变化时重新调整图表大小
    window.addEventListener('resize', () => {
      orderChart.resize()
    })
  }

  // 初始化用户增长趋势图表
  if (userChartRef.value) {
    const userChart = echarts.init(userChartRef.value)
    userChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['新增用户', '累计用户']
      },
      xAxis: {
        type: 'category',
        data: ['3/9', '3/10', '3/11', '3/12', '3/13', '3/14', '3/15']
      },
      yAxis: {
        type: 'value',
        name: '用户数'
      },
      series: [
        {
          name: '新增用户',
          type: 'bar',
          data: [12, 15, 10, 18, 20, 22, 25]
        },
        {
          name: '累计用户',
          type: 'line',
          data: [120, 135, 145, 163, 183, 205, 230]
        }
      ]
    })

    // 窗口大小变化时重新调整图表大小
    window.addEventListener('resize', () => {
      userChart.resize()
    })
  }
})


onUnmounted(() => {
  window.removeEventListener('resize', () => {
    if (orderChartRef.value) {
      const orderChart = echarts.init(orderChartRef.value)
      orderChart.resize()
    }
    if (userChartRef.value) {
      const userChart = echarts.init(userChartRef.value)
      userChart.resize()
    }
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-card {
  position: relative;
  overflow: hidden;
}

.data-card-content {
  position: relative;
  z-index: 1;
}

.data-card-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.data-card-title {
  font-size: 14px;
  color: #666;
}

.data-card-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 48px;
  opacity: 0.1;
}

.chart-row {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-icon {
  color: #409EFF;
}

.product-icon {
  color: #67C23A;
}

.order-icon {
  color: #E6A23C;
}

.content-icon {
  color: #F56C6C;
}
</style>