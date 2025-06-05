import request from '@/utils/request'

// 获取内容列表
export function getContentList(params: any) {
  return request({
    url: '/content/getList',
    method: 'get',
    params
  })
}

// 更新内容状态
export function updateContentStatus(data: any) {
  return request({
    url: '/content/updateStatus',
    method: 'post',
    data
  })
}