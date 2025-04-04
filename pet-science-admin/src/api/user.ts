import request from '@/utils/request'

// 获取用户列表
export function getUserList(params: any) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id: number) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 更新用户状态
export function updateUserStatus(id: number, status: number) {
  return request({
    url: `/user/status`,
    method: 'put',
    data: { userId: id, status }
  })
}