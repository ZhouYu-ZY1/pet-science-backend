import request from '@/utils/request'

// 获取分类列表
export function getCategoryList(params: any) {
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}

// 获取分类详情
export function getCategoryDetail(id: string) {
  return request({
    url: `/category/${id}`,
    method: 'get'
  })
}

// 新增分类
export function addCategory(data: any) {
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(data: any) {
  return request({
    url: `/category/${data.categoryId}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id: string) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}

// 获取所有分类（不分页）
export function getAllCategories() {
  return request({
    url: '/category/all',
    method: 'get'
  })
}