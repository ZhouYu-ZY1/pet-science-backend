import request from '@/utils/request'

// 获取产品列表
export function getProductList(params: any) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取产品详情
export function getProductDetail(id: number) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

// 创建产品
export function createProduct(data: any) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

// 更新产品
export function updateProduct(data: any) {
  return request({
    url: `/product/${data.productId}`,
    method: 'put',
    data
  })
}

// 删除产品
export function deleteProduct(id: number) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

//清理图片
export function cleanupImages(urls: string[]) {
  return request({
    url: '/cleanup/image',
    method: 'post',
    data: { urls }
  })
}