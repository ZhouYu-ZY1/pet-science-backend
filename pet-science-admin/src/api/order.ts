import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params: any) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id: number) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id: number, status: string) {
  return request({
    url: `/order/status`,
    method: 'put',
    data: { orderId: id, status }
  })
}